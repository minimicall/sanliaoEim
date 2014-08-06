package com.sanliao.eim.activity;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import com.sanliao.eim.manager.XmppConnectionManager;

/**
 * 
 * �������˳�Ӧ��.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class EimApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();

	// ���Activity��������
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// ��������Activity��finish
	public void exit() {
		XmppConnectionManager.getInstance().disconnect();
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
}
