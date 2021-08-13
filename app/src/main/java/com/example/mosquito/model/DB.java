package com.example.mosquito.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "Mosquito";
    static final int DATABASE_VERSION = 1;

    public DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static class TFonti implements BaseColumns {
        static final String TABLE_LOCAL_DATA = "fonti", COLUMN_WEB = "weblink", COLUMN_NOME = "nome";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        String query = "create table " + TFonti.TABLE_LOCAL_DATA + " (" + TFonti._ID + " integer primary key, "
                + TFonti.COLUMN_NOME + " varchar not null, " + TFonti.COLUMN_WEB + "  varchar not null);";
        db.execSQL(query);

        db.insert(TFonti.TABLE_LOCAL_DATA, null, converti("www.dpreview.com", "DPReview"));
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TFonti.TABLE_LOCAL_DATA);
        onCreate(db);
    }

    public ContentValues converti(String weblink, String nome) {
        ContentValues cv = new ContentValues();
        cv.put(TFonti.COLUMN_WEB, weblink);
        cv.put(TFonti.COLUMN_NOME, nome);
        return cv;
    }
}