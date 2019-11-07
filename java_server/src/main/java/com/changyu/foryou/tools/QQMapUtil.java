package com.changyu.foryou.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.AddressDTO;
import com.changyu.foryou.model.LocationDTO;

@Component
public class QQMapUtil {
	
	@Autowired
	HttpClientUtils httpClientUtils;
	
	private static final String GEO_CODER_URL = "https://apis.map.qq.com/ws/geocoder/v1/";

	/**
	  * 请求和返回详见https://lbs.qq.com/webservice_v1/guide-gcoder.html
	 * @param lat
	 * @param lng
	 * @param getPoi   是否返回周边POI列表：1.返回；0不返回(默认)
	 */
	public JSONObject geocoder(LocationDTO locationDTO, String getPoi) {
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("location", locationDTO.toString());
		querys.put("key", Constants.QQMAPKEY);
		querys.put("get_poi", getPoi);
		
		JSONObject jo = null;
		try {
			HttpResponse httpResponse = httpClientUtils.doGet(GEO_CODER_URL, null, null, Collections.EMPTY_MAP, querys);
			HttpEntity httpEntity = httpResponse.getEntity();
			String respContent = EntityUtils.toString(httpEntity, "UTF-8");
			jo = JSONObject.parseObject(respContent);
			
		} catch (Exception e) {
			
		}
		return jo;
	}
	
	public AddressDTO geocoder(LocationDTO locationDTO) {
		JSONObject jo = geocoder(locationDTO, "0");
		
		if (jo.containsKey("status") && !"0".equals(jo.getString("status"))) {
			return null;
		}
		
		JSONObject result = jo.getJSONObject("result");
		JSONObject addressComponent = result.getJSONObject("address_component");
		JSONObject adInfo = result.getJSONObject("ad_info");
		
		
		AddressDTO addressDTO = new AddressDTO();
		addressDTO.setAddress(result.getString("address"));
		addressDTO.setNation(addressComponent.getString("nation"));
		addressDTO.setProvince(addressComponent.getString("province"));
		addressDTO.setCity(addressComponent.getString("city"));
		addressDTO.setDistrict(addressComponent.getString("district"));
		addressDTO.setStreet(addressComponent.getString("street"));
		addressDTO.setStreetNumber(addressComponent.getString("street_number"));
		addressDTO.setNationCode(adInfo.getString("nation_code"));
		addressDTO.setAdcode(adInfo.getString("adcode"));
		addressDTO.setCityCode(adInfo.getString("city_code"));
		addressDTO.setName(adInfo.getString("name"));
		
		return addressDTO;
	}
}
