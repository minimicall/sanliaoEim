package com.sanliao.eim.notepad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class SortSQL {
	public static void insert(Context context, String sort){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "insert into "+ DataBaseInfo.SORT_TABLE+ "(" +
			DataBaseInfo.NOTE_SORT+") values ('"+sort+"')";
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "成功添加分类！", Toast.LENGTH_LONG).show();
	}
	
	public static void update(Context context, String newsort, String oldsort){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "UPDATE "+DataBaseInfo.SORT_TABLE+" SET "+
			DataBaseInfo.NOTE_SORT+" = '"+newsort+
			"' WHERE "+DataBaseInfo.NOTE_SORT+" = '"+oldsort+"'";
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
	}
	
	public static void delete(Context context, String sort){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "DELETE FROM "+DataBaseInfo.SORT_TABLE +" WHERE "+DataBaseInfo.NOTE_SORT+" = '"+ sort +"'";
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "删除成功！", Toast.LENGTH_LONG).show();
	}
	
	public static Cursor select(Context context){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select "+DataBaseInfo.NOTE_SORT+" from "+
				DataBaseInfo.SORT_TABLE+" order by _id desc", null);
		return c;
	}
}
