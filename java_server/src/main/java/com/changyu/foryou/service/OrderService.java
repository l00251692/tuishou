package com.changyu.foryou.service;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Hospital;
import com.changyu.foryou.model.Order;

public interface OrderService {
	
	public int addOrder(Map<String, Object> paramMap);
	
	public int deleteOrderById(Map<String, Object> paramMap);
	
	public Order getOrderByIdWx(Map<String, Object> paramMap);
	
	public int updateOrderStatus(Map<String, Object> paramMap);
	
	public int updateOrderIdCard(Map<String, Object> paramMap);
	
	public List<Order> getMineOrders(Map<String, Object> paramMap);
	
	public List<Order> selectOrdersByDate(Map<String, Object> paramMap);
	
	public List<Order> selectOrdersByNameIdCard(Map<String, Object> paramMap);
	
	public List<Order> getOrdersByStatus(Map<String, Object> paramMap);
	
	public long getOrdersStatusCount(Map<String, Object> paramMap);
	
	public int updateDeliveryNo(Map<String, Object> paramMap);
	
	public List<Order> getOrdersBeforeDate(Map<String, Object> paramMap);

}
