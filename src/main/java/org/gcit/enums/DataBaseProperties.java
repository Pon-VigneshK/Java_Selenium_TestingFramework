package org.gcit.enums;

/**
 * Enum representing the possible properties required for database configuration.
 * This enum contains constants that map to the keys used in the configuration map.
 *
 * Properties:
 * - HOSTNAME: The hostname of the database server.
 * - PORT: The port number on which the database server is listening.
 * - SCHEMA: The name of the database schema.
 * - DBUSERNAME: The username for accessing the database.
 * - DBPASSWORD: The password for accessing the database.
 *
 * This enum can be used in conjunction with configuration utilities to retrieve
 * specific configuration values required for database connectivity.
 */
public enum DataBaseProperties {
    HOSTNAME,
    PORT,
    SCHEMA,
    DBUSERNAME,
    DBPASSWORD

}
