package com.changyu.foryou.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
 
 
/**
 * 删除服务器上的文件
 * @author xiaoqun.yi
 */
//实现Runnable接口(推荐)，可以线程接口，预留一个extends(继承)，方便扩展
public class TempFileManager implements Runnable {
	private static String path;//路径
 
	private static String RETENTION_TIME = Constants.FILE_SAVED_TIME;
	
	/**
	 * 构造函数。初始化参数
	 * @param path
	 */
	public TempFileManager(String path) {
		this.path = path;
	}
	/**
	 * 把线程要执行的代码放在run()中
	 */
	public void run() {
		File file = new File(path);
		deletefiles(file);
	}
 
	/**
	 * 批量删除文件
	 * 
	 * @param folder
	 */
	public void deletefiles(File folder) {
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteFile(files[i]);
		}
	}
	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		try {
			if (file.isFile()) {
				// 删除符合条件的文件
				if (canDeleteFile(file)) {
					if (file.delete()) {
						System.out.println("文件" + file.getName() + "删除成功!");
					} else {
						System.out.println("文件" + file.getName()+ "删除失败!此文件可能正在被使用");
					}
				} else {
				}
			} else {
				System.out.println("没有可以删除的文件");
			}
		} catch (Exception e) {
			System.out.println("删除文件失败");
			e.printStackTrace();
		}
	}
	/**
	 * 判断文件是否能够被删除
	 */
	private boolean canDeleteFile(File file) {
		Date fileDate = getfileDate(file);
		Date date = new Date();
		long time = (date.getTime() - fileDate.getTime()) / 1000 / 60 - Integer.parseInt(RETENTION_TIME);// 当前时间与文件间隔的分钟
		if (time > 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 获取文件最后的修改时间
	 * 
	 * @param file
	 * @return
	 */
	private Date getfileDate(File file) {
		long modifiedTime = file.lastModified();
		Date d = new Date(modifiedTime);
		return d;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folder
	 */
	public void deleteFolder(File folder) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFolder(files[i]);
			}
			// 非当前目录，删除
			if (!folder.getAbsolutePath().equalsIgnoreCase(path)) {
				// 只删除在30分钟前创建的文件
				if (canDeleteFile(folder)) {
					if (folder.delete()) {
						System.out.println("文件夹" + folder.getName() + "删除成功!");
					} else {
						System.out.println("文件夹" + folder.getName()
								+ "删除失败!此文件夹内的文件可能正在被使用");
					}
				}
			}
		} else {
			deleteFile(folder);
		}
	}
}
