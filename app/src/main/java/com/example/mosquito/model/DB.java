package com.example.mosquito.model;
import com.example.mosquito.Mosquito;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "Mosquito";
    static final int DATABASE_VERSION = 19;
    private static DB db;

    private DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DB getInstance() {
        return db == null ? db = new DB(Mosquito.context) : db;
    }

    public static class TFonti implements BaseColumns {
        public static final String TABLE_LOCAL_DATA = "fonti", COLUMN_WEB = "weblink", COLUMN_NOME = "nome", COLUMN_NOTIFICA = "notifica";}
    public static class TImpo implements BaseColumns {
        public static final String TABLE_LOCAL_DATA = "impostazioni", COLUMN_ID = "impoid", COLUMN_VAL = "val";}
    public static class TNL implements BaseColumns {
        public static final String TABLE_LOCAL_DATA = "tnl", COLUMN_NOT = "guid";}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        String query = "create table " + TFonti.TABLE_LOCAL_DATA + " (" + TFonti._ID + " integer primary key, "
                + TFonti.COLUMN_NOME + " varchar not null, " + TFonti.COLUMN_WEB + "  varchar not null, " + TFonti.COLUMN_NOTIFICA + " integer);";
        db.execSQL(query);
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.dpreview.com/feeds/news.xml", "DPReview", false));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.hdblog.it/feed/", "HDBlog", false));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.sonyalpharumors.com/feed/", "SonyAlphaRumors", false));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://mspoweruser.com/feed/", "MSPoweruser", false));
        db.insert(TFonti.TABLE_LOCAL_DATA, null, convertiF("https://www.tomshw.it/feed/", "Tom's Hardware", false));

        query = "create table " + TImpo.TABLE_LOCAL_DATA + " (" + TImpo._ID + " integer primary key, "
                + TImpo.COLUMN_ID + " integer not null, " + TImpo.COLUMN_VAL + "  varchar not null);";
        db.execSQL(query);
        db.insert(TImpo.TABLE_LOCAL_DATA, null, convertiI(0, "true"));
        db.insert(TImpo.TABLE_LOCAL_DATA, null, convertiI(1, "normale"));
        db.insert(TImpo.TABLE_LOCAL_DATA, null, convertiI(2, "inapp"));

        query = "create table " + TNL.TABLE_LOCAL_DATA + " (" + TNL._ID + " integer primary key, "
                + TNL.COLUMN_NOT + " varchar not null);";
        db.execSQL(query);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TFonti.TABLE_LOCAL_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TImpo.TABLE_LOCAL_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TNL.TABLE_LOCAL_DATA);
        onCreate(db);
    }

    public static ContentValues convertiF(String weblink, String nome, boolean notifiche) {
        ContentValues cv = new ContentValues();
        cv.put(TFonti.COLUMN_WEB, weblink);
        cv.put(TFonti.COLUMN_NOME, nome);
        cv.put(TFonti.COLUMN_NOTIFICA, notifiche ? 1 : 0);
        return cv;
    }

    public static ContentValues convertiI(int id, String object) {
        ContentValues cv = new ContentValues();
        cv.put(TImpo.COLUMN_ID, id);
        cv.put(TImpo.COLUMN_VAL, object);
        return cv;
    }

    public static ContentValues convertiS(String guid) {
        ContentValues cv = new ContentValues();
        cv.put(TNL.COLUMN_NOT, guid);
        return cv;
    }

    public String ottieniImpostazione (int id) {
        Cursor cursor = db.getWritableDatabase().query(DB.TImpo.TABLE_LOCAL_DATA, new String[]{TImpo.COLUMN_VAL},
                TImpo.COLUMN_ID + " = ?", new String[]{""+id}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            String ret =  cursor.getString(0);
            cursor.close();
            return ret;
        }
        cursor.close();
        return null;
    }

    public void scriviImpostazione (int id, String val) {
        db.getWritableDatabase().update(TImpo.TABLE_LOCAL_DATA, convertiI(id, val), TImpo.COLUMN_ID + " = ?", new String[]{""+id});
    }

    public boolean letta(String guid) {
        Cursor cursor = db.getWritableDatabase().query(TNL.TABLE_LOCAL_DATA, new String[]{TNL.COLUMN_NOT}, TNL.COLUMN_NOT + " = ?", new String[]{guid}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void marcaLetta(String guid) {
        if (letta(guid)) return;
        db.getWritableDatabase().insert(TNL.TABLE_LOCAL_DATA, null, convertiS(guid));
    }

    public void marcaNonLetta(String guid) {
        db.getWritableDatabase().delete(TNL.TABLE_LOCAL_DATA, TNL.COLUMN_NOT + " = ? ", new String[]{guid});
    }
}