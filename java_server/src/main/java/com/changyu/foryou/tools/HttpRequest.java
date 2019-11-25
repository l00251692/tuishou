package com.changyu.foryou.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL; 
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List; 
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP; 
 
/** 
 * Created by lsh on 2017/6/22. 
 */
public class HttpRequest { 
  /** 
   * 向指定URL发送GET方法的请求 
   * 
   * @param url 
   *      发送请求的URL 
   * @param param 
   *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 
   * @return URL 所代表远程资源的响应结果 
   */
  public static String sendGet(String url, String param) { 
    String result = ""; 
    BufferedReader in = null; 
    try { 
      String urlNameString = url + "?" + param; 
      URL realUrl = new URL(urlNameString); 
      // 打开和URL之间的连接 
      URLConnection connection = realUrl.openConnection(); 
      // 设置通用的请求属性 
      connection.setRequestProperty("accept", "*/*"); 
      connection.setRequestProperty("connection", "Keep-Alive"); 
      connection.setRequestProperty("user-agent", 
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); 
      // 建立实际的连接 
      connection.connect(); 
      // 获取所有响应头字段 
      Map<String, List<String>> map = connection.getHeaderFields(); 
      // 遍历所有的响应头字段 
      for (String key : map.keySet()) { 
        System.out.println(key + "--->" + map.get(key)); 
      } 
      // 定义 BufferedReader输入流来读取URL的响应 
      in = new BufferedReader(new InputStreamReader( 
          connection.getInputStream())); 
      String line; 
      while ((line = in.readLine()) != null) { 
        result += line; 
      } 
    } catch (Exception e) { 
      System.out.println("发送GET请求出现异常！" + e); 
      e.printStackTrace(); 
    } 
    // 使用finally块来关闭输入流 
    finally { 
      try { 
        if (in != null) { 
          in.close(); 
        } 
      } catch (Exception e2) { 
        e2.printStackTrace(); 
      } 
    } 
    return result; 
  } 
 
  /** 
   * 向指定 URL 发送POST方法的请求 
   * 
   * @param url 
   *      发送请求的 URL 
   * @param param 
   *      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。 
   * @return 所代表远程资源的响应结果 
   */
  public static String sendPost(String url, String param) { 
    PrintWriter out = null; 
    BufferedReader in = null; 
    String result = ""; 
    try { 
      URL realUrl = new URL(url); 
      // 打开和URL之间的连接 
      URLConnection conn = realUrl.openConnection(); 
      // 设置通用的请求属性 
      conn.setRequestProperty("accept", "*/*"); 
      conn.setRequestProperty("connection", "Keep-Alive"); 
      conn.setRequestProperty("user-agent", 
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); 
      // 发送POST请求必须设置如下两行 
      conn.setDoOutput(true); 
      conn.setDoInput(true); 
      // 获取URLConnection对象对应的输出流 
      out = new PrintWriter(conn.getOutputStream()); 
      // 发送请求参数 
      out.print(param); 
      // flush输出流的缓冲 
      out.flush(); 
      // 定义BufferedReader输入流来读取URL的响应 
      in = new BufferedReader( 
          new InputStreamReader(conn.getInputStream())); 
      String line; 
      while ((line = in.readLine()) != null) { 
        result += line; 
      } 
    } catch (Exception e) { 
      System.out.println("发送 POST 请求出现异常！"+e); 
      e.printStackTrace(); 
    } 
    //使用finally块来关闭输出流、输入流 
    finally{ 
      try{ 
        if(out!=null){ 
          out.close(); 
        } 
        if(in!=null){ 
          in.close(); 
        } 
      } 
      catch(IOException ex){ 
        ex.printStackTrace(); 
      } 
    } 
    return result; 
  } 
  
  public static String httpPostWithJSONQr(String url, String json,String id)  
          throws Exception {  
      String result = null;  
      // 将JSON进行UTF-8编码,以便传输中文  
      String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);   
      DefaultHttpClient httpClient = new DefaultHttpClient();  
      HttpPost httpPost = new HttpPost(url);  
      httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");    
      StringEntity se = new StringEntity(json);  
      se.setContentType("application/json");  
      se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"UTF-8"));  
      httpPost.setEntity(se);  
      // httpClient.execute(httpPost);  
      HttpResponse response = httpClient.execute(httpPost);  
      if (response != null) {  
          HttpEntity resEntity = response.getEntity();  
          if (resEntity != null) {  
              InputStream instreams = resEntity.getContent();   
             // ResourceBundle systemConfig = ResourceBundle.getBundle("config/system", Locale.getDefault());  
            //  String uploadSysUrl = systemConfig.getString("agentImgUrl")+id+"/";  
            //  File saveFile = new File(uploadSysUrl+id+".jpg");  
              //String uploadSysUrl = "D:\\upload"+"/";  
              //File saveFile = new File(outpath+id+".jpg");  
              // 判断这个文件（saveFile）是否存在  
              //if (!saveFile.getParentFile().exists()) {  
			     // 如果不存在就创建这个文件夹  
			     //saveFile.getParentFile().mkdirs();  
              //}  
              //将文件保存到七牛云
              
              Configuration cfg = new Configuration(Zone.zone0()); //zone0为华东
              //...其他参数参考类注释
              UploadManager uploadManager = new UploadManager(cfg);
              String key = null;//默认不指定key的情况下，以文件内容的hash值作为文件名
              Auth auth = Auth.create(Constants.QINIU_AK, Constants.QINIU_SK);
      		  String upToken = auth.uploadToken(Constants.QINIU_BUCKET);
      		
      		  try{
      			Response response2 = uploadManager.put(instreams,key,upToken,null, null);
                
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response2.bodyString(), DefaultPutRet.class);
               // System.out.println(putRet.key+"二维码");
                return putRet.key; //文件名
			  
      		  }
      		  catch(QiniuException ex)
      		  {
      			  Response r = ex.response;
      			  System.err.println(r.toString());
      		  }
              
              //将文件保存到目的路径
              //saveToImgByInputStream(instreams, outpath, id+".jpg");  
          }  
      }  
      return result;  
  }   
  
  /**
   * @param requestUrl 微信上传临时素材的接口url
   * @param file       要上传的文件
   * @return String  上传成功后，微信服务器返回的消息
   * @desc ：微信上传素材的请求方法
   */
  public static String httpRequest(String requestUrl, File file) {
      StringBuffer buffer = new StringBuffer();

      try {
          //1.建立连接
          URL url = new URL(requestUrl);
          HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();  //打开链接

          //1.1输入输出设置
          httpUrlConn.setDoInput(true);
          httpUrlConn.setDoOutput(true);
          httpUrlConn.setUseCaches(false); // post方式不能使用缓存
          //1.2设置请求头信息
          httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
          httpUrlConn.setRequestProperty("Charset", "UTF-8");
          //1.3设置边界
          String BOUNDARY = "----------" + System.currentTimeMillis();
          httpUrlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

          // 请求正文信息
          // 第一部分：
          //2.将文件头输出到微信服务器
          StringBuilder sb = new StringBuilder();
          sb.append("--"); // 必须多两道线
          sb.append(BOUNDARY);
          sb.append("\r\n");
          sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length()
                  + "\";filename=\"" + file.getName() + "\"\r\n");
          sb.append("Content-Type:application/octet-stream\r\n\r\n");
          byte[] head = sb.toString().getBytes("utf-8");
          // 获得输出流
          OutputStream outputStream = new DataOutputStream(httpUrlConn.getOutputStream());
          // 将表头写入输出流中：输出表头
          outputStream.write(head);

          //3.将文件正文部分输出到微信服务器
          // 把文件以流文件的方式 写入到微信服务器中
          DataInputStream in = new DataInputStream(new FileInputStream(file));
          int bytes = 0;
          byte[] bufferOut = new byte[1024];
          while ((bytes = in.read(bufferOut)) != -1) {
              outputStream.write(bufferOut, 0, bytes);
          }
          in.close();
          //4.将结尾部分输出到微信服务器
          byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
          outputStream.write(foot);
          outputStream.flush();
          outputStream.close();


          //5.将微信服务器返回的输入流转换成字符串
          InputStream inputStream = httpUrlConn.getInputStream();
          InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
          BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

          String str = null;
          while ((str = bufferedReader.readLine()) != null) {
              buffer.append(str);
          }

          bufferedReader.close();
          inputStreamReader.close();
          // 释放资源
          inputStream.close();
          inputStream = null;
          httpUrlConn.disconnect();


      } catch (IOException e) {
          System.out.println("发送POST请求出现异常！" + e);
          e.printStackTrace();
      }
      //4.json字符串转对象：解析返回值，json反序列化
      String result = buffer.toString().replaceAll("[\\\\]", "");
      System.out.println("result:" + result);
      JSONObject resultJSON = JSONObject.parseObject(result);
      return resultJSON.getString("media_id");
  }
} 