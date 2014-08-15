package com.sanliao.eim.camera;

import com.sanliao.eim.R;
import com.sanliao.eim.activity.im.UserInfoActivity;
import com.sanliao.eim.camera.*;
import com.sanliao.eim.camera.CameraInterface.CamOpenOverCallback;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements CamOpenOverCallback {
	private static final String LOG_TAG = "CameraActivity";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	float previewRate = -1f;
	Point screen;
	Bitmap bitmap;
	Button yesbtn;
	Button noBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//开启一个线程打开照相机
		Thread openThread = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CameraInterface.getInstance().doOpenCamera("head_image",CameraActivity.this);
			}
		};
		openThread.start();
		//关联xml
		setContentView(R.layout.activity_camera);

		initUI();
		initViewParams();
		
		shutterBtn.setOnClickListener(new BtnListeners());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	private void initUI(){
		surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
		shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
		yesbtn = (Button)findViewById(R.id.btn_yes);
		noBtn = (Button)findViewById(R.id.btn_no);
		
		yesbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub	 
				Intent intent=new Intent(CameraActivity.this, UserInfoActivity.class);
			
				Bundle bundle = new Bundle();
				bundle.putBoolean("is_photo", true);
				intent.putExtra("bundle", bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		} );
		
	}
	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		screen= DisplayUtil.getScreenMetrics(this);
		params.width = screen.x;
		params.height = screen.y;
		previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
		surfaceView.setLayoutParams(params);

		//手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayUtil.dip2px(this, 80);
		p2.height = DisplayUtil.dip2px(this, 80);;		
		shutterBtn.setLayoutParams(p2);	

	}

	@Override
	public void cameraHasOpened() {
		// TODO Auto-generated method stub
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	private class BtnListeners implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_shutter:
				CameraInterface.getInstance().doTakePicture();//拍照
				
				
		 
				break;
		
			default:break;
			}
		}

	}

}
