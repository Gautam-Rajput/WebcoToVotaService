/**
 * This package contain the class which is used to get the data from API calling and implement the threading process to collect the data.
 *  Data is insert in the table after completing all the process.
 */
package com.webco.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.webco.Model.DataSet_Bean;
import com.webco.Model.FinalResponse;

@Service
public class ProcessData {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessData.class);

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	VotaDBLoader votaDBLoader;
	
	@Value("${vota.source.db.url}")
	String connectionUrl;

	@Value("${vota.source.db.driver.name}")
	String driverName;

	@Value("${vota.insert.current.log.query}")
	String currentLogQuery;

	@Value("${vota.insert.tmp.table.query}")
	String tmpTableQuery;

	@Value("${vota.cloudant.api}")
	String cloudantapi;

	@Value("${cloudant.username}")
	String username;

	@Value("${cloudant.password}")
	String password;

	StringBuilder insertBuilder = new StringBuilder();
	StringBuilder updateBuilder = new StringBuilder();
	Connection connection;
	public void dataProcessing()  {
		try {
			System.out.println("currentLogQuery is : " +currentLogQuery);
			insertBuilder.append(currentLogQuery + " ");

			/**
			 * Append the SQL query to insert the data in temporary table.
			 */
			System.out.println("tempTableQuery is : " + tmpTableQuery);
			updateBuilder.append(tmpTableQuery + " ");
			connection = votaDBLoader.getDbConnection(connectionUrl, driverName);
			if (connection != null){

				Set<String> imeiData =  new HashSet<String>();
				imeiData =  votaDBLoader.logData(connection);
				LOGGER.info("IMEI numbers in DataBase : " + imeiData);
				LOGGER.info("Size of ImeiNumber set is : "+imeiData.size());

				Set<String> chassisNo = new HashSet<String>();
				chassisNo = votaDBLoader.getChassis(connection);
				LOGGER.info("chassis number in database :" + chassisNo);
				LOGGER.info("Size of chassisNumber set is : "+chassisNo.size());

				List<String> imeiFound = new ArrayList<String>();
				LOGGER.info("Cloudent API calling");
				for(String chassis : chassisNo) {
					RestTemplate restTemplate = new RestTemplate();
					String cloudAPI = cloudantapi;
					
					cloudAPI=cloudAPI.replace("#devId", chassis);
					restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
					FinalResponse  finalRes = restTemplate.getForObject(cloudAPI, FinalResponse.class);

					if(finalRes.getRows().size()>0) {
						imeiFound.add(chassis);
						String[] values = finalRes.getRows().get(0).getValue().getValue().split(",");

						DataSet_Bean response = new DataSet_Bean();
						response.setImei(values[0]);
						response.setLatitude(values[2]);
						response.setLongitude(values[3]);
						response.setIgnition("off");
						response.setSpeed("0.0 km/h");
						response.setLastHeardDateTime(finalRes.getRows().get(0).getValue().geteDateTime());

						if (imeiData.contains(response.getImei())){
							updateBuilder.append(response + ",");
						}
						else{
							insertBuilder.append(response + ",");
						}
					}
				}
				LOGGER.info("Cloudent API called");
				LOGGER.info("Number of records found for imei numbers in cloudant DB : "+imeiFound.size());
				LOGGER.info("Found imei numbers in cloudant DB are : "+imeiFound);
				if(insertBuilder.toString().endsWith(",")){
					insertBuilder.deleteCharAt(insertBuilder.lastIndexOf(","));
					Boolean checkInsert = votaDBLoader.insertStatus(connection, insertBuilder);
					if (checkInsert) {

						LOGGER.info("Data inserted in table successfully ");
					} else {
						LOGGER.info("Fail to insert data in table");
					}
				}
				if(updateBuilder.toString().endsWith(",")){
					updateBuilder.deleteCharAt(updateBuilder.lastIndexOf(","));

					/**
					 * Call the method of class VotaDBLoader which is used to insert the
					 * data in the database table and return boolean value.
					 */
					Boolean checkInsert = votaDBLoader.insertTempData(connection, updateBuilder);
					if (checkInsert) {

						LOGGER.info("Data updated in table successfully ");
					} else {
						LOGGER.info("Fail to update data in table");
					}
				}
				boolean procCalling = votaDBLoader.callProcedure(connection);
				if(procCalling){
					LOGGER.info("Proceuderes Called Successfully.");
				}
			} else {
				LOGGER.info("Database connection is not established.");
			}

			connection.close();
		} catch (Exception exception) {
			LOGGER.info("Exception Occurred",exception.getMessage());
			exception.printStackTrace();
		}
	}
}