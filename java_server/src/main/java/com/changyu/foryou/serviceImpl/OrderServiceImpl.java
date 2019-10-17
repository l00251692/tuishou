package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.mapper.OrderMapper;
import com.changyu.foryou.model.Hospital;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.service.OrderService;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
	private OrderMapper orderMapper;
	
	public OrderMapper getOrderMapper() {
		return orderMapper;
	}

	@Autowired
	public void setOrderMapper(OrderMapper orderMapper) {
		this.orderMapper = orderMapper;
	}
	
	public int addOrder(Map<String, Object> paramMap) {
		return orderMapper.insertSelective(paramMap);
	}
	
	public int deleteOrderById(Map<String, Object> paramMap){
		return orderMapper.deleteOrderById(paramMap);
	}
	
	public Order getOrderByIdWx(Map<String, Object> paramMap) {
		return orderMapper.selectByPrimaryKey(paramMap);
	}
	
	
	public int updateOrderStatus(Map<String, Object> paramMap) {
		return orderMapper.updateOrderStatus(paramMap);
	}
	
	public int updateOrderIdCard(Map<String, Object> paramMap) {
		return orderMapper.updateOrderIdCard(paramMap);
	}
	
	public List<Order> getMineOrders(Map<String, Object> paramMap){
		return orderMapper.getMineOrders(paramMap);
	}
	
	public List<Order> selectOrdersByDate(Map<String, Object> paramMap){
		return orderMapper.selectOrdersByDate(paramMap);
	}
	
	public List<Order> selectOrdersByNameIdCard(Map<String, Object> paramMap){
		return orderMapper.selectOrdersByNameIdCard(paramMap);
	}
	
	public List<Order> getOrdersByStatus(Map<String, Object> paramMap){
		return orderMapper.getOrdersByStatus(paramMap);
	}
	
	public long getOrdersStatusCount(Map<String, Object> paramMap){
		return orderMapper.getOrdersStatusCount(paramMap);
	}
	
	public int updateDeliveryNo(Map<String, Object> paramMap){
		return orderMapper.updateDeliveryNo(paramMap);
	}
	
	
	public List<Order> getOrdersBeforeDate(Map<String, Object> paramMap){
		return orderMapper.getOrdersBeforeDate(paramMap);
	}
}
