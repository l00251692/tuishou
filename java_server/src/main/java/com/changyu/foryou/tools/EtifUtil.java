package com.changyu.foryou.tools;

import java.io.File;
import java.io.IOException;

import com.changyu.foryou.model.LocationDTO;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
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

	public static Object getPhotoTime(File imgFile) {
		String originalTime = null;
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(imgFile);
			ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
	        originalTime = exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			
		} catch (JpegProcessingException e) {
		} catch (IOException e) {
		}
		return originalTime;
	}
	
}
