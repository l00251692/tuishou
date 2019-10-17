package com.changyu.foryou.listener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.changyu.foryou.tools.Constants;
import com.changyu.foryou.tools.ScanOrderManager;
import com.changyu.foryou.tools.TempFileManager;

/**
 * 时间监听器
 * 
 * @author xiaoqun.yi
 */
public class TimerListener implements ServletContextListener {
		
	private Timer timerFile;
	private Timer timerOrder;
	//private SystemTaskTest systemTask;
	private static String every_time_run = Constants.TIMEER_TIMCE_CICLE;
	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
 
	// 监听器初始方法，不要弄错了
	public void contextInitialized(ServletContextEvent sce) {
 
		timerFile = new Timer();
		timerOrder = new Timer();
		
		String path= System.getProperty("user.dir").concat("/File/");	
		/*
		SystemTaskTest systemTask = new SystemTaskTest(path, sce.getServletContext());
		
		NFDFlightDataTimerTask task = new NFDFlightDataTimerTask(sce.getServletContext());
		try {
			Long time = Long.parseLong(every_time_run) * 60*1000;// 循环执行的时间,单位为ms
			// 第一个参数是要运行的代码,第二个参数是指定时间执行，只执行一次
			// timer.schedule(systemTask,time);
			// 第一个参数是要运行的代码，第二个参数是从什么时候开始运行，第三个参数是每隔多久在运行一次。重复执行
			timerFile.schedule(systemTask, 100000, time);
			sce.getServletContext().log("已经添加临时文件任务定时处理");
			
			//添加每天定时任务
			Calendar calendar = Calendar.getInstance(); 
            
	        //定制每日2:00执行方法 
	        calendar.set(Calendar.HOUR_OF_DAY, 2);
	        calendar.set(Calendar.MINUTE, 0);
	        calendar.set(Calendar.SECOND, 0);
	           
	        Date date=calendar.getTime(); //第一次执行定时任务的时间
	        
	        //如果第一次执行定时任务的时间 小于 当前的时间
	        //此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。循环执行的周期则以当前时间为准
	        if (date.before(new Date())) {
	        	sce.getServletContext().log("当前时间晚于凌晨，任务将于明天凌晨执行");
	        	Calendar startDT = Calendar.getInstance();
	            startDT.setTime(date);
	            startDT.add(Calendar.DAY_OF_MONTH, 1);	            
	            date = startDT.getTime();
	        }
	        
	        timerOrder.schedule(task,date,PERIOD_DAY);
	        sce.getServletContext().log("已经添加订单扫描任务定时处理");
	        
		} catch (Exception e) {
		}*/
	}
 
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			timerFile.cancel();
			timerOrder.cancel();
		} catch (Exception e) {
		}
	}
}
	
/**
 * 时间任务器
 * 
 * @author xiaoqun.yi
 */
class SystemTaskTest extends TimerTask {
	
	private ServletContext context;
	private String path;

	public SystemTaskTest(String path, ServletContext context) {
		this.path = path;
		this.context = context;
	}
	
	/**
	 * 把要定时执行的任务就在run中
	 */
	public void run() {	
		TempFileManager etf;	
		try {
			context.log("开始执行任务!");
			// 需要执行的代码
			etf = new TempFileManager(path);
			etf.run();		
			context.log("指定任务执行完成!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class NFDFlightDataTimerTask extends TimerTask {
    private ServletContext context;

	public NFDFlightDataTimerTask( ServletContext context) {
		this.context = context;
	}
 
    public void run() { 	
        try {
             //在这里写你要执行的内容
            ScanOrderManager sm = new ScanOrderManager();
            sm.run();
            
        } catch (Exception e) {
        	context.log("-------------扫描订单定时任务发生异常--------------");
        	e.printStackTrace();
        }
    }
     
}