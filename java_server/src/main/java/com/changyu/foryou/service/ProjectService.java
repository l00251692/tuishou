package com.changyu.foryou.service;

import java.util.List;
import java.util.Map;

import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Collect;
import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Followers;
import com.changyu.foryou.model.Project;

public interface ProjectService {
	
	public List<Banner> getBannerInfo();
	
	public List<Project> getProjectList(Map<String, Object> paramMap);
	
	public List<Follow> getMyProjectList(Map<String, Object> paramMap);
	
	public Project getProjectInfo(Map<String, Object> paramMap);
	
	public int createProject(Map<String, Object> paramMap);
	
	public int deleteProject(Map<String, Object> paramMap);
	
	public int updateProjectHeadImg(Map<String, Object> paramMap);
	
	public int updateProjectAddImgs(Map<String, Object> paramMap);
	
	public int commitProjectComment(Map<String, Object> paramMap);
	
	public int getCommentCount(Map<String, Object> paramMap);
	
	
	public int getCommentUnreadMsgCount(String userId);
	
	public int setProjectCommentRead(Map<String, Object> paramMap);
	
	public int addCollect(Map<String, Object> paramMap);
	
	public Collect getCollect(Map<String, Object> paramMap);
	
	public int updateCollectStatus(Map<String, Object> paramMap);
	
	public List<Collect> getCollectList(Map<String, Object> paramMap);
	
	public List<Collect> getTaskCollectList(Map<String, Object> paramMap);
	
	public int updateCollectFiles(Map<String, Object> paramMap);
	
	public int getFollowerCounts(Map<String, Object> paramMap);
	
	public int getCollectCounts(Map<String, Object> paramMap);
	
	public List<Followers> getFollowers(Map<String, Object> paramMap);
	
}
