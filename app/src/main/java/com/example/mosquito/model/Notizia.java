package com.example.mosquito.model;

public class Notizia {
    public String titolo, link, data;
    public Fonte f;

    public Notizia(String titolo, String link, String data, Fonte f) {
        this.titolo = titolo;
        this.link = link;
        this.data = data;
        this.f = f;
    }

    public String toString() {
        return titolo + " | " + link + " | " + data;
    }
}
