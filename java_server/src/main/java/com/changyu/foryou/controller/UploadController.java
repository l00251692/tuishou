package com.changyu.foryou.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.AddressDTO;
import com.changyu.foryou.tools.EtifUtil;
import com.changyu.foryou.tools.QQMapUtil;

@Controller
@RequestMapping("/upload")
public class UploadController {
	
	@Autowired
	QQMapUtil qqMapUtil;

	@RequestMapping("/getImgEtifInfo")
    public @ResponseBody Map<String,Object> getImgEtifInfo(@RequestParam Integer id, @RequestParam String url) {
		JSONObject rtn = new JSONObject();
		File imgFile = getFileFromQiNiu(url);
		
		AddressDTO addressDTO = qqMapUtil.geocoder(EtifUtil.getLocation(imgFile));
		rtn.put("address_info", JSONObject.toJSON(addressDTO));
		rtn.put("photo_time", EtifUtil.getPhotoTime(imgFile));
		
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
	}

	private File getFileFromQiNiu(String url) {
		// TODO Auto-generated method stub
		return null;
	}
}
