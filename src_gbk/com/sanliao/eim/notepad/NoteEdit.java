package com.sanliao.eim.notepad;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sanliao.eim.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class NoteEdit extends Activity{//编辑记事本
	private EditText title,content;//标题，内容
	private Spinner mSort;//下拉列表
	private int nid= 0;
	private ArrayAdapter<String> adapter;
	private Boolean isEdit;
	private final int Nsave=Menu.FIRST;
	private final int Nlist=Menu.FIRST + 1;
	private final int mClose=Menu.FIRST + 2;
	//private Button saveButton;
	//protected Context context = null;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.notepad_edit);
	   //context = this;
	    nid = getIntent().getIntExtra("dedit", 0);
	    isEdit = (nid > 0);	    
	    
	    title = (EditText)findViewById(R.id.mtitle);
	    content = (EditText)findViewById(R.id.mcontent);
	    mSort = (Spinner)findViewById(R.id.msort);
	    Button btn = (Button)findViewById(R.id.newsort);
	    
	   Button saveButton=(Button)findViewById(R.id.btn_save);
	    
	    List<String> spinnerlist = new ArrayList<String>();
	    Cursor sc = SortSQL.select(this);
	    sc.moveToFirst();   
        while (!sc.isAfterLast()) { 
        	spinnerlist.add(sc.getString(0));
        	sc.moveToNext();  
        }
        
	   adapter = new ArrayAdapter<String>(this, 
			   android.R.layout.simple_spinner_item,spinnerlist);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	    mSort.setAdapter(adapter);
	    
	    mSort.setSelected(true);
	    mSort.setPrompt("选择你的分类");
	    if(isEdit){
	    	setTitle("修改记事");
	    	getContent(nid);
	    }else
	    	setTitle("写一篇记事");
	    btn.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
			addDialog();
				
			}
		});
	    saveButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
		    saveNote();
			}
		});
	 
	}
	
	private void saveNote()
	{
		//保存到数据库中
		String t = title.getText().toString();
		String c = content.getText().toString();
		String s= mSort.getSelectedItem().toString();
		if(isEdit && t.trim().length()>0 && c.trim().length()>0 && s.length()>0)
			NoteSQL.update(NoteEdit.this, t, c, s, nid);
		else if(t.trim().length()>0 && c.trim().length()>0 && s.length()>0)
			NoteSQL.insert(NoteEdit.this, t, c, s);
			
		Intent i = new Intent(this, NoteList.class);
  		startActivity(i);
  		finish();
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
					Toast.makeText(NoteEdit.this, "分类不能为空！", 
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
	private void getContent(int id){
		int selestItem = 0;
		String sort = null;
		Cursor c=NoteSQL.selectOne(this,id);
		c.moveToFirst();   
        while (!c.isAfterLast()) { 
        	title.setText(c.getString(0));
        	content.setText(c.getString(1));
        	sort = c.getString(2);
        	c.moveToNext();  
        }
        
        DataBaseHelper helper = new DataBaseHelper(this, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor _sort = db.rawQuery("select * from "+
				DataBaseInfo.SORT_TABLE+" order by _id desc", null);
		_sort.moveToFirst();   
        while (!_sort.isAfterLast()) { 
        	if(sort != null && _sort.getString(1).equals(sort))
        		break;
        	selestItem++;
        	_sort.moveToNext();  
        }
        mSort.setSelection(selestItem);
        db.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setIconEnable(menu, true);
		menu.add(0, Nsave, Menu.NONE, "  保存").setIcon(R.drawable.notepad_save);
		menu.add(0, Nlist, Menu.NONE, "  转到列表").setIcon(R.drawable.notepad_redo);
		menu.add(0, mClose, Menu.NONE, "  关闭").setIcon(R.drawable.notepad_close);
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
	  	case Nsave: {
	  		String t = title.getText().toString();
			String c = content.getText().toString();
			String s= mSort.getSelectedItem().toString();
			if(isEdit && t.trim().length()>0 && c.trim().length()>0 && s.length()>0)
				NoteSQL.update(NoteEdit.this, t, c, s, nid);
			else if(t.trim().length()>0 && c.trim().length()>0 && s.length()>0){
				NoteSQL.insert(NoteEdit.this, t, c, s);
				Intent i = new Intent(this, NoteList.class);
		  		startActivity(i);
		  		finish();
			}else
				Toast.makeText(NoteEdit.this, "温馨提示：\n标题或内容不能为空！", 
						Toast.LENGTH_LONG).show();
	  		return true;
	  	}
	  	case Nlist:{
	  		Intent i = new Intent(this, NoteList.class);
	  		startActivity(i);
	  		finish();
	  		return true;
	  	}
	  	case mClose:
	  		finish();
	  		return true;
	  	default:
		  return false;
	  }   
	} 
}
