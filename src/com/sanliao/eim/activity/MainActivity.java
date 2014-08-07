package com.sanliao.eim.activity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smackx.packet.VCard;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.sanliao.eim.R;
import com.sanliao.eim.activity.im.ContacterMainActivity;
import com.sanliao.eim.activity.im.UserInfoActivity;
import com.sanliao.eim.activity.notice.MyNoticeActivity;
import com.sanliao.eim.comm.Constant;
import com.sanliao.eim.manager.UserManager;
import com.sanliao.eim.model.LoginConfig;
import com.sanliao.eim.model.MainPageItem;
import com.sanliao.eim.notepad.NoteBook;
import com.sanliao.eim.util.StringUtil;
import com.sanliao.eim.view.MainPageAdapter;

/**
 * 
 * 主页面.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class MainActivity extends ActivitySupport {
	private GridView gridview;
	private List<MainPageItem> list;
	private MainPageAdapter adapter;
	private ImageView iv_status;
	private ContacterReceiver receiver = null;
	private TextView usernameView;
	private UserManager userManager;
	private LoginConfig loginConfig;
	private ImageView userimageView;
	private static final String LOG_TAG="MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	    init();
	}

	@Override
	protected void onPause() {
		// 卸载广播接收器
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		// 好友请求
		filter.addAction(Constant.ROSTER_SUBSCRIPTION);
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		filter.addAction(Constant.ACTION_SYS_MSG);

		filter.addAction(Constant.ACTION_RECONNECT_STATE);
		registerReceiver(receiver, filter);

		if (getUserOnlineState()) {
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_online));
		} else {
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_offline));
		}

		super.onResume();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记
		case 1:
			setUserView();
			break;
		default:
			break;
		}
	}
   private VCard vCard; 
 private Bitmap bm ;
 private  InputStream is ;
	private void setUserView() {
		//创建一个新线程，用于从网络上获取图片
		 new Thread(new Runnable() {
			 public void run() {
				 Log.d(LOG_TAG,"new thread ,get user infomation");
					String jid = StringUtil.getJidByName(loginConfig.getUsername(),
							loginConfig.getXmppServiceName());
					Log.d(LOG_TAG,"jid: "+jid);
					vCard = userManager.getUserVCard(jid);
					  is = userManager.getUserImage(jid);
					Log.d(LOG_TAG,"Vcard returned ");
					if (is != null) {
						   bm = BitmapFactory.decodeStream(is);
						Log.d(LOG_TAG,"set i mage now");
						userimageView.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								userimageView.setImageBitmap(bm);
							}
						});
						//userimageView.setImageBitmap(bm);
					}
					Log.d(LOG_TAG,"set name now");
					if (vCard.getFirstName() != null) {
						//usernameView.setText(vCard.getFirstName()
						//		+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
							//			+ vCard.getOrganization() : ""));
						Log.d(LOG_TAG, "getFirstName"+vCard.getFirstName());
						usernameView.post(new Runnable() {
							public void run() {
								 usernameView.setText(vCard.getFirstName()
								 	+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
								 	+ vCard.getOrganization() : ""));
							}
						});
					} else {
						Log.d(LOG_TAG,"1username:"+loginConfig.getUsername());
						userimageView.post( new Runnable() {
							public void run() {
								Log.d(LOG_TAG,"username:"+loginConfig.getUsername());
								usernameView.setText(loginConfig.getUsername()
										+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
												+ vCard.getOrganization() : ""));
							}
						});
						
					}
					Message message = new Message();
					message.what=3;
					handler.sendMessage(message);
	
				 }			 
			 }).start();	//开启线程	
		/*
		String jid = StringUtil.getJidByName(loginConfig.getUsername(),
				loginConfig.getXmppServiceName());
		VCard vCard = userManager.getUserVCard(jid);
		InputStream is = userManager.getUserImage(jid);
		if (is != null) {
			Bitmap bm = BitmapFactory.decodeStream(is);
			userimageView.setImageBitmap(bm);
		}
		if (vCard.getFirstName() != null) {
			usernameView.setText(vCard.getFirstName()
					+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
							+ vCard.getOrganization() : ""));
		} else {
			usernameView.setText(loginConfig.getUsername()
					+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
							+ vCard.getOrganization() : ""));
		}
		*/

	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg)
		{
			Log.d(LOG_TAG,"handleMessage now.");
			switch(msg.what)
			{
			case 3:
				if(is!=null)
				userimageView.setImageBitmap(bm);
				if (vCard.getFirstName() != null) {
					usernameView.setText(vCard.getFirstName()
						+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
								+ vCard.getOrganization() : ""));
				
				} else {
					 
							Log.d(LOG_TAG,"username:"+loginConfig.getUsername());
							usernameView.setText(loginConfig.getUsername()
									+ (StringUtil.notEmpty(vCard.getOrganization()) ? " - "
											+ vCard.getOrganization() : ""));			
				}
				break;
			}
		}
	};

	private void init() {
		 
		Log.d(LOG_TAG, "init"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
	userManager = UserManager.getInstance(this);
		loginConfig = getLoginConfig();
		Log.d(LOG_TAG, "init1x"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		gridview = (GridView) findViewById(R.id.gridview);
		iv_status = (ImageView) findViewById(R.id.iv_status);
		userimageView = (ImageView) findViewById(R.id.userimage);
		usernameView = (TextView) findViewById(R.id.username);
		Log.d(LOG_TAG, "init1y"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		//setUserView();//原来的代码，这行代码会导致mainactivity加载特别的慢，所以我们用另外个线程来处理
	
		 
		Log.d(LOG_TAG, "init1z"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		//用户头像能够处理被点击的事件
		userimageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Log.d(LOG_TAG,"jump to userInfoActivity now");
				intent.setClass(context, UserInfoActivity.class);
				startActivityForResult(intent, 1);
			}
		});
		Log.d(LOG_TAG, "init1.5"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		// 初始化广播
		receiver = new ContacterReceiver();
		Log.d(LOG_TAG, "init2"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		loadMenuList();
		adapter = new MainPageAdapter(this);
		adapter.setList(list);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Intent intent = new Intent();
				switch (position) {
				case 0:// 我的联系人
					intent.setClass(context, ContacterMainActivity.class);
					startActivity(intent);
					break;
				case 1:// 我的消息
					intent.setClass(context, MyNoticeActivity.class);
					startActivity(intent);
					break;
				case 2:// 企业通讯录
					break;
				case 3:// 个人通讯录
					break;
				case 4:// 我的邮件
			
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Intent eIntent=new Intent();
							eIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							ComponentName comp=new ComponentName("com.fsck.k9","com.fsck.k9.activity.Accounts");
							eIntent.setComponent(comp);
							eIntent.setAction("android.intent.action.VIEW");
							startActivity(eIntent);
						}
					}).start();
					break;
				case 5:// 网络收藏夹
					break;
				case 6:// 个人文件夹
					break;
				case 7://记事本
					intent.setClass(context,  NoteBook.class);
					startActivity(intent);
					break;
				default:			
					break;
				}
			}
		});
		Log.d(LOG_TAG, "init3"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		setUserView();
	}

	/**
	 * 
	 * 加载菜单.
	 * 
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 下午7:15:21
	 */
	protected void loadMenuList() {
		list = new ArrayList<MainPageItem>();
		list.add(new MainPageItem("我的联系人", R.drawable.mycontacts));//0
		list.add(new MainPageItem("我的消息", R.drawable.mynotice));//1
		list.add(new MainPageItem("企业通讯录", R.drawable.e_contact));//2
		list.add(new MainPageItem("个人通讯录", R.drawable.p_contact));//3
		list.add(new MainPageItem("邮件", R.drawable.email));//4
		list.add(new MainPageItem("单点登录", R.drawable.sso));//5
		list.add(new MainPageItem("个人文件夹", R.drawable.p_folder));//6
		list.add(new MainPageItem("我的笔记", R.drawable.mynote));//7
		list.add(new MainPageItem("我的签到", R.drawable.signin));//8
		list.add(new MainPageItem("我的工作日报", R.drawable.mydaily));//9
		list.add(new MainPageItem("我的日程", R.drawable.mymemo));//10
		list.add(new MainPageItem("设置", R.drawable.set));//11
	}

	@Override
	protected void onRestart() {
		adapter.notifyDataSetChanged();
		super.onRestart();
	}

	private class ContacterReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.ROSTER_SUBSCRIPTION.equals(action)) {
				adapter.notifyDataSetChanged();
			} else if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
				// 添加小气泡
				adapter.notifyDataSetChanged();
			} else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
				boolean isSuccess = intent.getBooleanExtra(
						Constant.RECONNECT_STATE, false);
				handReConnect(isSuccess);
			} else if (Constant.ACTION_SYS_MSG.equals(action)) {
				adapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_page_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.menu_relogin:
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
						.setMessage("作者：迅雷.曾金龙\nemail:470910357@qq.com")
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
			break;
			
		
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		isExit();
	}

	/**
	 * 处理冲连接返回状态，连接成功 改变头像 ，失败
	 * 
	 * @param isSuccess
	 */
	private void handReConnect(boolean isSuccess) {
		// 成功了连接
		if (Constant.RECONNECT_STATE_SUCCESS == isSuccess) {
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_online));
			// Toast.makeText(context, "网络恢复,用户已上线!", Toast.LENGTH_LONG).show();
		} else if (Constant.RECONNECT_STATE_FAIL == isSuccess) {// 失败
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_offline));
			// Toast.makeText(context, "网络断开,用户已离线!", Toast.LENGTH_LONG).show();
		}

	}
}