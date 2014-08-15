package com.sanliao.eim.activity;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.sanliao.eim.manager.XmppConnectionManager;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 
 * 完整的退出应用.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class EimApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();

	public void onCreate()
	{
		super.onCreate();
		//使用log4j开启日志系统
		LogConfigurator logConfigurator =new LogConfigurator();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 java.util.Date date=new java.util.Date();
		 String dateString=date.getYear()+"_"+date.getMonth()+"_"+date.getDate()+"_"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds();
	 
		String pathString=Environment.getExternalStorageDirectory()+
				File.separator+"Eim"+File.separator+"logs"+File.separator+"eim"+dateString+".log";
		Log.d("EIM",pathString);
		logConfigurator.setFileName(pathString);
		logConfigurator.setRootLevel(Level.DEBUG	);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %t %-5p [%c{2}]-[%L] [%M] %m%n");
		logConfigurator.setMaxFileSize(1024*1024*5);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
		Logger logger =Logger.getLogger(EimApplication.class);
		logger.info("EIM APP is created info");
		logger.debug("EIM APP is creatd debug");
		logger.error("EIM APP is create error");
		XMPPConnection.DEBUG_ENABLED=true;
	
		logger.debug("XMPPConnectionDebug enable");
	 
	}
	
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		XmppConnectionManager.getInstance().disconnect();
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
}
