package com.sanliao.eim.task;

import java.util.Collection;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.XMPPError;
 

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.sanliao.eim.R;
import com.sanliao.eim.activity.GuideViewActivity;
import com.sanliao.eim.activity.IActivitySupport;
import com.sanliao.eim.activity.MainActivity;
import com.sanliao.eim.comm.Constant;
import com.sanliao.eim.manager.XmppConnectionManager;
import com.sanliao.eim.model.LoginConfig;

/**
 * 
 * 登录异步任务.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private final static String LOG_TAG="LoginTask";
	private ProgressDialog pd;
	private Context context;
	private IActivitySupport activitySupport;
	private LoginConfig loginConfig;

	public LoginTask(IActivitySupport activitySupport, LoginConfig loginConfig) {
		this.activitySupport = activitySupport;
		this.loginConfig = loginConfig;
		this.pd = activitySupport.getProgressDialog();
		this.context = activitySupport.getContext();
	}

	@Override
	protected void onPreExecute() {
		pd.setTitle("请稍等");
		pd.setMessage("正在登录...");
		pd.show();
		Log.d(LOG_TAG,"onPreExecute");
		
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		
		boolean isRegister = loginConfig.isRegister();
		if(isRegister==true)
			return regiester();
		else
		return login();
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		pd.setMessage("正在登入"+values+"%");
	}

	@Override
	protected void onPostExecute(Integer result) {
		
	    pd.dismiss();
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			if (loginConfig.isFirstStart()) {// 如果是首次启动
				intent.setClass(context, GuideViewActivity.class);
				loginConfig.setFirstStart(false);
			} else {
				intent.setClass(context, MainActivity.class);
			}
			activitySupport.saveLoginConfig(loginConfig);// 保存用户配置信息
			activitySupport.startService(); // 初始化各项服务
			 
			context.startActivity(intent);
			//activitySupport.startService(); // 初始化各项服务
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_invalid_username_password),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_server_unavailable),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.unrecoverable_error), Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.LOGIN_ERROR_ALREADY_REGISTERED://重复注册
			Toast.makeText(context, context.getResources().getString(R.string.alreadregistered_error), Toast.LENGTH_SHORT).show();
			break;
		}
		super.onPostExecute(result);
	}

	// 登录
	private Integer login() {
		String username = loginConfig.getUsername();
		String password = loginConfig.getPassword();
		Log.d(LOG_TAG, "login now");
		try {
			XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
			Log.d(LOG_TAG,"login c2");
			connection.connect();
	 
			Log.d(LOG_TAG,"login c3");
			connection.login(username, password); // 登录
			Log.d(LOG_TAG, "login c4");
			// OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
			connection.sendPacket(new Presence(Presence.Type.available));
			Log.d(LOG_TAG, "login c5");
			if (loginConfig.isNovisible()) {// 隐身登录
				Presence presence = new Presence(Presence.Type.unavailable);
				Collection<RosterEntry> rosters = connection.getRoster()
						.getEntries();
				for (RosterEntry rosterEntry : rosters) {
					presence.setTo(rosterEntry.getUser());
					connection.sendPacket(presence);
				}
			}
			loginConfig.setUsername(username);
			if (loginConfig.isRemember()) {// 保存密码
				loginConfig.setPassword(password);
			} else {
				loginConfig.setPassword("");
			}
			loginConfig.setOnline(true);
			
			return Constant.LOGIN_SECCESS;
		} catch (Exception xee) {
			Log.d(LOG_TAG,xee.toString() );
			if (xee instanceof XMPPException) {
				XMPPException xe = (XMPPException) xee;
				Log.d(LOG_TAG,xe.toString() );
				final XMPPError error = xe.getXMPPError();
				//final XMPPError error=new XMPPError(null);
				int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
	 
				}
				if (errorCode == 401) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				}else if (errorCode == 403) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					return Constant.SERVER_UNAVAILABLE;
				}
			} else {
				return Constant.LOGIN_ERROR;
			}
		}
	}
	//注册
		private Integer regiester() {
			String username = loginConfig.getUsername();
			String password = loginConfig.getPassword();
			try {
				XMPPConnection connection = XmppConnectionManager.getInstance()
						.getConnection();
				connection.connect();
			 
				connection.getAccountManager().createAccount(username, password);//注 
				/*网上的另一种代码，暂时没试过。因为我这么简单就解决了问题，干嘛还用你这么多的代码。
				Registration registration = new Registration();
				registration.setType(IQ.Type.SET);
				registration.setTo(connection.getServiceName());
				registration.setUsername(username);
				registration.setPassword(password);
				registration.addAttribute("android", "create_user_by_android");
				PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()),new PacketTypeFilter(IQ.class));
				PacketCollector collector = connection.createPacketCollector(filter);  
				connection.sendPacket(registration);  
				IQ result = (IQ)collector.nextResult(SmackConfiguration.getPacketReplyTimeout()); 
				collector.cancel();
				if (result == null) {  
					//Log.e(“xmppMainRegiter”, “No response from server.”);  
					//Toast.makeText(xmppMain.this, “服务器没有返回结果”, Toast.LENGTH_SHORT).show();  
					}  
				else if (result.getType() == IQ.Type.ERROR)
				{  
						if(result.getError().toString().equalsIgnoreCase("conflict(409)")){  
					//	Log.e(“xmppMainRegiter”, “IQ.Type.ERROR: “+result.getError().toString());  
						//Toast.makeText(xmppMain.this, “这个账号已经存在”, Toast.LENGTH_SHORT).show();  
						}else{  
						//Log.e(“xmppMainRegiter”, “IQ.Type.ERROR: “+result.getError().toString());  
						//Toast.makeText(xmppMain.this, “注册失败”, Toast.LENGTH_SHORT).show();  
						}  
					}
				else {
					//regeister success.
				}
				*/
				connection.login(username, password); // 登录
				// OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
				connection.sendPacket(new Presence(Presence.Type.available));
				if (loginConfig.isNovisible()) {// 隐身登录
					Presence presence = new Presence(Presence.Type.unavailable);
					Collection<RosterEntry> rosters = connection.getRoster()
							.getEntries();
					for (RosterEntry rosterEntry : rosters) {
						presence.setTo(rosterEntry.getUser());
						connection.sendPacket(presence);
					}
				}
				loginConfig.setUsername(username);
				if (loginConfig.isRemember()) {// 保存密码
					loginConfig.setPassword(password);
				} else {
					loginConfig.setPassword("");
				}
				loginConfig.setOnline(true);
				return Constant.LOGIN_SECCESS;
			} catch (Exception xee) {
				if (xee instanceof XMPPException) {
					XMPPException xe = (XMPPException) xee;
				//	final XMPPError error = xe.getXMPPError();
					final XMPPError error=null;
					int errorCode = 0;
					if (error != null) {
						errorCode = error.getCode();
					}
					if (errorCode == 401) {
						return Constant.LOGIN_ERROR_ACCOUNT_PASS;
					}else if (errorCode == 403) {
						return Constant.LOGIN_ERROR_ACCOUNT_PASS;
					}else if(errorCode==409)
					{
						return Constant.LOGIN_ERROR_ALREADY_REGISTERED;
					}
					else {
						return Constant.SERVER_UNAVAILABLE;
					}
				} else {
					return Constant.LOGIN_ERROR;
				}
			}
		}
}
