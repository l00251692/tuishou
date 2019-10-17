package com.changyu.foryou.model;

import java.util.Date;

public class Project {
	
	private String projectId;
	
	private String type; //标题
	
	private String title; //标题
	
	private String detail; //内容
	
	private String salary;  //联系方式
	
	private Date createTime;
	
	private String createUserId;
	
	private String headImg; //封面
	
	private Date startTime;
	
	private Date deadLineTime;
	
	private String rule;
	
	private String region;
	
	private String addImgs;
	
	private int follow;
	
	private int status;
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public int getFollow() {
		return follow;
	}

	public void setFollow(int interest) {
		this.follow = interest;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public Date getDeadLineTime() {
		return deadLineTime;
	}

	public void setDeadLineTime(Date deadLineTime) {
		this.deadLineTime = deadLineTime;
	}

	public String getAddImgs() {
		return addImgs;
	}

	public void setAddImgs(String addImgs) {
		this.addImgs = addImgs;
	}

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
