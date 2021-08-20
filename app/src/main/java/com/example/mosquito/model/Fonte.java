package com.example.mosquito.model;

import java.io.Serializable;

public class Fonte implements Serializable {
    public String weblink, nome;
    public boolean notifiche;

    public Fonte(String weblink, String nome, boolean notifiche) {
        this.weblink = weblink;
        this.nome = nome;
        this.notifiche = notifiche;
    }

    public String toString() {return nome.substring(0, Math.min(18, nome.length()));}
}