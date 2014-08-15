package com.sanliao.eim.notepad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBaseHelper extends SQLiteOpenHelper {
	//数据库辅助类
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//创建一个表格
		db.execSQL("CREATE TABLE IF NOT EXISTS "+
					DataBaseInfo.NOTE_TABLE+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
					DataBaseInfo.N_TITLE+" VARCHAR,"+
					DataBaseInfo.N_CONTENT+" TEXT,"+
					DataBaseInfo.N_TIME+" VARCHAR,"+
					DataBaseInfo.N_SORT+" VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "+
				DataBaseInfo.SORT_TABLE+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
				DataBaseInfo.NOTE_SORT+" VARCHAR)");	
	}
//更新
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+DataBaseInfo.NOTE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "+DataBaseInfo.SORT_TABLE);
        onCreate(db);	
	}

}
