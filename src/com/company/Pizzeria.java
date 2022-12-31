package com.company;

public class Pizzeria {
    public final int x;
    public final int y;
    public final int ind;
    private int c;

    public Pizzeria(int x, int y, int ind, int c) {
        this.x = x;
        this.y = y;
        this.ind = ind;
        this.c = c;
    }

    public int getC() {
        return c;
    }

    public void downC(int p) {
        this.c = c - p;
    }

}
