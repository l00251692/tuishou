package com.changyu.foryou.tools;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.AddressDTO;
import com.changyu.foryou.model.LocationDTO;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

@Component
public class EtifUtil {
	
	private static final Logger logger = Logger.getLogger(EtifUtil.class);
	
	@Autowired
	QQMapUtil qqMapUtil;
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	private static final String ETIF_SUFFIX = "?exif";
	
	private static final String ORIGINAL_TIME = "original_time";
	
	private static final String ADDRESS_INFO = "address_info";

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
	
	public JSONObject getEtifInfoFromQiNiu(String url) {
		JSONObject etifInfo = new JSONObject();
		JSONObject jo = httpClientUtils.doGet(url + ETIF_SUFFIX);
		if (null == jo) {
			return etifInfo;
		}
		
		if (jo.containsKey("DateTimeOriginal")) {
			// 2016:03:31 13:04:53
			etifInfo.put(ORIGINAL_TIME, jo.getJSONObject("DateTimeOriginal").getString("val"));
		}
		
		if (jo.containsKey("GPSLatitude")) {
			LocationDTO locationDTO = new LocationDTO();
			locationDTO.setLat(parseGPS(jo.getJSONObject("GPSLatitude").getString("val")).toString());
			locationDTO.setLng(parseGPS(jo.getJSONObject("GPSLongitude").getString("val")).toString());
			
			AddressDTO addressDTO = qqMapUtil.geocoder(locationDTO);
			etifInfo.put(ADDRESS_INFO, JSONObject.toJSON(addressDTO));
		}
		return etifInfo;
	}
	
	public Double parseGPS(String degreesMinutesSeconds) {
		String[] arr = degreesMinutesSeconds.split(",");
		if (arr.length != 3) {
			return null;
		}
		
		double decimal = Double.valueOf(arr[0].trim()) + Double.valueOf(arr[1].trim()) / 60.0d + Double.valueOf(arr[1].trim()) / 3600.0d;
		if (Double.isNaN(decimal))
            return null;
		return decimal;
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
