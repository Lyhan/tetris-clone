package com.games.tetris;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tetramino {
    protected int x, y, width, height;
    protected Color color1, color2;

    protected int[][] tetramino;

    public Tetramino(int posX, int posY, int[][] tetramino) {
        this.x = posX;
        this.y = posY;
        this.tetramino = tetramino;
        this.color1 = this.generateRandomColor();
        this.color2 = this.generateRandomColor();
        while (this.color1.equals(this.color2)) {
            this.color2 = this.generateRandomColor();
        }
        this.width = this.tetramino[0].length;
        this.height = this.tetramino.length;

    }

    public void setX(int val) {
        this.x = val;
    }

    public void setY(int val) {
        this.y = val;
    }

    private Color generateRandomColor() {
        Map<String, Color> colors = new HashMap<>();
        colors.put("red", Color.RED);
        colors.put("green", Color.GREEN);
        colors.put("yellow", Color.YELLOW);
        colors.put("blue", Color.BLUE);
        String[] availableColors = new String[]{"red", "green", "yellow", "blue"};
        Random rand = new Random();
        return colors.get(availableColors[rand.nextInt(availableColors.length)]);
    }
}
