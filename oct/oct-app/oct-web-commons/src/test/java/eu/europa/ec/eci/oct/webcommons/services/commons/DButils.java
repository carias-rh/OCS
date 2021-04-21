package eu.europa.ec.eci.oct.webcommons.services.commons;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Ignore;
import org.junit.Test;

public class DButils extends ServicesTest {

	/**
	 * @throws Exception
	 *             convert db tables in xml dataset for the test environment
	 *             setup
	 */
	protected static final String SQL_SCRIPT_PATH = "src/test/resources/db/test_db_script.sql";
	protected static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
	protected static final String DB_CONNECTION_URL = "jdbc:mysql://localhost:3306/oct_test";
	protected static final String USERNAME = "root";
	protected static final String PASSWORD = "root";
	protected Connection connection;

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void DatabaseExportSample() throws Exception {
		// database connection
		@SuppressWarnings("unused")
		Class driverClass = Class.forName("org.hsqldb.jdbcDriver");
		Connection jdbcConnection = DriverManager.getConnection(DB_CONNECTION_URL, USERNAME,
				PASSWORD);
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(connection);
		partialDataSet.addTable("sigByCountryView", "SELECT * FROM oct_social_media_msg");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

		// full database export
		// IDataSet fullDataSet = connection.createDataSet();
		// FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		// String[] depTableNames =
		// TablesDependencyHelper.getAllDependentTables( connection, "X" );
		// IDataSet depDataset = connection.createDataSet( depTableNames );
		// FlatXmlDataSet.write(depDataset, new
		// FileOutputStream("dependents.xml"));
	}

}
