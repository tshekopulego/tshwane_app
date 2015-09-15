package com.pulego.mysafety.utils;


public class AboutType {
	
	public String _name;

	public String _version;

	public AboutType(String name, String version) {
		_name = name;
		_version = version;
	}

	@Override
	public String toString() {
		return _name;
	}

	public String getName() {
		return _name;
	}

	public String getVersion() {
		return _version;
	}
	
}
