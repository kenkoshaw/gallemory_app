package com.example.kenkoku.gallemory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "photos.db";
    private static final String TABLE_PHOTOS = "photos";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_IMAGE = "image_data";
    private static final String COLUMN_TITLE = "_title";
    private static final String COLUMN_CAPTION = "_caption";
    private static final String COLUMN_DATE = "_date";
    private static final String COLUMN_ROUND = "_round";
    private static int key_id;
    private static SimpleDateFormat formatter;
    private static Date currentDate;

    MyDBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        setStartID();
        formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String photoQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_PHOTOS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_IMAGE + " BLOB, " + COLUMN_TITLE + " TEXT NOT NULL, " + COLUMN_CAPTION + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_ROUND + " INT " +
                ");";
        db.execSQL(photoQuery);
        db.close();
    }

    public void setStartID(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PHOTOS,
                null);

        Log.v("PREVIOUS ID", "ID_: " + key_id);
        if (!isEmpty()) {
            c.moveToLast();
            key_id = c.getInt(c.getColumnIndex(COLUMN_ID)) + 1;
            Log.v("NEW ID", "ID_: " + key_id);
        } else {
            key_id = 1;
        }
        Log.v("FINAL ID", "ID_: " + key_id);
        c.close();
    }

    public boolean isEmpty(){
        boolean empty = true;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PHOTOS, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();

        return empty;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    public static int getKey_id() {
        return key_id;
    }

    public int addEntry(MyData imgData, String title, String caption, String date, boolean round) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, key_id);
        values.put(COLUMN_IMAGE, imgData.getImageData());
        if (title.isEmpty()){
            title = "Image " + key_id;
        }
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CAPTION, caption);
        if (date.isEmpty()){
            currentDate = new Date();
            date = formatter.format(currentDate);
        }
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_ROUND, round);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PHOTOS, null, values);
        db.close();
        int toRet = key_id;
        key_id += 1;
        return toRet;
    }

    public void deleteEntry(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PHOTOS + " WHERE " + COLUMN_ID + "=\"" + id + "\";");
        db.close();
    }

    public void updateEntry(int id, String title, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_PHOTOS + " SET " + COLUMN_TITLE + "=\"" + title + "\"," +
                COLUMN_CAPTION + "=\"" + comment + "\"" + " WHERE " + COLUMN_ID + "=\"" + id + "\";");
        db.close();
    }

    public MyData getImage(int id) {
        Cursor c = getCursor(id);
        MyData img = new MyData(c.getBlob(c.getColumnIndex(COLUMN_IMAGE)));
        c.close();
        return img;
    }

    public String getTitle(int id) {
        Cursor c = getCursor(id);
        String title =  c.getString(c.getColumnIndex(COLUMN_TITLE));
        c.close();
        return title;
    }

    public String getCaption(int id) {
        Cursor c = getCursor(id);
        String caption = c.getString(c.getColumnIndex(COLUMN_CAPTION));
        c.close();
        return caption;
    }

    public String getDate(int id){
        Cursor c = getCursor(id);
        String date = c.getString(c.getColumnIndex(COLUMN_DATE));
        c.close();
        return date;
    }

    public boolean getRound(int id) {
        Cursor c = getCursor(id);
        int boolVal = c.getInt(c.getColumnIndex(COLUMN_ROUND));
        c.close();
        return boolVal > 0;
    }

    private Cursor getCursor(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PHOTOS +
                        " WHERE " + COLUMN_ID + "=\"" + id + "\";",
                null);
        c.moveToFirst();
        return c;
    }

    public ArrayList<MyData> getAllImages() {
        String query = "SELECT * FROM " + TABLE_PHOTOS;
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        ArrayList<MyData> photoList = new ArrayList<>();

        if (c.getCount() > 0) {
            byte[] image;
            while (c.moveToNext()) {
                image = c.getBlob(c.getColumnIndex(COLUMN_IMAGE));
                photoList.add(new MyData(image));
            }
        }
        c.close();
        return photoList;
    }

    public ArrayList<Integer> getAllIds() {
        String query = "SELECT * FROM " + TABLE_PHOTOS;
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        ArrayList<Integer> idList = new ArrayList<>();

        if (c.getCount() > 0) {
            int id;
            while (c.moveToNext()) {
                id = c.getInt(c.getColumnIndex(COLUMN_ID));
                idList.add(id);
            }
        }
        c.close();
        return idList;
    }
}