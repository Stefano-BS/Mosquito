package com.example.mosquito.model;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.LinkedList;

public class Fonti {
    private static Fonti f;
    private static LinkedList<Fonte> fonti;
    public LinkedList<Fonte> getFonti() {return fonti;}

    private Fonti() {}

    public static Fonti getInstance(){
        if (f == null) {
            f =  new Fonti();
            fonti = new LinkedList<>();
            String [] tutte = new String [] {DB.TFonti.COLUMN_WEB, DB.TFonti.COLUMN_NOME, DB.TFonti.COLUMN_NOTIFICA};
            Cursor cursor = DB.getInstance().getWritableDatabase().query(DB.TFonti.TABLE_LOCAL_DATA, tutte, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                fonti.add(new Fonte(cursor.getString(0), cursor.getString(1), cursor.getInt(2)==1));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return f;
    }

    public void aggiungiFonte(Fonte fonte) {
        DB.getInstance().getWritableDatabase().insert(DB.TFonti.TABLE_LOCAL_DATA,null, DB.convertiF(fonte.weblink, fonte.nome, fonte.notifiche));
        fonti.add(fonte);
    }

    public void eliminaFonte(Fonte fonte) {
        DB.getInstance().getWritableDatabase().delete(DB.TFonti.TABLE_LOCAL_DATA, DB.TFonti.COLUMN_NOME + " = ? and " + DB.TFonti.COLUMN_WEB + " = ?", new String[] {fonte.nome, fonte.weblink});
        fonti.remove(fonte);
    }

    public void notificaFonte(Fonte fonte) {
        ContentValues cv = new ContentValues();
        cv.put(DB.TFonti.COLUMN_NOTIFICA, fonte.notifiche ? 1 : 0);
        DB.getInstance().getWritableDatabase().update(DB.TFonti.TABLE_LOCAL_DATA, cv, DB.TFonti.COLUMN_NOME + " = ? and " + DB.TFonti.COLUMN_WEB + " = ?", new String[] {fonte.nome, fonte.weblink});
    }
}