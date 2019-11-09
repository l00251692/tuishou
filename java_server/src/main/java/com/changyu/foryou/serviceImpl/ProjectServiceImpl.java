package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.changyu.foryou.mapper.ProjectMapper;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Collect;
import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Followers;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.service.ProjectService;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectMapper projectMapper;
	
	public List<Banner> getBannerInfo()
	{
		return projectMapper.getBannerInfo();
	}
	
	public List<Project> getProjectList(Map<String, Object> paramMap)
	{
		return projectMapper.getProjectList(paramMap);
	}
	
	public List<Follow> getMyProjectList(Map<String, Object> paramMap)
	{
		return projectMapper.getMyProjectList(paramMap);
	}
	
	public Project getProjectInfo(Map<String, Object> paramMap)
	{
		return projectMapper.getProjectInfo(paramMap);
	}
	
	public int createProject(Map<String, Object> paramMap)
	{
		return projectMapper.insertSelective(paramMap);
	}
	
	public int deleteProject(Map<String, Object> paramMap){
		return projectMapper.deleteProject(paramMap);
	}

	public int updateProjectHeadImg(Map<String, Object> paramMap)
	{
		return projectMapper.updateProjectHeadImg(paramMap);
	}
	
	public int updateProjectQrCode(Map<String, Object> paramMap)
	{
		return projectMapper.updateProjectQrCode(paramMap);
	}
	
	public int updateProjectAddImgs(Map<String, Object> paramMap)
	{
		return projectMapper.updateProjectAddImgs(paramMap);
	}
	
	public int commitProjectComment(Map<String, Object> paramMap)
	{
		return projectMapper.commitComment(paramMap);
	}
	
	
	public int getCommentCount(Map<String, Object> paramMap)
	{
		return projectMapper.getCommentCount(paramMap);
	}
	
	
	public int getCommentUnreadMsgCount(String userId)
	{
		return projectMapper.getCommentUnreadMsgCount(userId);
	}
	
	public int setProjectCommentRead(Map<String, Object> paramMap)
	{
		return projectMapper.setProjectCommentRead(paramMap);
	}
	
	public int addCollect(Map<String, Object> paramMap){
		return projectMapper.addCollect(paramMap);
	}
	
	public Collect getCollect(Map<String, Object> paramMap){
		return projectMapper.getCollect(paramMap);
	}
	
	public int updateCollectStatus(Map<String, Object> paramMap){
		return projectMapper.updateCollectStatus(paramMap);
	}
	
	public List<Collect> getCollectList(Map<String, Object> paramMap){
		return projectMapper.getCollectList(paramMap);
	}
	
	public List<Collect> getTaskCollectList(Map<String, Object> paramMap){
		return projectMapper.getTaskCollectList(paramMap);
	}
	
	public int updateCollectFiles(Map<String, Object> paramMap){
		return projectMapper.updateCollectFiles(paramMap);
	}	
	
	public int getFollowerCounts(Map<String, Object> paramMap){
		return projectMapper.getFollowerCounts(paramMap);
	}
	
	public int getCollectCounts(Map<String, Object> paramMap){
		return projectMapper.getCollectCounts(paramMap);
	}
	
	public int getCollectCountsByStatus(Map<String, Object> paramMap){
		return projectMapper.getCollectCountsByStatus(paramMap);
	}
	
	public List<Followers> getFollowers(Map<String, Object> paramMap){
		return projectMapper.getFollowers(paramMap);
	}
}


