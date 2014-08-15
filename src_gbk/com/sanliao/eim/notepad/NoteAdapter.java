package com.sanliao.eim.notepad;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class NoteAdapter extends BaseAdapter {

	private Context mContext;//������
	private List<NoteInfo> mItems;//��Ŀ
	
	public NoteAdapter(Context context,List<NoteInfo> mItems) {
		this.mContext = context;
		this.mItems = mItems;
	}
	
	@Override
	public int getCount() { //��ȡ��Ŀ����
		return mItems.size(); 
	}
	
	@Override
	public Object getItem(int position) {//��ȡ��i����Ŀ
		return mItems.get(position); 
	}
	
	@Override
	public long getItemId(int position) {//��ȡ��Ŀid
		return position;
	}
//��ȡ��ͼ
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NoteListView btv;
		if (convertView == null) {
			btv = new NoteListView(mContext, mItems.get(position));
		} else {
			btv = (NoteListView) convertView;
			btv.setDiaryTitle(mItems.get(position).mtitle);
			btv.setDiaryTime("�������ࣺ"+mItems.get(position).msort+"       "+mItems.get(position).mtime);
		}
		return btv;
	}
}