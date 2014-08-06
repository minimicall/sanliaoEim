package com.sanliao.eim.notepad;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sanliao.eim.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
	
public class NoteList extends ListActivity{
	private final static String TAG="NoteList";
	private List<NoteInfo> list = new ArrayList<NoteInfo>();
	private NoteAdapter adapter;
	
	private final int NSort = Menu.FIRST;//分类
	private final int NSearch = Menu.FIRST+1;//查找
	private final int NEdit = Menu.FIRST+2;//编辑
	private final int NNew = Menu.FIRST+3;//新建
	private final int NDelete = Menu.FIRST+4;//删除
	private final int NAll = Menu.FIRST+5;//全选?
	private final int NColse = Menu.FIRST+6;//关闭
	private final int NDeleteAll = Menu.FIRST+7;//删除所有
	private final int NChange = Menu.FIRST+8;//更改
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.notepad_note_list);
        list = getDiaryList(NoteSQL.selectNormal(this));//通过静态函数来获取日志列表
        adapter = new NoteAdapter(this, list);//新建日志适配器
        setListAdapter(adapter);   
	}
	
	@Override
	protected void onStart() {
		if(getIntent().hasExtra("mysort"))
        	seeSort(getIntent().getStringExtra("mysort").toString());
		if(list.size()>0)
        	setSelection(0);
		super.onStart();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteShow.class);
		i.putExtra("dedit", list.get(position).mid);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	
	private void seeSort(String sort){
		list.clear();
		DataBaseHelper helper = new DataBaseHelper(this, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor mc = db.rawQuery("select _id, "+
				DataBaseInfo.N_TITLE+","+
				DataBaseInfo.N_TIME+","+
				DataBaseInfo.N_SORT+" from "+
				DataBaseInfo.NOTE_TABLE+" where "+DataBaseInfo.N_SORT+" = '"+ sort +"' order by _id desc", null);
		list.addAll(getDiaryList(mc));
		adapter.notifyDataSetChanged();
		db.close();
	}
	
	private void seeAll(){
		list.clear();
		list.addAll(getDiaryList(NoteSQL.selectAll(this)));
		adapter.notifyDataSetChanged();
	}
	
	private List<NoteInfo> getDiaryList(Cursor cursor){
		List<NoteInfo> dl = new ArrayList<NoteInfo>();//生成一个数组列表
		cursor.moveToFirst();   
        while (!cursor.isAfterLast()) {
        	
        	NoteInfo diaryInfo = new NoteInfo();
        	diaryInfo.mid = cursor.getInt(0);
        	diaryInfo.mtitle = cursor.getString(1);
        	diaryInfo.mtime = cursor.getString(2);
        	diaryInfo.msort = cursor.getString(3);
        	dl.add(diaryInfo);
        	
        	cursor.moveToNext();  
        }
		return dl;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        setIconEnable(menu, true);  
		menu.add(0, NNew, Menu.NONE, "  新建").setIcon(R.drawable.notepad_nnew);
		menu.add(0, NEdit, Menu.NONE, "  修改").setIcon(R.drawable.notepad_edit);
		menu.add(0, NSort, Menu.NONE, "  查看分类").setIcon(R.drawable.notepad_sort);
		//menu.add(0, NChange, Menu.NONE, "旋转屏幕").setIcon(R.drawable.refresh);
		menu.add(0, NSearch, Menu.NONE, "  查找记事").setIcon(R.drawable.notepad_book_search);
		menu.add(0, NDelete, Menu.NONE, "  删除").setIcon(R.drawable.notepad_delete);
		menu.add(0, NAll, Menu.NONE, "  查看全部").setIcon(R.drawable.notepad_find);
		menu.add(0, NColse, Menu.NONE, "  关闭").setIcon(R.drawable.notepad_close);
		menu.add(0, NDeleteAll, Menu.NONE, "  清空记事").setIcon(R.drawable.notepad_clear);
		return true;
	} 
	
	///下面这个方法是为了使得菜单能够加载图标，不这么做加载不了图标。不过，到现在位置，还是没能实现
	//菜单横排列
	 //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效  
    private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            m.setAccessible(true);  
              
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)  
            m.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  
	
	
	
	
	@Override   
	public boolean onOptionsItemSelected(MenuItem item) {     
	  switch (item.getItemId()) {
	  	case NSort:
	  		Intent si = new Intent(this, SortList.class);
	  		startActivity(si);
	  		return true;
	  	case NChange:
	  		changeSreen();
	  		return true;
	  	case NSearch:
	  		addDialog();
	  		return true;
	  	case NEdit:
	  		if(getSelectedItemPosition()>=0){
	  			Intent edit = new Intent(this, NoteEdit.class);
	  			edit.putExtra("dedit", list.get(getSelectedItemPosition()).mid);
	  			startActivity(edit);
	  		}else
	  			Toast.makeText(NoteList.this, "您没有选择要修改的记事！", 
	  					Toast.LENGTH_LONG).show();
	  		return true;
	  	case NNew:
	  		Intent i = new Intent(this, NoteEdit.class);
	  		startActivity(i);
	  		finish();
	  		return true;
	  	case NDelete:
	  		if(getSelectedItemPosition() >=0)
	  			deleteOneDialog();
	  		else
	  			Toast.makeText(NoteList.this, "您没有选择要修改的记事！", 
	  					Toast.LENGTH_LONG).show();
	  		return true;
	  	case NAll:
	  		seeAll();
	  		return true;
	  	case NColse:
	  		finish();
	  		return true;
	  	case NDeleteAll:
	  		deleteAllDialog();
	  		return true;
	  }  
	  return false;
	} 
	
	private void deleteOneDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setTitle("温馨提示");  
        builder.setMessage("你确定要删除这篇记事:"+
        		list.get(getSelectedItemPosition()).mtitle+"吗？");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确定", new OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int which) {
        			int id = list.get(getSelectedItemPosition()).mid;
        			NoteSQL.deleteOne(NoteList.this, id);
        			list.remove(getSelectedItemPosition());
        			adapter.notifyDataSetChanged();
        	}
        });
        builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        builder.show();
	}
	
	private void deleteAllDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setTitle("温馨提示");  
        builder.setMessage("你确定要删除所有记事吗？");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确定", new OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int which) {
        		NoteSQL.deleteAll(NoteList.this);
          		list.clear();
          		adapter.notifyDataSetChanged();
        	}
        });
        builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        builder.show();
	}
	
	private void addDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
  		alert.setTitle("关键字搜索");
  		alert.setIcon(R.drawable.notepad_book_search);
  		LinearLayout l = new LinearLayout(this);
  		l.setPadding(5, 4, 5, 4);
  		final EditText input = new EditText(this);
  		input.setHint("请输入关键字");
  		l.addView(input, new LinearLayout.LayoutParams(
  				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
  		alert.setView(l);
  		alert.setPositiveButton("确定", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String searchStr = input.getText().toString().trim();
				if(searchStr.length()>0){
					DataBaseHelper helper = new DataBaseHelper(NoteList.this, DataBaseInfo.DB_NAME, null, 1);
					SQLiteDatabase db = helper.getReadableDatabase();
					Cursor c = db.rawQuery("select _id, "+
							DataBaseInfo.N_TITLE+","+DataBaseInfo.N_TIME+","+
							DataBaseInfo.N_SORT+" from "+DataBaseInfo.NOTE_TABLE+
							" where "+DataBaseInfo.N_TITLE+" like '%"+searchStr+"%' or "+
							DataBaseInfo.N_CONTENT+" like '%"+searchStr+"%' order by _id desc", null);
					
					list.clear();
					list.addAll(getDiaryList(c));
					adapter.notifyDataSetChanged();
					db.close();
				}else
					Toast.makeText(NoteList.this, "分类不能为空！", 
							Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
  		alert.setNegativeButton("取消", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
  		alert.show();
	}
	
	private void changeSreen(){
		if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
}
