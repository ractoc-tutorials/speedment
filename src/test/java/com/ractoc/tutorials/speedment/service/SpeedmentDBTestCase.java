package com.ractoc.tutorials.speedment.service;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

/**
 * Base class for unit testing with  Speedment. This class will startup an embedded MariaDB database to be used with Speedment.
 * It also contains various convenience methods for use with DBUnit.
 */
public abstract class SpeedmentDBTestCase {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    static String dbUrl;
    private static DB db;
    private static String dbSchema;
    private static String initialDataSetFile;
    private IDatabaseTester databaseTester;
    private IDatabaseConnection connection;

    /**
     * Creates and starts a new, temporary, MariaDB Database based on the supplied information.
     * <p>
     * This method should be called from the @BeforeAll method in the actual testcase.
     *
     * @param dbName         The title of the database instance. This needs to be the same as the title in the speedment.json file.
     * @param portNumber     The port number on which to connect to the database. This needs to be the same as the title in the speedment.json file.
     * @param schemaScript   The script containing the SQL to setup the database.
     * @param initialDataset The initial dataset to load at the start of each testcase.
     * @throws ManagedProcessException An indication something went wrong while creating the database.
     */
    static void createDatabase(String dbName, int portNumber, String schemaScript, String initialDataset) throws ManagedProcessException {
        SpeedmentDBTestCase.dbSchema = dbName;
        SpeedmentDBTestCase.initialDataSetFile = initialDataset;

        DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
        configBuilder.setPort(portNumber);
        // make sure the temporary datafiles are created in the target folder so they
        // are cleaned each time a mvn clean is performed
        configBuilder.setDataDir("target/test/db");
        db = DB.newEmbeddedDB(configBuilder.build());
        db.start();
        db.createDB(dbName);
        db.source(schemaScript);

        dbUrl = configBuilder.getURL(dbName);
    }

    /**
     * Stops the temporary MariaDB database.
     * <p>
     * This method should be called from the @AfterAll method in the testcase.
     *
     * @throws ManagedProcessException An indication something went wrong while stopping the database.
     */
    static void destroyDatabase() throws ManagedProcessException {
        if (db != null) {
            db.stop();
        }
    }

    /**
     * Method for seting up the database based on the provided initial dataset.
     * @throws Exception Propagates the exception from the cleanlyInsert
     */
    @BeforeEach
    public void setUpDbUnit() throws Exception {
        cleanlyInsert(getDataSet(initialDataSetFile));
    }

    /**
     * Method for clearing all tables at teardown. This will remove ALL data from the database.
     * @throws Exception Something went wrong while clearing the database.
     */
    @AfterEach
    public void tearDownDbUnit() throws Exception {
        databaseTester.setTearDownOperation(DatabaseOperation.DELETE_ALL);
        databaseTester.onTearDown();
    }

    /**
     * Returns the internal database connections as used by dbUnit.
     * This connection can be used when asserting the actual state of a table with the expected state of a table.
     *
     * @return The internal database connection as used by dbUnit.
     */
    private IDatabaseConnection getConnection() {
        return connection;
    }

    /**
     * Get a dataset based on the supplied FlatXml datafile. The FlatXml dataformat can be found on the dbUnit website.
     * In this FlatXml file, all table names should be in the same case as they are in the actual database. Column names are case insensitive.
     *
     * @param filename Name of the FlatXml file. This file should be located onthe classpath, for example src/test/resources
     * @return The DataSet representing the FlatXml file.
     * @throws DataSetException Something went wrong while reading  the FlatXml file.
     */
    private IDataSet getDataSet(String filename) throws DataSetException {
        ReplacementDataSet dataSet = new ReplacementDataSet(
                new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream(filename)));
        dataSet.addReplacementObject("[NULL]", null);
        return dataSet;
    }

    /**
     * Compare a table based on the supplied FlatXml datafile. The FlatXml dataformat can be found on the dbUnit website.
     * The table in the test database is compared to the table definition in the FlatXml file.
     *
     * @param filename  Name of the FlatXml file. This file should be located onthe classpath, for example src/test/resources
     * @param tableName Name of the table to compare
     * @throws SQLException Something went wrong while reading  the FlatXml file or the assertion failed.
     * @throws DatabaseUnitException Something went wrong while reading  the FlatXml file or the assertion failed.
     */
    void compareItemTable(String filename, String tableName) throws SQLException, DatabaseUnitException {
        IDataSet databaseDataSet = getConnection().createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);

        IDataSet expectedDataSet = getDataSet(filename);
        ITable expectedTable = expectedDataSet.getTable(tableName);

        ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                expectedTable.getTableMetaData().getColumns());

        Assertion.assertEquals(new SortedTable(expectedTable),
                new SortedTable(filteredTable, expectedTable.getTableMetaData()));
    }

    /**
     * This method will run the provided dataset against the database. This will insert all rows in the dataset.
     *
     * @param dataSet The dataset to insert into the database. This can contain multiple rows in multiple tables.
     * @throws Exception There was a problem inserting the dataset into the database
     */
    private void cleanlyInsert(IDataSet dataSet) throws Exception {
        databaseTester = new MySqlDatabaseTester(JDBC_DRIVER,
                dbUrl, USERNAME, PASSWORD, dbSchema);
        databaseTester.setSetUpOperation(DatabaseOperation.INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
        connection = databaseTester.getConnection();
    }

    /**
     * Internal JdbDatabaseTester implementation specific for MySql.
     * This is required since MySQL uses a non-default database connection.
     */
    private static class MySqlDatabaseTester extends JdbcDatabaseTester {

        MySqlDatabaseTester(String driverClass, String connectionUrl, String username, String password,
                            String schema) throws ClassNotFoundException {
            super(driverClass, connectionUrl, username, password, schema);
        }

        /**
         * Converts the default database connection to a MySQL database connection.
         */
        @Override
        public IDatabaseConnection getConnection() throws Exception {
            IDatabaseConnection dbConn = super.getConnection();
            return new MySqlConnection(dbConn.getConnection(), getSchema());
        }
    }
}
