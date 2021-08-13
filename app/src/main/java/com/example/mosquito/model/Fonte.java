package com.example.mosquito.model;

public class Fonte {
    public String weblink, nome;

    public Fonte(String weblink, String nome) {
        this.weblink = weblink;
        this.nome = nome;
    }

    public String toString() {return nome;}
}