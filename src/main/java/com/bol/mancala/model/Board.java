package com.bol.mancala.model;

public class Board {
    private int[] side1;
    private int[] side2;

    static final int[] PITS_PLAYABLE = {0,1,2,3,4,5};
    static final int PIT_MAIN = 6;

    static final int INITIAL_STOCK = 4;

    public Board() {
        side1 = new int[6];
        side2 = new int[6];
        for (int i = 0; i < PIT_MAIN; i++) {
          side1[i] = INITIAL_STOCK;
          side2[i] = INITIAL_STOCK;
        }
    }

    public int[] getSide1() { return side1; }

    public int[] getSide2() {
        return side2;
    }

}
