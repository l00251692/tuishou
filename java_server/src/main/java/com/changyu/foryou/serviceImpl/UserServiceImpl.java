package com.changyu.foryou.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.changyu.foryou.mapper.UsersMapper;
import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.model.WithDraw;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.ToolUtil;


@Service("userService")
public class UserServiceImpl implements UserService {
	private UsersMapper usersMapper;         //操作用户信息


	@Autowired
	public void setUsersMapper(UsersMapper usersMapper) {
		this.usersMapper = usersMapper;
	}

	
	public Users selectByUserId(String userId) {
		return usersMapper.selectByPrimaryKey(userId);
	}


	public void addUsers(Users users) {
		usersMapper.insertSelective(users);
	}


	public int updateUserInfo(Users users) {
		return usersMapper.updateByPrimaryKeySelective(users);
	}
	
	@Override
	public Users checkLogin(String user_id) {
		return usersMapper.checkLogin(user_id);
	}
	
    public int follow(Map<String, Object> paramMap){
    	return usersMapper.follow(paramMap);
    }
	
	public int unfollow(Map<String, Object> paramMap){
		return usersMapper.unfollow(paramMap);
	}
	
	public int bindPhone(Map<String, Object> paramMap){
    	return usersMapper.bindPhone(paramMap);
    }
	
	public Follow checkFollow(Map<String, Object> paramMap){
    	return usersMapper.checkFollow(paramMap);
    }
	
	public int updateLocation(Map<String, Object> paramMap){
		return usersMapper.updateLocation(paramMap);
	}
	
	public int updateBalance(Map<String, Object> paramMap){
		return usersMapper.updateBalance(paramMap);
	}
	
    public int createWithDraw(Map<String, Object> paramMap){
		return usersMapper.createWithDraw(paramMap);
	}
	
    public List<WithDraw> getUserWithDraw(Map<String, Object> paramMap){
		return usersMapper.getUserWithDraw(paramMap);
	}
    
    public int postFeedBack(Map<String, Object> paramMap){
    	return usersMapper.postFeedBack(paramMap);
    }
}
