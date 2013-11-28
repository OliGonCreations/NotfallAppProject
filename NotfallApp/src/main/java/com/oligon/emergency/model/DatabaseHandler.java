package com.oligon.emergency.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oligon.emergency.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context mContext;

    private final static String DATABASE_NAME = "NotfallApp";
    private final static int DATABASE_VERSION = 2;

    private final static String SMS_TEMPLATE = "sms_template";
    private final static String TEL_NUMBERS = "tel_numbers";
    private final static String KEY_ID = "_id";
    private final static String KEY_SMS_TITLE = "sms_title";
    private final static String KEY_SMS_SUBJECT = "sms_subject";
    private final static String KEY_SMS_BODY = "sms_body";
    private final static String KEY_SMS_NUMBER = "sms_number";
    public final static String KEY_TEL_TITLE = "tel_title";
    public final static String KEY_TEL_NUMBER = "tel_number";
    public final static String KEY_TEL_IMG = "tel_img";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + SMS_TEMPLATE + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SMS_TITLE + " TEXT," + KEY_SMS_SUBJECT + " TEXT," + KEY_SMS_BODY + " TEXT," + KEY_SMS_NUMBER + " TEXT)");
        db.execSQL("CREATE TABLE " + TEL_NUMBERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEL_TITLE + " TEXT," + KEY_TEL_NUMBER + " TEXT," + KEY_TEL_IMG + " INTEGER)");
        initialize(db);
    }

    private void initialize(SQLiteDatabase db) {
        db.delete(TEL_NUMBERS, null, null);
        ContentValues values = new ContentValues();
        String[] titles = mContext.getResources().getStringArray(R.array.numbers_title);
        String[] numbers = mContext.getResources().getStringArray(R.array.numbers_number);
        String[] images = mContext.getResources().getStringArray(R.array.numbers_images);
        for (int i = 0; i < titles.length; i++) {
            values.put(KEY_TEL_TITLE, titles[i]);
            values.put(KEY_TEL_NUMBER, numbers[i]);
            values.put(KEY_TEL_IMG, mContext.getResources().getIdentifier(images[i], "drawable", "com.oligon.emergency"));
            db.insert(TEL_NUMBERS, null, values);
            values.clear();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TEMPLATE);
        db.execSQL("DROP TABLE IF EXISTS " + TEL_NUMBERS);
        onCreate(db);
    }

    public void addSMSTemp(String title, String subject, String body, String number) {
        ContentValues values = new ContentValues();
        values.put(KEY_SMS_TITLE, title);
        values.put(KEY_SMS_SUBJECT, subject);
        values.put(KEY_SMS_BODY, body);
        values.put(KEY_SMS_NUMBER, number);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(SMS_TEMPLATE, null, values);
        db.close();
    }

    public List<String> getSMSTitles() {
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
        if (cursor.moveToFirst()) {
            info.add(cursor.getString(2));
            info.add(cursor.getString(3));
            info.add(cursor.getString(4));
        }
        cursor.close();
        db.close();
        return info;
    }

    public void addTelNumber(String title, String number, int img) {
        ContentValues values = new ContentValues();
        values.put(KEY_TEL_TITLE, title);
        values.put(KEY_TEL_NUMBER, number);
        values.put(KEY_TEL_IMG, img);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TEL_NUMBERS, null, values);
        db.close();
    }

    public Cursor getAllNumbers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db != null ? db.query(TEL_NUMBERS, new String[]{KEY_ID, KEY_TEL_TITLE, KEY_TEL_NUMBER, KEY_TEL_IMG}, null, null, null, null, null) : null;
    }

    public ArrayList<String> getAllNumbersInList() {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TEL_NUMBERS, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1) + ": " + cursor.getString(2));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateNumbers(ArrayList<String> list) {
        SQLiteDatabase dbRead = this.getReadableDatabase();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Cursor cursor = dbRead.rawQuery("SELECT * FROM " + TEL_NUMBERS, null);
        if(cursor.moveToFirst()) {
            do {
                map.put(cursor.getString(1), cursor.getInt(3));
            } while (cursor.moveToNext());
        }
        dbRead.close();
        cursor.close();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEL_NUMBERS, null, null);
        ContentValues values = new ContentValues();
        for (String temp : list) {
            Log.d("test", temp);
            String[] array = temp.split(": ");
            values.put(KEY_TEL_TITLE, array[0]);
            values.put(KEY_TEL_NUMBER, array[1]);
            values.put(KEY_TEL_IMG, map.get(array[0]));
            Log.d("test", values.toString());
            db.insert(TEL_NUMBERS, null, values);
            values.clear();
        }
        db.close();

    }
}
