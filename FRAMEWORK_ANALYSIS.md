# Framework End-to-End Analysis — Capabilities & Features

> A deep-dive into the **Java + Selenium + TestNG** UI automation framework (`org.gcit`), documenting **every capability, how it is implemented, and how it works at runtime** — written as a blueprint reference for building an equivalent **Playwright** framework.

---

## Table of Contents

1. [Tech Stack & Project Structure](#1-tech-stack--project-structure)
2. [End-to-End Execution Flow](#2-end-to-end-execution-flow)
3. [Configuration Management](#3-configuration-management)
4. [Driver Management (Browser Factory + ThreadLocal)](#4-driver-management)
5. [Runner Manager (MethodInterceptor — which tests run, priority, repeat count)](#5-runner-manager--methodinterceptor)
6. [Data-Driven Testing (DataProvider invocation)](#6-data-driven-testing--how-the-dataprovider-is-invoked)
7. [Test Data Management (DB / Excel / JSON sources)](#7-test-data-management)
8. [Page Object Model & Action Layer](#8-page-object-model--action-layer)
9. [Wait Strategy (ExplicitWaitFactory)](#9-wait-strategy--explicitwaitfactory)
10. [Reporting (Extent Reports + DB result audit)](#10-reporting)
11. [Screenshots](#11-screenshots)
12. [Logging (Log4j2 LogService)](#12-logging)
13. [Listeners (Suite/Test lifecycle, Retry, Annotation Transformer)](#13-listeners)
14. [Custom Annotations & Enums](#14-custom-annotations--enums)
15. [Custom Exceptions](#15-custom-exceptions)
16. [Parallel Execution & Selenium Grid (Docker)](#16-parallel-execution--selenium-grid-docker)
17. [Playwright Migration Blueprint (feature-by-feature mapping)](#17-playwright-migration-blueprint)

---

## 1. Tech Stack & Project Structure

| Concern | Library / Tool | Version |
|---|---|---|
| Browser automation | Selenium WebDriver | 4.20.0 |
| Test runner | TestNG | 7.8.0 |
| Build tool | Maven (Surefire profile `demo_tests`) | — |
| Reporting | ExtentReports (Spark reporter) | 5.1.1 |
| Excel test data | Apache POI | 4.1.2 |
| JSON (de)serialization | Jackson Databind | 2.9.6 |
| DB test data | MySQL Connector / SQLite JDBC | 8.0.33 / 3.7.2 |
| Logging | Log4j2 | 2.17.2 |
| Assertions | AssertJ | 3.24.2 |
| Random data | JavaFaker | 0.12 |
| Remote grid | Selenium Grid via docker-compose | 4.20.0 |

### Folder layout

```
src/main/java/org/gcit/
├── annotations/      FrameworkAnnotation (author, category metadata)
├── constants/        FrameworkConstants (all paths, timeouts, report names)
├── driver/           Driver (init/quit), DriverManager (ThreadLocal storage)
├── enums/            ConfigProperties, WaitStrategy, LogType, CategoryType, DataBaseProperties
├── exceptions/       Custom unchecked exceptions (BaseException + 6 children)
├── factories/        DriverSupplier (browser factory), ExplicitWaitFactory (waits)
├── listeners/        ListenerClass, MethodInterceptor, AnnotationTransformer, RetryFailedTests
├── logger/           LogService (Log4j2 wrapper)
├── pages/            BaseAction (common actions) + page objects (HRMLoginPage, HRMHomePage)
├── reports/          ExtentReport, ExtentManager (ThreadLocal), ExtentLogger
└── utils/            PropertyUtils, JsonUtils, ExcelUtils, DataProviderUtils,
                      DataBaseConnectionUtils, JsonConfigUtils, ScreenshotUtils,
                      ReportDatabaseController

src/main/resources/
├── Configuration/    DataBaseConfig.json (DB creds), SqlQueries.json (named SQL queries)
└── runnerlist/       RUNMANAGER.json (generated/static runner list)

src/test/java/org/gcit/tests/
├── BaseSetup/        BaseTest (@BeforeMethod/@AfterMethod driver lifecycle)
└── orangehrmtest/    OrangeHRMTest (sample data-driven test)

src/test/resources/
├── config/           config.properties (single source of runtime switches)
├── testrunners/      testng.xml (suite + listeners + parallel config)
├── testdata/         excel/testdata.xlsx, json/QA_TestData.json, database/inputData.db
└── log4j2.xml        File appender, TRACE root level
```

---

## 2. End-to-End Execution Flow

This is the complete runtime sequence when you run `mvn test -P demo_tests`:

```
mvn test -P demo_tests
   │
   ▼
Surefire picks up src/test/resources/testrunners/testng.xml
   (suite: parallel="tests", thread-count="3", 3 listeners registered)
   │
   ▼
① AnnotationTransformer.transform()            [IAnnotationTransformer]
   • Injects dataProvider="getTestData" (DataProviderUtils) into EVERY @Test
   • Injects retryAnalyzer=RetryFailedTests into EVERY @Test
   │
   ▼
② MethodInterceptor.intercept()                [IMethodInterceptor]
   • Reads `datasource` from config.properties (db | excel | json)
   • db    → generates RUNMANAGER.json from DB query, then reads it
   • excel → generates RUNMANAGER.json from RUNMANAGER sheet, then reads it
   • json  → reads RUNMANAGER.json directly
   • Keeps only tests where execute == "yes"
   • Sets test description + priority from runner list
   • Stores invocation `count` per test in a static map
   │
   ▼
③ ListenerClass.onStart(ISuite)                [ISuiteListener]
   • ExtentReport.initReports(suiteName) → creates Spark reporters
   │
   ▼
④ For each selected test method (parallel threads):
   BaseTest @BeforeMethod  → Driver.initDriver(browser from config)
                              → DriverSupplier.getWebDriver() builds local/remote driver
                              → DriverManager.setDriver() stores in ThreadLocal
                              → navigates to configured URL
   ListenerClass.onTestStart → ExtentReport.createTest() (ThreadLocal ExtentTest)
   DataProviderUtils.getDataProvider(method)
                              → pulls rows for THIS method from db/excel/json
                              → trims rows to the runner-list invocation count
   @Test method runs          → page objects → BaseAction → ExplicitWaitFactory
                              → each action logs to Extent (with screenshot) + Log4j2
   ListenerClass.onTestSuccess/Failure/Skipped
                              → Extent pass/fail/skip log (+ screenshot)
                              → ReportDatabaseController.storeReportInDatabase()
                                (writes Pass/Fail/Skip row to MySQL audit table)
   RetryFailedTests.retry()   → re-runs failures if retry=yes (up to retryattempts)
   BaseTest @AfterMethod      → Driver.quitDriver() → ThreadLocal cleanup
   │
   ▼
⑤ ListenerClass.onFinish(ISuite)
   • ExtentReport.flushReports() → writes HTML, opens it in default browser
```

---

## 3. Configuration Management

### How it is implemented

**File:** `src/test/resources/config/config.properties` — the single switchboard for the framework:

```properties
url=https://opensource-demo.orangehrmlive.com/...
overridereports=yes        # yes → fixed report name; no → timestamped report folder
browser=chrome             # chrome | firefox | edge
datasource=db              # db | excel | json   ← drives BOTH runner list AND test data
env=qa                     # environment key for test data (QA_TestData.json / DB rows)
explicitwaittimeout=30     # seconds, used by ExplicitWaitFactory
browserheadlessmode=yes
passedstepscreenshot=yes   # per-status screenshot toggles
failedstepscreenshot=yes
skippedstepscreenshot=yes
retry=no                   # retry failed tests
retryattempts=1
runmode=local              # local | remote (Selenium Grid)  ← also selects MySQL vs SQLite!
seleniumgridaddress=http://localhost:4444/
```

**Loader:** `PropertyUtils` (`utils/PropertyUtils.java`)
- A **static initializer block** loads the properties file **once** at class-load time into a `HashMap<String,String>` (`CONFIG_MAP`), trimming values.
- Access is **type-safe via enum**: `PropertyUtils.getValue(ConfigProperties.BROWSER)` — the enum name is lowercased and used as the map key, so typos in calling code are compile errors, not runtime surprises.
- Missing keys throw a custom `PropertiesFileException`.

**Secondary config:** `JsonConfigUtils` does the same pattern for `DataBaseConfig.json` (DB host/port/schema/username/password), keyed by the `DataBaseProperties` enum.

**Path management:** `FrameworkConstants` centralizes **every** file path (config, excel, JSON test data, SQLite DB, runner-list JSON, SQL-queries JSON, report output dirs), built from `user.dir`. It also:
- Computes the report path based on `overridereports` (timestamped folder vs fixed `index.html`).
- Holds the resolved `ENVIRONMENT` and the explicit-wait timeout.
- Resolves the env-specific test data file name: `<ENV>_TestData.json` (e.g. `QA_TestData.json`).

### Key design takeaways
- Enum-keyed config access (compile-time safety).
- Load-once static caching.
- Everything path-related in one constants class.

---

## 4. Driver Management

Three classes split the responsibility cleanly:

### `DriverManager` — thread-safe storage
```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
getDriver() / setDriver(dr) / unloadDriver()
```
- Each TestNG worker thread gets **its own WebDriver** via `ThreadLocal`, which is what makes `parallel="tests" thread-count="3"` safe.
- `unloadDriver()` calls `remove()` to prevent ThreadLocal memory leaks.

### `DriverSupplier` — the browser factory
- `getWebDriver(String browser)` is a **switch over browser name** (`chrome` / `edge` / `firefox`).
- For each browser it builds the matching `Options` object:
  - headless flag added when `browserheadlessmode=yes`
  - `--start-maximized`, `--disable-dev-shm-usage`, `--disable-gpu`, popup-blocking excluded
  - Firefox additionally gets a `FirefoxProfile` accepting untrusted certs
- **Local vs Remote decided by `runmode`**:
  - `remote` → `new RemoteWebDriver(new URL(seleniumgridaddress), options)` (Selenium Grid / Docker)
  - `local` → `new ChromeDriver(options)` etc. (Selenium Manager resolves the binary)
- Guards against double-initialization (throws if a driver already exists for the thread).
- Window is force-sized to 1920×1080 after creation.

### `Driver` — lifecycle facade used by tests
- `initDriver(browser)`:
  1. `DriverManager.setDriver(DriverSupplier.getWebDriver(browser))`
  2. Navigates to `url` from config (`driver.get(url)`)
  3. On navigation failure → logs, quits, rethrows.
- `quitDriver()`: null-safe `quit()` + **always** `unloadDriver()` in `finally`.

### Where it's invoked
`BaseTest` (test layer) wires it to TestNG lifecycle:
```java
@BeforeMethod(alwaysRun = true) setUp()  → Driver.initDriver(getValue(BROWSER));
@AfterMethod(alwaysRun = true) tearDown() → Driver.quitDriver();
```
→ **A fresh browser per test method** (not per class/suite), giving full test isolation.

---

## 5. Runner Manager — MethodInterceptor

This is the framework's signature feature: **which tests run, in what priority, with what description, and how many times is controlled by external data — not by code/annotations.**

### The runner-list data shape
Each runner record (DB row / Excel row / JSON entry) has:

| Field | Meaning |
|---|---|
| `testcasename` | Must match the `@Test` method name (case-insensitive) |
| `execute` | `yes` / `no` — include or skip the test |
| `testdescription` | Injected as the TestNG test description |
| `priority` | Injected as the TestNG priority (execution order) |
| `count` | How many data iterations of this test to run |

### How it's invoked & how it works
`MethodInterceptor implements IMethodInterceptor` is registered in `testng.xml`. TestNG calls `intercept(methods, context)` **once, before any test runs**, allowing the list of test methods to be rewritten:

```java
String dataSource = PropertyUtils.getValue(ConfigProperties.DATASOURCE);
switch (dataSource) {
    case "db":    JsonUtils.generateRunnerListJsonData();          // SQL → RUNMANAGER.json
                  list = JsonUtils.getTestDetails("RunManager");   break;
    case "excel": JsonUtils.generateRunnerListJsonDataFromExcel("RUNMANAGER"); // sheet → RUNMANAGER.json
                  list = JsonUtils.getTestDetails("RunManager");   break;
    case "json":  list = JsonUtils.getTestDetails("RunManager");   break;  // read as-is
}
```

**Key trick:** the DB and Excel paths don't read directly into memory — they first **normalize everything into one canonical JSON file** (`src/main/resources/runnerlist/RUNMANAGER.json`), then read that JSON. JSON is the canonical intermediate format for all three sources.

Then, for every discovered `@Test` method:
```java
if (methodName == testData.testcasename && testData.execute == "yes") {
    method.setDescription(testData.testdescription);
    method.setPriority(testData.priority);
    invocationCounts.put(methodName, testData.count);  // static map, read later by DataProvider
    results.add(methodInstance);                       // only matched methods survive
}
```
- Non-matching / `execute=no` methods are simply **dropped from the run**.
- `invocationCounts` is a `static Map<String,Integer>` shared with `DataProviderUtils.getInvocation(name)` — this is how the runner list **caps how many data rows each test consumes**.

The RUNMANAGER.json canonical structure:
```json
{
  "RunManager": {
    "testCaseLists": [
      { "testcasename": "loginLogoutTest", "execute": "yes",
        "testdescription": "Login/Logout flow", "priority": 1, "count": 2 }
    ]
  }
}
```

---

## 6. Data-Driven Testing — how the DataProvider is invoked

### Step 1 — `AnnotationTransformer` wires it invisibly
`AnnotationTransformer implements IAnnotationTransformer` (registered in `testng.xml`) runs at annotation-processing time and **rewrites every `@Test` annotation**:

```java
annotation.setDataProvider("getTestData");
annotation.setDataProviderClass(DataProviderUtils.class);
annotation.setRetryAnalyzer(RetryFailedTests.class);
```

→ Test authors **never write** `@Test(dataProvider=...)`. They just declare a `Map` parameter:

```java
@Test
public void loginLogoutTest(Map<String, String> data) {
    new HRMLoginPage()
        .enterUsername(data.get("username"))
        .enterPassword(data.get("password"))
        ...
}
```

### Step 2 — `DataProviderUtils.getDataProvider(Method)` routes by source
```java
@DataProvider(name = "getTestData")
public static Object[][] getDataProvider(Method method) {
    switch (datasource) {           // same config key as the runner manager
        case "db":    return getJsonData(method);        // DB → JSON file → filter
        case "excel": return getData(method);            // POI read DATA sheet → filter
        case "json":  return getJsonDataFromJson(method);// read <ENV>_TestData.json → filter
    }
}
```

All three paths apply the same filtering contract:
1. Filter rows where `testcasename == method.getName()` (the data rows are tagged with the test they belong to).
2. Filter rows where `execute == "yes"`.
3. **Trim the row count to the runner-list `count`**: `numEntries = min(filteredRows, MethodInterceptor.getInvocation(testName))`.
4. Return `Object[][]` where each row is a single `Map<String,Object>` — so every test method has the same one-`Map` signature regardless of how many columns the data has.

So one test method runs **N times with N data rows**, where N comes from the runner manager — data iteration count is externally configurable without touching code.

---

## 7. Test Data Management

Three interchangeable sources, selected by one config key (`datasource`), all normalized to the same shape:

### a) Database source (`datasource=db`)
- **Connection:** `DataBaseConnectionUtils` — static-initializer singleton `Connection`. Ironically keyed off `runmode`: `local` → MySQL (creds from `DataBaseConfig.json` via `JsonConfigUtils`), `remote` → SQLite file `testdata/database/inputData.db`. Auto-reconnects if closed.
- **Queries:** externalized in `SqlQueries.json` as named query groups:
  ```json
  { "selectqueries":     { "HRMLoginTestData": "select * from hrm_testdata;" },
    "runnerlistqueries": { "HRMRunnerlist":    "select * from hrm_runmanager;" } }
  ```
- **Flow (`JsonUtils.generateTestDataJson()`):**
  1. Execute each `selectqueries` entry.
  2. Convert `ResultSet` → `List<Map<colName,value>>` via `ResultSetMetaData` (schema-agnostic — works with any columns).
  3. **Filter rows by `environment` column == configured `env`** (e.g. only `qa` rows).
  4. Serialize to `<ENV>_TestData.json` with Jackson — i.e. the DB is dumped to the same JSON format the `json` source reads.
- Same pattern for the runner list (`generateRunnerListJsonData()` → `RUNMANAGER.json`).

### b) Excel source (`datasource=excel`)
- **File:** `testdata/excel/testdata.xlsx` with two sheets: `RUNMANAGER` (runner list) and `DATA` (test data).
- **Reader:** `ExcelUtils.getTestDetails(sheetName)` (Apache POI):
  - Row 0 = headers → map keys; every other row → one `Map<String,String>`.
  - `convertCellValueToString()` normalizes cell types (numeric → int-string when whole, boolean, formula).
- Runner list is converted to RUNMANAGER.json; the `DATA` sheet is read directly by the DataProvider.

### c) JSON source (`datasource=json`)
- **File:** `testdata/json/<ENV>_TestData.json`, keyed by environment at the top level:
  ```json
  { "qa": {
      "loginData": [
        { "testcasename": "loginLogoutTest", "execute": "yes",
          "username": "Admin", "password": "admin123", "environment": "qa" }
      ]
  } }
  ```
- `JsonUtils.getJsonTestDataDetails()` reads the file with Jackson (`FAIL_ON_UNKNOWN_PROPERTIES=false`), takes the sub-tree for the configured environment, and flattens all inner lists into one `List<Map<String,Object>>`.

### The unifying idea
> **JSON is the canonical format.** DB and Excel are "compiled" to JSON first; the consumption layer (interceptor + data provider) always works with the same JSON-shaped maps. Adding a new source only requires a new "generate JSON" function.

Environment switching (`env=qa|dev|...`) selects both the JSON file name and the DB row filter — one framework, many environments.

---

## 8. Page Object Model & Action Layer

### `BaseAction` (in `pages/CommonAction/`) — the protected action toolkit
Page objects extend `BaseAction`, which wraps every Selenium interaction with **wait + logging + reporting + error capture**:

| Method | What it does |
|---|---|
| `click(by, waitStrategy, elementName)` | Wait → click → Extent pass log w/ screenshot → Log4j2; on failure: Extent fail w/ screenshot |
| `sendKeys(by, value, waitStrategy, elementName, log)` | Wait → type; optional report logging (e.g. hide passwords) |
| `selectedValueInDropdownByIndex(...)` | `Select` wrapper |
| `isVisible(...)`, `getText(...)`, `clear(...)` | Wait-based reads/cleans with logging |
| `maximizeWindow/minimizeWindow/setFullScreen/setSizeWindow` | Window management |
| `getTitle()` | Page title |

Every action takes a **human-readable `elementName`** so reports/logs read like a story ("Clicked on element 'buttonLogin'").

### Page objects — fluent chaining
```java
public final class HRMLoginPage extends BaseAction {
    private static final By textboxUsername = By.name("username");

    public HRMLoginPage enterUsername(String u) {
        sendKeys(textboxUsername, u, WaitStrategy.PRESENCE, "textboxUsername", true);
        return this;                       // ← returns self for chaining
    }
    public HRMHomePage clickLoginButton() {
        click(buttonLogin, WaitStrategy.CLICKABLE, "buttonLogin");
        return new HRMHomePage();          // ← returns the NEXT page
    }
}
```
- Locators are `private static final By` fields — encapsulated, never exposed to tests.
- Methods return `this` or the next page object → tests are a **fluent chain**:
```java
String title = new HRMLoginPage()
    .enterUsername(data.get("username")).enterPassword(data.get("password"))
    .clickLoginButton().clickWelcome().clickLogoutButton().getPageTitle();
Assertions.assertThat(title).isEqualTo("OrangeHRM");   // AssertJ in the TEST, not the page
```
- **Assertions live only in test classes** (AssertJ), never in page objects.

---

## 9. Wait Strategy — ExplicitWaitFactory

`ExplicitWaitFactory.performExplicitWait(WaitStrategy, By)` is the **only** place elements are located:

```java
WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(FrameworkConstants.getExplicitWait()));
switch (waitStrategy) {
    case CLICKABLE: return wait.until(ExpectedConditions.elementToBeClickable(by));
    case PRESENCE:  return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    case VISIBLE:   return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    case NONE:      return getDriver().findElement(by);   // no wait
}
```

- The **caller chooses the wait semantics per interaction** via the `WaitStrategy` enum — no implicit waits, no `Thread.sleep`.
- Timeout is one global config value (`explicitwaittimeout=30`).
- `TimeoutException` is caught and logged; returns `null` (the action layer then logs the failure).

---

## 10. Reporting

Three classes, same pattern as driver management (facade / ThreadLocal manager / logger):

### `ExtentReport` — lifecycle (singleton)
- `initReports(suiteName)` (called from `ListenerClass.onStart`):
  - Creates a singleton `ExtentReports`.
  - Attaches **two** `ExtentSparkReporter`s:
    1. Main report — Dashboard + Test views, themed, titled with the suite name. Path from `FrameworkConstants.getReportPath()`: timestamped folder (`extent-test-output/<date_time>/..._Automation Report.html`) when `overridereports=no`, or fixed `extent-test-output/index.html` when `yes`.
    2. **Failed-only report** (`<suite>_failed_testcase.html`) using a status filter `Status.FAIL` — a ready-made rerun/triage artifact.
- `createTest(name)` (called from `onTestStart`) → creates an `ExtentTest` node and stores it via `ExtentManager`.
- `flushReports()` (called from `onFinish`) → flush + **auto-open the HTML report in the default browser** (`Desktop.getDesktop().browse(...)`).
- `addAuthors` / `addCategories` use the `FrameworkAnnotation` metadata (currently commented out in the listener).

### `ExtentManager` — thread safety
`ThreadLocal<ExtentTest>` with get/set/unload — parallel tests each write to their own report node, mirroring `DriverManager`.

### `ExtentLogger` — step logging API
- `pass/fail/skip/info(message)` — color-coded labels (`MarkupHelper`).
- Overloads `pass/fail/skip(message, isScreenshotNeeded)`:
  - Check the per-status config toggle (`passedstepscreenshot` etc.).
  - If enabled → attach a **Base64 screenshot inline** (`MediaEntityBuilder.createScreenCaptureFromBase64String`) — no image files on disk, the HTML is self-contained.

### `ReportDatabaseController` — results audit in DB
On every test success/failure/skip, `ListenerClass` calls:
```java
storeReportInDatabase(testCaseName, "Pass" | "Fail" | "Skip");
```
which (in local runmode) inserts into `ui_automation_result.hrm_result`:
`(environment, testCaseName, status, executionTime)` — giving a **queryable execution history across runs** (trend dashboards, flakiness analysis).

---

## 11. Screenshots

`ScreenshotUtils.getBase64Image()`:
```java
((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
```
- Pulls the driver from the ThreadLocal → always the right browser in parallel runs.
- Base64-only (embedded in the Extent HTML), controlled per status by three config flags.
- Invoked automatically by every `BaseAction` action and by the result listeners — test authors never call it directly.

---

## 12. Logging

### `LogService` — a Log4j2 wrapper with enum-dispatched levels
```java
private static final Map<LogType, Consumer<String>> LOGSMAP = new EnumMap<>(LogType.class);
// TRACE/DEBUG/INFO/WARN/ERROR/FATAL each map to a logger lambda
public static void log(LogType logType, String message) { LOGSMAP.get(logType).accept(message); }
```
- Single static entry point used everywhere: `log(INFO, "Clicked ...")`.
- The `LogType` enum + `EnumMap` of `Consumer<String>` replaces if/else chains.

### `log4j2.xml` configuration
- File appender → `logs/execution-<yyyy-MM-dd_HH_mm_ss>.log` (**timestamped log per run**).
- Pattern: `%d{yyyy-MM-dd HH:mm:ss} - (%p) %m%n`.
- Root level `TRACE`, size-based rolling policy (~2 MB).

### Dual-channel logging philosophy
Every meaningful action is logged **twice**:
- `ExtentLogger` → business-readable HTML report (for stakeholders), with screenshots.
- `LogService` → technical file log (for engineers debugging).

---

## 13. Listeners

All registered in `testng.xml`:

| Listener | TestNG interface | Responsibility |
|---|---|---|
| `ListenerClass` | `ISuiteListener`, `ITestListener` | Suite start/finish → init/flush Extent report. Test start → create Extent test node. Test pass/fail/skip → Extent log + screenshot + **DB result insert** + Log4j2 log + stack trace on failure. |
| `MethodInterceptor` | `IMethodInterceptor` | Runner manager (see §5). |
| `AnnotationTransformer` | `IAnnotationTransformer` | Injects DataProvider + RetryAnalyzer into every `@Test` (see §6). |
| `RetryFailedTests` | `IRetryAnalyzer` | Retry logic (below). |

### Retry mechanism
```java
public boolean retry(ITestResult result) {
    if (config RETRY == "yes") { return count++ < retries; }   // retries = retryattempts
    return false;
}
```
- Config-driven: `retry=yes` + `retryattempts=N` → each failed test re-executes up to N times before being marked failed.
- Applied globally by `AnnotationTransformer` — no per-test annotation needed.

---

## 14. Custom Annotations & Enums

### `@FrameworkAnnotation` (RUNTIME, METHOD)
```java
String[] author() default {};
CategoryType[] category() default {};   // SMOKE, FULL_REGRESSION, HIGH_LEVEL_TESTING
```
Test metadata consumed by the listener to tag Extent tests with author/category (reporting filters).

### Enums as the framework's type system
| Enum | Purpose |
|---|---|
| `ConfigProperties` | Type-safe config keys (URL, BROWSER, DATASOURCE, ENV, RETRY, RUNMODE, ...) |
| `WaitStrategy` | CLICKABLE, PRESENCE, VISIBLE, NONE |
| `LogType` | TRACE, DEBUG, INFO, WARN, ERROR, FATAL |
| `CategoryType` | Test categories for annotation/reporting |
| `DataBaseProperties` | DB config keys (HOSTNAME, PORT, SCHEMA, DBUSERNAME, DBPASSWORD) |

---

## 15. Custom Exceptions

All extend `BaseException` (an unchecked `RuntimeException` wrapper):

| Exception | Thrown when |
|---|---|
| `PropertiesFileException` | Config key missing in properties file |
| `ExcelFileNotFoundException` | Excel test data file missing |
| `JsonExceptions` | JSON mapping/generation failures |
| `BrowserInvocationException` | WebDriver creation fails (e.g. bad grid URL) |
| `SQLConnectionException` | DB query/connection failures |
| `InvalidFilepathException` | Runner-list / test-data JSON path wrong |

Purpose: convert checked low-level exceptions into **descriptive, framework-domain unchecked errors** with actionable messages ("please check your path: ...").

---

## 16. Parallel Execution & Selenium Grid (Docker)

### TestNG parallelism
```xml
<suite name="Suite" parallel="tests" thread-count="3">
```
Safe because **all mutable per-test state is ThreadLocal** (WebDriver + ExtentTest).

### Remote execution
- `runmode=remote` → `RemoteWebDriver` pointed at `seleniumgridaddress` (default `http://localhost:4444/`).
- `docker-compose.yaml` stands up a full **Selenium Grid 4.20**: `selenium-hub` + chrome / chrome-98 / edge / firefox nodes, each with `SE_NODE_MAX_SESSIONS=5` and 2 GB shm → up to 5 concurrent sessions per node, plus a pinned-version node (chrome 98) for backward-compat testing.
- Run with `docker-compose -f docker-compose.yaml up -d`, then `runmode=remote`.

### Maven execution
```bash
mvn test -P demo_tests        # surefire profile bound to testng.xml
```

---

## 17. Playwright Migration Blueprint

Feature-by-feature mapping to a **Playwright + TypeScript** framework (`@playwright/test`):

| # | Java/Selenium feature | How it works today | Playwright equivalent / recommendation |
|---|---|---|---|
| 1 | `config.properties` + `PropertyUtils` enum access | Static-loaded properties map | `playwright.config.ts` + typed `env.ts` module reading `process.env` / `.env` files (`dotenv`). Use a typed `Config` interface instead of enums. Use [config `projects`] for browser/env matrices. |
| 2 | `DriverSupplier` browser factory (chrome/edge/firefox, headless, args) | switch + Options objects | **Built-in.** `projects: [{ name: 'chromium', use: { ...devices } }, ...]` in `playwright.config.ts`; `headless`, `viewport`, `launchOptions.args` per project. No factory code needed. |
| 3 | `DriverManager` ThreadLocal | Thread-isolated WebDriver | **Built-in.** Each test gets an isolated `page`/`context` fixture; workers are separate processes. No ThreadLocal needed. |
| 4 | `Driver.initDriver/quitDriver` + `BaseTest` @Before/@AfterMethod | Fresh browser per test, navigate to URL | Built-in fixtures auto create/dispose context per test. Navigation: `baseURL` in config + `await page.goto('/')` in a fixture or `test.beforeEach`. |
| 5 | Selenium Grid / docker-compose remote run | `RemoteWebDriver` + grid containers | Playwright workers scale locally without a grid; for distributed runs use `--shard`, CI matrix, or a remote provider. Official Docker image `mcr.microsoft.com/playwright` replaces node containers. `connect()`/`connectOverCDP()` exist for remote browsers if needed. |
| 6 | **Runner Manager (MethodInterceptor)** — execute flag, priority, count from DB/Excel/JSON | `IMethodInterceptor` filters/annotates methods pre-run | No interceptor concept; recreate by **generating the run config in `globalSetup`**: read runner list (DB/Excel/JSON) → write canonical `runmanager.json`; then either (a) drive `test.skip(!shouldRun(title))` from a helper, (b) generate `--grep` patterns / tag filters (`@smoke`), or (c) generate the data-driven loop count (see #8). Priority/ordering: prefer independent tests; if needed, use serial mode in a describe block. |
| 7 | `AnnotationTransformer` (global DataProvider + retry injection) | Rewrites annotations at runtime | Not needed: retries are config-level (`retries: N` in `playwright.config.ts`); data-driven is explicit (see #8). Global behaviors go in **custom fixtures** (`test.extend`) instead of bytecode-level transforms. |
| 8 | **DataProvider** (`Object[][]` of Maps, filtered by testcasename/execute, capped by count) | `@DataProvider` + method-name matching | **Parameterized loop pattern:** `for (const row of getTestData('loginLogoutTest')) { test(\`login ${row.username}\`, async ({page}) => {...}) }`. Implement `getTestData(name)` to read the canonical JSON, filter `execute === 'yes'`, and `slice(0, invocationCount)` — direct port of the Java logic. |
| 9 | Test data: DB → JSON generation (`JsonUtils.generateTestDataJson`) | JDBC + Jackson, env-filtered | `globalSetup` script using `mysql2`/`better-sqlite3` → write `<ENV>_TestData.json`. Keep the "JSON is canonical" design — it ports perfectly. |
| 10 | Test data: Excel (Apache POI) | Header row → list of maps | `xlsx` (SheetJS) or `exceljs`: `XLSX.utils.sheet_to_json(sheet)` gives the same array-of-objects in one line. |
| 11 | Test data: env-keyed JSON (`QA_TestData.json`) | Jackson read + env subtree flatten | Plain `JSON.parse(fs.readFileSync(...))` + `data[process.env.TEST_ENV]`. Type it with an interface. |
| 12 | `ExplicitWaitFactory` (CLICKABLE/PRESENCE/VISIBLE/NONE) | WebDriverWait + ExpectedConditions | **Built-in auto-waiting.** `locator.click()` already waits for visible/enabled/stable. For explicit cases: `locator.waitFor({ state: 'visible'|'attached' })` and `expect(locator).toBeVisible()`. Global timeout via `actionTimeout`/`expect.timeout`. Delete the factory entirely. |
| 13 | `BaseAction` wrapper (click/sendKeys/etc. with logging + screenshots) | Protected methods wrapping Selenium | Two options: (a) lean on built-in **tracing** (`trace: 'on-first-retry'`) which records every action + screenshots + DOM — replaces most of this layer; (b) if you want named-step business logging, wrap actions with `test.step('Click login button', () => locator.click())` in a small `BaseActions` class taking `page`. |
| 14 | Page Object Model, fluent chaining, `By` locators | Classes returning `this`/next page | Same pattern: page classes receiving `page: Page` in constructor, `readonly locators = this.page.getByRole(...)`. Fluent chaining works with async: `return this;` / `return new HomePage(this.page);` (await each call). Prefer `getByRole/getByLabel` over XPath. Inject pages as **fixtures** (`test.extend<{ loginPage: LoginPage }>`). |
| 15 | Extent Reports (HTML + failed-only report, dashboard) | ExtentSparkReporter, ThreadLocal ExtentTest | Built-in **HTML reporter** (`reporter: [['html'], ['list']]`) + trace viewer. For richer dashboards: `allure-playwright` or `monocart-reporter`. Failed-only view is a filter in the built-in HTML report. Authors/categories → test **annotations** & **tags** (`test('...', { tag: '@smoke', annotation: {...} })`). |
| 16 | Step screenshots (Base64, per-status config flags) | TakesScreenshot + MediaEntityBuilder | Built-in: `screenshot: 'only-on-failure'` (or `'on'`) in `use{}`; screenshots auto-attach to the HTML report. Manual: `await testInfo.attach('name', { body: await page.screenshot() })`. |
| 17 | Log4j2 `LogService` (file log per run) | EnumMap of consumers | `winston` or `pino` with a timestamped file transport; or rely on Playwright's trace + `console` capture. Keep a tiny `log.ts` facade for parity. |
| 18 | `ListenerClass` (ITestListener / ISuiteListener) | Suite/test lifecycle hooks | Custom **Reporter** (`class MyReporter implements Reporter`: `onBegin/onTestEnd/onEnd`) — this is where you port the **DB result audit insert** (`onTestEnd` → insert name/status/duration). Register via `reporter: [['./my-reporter.ts'], ['html']]`. |
| 19 | `RetryFailedTests` (config-driven retry count) | IRetryAnalyzer per test | Built-in: `retries: Number(process.env.RETRIES ?? 0)` in config; per-project overrides; `test.info().retry` tells you the attempt number. |
| 20 | `ReportDatabaseController` (Pass/Fail audit table) | JDBC insert on each result | In the custom reporter's `onTestEnd`, insert into MySQL via `mysql2/promise`. Identical table schema works. |
| 21 | Parallel: `parallel="tests" thread-count="3"` | TestNG threads + ThreadLocal | Built-in: `workers: 3`, `fullyParallel: true`. Process isolation > thread isolation; no shared statics — pass state via fixtures. |
| 22 | Custom exceptions | Domain unchecked exceptions | Custom `Error` subclasses (`class TestDataError extends Error`) thrown from data/config helpers. |
| 23 | Maven surefire profile | `mvn test -P demo_tests` | `npx playwright test` + npm scripts (`"test:qa": "TEST_ENV=qa playwright test --project=chromium"`). |
| 24 | AssertJ fluent assertions in tests | `Assertions.assertThat(title).isEqualTo(...)` | Built-in web-first assertions: `await expect(page).toHaveTitle('OrangeHRM')` — auto-retrying, preferred over value-grab-then-assert. |

### Suggested Playwright project skeleton

```
playwright-framework/
├── playwright.config.ts          # projects (browsers/envs), retries, workers, reporters, baseURL
├── .env / .env.qa                # url, env, datasource, db creds, toggles
├── global-setup.ts               # generate runmanager.json + <ENV>_TestData.json from DB/Excel
├── src/
│   ├── config/env.ts             # typed config accessor (≈ PropertyUtils + ConfigProperties)
│   ├── data/
│   │   ├── runner-manager.ts     # shouldRun(testName), getInvocationCount(testName)
│   │   ├── data-provider.ts      # getTestData(testName): Row[]  (db|excel|json switch)
│   │   ├── db-client.ts          # mysql2/sqlite (≈ DataBaseConnectionUtils)
│   │   └── excel-reader.ts       # SheetJS (≈ ExcelUtils)
│   ├── fixtures/fixtures.ts      # test.extend: page objects, auto-goto baseURL, logger
│   ├── pages/
│   │   ├── base.page.ts          # optional test.step-wrapped helpers (≈ BaseAction)
│   │   ├── hrm-login.page.ts
│   │   └── hrm-home.page.ts
│   ├── reporting/db-reporter.ts  # custom Reporter → MySQL audit (≈ ListenerClass + ReportDatabaseController)
│   └── utils/logger.ts           # winston file log (≈ LogService)
└── tests/
    └── orange-hrm.spec.ts        # for-of data loop + test.skip via runner manager
```

### What you should NOT port (Playwright already does it)
- ThreadLocal driver/report management → worker isolation is built in.
- ExplicitWaitFactory / WaitStrategy → auto-waiting + web-first assertions.
- Browser factory & headless flags → config `projects` + `use` options.
- Retry analyzer → `retries` config.
- Screenshot plumbing → `screenshot`/`video`/`trace` config options.
- Selenium Grid for parallelism → workers + sharding.

### What IS worth porting (the framework's real IP)
1. **Runner Manager** — external execute/priority/count control (globalSetup + skip/grep helper).
2. **Multi-source test data normalized to canonical JSON** (DB/Excel/JSON → one shape).
3. **Environment-keyed test data** (`<ENV>_TestData.json` + env row filtering).
4. **DB results audit** (custom Reporter writing Pass/Fail history).
5. **Named-step business-readable reporting** (`test.step` + elementName-style messages).
6. **Type-safe config access** (typed env module replacing the enum pattern).

---

## Quick Reference — Where each feature lives

| Feature | Implementation files |
|---|---|
| Config management | `utils/PropertyUtils.java`, `enums/ConfigProperties.java`, `config/config.properties`, `constants/FrameworkConstants.java` |
| Driver management | `driver/Driver.java`, `driver/DriverManager.java`, `factories/DriverSupplier.java`, `tests/BaseSetup/BaseTest.java` |
| Runner manager | `listeners/MethodInterceptor.java`, `runnerlist/RUNMANAGER.json`, `utils/JsonUtils.java` |
| Data-driven invocation | `listeners/AnnotationTransformer.java`, `utils/DataProviderUtils.java` |
| Test data sources | `utils/JsonUtils.java` (DB→JSON, JSON), `utils/ExcelUtils.java` (Excel), `utils/DataBaseConnectionUtils.java`, `Configuration/SqlQueries.json`, `Configuration/DataBaseConfig.json` |
| Waits | `factories/ExplicitWaitFactory.java`, `enums/WaitStrategy.java` |
| Page objects / actions | `pages/CommonAction/BaseAction.java`, `pages/orangehrmpage/*.java` |
| Reporting | `reports/ExtentReport.java`, `reports/ExtentManager.java`, `reports/ExtentLogger.java` |
| Result audit DB | `utils/ReportDatabaseController.java` |
| Screenshots | `utils/ScreenshotUtils.java` |
| Logging | `logger/LogService.java`, `enums/LogType.java`, `log4j2.xml` |
| Lifecycle & retry | `listeners/ListenerClass.java`, `listeners/RetryFailedTests.java` |
| Parallel / grid | `testrunners/testng.xml`, `docker-compose.yaml` |
