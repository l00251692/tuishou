package com.changyu.foryou.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.PayUtil;


@Controller
@RequestMapping("customer")
public class Customer {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	private static final Logger logger = Logger.getLogger(Customer.class);
	
	// 与接口配置信息中的Token要一致
	 private static String WECHAT_TOKEN = "Tuishou";
	@RequestMapping("/checkToken")
	public void get(HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	    boolean isGet = request.getMethod().toLowerCase().equals("get");
	    PrintWriter print;
	    
	    logger.info("check customer token");
	    if (isGet) {
	        // 微信加密签名
	        String signature = request.getParameter("signature");
	        // 时间戳
	        String timestamp = request.getParameter("timestamp");
	        // 随机数
	        String nonce = request.getParameter("nonce");
	        // 随机字符串
	        String echostr = request.getParameter("echostr");
	        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
	        if (signature != null && checkSignature(signature, timestamp, nonce)) {
	            System.out.println("111111111");
	            System.out.println("jiaoyanchenggong");
	            try {
	                print = response.getWriter();
	                print.write(echostr);
	                print.flush();
	
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } else {
	
	        }
	    } else {
	
	    }
	}
	 /**
	 * 验证签名
	 *
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
	    String[] arr = new String[]{WECHAT_TOKEN, timestamp, nonce};
	    // 将token、timestamp、nonce三个参数进行字典序排序
	    // Arrays.sort(arr);
	    sort(arr);
	    StringBuilder content = new StringBuilder();
	    for (int i = 0; i < arr.length; i++) {
	        content.append(arr[i]);
	    }
	    MessageDigest md = null;
	    String tmpStr = null;
	
	    try {
	        md = MessageDigest.getInstance("SHA-1");
	        // 将三个参数字符串拼接成一个字符串进行sha1加密
	        byte[] digest = md.digest(content.toString().getBytes());
	        tmpStr = byteToStr(digest);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    content = null;
	    // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
	    return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
	
	/**
	 * 将字节数组转换为十六进制字符串
	 *
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
	    String strDigest = "";
	    for (int i = 0; i < byteArray.length; i++) {
	        strDigest += byteToHexStr(byteArray[i]);
	    }
	    return strDigest;
	}
	
	/**
	 * 将字节转换为十六进制字符串
	 *
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
	    char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	    char[] tempArr = new char[2];
	    tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
	    tempArr[1] = Digit[mByte & 0X0F];
	    String s = new String(tempArr);
	    return s;
	}
	
	public static void sort(String a[]) {
	    for (int i = 0; i < a.length - 1; i++) {
	        for (int j = i + 1; j < a.length; j++) {
	            if (a[j].compareTo(a[i]) < 0) {
	                String temp = a[i];
	                a[i] = a[j];
	                a[j] = temp;
	            }
	        }
	    }
	}
	
	/**
     * 接收微信后台发来的用户消息
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "/checkToken", method = RequestMethod.POST)
    @ResponseBody
    public String receiveMessage(@RequestBody Map<String, Object> msg) throws Exception {
    	logger.info("receiveMessage");
        //获取token
    	String access_token = (String)PayUtil.getAccessToken().get("access_token");
        System.out.println("access_token:--------" + access_token);

        //用户openId
        String fromUserName = msg.get("FromUserName").toString();
        String createTime = msg.get("CreateTime").toString();
        String toUserName = msg.get("ToUserName").toString();
        String msgType = msg.get("MsgType").toString();
        if (msgType.equals("text")) { //收到的是文本消息,并将消息返回给客服
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("ToUserName", fromUserName);
            resultMap.put("FromUserName", toUserName);
            resultMap.put("CreateTime", Long.parseLong(createTime));
            resultMap.put("MsgType", "transfer_customer_service");
            String json = JSON.toJSONString(resultMap);
            com.alibaba.fastjson.JSONObject result = com.alibaba.fastjson.JSONObject.parseObject(json);
            return result.toString();
        }
        else if(msgType.equals("event")){
        	
        	String sessionFrom = msg.get("SessionFrom").toString();
        	logger.info("receive customer msg:" + sessionFrom);
        	
        	Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("projectId", sessionFrom);
    		Project project = projectService.getProjectInfo(paramMap);
    		JSONObject text = new JSONObject();
    		if(project != null && project.getLink()!= null && project.getLink().length()>0)
    		{
    			text.put("content", project.getLink());
    		}
    		else
    		{
    			text.put("content", "欢迎使用小程序，请回复数字查看具体内容");
    		}
             
            //https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/customer-message/customerServiceMessage.send.html
            JSONObject json = new JSONObject();
            json.put("touser", toUserName);
            json.put("msgtype", "text");
            json.put("text", text);
            
            //发送aip
            sendMsToCustomer(access_token, json);
        	
        }
        
        logger.info("receive customer service msg, send to user");
        //客服方面,也回复一个文本消息
        JSONObject text = new JSONObject();
        text.put("content", msg.get("MsgType"));
        JSONObject json = new JSONObject();
        json.put("touser", toUserName);
        json.put("text", text);
        json.put("msgtype", "text");
        //发送aip
        sendMsToCustomer(access_token, json);
        return "success";
    }
    
    /**
     * 用户发送消息给客服
     *
     * @param json 消息的参数
     */
    private void sendMsToCustomer(String access_token, JSONObject json) {

        RestTemplate restTemplate = new RestTemplate();

        //access_token
        String result = restTemplate.postForEntity("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" +
                        access_token, json, String.class).getBody();
    }
	
	
	/*
     * 微信小程序推送单个用户
     * */
    @RequestMapping(value = "/Phsu", method = RequestMethod.POST)
    @ResponseBody
    public String pushOneUser(String openid, String formid, Long orderId) throws Exception {
        //获取access_token
        //String access_token = getAccess_token(Constants.appId, Constants.apiKey);//这里是自己小程序的appId和secretKey
    	String access_token = (String)PayUtil.getAccessToken().get("access_token");
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send" +
                "?access_token=" + access_token;
        return "";
        /*
  		//拼接推送的模版
         WxMssVo wxMssVo = new WxMssVo();
         wxMssVo.setTouser(openid);//用户openid
         wxMssVo.setTemplate_id("DkA9fB-xmODJLVf1JvRtWdxr5-iTGETOfV5_6Nuk8NU");//模版id
         wxMssVo.setForm_id(formid);//formid

         Map<String, TemplateDataVo> m = new HashMap<>();

         if (order.getStatus().equals(20)) {
                List<OrderProduct> orderProducts = order.getOrderProducts();
                String ProductName = null;
                for (OrderProduct orderProduct : orderProducts) {
                    ProductName = orderProduct.getName();
                }
                TemplateDataVo keyword1 = new TemplateDataVo();
                keyword1.setValue(ProductName);//商品名称
                m.put("keyword1", keyword1);
                wxMssVo.setData(m);
                System.out.println(keyword1.getValue() + ":name");

                TemplateDataVo keyword2 = new TemplateDataVo();
                keyword2.setValue(order.getOrderSn() + "");//订单编号
                m.put("keyword2", keyword2);
                wxMssVo.setData(m);
                System.out.println(keyword2.getValue() + ":bianhao");

                TemplateDataVo keyword3 = new TemplateDataVo();
                keyword3.setValue(order.getTotalPrice() + "");//订单金额
                m.put("keyword3", keyword3);
                wxMssVo.setData(m);
                System.out.println(keyword3.getValue() + ":jine");

                TemplateDataVo keyword4 = new TemplateDataVo();
                keyword4.setValue(order.getPoyTime() + "");//支付时间
                m.put("keyword4", keyword4);
                wxMssVo.setData(m);
                System.out.println(keyword4.getValue() + ":time");
            }
            ResponseEntity<String> responseEntity =
                    restTemplate.postForEntity(url, wxMssVo, String.class);

            System.out.println(responseEntity.getBody() + "wancheng");
            //发送成功
            return responseEntity.getBody()
            */
    }
    
}
