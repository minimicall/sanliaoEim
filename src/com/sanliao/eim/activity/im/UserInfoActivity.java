package com.sanliao.eim.activity.im;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.sanliao.eim.R;
import com.sanliao.eim.activity.ActivitySupport;
import com.sanliao.eim.camera.CameraActivity;
import com.sanliao.eim.manager.UserManager;
import com.sanliao.eim.model.LoginConfig;
import com.sanliao.eim.util.StringUtil;

/**
 * 
 * 用户资料查看.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class UserInfoActivity extends ActivitySupport {
	private final static String LOG_TAG="UserInfoActivity";
	private ImageView titleBack, userimageView,cameraImageView;
	private LinearLayout user_info_detail, user_info_edit;
	private Button edit_btn, finish_btn;
	private TextView firstnameView, nicknameView, orgnameView, orgunitView,
			mobileView, emailhomeView, discView;
	private EditText firstnameEdit, nicknameEdit, orgnameEdit, orgunitEdit,
			mobileEdit, emailhomeEdit, discEdit;
	private UserManager userManager;
	private LoginConfig loginConfig;
	private VCard vCard;
	private final static int REQUEST_CAMERA=1;
	private final static String DST_FOLDER_NAME="sanliao";
	private final static String HEAD_IMG="head_img";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		init();
	}

	private void init() {
		userManager = UserManager.getInstance(this);
		loginConfig = getLoginConfig();
		userimageView = (ImageView) findViewById(R.id.userimage);
		firstnameView = (TextView) findViewById(R.id.firstname);
		nicknameView = (TextView) findViewById(R.id.nickname);
		orgnameView = (TextView) findViewById(R.id.orgname);
		orgunitView = (TextView) findViewById(R.id.orgunit);
		mobileView = (TextView) findViewById(R.id.mobile);
		emailhomeView = (TextView) findViewById(R.id.emailhome);
		discView = (TextView) findViewById(R.id.disc);

		firstnameEdit = (EditText) findViewById(R.id.e_firstname);
		nicknameEdit = (EditText) findViewById(R.id.e_nickname);
		orgnameEdit = (EditText) findViewById(R.id.e_orgname);
		orgunitEdit = (EditText) findViewById(R.id.e_orgunit);
		mobileEdit = (EditText) findViewById(R.id.e_mobile);
		emailhomeEdit = (EditText) findViewById(R.id.e_emailhome);
		discEdit = (EditText) findViewById(R.id.e_disc);
		cameraImageView=(ImageView)findViewById(R.id.faceIcon);
		
		
/*
		String jid = StringUtil.getJidByName(loginConfig.getUsername(),
				loginConfig.getXmppServiceName());
		vCard = userManager.getUserVCard(jid);
		InputStream is = userManager.getUserImage(jid);
		if (is != null) {
			Bitmap bm = BitmapFactory.decodeStream(is);
			userimageView.setImageBitmap(bm);
		}
		setVCardView(vCard);
		*/
		pullUserInfo();

		titleBack = (ImageView) findViewById(R.id.title_back);
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});
		user_info_detail = (LinearLayout) findViewById(R.id.user_info_detail);
		user_info_edit = (LinearLayout) findViewById(R.id.user_info_edit);
		edit_btn = (Button) findViewById(R.id.edit_btn);
		finish_btn = (Button) findViewById(R.id.finish_btn);
		edit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish_btn.setVisibility(View.VISIBLE);
				user_info_edit.setVisibility(View.VISIBLE);
				edit_btn.setVisibility(View.GONE);
				user_info_detail.setVisibility(View.GONE);
				discView.setVisibility(View.GONE);
				discEdit.setVisibility(View.VISIBLE);
			}
		});
	
		
		finish_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vCard.setFirstName(firstnameEdit.getText().toString());
				vCard.setNickName(nicknameEdit.getText().toString());
				vCard.setOrganization(orgnameEdit.getText().toString());
				vCard.setOrganizationUnit(orgunitEdit.getText().toString());
				vCard.setField("MOBILE", mobileEdit.getText().toString());
				vCard.setEmailHome(emailhomeEdit.getText().toString());
				vCard.setField("DESC", discEdit.getText().toString());
				vCard = userManager.saveUserVCard(vCard);
				if (vCard != null) {
					setVCardView(vCard);
					finish_btn.setVisibility(View.GONE);
					user_info_edit.setVisibility(View.GONE);
					edit_btn.setVisibility(View.VISIBLE);
					user_info_detail.setVisibility(View.VISIBLE);
					discView.setVisibility(View.VISIBLE);
					discEdit.setVisibility(View.GONE);
					showToast("用户信息已保存!");
				} else {
					showToast("更新用户信息失败!");
				}
			}
		});
		
		//处理摄像头图像被点击的事件，拉起摄像头，照图片，然后设置为头像
		cameraImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "cameraImageView is clicked");
				Intent intent = new Intent();
				Log.d(LOG_TAG,"jump to userInfoActivity now");
				intent.setClass(context, CameraActivity.class);
				startActivityForResult(intent,REQUEST_CAMERA);
				
			}
		});

	}
	
	protected void  onActivityResult(int requestCode,int resultCode,Intent data ) {
		if(requestCode==REQUEST_CAMERA && resultCode==RESULT_OK)
		{
			//Bundle bundle = data.getExtras();
			//boolean is_photo_taked = bundle.getBoolean("is_photo");
			//if(is_photo_taked==false)
			//{
			//	Toast.makeText(context, "照相失败...", Toast.LENGTH_SHORT).show();
			//	return ;
			//}
			Toast.makeText(context, "上传头像中...", Toast.LENGTH_SHORT).show();
			 changeImage();
			/*
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			byte [] b=bos.toByteArray();
			
		InputStream is=new ByteArrayInputStream(b);
			 Bitmap  bm = BitmapFactory.decodeStream(is);
			  userimageView.setImageBitmap(bm);
			
			String encodedImageString= StringUtils.encodeBase64(b);
			vCard.setAvatar(b,encodedImageString);
			vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"+encodedImageString+"</BINVAL",true);
			//vCard = userManager.saveUserVCard(vCard);
			
			*/
	 
		 
		 
			
		}
		
	}
	
	private void changeImage()
	{
		 File parentPath = Environment.getExternalStorageDirectory();
		String 	storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
		 String filenameString=storagePath+"/"+HEAD_IMG;
		 File file=new File(filenameString);
		 FileInputStream finFileInputStream = null;
		try {
			finFileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 final byte b[]=new byte[(int)file.length()];
		int readbytes = 0;
		try {
			readbytes = finFileInputStream.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(readbytes!=file.length())
		{
			Log.d(LOG_TAG,"Entire file not read");
			 return ;
		}
		InputStream is=new ByteArrayInputStream(b);
		 Bitmap  bm = BitmapFactory.decodeStream(is);
		  userimageView.setImageBitmap(bm);
		
		final String encodedImageString= StringUtils.encodeBase64(b);
		new Thread(new Runnable(){
			public void run()
			{
				String jid = StringUtil.getJidByName(loginConfig.getUsername(),
						loginConfig.getXmppServiceName());//	loginConfig.getXmppServiceName()
				Log.d(LOG_TAG,"jid: "+jid);
			vCard.setAvatar(b);
			// vCard.setAvatar(b,encodedImageString);
			 //vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>"+encodedImageString+"</BINVAL",true);
			 vCard = userManager.saveUserVCard(vCard);
			}
		});
		
		//
		 
	}	
	private  void pullUserInfo()
		{
			//创建一个新线程，用于从网络上获取用户信息
		 new Thread(new Runnable() {
			 public void run() {
				 Log.d(LOG_TAG,"new thread ,get user infomation");
					String jid = StringUtil.getJidByName(loginConfig.getUsername(),
							loginConfig.getXmppServiceName());//	loginConfig.getXmppServiceName()
					Log.d(LOG_TAG,"jid: "+jid);
					vCard = userManager.getUserVCard(jid);
					InputStream is = userManager.getUserImage(jid);
					if (is != null) {
						final   Bitmap bm = BitmapFactory.decodeStream(is);
						userimageView.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								userimageView.setImageBitmap(bm);
							}
						});
				
					}	
				Message message = new Message();
				message.what=1;
				handler.sendMessage(message);
				 }			 
			 }).start();	//开启线程	
			
		}
	Handler handler=new Handler(){
		public void handleMessage(Message msg)
		{
			Log.d(LOG_TAG,"handleMessage now.");
			switch(msg.what)
			{
			case 1:
				setVCardView(vCard);
				break;
			}
		}
	};

	private void setVCardView(VCard vCard) {
		firstnameView.setText(vCard.getFirstName());
		nicknameView.setText(vCard.getNickName());
		orgnameView.setText(vCard.getOrganization());
		orgunitView.setText(vCard.getOrganizationUnit());
		mobileView.setText(vCard.getField("MOBILE"));
		emailhomeView.setText(vCard.getEmailHome());
		discView.setText(vCard.getField("DESC"));

		firstnameEdit.setText(vCard.getFirstName());
		nicknameEdit.setText(vCard.getNickName());
		orgnameEdit.setText(vCard.getOrganization());
		orgunitEdit.setText(vCard.getOrganizationUnit());
		mobileEdit.setText(vCard.getField("MOBILE"));
		emailhomeEdit.setText(vCard.getEmailHome());
		discEdit.setText(vCard.getField("DESC"));
	 

	}
	
}
