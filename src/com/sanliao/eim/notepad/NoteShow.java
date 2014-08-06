package com.sanliao.eim.notepad;

import java.lang.reflect.Method;

import com.sanliao.eim.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class NoteShow extends Activity{
	private int nid= 0;
	private final int mEdit=Menu.FIRST;
	private final int mChange=Menu.FIRST + 1;
	private final int mClose=Menu.FIRST + 2;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    nid = getIntent().getIntExtra("dedit", 0);
	    final Boolean isEdit = (nid > 0);
	    
	    setContentView(R.layout.notepad_show);
	    TextView show = (TextView)findViewById(R.id.show);

	    if(isEdit){
	    	Cursor c=NoteSQL.selectOne(this,nid);
	    	c.moveToFirst();   
	    	while (!c.isAfterLast()) { 
	    		setTitle(c.getString(0)+"-��������:"+c.getString(2));
	    		show.setText(c.getString(1));
	    		c.moveToNext();   
	    	}
	    }else{
	    	setTitle(getResources().getString(R.string.app_name));
    		show.setText("��ӭʹ�� Android ���±�");
	    }
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setIconEnable(menu, true);
		menu.add(0, mEdit, Menu.NONE, "  �޸�").setIcon(R.drawable.notepad_edit);
		//menu.add(0, mChange, Menu.NONE, "ת����Ļ").setIcon(R.drawable.refresh);
		menu.add(0, mClose, Menu.NONE, "  �ر�").setIcon(R.drawable.notepad_close);
		return true;
	} 

	///�������������Ϊ��ʹ�ò˵��ܹ�����ͼ�꣬����ô�����ز���ͼ�ꡣ������������λ�ã�����û��ʵ��
	//�˵�������
	 //enableΪtrueʱ���˵����ͼ����Ч��enableΪfalseʱ��Ч��4.0ϵͳĬ����Ч  
    private void setIconEnable(Menu menu, boolean enable)  
    {  
        try   
        {  
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");  
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);  
            m.setAccessible(true);  
              
            //MenuBuilderʵ��Menu�ӿڣ������˵�ʱ����������menu��ʵ����MenuBuilder����(java�Ķ�̬����)  
            m.invoke(menu, enable);  
              
        } catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
    }  
	
	
	@Override   
	public boolean onOptionsItemSelected(MenuItem item) {     
	  switch (item.getItemId()) {
	  case mEdit:
		Intent i = new Intent(NoteShow.this, NoteEdit.class);
		i.putExtra("dedit", nid);
        startActivity(i);
        finish();
		return true;
	  case mChange:
		changeSreen();
		return true;
	  case mClose:
		finish();
		return true;
	  default:
		return false;
	  }
	}
	
	private void changeSreen(){
		if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
}
