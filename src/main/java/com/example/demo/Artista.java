package com.example.demo;

public class Artista

{
    private int id;
    private String name;

    public Artista () {}

    public Artista (int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name; // Mostrar solo el nombre en el ListView
    }
}
