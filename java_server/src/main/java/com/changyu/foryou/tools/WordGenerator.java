package com.changyu.foryou.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.changyu.foryou.controller.OrderControler;

import freemarker.template.Configuration;
import freemarker.template.Template;

 
public class WordGenerator {
	private static Configuration configuration = null;
	private static Map<String, Template> allTemplates = null;
	
	private static final Logger logger = LoggerFactory.getLogger(WordGenerator.class);
 
	static {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		configuration.setClassForTemplateLoading(WordGenerator.class, "/");
		allTemplates = new HashMap<>(); // Java 7 钻石语法  
		try {
			allTemplates.put("order", configuration.getTemplate("order.ftl"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
 
	private WordGenerator() {
		throw new AssertionError();
	}
 
	public static File createDoc(Map<?, ?> dataMap,String path, String name, String type) {
		
        File foler =new File(path);    

        if (!foler.exists()  && !foler.isDirectory())      
        {        
        	foler.mkdir();    
        } 

		File f = new File(path + name);
		Template t = allTemplates.get(type);
		try {
			// 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开  
			Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			t.process(dataMap, w);
			w.flush();
			w.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("[WordGenerator Exception]createDoc:" + name + ",Exception:" + ex.getMessage());	
			throw new RuntimeException(ex);	
		}
		return f;
	}
	
	public static String GetImageStrFromUrl(String imgURL) {
		String rtnString = "";
		URL url = null;  
        InputStream inStream = null;   
        ByteArrayOutputStream outStream = null;  
        HttpURLConnection conn = null;
        
        if(imgURL == null || imgURL.length() == 0)
        {	
        	return rtnString;
        }
        
		try {
			// 创建URL
			url = new URL(imgURL);
			// 创建链接
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);

			inStream = conn.getInputStream();
			outStream = new ByteArrayOutputStream();  

            //创建一个Buffer字符串  
            byte[] buffer = new byte[1024];  
            //每次读取的字符串长度，如果为-1，代表全部读取完毕  
            int len = 0;  
            //使用一个输入流从buffer里把数据读取出来  
            while( (len=inStream.read(buffer)) != -1 ){  

                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
                outStream.write(buffer, 0, len);  
            }  
            rtnString = Base64.getEncoder().encodeToString(outStream.toByteArray()); 
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{  
            if(inStream != null)  
            {  
                try {  
                	inStream.close();  

                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  

            if(outStream != null)  
            {  
                try {  
                    outStream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  

            if(conn != null)  
            {  
            	conn.disconnect();  
            }  
        }
		
		return rtnString;
	}
	
	public static String GetImageStrFromPath(String imgPath) {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgPath);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 返回Base64编码过的字节数组字符串
		return data != null ? Base64.getEncoder().encodeToString(data) : "";
	}
}
