package com.sanliao.eim.notepad;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteListView extends LinearLayout {
	
	private TextView mTitle, mTime;
	
	public NoteListView(Context context, NoteInfo diaryInfo) {
		super(context);
		this.setOrientation(VERTICAL);
		this.setPadding(5, 5, 5, 5);
		this.setBackgroundColor(0xff888888);
		
		mTitle = new TextView(context);
		mTitle.setText(diaryInfo.mtitle);
		mTitle.setTextSize(20);
		addView(mTitle, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		mTime = new TextView(context);
		mTime.setText("À˘ Ù∑÷¿‡£∫"+diaryInfo.msort+"       "+diaryInfo.mtime);
		mTime.setTextSize(12);
		mTime.setGravity(Gravity.RIGHT);
		addView(mTime, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	public void setDiaryTitle(String name) {
		mTitle.setText(name);
	}
	
	public void setDiaryTime(String name) {
		mTime.setText(name);
	}
}