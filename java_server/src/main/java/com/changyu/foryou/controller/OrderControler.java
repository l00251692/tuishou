package com.changyu.foryou.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.changyu.foryou.model.Hospital;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.service.EmployeeService;
import com.changyu.foryou.service.OrderService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.SendSms;
import com.changyu.foryou.tools.WordGenerator;
import com.qiniu.util.Auth;

@Controller
@RequestMapping("/order")
public class OrderControler {
	private OrderService orderService;
	private UserService userService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
	
	@Autowired
	public void setUserServce(UserService userService) {
		this.userService = userService;
	}
	
    private static final Logger logger = LoggerFactory.getLogger(OrderControler.class);
    
    @RequestMapping("/getQiniuTokenWx")
    public @ResponseBody Map<String,Object> getQiniuTokenWx()throws Exception{
		Map<String,Object> data = new HashMap<String, Object>();
		
		Auth auth = Auth.create(Constants.QINIU_AK, Constants.QINIU_SK);
		String upToken = auth.uploadToken(Constants.QINIU_BUCKET);
		
		JSONObject rtn = new JSONObject();
        rtn.put("upToken", upToken);
        
		data.put("State", "Success");
		data.put("data", rtn);			
    	return data;	
	}
    
    @RequestMapping("/getHospitalListWx")
    public @ResponseBody Map<String,Object> getHospitalListWx()throws Exception{
		Map<String,Object> data = new HashMap<String, Object>();
		
		JSONArray provinceList = new JSONArray(); 
		JSONArray hospitalList = new JSONArray(); 
        
        List<String> list1 = employeeService.getProvinceList();
        for(String provTmp: list1)
        {
        	JSONObject province = new JSONObject();
        	
        	province.put("name", provTmp);
        	
        	provinceList.add(province);
        	
        	Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("province", provTmp);
        	
        	List<Hospital> list2 = employeeService.getHospitalList(paramMap);
   
        	JSONArray hospitals = new JSONArray();
            for(Hospital hospitalTmp: list2)
            {
            	
            	JSONObject tmp = new JSONObject();
            	tmp.put("name", hospitalTmp.getHospital()+"(" + hospitalTmp.getAddress() + ")");
            	hospitals.add(tmp);
            }
            
            JSONObject obj = new JSONObject();
            obj.put("province", provTmp);
            obj.put("hospitals", hospitals);
            hospitalList.add(obj);
        }
		JSONObject rtn = new JSONObject();
		rtn.put("provinceList", provinceList);
		rtn.put("hospitalList", hospitalList);
						
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;			
	}
	
	
	/**
	 * 生成订单
	 * 
	 * @param phoneId
	 * @param foodId
	 * @param foodCount
	 * @param foodSpecial
	 * @return
	 */
	@RequestMapping("/addOrderWx")
	public @ResponseBody Map<String, Object> addOrderWx(@RequestParam String name,@RequestParam String idcard,  @RequestParam String sex, 
			@RequestParam String hospital,@RequestParam String hospitalArea, @RequestParam String mrNo,@RequestParam String department,
			@RequestParam String doctor, @RequestParam String bedNo, @RequestParam String diseases, @RequestParam String date,@RequestParam String phone,@RequestParam String concatName,
			@RequestParam String concatPhone, @RequestParam String addresstr,@RequestParam String adDetail, @RequestParam String user_id){
		Map<String,Object> map = new HashMap<String, Object>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			
			Date createTime =  new Date();
			paramMap.put("createUser",user_id);
			paramMap.put("name",name);
			paramMap.put("idCard",idcard);
			paramMap.put("sex",sex);
			paramMap.put("hospital",hospital.replaceAll("&#40;", "("));
			paramMap.put("hospitalArea",hospitalArea);
			paramMap.put("mrNo",mrNo);
			paramMap.put("department",department);
			paramMap.put("doctor",doctor);
			paramMap.put("bedNo",bedNo);
			paramMap.put("diseases",diseases);
			paramMap.put("outDate",date);
			paramMap.put("address",addresstr);
			paramMap.put("detail",adDetail);
			paramMap.put("phone",phone);
			paramMap.put("concatName",concatName);
			paramMap.put("concatPhone",concatPhone);
				
			JSONObject addr = JSON.parseObject(addresstr);
			
			paramMap.put("provice",addr.get("province"));
			paramMap.put("city",addr.get("city"));
			paramMap.put("district",addr.get("district"));
			paramMap.put("adrTitle",addr.get("title").toString().replaceAll("&#40;", "("));
			
			Date now = new Date();
			paramMap.put("createTime",now);
			paramMap.put("lastUpdateTime",now);
			paramMap.put("orderStatus",Constants.STATUS_CREATE);
			
			JSONArray records = new JSONArray();
			JSONObject record = new JSONObject();
			record.put("status", Constants.STATUS_CREATE);
			record.put("time", now);
			records.add(record);
			paramMap.put("records",records.toString());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Random random = new Random();  
			String result="";  
			for (int i=0; i<4; i++)  
			{  
			    result+=random.nextInt(10);  
			}  
			String orderId = sdf.format(createTime) + result;
			
			paramMap.put("orderId",orderId);
			
			int flag = orderService.addOrder(paramMap);
			if(flag != 0 && flag != -1)
			{		
				JSONObject obj = new JSONObject();
				obj.put("orderId", orderId);
				
				map.put("State", "Success");
				map.put("data", obj);	

				return map;
			}
			else{
				logger.error("[add order Fail:orderId=]" + orderId);
			}
			
			
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		map.put("State", "Fail");
		map.put("info", "提交订单失败");	

		return map;
	}
	
	@RequestMapping("/uploadOrderIdCardWx")
	public @ResponseBody Map<String, String> uploadOrderIdCardWx(
			@RequestParam String user_id,  @RequestParam String order_id,@RequestParam String front_img,  @RequestParam String back_img
			,@RequestParam String summary_img, @RequestParam String sign_img){
		Map<String,String> result = new HashMap<String, String>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			paramMap.put("createUser",user_id);
			paramMap.put("idCardFront","https://" + front_img);
			paramMap.put("idCardBack","https://" + back_img);
			paramMap.put("outSummary","https://" + summary_img);
			paramMap.put("sign","https://" + sign_img);

			
			int flag = orderService.updateOrderIdCard(paramMap);
			if (flag != -1 && flag != 0)
			{	
				result.put("State", "Success");
				result.put("data", null);	
				return result;
			} 
			else 
			{
				result.put("State", "Fail");
				result.put("info", "上传身份证照片失败");	
				logger.error("[uploadOrderIdCardWx Fail:orderId=]" + order_id);
				return result;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[updateOrderIdCardWx Exceptiom:]" + e.getMessage());
		}
		
		result.put("State", "Fail");
		result.put("info", "更新身份证照片失败");	

		return result;
	}
	
	@RequestMapping("/updateOrderIdCardWx")
	public @ResponseBody Map<String, String> updateOrderIdCardWx(
			@RequestParam String user_id,  @RequestParam String order_id,@RequestParam String front_img,  @RequestParam String back_img){
		Map<String,String> result = new HashMap<String, String>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			paramMap.put("createUser",user_id);
			
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put("State", "Fail");
				result.put("info", "读取订单信息失败");	

				return result;
			}
			
			paramMap.put("idCardFront","https://" + front_img);
			paramMap.put("idCardBack","https://" + back_img);

			
			int flag = orderService.updateOrderIdCard(paramMap);
			if (flag != -1 && flag != 0)
			{	
				paramMap.put("orderStatus",Constants.STATUS_PAYED);
				
				Date now = new Date();
				
				JSONArray recordes = JSON.parseArray(order.getRecords());
				JSONObject record = new JSONObject();
				record.put("status",Constants.STATUS_PAYED);
				record.put("time", now);
				record.put("content", "更新身份证照片");
				recordes.add(record);

				paramMap.put("records",recordes.toString());
				paramMap.put("lastUpdateTime",now);
				
				
				int flag2 = orderService.updateOrderStatus(paramMap);
				if (flag2 != -1 && flag2 != 0)
				{	
					result.put("State", "Success");
					result.put("data", null);	
					return result;
				} 
				else 
				{
					result.put("State", "Fail");
					result.put("info", "更新订单信息失败");	
					logger.error("[updateOrderPayedWx Err]order_id=" + order_id);
					return result;
				}
				
			} 
			else 
			{
				result.put("State", "Fail");
				result.put("info", "更新身份证照片失败");	
				logger.error("[updateOrderIdCardWx Fail:orderId=]" + order_id);
				return result;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[updateOrderIdCardWx Exceptiom:]" + e.getMessage());
		}
		
		result.put("State", "Fail");
		result.put("info", "更新身份证照片失败");	

		return result;
	}
	
	@RequestMapping("/cancelOrderWx")
	public @ResponseBody Map<String, String> cancelOrderWx(
			@RequestParam String user_id,  @RequestParam String order_id){
		Map<String,String> result = new HashMap<String, String>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put("State", "Fail");
				result.put("info", "读取订单信息失败");	

				return result;
			}

			paramMap.put("orderStatus",Constants.STATUS_CANCEL);
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",Constants.STATUS_CANCEL);
			record.put("time", new Date());
			recordes.add(record);

			paramMap.put("records",recordes.toString());
			
			
			int flag = orderService.updateOrderStatus(paramMap);
			if (flag != -1 && flag != 0)
			{	
				result.put("State", "Success");
				result.put("data", null);	
				return result;
			} 
			else 
			{
				result.put("State", "Fail");
				result.put("info", "更新订单信息失败");	
				return result;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		result.put("State", "Fail");
		result.put("info", "取消订单失败");	

		return result;
	}
	
	@RequestMapping("/updateOrderPayedWx")
	public @ResponseBody Map<String, String> updateOrderPayedWx(
			@RequestParam String user_id,  @RequestParam String order_id){
		Map<String,String> result = new HashMap<String, String>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			paramMap.put("createUser",user_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put("State", "Fail");
				result.put("info", "读取订单信息失败");	

				return result;
			}

			paramMap.put("orderStatus",Constants.STATUS_PAYED);
			
			Date now = new Date();
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",Constants.STATUS_PAYED);
			record.put("time", now);
			recordes.add(record);

			paramMap.put("records",recordes.toString());
			paramMap.put("lastUpdateTime",now);
			
			
			int flag = orderService.updateOrderStatus(paramMap);
			if (flag != -1 && flag != 0)
			{	
				result.put("State", "Success");
				result.put("data", null);	
				return result;
			} 
			else 
			{
				result.put("State", "Fail");
				result.put("info", "更新订单失败");	
				logger.error("[updateOrderPayedWx Err]order_id=" + order_id);
				return result;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[updateOrderPayedWx Exception]" + e.getMessage());
		}
		
		result.put("State", "Fail");
		result.put("info", "更新订单失败");	

		return result;
	}
	
	/**
	 * 获取订单具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getMineOrdersWx")
	public @ResponseBody Map<String, Object> getMineOrdersWx(
			@RequestParam String  user_id, @RequestParam Integer page) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("createUser",user_id);
			paramMap.put("limit",5);
			paramMap.put("offset",page*5);
			
			List<Order> list = orderService.getMineOrders(paramMap);
			JSONArray arr = new JSONArray();
			for(Order order: list){
				JSONObject obj = new JSONObject();
				obj.put("order_id", order.getOrderId());
				obj.put("name", order.getName());
				obj.put("status", order.getOrderStatus());
				obj.put("create_time", order.getCreateTime());
				
				if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
				{
					obj.put("deliveryNo", "暂无");
				}
				else
				{
					obj.put("deliveryNo", order.getDeliveryNo());
				}
				
				arr.add(obj);
			}
		
			JSONObject data = new JSONObject();
			data.put("list", arr);
			data.put("count", list.size());
			
			map.put("State", "Success");
			map.put("data", data);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取订单具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getOrderInfoWx")
	public @ResponseBody Map<String, Object> getOrderInfoWx(
			@RequestParam String  user_id, @RequestParam String order_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			
			if (order == null)
			{
				map.put("State", "Fail");
				map.put("data", "查询订单详细信息失败");	
				return map;
			}
		
			JSONObject obj = new JSONObject();
			obj.put("order_id", order.getOrderId());
			obj.put("status", order.getOrderStatus());
			obj.put("create_time", order.getCreateTime());
			obj.put("last_update_time", order.getLastUpdateTime());
			obj.put("name", order.getName());
			obj.put("sex", order.getSex());
			obj.put("idcard", order.getIdCard());
			obj.put("hospital", order.getHospital());
			obj.put("hospitalArea", order.getHospitalArea());
			obj.put("mrNo", order.getMrNo());
			obj.put("department", order.getDepartment());
			obj.put("doctor", order.getDoctor());
			obj.put("bedNo", order.getBedNo());
			obj.put("diseases", order.getDiseases());
			obj.put("date", order.getOutDate());
			obj.put("phone", order.getPhone());
			obj.put("concatName", order.getConcatName());
			obj.put("concatPhone", order.getConcatPhone());
			if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
			{
				obj.put("deliveryNo", "暂无");
			}
			else
			{
				obj.put("deliveryNo", order.getDeliveryNo());
			}
			String temp = order.getProvice() + order.getCity()+order.getDistrict()+order.getAdrTitle();			
			if(order.getDetail() == null || order.getDetail().isEmpty()){
				obj.put("addr", temp);
			}
			else
			{
				obj.put("addr", temp + order.getDetail());
			}
			
			obj.put("front_img", order.getIdCardFront());
			obj.put("back_img", order.getIdCardBack());
			obj.put("summary_img", order.getOutSummary());
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONArray arr = new JSONArray();
			for(int i = recordes.size() -1; i >= 0; i--)
			{
				JSONObject record = new JSONObject();
				short state = recordes.getJSONObject(i).getShort("status");

				DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String timeStr = formattmp.format(recordes.getJSONObject(i).getDate("time"));
				if( state == 0){
					record.put("name", "取消");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("取消时间", timeStr);
					record.put("list", tmp);
				}
				else if(state == 1){
					record.put("name", "创建");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("提交订单", timeStr);
					record.put("list", tmp);
				}
				else if(state == 2){
					record.put("name", "待发货");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					String content = recordes.getJSONObject(i).getString("content");
					if(content != null && !content.isEmpty()){
						tmp.put("更新订单", timeStr);	
						tmp.put("更新原因", content);
					}
					else{
						tmp.put("支付成功", timeStr);	
					}
								
					record.put("list", tmp);
				}
				else if(state == 3){
					record.put("name", "配送");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("快递发货", timeStr);
					tmp.put("快递单号", order.getDeliveryNo());
					record.put("list", tmp);
				}
				else if(state == 4){
					record.put("name", "被打回");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("订单打回", timeStr);
					tmp.put("原因", recordes.getJSONObject(i).getString("content"));
					record.put("list", tmp);
					if(recordes.getJSONObject(i).getString("content").startsWith("身份证不清晰"))
					{
						obj.put("reason", 1);
					}
					else if(recordes.getJSONObject(i).getString("content").startsWith("资料超过50页")){
						obj.put("reason", 2);
					}	
				}
				else if(state == 9){
					record.put("name", "订单预删除");
					record.put("type", state);
					Map<String, Object> tmp = new HashMap<String, Object>();
					tmp.put("订单删除", "系统即将自动删除订单信息");
					record.put("list", tmp);
				}
				
				arr.add(record);
			}
			
			JSONObject data = new JSONObject();
			data.put("info", obj);
			data.put("state", arr);

			map.put("State", "Success");
			map.put("data", data);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	
	/**
     * 申请退款
     * @return
     */
	@RequestMapping("/refundWx")
    public @ResponseBody Map<String, Object> refundWx(@RequestParam String order_id,String user_id,Float fee) {
          Map<String,Object> result = new HashMap<String,Object>();
          
          /*String resultStr = payService.refund(order_id, String.valueOf(fee*100));
          
          System.out.println("调试模式_统一下单接口 返回XML数据：" + result);  
            
          //解析结果
          try {
              Map map =  PayUtil.doXMLParse(resultStr);
              String returnCode = map.get("return_code").toString();
              if(returnCode.equals("SUCCESS")){
                  String resultCode = map.get("result_code").toString();
                  if(resultCode.equals("SUCCESS")){
                      JSONObject node = new JSONObject();
                      node.put("totalFee", fee);
                      result.put("State", "Success");
                      result.put("data", node);	
          			return result;
                  }
                  else
                  {
                      result.put("State", "Fail");
                  }
              }
              else
              {
                  result.put("State", "Fail");
              }
          } 
          catch (Exception e) 
          {
              e.printStackTrace();
              result.put("State", "Fail");
          }*/
          result.put("State", "Success");
          result.put("data", null);	
          return result;
    }
	
	//PC端接口
	/**
	 * 获取某日订单所有订单详情
	 * 
	 * @param date
	 * @return
	 */
	@RequestMapping("/getOrdersByDate")
	@ResponseBody
	public Map<String, Object> getOrdersByDate(String date_start, String date_end, Integer limit, Integer page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  

		try {
			if (date_start.equals("") || date_start.equals("null"))
			{
				if (date_end.equals("") || date_end.equals("null"))
				{
					date_end =formattmp.format(new Date());
				}
				else
				{
					date_end = date_end.replace("年", "-").replace("月", "-").replace("日", "");
				}
				
				date_start = date_end;
				date_end = date_end + " 23:59:59";
			}
			else
			{
				date_start = date_start.replace("年", "-").replace("月", "-").replace("日", "");
				if (date_end.equals("") || date_end.equals("null"))
				{
					date_end = date_start;
				}
				else
				{
					date_end = date_end.replace("年", "-").replace("月", "-").replace("日", "");
				}
				
				date_end = date_end + " 23:59:59";
			}
			
			System.out.println("getOrdersByDate:" + date_start + "," + date_end);
			
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("TimeBegin", date_start);
			paramMap.put("TimeEnd", date_end);

			if (page != null && limit != null) {
				paramMap.put("limit", limit);
				paramMap.put("offset", (page - 1) * limit);
			}

			
			List<Order> lists = orderService.selectOrdersByDate(paramMap);
			
			JSONArray arr = new JSONArray();

			for (Order order : lists)
			{
				JSONObject obj =  new JSONObject();
				
				Users user = userService.selectByUserId(order.getCreateUser());
				if(user != null)
				{
					obj.put("nick_name", user.getNickname());
				}
				obj.put("order_id", order.getOrderId());
				
				short status = order.getOrderStatus();
				if(status == 1){
					obj.put("status", "待支付");
				}
				else if(status == 2){
					obj.put("status", "待发货");
				}
				else if(status == 3){
					obj.put("status", "已发货");
				}
				else if(status == 4){
					obj.put("status", "被打回");
				}
				else{
					obj.put("status", "");
				}
				
				obj.put("create_time", order.getCreateTime());
				obj.put("last_update_time", order.getLastUpdateTime());
				obj.put("name", order.getName());
				obj.put("idcard", order.getIdCard());
				obj.put("sex", order.getSex());
				obj.put("hospital", order.getHospital());
				obj.put("hospitalArea", order.getHospitalArea());
				obj.put("mrNo", order.getMrNo());
				obj.put("department", order.getDepartment());
				obj.put("doctor", order.getDoctor());
				obj.put("bedNo", order.getBedNo());	
				obj.put("diseases", order.getDiseases());
				obj.put("date", order.getOutDate());
				obj.put("phone", order.getPhone());
				obj.put("concatName", order.getConcatName());
				obj.put("concatPhone", order.getConcatPhone());
				if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
				{
					obj.put("deliveryNo", "暂无");
				}
				else
				{
					obj.put("deliveryNo", order.getDeliveryNo());
				}
				String temp = order.getProvice() + order.getCity()+order.getDistrict()+order.getAdrTitle();			
				if(order.getDetail() == null || order.getDetail().isEmpty()){
					obj.put("addr", temp);
				}
				else
				{
					obj.put("addr", temp + order.getDetail());
				}	
				obj.put("front_img", order.getIdCardFront());
				obj.put("back_img", order.getIdCardBack());
				obj.put("summary_img", order.getOutSummary());
				
				arr.add(obj);
				
			}
			
			resultMap.put("counts", arr.size());
			resultMap.put("orderList", JSONArray.parse(JSON.toJSONStringWithDateFormat(arr,
							"yyyy-MM-dd HH:mm:ss")));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[getOrdersByDate Exception:]" + e.getMessage());
		}

		return resultMap;
	}
	
	
	/**
	 * 根据姓名和身份证号查询所有订单详情
	 * 
	 * @param date
	 * @return
	 */
	@RequestMapping("/getOrdersByNameAndIdcard")
	@ResponseBody
	public Map<String, Object> getOrdersByNameAndIdcard(String name, String idcard, Integer limit, Integer page) {
		Map<String, Object> resultMap = new HashMap<String, Object>();


		try{ 
				
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("name", name);
			paramMap.put("idcard", idcard);
			
			List<Order> lists = orderService.selectOrdersByNameIdCard(paramMap);
			
			JSONArray arr = new JSONArray();

			for (Order order : lists)
			{
				JSONObject obj =  new JSONObject();
				
				Users user = userService.selectByUserId(order.getCreateUser());
				if(user != null)
				{
					obj.put("nick_name", user.getNickname());
				}
				obj.put("order_id", order.getOrderId());
				
				short status = order.getOrderStatus();
				if(status == 1){
					obj.put("status", "待支付");
				}
				else if(status == 2){
					obj.put("status", "待发货");
				}
				else if(status == 3){
					obj.put("status", "已发货");
				}
				else if(status == 4){
					obj.put("status", "被打回");
				}
				else{
					obj.put("status", "");
				}
				
				obj.put("create_time", order.getCreateTime());
				obj.put("last_update_time", order.getLastUpdateTime());
				obj.put("name", order.getName());
				obj.put("idcard", order.getIdCard());
				obj.put("sex", order.getSex());
				obj.put("hospital", order.getHospital());
				obj.put("hospitalArea", order.getHospitalArea());
				obj.put("mrNo", order.getMrNo());
				obj.put("department", order.getDepartment());
				obj.put("doctor", order.getDoctor());
				obj.put("bedNo", order.getBedNo());	
				obj.put("diseases", order.getDiseases());
				obj.put("date", order.getOutDate());
				obj.put("phone", order.getPhone());
				obj.put("concatName", order.getConcatName());
				obj.put("concatPhone", order.getConcatPhone());
				if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
				{
					obj.put("deliveryNo", "暂无");
				}
				else
				{
					obj.put("deliveryNo", order.getDeliveryNo());
				}
				String temp = order.getProvice() + order.getCity()+order.getDistrict()+order.getAdrTitle();			
				if(order.getDetail() == null || order.getDetail().isEmpty()){
					obj.put("addr", temp);
				}
				else
				{
					obj.put("addr", temp + order.getDetail());
				}	
				obj.put("front_img", order.getIdCardFront());
				obj.put("back_img", order.getIdCardBack());
				obj.put("summary_img", order.getOutSummary());
				
				arr.add(obj);
				
			}
			
			resultMap.put("counts", arr.size());
			resultMap.put("orderList", JSONArray.parse(JSON.toJSONStringWithDateFormat(arr,
							"yyyy-MM-dd HH:mm:ss")));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[getOrdersByDate Exception:]" + e.getMessage());
		}

		return resultMap;
	}
	
	/**
	 * 获取所有订单详情
	 * 
	 * @param date
	 * @return
	 */
	@RequestMapping("/getOrderList")
	@ResponseBody
	public Map<String, Object> getOrderList(Short status,Integer limit, Integer offset, String search) {
		Map<String, Object> map = new HashMap<String, Object>();
		
        Map<String,Object> paramMap=new HashMap<String,Object>();
        
        paramMap.put("limit", limit);
        paramMap.put("offset", offset);
        paramMap.put("search", search);
        paramMap.put("status", status);
        
		List<Order> lists = orderService.getOrdersByStatus(paramMap);
		JSONArray arr = new JSONArray();
		DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		
		for (Order order: lists)
		{
			JSONObject obj = new JSONObject();
			Users user = userService.selectByUserId(order.getCreateUser());
			if(user != null)
			{
				obj.put("nick_name", user.getNickname());
			}
			
			obj.put("order_id", order.getOrderId());
			obj.put("status", order.getOrderStatus());
			obj.put("create_time", formattmp.format(order.getCreateTime()));
			obj.put("last_update_time", order.getLastUpdateTime());
			obj.put("name", order.getName());
			obj.put("idcard", order.getIdCard());
			obj.put("sex", order.getSex());
			obj.put("hospital", order.getHospital());
			obj.put("hospitalArea", order.getHospitalArea());
			obj.put("mrNo", order.getMrNo());
			obj.put("department", order.getDepartment());
			obj.put("doctor", order.getDoctor());
			obj.put("bedNo", order.getBedNo());	
			obj.put("diseases", order.getDiseases());
			obj.put("date", order.getOutDate());
			obj.put("phone", order.getPhone());
			obj.put("concatName", order.getConcatName());
			obj.put("concatPhone", order.getConcatPhone());
			if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
			{
				obj.put("deliveryNo", "暂无");
			}
			else
			{
				obj.put("deliveryNo", order.getDeliveryNo());
			}
			String temp = order.getProvice() + order.getCity()+order.getDistrict()+order.getAdrTitle();			
			if(order.getDetail() == null || order.getDetail().isEmpty()){
				obj.put("addr", temp);
			}
			else
			{
				obj.put("addr", temp + order.getDetail());
			}	
			obj.put("front_img", order.getIdCardFront());
			obj.put("back_img", order.getIdCardBack());	
			obj.put("summary_img", order.getOutSummary());
			arr.add(obj);	
		}
		

		JSONArray jsonArray = JSONArray.parseArray(JSON
				.toJSONStringWithDateFormat(arr, "yyyy-MM-dd"));

		long totalCount = orderService.getOrdersStatusCount(paramMap);
		map.put("rows", jsonArray);
		map.put("total", totalCount);
		return map;
	}
	
	/**
	 * 获取所有订单详情
	 * 
	 * @param date
	 * @return
	 */
	@RequestMapping("/deleteOrders")
	@ResponseBody
	public Map<String, Object> deleteOrders(String orderIds, String userId, String userType) {
		Map<String,Object> result = new HashMap<String, Object>();
		
		logger.info("deleteOrders:userId:" + userId + ",userType:" + userType);
		logger.info("deleteOrders:orderIds:" + orderIds);
		
		if (orderIds == null || orderIds.length() == 0){
			result.put(Constants.STATUS, Constants.FAILURE);
			result.put(Constants.MESSAGE, "未找到相应订单信息");
			return result;
		}
		String[] arr = orderIds.split(",");
		Map<String,Object> paramMap=new HashMap<String,Object>();
		int fail = 0;
		
		for (int i = 0; i < arr.length; i++)
		{
			paramMap.put("orderId", arr[i]);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				fail++;
				logger.error("未查找到待删除订单，orderId=" + arr[i]);
				continue;
			}
			
			paramMap.put("orderStatus",Constants.STATUS_SYS_DELETE);
			
			Date now = new Date();
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",Constants.STATUS_SYS_DELETE);
			record.put("time", now);
			record.put("content", "订单信息即将被系统删除");
			recordes.add(record);
			
			paramMap.put("records",recordes.toString());
			paramMap.put("lastUpdateTime",now);
			
			int flag = orderService.deleteOrderById(paramMap);
			if(flag == -1 || flag ==0)
			{
				fail++;
				logger.error("删除订单失败，orderId=" + arr[i]);
			}
		}
		
		if( fail == 0){
			result.put(Constants.STATUS, Constants.SUCCESS);
			result.put(Constants.MESSAGE, "删除订单成功");
			return result;
		}
		else{
			result.put(Constants.STATUS, Constants.FAILURE);
			result.put(Constants.MESSAGE, "部分订单删除失败，请刷新后再试");
			return result;
		}	
	}
	
	@RequestMapping("/updateDeliveryNo")
	public @ResponseBody Map<String, String> updateDeliveryNo(
			@RequestParam String order_id,  @RequestParam String deliveryNo){
		Map<String,String> result = new HashMap<String, String>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.MESSAGE, "未找到相应订单信息");
				return result;
			}

			paramMap.put("orderStatus",Constants.STATUS_DELIVERED);
			
			Date now = new Date();
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",Constants.STATUS_DELIVERED);
			record.put("time", now);
			recordes.add(record);

			paramMap.put("records",recordes.toString());
			paramMap.put("lastUpdateTime",now);
			paramMap.put("deliveryNo",deliveryNo);
			
			int flag = orderService.updateDeliveryNo(paramMap);
			if (flag != -1 && flag != 0)
			{	
				//向用户发送短信信息
				//组装请求对象-具体描述见控制台-文档部分内容
		        SendSmsRequest request = new SendSmsRequest();
		        //必填:待发送手机号
		        request.setPhoneNumbers(order.getPhone());
		        //必填:短信签名-可在短信控制台中找到
		        request.setSignName("上海明静");
		        //必填:短信模板-可在短信控制台中找到
		        request.setTemplateCode("SMS_145597339");
		        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		        request.setTemplateParam("{\"orderId\":\"" + order.getOrderId() + "\", \"deliveryNo\":\"" + deliveryNo + "\"}");
		        
		        SendSms.sendSmsAli(request);
		        
				result.put(Constants.STATUS, Constants.SUCCESS);
				result.put(Constants.MESSAGE, "更新快递单号信息成功");
				return result;
			} 
			else 
			{
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.MESSAGE, "更新快递单号信息失败");
				logger.error("[updateDeliveryNo Err]order_id=" + order_id);
				return result;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[updateDeliveryNo Exception]" + e.getMessage());
		}

		result.put(Constants.STATUS, Constants.FAILURE);
		result.put(Constants.MESSAGE, "更新快递单号信息失败");	
		return result;
	}
	
	
	@RequestMapping("/updateOrderByEmployee")
	public @ResponseBody Map<String, String> updateOrderByEmployee(@RequestParam String phone,
			@RequestParam String order_id, @RequestParam Integer reason, @RequestParam String content){
		Map<String,String> result = new HashMap<String, String>();

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.MESSAGE, "未找到相应订单信息");
				return result;
			}

			paramMap.put("orderStatus",Constants.STATUS_REJECTED);
			paramMap.put("createUser",order.getCreateUser());
			
			String tmp = "";
			
			//!!!注意这里修改字符串需要同时修改订单详情的时候中判断打回原因微信接口
			if(reason == 1){
				tmp = "身份证不清晰";
			}
			else if(reason == 2){
				tmp = "资料超过50页需补缴费用";
			}
			
			if (content != null && !content.isEmpty())
			{
				tmp = tmp + "(" + content + ")";
			}
			
			Date now = new Date();
			
			JSONArray recordes = JSON.parseArray(order.getRecords());
			JSONObject record = new JSONObject();
			record.put("status",Constants.STATUS_REJECTED);
			record.put("time", now);
			record.put("content", tmp);
			recordes.add(record);
			
			paramMap.put("records",recordes.toString());
			paramMap.put("lastUpdateTime",now);
			
			int flag = orderService.updateOrderStatus(paramMap);			
			if (flag != -1 && flag != 0)
			{	
				//向用户发送短信信息
				//组装请求对象-具体描述见控制台-文档部分内容
		        SendSmsRequest request = new SendSmsRequest();
		        //必填:待发送手机号
		        request.setPhoneNumbers(order.getPhone());
		        //必填:短信签名-可在短信控制台中找到
		        request.setSignName("上海明静");
		        //必填:短信模板-可在短信控制台中找到
		        request.setTemplateCode("SMS_145597385");
		        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		        request.setTemplateParam("{\"orderId\":\"" + order.getOrderId() + "\", \"reason\":\"" + tmp + "\"}");      
		        SendSms.sendSmsAli(request);
		        
				result.put(Constants.STATUS, Constants.SUCCESS);
				result.put(Constants.MESSAGE, "更新订单状态成功");
				return result;
			} 
			else 
			{
				result.put(Constants.STATUS, Constants.FAILURE);
				result.put(Constants.MESSAGE, "更新订单状态失败");
				logger.error("[updateOrderByEmployee Err]order_id=" + order_id);
				return result;
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[updateDeliveryNo Exception]" + e.getMessage());
		}

		result.put(Constants.STATUS, Constants.FAILURE);
		result.put(Constants.MESSAGE, "更新快递单号信息失败");	
		return result;
	}
		
	@RequestMapping("/download")
	public void downloadOrder(@RequestParam String order_id, HttpServletRequest request, HttpServletResponse response){

		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("orderId",order_id);
			Order order = orderService.getOrderByIdWx(paramMap);
			if(order == null)
			{
				return;
			}
			
			Map<String,Object> dataMap=new HashMap<String,Object>();
			
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			dataMap.put("orderId", order.getOrderId());
			dataMap.put("createTime", formattmp.format(order.getCreateTime()));
			dataMap.put("name", order.getName());
			dataMap.put("sex", order.getSex());
			dataMap.put("idcard", order.getIdCard());
			dataMap.put("concatName", order.getConcatName());
			dataMap.put("concatPhone", order.getConcatPhone());
			
			dataMap.put("hospital", order.getHospital());
			dataMap.put("hospitalArea", order.getHospitalArea());
			dataMap.put("department", order.getDepartment());
			dataMap.put("bedNo", order.getBedNo());	
			dataMap.put("mrNo", order.getMrNo());
			dataMap.put("doctor", order.getDoctor());
			dataMap.put("diseases", order.getDiseases());
			dataMap.put("outDate", order.getOutDate());
			
			
			if(order.getDeliveryNo() == null || order.getDeliveryNo().length() == 0)
			{
				dataMap.put("deliveryNo", "暂无");
			}
			else
			{
				dataMap.put("deliveryNo", order.getDeliveryNo());
			}
			
			dataMap.put("phone", order.getPhone());
			
			String temp = order.getProvice() + order.getCity()+order.getDistrict()+order.getAdrTitle();			
			if(order.getDetail() == null || order.getDetail().isEmpty()){
				dataMap.put("address", temp);
			}
			else
			{
				dataMap.put("address", temp + order.getDetail());
			}	
			
            String image1 = WordGenerator.GetImageStrFromUrl(order.getIdCardFront());
            String image2 = WordGenerator.GetImageStrFromUrl(order.getIdCardBack());
            String image3 = WordGenerator.GetImageStrFromUrl(order.getOutSummary());
            //用户上传的签名照片需要进行翻转
            String image4 = "";
            if(order.getSign()!= null && order.getSign().length() > 0)
            {
            	image4 = WordGenerator.GetImageStrFromUrl(order.getSign()+"?imageMogr2/rotate/270");
            }

            dataMap.put("image1", image1);
            dataMap.put("image2", image2);
            dataMap.put("image3", image3);
            dataMap.put("image4", image4);
			     
            String path = System.getProperty("user.dir").concat("/File/");
	        //String path = request.getSession().getServletContext().getRealPath("/").concat("File/");
	        //System.out.println("downloadOrder:" + path);
	        String name = order.getOrderId() + ".doc";
	        File outFile = WordGenerator.createDoc(dataMap, path, name, "order");
			
			//将文件返回给浏览器
			try {
				// 清空response
				response.reset();
				
				// 设置response的Header
				response.addHeader("Content-Disposition", "attachment;filename=" + order_id + ".doc");
				response.addHeader("Content-Length", "" + outFile.length());
				response.setContentType("application/octet-stream");
				
				//设置输出的文件内容
				InputStream fis = new BufferedInputStream(new FileInputStream(outFile));
				OutputStream outStream = response.getOutputStream();
				byte buffer[] = new byte[1024];
				
				int len = 0;
		        //循环将输入流中的内容读取到缓冲区当中
		        while((len=fis.read(buffer))>0){
		             //输出缓冲区的内容到浏览器，实现文件下载
		        	outStream.write(buffer, 0, len);
		        }
		        outStream.flush();
		        outStream.close();
		        fis.close();
					
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error("[downloadOrder Exception]" + order_id);
		}	
		return;
	}
	
}
