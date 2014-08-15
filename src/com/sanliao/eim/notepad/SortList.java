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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SortList extends ListActivity{
	private final int Snew = Menu.FIRST;
	private final int Sedit = Menu.FIRST+1;
	private final int Sdelete = Menu.FIRST+2;
	private final int SClose = Menu.FIRST+3;

	private ArrayAdapter<String> adapter;
	private List<String> spinnerlist;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_note_list);
        spinnerlist = new ArrayList<String>();
	    Cursor sc = SortSQL.select(this);
	    sc.moveToFirst();   
        while (!sc.isAfterLast()) { 
        	spinnerlist.add(sc.getString(0));
        	sc.moveToNext();  
        }
        
	   adapter = new ArrayAdapter<String>(this, 
			   android.R.layout.simple_list_item_1,spinnerlist);
	    setListAdapter(adapter); 
	    setTitle("分类管理");
	}
	
	private void listUpdate(){
		spinnerlist.clear();
		 Cursor sc = SortSQL.select(this);
		 sc.moveToFirst();   
	     while (!sc.isAfterLast()) { 
	        	spinnerlist.add(sc.getString(0));
	        	sc.moveToNext();  
	     }
	     adapter.notifyDataSetChanged(); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setIconEnable(menu, true);
		menu.add(0, Snew, Menu.NONE, "  添加分类").setIcon(R.drawable.notepad_add);
		menu.add(0, Sedit, Menu.NONE, "  修改分类").setIcon(R.drawable.notepad_edit);
		menu.add(0, Sdelete, Menu.NONE, "  删除分类").setIcon(R.drawable.notepad_delete);
		menu.add(0, SClose, Menu.NONE, "  关闭").setIcon(R.drawable.notepad_close);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteList.class);
		i.putExtra("mysort", spinnerlist.get(position).toString());
		startActivity(i);
		finish();
	}
	
	@Override   
	public boolean onOptionsItemSelected(MenuItem item) {     
	  switch (item.getItemId()) {   
	  	case Snew: {
	  		addDialog();
	  		return true;
	  	}
	  	case Sedit:{
	  		int i = getSelectedItemPosition();
	  		if(i >=0)
	  			editDialog(i);
	  		else
	  			Toast.makeText(SortList.this, "没有选择要修改的分类！", 
						Toast.LENGTH_LONG).show();
	  		return true;
	  	}
	  	case Sdelete:
	  		int i = getSelectedItemPosition();
	  		if(i >=0)
	  			delDialog(i);
	  		else
	  			Toast.makeText(SortList.this, "没有选择要删除的分类！", 
						Toast.LENGTH_LONG).show();
	  		return true;
	  	case SClose:
	  		finish();
	  		return true;
	  	default:
		  return false;
	  }   
	} 
	
	private void addDialog(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
  		alert.setTitle("添加一个分类");
  		LinearLayout l = new LinearLayout(this);
  		l.setPadding(5, 4, 5, 4);
  		final EditText input = new EditText(this);
  		l.addView(input, new LinearLayout.LayoutParams(
  				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
  		alert.setView(l);
  		alert.setPositiveButton("确定", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sort = input.getText().toString();
				if(sort.trim().length()>0)
					addSort(input.getText().toString());
				else
					Toast.makeText(SortList.this, "分类不能为空！", 
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
	
	private void addSort(String sort){
		DataBaseHelper helper = new DataBaseHelper(this, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from "+DataBaseInfo.SORT_TABLE+" where "+
				DataBaseInfo.NOTE_SORT+"='"+sort+"'", null);
		if(c.getCount()>0)
			Toast.makeText(this, "分类已经存在！", Toast.LENGTH_LONG).show();
		else{
			SortSQL.insert(this, sort);
			adapter.add(sort);
		}
		db.close();
	}
	
	private void delDialog(int sItem){
		final String s = spinnerlist.get(sItem).toString();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("温馨提示")
		.setIcon(android.R.drawable.ic_delete);
		alert.setMessage("你确定要删除分类："+ s);
		alert.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SortSQL.delete(SortList.this, s);
				adapter.remove(s);
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
	
	private void editDialog(int Sitem){
		final String s = spinnerlist.get(Sitem).toString();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
  		alert.setTitle("把“"+s+"”修改为 ：");
  		LinearLayout l = new LinearLayout(this);
  		l.setPadding(5, 4, 5, 4);
  		final EditText input = new EditText(this);
  		input.setText(s);
  		l.addView(input, new LinearLayout.LayoutParams(
  				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
  		alert.setView(l);
  		
  		alert.setPositiveButton("确定", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SortSQL.update(SortList.this, input.getText().toString(), s);
				listUpdate();
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
	
}
