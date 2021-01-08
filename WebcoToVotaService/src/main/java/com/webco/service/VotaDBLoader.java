package com.webco.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VotaDBLoader {


	private static final Logger LOGGER = LoggerFactory.getLogger(VotaDBLoader.class);

	@Value("${vota.select.query}")
	String selectQuery;

	@Value("${vota.drop.tmp.table.query}")
	String dropTmpTableQuery;

	@Value("${vota.temp.query}")
	String createTempTable;

	@Value("${vota.select.tmp.table.query}")
	String selectTmpTableQuery;

	@Value("${vota.update.current.log.query}")
	String updateCurrentLogQuery;

	@Value("${vota.procedure.name}")
	String procedureNames;

	@Value("${vota.procedure.chassisNumber}")
	String chassisNoQuery;

	public Connection getDbConnection(String url, String DriverName) {

		try {
			/**
			 * Instance for driver class initialization.
			 */
			Class.forName(DriverName);

			/**
			 * Create the connection.
			 */
			Connection connection = DriverManager.getConnection(url);
			LOGGER.info("Connection Established: " + connection);
			/**
			 * Return connection if connection is established.
			 */
			return connection;

		} catch (Exception exception) {
			LOGGER.info("Exception occur",exception.getMessage());
			exception.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to insert the data in the table vecv_log_tcp.current_log.
	 * 
	 * @param connection : Here pass the database connection.
	 * @param builder : Here pass the query to insert the data in table.
	 * @return True or False either data inserted or not.
	 */
	public boolean insertStatus(Connection connection, StringBuilder builder) {

		try {

			String Updated_query = builder.toString() + " ;";
			LOGGER.info("Query to be executed : " + Updated_query);
			/**
			 * Create object of Statement class and execute the query.
			 */
			Statement statement = connection.createStatement();
			statement.executeUpdate(Updated_query.toString());

			/**
			 * Close the connection.
			 */
			//	connection.close();

			/**
			 * Return true if executed successfully.
			 */
			return true;

		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is used to get imei numbers present in current_log table.
	 * @param connection : Here pass the database connection.
	 * @return Set of imei numbers present in database.
	 */
	public Set<String> logData(Connection connection){

		Set<String> imeiNumber = new HashSet<>();
		ResultSet resultSet;
		try{
			LOGGER.info("Query to be executed : " + selectQuery);
			/**
			 * Create object of Statement class and execute the query.
			 */
			Statement statement = connection.createStatement();
			statement.executeQuery(selectQuery.toString());

			/**
			 * Retrieve imei numbers from the resultset.
			 */
			resultSet = statement.getResultSet();
			while (resultSet.next()) {
				String imeiNum = resultSet.getString("imei_number");
				imeiNumber.add(imeiNum);
			}

			return imeiNumber;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return imeiNumber;
		}
	}


	/**
	 * This method is to truncate data from the temporary table,
	 * after data is updated into the database.
	 * 
	 * @param connection : Here pass database connection.
	 * @return The row count for SQL Database tables.
	 */
	public int truncateTable(Connection connection) {

		try {
			// Create the object of class Statement and Execute SQL query.
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(dropTmpTableQuery);
			LOGGER.info("Result After Truncate Temp Table:- " + result);

			return result;
		} catch (Exception exception) {
			LOGGER.info("Exception occur",exception.getMessage());
			exception.printStackTrace();
			return 0;
		}
	}

	/**
	 * This method is used for insert the data in temporary and current table.
	 * The updated data query is also execute which check if data already exist
	 * in current table, it will update the data else insert the data..
	 * 
	 * @param connection
	 *            , Here pass database connection.
	 * @param builder
	 *            , Here pass the data string which need to insert in
	 *            table. @return, This method returns boolean values.
	 */
	public boolean insertTempData(Connection connection, StringBuilder builder) throws SQLException {

		try {
			ResultSet resultSet;
			// Create the object of class Statement
			Statement statement = connection.createStatement();

			// Query to create the temporary table.
			LOGGER.info("Query to be executed : " + createTempTable);
			int created = statement.executeUpdate(createTempTable.toString());
			if(created == 1 || created == 0){
				LOGGER.info("Temp Table created.");
			}

			String insert_temp=builder.toString() + ";";
			LOGGER.info("Query is : "+insert_temp.toString());
			statement.executeUpdate(insert_temp.toString());

			LOGGER.info("Query to be executed : " + selectTmpTableQuery);
			statement.executeQuery(selectTmpTableQuery);
			resultSet = statement.getResultSet();

			if(!resultSet.next()){
				//logger.info("No data available to insert in temp history table");
			}else{
				LOGGER.info("Query to be executed : " + updateCurrentLogQuery);
				statement.executeUpdate(updateCurrentLogQuery);
			}

			// Call the method to truncate the table.
			truncateTable(connection);

			// return true if executed successfully
			return true;

		} catch (Exception exception) {

			LOGGER.info("Exception occur",exception.getMessage());
			exception.printStackTrace();
			// return false if not executed successfully
			return false;
		}
	}

	/**
	 * This method is used for calling stored procedures.
	 * @return True or False if procedure is called or not.
	 */
	public boolean callProcedure(Connection connection) {

		/**
		 * Create object of type CallableStatement which maintain the call
		 * connection with storedProcedure
		 */
		CallableStatement statement = null;

		/**
		 * Here split the procedure using "," and store them in array of type
		 * string.
		 */
		String[] procedureArray = procedureNames.split(",");
		// Process for each procedure.
		try {
			for (int i = 0; i < procedureArray.length; i++) {
				String storedProcedure = ("select * from " + procedureArray[i]);
				/*
				 * initialize the object and execute the calling of procedure.
				 */
				statement = connection.prepareCall(storedProcedure);
				boolean procCalled = statement.execute();
				if(procCalled){
					LOGGER.info("Procedure called :-" + procedureArray[i]);
				}
			}
			connection.close();
			return true;
		} catch (Exception exception) {
			LOGGER.info("Exception occur in calling procedures",exception.getMessage());
			exception.printStackTrace();
			return false;
		}
	}

	public Set<String> getChassis(Connection connection) {

		Set<String> chasssisNumber = new HashSet<>();

		ResultSet resultSet = null;

		try {
			String chassisProcedure = ("select * from " + chassisNoQuery);

			Statement statement = connection.createStatement();
			statement.executeQuery(chassisProcedure);

			resultSet = statement.getResultSet();
			while (resultSet.next()) {
				String chassis = resultSet.getString("Device_imei");
				chasssisNumber.add(chassis);
			}
			return chasssisNumber;
		}
		catch(Exception exception){
			exception.printStackTrace();
			return chasssisNumber;
		}
	}
}