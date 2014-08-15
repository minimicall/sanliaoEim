package com.sanliao.eim.notepad;

import com.sanliao.eim.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

public class NoteBook extends Activity {
	private final static String TAG="NoteBook";
	private SharedPreferences sp;//�ṩ��ֵ�Դ洢����
	private Intent intent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        intent = new Intent(NoteBook.this, NoteList.class);
        
        sp = getSharedPreferences("notebook_authentication", Context.MODE_PRIVATE);
        if(sp.getString("keys", null) !=null && sp.getString("keys", null).equals("privateKey")){
        	Log.d(TAG,"start NoteList activity now.");
        	startActivity(intent);
        	finish();//���ٱ�activity
        }else{
        	aboutDialog();//�����Ի���
        }
    }
    
    private void aboutDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setTitle("Android ���±�");  
        builder.setMessage("��ӭʹ�� Android���±�\n�汾��1.0\n" +
        		"֧��Android OS 2.1��������\n���ߣ�������\n" +
        		"��ҳ:http://blog.csdn.net/minimicall");
        builder.setIcon(R.drawable.notepad_icon);
        builder.setPositiveButton("ȷ��", new OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int which) {
        		(sp.edit()).putString("keys", "privateKey").commit();
                startActivity(intent);
                finish();
        	}
        });
        builder.setNegativeButton("ȡ��", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
        builder.show();
	}
}