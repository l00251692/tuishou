package com.changyu.foryou.mapper;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.model.WithDraw;

public interface UsersMapper {


    int insertSelective(Users record);

    Users selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(Users record);
    
    int updateUserBallance(Map<String, Object> paramMap);
    
    int updateUserLocation(Map<String, Object> paramMap);
    
    int updateUserSanReg(Users record);

	Users checkLogin(String user_id);
	
	List<Users> getDistrictUsers(Map<String, Object> paramMap);
	
	int follow(Map<String, Object> paramMap);
	
	int unfollow(Map<String, Object> paramMap);
	
	int bindPhone(Map<String, Object> paramMap);
	
	public Follow checkFollow(Map<String, Object> paramMap);
	
	public int updateLocation(Map<String, Object> paramMap);
	
	public int updateBalance(Map<String, Object> paramMap);
	
	public int createWithDraw(Map<String, Object> paramMap);
	
    public List<WithDraw> getUserWithDraw(Map<String, Object> paramMap);
    
    public int postFeedBack(Map<String, Object> paramMap);

}