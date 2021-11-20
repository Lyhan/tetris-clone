package com.games.tetris;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tetramino {
    private int x;
    private int y;
    protected int width;
    protected int height;
    protected Color color1, color2;

    protected int[][] tetramino, nextRotation;

    public Tetramino(int posX, int posY, int[][] tetramino) {
        this.x = posX;
        this.y = posY;
        this.tetramino = tetramino;
        this.color1 = generateRandomColor();
        this.color2 = generateRandomColor();
        while (this.color1.equals(this.color2)) {
            this.color2 = generateRandomColor();
        }
        width = this.tetramino[0].length;
        height = this.tetramino.length;
        nextRotation = getNextRotation();

    }


    public int[][] getNextRotation() {
        int rows = tetramino.length;
        int rowLength = tetramino[0].length;
        int[][] rotated = new int[rowLength][rows];

        for (int i = 0; i < rowLength; i++) {
            for (int j = 0; j < rows; j++) {
                rotated[i][rows - j - 1] = tetramino[j][i];
            }
        }
        return rotated;
    }

    public void rotate(){
        tetramino = nextRotation;
        width = tetramino[0].length;
        height = tetramino.length;
        nextRotation = getNextRotation();
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
