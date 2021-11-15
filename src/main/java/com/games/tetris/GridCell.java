package com.games.tetris;

import javafx.scene.paint.Color;

public class GridCell {
    private boolean occupied;
    private Color color1;
    private Color color2;
    private int posX;
    private int posY;

    // arg-less constructor
    public GridCell() {
        this(false);
    }

    // parameterized constructor
    public GridCell(boolean state) {
        this.occupied = state;
    }


    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    public Color getColor1() {
        return color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
