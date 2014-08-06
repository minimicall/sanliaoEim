package com.sanliao.eim.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * 
 * ��¼.
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		init();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// У��SD��
		checkMemoryCard();
		// �������Ͱ汾
		validateInternet();
		// ��ʼ��xmpp����
		XmppConnectionManager.getInstance().init(loginConfig);
	}

	/**
	 * 
	 * ��ʼ��.
	 * 
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 ����9:13:01
	 */
	protected void init() {
		loginConfig = getLoginConfig();
		// ���Ϊ�Զ���¼
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

		// ��ʼ���������Ĭ��״̬
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

					// �ȼ�¼�¸������Ŀǰ״̬,��¼�ɹ���ű���
					loginConfig.setPassword(password);
					loginConfig.setUsername(username);
					loginConfig.setRemember(rememberCb.isChecked());
					loginConfig.setAutoLogin(autologinCb.isChecked());
					loginConfig.setNovisible(novisibleCb.isChecked());
					loginConfig.setIsRegister(false);//���β����Ƿ�Ϊע��

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

					// �ȼ�¼�¸������Ŀǰ״̬,��¼�ɹ���ű���
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
	 * ��¼У��.
	 * 
	 * @return
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 ����9:12:37
	 */
	private boolean checkData() {
		boolean checked = false;
		checked = (!ValidateUtil.isEmpty(edt_username, "��¼��") && !ValidateUtil
				.isEmpty(edt_pwd, "����"));
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
			dialog.setTitle("����������")
					.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("�����÷�����IP��ַ")
					.setView(view)
					.setPositiveButton("ȷ��",
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
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();// ȡ��������
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
			dialog2.setTitle("����������")
					.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("���ߣ�Ѹ��.������\n email:470910357@qq.com")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								 
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();// ȡ��������
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
