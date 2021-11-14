package com.games.tetris;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class TetrisGame extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //    int[][] tetramino0 = {{1}};
    int[][] tetramino1 = {{1, 0}, {1, 0}, {1, 1}};
    int[][] tetramino2 = {{1, 1}, {1, 1}};
    int[][] tetramino3 = {{1}, {1}, {1}, {1}};
    int[][] tetramino4 = {{0, 1}, {0, 1}, {1, 1}};
    int[][] tetramino5 = {{1, 1, 0}, {0, 1, 1}};
    int[][] tetramino6 = {{0, 1, 1}, {1, 1, 0}};
    int[][] tetramino7 = {{0, 1, 0}, {1, 1, 1}};

    static int blockSize = 40;
    static int maxStackBlocks = 20;
    static int maxInlineBlocks = 10;
    static int panelPadding = 10;

    int panelWidth = maxInlineBlocks * blockSize;
    int panelHeight = maxStackBlocks * blockSize;
    int screenHeight = panelHeight + 2 * panelPadding;
    int screenWidth = panelWidth * 2;
    ArrayList<Tetramino> landedTetraminos = new ArrayList<>();

    public static Tetramino currentTetramino;
    public static Canvas canvas;
    public static GraphicsContext gc;
    public static int[][] grid = new int[maxStackBlocks][maxInlineBlocks];

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        Group root = new Group();

        canvas = new Canvas(screenWidth, screenHeight);
        gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root);
        scene.setFill(Color.rgb(100, 100, 100, 0.5));

        currentTetramino = getRandomTetramino();

        drawGame(gc, canvas, currentTetramino, landedTetraminos);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            String keypress = "";
            if (key.getCode() == KeyCode.RIGHT
                    | key.getCode() == KeyCode.LEFT
                    | key.getCode() == KeyCode.UP
                    | key.getCode() == KeyCode.DOWN
                    | key.getCode() == KeyCode.SPACE
            ) {
                keypress = key.getCode().toString();
            }

            switch (keypress) {
                case "RIGHT":
                    if (currentTetramino.x < maxInlineBlocks - currentTetramino.width && !detectRightColision(currentTetramino, grid)) {
                        currentTetramino.setX(currentTetramino.x + 1);
                    }
                    break;
                case "LEFT":
                    if (currentTetramino.x > 0 && !detectLeftColision(currentTetramino, grid)) {
                        currentTetramino.setX(currentTetramino.x - 1);
                    }
                    break;
                case "UP":
                    while (currentTetramino.y < maxStackBlocks - currentTetramino.height && !detectVerticalCollision(currentTetramino, grid)) {
                        currentTetramino.setY(currentTetramino.y + 1);
                    }
                    handleLandedTetramino(currentTetramino);
                    currentTetramino = getRandomTetramino();
                    break;
                case "DOWN":
                    if (currentTetramino.y < maxStackBlocks - currentTetramino.height) {
                        currentTetramino.setY(currentTetramino.y + 1);
                    }
                    break;
                case "SPACE":
                    if (!detectRotationCollision(currentTetramino)) {
                        currentTetramino.rotate();
                    }
                    break;
            }
            drawGame(gc, canvas, currentTetramino, landedTetraminos);
        });

        root.getChildren().addAll(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();

        new AnimationTimer() {
            private double lastupdate = 0;

            @Override
            public void handle(long now) {
                // Throttle updates to 500ms
                if ((now - lastupdate >= 500_000_000)) {
                    lastupdate = now;
                    if (currentTetramino.y < maxStackBlocks - currentTetramino.height && !detectVerticalCollision(currentTetramino, grid)) {
                        currentTetramino.setY(currentTetramino.y + 1);
                        drawGame(gc, canvas, currentTetramino, landedTetraminos);
                    } else {
                        handleLandedTetramino(currentTetramino);
                        currentTetramino = getRandomTetramino();
                        if (detectVerticalCollision(currentTetramino, grid)) {
                            this.stop();
                            gameOver(gc, canvas);
                        }
                    }
                }
            }
        }.start();
    }

    private void handleLandedTetramino(Tetramino tetramino) {
        landedTetraminos.add(tetramino);
        addToGrid(currentTetramino);
    }

    private void gameOver(GraphicsContext gc, Canvas canvas) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPanel(canvas, panelPadding, panelPadding, panelWidth, panelHeight);
        gc.setFont(new Font(50));
        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, Color.RED),
                new Stop(1.0, Color.BLUE)));
        gc.fillText("Game over", (float) panelWidth / 6, (float) panelHeight / 2);
        gc.stroke();
    }

    private boolean detectRotationCollision(Tetramino tetramino) {
        if (tetramino.nextRotation[0].length + tetramino.x > grid[0].length) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.nextRotation.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.nextRotation[rowIndex].length; blockIndex++) {
                if (grid[tetramino.y + rowIndex][tetramino.x + blockIndex] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectLeftColision(Tetramino tetramino, int[][] grid) {
        if (tetramino.x == 0) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && grid[tetramino.y + rowIndex][tetramino.x + blockIndex - 1] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectRightColision(Tetramino tetramino, int[][] grid) {
        if (tetramino.x == grid[0].length) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && grid[tetramino.y + rowIndex][tetramino.x + blockIndex + 1] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectVerticalCollision(Tetramino tetramino, int[][] grid) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && grid[tetramino.y + rowIndex + 1][tetramino.x + blockIndex] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addToGrid(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    grid[tetramino.y + rowIndex][tetramino.x + blockIndex] = 1;
                }
            }
        }
//        printGrid(grid);
    }

    private void printGrid(int[][] grid) {
        for (int[] row : grid) {
            for (int bit : row) {
                System.out.print(bit);
            }
            System.out.print("\n");
        }
    }

    private void drawGame(GraphicsContext gc, Canvas canvas, Tetramino currentShape, ArrayList<Tetramino> tetraminoArrayList) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Tetramino tetra : tetraminoArrayList) {
            drawTetramino(canvas, tetra);
        }
        drawTetramino(canvas, currentShape);
        drawPanel(canvas, panelPadding, panelPadding, panelWidth, panelHeight);
    }

    private void drawPanel(Canvas canvas, int posX, int posY, int panelWidth, int panelHeight) {
        gc = canvas.getGraphicsContext2D();
        gc.beginPath();
        gc.moveTo(posX, posY);
        gc.lineTo(panelPadding + panelWidth, panelPadding);
        gc.lineTo(panelPadding + panelWidth, panelPadding + panelHeight);
        gc.lineTo(panelPadding, panelPadding + panelHeight);
        gc.lineTo(panelPadding, panelPadding);
        gc.stroke();
    }

    private void drawBlock(Canvas canvas, int blockSize, int posX, int posY, Color color1, Color color2) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int screenXPosition = (posX * blockSize) + panelPadding;
        int screenYPosition = (posY * blockSize) + panelPadding;

        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, color2),
                new Stop(1.0, color1)));
        gc.setLineWidth(2);

        gc.fillRoundRect(screenXPosition, screenYPosition, blockSize, blockSize, 8, 8);

        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, color1),
                new Stop(1.0, color2)));
        gc.fillRoundRect(screenXPosition + 5, screenYPosition + 5, blockSize - 10, blockSize - 10, 5, 5);
    }

    private Tetramino getRandomTetramino() {
        Random rand = new Random();
        ArrayList<int[][]> tetraminos = new ArrayList<>();
        tetraminos.add(tetramino1);
        tetraminos.add(tetramino2);
        tetraminos.add(tetramino3);
        tetraminos.add(tetramino4);
        tetraminos.add(tetramino5);
        tetraminos.add(tetramino6);
        tetraminos.add(tetramino7);

        return new Tetramino(0, 0, tetraminos.get(rand.nextInt(tetraminos.size())));
    }

    private void drawTetramino(Canvas canvas, Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    drawBlock(canvas, blockSize, (tetramino.x + blockIndex), (tetramino.y + rowIndex), tetramino.color1, tetramino.color2);
                }
            }
        }

    }
}