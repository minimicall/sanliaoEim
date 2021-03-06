package com.sanliao.eim.activity.im;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.sanliao.eim.R;
import com.sanliao.eim.activity.ActivitySupport;

/**
 * 
 * 用户资料查看.
 * 
 * @author xunlei.zengjinlong 470910357@qq.com
 */
public class FriendInfoActivity extends ActivitySupport {
	private ImageView titleBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info);
		init();
	}

	private void init() {
		getEimApplication().addActivity(this);
		titleBack = (ImageView) findViewById(R.id.title_back);
		titleBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
