package com.changyu.foryou.mapper;

import java.util.List;
import java.util.Map;
import com.changyu.foryou.model.Order;


public interface OrderMapper {
	int insertSelective(Map<String, Object> paramMap);
	
	int deleteOrderById(Map<String, Object> paramMap);
	
	Order selectByPrimaryKey(Map<String,Object> paramMap);
	
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
