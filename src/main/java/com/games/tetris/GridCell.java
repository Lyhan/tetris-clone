package com.games.tetris;

import javafx.scene.paint.Color;

public class GridCell {
    private boolean occupied;
    private Color color1;
    private Color color2;

    // arg-less constructor
    public GridCell() {
        this(false);
    }

    // parameterized constructor
    public GridCell(boolean occupied) {
        this.occupied = occupied;
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
}
