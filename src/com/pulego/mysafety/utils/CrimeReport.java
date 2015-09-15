package com.pulego.mysafety.utils;

public class CrimeReport {

	public String uid;
	public String title;
	public String datetime;
	public String description;
	public String status;
	public String lat,lot;

	public CrimeReport(String _uid,String _title,String _datetime, String _description, String _status,String _lat,String _lot) {
		
		uid = _uid;
		title = _title;
		datetime = _datetime;
		description = _description;
		status = _status;
		lat = _lat;
		lot = _lot;
		
	}

	public String getUId() {
		return uid;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDateTime()
	{
		return datetime;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getLat()
	{
		return lat;
	}
	
	public String getLot()
	{
		return lot;
	}
}
