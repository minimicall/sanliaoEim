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
 * ��ҳ��.
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
		// ж�ع㲥������
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// ע��㲥������
		IntentFilter filter = new IntentFilter();
		// ��������
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
		switch (resultCode) { // resultCodeΪ�ش��ı��
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
		//����һ�����̣߳����ڴ������ϻ�ȡͼƬ
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
			 }).start();	//�����߳�	
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
		//setUserView();//ԭ���Ĵ��룬���д���ᵼ��mainactivity�����ر����������������������߳�������
	
		 
		Log.d(LOG_TAG, "init1z"+new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		//�û�ͷ���ܹ�����������¼�
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
		// ��ʼ���㲥
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
				case 0:// �ҵ���ϵ��
					intent.setClass(context, ContacterMainActivity.class);
					startActivity(intent);
					break;
				case 1:// �ҵ���Ϣ
					intent.setClass(context, MyNoticeActivity.class);
					startActivity(intent);
					break;
				case 2:// ��ҵͨѶ¼
					break;
				case 3:// ����ͨѶ¼
					break;
				case 4:// �ҵ��ʼ�
			
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
				case 5:// �����ղؼ�
					break;
				case 6:// �����ļ���
					break;
				case 7://���±�
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
	 * ���ز˵�.
	 * 
	 * @author xunlei.zengjinlong 470910357@qq.com
	 * @update 2012-5-16 ����7:15:21
	 */
	protected void loadMenuList() {
		list = new ArrayList<MainPageItem>();
		list.add(new MainPageItem("�ҵ���ϵ��", R.drawable.mycontacts));//0
		list.add(new MainPageItem("�ҵ���Ϣ", R.drawable.mynotice));//1
		list.add(new MainPageItem("��ҵͨѶ¼", R.drawable.e_contact));//2
		list.add(new MainPageItem("����ͨѶ¼", R.drawable.p_contact));//3
		list.add(new MainPageItem("�ʼ�", R.drawable.email));//4
		list.add(new MainPageItem("�����¼", R.drawable.sso));//5
		list.add(new MainPageItem("�����ļ���", R.drawable.p_folder));//6
		list.add(new MainPageItem("�ҵıʼ�", R.drawable.mynote));//7
		list.add(new MainPageItem("�ҵ�ǩ��", R.drawable.signin));//8
		list.add(new MainPageItem("�ҵĹ����ձ�", R.drawable.mydaily));//9
		list.add(new MainPageItem("�ҵ��ճ�", R.drawable.mymemo));//10
		list.add(new MainPageItem("����", R.drawable.set));//11
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
				// ���С����
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
			dialog2.setTitle("����������")
					.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage("���ߣ�Ѹ��.������\nemail:470910357@qq.com")
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
			break;
			
		
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		isExit();
	}

	/**
	 * ��������ӷ���״̬�����ӳɹ� �ı�ͷ�� ��ʧ��
	 * 
	 * @param isSuccess
	 */
	private void handReConnect(boolean isSuccess) {
		// �ɹ�������
		if (Constant.RECONNECT_STATE_SUCCESS == isSuccess) {
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_online));
			// Toast.makeText(context, "����ָ�,�û�������!", Toast.LENGTH_LONG).show();
		} else if (Constant.RECONNECT_STATE_FAIL == isSuccess) {// ʧ��
			iv_status.setImageDrawable(getResources().getDrawable(
					R.drawable.status_offline));
			// Toast.makeText(context, "����Ͽ�,�û�������!", Toast.LENGTH_LONG).show();
		}

	}
}