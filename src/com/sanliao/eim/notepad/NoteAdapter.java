package com.sanliao.eim.notepad;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class NoteAdapter extends BaseAdapter {

	private Context mContext;//上下文
	private List<NoteInfo> mItems;//条目
	
	public NoteAdapter(Context context,List<NoteInfo> mItems) {
		this.mContext = context;
		this.mItems = mItems;
	}
	
	@Override
	public int getCount() { //获取条目数量
		return mItems.size(); 
	}
	
	@Override
	public Object getItem(int position) {//获取第i个条目
		return mItems.get(position); 
	}
	
	@Override
	public long getItemId(int position) {//获取条目id
		return position;
	}
//获取视图
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NoteListView btv;
		if (convertView == null) {
			btv = new NoteListView(mContext, mItems.get(position));
		} else {
			btv = (NoteListView) convertView;
			btv.setDiaryTitle(mItems.get(position).mtitle);
			btv.setDiaryTime("所属分类："+mItems.get(position).msort+"       "+mItems.get(position).mtime);
		}
		return btv;
	}
}