package com.changyu.foryou.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.EtifUtil;

@Controller
@RequestMapping("/upload")
public class UploadController {
	
	private static final Logger logger = Logger.getLogger(UploadController.class);
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	EtifUtil etifUtil;

	@RequestMapping("/getImgEtifInfo")
    public @ResponseBody Map<String,Object> getImgEtifInfo(@RequestParam String project_id, @RequestParam String url) {
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", etifUtil.getEtifInfoFromQiNiu(url));				
		return data;
	}

}
