package com.changyu.foryou.tools;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.changyu.foryou.model.Order;
import com.changyu.foryou.service.OrderService;


@Component
public class ScanOrderManager {
	
	@Autowired
	private OrderService orderService;
	
	private static ScanOrderManager scanOrderManager;
	
	@PostConstruct
	public void init(){
		scanOrderManager = this;
		scanOrderManager.orderService = this.orderService;
	}
	
	public void run() throws MessagingException, UnsupportedEncodingException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -14);//最后一个数字可改，代表天数
	    Date date = cal.getTime();
	    String timeEnd = sdf.format(date);//十四天之前日期
	    System.out.println("ScanOrderManager:date-"  + timeEnd);
		
		//查询所有待发货订单距离现在日期超过20天的订单	
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("status", Constants.STATUS_PAYED);
		paramMap.put("TimeEnd", timeEnd);
		
		List<Order> list = scanOrderManager.orderService.getOrdersBeforeDate(paramMap);
		
		if(list.size() < 1){
			return;
		}
		
		
		Map<String,String> map= new HashMap<String,String>();
        SendMail mail = new SendMail("18261149716@163.com","liu1109");//163邮箱和授权码
        map.put("mail.smtp.host", "smtp.163.com");
        map.put("mail.smtp.auth", "true");
        map.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        map.put("mail.smtp.port", "994");
        map.put("mail.smtp.socketFactory.port", "994");
        mail.setPros(map);
        mail.initMessage();

        mail.setRecipient("4264806@qq.com");
        mail.setRecipientCC("18261149716@163.com");
        mail.setSubject("【提醒】订单距离用户出院日期已超过14天，请尽快处理");
        
        StringBuilder content = new StringBuilder();
        content.append("<body>");
        content.append("<span style=\"font-size:15px\">如下订单请尽快处理.</span>");
        content.append("<hr>");
        
        content.append("<table>");
        content.append("<thead>");
       	content.append("<tr>");
        content.append("<th>订单号</th>");
        content.append("<th>患者姓名</th>");
        content.append("<th>创建时间</th>");
        content.append("<th>医院名称</th>");
        content.append("<th>出院时间</th>");
        content.append("</tr>");
        content.append("</thead>");
        content.append("<tbody>");
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Order order: list) {
            content.append("<tr>");
            content.append("<td style=\"text-align:center\">"+order.getOrderId()+"</td>");
            content.append("<td style=\"text-align:center\">"+order.getName()+"</td>");
            content.append("<td style=\"text-align:center\">"+format.format(order.getCreateTime())+"</td>");
            content.append("<td style=\"text-align:center\">"+order.getHospital()+"</td>");
            content.append("<td style=\"text-align:center\">"+order.getOutDate()+"</td>");
            content.append("</tr>");
        }
        content.append("</tbody>");
	    content.append("</table>");
	
	    content.append("<hr>");
	    content.append("<span style=\"font-size:17px\">感谢您的使用! </span></body>");

        mail.setContent(content.toString(),"text/html;charset=gb2312");
        mail.setDate(new Date());
        mail.setFrom("订单管理系统邮件");
        mail.sendMessage();
	}
}
