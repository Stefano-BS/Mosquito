package com.example.mosquito.model;

import java.io.Serializable;

public class Fonte implements Serializable {
    public String weblink, nome;
    private String nomeBreve;

    public Fonte(String weblink, String nome) {
        this.weblink = weblink;
        this.nome = nome;
        nomeBreve = nome.substring(0, Math.min(18, nome.length()));
    }

    public String toString() {return nomeBreve;}
}