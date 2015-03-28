package com.example.nilesh.database;

/**
 * Created by Richa on 3/28/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "HIKE_DB";
    // tasks table name
    private static final String TABLE_NAME1 = "HIKE_MSG";
    private static final String TABLE_NAME2 = "HIKE_PRIORITY";
    // Table1 Columns names
    private static final String FROM = "from_user";
    private static final String TO = "to_user";
    private static final String ID = "id";

    // Table2 Columns names
    private static final String MSG = "message";
    // Table3 Columns names
    private static final String PRIORITY = "priority";





    private SQLiteDatabase dbase;
    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME1 + " ( "
                + ID +" INTEGER AUTO INCREMENT, "
                + TO +" TEXT, "
                + FROM + " TEXT  KEY, "
                + MSG + " TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " ( "
                + FROM + " TEXT , "
                + PRIORITY + " TEXT)";
        db.execSQL(sql);
    }


    public void insertMessages(String to_user_id, String from_user_id,String msg){
        dbase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TO, to_user_id);
        values.put(FROM, from_user_id);
        values.put(MSG, msg);
        dbase.insert(TABLE_NAME1, null, values);

    }
    public void insertPriority(String from_user_id,String priority){
        dbase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FROM, from_user_id);
        values.put(PRIORITY,priority);
        Log.v("TAG", "inserting data" + from_user_id + ":" + priority);
        dbase.insert(TABLE_NAME2, null, values);
    }

    public List<String> getAllMessage(String from_user){
        List msgs = new ArrayList<String>();
        dbase = getWritableDatabase();
        String sql="SELECT * FROM "+TABLE_NAME1 + "  WHERE "+ FROM +"= '"+ from_user +"' union SELECT * FROM "+TABLE_NAME1+" WHERE "+ TO +"= '"+ from_user +"' order by "+ID+" desc";
        dbase=getReadableDatabase();
        Cursor cursor=dbase.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                msgs.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return  msgs;
    }
    public String getPriority(String from_user){
        String priority="";
        String sql="SELECT * FROM "+TABLE_NAME2 + "  WHERE "+ FROM +" = '"+ from_user +"'";
        dbase=getReadableDatabase();
        Cursor cursor=dbase.rawQuery(sql,null);
        if(cursor.moveToFirst()){
            do {
                priority = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        return priority;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
