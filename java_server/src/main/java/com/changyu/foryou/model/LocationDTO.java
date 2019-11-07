package com.changyu.foryou.model;

public class LocationDTO {

	String lat;
	
	String lng;

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return lat + "," + lng;
	}
	
	
	
}
