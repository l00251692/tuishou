package com.changyu.foryou.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changyu.foryou.model.AddressDTO;
import com.changyu.foryou.model.Banner;
import com.changyu.foryou.model.Collect;
import com.changyu.foryou.model.Follow;
import com.changyu.foryou.model.Followers;
import com.changyu.foryou.model.LocationDTO;
import com.changyu.foryou.model.Order;
import com.changyu.foryou.model.Project;
import com.changyu.foryou.model.Users;
import com.changyu.foryou.service.ProjectService;
import com.changyu.foryou.service.UserService;
import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.EtifUtil;
import com.changyu.foryou.tools.HttpRequest;
import com.changyu.foryou.tools.PayUtil;
import com.changyu.foryou.tools.QQMapUtil;
import com.qiniu.util.Auth;

@Controller
@RequestMapping("/project")
public class ProjectController {
	
	private ProjectService projectService;
	
	private UserService userService;
	
	@Autowired
	EtifUtil etifUtil;
	
	private static final Logger logger = Logger.getLogger(ProjectController.class);

	@Autowired
	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 获得首页广告信息
	 * add by ljt
	 */
	
	@RequestMapping("/getBannerInfoWx")
    public @ResponseBody Map<String,String> getBannerInfoWx(HttpServletRequest request) {
		
		//String realPath = request.getSession().getServletContext().getRealPath("/");
        //realPath = realPath.concat("JiMuImage/banner/");
        
        List<Banner> bannerlist = projectService.getBannerInfo();
        Map<String,String> data = new HashMap<String, String>();
        JSONArray banner_arr = new JSONArray();
        for(Banner banner: bannerlist)
        {
        	JSONObject obj = new JSONObject();
        	obj.put("banner_id",banner.getBannerId());
        	//String url = Constants.localIp + "/banner/" + banner.getImgUrl();
        	obj.put("carousel_img",banner.getImgUrl());
        	banner_arr.add(obj);
        }
        data.put("State", "Success");
		data.put("data", banner_arr.toString());				
		return data;
	}
	
	
	@RequestMapping("/getProjectListWx")
    public @ResponseBody Map<String,Object> getProjectListWx(@RequestParam Integer page) {
		
		logger.info("enter getProjectListWx");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
        List<Project> projectlist = projectService.getProjectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", projectlist.size());
				
		for (Project project: projectlist)
		{
			JSONObject node = new JSONObject(); 
			node.put("task_id", project.getProjectId());
			node.put("task_name", project.getTitle());
			node.put("task_detail", project.getDetail());
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
			node.put("create_time", formattmp.format(project.getCreateTime()));
			node.put("start_date", formattmp.format(project.getStartTime()));
			node.put("end_date", formattmp.format(project.getDeadLineTime()));
			node.put("task_head", project.getHeadImg());
			node.put("salary", project.getSalary());
			
			
			
			Users user = userService.selectByUserId(project.getCreateUserId());
			if (user == null)
			{
				node.put("create_userName", "");
				node.put("task_auth", "");
			}
			else
			{
				node.put("create_userName", user.getNickname());
				node.put("task_auth", user.getImgUrl());
			}
			
			
			node.put("region", project.getRegion());
			
			
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(project.getDeadLineTime());
			
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(project.getStartTime());
			
			Calendar  today = Calendar.getInstance();
			

			//0 ---未开始   1----进行中   2---已结束
			if(calendar2.getTimeInMillis() > today.getTimeInMillis())
			{
				node.put("remain_days", -1);
				node.put("state", 0); //未开始
			}
			else
			{
				Map<String, Object> paramMap2 = new HashMap<String, Object>();
				paramMap2.put("projectId", project.getProjectId());
				int collectCount = projectService.getCollectCounts(paramMap2);
				
				double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
				if(days < 0 || (project.getCount() >0 && collectCount >= project.getCount()))
				{
					node.put("state", 2); //已结束
				}
				else{
					node.put("state", 1); //进行中
				}
				
				
				node.put("remain_days", days);
			}
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getMyProjectListWx")
    public @ResponseBody Map<String,Object> getMyProjectListWx(@RequestParam String user_id, @RequestParam Integer type, @RequestParam Integer page) {

		//:TODO  测试 ，删除
		//JSONObject jo = etifUtil.getEtifInfoFromQiNiu("http://pz8njmvtg.bkt.clouddn.com/paascloud/file/735227650246755328.jpg");
		
		
		Map<String,Object> data = new HashMap<String, Object>();
		if(user_id == null || user_id.length() == 0)
		{
			data.put("State", "Success");
			data.put("data", "");				
			return data;
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
		paramMap.put("followerId", user_id);
        List<Follow> projectlist = projectService.getMyProjectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", projectlist.size());
				
		for (Follow project: projectlist)
		{
			if(type == 1 && !project.getCreateUserid().equals(user_id))
			{
				//查看我发布的，实际上此条是我关注的
				continue;
			}
			
			if(type == 2 && project.getCreateUserid().equals(user_id)){
				//我领取的但是是我自己创建的
				continue;
			}
			
			
			JSONObject node = new JSONObject(); 
			node.put("task_id", project.getProjectId());
			node.put("task_name", project.getTitle());

			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
			node.put("create_time", formattmp.format(project.getCreateTime()));
			node.put("start_date", formattmp.format(project.getStartTime()));
			node.put("end_date", formattmp.format(project.getDeadlineTime()));
		
			node.put("region", project.getRegion());
			node.put("rule", project.getRule());
			node.put("salary", project.getSalary());
			
			if(project.getCreateUserid().equals(user_id))
			{
				node.put("task_type", "my_create");
			}
			else
			{
				node.put("task_type", "my_follow");
			}
			//int likecount = userService.getProjectLikeCount(paramMap2);
			//node.put("like", likecount);
			/*
			if (project.getDeadLineTime() != null)
			{
				Date  deadLineTime =  project.getDeadLineTime();	
				Calendar calendar1 = Calendar.getInstance();
				calendar1.setTime(deadLineTime);
				Calendar  today = Calendar.getInstance();
				double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
				node.put("days", days);
			}
			else
			{
				node.put("days", 0);
			}
			*/
			
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);

		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getProjectInfoWx")
    public @ResponseBody Map<String,Object> getProjectInfoWx(@RequestParam String user_id, @RequestParam String project_id) {
		Map<String,Object> data = new HashMap<String, Object>();
			
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);

        Project project = projectService.getProjectInfo(paramMap);
        if(project == null)
        {
        	data.put("State", "Fail");
    		data.put("info", null);
    		return data;
        }

		JSONObject node = new JSONObject(); 
		node.put("task_id", project.getProjectId());
		node.put("task_name", project.getTitle());
		node.put("detail", project.getDetail());
		node.put("salary", project.getSalary());
		node.put("contact", project.getContact());
		node.put("rule", project.getRule());
		DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
		node.put("create_time", formattmp.format(project.getCreateTime()));
		node.put("start_date", formattmp.format(project.getStartTime()));
		node.put("end_date", formattmp.format(project.getDeadLineTime()));
		node.put("task_head", project.getHeadImg());
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(project.getDeadLineTime());
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(project.getStartTime());
		
		Calendar  today = Calendar.getInstance();
		

		//0 ---未开始   1----进行中   2---已结束
		if(calendar2.getTimeInMillis() > today.getTimeInMillis())
		{
			node.put("remain_days", -1);
			node.put("state", 0); //未开始
		}
		else
		{
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("projectId", project.getProjectId());
			int collectCount = projectService.getCollectCounts(paramMap2);
			
			double days = ( calendar1.getTimeInMillis() - today.getTimeInMillis())/(1000 * 60 * 60 * 24);
			if(days < 0 || (project.getCount() >0 && collectCount >= project.getCount()))
			{
				node.put("state", 2); //已结束
			}
			else{
				node.put("state", 1); //进行中
			}
			node.put("remain_days", days);
		}
		
		node.put("region", project.getRegion());
		node.put("count", project.getCount());
		node.put("link", project.getLink());
		if (project.getCreateUserId().equals(user_id))
		{
			node.put("my_create", true);
			
			//返回项目数据信息
			int followers = projectService.getFollowerCounts(paramMap);
			int collects = projectService.getCollectCounts(paramMap);
			node.put("followers", followers - 1);
			node.put("collects", collects);
		}
		else
		{
			node.put("my_create", false);
		}
		
		Users user = userService.selectByUserId(project.getCreateUserId());
		node.put("create_userName", user.getNickname());
		node.put("create_userHead", user.getImgUrl());
		
		JSONArray specArr = JSON.parseArray(project.getSpecImgs());
		node.put("specImgarr", specArr);
		JSONArray operArr = JSON.parseArray(project.getOperImgs());
		node.put("operImgarr", operArr);
		node.put("addVideoarr", JSON.parseArray(project.getAddVideos()));
		
		if(user_id.equals("0"))//用户未登录
		{
			node.put("follow", false);
		}
		else
		{
			//查看我是否已经关注了项目
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("followerId", user_id);
			paramMap2.put("projectId", project_id);
			
			Follow result = userService.checkFollow(paramMap2);
			if(result != null && !project.getCreateUserId().equals(user_id))
			{
				node.put("follow", true);
			}
			else
			{
				node.put("follow", false);
			}
		}
		
		data.put("State", "Success");
		data.put("data", node);				
		return data;
		
	}
	
	@RequestMapping("/createProjectWx")
    public @ResponseBody Map<String,Object> createProjectWx(@RequestParam String user_id,@RequestParam String type,@RequestParam String title,
    		@RequestParam String start_date, @RequestParam String end_date, @RequestParam String rule, @RequestParam String salary, @RequestParam String contact, 
    		@RequestParam String region, @RequestParam String detail, @RequestParam Integer count, @RequestParam String link) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		Calendar calendar=Calendar.getInstance();
		
		String projectId = String.valueOf(calendar.getTimeInMillis());
		
		paramMap.put("projectId", projectId);
		paramMap.put("type", "推手任务");
		paramMap.put("title", title);
		paramMap.put("salary", salary);
		paramMap.put("contact", contact);
		paramMap.put("detail", detail);
		paramMap.put("createTime", new Date());
		paramMap.put("createUserId", user_id);
		paramMap.put("headImg", "");
		paramMap.put("follow", 0);
		paramMap.put("startTime", start_date);
		paramMap.put("deadLineTime", end_date);
		paramMap.put("rule", rule);
		paramMap.put("region", region);
		logger.info("create count:" + count);
		if(count > 0)
		{
			paramMap.put("count", count);
		}
		paramMap.put("link", link);
		
		paramMap.put("addImgs", "");
		//状态1--创建 , 2--结束  
		paramMap.put("status", 1);
        int flag = projectService.createProject(paramMap);
        
        if(flag != -1 && flag !=0)
        {
        	Map<String, Object> paramMap2=new HashMap<String, Object>();
    		paramMap2.put("followerId",user_id);
    		paramMap2.put("projectId",projectId);
        	userService.follow(paramMap2);
        	JSONObject data = new JSONObject();
        	data.put("project_id", projectId);
        	
        	map.put("State", "Success");
        	map.put("data", data);	
        }
        else
        {
        	map.put("State", "False");
        	map.put("data", null);	
        }
								
		return map;	
	}
	
	/*@RequestMapping("/updateProjectImgWx")
    public @ResponseBody Map<String,Object> updateProjectImgWx(@RequestParam MultipartFile[] image,HttpServletRequest request)throws Exception{
		
		Map<String,Object> map = new HashMap<String, Object>();
		//获取文件需要上传到的路径
        String path = request.getSession().getServletContext().getRealPath("/");
        path = path.concat("JiMuImage/project/");
        
        String projectId = request.getParameter("project_id");
   
        
   
        List<String> imageUrl = new ArrayList<String>();
        for (MultipartFile file : image) 
        {
            if (file.isEmpty()) {
                System.out.println("文件未上传");
                imageUrl.add(null);
            } else 
            {
                String contentType = file.getContentType();

                if (contentType.startsWith("image"))
                {
                    String newFileName = projectId + ".jpg";
                    FileUtils.copyInputStreamToFile(file.getInputStream(),new File(path, newFileName)); // 写文件
                    imageUrl.add(Constants.localIp + "/project/" + newFileName);
                }
            }
        }
        System.out.println("project_id="+ projectId);
        System.out.println("headImg="+ imageUrl.get(0));
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", projectId);
		paramMap.put("headImg", imageUrl.get(0));
        int flag = projectService.updateProjectHeadImg(paramMap);
        
        if(flag != -1 && flag !=0)
        {  
        	JSONObject data = new JSONObject();
        	data.put("project_id", projectId);
        	map.put("State", "Success");
        	map.put("data", data);	
        	return map;	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传项目图片失败");	
        }
    	return map;	
	}*/
	
	@RequestMapping("/updateProjectImgWx")
    public @ResponseBody Map<String,Object> updateProjectImgWx(@RequestParam String project_id,@RequestParam String head_img)throws Exception{
		
		Map<String,Object> map = new HashMap<String, Object>();
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		paramMap.put("headImg", "https://" + head_img);
        int flag = projectService.updateProjectHeadImg(paramMap);
        
        if(flag != -1 && flag !=0)
        {  
        	JSONObject data = new JSONObject();
        	data.put("project_id", project_id);
        	map.put("State", "Success");
        	map.put("data", data);	
        	return map;	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传项目图片失败");	
        }
    	return map;	
	}
	
	
	@RequestMapping("/updateProjectInfoImgWx")
    public @ResponseBody Map<String,String> updateProjectInfoImgWx(@RequestParam String project_id,@RequestParam String info_img_url)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		 
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		
		Project project = projectService.getProjectInfo(paramMap);
		
		JSONArray arr;
		
		if(project.getAddImgs() != null && !project.getAddImgs().isEmpty())
		{
			arr = JSON.parseArray(project.getAddImgs());
		}
		else
		{
			arr = new JSONArray();
		}
		
		
		JSONObject obj = new JSONObject();
		obj.put("url","https://" + info_img_url);
		arr.add(obj);
		paramMap.put("addImgs", arr.toJSONString());
        int flag = projectService.updateProjectAddImgs(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传图片成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传图片失败");	
        }
    	return map;	
	}
	
	@RequestMapping("/updateProjectInfoOperImgWx")
    public @ResponseBody Map<String,String> updateProjectInfoOperImgWx(@RequestParam String project_id,@RequestParam String info_img_urls)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		 
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		
		Project project = projectService.getProjectInfo(paramMap);
		
		JSONArray arr;
		
		if(project.getOperImgs() != null && !project.getOperImgs().isEmpty())
		{
			arr = JSON.parseArray(project.getOperImgs());
		}
		else
		{
			arr = new JSONArray();
		}
		
		for (String infoImgUrl : info_img_urls.split(",")) {
			JSONObject obj = new JSONObject();
			obj.put("url","https://" + infoImgUrl);
			arr.add(obj);
		}
		
		if (arr.size() > 3) {
			map.put("State", "False");
        	map.put("info", "最多上传3张照片");	
        	return map;
		}
		
		paramMap.put("operImgs", arr.toJSONString());
        int flag = projectService.updateProjectOperImgs(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传图片成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传图片失败");	
        }
    	return map;	
	}
	
	@RequestMapping("/updateProjectInfoSpecImgWx")
    public @ResponseBody Map<String,String> updateProjectInfoSpecImgWx(@RequestParam String project_id,@RequestParam String info_img_urls)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		 
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		
		Project project = projectService.getProjectInfo(paramMap);
		
		JSONArray arr;
		
		if(project.getSpecImgs() != null && !project.getSpecImgs().isEmpty())
		{
			arr = JSON.parseArray(project.getSpecImgs());
		}
		else
		{
			arr = new JSONArray();
		}
		
		
		
		for (String infoImgUrl : info_img_urls.split(",")) {
			JSONObject obj = new JSONObject();
			obj.put("url","https://" + infoImgUrl);
			arr.add(obj);
		}
		
		if (arr.size() > 5) {
			map.put("State", "False");
        	map.put("info", "最多上传5张照片");	
        	return map;
		}
		
		paramMap.put("specImgs", arr.toJSONString());
        int flag = projectService.updateProjectSpecImgs(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传图片成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传图片失败");	
        }
    	return map;	
	}
	
	@RequestMapping("/updateProjectInfoVideoWx")
    public @ResponseBody Map<String,String> updateProjectInfoVideoWx(@RequestParam String project_id,@RequestParam String info_video_url)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		 
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		
		Project project = projectService.getProjectInfo(paramMap);
		
		JSONArray arr;
		
		if(project.getAddVideos() != null && !project.getAddVideos().isEmpty())
		{
			arr = JSON.parseArray(project.getAddVideos());
		}
		else
		{
			arr = new JSONArray();
		}
		
		
		JSONObject obj = new JSONObject();
		obj.put("url","https://" + info_video_url);
		arr.add(obj);
		paramMap.put("addVideos", arr.toJSONString());
        int flag = projectService.updateProjectAddVideos(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传视频成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传视频失败");	
        }
    	return map;	
	}
	
	@RequestMapping(value="deleteProjectWx")
	public @ResponseBody Map<String, Object> deleteProjectWx(@RequestParam String project_id,@RequestParam String user_id){
		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Object> paramMap=new HashMap<String, Object>();
		paramMap.put("createUserId",user_id);
		paramMap.put("projectId",project_id);
		paramMap.put("status",0);
		
		int flag = projectService.deleteProject(paramMap);
		if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "删除任务成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "删除任务失败");	
        }
		
		return map;
	}
	
	
	@RequestMapping("/sendProjdecCommentWx")
    public @ResponseBody Map<String,Object> sendProjdecCommentWx(@RequestParam String user_id,@RequestParam String project_id,@RequestParam String comment) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", user_id);
		paramMap.put("projectId", project_id);
		paramMap.put("comment", comment);
		paramMap.put("commentTime", new Date());
        int flag = projectService.commitProjectComment(paramMap);
        if(flag != -1 && flag !=0)
        {
  
        	map.put("State", "Success");
        	map.put("data", null);	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "提交评论失败");	
        }
								
		return map;	
	}
	
	/*
	@RequestMapping("/getCommentListWx")
    public @ResponseBody Map<String,Object> getCommentListWx(@RequestParam String project_id,@RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
		
        List<ProjectComment> commentlist = projectService.getCommentList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", commentlist.size());
				
		for (ProjectComment comment: commentlist)
		{
			JSONObject node = new JSONObject(); 
			
			node.put("user_id", comment.getUserId());
			node.put("comment", comment.getComment());
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			node.put("time", formattmp.format(comment.getCommentTime()));
			node.put("user_head", comment.getUserHead());
			node.put("user_name", comment.getUserName());
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	
	@RequestMapping("/getUnivListWx")
    public @ResponseBody Map<String,Object> getUnivListWx(@RequestParam int flag) {
		
		JSONArray provinceList = new JSONArray(); 
		JSONArray univList = new JSONArray(); 
        
        List<University> provicelist = projectService.getProviceList();
        for(University provTmp: provicelist)
        {
        	JSONObject provice = new JSONObject();
        	
        	provice.put("proviceId", provTmp.getProviceId());
        	provice.put("name", provTmp.getProvice());
        	
        	provinceList.add(provice);
        	
        	Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("proviceId", provTmp.getProviceId());
    		paramMap.put("chooseFlag", flag);
        	
        	List<University> universitylist = projectService.getUnivList(paramMap);
   
        	JSONArray univs = new JSONArray();
            for(University univTmp: universitylist)
            {
            	
            	JSONObject tmp = new JSONObject();
            	tmp.put("id", univTmp.getUnivId());
            	tmp.put("name", univTmp.getUniversity());
            	univs.add(tmp);
            }
            
            JSONObject univ = new JSONObject();
        	univ.put("proviceId", provTmp.getProviceId());
            univ.put("univs", univs);
            univList.add(univ);
        }
	
		JSONObject rtn = new JSONObject();
		rtn.put("provinceList", provinceList);
		rtn.put("univList", univList);
						
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	*/
	
	@RequestMapping("/getShareQrWx")
    public @ResponseBody Map<String,Object> getShareQrWx(@RequestParam String project_id, HttpServletRequest request) throws Exception {
		Map<String,Object> data = new HashMap<String, Object>();
		
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
        Project project = projectService.getProjectInfo(paramMap);
        
        if(project == null)
        {
        	data.put("State", "Fail");
    		data.put("info", "生成二维码失败");				
    		return data;
        }
        
        if(project.getQrCode() != null && project.getQrCode().length() > 0)
        {
        	JSONObject rtn = new JSONObject();
            rtn.put("path", project.getQrCode());
    		data.put("State", "Success");
    		data.put("data", rtn);				
    		return data;
        }
        else
        {
        	//接口B：生成无限制但需要先发布的小程序
    		String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
    		
    		//接口C：调试用
    		//String url = "https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode";

    		String access_token = (String) PayUtil.getAccessToken().get("access_token");
    		//取access_token
        
    		url = url + "?access_token=" + access_token;
    		
    		Map<String, Object> params = new HashMap<>();
            params.put("scene", "id=" + project_id);
            params.put("page", "pages/task/detail");
            //params.put("path", "pages/task/detail?id=" + project_id);
            params.put("width", 160);
            String body = JSON.toJSONString(params);
               
            String resultstr = HttpRequest.httpPostWithJSONQr(url,body, project_id);
            if(resultstr == null)
            {
            	data.put("State", "Fail");
        		data.put("info", "生成二维码失败");				
        		return data;
            }
            
            JSONObject rtn = new JSONObject();
            String putpath = Constants.QINIU_IP + resultstr;
            rtn.put("path", putpath);
            
            paramMap.put("qrCode", putpath);
            projectService.updateProjectQrCode(paramMap);
            
    		data.put("State", "Success");
    		data.put("data", rtn);				
    		return data;
        }
	}
	
	
	@RequestMapping("/getTuiguangQrWx")
    public @ResponseBody Map<String,Object> getTuiguangQrWx(@RequestParam String project_id, @RequestParam String user_id, HttpServletRequest request) throws Exception {
		Map<String,Object> data = new HashMap<String, Object>();
		
		//https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/qr-code.html
		//https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html
		//接口B：生成无限制但需要先发布的小程序
		//String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
		
		//接口A：调试用
		String url = "https://api.weixin.qq.com/wxa/getwxacode";

		String access_token = (String) PayUtil.getAccessToken().get("access_token");
		//取access_token
    
		url = url + "?access_token=" + access_token;
		
		Map<String, Object> params = new HashMap<>();
		//params.put("scene", "id=" + project_id);
		//params.put("page", "pages/customer/qrIn");
        params.put("path", "pages/customer/qrIn?id=" + project_id + "&from_user_id=" + user_id);
        params.put("width", 160);
        String body = JSON.toJSONString(params);
           
        String resultstr = HttpRequest.httpPostWithJSONQr(url,body, project_id);
        if(resultstr == null)
        {
        	data.put("State", "Fail");
    		data.put("info", "生成二维码失败");				
    		return data;
        }
        
        JSONObject rtn = new JSONObject();
        String putpath = Constants.QINIU_IP + resultstr;
        rtn.put("path", putpath);
        
        
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
	}
	
	@RequestMapping("/getQiniuTokenWx")
    public @ResponseBody Map<String,Object> getQiniuTokenWx()throws Exception{
		Map<String,Object> data = new HashMap<String, Object>();
		
		Auth auth = Auth.create(Constants.QINIU_AK, Constants.QINIU_SK);
		String upToken = auth.uploadToken(Constants.QINIU_BUCKET);
		
		JSONObject rtn = new JSONObject();
        rtn.put("upToken", upToken);
        
		data.put("State", "Success");
		data.put("data", rtn);			
    	return data;	
	}
	
	@RequestMapping("/uploadCollectWx")
    public @ResponseBody Map<String,Object> uploadCollectWx(@RequestParam String project_id,@RequestParam String user_id,@RequestParam String name,
    		@RequestParam String phone, @RequestParam String content, @RequestParam String latitude, @RequestParam String longitude
    		, @RequestParam String address, @RequestParam String detail) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		Calendar calendar=Calendar.getInstance();
		
		String id = String.valueOf(calendar.getTimeInMillis());
		
		paramMap.put("id", id);
		paramMap.put("projectId", project_id);
		paramMap.put("upUserId", user_id);
		paramMap.put("name", name);
		paramMap.put("phone", phone);
		paramMap.put("content", content);
		paramMap.put("latitude", latitude);
		paramMap.put("longitude", longitude);
		
		address = address.replaceAll("&#40;", "(");
		detail = detail.replaceAll("&#40;", "(");
		paramMap.put("address", address);
		paramMap.put("adDetail", detail);
		paramMap.put("createTime", new Date());
		
		paramMap.put("status", 1);
		
		JSONArray records = new JSONArray();
		JSONObject record = new JSONObject();
		record.put("status", 1);
		record.put("time", new Date());
		records.add(record);
		paramMap.put("record",records.toString());
		
        int flag = projectService.addCollect(paramMap);
        
        if(flag != -1 && flag !=0)
        {
        	JSONObject data = new JSONObject();
        	data.put("collectId", id);
        	
        	map.put("State", "Success");
        	map.put("data", data);	
        }
        else
        {
        	map.put("State", "False");
        	map.put("data", null);	
        }
								
		return map;	
	}
	
	@RequestMapping("/uploadCollectFileWx")
    public @ResponseBody Map<String,String> uploadCollectFileWx(@RequestParam String collect_id,@RequestParam String file)throws Exception{
		
		Map<String,String> map = new HashMap<String, String>();
		 
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", collect_id);
		
		Collect project = projectService.getCollect(paramMap);
		
		JSONArray arr;
		
		if(project.getFiles() != null && !project.getFiles().isEmpty())
		{
			arr = JSON.parseArray(project.getFiles());
		}
		else
		{
			arr = new JSONArray();
		}
		
		//JSONObject obj = new JSONObject();
		//obj.put("url","http://" + file);
		arr.add("http://" + file);
		paramMap.put("files", arr.toJSONString());
        int flag = projectService.updateCollectFiles(paramMap);
        
        if(flag != -1 && flag !=0)
        {       	
        	map.put("State", "Success");
        	map.put("data", "上传图片成功");	
        }
        else
        {
        	map.put("State", "False");
        	map.put("info", "上传图片失败");	
        }
    	return map;	
	}
	
	@RequestMapping("/getCollectsWx")
    public @ResponseBody Map<String,Object> getCollectsWx(@RequestParam String user_id, @RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 5);
		paramMap.put("limit", 5);
		paramMap.put("upUserId", user_id);
        List<Collect> collectList = projectService.getCollectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", collectList.size());
				
		for (Collect collect: collectList)
		{
			JSONObject node = new JSONObject();
			node.put("id", collect.getId());
			node.put("task_id", collect.getProjectId());
			node.put("status", collect.getStatus());
			
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("projectId", collect.getProjectId());

	        Project project = projectService.getProjectInfo(tmp);
	        if(project == null)
	        {
	        	node.put("task_name", "");
	        	node.put("salary", "--");
	        }
	        else
	        {
	        	node.put("task_name", project.getTitle());
	        	node.put("salary", project.getSalary());
	        }
			
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
			node.put("create_time", formattmp.format(collect.getCreateTime()));
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	@RequestMapping("/getImgDetailInfoWx")
    public @ResponseBody Map<String,Object> getImgDetailInfoWx(@RequestParam String collect_id, @RequestParam String img_url) {
		Map<String,Object> data = new HashMap<String, Object>();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", collect_id);
		
		Collect collect = projectService.getCollect(paramMap);
		
		if (collect == null)
		{
			data.put("State", "Fail");
			data.put("info", "查询采集信息失败");	
			return data;
		}
		
		JSONObject obj = new JSONObject();
		
		Users user = userService.selectByUserId(collect.getUpUserId());
		if (user == null)
		{
			data.put("State", "Fail");
			data.put("info", "查询上传用户信息失败");	
			return data;
		}
		else{
			obj.put("upUser_name", user.getNickname());
			obj.put("upUser_head", user.getImgUrl());
		};
		
		obj.put("files", JSONObject.parse(collect.getFiles()));
		obj.put("time", collect.getCreateTime());
		if(collect.getAddress() != "" && collect.getAddress().length() > 0)
		{
			//AddressDTO addressDTO = JSONObject.toJavaObject((JSONObject)etifInfo.get("address_info"),AddressDTO.class);
			obj.put("latitude", collect.getLatitude());
			obj.put("longitude", collect.getLongitude());
			obj.put("address", collect.getAddress());
			obj.put("detail",collect.getAdDetail());
		}
		else{
			obj.put("latitude", user.getLatitude());
			obj.put("longitude",user.getLongitude());
			obj.put("address", "未知地点");
			obj.put("detail", "未知地点");
		}
		
		data.put("State", "Success");
		data.put("data", obj);				
		return data;
	}
	
	@RequestMapping("/getTaskCollectSummaryWx")
    public @ResponseBody Map<String,Object> getTaskCollectSummaryWx(@RequestParam String project_id) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", project_id);
        int all = projectService.getCollectCounts(paramMap);
        
        //3--被打回  2---审核通过  1---待审核
        
        paramMap.put("status", 1);
        int toCheck = projectService.getCollectCountsByStatus(paramMap);
        
        paramMap.put("status", 2);
        int pass = projectService.getCollectCountsByStatus(paramMap);
        
        paramMap.put("status", 3);
        int reject = projectService.getCollectCountsByStatus(paramMap);
		
		JSONObject obj = new JSONObject();
		obj.put("all", all);
		obj.put("toCheck", toCheck);
		obj.put("pass", pass);
		obj.put("reject", reject);
		
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", obj);				
		return data;
		
	}
	
	@RequestMapping("/selectCollectsByuserIdWx")
    public @ResponseBody Map<String,Object> selectCollectsByuserIdWx(@RequestParam String project_id, @RequestParam String user_id, @RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 5);
		paramMap.put("limit", 5);
		paramMap.put("upUserId", user_id);
		paramMap.put("projectId", project_id);
        List<Collect> collectList = projectService.getTaskCollectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", collectList.size());
				
		for (Collect collect: collectList)
		{
			JSONObject node = new JSONObject();
			node.put("id", collect.getId());
			node.put("task_id", collect.getProjectId());
			node.put("status", collect.getStatus());
			
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("projectId", collect.getProjectId());

	        Project project = projectService.getProjectInfo(tmp);
	        if(project == null)
	        {
	        	node.put("task_name", "");
	        	node.put("salary", "--");
	        }
	        else
	        {
	        	node.put("task_name", project.getTitle());
	        	node.put("salary", project.getSalary());
	        }
			
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
			node.put("create_time", formattmp.format(collect.getCreateTime()));
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	
	@RequestMapping("/getTaskCollectsWx")
    public @ResponseBody Map<String,Object> getTaskCollectsWx(@RequestParam String project_id, @RequestParam Integer type, @RequestParam Integer page) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 5);
		paramMap.put("limit", 5);
		paramMap.put("projectId", project_id);
		
		if (type == 1){
			paramMap.put("status", 1);//待审核
		}
		if (type == 2){
			paramMap.put("status", 2);//审核通过
		}
		if (type == 3){
			paramMap.put("status", 3);//被打回
		}
        List<Collect> collectList = projectService.getTaskCollectList(paramMap);
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", collectList.size());
				
		for (Collect collect: collectList)
		{
			JSONObject node = new JSONObject();
			node.put("id", collect.getId());
			node.put("task_id", collect.getProjectId());
			node.put("status", collect.getStatus());
			
			Users user = userService.selectByUserId(collect.getUpUserId());
			if(user != null)
			{
				node.put("nickName", user.getNickname());
			}
			
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("projectId", collect.getProjectId());

	        Project project = projectService.getProjectInfo(tmp);
	        if(project == null)
	        {
	        	node.put("task_name", "");
	        	node.put("salary", "--");
	        }
	        else
	        {
	        	node.put("task_name", project.getTitle());
	        	node.put("salary", project.getSalary());
	        }
			
			DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd");  
			node.put("create_time", formattmp.format(collect.getCreateTime()));
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
	
	/**
	 * 获取具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/getCollectInfoWx")
	public @ResponseBody Map<String, Object> getCollectInfoWx(
			@RequestParam String  user_id, @RequestParam String collect_id) {
		Map<String, Object> map = new HashMap<String, Object>();

		JSONObject obj = new JSONObject();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id",collect_id);
			Collect collect = projectService.getCollect(paramMap);
			
			if (collect == null)
			{
				map.put("State", "Fail");
				map.put("info", "查询详细信息失败");	
				return map;
			}
			
			Map<String, Object> tmp = new HashMap<String, Object>();
			tmp.put("projectId", collect.getProjectId());

	        Project project = projectService.getProjectInfo(tmp);
	        if(project == null)
	        {
	        	obj.put("task_name", "");
	        	obj.put("salary", "--");
	        }
	        else
	        {
	        	obj.put("task_name", project.getTitle());
	        	obj.put("salary", project.getSalary());
	        }
		
			obj.put("id", collect.getId());
			obj.put("status", collect.getStatus());
			obj.put("create_time", collect.getCreateTime());
			obj.put("name", collect.getName());
			obj.put("phone", collect.getPhone());
			obj.put("content", collect.getContent());
			
			Users user = userService.selectByUserId(collect.getUpUserId());
			if (user != null)
			{
				obj.put("up_user_name", user.getNickname());
			}
			if(collect.getFiles() != null && !collect.getFiles().isEmpty())
			{
				JSONArray files = JSON.parseArray(collect.getFiles());
				obj.put("files", files);
			}
			else
			{
				obj.put("files", "");
			}
			
			if(collect.getUpUserId().equals(user_id))
			{
				obj.put("type", 1);
			}
			else
			{
				obj.put("type", 0);
			}
			
			JSONArray records = JSON.parseArray(collect.getRecord());
			JSONArray arr = new JSONArray();
			for(int i = records.size()-1; i >= 0; i--)
			{
				JSONObject record = new JSONObject();
				short state = records.getJSONObject(i).getShort("status");
				String reason = records.getJSONObject(i).getString("reason");

				DateFormat formattmp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				String timeStr = formattmp.format(records.getJSONObject(i).getDate("time"));
				if( state == 0){
					record.put("name", "取消");
					record.put("type", state);
					record.put("time", timeStr);
					record.put("reason", reason);
				}
				else if(state == 1){
					record.put("name", "待审核");
					record.put("type", state);
					record.put("time", timeStr);
					record.put("reason", reason);
				}
				else if(state == 2){
					record.put("name", "审核通过");
					record.put("type", state);
					record.put("time", timeStr);
					record.put("reason", reason);
				}
				else if(state == 3){
					record.put("name", "被打回");
					record.put("type", state);
					record.put("time", timeStr);
					record.put("reason", reason);				
				}
				else if(state == 9){
					record.put("name", "预删除");
					record.put("type", state);
					Map<String, Object> tmp5 = new HashMap<String, Object>();
					tmp5.put("删除", "系统即将自动删除信息");
					record.put("list", tmp5);
				}
				
				arr.add(record);
			}
			
			JSONObject data = new JSONObject();
			data.put("info", obj);
			data.put("state", arr);

			map.put("State", "Success");
			map.put("data", data);	
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	
	/**
	 * 获取具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/cancelCollectWx")
	public @ResponseBody Map<String, Object> cancelCollectWx(
			@RequestParam String  user_id, @RequestParam String collect_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id",collect_id);
			Collect collect = projectService.getCollect(paramMap);
			
			if (collect == null)
			{
				map.put("State", "Fail");
				map.put("info", "查询详细信息失败");	
				return map;
			}
			
			
			JSONArray records = JSON.parseArray(collect.getRecord());
			
			
			JSONObject record = new JSONObject();
			record.put("status", 0);
			record.put("time", new Date());
			record.put("reason", "用户取消");
			records.add(record);
			paramMap.put("record",records.toString());
			
			paramMap.put("status",0);
			
			int flag = projectService.updateCollectStatus(paramMap);
			

			if(flag != -1 && flag !=0)
	        {       	
	        	map.put("State", "Success");
	        	map.put("data", "");	
	        }
	        else
	        {
	        	map.put("State", "False");
	        	map.put("info", "取消失败");	
	        }
			
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/passCollectWx")
	public @ResponseBody Map<String, Object> passCollectWx(
			@RequestParam String  user_id, @RequestParam String collect_id) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id",collect_id);
			Collect collect = projectService.getCollect(paramMap);
			
			if (collect == null)
			{
				map.put("State", "Fail");
				map.put("info", "查询详细信息失败");	
				return map;
			}
			
			
			JSONArray records = JSON.parseArray(collect.getRecord());
			
			
			JSONObject record = new JSONObject();
			record.put("status", 2);
			record.put("time", new Date());
			records.add(record);
			paramMap.put("record",records.toString());
			
			paramMap.put("status",2);
			
			//先查询用户和工程信息防止后面出错
			Users user = userService.selectByUserId(collect.getUpUserId());
        	
        	Map<String, Object> paramMap2 = new HashMap<String, Object>();
			paramMap2.put("projectId", collect.getProjectId());
        	Project project = projectService.getProjectInfo(paramMap2);
        	
        	if(user == null || project == null)
        	{
        		map.put("State", "False");
	        	map.put("info", "用户信息错误");	
	        	return map;
        	}
			
			int flag = projectService.updateCollectStatus(paramMap);
			

			if(flag != -1 && flag !=0)
	        {       	
	        	
	        	Map<String, Object> paramMap3 = new HashMap<String, Object>();
				paramMap3.put("userId",collect.getUpUserId());
				paramMap3.put("balance",user.getBalance() + Float.parseFloat(project.getSalary()));
	        	
	        	int result = userService.updateBalance(paramMap3);
	        	if(result != -1 && result !=0)
	        	{
	        		map.put("State", "Success");
		        	map.put("data", "");	
	        	}
	        	else
	        	{
	        		map.put("State", "False");
		        	map.put("info", "处理失败");	
	        	}
	        }
	        else
	        {
	        	map.put("State", "False");
	        	map.put("info", "处理失败");	
	        }
			
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	/**
	 * 获取具体信息
	 * 
	 * @param phoneId
	 *            ,status
	 * @return
	 */
	@RequestMapping("/rejectCollectWx")
	public @ResponseBody Map<String, Object> rejectCollectWx(
			@RequestParam String  user_id, @RequestParam String collect_id, @RequestParam String reason) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("id",collect_id);
			Collect collect = projectService.getCollect(paramMap);
			
			if (collect == null)
			{
				map.put("State", "Fail");
				map.put("info", "查询详细信息失败");	
				return map;
			}
			
			
			JSONArray records = JSON.parseArray(collect.getRecord());
			
			
			JSONObject record = new JSONObject();
			record.put("status", 3);
			record.put("time", new Date());
			record.put("reason", reason);
			records.add(record);
			paramMap.put("record",records.toString());
			
			paramMap.put("status",3);
			
			int flag = projectService.updateCollectStatus(paramMap);
			

			if(flag != -1 && flag !=0)
	        {       	
	        	map.put("State", "Success");
	        	map.put("data", "");	
	        }
	        else
	        {
	        	map.put("State", "False");
	        	map.put("info", "取消失败");	
	        }
			
			return map;

		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		map.put("State", "False");
		map.put("data", null);	
		return map;
	}
	
	@RequestMapping("/getFollowersWx")
    public @ResponseBody Map<String,Object> getFollowersWx(@RequestParam String project_id, @RequestParam String start_date,
    		@RequestParam String end_date, @RequestParam Integer page, @RequestParam String user_id) {
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", page * 10);
		paramMap.put("limit", 10);
		if (start_date != null && start_date.length() > 0){
			paramMap.put("startDate", start_date);
		}
		
		if (end_date != null && end_date.length() > 0){
			paramMap.put("endDate", end_date);
		}
		paramMap.put("projectId", project_id);
		
        List<Followers> followerList = projectService.getFollowers(paramMap);
        
		JSONArray jsonarray = new JSONArray(); 
		JSONObject rtn = new JSONObject();
		rtn.put("count", followerList.size());
				
		for (Followers follower: followerList)
		{
			JSONObject node = new JSONObject(); 
			node.put("user_id", follower.getUserId());
			node.put("nickname", follower.getNickName());
			node.put("count", follower.getCount());
			node.put("head_img", follower.getHeadImg());
			
			jsonarray.add(node);
		}
		rtn.put("list", jsonarray);
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("State", "Success");
		data.put("data", rtn);				
		return data;
		
	}
}
