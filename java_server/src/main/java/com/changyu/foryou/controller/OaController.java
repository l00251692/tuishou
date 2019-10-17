package com.changyu.foryou.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Employee;
import com.changyu.foryou.model.Hospital;
import com.changyu.foryou.service.EmployeeService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.Md5;

@Controller
@RequestMapping("/oa")
public class OaController {
	
	@Autowired
	private EmployeeService employeeService;
	
	private static final Logger logger = Logger.getLogger(OaController.class);
	
	/**
	 * 用户登陆
	 * @param phone
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/toLogin")
	public @ResponseBody
	Map<String, Object> toLogin(@RequestParam String phone,@RequestParam String password,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();


		if (phone!=null&&password!=null&&!phone.trim().equals("") && !password.trim().equals("")) {
			Employee employee = employeeService.checkLogin(phone);
			if (employee != null) {
				if (employee.getPassword().equals(Md5.GetMD5Code(password))) {

					map.put(Constants.STATUS, Constants.SUCCESS);
					map.put(Constants.MESSAGE, "登陆成功");
					map.put("type", employee.getType());
					HttpSession session= request.getSession();
					session.setAttribute("type", employee.getType());
					session.setAttribute("phone", employee.getPhone());
					
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("phone",phone);
					paramMap.put("lastLoginDate",new Date());
					
					employeeService.updateLastLoginTime(paramMap);
				} else {
					map.put(Constants.STATUS, Constants.FAILURE);
					map.put(Constants.MESSAGE, "账号或密码错误，请检查后输入");
				}
			} else {
				map.put(Constants.STATUS, Constants.FAILURE);
				map.put(Constants.MESSAGE, "账号或密码错误，请检查后输入");
			}
		}

		return map;
	}
	
	/**
     * 添加账号
     *
     * @param campusName
     * @param campusName
     * @param campusAdminName
     * @return
     */
    @RequestMapping("addEmployee")
    public @ResponseBody
    Map<String, Object> addEmployee(@RequestParam int usertype,@RequestParam String type, @RequestParam String phone, @RequestParam String password) {
        Map<String, Object> responseMap = new HashMap<>();
        
        if(usertype != 0){
        	responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "您没有操作权限");
            return responseMap;
        }
        
        Map<String, Object> paramMap = new HashMap<>();
   
        if(type.equals("系统管理员")){
        	paramMap.put("type", 0);
        	paramMap.put("authority",type);
        }
        else if(type.equals("普通用户")){
        	paramMap.put("type", 1);
        	paramMap.put("authority",type);
        }
        paramMap.put("phone", phone);
        paramMap.put("password", Md5.GetMD5Code(password));

        int flag = employeeService.addEmployee(paramMap);

        if (flag != 0 && flag != -1) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "添加账号成功，请及时将账号分派给相应人员并提醒他/她修改密码");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "添加账号失败");
            logger.error("添加账号失败，账号名称:" + phone);
        }
        return responseMap;
    }
    
    /**
     * 修改账号信息
     *
     * @param campusId
     * @param campusAdminName
     * @return
     */
    @RequestMapping("/updateEmployee")
    public @ResponseBody
    Map<String, Object> updateEmployee(@RequestParam int usertype,@RequestParam String type, @RequestParam String phone, @RequestParam String password) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        if(usertype != 0){
        	responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "您没有操作权限");
            return responseMap;
        }

        if(type.equals("系统管理员")){
        	paramMap.put("type", 0);
        	paramMap.put("authority",type);
        }
        else if(type.equals("普通用户")){
        	paramMap.put("type", 1);
        	paramMap.put("authority",type);
        }
        paramMap.put("phone", phone);
        paramMap.put("password", Md5.GetMD5Code(password));

        int result = employeeService.updateEmployee(paramMap);

        if (result == 0 || result == -1) {
            //没更新成功
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "修改账号失败！");
        } else {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "修改账号成功！");
        }
        
        return responseMap;
    }
    
    /**
     * 删除账号
     *
     * @param campusId
     * @param campusAdminName
     * @return
     */
    @RequestMapping("/deleteEmployee")
    public @ResponseBody
    Map<String, Object> deleteEmployee(@RequestParam int usertype,@RequestParam String phone) {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        if(usertype != 0){
        	responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "您没有操作权限");
            return responseMap;
        }

        paramMap.put("phone", phone);

        int result = employeeService.delEmployee(paramMap);
        if (result > 0) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "删除账号成功");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "删除账号失败");
        }
        return responseMap;
    }
    
    /**
     * 返回所有账号
     *
     * @return
     */
    @RequestMapping("getAllEmployee")
    public @ResponseBody JSONArray getAllEmployee() {
    	
    	JSONArray array = new JSONArray();
    	Map<String, Object> paramMap = new HashMap<String, Object>();

        
        List<Employee> list = employeeService.getAllEmployee(paramMap);
        DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
  
        for(Employee employee : list)
        {
        	JSONObject obj = new JSONObject();
        	obj.put("phone", employee.getPhone());
        	obj.put("authority", employee.getAuthority());
        	if(employee.getLastLoginDate() == null)
        	{
        		obj.put("lastLoginTime", ""); 
        	}
        	else
        	{
        		obj.put("lastLoginTime", formattmp.format(employee.getLastLoginDate())); 
        	}
        	      	
        	array.add(obj);
        }
        return array;
    }
    
    /**
	 * 用户更改密码(用老密码更改）
	 * @param phone
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@RequestMapping("/changePassword")
	public @ResponseBody Map<String,Object> changePassword(@RequestParam String phone
			,@RequestParam String oldPassword,@RequestParam String newPassword)
			{
				Map<String, Object> map=new HashMap<String, Object>();
				try {
					Map<String, Object> paramMap=new HashMap<String, Object>();
					paramMap.put("phone",phone);
					String passwordMd5=Md5.GetMD5Code(oldPassword);
					paramMap.put("password",passwordMd5);
					List<Employee> users=employeeService.selectByPhoneAndPassword(paramMap);
					if(users.size()==0)
					{
						map.put(Constants.STATUS, Constants.FAILURE);
						map.put(Constants.MESSAGE, "更改失败，原密码错误");
					}
					else
					{
						Map<String, Object> paramMap2=new HashMap<String, Object>();
						paramMap2.put("phone",phone);
						paramMap2.put("password",Md5.GetMD5Code(newPassword));
						employeeService.updatePassword(paramMap2);  //对密码做md5加密
						
						map.put(Constants.STATUS, Constants.SUCCESS);
						map.put(Constants.MESSAGE, "修改密码成功");
					}	
					
				} catch (Exception e) {
					e.printStackTrace();	
					map.put(Constants.STATUS, Constants.FAILURE);
					map.put(Constants.MESSAGE, "修改密码失败");					
				}
				
				
				return map;	
	}
	
	/**
     * 返回所有医院列表
     *
     * @return
     */
    @RequestMapping("getAllHospital")
    public @ResponseBody JSONArray getAllHospital() {
    	
    	JSONArray array = new JSONArray();
    	Map<String, Object> paramMap = new HashMap<String, Object>();
  
        List<Hospital> list = employeeService.getAllHospital(paramMap); 
  
        for(Hospital hospital : list)
        {
        	JSONObject obj = new JSONObject();
        	obj.put("province", hospital.getProvince());
        	obj.put("hospital", hospital.getHospital());
        	obj.put("address", hospital.getAddress());
        	      	
        	array.add(obj);
        }
        return array;
    }
    
    /**
     * 添加医院
     *
     * @param province
     * @param hospital
     * @param address
     * @return
     */
    @RequestMapping("addHospital")
    public @ResponseBody
    Map<String, Object> addHospital(@RequestParam String province, @RequestParam String hospital, @RequestParam String address) {
        Map<String, Object> responseMap = new HashMap<>();
       
        
        Map<String, Object> paramMap = new HashMap<>();
   
        paramMap.put("province", province);
        paramMap.put("hospital", hospital);
        paramMap.put("address", address);

        int flag = employeeService.addHospital(paramMap);

        if (flag != 0 && flag != -1) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "添加医院成功");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "添加医院失败");
            logger.error("添加医院失败，医院名称:" + hospital);
        }
        return responseMap;
    }
    
    /**
     * 添加医院
     *
     * @param province
     * @param hospital
     * @param address
     * @return
     */
    @RequestMapping("deleteHospital")
    public @ResponseBody
    Map<String, Object> deleteHospital(@RequestParam String province, @RequestParam String hospital, @RequestParam String address) {
        Map<String, Object> responseMap = new HashMap<>();
       
        
        Map<String, Object> paramMap = new HashMap<>();
   
        paramMap.put("province", province);
        paramMap.put("hospital", hospital);
        paramMap.put("address", address);

        int flag = employeeService.delHospital(paramMap);

        if (flag != 0 && flag != -1) {
            responseMap.put(Constants.STATUS, Constants.SUCCESS);
            responseMap.put(Constants.MESSAGE, "删除医院成功");
        } else {
            responseMap.put(Constants.STATUS, Constants.FAILURE);
            responseMap.put(Constants.MESSAGE, "删除医院失败");
            logger.error("删除医院失败，医院名称:" + hospital);
        }
        return responseMap;
    }
    
    
}
