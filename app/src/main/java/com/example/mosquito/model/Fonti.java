package com.example.mosquito.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.mosquito.Mosquito;
import java.util.LinkedList;

public class Fonti {
    private static Fonti f;
    private static SQLiteDatabase db;
    private static LinkedList<Fonte> fonti;

    private Fonti() {}

    public static Fonti getInstance(){
        if (f == null) {
            f =  new Fonti();
            db = DB.getInstance().getWritableDatabase();
            carica();
        }
        return f;
    }

    void close() {
        // dbH.close();
        db = null;
        f = null;
    }

    private static void carica() {
        fonti = new LinkedList<>();
        String [] tutte = new String [] {DB.TFonti.COLUMN_WEB, DB.TFonti.COLUMN_NOME};
        Cursor cursor = db.query(DB.TFonti.TABLE_LOCAL_DATA, tutte, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            fonti.add(new Fonte(cursor.getString(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public LinkedList<Fonte> getFonti() {return fonti;}

    public void aggiungiFonte(Fonte fonte) {
        db.insert(DB.TFonti.TABLE_LOCAL_DATA,null, DB.getInstance().convertiF(fonte.weblink, fonte.nome));
        fonti.add(fonte);
    }

    public void eliminaFonte(Fonte fonte) {
        db.delete(DB.TFonti.TABLE_LOCAL_DATA, DB.TFonti.COLUMN_NOME + " = ? and " + DB.TFonti.COLUMN_WEB + " = ?", new String[] {fonte.nome, fonte.weblink});
        fonti.remove(fonte);
    }
}



/*private static Fonti f;
private LinkedList<Fonte> fonti;

private Fonti() {
    fonti = new LinkedList<>(Arrays.asList(new Fonte("www.dpreview.com", "DPReview")));
}
public static Fonti getIstance(){
    if (f == null) f =  new Fonti();
    return f;
}

public void addFonte(Fonte fonte) {fonti.add(fonte); notifyObservers(fonte);}
public void remFonte(Fonte fonte) {fonti.remove(fonte); notifyObservers();}
public LinkedList<Fonte> getFonti() {return fonti;}*/