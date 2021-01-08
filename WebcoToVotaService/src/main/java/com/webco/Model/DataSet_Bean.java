/**
 *  This package is contain the class which is used to define the domain class(Bean class) to get and set all the variables values. 
 */
package com.webco.Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *  This class is used to define the domain class(Bean class) to get and set all the variables values and return all the values in method.. 
 */
public class DataSet_Bean {

	private String vehicleno;
	private String distance;
	private String vehiclestatus;
	private String speed;
	private String direction;
	private String imei;
	private String ignition;
	private String latlng;
	private String lastHeardDateTime;
	String changedDate = "0000-00-00 00:00:00";
	private String latitude;
	private String longitude;
	private String logDate = "0000-00-00 00:00:00";

	
	public String getVehicleno() {
		return vehicleno;
	}

	public void setVehicleno(String vehicleno) {
		this.vehicleno = vehicleno;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getVehiclestatus() {
		return vehiclestatus;
	}

	public void setVehiclestatus(String vehiclestatus) {
		this.vehiclestatus = vehiclestatus;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getIgnition() {
		return ignition;
	}

	public void setIgnition(String ignition) {
		this.ignition = ignition;
	}

	public String getLatlng() {
		return latlng;
	}

	public void setLatlng(String latlng) {
		this.latlng = latlng;
	}

	public String getLastHeardDateTime() {
		return lastHeardDateTime;
	}

	public void setLastHeardDateTime(String lastHeardDateTime) {
		this.lastHeardDateTime = lastHeardDateTime;
	}

	public String getChangedDate() {
		return changedDate;
	}
	public void setChangedDate(String lastHeardDateTime) {
		changedDate = lastHeardDateTime;
	}

	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latlng) {
		this.latitude = latlng;
	}

	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String latlng) {
		this.longitude = latlng;
	}



	/**
	 * This method is used to return all the variables values.
	 */
	@Override
	public String toString() {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			changedDate = sdf.format(new Date());
						
			DateFormat formatterIST = new SimpleDateFormat("yyyyMMddHHmmss");
			formatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // better than using IST
			Date date = formatterIST.parse(lastHeardDateTime);

			DateFormat formatterUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
			logDate = formatterUTC.format(date);
		//	logDate = changedDate;
			//System.out.println("Log Date :     " + logDate);
			

		} catch(Exception e){
			e.printStackTrace();
		}
		
//		latitude = latlng.split(",")[0];
//		longitude = latlng.split(",")[1];
		
		// Here return all the variable values.
		return "('" + imei + "', '" + latitude + "', '" + longitude + "', '" + ignition
				+ "', '" + speed + "', '" + logDate + "', '" + changedDate +"')";
		
	}
}