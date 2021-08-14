package com.example.mosquito.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import com.example.mosquito.Mosquito;

import java.io.Serializable;

public class DB extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "Mosquito";
    static final int DATABASE_VERSION = 7;
    private static DB db;

    private DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DB getInstance() {
        return db == null ? db = new DB(Mosquito.context) : db;
    }

    public static class TFonti implements BaseColumns {
        public static final String TABLE_LOCAL_DATA = "fonti", COLUMN_WEB = "weblink", COLUMN_NOME = "nome";}
    public static class TImpo implements BaseColumns {
        public static final String TABLE_LOCAL_DATA = "impostazioni", COLUMN_ID = "impoid", COLUMN_VAL = "val";}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        String query = "create table " + TFonti.TABLE_LOCAL_DATA + " (" + TFonti._ID + " integer primary key, "
                + TFonti.COLUMN_NOME + " varchar not null, " + TFonti.COLUMN_WEB + "  varchar not null);";
        db.execSQL(query);
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.dpreview.com/feeds/news.xml", "DPReview"));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.hdblog.it/feed/", "HDBlog"));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.sonyalpharumors.com/feed/", "SonyAlphaRumors"));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://mspoweruser.com/feed/", "MSPoweruser"));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.tomshw.it/feed/", "Tom's Hardware"));

        query = "create table " + TImpo.TABLE_LOCAL_DATA + " (" + TImpo._ID + " integer primary key, "
                + TImpo.COLUMN_ID + " integer not null, " + TImpo.COLUMN_VAL + "  varchar not null);";
        db.execSQL(query);
        db.insert(TImpo.TABLE_LOCAL_DATA, null, convertiI(0, "true"));

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TFonti.TABLE_LOCAL_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TImpo.TABLE_LOCAL_DATA);
        onCreate(db);
    }

    public static ContentValues convertiF(String weblink, String nome) {
        ContentValues cv = new ContentValues();
        cv.put(TFonti.COLUMN_WEB, weblink);
        cv.put(TFonti.COLUMN_NOME, nome);
        return cv;
    }

    public static ContentValues convertiI(int id, String object) {
        ContentValues cv = new ContentValues();
        cv.put(TImpo.COLUMN_ID, id);
        cv.put(TImpo.COLUMN_VAL, object);
        return cv;
    }

    public String ottieniImpostazione (int id) {
        Cursor cursor = db.getWritableDatabase().query(DB.TImpo.TABLE_LOCAL_DATA, new String[]{DB.TImpo.COLUMN_VAL},
                DB.TImpo.COLUMN_ID + " = ?", new String[]{""+id}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String ret =  cursor.getString(0);
            cursor.close();
            return ret;
        }
        cursor.close();
        return null;
    }

    public void scriviImpostazione (int id, String val) {
        db.getWritableDatabase().update(DB.TImpo.TABLE_LOCAL_DATA, convertiI(id, val), TImpo.COLUMN_ID + " = ?", new String[]{""+id});
    }
}