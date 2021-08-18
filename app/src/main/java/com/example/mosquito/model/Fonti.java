package com.example.mosquito.model;

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
            String [] tutte = new String [] {DB.TFonti.COLUMN_WEB, DB.TFonti.COLUMN_NOME};
            Cursor cursor = DB.getInstance().getWritableDatabase().query(DB.TFonti.TABLE_LOCAL_DATA, tutte, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                fonti.add(new Fonte(cursor.getString(0), cursor.getString(1)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return f;
    }

    public void aggiungiFonte(Fonte fonte) {
        DB.getInstance().getWritableDatabase().insert(DB.TFonti.TABLE_LOCAL_DATA,null, DB.convertiF(fonte.weblink, fonte.nome));
        fonti.add(fonte);
    }

    public void eliminaFonte(Fonte fonte) {
        DB.getInstance().getWritableDatabase().delete(DB.TFonti.TABLE_LOCAL_DATA, DB.TFonti.COLUMN_NOME + " = ? and " + DB.TFonti.COLUMN_WEB + " = ?", new String[] {fonte.nome, fonte.weblink});
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