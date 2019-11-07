package com.changyu.foryou.tools;

import java.io.File;
import java.io.IOException;

import com.changyu.foryou.model.LocationDTO;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class EtifUtil {

	public static LocationDTO getLocation(File imgFile) {
		LocationDTO locationDTO = new LocationDTO();
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(imgFile);
			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
			
			GeoLocation geoLocation = gpsDirectory.getGeoLocation();
			locationDTO.setLat(geoLocation.getLatitude() + "");
			locationDTO.setLng(geoLocation.getLongitude() + "");
		} catch (JpegProcessingException e) {
		} catch (IOException e) {
		}
		
		return locationDTO;
	}
	
}
