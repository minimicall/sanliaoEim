package com.sanliao.eim.notepad;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class NoteSQL {
	//SQL操作
	public static void insert(Context context, String title, String content, String sort){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
		String mtime = dateFormat.format(new Date());
		String sql = "insert into "+ DataBaseInfo.NOTE_TABLE+ "(" +
			DataBaseInfo.N_TITLE+", " +DataBaseInfo.N_CONTENT+", "+DataBaseInfo.N_TIME +", "+DataBaseInfo.N_SORT+")" +
			"values('"+title+"', '" + content + "', '" + mtime+"', '"+ sort +"');";
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "已经写入一篇新的记事！", Toast.LENGTH_LONG).show();
	}
	
	public static void update(Context context, String title, String content, String sort, int id){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
		String mtime = dateFormat.format(new Date());
		String sql = "UPDATE "+DataBaseInfo.NOTE_TABLE+" SET "+
			DataBaseInfo.N_TITLE+" = '"+title+"',"+
			DataBaseInfo.N_CONTENT+" = '"+content+"',"+
			DataBaseInfo.N_TIME+" = '"+mtime+"',"+
			DataBaseInfo.N_SORT+" = '"+sort+
			"' WHERE _id = "+id;
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "修改成功！", Toast.LENGTH_LONG).show();
	}
	
	public static void deleteOne(Context context, int id){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "DELETE FROM "+DataBaseInfo.NOTE_TABLE +" WHERE _id = "+ id;
		db.execSQL(sql);
		db.close();
		Toast.makeText(context, "删除成功！", Toast.LENGTH_LONG).show();
	}
	
	public static void deleteAll(Context context){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "DELETE FROM "+DataBaseInfo.NOTE_TABLE;
		db.execSQL(sql);
		db.execSQL("DELETE FROM "+DataBaseInfo.SORT_TABLE);
		db.close();
		Toast.makeText(context, "成功清空所有记事！", Toast.LENGTH_LONG).show();
	}
	
	public static Cursor selectAll(Context context){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select _id, "+DataBaseInfo.N_TITLE+","+DataBaseInfo.N_TIME+","+DataBaseInfo.N_SORT+" from "+DataBaseInfo.NOTE_TABLE+" order by _id desc", null);
		return c;
	}
	
	public static Cursor selectNormal(Context context){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select _id, "+DataBaseInfo.N_TITLE+","+DataBaseInfo.N_TIME+","+DataBaseInfo.N_SORT+" from "+DataBaseInfo.NOTE_TABLE+" order by _id desc limit 20", null);
		return c;
	}
	
	public static Cursor selectOne(Context context, int id){
		DataBaseHelper helper = new DataBaseHelper(context, DataBaseInfo.DB_NAME, null, 1);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] whereValue={Integer.toString(id)};
		Cursor c=db.query(
			DataBaseInfo.NOTE_TABLE, new String[]{
				DataBaseInfo.N_TITLE, 
				DataBaseInfo.N_CONTENT, 
				DataBaseInfo.N_SORT
		    }, "_id=?", whereValue, null, null, null);
		return c;
	}
}
