package com.games.tetris;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.IntStream;

public class TetrisGame extends Application {

    //    int[][] tetramino0 = {{1}};
    int[][] tetramino1 = {{1, 0}, {1, 0}, {1, 1}};
    int[][] tetramino2 = {{1, 1}, {1, 1}};
    int[][] tetramino3 = {{1}, {1}, {1}, {1}};
    int[][] tetramino4 = {{0, 1}, {0, 1}, {1, 1}};
    int[][] tetramino5 = {{1, 1, 0}, {0, 1, 1}};
    int[][] tetramino6 = {{0, 1, 1}, {1, 1, 0}};
    int[][] tetramino7 = {{0, 1, 0}, {1, 1, 1}};

    final private int blockSize;
    final private int maxStackBlocks;
    final private int maxInlineBlocks;
    final private int panelWidth;
    final private int panelHeight;
    final private int panelPadding = 10;

    public static void main(String[] args) {
        launch(args);
    }

    ArrayList<Tetramino> landedTetraminos = new ArrayList<>();
    public Tetramino currentTetramino;
    public Tetramino nextTetramino;

    private final Canvas canvas;
    private final GraphicsContext gc;
    public int[][] grid;
    public boolean gameEnd = false;
    AnimationTimer gameLoop;

    public TetrisGame() {
        this.blockSize = 40;
        this.maxStackBlocks = 16;
        this.maxInlineBlocks = 10;
        this.grid = new int[maxStackBlocks][maxInlineBlocks];
        this.panelWidth = maxInlineBlocks * blockSize;
        this.panelHeight = maxStackBlocks * blockSize;
        int screenHeight = panelHeight + 2 * panelPadding;
        int screenWidth = panelWidth * 2;
        this.canvas = new Canvas(screenWidth, screenHeight);
        this.gc = canvas.getGraphicsContext2D();
        this.initializeTetramino();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        Group root = new Group();

        Scene scene = new Scene(root);
        scene.setFill(Color.rgb(100, 100, 100, 0.5));

        drawGame();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (this.gameEnd) {
                if (key.getCode().toString().equals("SPACE")) {
                    resetGame();
                }
                return;
            }

            switch (key.getCode().toString()) {
                case "RIGHT":
                    if (this.currentTetramino.x < this.maxInlineBlocks - this.currentTetramino.width && !detectRightColision(currentTetramino)) {
                        this.currentTetramino.setX(this.currentTetramino.x + 1);
                    }
                    break;
                case "LEFT":
                    if (this.currentTetramino.x > 0 && !detectLeftColision(currentTetramino)) {
                        this.currentTetramino.setX(this.currentTetramino.x - 1);
                    }
                    break;
                case "UP":
                    while (this.currentTetramino.y < this.maxStackBlocks - this.currentTetramino.height && !detectVerticalCollision(currentTetramino)) {
                        this.currentTetramino.setY(this.currentTetramino.y + 1);
                    }
                    handleLandedTetramino(this.currentTetramino);
                    initializeTetramino();
                    break;
                case "DOWN":
                    if (this.currentTetramino.y < this.maxStackBlocks - this.currentTetramino.height) {
                        this.currentTetramino.setY(this.currentTetramino.y + 1);
                    }
                    break;
                case "SPACE":
                    if (!detectRotationCollision(this.currentTetramino)) {
                        this.currentTetramino.rotate();
                    }
                    break;
                case "S":
                    this.gameLoop.stop();
                    break;
                case "R":
                    this.gameLoop.start();
                    break;
                case "A":
                    initializeTetramino();
                    break;
                default:
                    System.out.println(key.getCode().toString());
            }
            drawGame();
        });

        root.getChildren().addAll(this.canvas);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.gameLoop = new AnimationTimer() {
            private double lastupdate = 0;

            @Override
            public void handle(long now) {
                // Throttle updates to 500ms
                if ((now - lastupdate >= 1000_000_000)) {
                    lastupdate = now;
                    if (currentTetramino.y < maxStackBlocks - currentTetramino.height && !detectVerticalCollision(currentTetramino)) {
                        currentTetramino.setY(currentTetramino.y + 1);
                        drawGame();
                    } else {
                        handleLandedTetramino(currentTetramino);
                        initializeTetramino();
                        drawGame();
                        if (detectVerticalCollision(currentTetramino)) {
                            this.stop();
                            gameOver();
                        }
                    }
                }
            }
        };

        gameLoop.start();
    }


    private void resetGame() {
        this.landedTetraminos.clear();
        this.grid = new int[maxStackBlocks][maxInlineBlocks];
        this.gameEnd = false;
        this.gameLoop.start();
    }

    private void handleLandedTetramino(Tetramino tetramino) {
        this.landedTetraminos.add(tetramino);
        addToGrid(currentTetramino);
        clearCompletedRows();
    }

    private int gridHasCompletedRows() {
        for (int i = 0; i < this.grid.length; i++) {
            if (IntStream.of(this.grid[i]).noneMatch(x -> x == 0)) {
                return i;
            }
        }
        return -1;
    }

    private void shiftGridRows(int row) {
        if (row >= 0) System.arraycopy(this.grid, 0, this.grid, 1, row);
    }

    private void crippleLandedTetraminos(int row){
        for (Tetramino tetramino : this.landedTetraminos) {
            // Remove empty tetraminos
            if (tetramino.tetramino.length == 0) {
                this.landedTetraminos.remove(tetramino);
                // Cripple tetraminos
            } else if ((row + 1) - tetramino.y <= tetramino.height) {
                int rowToRemove = (row + 1) - tetramino.y;
                int[][] crippledTetramino = new int[tetramino.tetramino.length - 1][tetramino.tetramino[0].length];
                int index = 0;
                for (int k = 0; k < tetramino.tetramino.length - 1; k++) {
                    if (k != rowToRemove) {
                        crippledTetramino[index] = tetramino.tetramino[k];
                        index++;
                    }
                }
                //Remove completed lines
                tetramino.tetramino = crippledTetramino;
            }
        }
    }

    private void shiftDownLandedTetraminos(int row){
        for (Tetramino tetramino : this.landedTetraminos) {
            if (row + 1 > tetramino.y) {
                tetramino.setY(tetramino.y + 1);
            }
        }
    }

    private void clearCompletedRows() {
        printGrid();
        int completedRow = this.gridHasCompletedRows();
        if (completedRow == -1) {
            return;
        }
        this.shiftGridRows(completedRow);
        this.crippleLandedTetraminos(completedRow);
        this.shiftDownLandedTetraminos(completedRow);
    }

    private void gameOver() {
        this.gameEnd = true;
        this.gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
        drawPanel(this.panelPadding, panelPadding, panelWidth, panelHeight);
        this.gc.setFont(new Font(50));
        this.gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, Color.RED),
                new Stop(1.0, Color.BLUE)));
        this.gc.fillText("Game over", (float) panelWidth / 6, (float) panelHeight / 2);
        this.gc.stroke();
    }

    private boolean detectRotationCollision(Tetramino tetramino) {
        if (tetramino.nextRotation[0].length + tetramino.x > this.grid[0].length) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.nextRotation.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.nextRotation[rowIndex].length; blockIndex++) {
                if (this.grid[tetramino.y + rowIndex][tetramino.x + blockIndex] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectLeftColision(Tetramino tetramino) {
        if (tetramino.x == 0) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && this.grid[tetramino.y + rowIndex][tetramino.x + blockIndex - 1] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectRightColision(Tetramino tetramino) {
        if (tetramino.x == this.grid[0].length) {
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

    private boolean detectVerticalCollision(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && this.grid[tetramino.y + rowIndex + 1][tetramino.x + blockIndex] == 1) {
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
                    this.grid[tetramino.y + rowIndex][tetramino.x + blockIndex] = 1;
                }
            }
        }
//        printGrid();
    }

    private void printGrid() {
        for (int[] row : this.grid) {
            for (int bit : row) {
                System.out.print(bit);
            }
            System.out.print("\n");
        }
    }

    private void drawGame() {
        this.gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
        for (Tetramino tetramino : this.landedTetraminos) {
            drawTetramino(tetramino);
        }
        drawTetramino(this.currentTetramino);
        drawPanel(this.panelPadding, this.panelPadding, this.panelWidth, this.panelHeight);
        drawNextTetramino();
    }

    private void drawNextTetramino() {
//        this.gc.strokeRect(((this.maxInlineBlocks + 1) * this.blockSize) + this.panelPadding, (this.blockSize * 1) + this.panelPadding,  this.blockSize * 5, this.blockSize * 4);
        this.nextTetramino.setX(this.maxInlineBlocks + 2);
        this.nextTetramino.setY(2);
        drawTetramino(this.nextTetramino);

    }

    private void drawPanel(int posX, int posY, int panelWidth, int panelHeight) {
        this.gc.beginPath();
        this.gc.moveTo(posX, posY);
        this.gc.lineTo(this.panelPadding + panelWidth, this.panelPadding);
        this.gc.lineTo(this.panelPadding + panelWidth, this.panelPadding + panelHeight);
        this.gc.lineTo(this.panelPadding, this.panelPadding + panelHeight);
        this.gc.lineTo(this.panelPadding, this.panelPadding);
        this.gc.stroke();
    }

    private void drawBlock(int posX, int posY, Color color1, Color color2) {
        int screenXPosition = (posX * this.blockSize) + this.panelPadding;
        int screenYPosition = (posY * this.blockSize) + this.panelPadding;

        this.gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, color2),
                new Stop(1.0, color1)));
        this.gc.setLineWidth(2);

        this.gc.fillRoundRect(screenXPosition, screenYPosition, this.blockSize, this.blockSize, 8, 8);

        this.gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, color1),
                new Stop(1.0, color2)));
        this.gc.fillRoundRect(screenXPosition + 5, screenYPosition + 5, blockSize - 10, blockSize - 10, 5, 5);
    }

    private void initializeTetramino() {
        Random rand = new Random();
        ArrayList<int[][]> tetraminos = new ArrayList<>();
        tetraminos.add(tetramino1);
        tetraminos.add(tetramino2);
        tetraminos.add(tetramino3);
        tetraminos.add(tetramino4);
        tetraminos.add(tetramino5);
        tetraminos.add(tetramino6);
        tetraminos.add(tetramino7);
        if (this.nextTetramino == null) {
            this.nextTetramino = new Tetramino(0, 0, tetraminos.get(rand.nextInt(tetraminos.size())));
        }
        this.currentTetramino = this.nextTetramino;
        this.currentTetramino.setX((this.maxInlineBlocks / 2) - 1);
        this.currentTetramino.setY(0);
        this.nextTetramino = new Tetramino(0, 0, tetraminos.get(rand.nextInt(tetraminos.size())));
    }

    private void drawTetramino(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    drawBlock((tetramino.x + blockIndex), (tetramino.y + rowIndex), tetramino.color1, tetramino.color2);
                }
            }
        }

    }
}