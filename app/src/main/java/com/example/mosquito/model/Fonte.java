package com.example.mosquito.model;

import java.io.Serializable;

public class Fonte implements Serializable {
    public String weblink, nome;

    public Fonte(String weblink, String nome) {
        this.weblink = weblink;
        this.nome = nome;
    }

    public String toString() {return nome;}
}