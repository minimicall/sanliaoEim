package com.sanliao.eim.activity;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import com.sanliao.eim.R;
import com.sanliao.eim.manager.XmppConnectionManager;
import com.sanliao.eim.model.LoginConfig;
import com.sanliao.eim.task.LoginTask;
import com.sanliao.eim.util.StringUtil;
import com.sanliao.eim.util.ValidateUtil;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 
 * 登录.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class LoginActivity extends ActivitySupport {
	private final static String LOG_TAG="LoginActivity";
	private EditText edt_username, edt_pwd;
	private CheckBox rememberCb, autologinCb, novisibleCb;
	private Button btn_login = null;
	private Button btn_register = null;
	private LoginConfig loginConfig;
	
	private final static Logger logger =Logger.getLogger(LoginActivity.class);//日志，写入到文件中的
 

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
		init();		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 校验SD卡
		checkMemoryCard();
		// 检测网络和版本
		validateInternet();
		// 初始化xmpp配置
		XmppConnectionManager.getInstance().init(loginConfig);
	}

	/**
	 * 
	 * 初始化.
	 * 
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 上午9:13:01
	 */
	protected void init() {
		loginConfig = getLoginConfig();
		// 如果为自动登录
		if (loginConfig.isAutoLogin()) {
			LoginTask loginTask = new LoginTask(LoginActivity.this, loginConfig);
			loginTask.execute();
		}
		edt_username = (EditText) findViewById(R.id.ui_username_input);
		edt_pwd = (EditText) findViewById(R.id.ui_password_input);
		rememberCb = (CheckBox) findViewById(R.id.remember);
		autologinCb = (CheckBox) findViewById(R.id.autologin);
		novisibleCb = (CheckBox) findViewById(R.id.novisible);
		btn_login = (Button) findViewById(R.id.ui_login_btn);
		btn_register=(Button)findViewById(R.id.ui_register_btn);

		// 初始化各组件的默认状态
		edt_username.setText(loginConfig.getUsername());
		edt_pwd.setText(loginConfig.getPassword());
		rememberCb.setChecked(loginConfig.isRemember());
		rememberCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!isChecked)
					autologinCb.setChecked(false);
			}
		});
		autologinCb.setChecked(loginConfig.isAutoLogin());
		novisibleCb.setChecked(loginConfig.isNovisible());

		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkData() && validateInternet()) {
					String username = edt_username.getText().toString();
					String password = edt_pwd.getText().toString();

					// 先记录下各组件的目前状态,登录成功后才保存
					loginConfig.setPassword(password);
					loginConfig.setUsername(username);
					loginConfig.setRemember(rememberCb.isChecked());
					loginConfig.setAutoLogin(autologinCb.isChecked());
					loginConfig.setNovisible(novisibleCb.isChecked());
					loginConfig.setIsRegister(false);//本次操作是否为注册
					logger.debug("execute LoginTask now.");
					LoginTask loginTask = new LoginTask(LoginActivity.this,
							loginConfig);
					loginTask.execute();
				}
			}
		});
		
		btn_register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkData() && validateInternet()) {
					String username = edt_username.getText().toString();
					String password = edt_pwd.getText().toString();

					// 先记录下各组件的目前状态,登录成功后才保存
					loginConfig.setPassword(password);
					loginConfig.setUsername(username);
					loginConfig.setRemember(rememberCb.isChecked());
					loginConfig.setAutoLogin(autologinCb.isChecked());
					loginConfig.setNovisible(novisibleCb.isChecked());
					loginConfig.setIsRegister(true);
					LoginTask loginTask = new LoginTask(LoginActivity.this,
							loginConfig);
					loginTask.execute();
				}
			}
		});
	}

	/**
	 * 
	 * 登录校验.
	 * 
	 * @return
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 上午9:12:37
	 */
	private boolean checkData() {
		boolean checked = false;
		checked = (!ValidateUtil.isEmpty(edt_username, "登录名") && !ValidateUtil
				.isEmpty(edt_pwd, "密码"));
		return checked;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//get the input layout as view
		 LayoutInflater inflater = (LayoutInflater) LoginActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);   
		final View view = inflater.inflate(R.layout.setserver, null);
	
		  EditText xmppHostText =(EditText)view.findViewById(R.id.edt_server_ip);
		 xmppHostText.setText(loginConfig.getXmppHost());
		 EditText xmppSerTx =(EditText)view.findViewById(R.id.edt_server_domain);
		xmppSerTx.setText(loginConfig.getXmppServiceName());
		switch (item.getItemId()) {
		case R.id.menu_login_set:
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle("服务器设置")
					.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("请设置服务器IP地址")
					.setView(view)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									  EditText xmppHostText =(EditText)view.findViewById(R.id.edt_server_ip);
									String xmppHost = StringUtil
											.doEmpty(xmppHostText.getText()
													.toString());
									loginConfig.setXmppHost(xmppHost);
									 EditText xmppSerTx =(EditText)view.findViewById(R.id.edt_server_domain);
									 String xmppnameString= StringUtil
												.doEmpty(xmppSerTx.getText()
														.toString());
									 loginConfig.setXmppServiceName(xmppnameString);
								Log.d(LOG_TAG,"IP:"+xmppHost+",domain:"+xmppnameString);
									XmppConnectionManager.getInstance().init(
											loginConfig);
									LoginActivity.this
											.saveLoginConfig(loginConfig);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();// 取消弹出框
								}
							}).create().show();

			break;
		case R.id.menu_relogin:
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.menu_exit:
			isExit();
			break;
		case R.id.menu_contactus:
			
			AlertDialog.Builder dialog2 = new AlertDialog.Builder(context);
			dialog2.setTitle("服务器设置")
					.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("作者：迅雷.曾金龙\n email:470910357@qq.com")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								 
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();// 取消弹出框
								}
							}).create().show();
		}
		return true;

	}

	@Override
	public void onBackPressed() {
		isExit();
	}
}
