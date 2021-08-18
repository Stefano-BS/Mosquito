package com.example.mosquito.model;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Notizia implements Comparable<Notizia> , Serializable {
    public String titolo, link, data, desc, imgSrc;
    public Date d;
    public Fonte f;
    public boolean letta = false;

    private boolean dataFail = false;
    private static boolean ascdesc = true;

    public Notizia(String titolo, String link, String data, Fonte f) {
        this.titolo = titolo;
        this.link = link;
        this.data = data;
        this.f = f;
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(data));
            d = c.getTime();
        }
        catch (Exception e) {
            data = data.replace("Z", "");
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date(data));
                d = c.getTime();
            }
            catch (Exception e2) {
                d = new Date();
                dataFail = true;
            }
        }
    }

    public Notizia(String titolo, String link, String data, Fonte f, String desc, String imgSrc) {
        this(titolo, link, data, f);
        this.desc = desc;
        this.imgSrc = imgSrc;
    }

    public String toString() {
        return titolo + " | " + link + " | " + data;
    }

    public String dataString() {
        if (dataFail) return data;
        else {
            DateFormat df = new SimpleDateFormat();
            return df.format(d);
        }
    }

    @Override
    public int compareTo(Notizia n2) {
        return ascdesc? this.d.compareTo(n2.d) : n2.d.compareTo(this.d);
    }

    public static void aggiornaAscDesc(){
        ascdesc = DB.getInstance().ottieniImpostazione(0).equals("true");
    }
}
