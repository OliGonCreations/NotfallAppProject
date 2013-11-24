package com.oligon.emergency.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonas on 24.11.13.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "NotfallApp";
    private final static int DATABASE_VERSION = 1;

    private final static String SMS_TEMPLATE = "sms_template";
    private final static String KEY_ID = "id";
    private final static String KEY_TITLE = "sms_title";
    private final static String KEY_SUBJECT = "sms_subject";
    private final static String KEY_BODY = "sms_body";
    private final static String KEY_NUMBER = "sms_number";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SMS_TEMPLATE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_SUBJECT + " TEXT," + KEY_BODY + " TEXT," + KEY_NUMBER + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TEMPLATE);
        onCreate(db);
    }

    public void addSMSTemp(String title, String subject, String body, String number) {
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, title);
        values.put(KEY_SUBJECT, subject);
        values.put(KEY_BODY, body);
        values.put(KEY_NUMBER, number);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(SMS_TEMPLATE, null, values);
        db.close();
    }

    public List<String> getSMSTitles(){
        List<String> titles = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SMS_TEMPLATE, null);

        if (cursor.moveToFirst()) {
            do {
                titles.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return titles;
    }

    public List<String> getSMSInfo(int id) {
        List<String> info = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SMS_TEMPLATE + " WHERE " + KEY_ID + "=" + id, null);
        if(cursor.moveToFirst()) {
            info.add(cursor.getString(2));
            info.add(cursor.getString(3));
            info.add(cursor.getString(4));
        }
        cursor.close();
        db.close();
        return info;
    }
}
