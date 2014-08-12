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
 * ��¼�첽����.
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
		pd.setTitle("���Ե�");
		pd.setMessage("���ڵ�¼...");
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
		pd.setMessage("���ڵ���"+values+"%");
	}

	@Override
	protected void onPostExecute(Integer result) {
		
	    pd.dismiss();
		switch (result) {
		case Constant.LOGIN_SECCESS: // ��¼�ɹ�
			Toast.makeText(context, "��½�ɹ�", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			if (loginConfig.isFirstStart()) {// ������״�����
				intent.setClass(context, GuideViewActivity.class);
				loginConfig.setFirstStart(false);
			} else {
				intent.setClass(context, MainActivity.class);
			}
			activitySupport.saveLoginConfig(loginConfig);// �����û�������Ϣ
			activitySupport.startService(); // ��ʼ���������
			 
			context.startActivity(intent);
			//activitySupport.startService(); // ��ʼ���������
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// �˻������������
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_invalid_username_password),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.SERVER_UNAVAILABLE:// ����������ʧ��
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_server_unavailable),
					Toast.LENGTH_SHORT).show();
			break;
		case Constant.LOGIN_ERROR:// δ֪�쳣
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.unrecoverable_error), Toast.LENGTH_SHORT)
					.show();
			break;
		case Constant.LOGIN_ERROR_ALREADY_REGISTERED://�ظ�ע��
			Toast.makeText(context, context.getResources().getString(R.string.alreadregistered_error), Toast.LENGTH_SHORT).show();
			break;
		}
		super.onPostExecute(result);
	}

	// ��¼
	private Integer login() {
		String username = loginConfig.getUsername();
		String password = loginConfig.getPassword();
		Log.d(LOG_TAG, "login now");
		try {
			XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
			Log.d(LOG_TAG,"login c2");
			connection.connect();
	 
			Log.d(LOG_TAG,"login c3");
			connection.login(username, password); // ��¼
			Log.d(LOG_TAG, "login c4");
			// OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//����������Ϣ
			connection.sendPacket(new Presence(Presence.Type.available));
			Log.d(LOG_TAG, "login c5");
			if (loginConfig.isNovisible()) {// �����¼
				Presence presence = new Presence(Presence.Type.unavailable);
				Collection<RosterEntry> rosters = connection.getRoster()
						.getEntries();
				for (RosterEntry rosterEntry : rosters) {
					presence.setTo(rosterEntry.getUser());
					connection.sendPacket(presence);
				}
			}
			loginConfig.setUsername(username);
			if (loginConfig.isRemember()) {// ��������
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
	//ע��
		private Integer regiester() {
			String username = loginConfig.getUsername();
			String password = loginConfig.getPassword();
			try {
				XMPPConnection connection = XmppConnectionManager.getInstance()
						.getConnection();
				connection.connect();
			 
				connection.getAccountManager().createAccount(username, password);//ע 
				/*���ϵ���һ�ִ��룬��ʱû�Թ�����Ϊ����ô�򵥾ͽ�������⣬���ﻹ������ô��Ĵ��롣
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
					//Log.e(��xmppMainRegiter��, ��No response from server.��);  
					//Toast.makeText(xmppMain.this, ��������û�з��ؽ����, Toast.LENGTH_SHORT).show();  
					}  
				else if (result.getType() == IQ.Type.ERROR)
				{  
						if(result.getError().toString().equalsIgnoreCase("conflict(409)")){  
					//	Log.e(��xmppMainRegiter��, ��IQ.Type.ERROR: ��+result.getError().toString());  
						//Toast.makeText(xmppMain.this, ������˺��Ѿ����ڡ�, Toast.LENGTH_SHORT).show();  
						}else{  
						//Log.e(��xmppMainRegiter��, ��IQ.Type.ERROR: ��+result.getError().toString());  
						//Toast.makeText(xmppMain.this, ��ע��ʧ�ܡ�, Toast.LENGTH_SHORT).show();  
						}  
					}
				else {
					//regeister success.
				}
				*/
				connection.login(username, password); // ��¼
				// OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//����������Ϣ
				connection.sendPacket(new Presence(Presence.Type.available));
				if (loginConfig.isNovisible()) {// �����¼
					Presence presence = new Presence(Presence.Type.unavailable);
					Collection<RosterEntry> rosters = connection.getRoster()
							.getEntries();
					for (RosterEntry rosterEntry : rosters) {
						presence.setTo(rosterEntry.getUser());
						connection.sendPacket(presence);
					}
				}
				loginConfig.setUsername(username);
				if (loginConfig.isRemember()) {// ��������
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
