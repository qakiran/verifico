package data.sql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySQLData implements SQLData {
	
	private Logger log;
	private String url;
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private ScriptRunner scriptRunner;
	private String dbServer;
	private String dbName;
	private String user;
	private String password;
	private final String DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

	public MySQLData(String dbServer, String dbName, String user, String password) {
		this.log = LogManager.getLogger(this.getClass());
		this.dbServer = dbServer;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Initializes MySQL JDBC Driver.
	 * 
	 * @throws ExceptionInInitializerError
	 */
	
	private void createSQLInstance() throws ExceptionInInitializerError {
		this.log.debug("Initializing MySQL JDBC Driver.");
		try {
			Class.forName(this.DRIVER_MYSQL).getDeclaredConstructor().newInstance();
			this.log.debug("Successfully instantiated MySQL JDBC Driver.");
		} catch (LinkageError e) {
			this.log.fatal("Encountered LinkageError while instantiating MySQL JDBC Driver!");
		} catch (IllegalAccessException e) {
			this.log.fatal("Encountered IllegalAccessException while instantiating MySQL JDBC Driver!");
		} catch (InstantiationException e) {
			this.log.fatal("Encountered InstantiationException while instantiating MySQL JDBC Driver!");
		} catch (ClassNotFoundException e) {
			this.log.fatal("Encountered ClassNotFoundException while instantiating MySQL JDBC Driver!");
		} catch (SecurityException e) {
			this.log.fatal("Encountered SecurityException while instantiating MySQL JDBC Driver!");
		} catch (Exception e) {
			this.log.fatal("Encountered Exception while instantiating MySQL JDBC Driver!");
		}
	}
	
	/**
	 * Initializes SQL Connection.
	 */
	
	private void initializeConnection() {
		this.log.debug("Initializing MySQL Connection.");
		this.url = "jdbc:mysql://" + this.dbServer + ":3306/" + this.dbName;
		try {
			this.connection = DriverManager.getConnection(this.url, this.user, this.password);
			this.scriptRunner = new ScriptRunner(this.connection);
			this.log.debug("Successfully initialized SQL Connection.");
		} catch (SQLException e) {
			this.log.fatal("Encountered SQLException while initializing MySQL Connection!");
		} catch (Exception e) {
			this.log.fatal("Encountered Exception while initializing MySQL Connection!");
		}
	}
	
	/**
	 * Creates a Statement Object for sending SQL statements to the database.
	 */
	
	private void createSQLStatement() {
		this.log.debug("Creating SQL Statement.");
		try {
			this.statement = this.connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY
			);
			this.log.debug("Successfully created SQL Statement.");
		} catch (SQLException e) {
			this.log.fatal("Encountered SQLException while creating SQL Statement!");
		} catch (Exception e) {
			this.log.fatal("Encountered Exception while creating SQL Statement!");
		}
	}
	
	/**
	 * Reads SQL Script defined at the File Path and converts it in a String Format.
	 * 
	 * @param sqlFilePath File Path of SQL Script.
	 * @return SQL Script in String Format
	 */
	
	private String readSQLScript(String sqlFilePath) {
		String sqlQuery = null;
		try {
			this.log.debug("Reading SQL Script.");
			FileReader fileReader = new FileReader(sqlFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			sqlQuery = bufferedReader.readLine();
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			this.log.fatal("Encountered FileNotFoundException while retrieving SQL Data!");
		} catch (IOException e) {
			this.log.fatal("Encountered IOException while retrieving SQL Data!");
		} catch (Exception e) {
			this.log.fatal("Encountered Exception while retrieving SQL Data!");
		} 
		return sqlQuery;
	}
	
	@Override
	public Object[][] select(String sqlFilePath) {
		this.createSQLInstance();
		this.initializeConnection();
		this.createSQLStatement();
		String sqlQuery = this.readSQLScript(sqlFilePath);
		Object[][] sqlData = null;
		this.log.info("Getting SQL Data.");
		if (sqlQuery.toLowerCase().trim().startsWith("select")) {
			try {
				this.resultSet = this.statement.executeQuery(sqlQuery);
				this.log.info("Successfully retrieved SQL Data.");
			} catch(SQLException e) {
				this.log.fatal("Encountered SQLException while retrieving SQL Data!");
			} catch(Exception e) {
				this.log.fatal("Encountered Exception while retrieving SQL Data!");
			}
		} else {
			this.log.error("Invalid SQL Script! \"SELECT\" SQL Statements are expected.");
		}
		sqlData = this.parseResultSetToObjectArray(this.resultSet);
		this.closeConnection();
		return sqlData;
	}
	
	/**
	 * Parses an input Result Set to an Object Array.
	 * 
	 * @param  resultSet Input ResultSet Object
	 * @return Parsed Object Array
	 */
	
	private Object[][] parseResultSetToObjectArray(ResultSet resultSet) {
		Object[][] sqlData = null;
		try {
			int columnCount = this.getColumnCount();
			int rowCount = this.getRowCount();
			sqlData = new Object[rowCount][columnCount];
			if (rowCount > 0) {
				for (int i = 0; i < rowCount; i++) {
					for (int j = 0; j < columnCount; j++) {
						resultSet.next();
						sqlData[i][j] = resultSet.getObject(j + 1);
						continue;
					}
				}
			} else {
				this.log.fatal("No Data is available.");
			}
		} catch(SQLException e) {
			this.log.fatal("Encountered SQLException while retrieving SQL Data!");
			e.printStackTrace();
		} catch(Exception e) {
			this.log.fatal("Encountered Exception while retrieving SQL Data!");
			e.printStackTrace();
		}
		return sqlData;
	}
	
	/**
	 * Gets the Row Count of an executed SELECT Statement.
	 * 
	 * @return Row Count
	 */
	
	private int getRowCount() {
		int rows = 0;
		try {
			this.resultSet.last();
			rows = this.resultSet.getRow();
			this.resultSet.beforeFirst();
		} catch (SQLException e) {
			this.log.error("Encountered SQLException while getting Row Count.");
		}
		return rows;
	}
	
	/**
	 * Gets the Column Count of an executed SELECT Statement.
	 * 
	 * @return Column Count
	 */
	
	private int getColumnCount() {
		int columns = 0;
		try {
			columns = this.resultSet.getMetaData().getColumnCount();
		} catch (SQLException e) {
			this.log.error("Encountered SQLException while getting Column Count.");
		}
		return columns;
	}
	
	/**
	 * Closes SQL Connection.
	 * 
	 */
	
	private void closeConnection() {
		this.log.debug("Closing All Connections and Emptying ResultSet.");
		try {
			if(this.resultSet != null) this.resultSet.close();
			if(this.statement != null) this.statement.close();
			if(this.connection != null) this.connection.close();
			if(this.scriptRunner != null) this.scriptRunner.closeConnection();
		} catch (SQLException e) {
			this.log.fatal("Encountered SQLException while closing SQL Connection!");
			e.printStackTrace();
		} catch (Exception e) {
			this.log.fatal("Encountered Exception while closing SQL Connection!");
			e.printStackTrace();
		}
		this.log.debug("Successfully closed all connections");
	}
	
}