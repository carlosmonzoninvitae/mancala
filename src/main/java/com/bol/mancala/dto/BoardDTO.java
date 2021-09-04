package com.bol.mancala.dto;

public class BoardDTO {
    private int[] side1;
    private int[] side2;

    public BoardDTO() {}

    public int[] getSide1() {
        return side1;
    }

    public void setSide1(int[] side1) {
        this.side1 = side1;
    }

    public int[] getSide2() {
        return side2;
    }

    public void setSide2(int[] side2) {
        this.side2 = side2;
    }
}
