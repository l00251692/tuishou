package com.changyu.foryou.service;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.model.WithDraw;

public interface UserService {
	
	Users selectByUserId(String user_id);//根据用户手机号获取用户信息

	void addUsers(Users users);

	int updateUserInfo(Users users);
	
	Users checkLogin(String user_id);
	
	int follow(Map<String, Object> paramMap);
	
	int unfollow(Map<String, Object> paramMap);
	
	int bindPhone(Map<String, Object> paramMap);
	
	Follow checkFollow(Map<String, Object> paramMap);
	
	int updateLocation(Map<String, Object> paramMap);
	
	int updateBalance(Map<String, Object> paramMap);
	
	int createWithDraw(Map<String, Object> paramMap);
	
	List<WithDraw> getUserWithDraw(Map<String, Object> paramMap);
	
	int postFeedBack(Map<String, Object> paramMap);
}