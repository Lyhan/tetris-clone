package com.example.demo;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.stage.Stage;

import java.util.ArrayList;


public class TetrisGame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    int[][] shape1 = {{1, 0, 0}, {1, 0, 0}, {1, 1, 0}};
    int[][] shape2 = {{1, 1}, {1, 1}};
    int[][] shape3 = {{1}, {1}, {1}, {1}};
    int[][] shape4 = {{0, 0, 1}, {0, 0, 1}, {0, 1, 1}};
    int[][] shape5 = {{1, 1, 0}, {0, 1, 1}};
    int[][] shape6 = {{0, 1, 1}, {1, 1, 0}};
    int[][] shape7 = {{0, 1, 0}, {1, 1, 1}};

    int blockSize = 40;
    int ScreenHeight = 900;
    int ScreenWidth = 600;
    int maxStackBlocks = 20;
    int maxInlineBlocks = 10;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        primaryStage.setAlwaysOnTop(true);
        Group root = new Group();

        int width = maxInlineBlocks * blockSize * 2;
        int height = maxStackBlocks * blockSize * 2;
        Canvas canvas = new Canvas(width, height);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));


        ArrayList<int[][]> shapes = new ArrayList<int[][]>();

        shapes.add(shape1);
        shapes.add(shape2);
        shapes.add(shape3);
        shapes.add(shape4);
        shapes.add(shape5);
        shapes.add(shape6);
        shapes.add(shape7);

        drawFigure(canvas, shapes.get(0), 0, 0, Color.BLUE, Color.GREEN);
        drawFigure(canvas, shapes.get(1), blockSize * 2, 0, Color.BLUE, Color.GREEN);
        drawFigure(canvas, shapes.get(2), blockSize * 5, 0, Color.RED, Color.YELLOW);
        drawFigure(canvas, shapes.get(3), blockSize * 6, 0, Color.BLUE, Color.GREEN);
        drawFigure(canvas, shapes.get(4), blockSize * 10, 0, Color.BLUE, Color.GREEN);
        drawFigure(canvas, shapes.get(5), blockSize * 14, 0, Color.BLUE, Color.GREEN);
        drawFigure(canvas, shapes.get(6), blockSize * 17, 0, Color.BLUE, Color.GREEN);

        primaryStage.show();
    }

    private void drawBlock(Canvas canvas, int blockSize, int posX, int posY, Color firstColor, Color lastColor, CycleMethod cycleMethod) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, cycleMethod,
                new Stop(0.0, lastColor),
                new Stop(1.0, firstColor)));
        gc.setLineWidth(2);

        gc.fillRoundRect(posX, posY, blockSize, blockSize, 8, 8);

        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, cycleMethod,
                new Stop(0.0, firstColor),
                new Stop(1.0, lastColor)));
        gc.fillRoundRect(posX + 5, posY + 5, blockSize - 10, blockSize - 10, 5, 5);
    }


    private void drawFigure(Canvas canvas, int[][] shape, int posX, int posY, Color firstColor, Color lastColor) {
        int currentX;
        int currentY = posY;
        for (int[] row : shape) {
            currentX = posX;
            for (int bit : row) {
                if (bit > 0) {
                    drawBlock(canvas, blockSize, currentX, currentY, firstColor, lastColor, CycleMethod.REPEAT);
                }
                currentX += blockSize;
            }
            currentY += blockSize;
        }
    }
}