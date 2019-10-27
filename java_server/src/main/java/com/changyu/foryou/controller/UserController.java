package com.changyu.foryou.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.model.WithDraw;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.AesCbcUtil;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.HttpRequest;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	private static final Logger logger = Logger.getLogger(UserController.class);
	
	/**
	 * 用户登陆
	 * @param phone
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/toLoginWx")
	public @ResponseBody
	Map<String, Object> toLoginWx(@RequestParam String wx_code,@RequestParam String encryptedData,@RequestParam String iv) {
		Map<String, Object> map = new HashMap<String, Object>();
			
		//登录凭证不能为空 
		if (wx_code == null || wx_code.length() == 0) 
		{ 
			map.put("State", "Fail"); 
			map.put("info", "授权凭证错误"); 
			return map; 	
		} 

		//小程序唯一标识  (在微信小程序管理后台获取) 
		String wxspAppid = Constants.appId;
		//小程序的 app secret (在微信小程序管理后台获取) 
		String wxspSecret = Constants.apiKey;
	    //授权（必填） 
		String grant_type = "authorization_code"; 
	
	    
		//////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openid //////////////// 
		//请求参数 
		String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + wx_code + "&grant_type=" + grant_type; 
		//发送请求 
		String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params); 
		//解析相应内容（转换成json对象） 
		//JSONObject json = JSONObject.fromObject(sr); 
		JSONObject json = JSONObject.parseObject(sr);
		
		//获取会话密钥（session_key）
		String session_key = "";
		if(json.get("session_key") != null){
			session_key = json.get("session_key").toString(); 
		}
		else
		{
			map.put("State", "Fail"); 
			map.put("info", "获取信息失败"); 
			return map; 
		}
		
	
		//////////////// 2、对encryptedData加密数据进行AES解密 //////////////// 
		try { 
		  
		  String result = AesCbcUtil.decrypt(encryptedData, session_key, iv, "UTF-8");
		  if (null != result && result.length() > 0) 
		  { 
		
			JSONObject userInfoJSON = JSONObject.parseObject(result); 
			
			JSONObject userInfo = new JSONObject(); 
			userInfo.put("openId", userInfoJSON.get("openId")); 
			userInfo.put("nickName", userInfoJSON.get("nickName")); 
			userInfo.put("gender", userInfoJSON.get("gender")); 
			userInfo.put("city", userInfoJSON.get("city")); 
			userInfo.put("province", userInfoJSON.get("province")); 
			userInfo.put("country", userInfoJSON.get("country")); 
			userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl")); 
			userInfo.put("user_id", userInfoJSON.get("openId")); 
			userInfo.put("user_token", userInfoJSON.get("unionId"));
			userInfo.put("session_key", session_key);


			try {
				
				Users users = userService.checkLogin(userInfoJSON.get("openId").toString());
				if (users != null)
				{
					userInfo.put("phone", users.getPhone());
					userInfo.put("balance", users.getBalance());
					
					users.setImgUrl(userInfoJSON.get("avatarUrl").toString());
					users.setNickname(userInfoJSON.get("nickName").toString());
					users.setLastLoginDate(new Date());
					users.setSex(new Short(userInfoJSON.get("gender").toString()));
					userService.updateUserInfo(users);
				}
				else
				{
					Users users2 = new Users();
					users2.setUserId(userInfoJSON.get("openId").toString());
					users2.setLastLoginDate(new Date());
					users2.setNickname(userInfoJSON.get("nickName").toString());
					users2.setSex(new Short(userInfoJSON.get("gender").toString()));
					users2.setType((short)1);
					users2.setBalance(0.0f);
					users2.setImgUrl(userInfoJSON.get("avatarUrl").toString());
			
					userService.addUsers(users2);
				}				
			} catch (Exception e) {
				logger.error("[login fail]userId=" + userInfoJSON.get("openId"));
			}
			
			JSONObject loginInfo = new JSONObject();
			loginInfo.put("haslogin", "1");
			loginInfo.put("userInfo", userInfo); 
			
			map.put("State", "Success"); 
			map.put("data", loginInfo); 
			return map; 
		  }
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();
			logger.error("[login fail]" + e.getMessage()); 
		} 
		
		map.put("State", "Fail"); 
		map.put("info", "登陆失败"); 
		return map; 	
	
	}


	/**
	 * 获取我的用户总信息
	 * @param phone 用户id
	 * @return
	 */
	@RequestMapping(value="getRegisterInfoWx")
	public @ResponseBody Map<String, Object> getRegisterInfoWx(@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
	
		Users users=userService.selectByUserId(user_id);
		if(users == null)
		{
			map.put("State", "Fail"); 
			map.put("info", "获得我的信息失败"); 
			return map; 
		}
		
		JSONObject obj = new JSONObject();
		obj.put("phone", users.getPhone());
		
		JSONObject location = new JSONObject();
		location.put("province", users.getProvince());
		location.put("name", users.getAddress());
		location.put("city", users.getCity());
		location.put("district", users.getDistrict());
		
		JSONObject weidu = new JSONObject();
		weidu.put("longitude", users.getLongitude());
		weidu.put("latitude", users.getLatitude());
		
		location.put("location", weidu);
		
		obj.put("location", location);
			
		map.put("State", "Success"); 
		map.put("data", obj); 
		return map;
	}
	
	@RequestMapping(value="setProjectFollowStatusWx")
	public @ResponseBody Map<String, Object> setProjectFollowStatusWx(@RequestParam Boolean status, @RequestParam String project_id,@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("followerId",user_id);
		paramMap.put("projectId",project_id);
		
		int flag;
		if(status == true)
		{
			flag =userService.follow(paramMap);
		}
		else
		{
			flag =userService.unfollow(paramMap);
		}
		
			
		map.put("State", "Success"); 
		map.put("data", flag); 
		return map;
	}
	
	@RequestMapping(value="bindPhoneWx")
	public @ResponseBody Map<String, Object> bindPhoneWx(@RequestParam String ency, 
			@RequestParam String iv, @RequestParam String session_key,@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
		
		try { 
			String result = AesCbcUtil.decrypt(ency, session_key, iv, "UTF-8");
			
			if (null != result && result.length() > 0) 
			{
				
				JSONObject resultObject = JSONObject.parseObject(result); 
				
				JSONObject obj = new JSONObject();
				obj.put("phone", resultObject.get("phoneNumber")); 
				
				Map<String, Object> paramMap=new HashMap<String, Object>();
				paramMap.put("userId",user_id);
				paramMap.put("phone",resultObject.get("phoneNumber"));
				
				int flag = userService.bindPhone(paramMap);
				
				map.put("State", "Success"); 
				map.put("data", obj); 
				return map; 
			}
			else
			{
				map.put("State", "Fail"); 
				map.put("info", "手机号数据获取失败"); 
				return map; 
			}
		}
		catch (Exception e) 
		{ 
			e.printStackTrace();
			logger.error("[login fail]" + e.getMessage()); 
		} 
		
		map.put("State", "Fail"); 
		map.put("info", "绑定手机号失败"); 
		return map; 
	}
    
	/**
	 * 获取我的用户总信息
	 * @param phone 用户id
	 * @return
	 */
	@RequestMapping(value="updateLocationWx")
	public @ResponseBody Map<String, Object> updateLocationWx(@RequestParam String user_id, @RequestParam String longitude, 
			@RequestParam String latitude, @RequestParam String province, @RequestParam String city, 
			@RequestParam String district, @RequestParam String name){
		Map<String, Object> map = new HashMap<String, Object>();
	
		Users users=userService.selectByUserId(user_id);
		if(users == null)
		{
			map.put("State", "Fail"); 
			map.put("info", "获得我的信息失败"); 
			return map; 
		}
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userId",user_id);
		paramMap.put("longitude",longitude);
		paramMap.put("latitude",latitude);
		paramMap.put("province",province);
		paramMap.put("city",city);
		paramMap.put("district",district);
		paramMap.put("address",name);
		
		userService.updateLocation(paramMap);
		
			
		map.put("State", "Success"); 
		map.put("data", ""); 
		return map;
	}
	
	
	/**
	 * 获取我的用户总信息
	 * @param phone 用户id
	 * @return
	 */
	@RequestMapping(value="getWithDrawWx")
	public @ResponseBody Map<String, Object> getWithDrawWx(@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
	
		
		Users users=userService.selectByUserId(user_id);
		if(users == null)
		{
			map.put("State", "Fail"); 
			map.put("info", "获得我的信息失败"); 
			return map; 
		}
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("applyUserId",user_id);
			
		List<WithDraw> list = userService.getUserWithDraw(paramMap);
		
		if(list != null && !list.isEmpty())
		{
			map.put("State", "Success"); 
			map.put("data", true); 
		}
		else
		{
			map.put("State", "Success"); 
			map.put("data", "false"); 
		}
		
		return map;
	}
	
	
	@RequestMapping(value="applyWithDrawWx")
	public @ResponseBody Map<String, Object> applyWithDrawWx(@RequestParam String user_id, @RequestParam String balance, @RequestParam String account){
		Map<String, Object> map = new HashMap<String, Object>();
	
		
		Users users=userService.selectByUserId(user_id);
		if(users == null)
		{
			map.put("State", "Fail"); 
			map.put("info", "获得用户信息失败"); 
			return map; 
		}
		
		Calendar calendar=Calendar.getInstance();
		
		String id = String.valueOf(calendar.getTimeInMillis());
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("id",id);
		paramMap.put("applyUserId",user_id);
		paramMap.put("money",balance);
		paramMap.put("applyTime",new Date());
		paramMap.put("status",1);
		
		int flag = userService.createWithDraw(paramMap);
		
		if(flag != -1 && flag != 0){
			map.put("State", "Success"); 
			map.put("data", ""); 
		}
		else
		{
			map.put("State", "Fail"); 
			map.put("info", "提交申请失败"); 
		}
			
		return map;
	}
	
	
	@RequestMapping(value="postFeedbackWx")
	public @ResponseBody Map<String, Object> postFeedbackWx(@RequestParam String user_id, @RequestParam String phone, @RequestParam String feedType, @RequestParam String content){
		Map<String, Object> map = new HashMap<String, Object>();
	
		
		Calendar calendar=Calendar.getInstance();
		
		String id = String.valueOf(calendar.getTimeInMillis());
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("id",id);
		paramMap.put("userId",user_id);
		paramMap.put("phone",phone);
		paramMap.put("createTime",new Date());
		paramMap.put("type",feedType);
		paramMap.put("content",content);
		
		int flag = userService.postFeedBack(paramMap);
		
		if(flag != -1 && flag != 0){
			map.put("State", "Success"); 
			map.put("data", ""); 
		}
		else
		{
			map.put("State", "Fail"); 
			map.put("info", "提交申请失败"); 
		}
			
		return map;
	}
	
	
	
}
