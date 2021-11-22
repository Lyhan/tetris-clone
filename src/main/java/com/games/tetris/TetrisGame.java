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

public class TetrisGame extends Application {

    int[][] tetramino0 = {{1}};
    int[][] tetramino1 = {{1, 0}, {1, 0}, {1, 1}};
    int[][] tetramino2 = {{1, 1}, {1, 1}};
    int[][] tetramino3 = {{1}, {1}, {1}, {1}};
    int[][] tetramino4 = {{0, 1}, {0, 1}, {1, 1}};
    int[][] tetramino5 = {{1, 1, 0}, {0, 1, 1}};
    int[][] tetramino6 = {{0, 1, 1}, {1, 1, 0}};
    int[][] tetramino7 = {{0, 1, 0}, {1, 1, 1}};

    final private int blockSize;
    final private int blocksHeight;
    final private int blocksWidth;
    final private int panelWidth;
    final private int panelHeight;
    final private int panelPadding = 10;
    final private int nextPanelWidth = 150;
    final private int nextPanelHeight = 150;

    public static void main(String[] args) {
        launch(args);
    }

    public Tetramino currentTetramino;
    public Tetramino nextTetramino;

    private final Canvas canvas;
    private final GraphicsContext gc;
    public Grid grid;
    public boolean gameEnd = false;
    AnimationTimer gameLoop;

    public TetrisGame() {
        blockSize = 40;
        blocksHeight = 16;
        blocksWidth = 10;
        grid = new Grid(blocksHeight, blocksWidth);
        panelWidth = blocksWidth * blockSize;
        panelHeight = blocksHeight * blockSize;
        int screenHeight = panelHeight + 2 * panelPadding;
        int screenWidth = panelWidth + (4 * panelPadding) + nextPanelWidth;
        canvas = new Canvas(screenWidth, screenHeight);
        gc = canvas.getGraphicsContext2D();
        grid.initializeGrid();
        initializeTetramino();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        Group root = new Group();

        Scene scene = new Scene(root);
        scene.setFill(Color.rgb(100, 100, 100, 0.5));

        drawGame();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (gameEnd) {
                if (key.getCode().toString().equals("SPACE")) {
                    resetGame();
                }
                return;
            }

            switch (key.getCode().toString()) {
                case "RIGHT":
                    if (currentTetramino.getX() < blocksWidth - currentTetramino.getBlocksWidth() && !detectRightColision(currentTetramino)) {
                        currentTetramino.setX(currentTetramino.getX() + 1);
                    }
                    break;
                case "LEFT":
                    if (currentTetramino.getX() > 0 && !detectLeftColision(currentTetramino)) {
                        currentTetramino.setX(currentTetramino.getX() - 1);
                    }
                    break;
                case "UP":
                    while (currentTetramino.getY() < blocksHeight - currentTetramino.getBlocksHeight() && !detectBottomCollision(currentTetramino)) {
                        currentTetramino.setY(currentTetramino.getY() + 1);
                    }
                    grid.addTetramino(currentTetramino);
                    initializeTetramino();
                    break;
                case "DOWN":
                    if (currentTetramino.getY() < blocksHeight - currentTetramino.getBlocksHeight()) {
                        currentTetramino.setY(currentTetramino.getY() + 1);
                    }
                    break;
                case "SPACE":
                    if (!detectRotationCollision(currentTetramino)) {
                        currentTetramino.rotate();
                    }
                    break;
                case "S":
                    gameLoop.stop();
                    break;
                case "R":
                    gameLoop.start();
                    break;
                case "A":
                    initializeTetramino();
                    drawGame();
                    break;
                default:
                    System.out.println(key.getCode().toString());
            }
            drawGame();
        });

        root.getChildren().addAll(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();

        gameLoop = new AnimationTimer() {
            private double lastupdate = 0;

            @Override
            public void handle(long now) {
                // Throttle updates to 500ms
                if ((now - lastupdate >= 1000_000_000)) {
                    lastupdate = now;
                    if (currentTetramino.getY() < blocksHeight - currentTetramino.getBlocksHeight() && !detectBottomCollision(currentTetramino)) {
                        currentTetramino.setY(currentTetramino.getY() + 1);
                        drawGame();
                    } else {
                        grid.addTetramino(currentTetramino);
                        initializeTetramino();
                        drawGame();
                        if (detectBottomCollision(currentTetramino)) {
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
        grid.initializeGrid();
        gameEnd = false;
        gameLoop.start();
    }

    private void gameOver() {
        gameEnd = true;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPanel(panelPadding, panelPadding, panelWidth, panelHeight, Color.RED);
        gc.setFont(new Font(50));
        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, true, CycleMethod.REPEAT,
                new Stop(0.0, Color.RED),
                new Stop(1.0, Color.BLUE)));
        gc.fillText("Game over", (float) panelWidth / 6, (float) panelHeight / 2);
        gc.stroke();
    }

    private boolean detectRotationCollision(Tetramino tetramino) {
        if (tetramino.nextRotation[0].length + tetramino.getX() > grid.getWidth()) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.nextRotation.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.nextRotation[rowIndex].length; blockIndex++) {
                GridCell cell = grid.getGridCell(tetramino.getY() + rowIndex, tetramino.getX() + blockIndex);
                if (cell.isOccupied()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectLeftColision(Tetramino tetramino) {
        if (tetramino.getX() == 0) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                GridCell cell = grid.getGridCell(tetramino.getY() + rowIndex, tetramino.getX() + blockIndex - 1);
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && cell.isOccupied()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectRightColision(Tetramino tetramino) {
        if (tetramino.getX() == grid.getWidth()) {
            return true;
        }
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                GridCell cell = grid.getGridCell(tetramino.getY() + rowIndex, tetramino.getX() + blockIndex + 1);
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && cell.isOccupied()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectBottomCollision(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                GridCell cell = grid.getGridCell(tetramino.getY() + rowIndex + 1, tetramino.getX() + blockIndex);
                if (tetramino.tetramino[rowIndex][blockIndex] > 0 && cell.isOccupied()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int convertToPixels(int position) {
        return (position * blockSize) + panelPadding;
    }

    private void drawGame() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawTetramino(currentTetramino, blockSize, convertToPixels(currentTetramino.getX()), convertToPixels(currentTetramino.getY()));
        drawPanel(panelPadding, panelPadding, panelWidth, panelHeight, Color.BLUE);
        drawNextTetramino();
        drawLandedTetraminos();
    }

    private void drawLandedTetraminos() {
        for (int row = 0; row < blocksHeight; row++) {
            ArrayList<GridCell> currentRow = grid.getRow(row);
            for (int index = 0; index < currentRow.size(); index++) {
                GridCell gridCell = currentRow.get(index);
                if (gridCell.isOccupied()) {
                    drawBlock((index * blockSize) + panelPadding, (row * blockSize) + panelPadding, blockSize, gridCell.getColor1(), gridCell.getColor2());
                }
            }
        }
    }

    private int centerInContainer(int offset, int containerInnerSize, int itemOuterSize) {
        return offset + (containerInnerSize / 2) - (itemOuterSize / 2);
    }

    private void drawNextTetramino() {
        int nextTetraminoPanelX = (3 * panelPadding) + panelWidth;
        drawPanel(nextTetraminoPanelX, panelPadding, nextPanelWidth, nextPanelHeight, Color.GREEN);
        int size = blockSize - 10;
        int posX = centerInContainer(nextTetraminoPanelX, nextPanelWidth, nextTetramino.getBlocksWidth() * size);
        int posY = centerInContainer(panelPadding, nextPanelHeight, nextTetramino.getBlocksHeight() * size);
        drawTetramino(nextTetramino, size, posX, posY);
    }

    private void drawPanel(double posX, double posY, double panelWidth, double panelHeight, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(4.0);
        gc.strokeRoundRect(posX - 2, posY - 2, panelWidth + 4, panelHeight + 4, 5, 5);
        gc.stroke();
    }

    private void drawBlock(int posX, int posY, int blockSize, Color color1, Color color2) {
        gc.setFill(new LinearGradient(0, 0, 0.6, 0.6, true, CycleMethod.REPEAT,
                new Stop(0.0, color2),
                new Stop(1.0, color1)));
        gc.setLineWidth(2);

        gc.fillRoundRect(posX, posY, blockSize, blockSize, 8, 8);

        gc.setFill(new LinearGradient(0, 0, 0.5, 0.5, false, CycleMethod.REPEAT,
                new Stop(0.0, color1),
                new Stop(1.0, color2)));
        gc.fillRoundRect(posX + (blockSize * 0.25 / 2), posY + (blockSize * 0.25 / 2), blockSize - (blockSize * 0.25), blockSize - (blockSize * 0.25), 5, 5);
    }

    private void initializeTetramino() {
        Random rand = new Random();
        ArrayList<int[][]> tetraminos = new ArrayList<>();
//        tetraminos.add(tetramino0);
        tetraminos.add(tetramino1);
        tetraminos.add(tetramino2);
        tetraminos.add(tetramino3);
        tetraminos.add(tetramino4);
        tetraminos.add(tetramino5);
        tetraminos.add(tetramino6);
        tetraminos.add(tetramino7);
        if (nextTetramino == null) {
            nextTetramino = new Tetramino(0, 0, tetraminos.get(rand.nextInt(tetraminos.size())));
        }
        currentTetramino = nextTetramino;
        currentTetramino.setX((blocksWidth / 2) - 1);
        currentTetramino.setY(0);
        nextTetramino = new Tetramino(0, 0, tetraminos.get(rand.nextInt(tetraminos.size())));
    }

    private void drawTetramino(Tetramino tetramino, int blockSize, int screenPositionX, int screenPositionY) {
        int screenY = screenPositionY;
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            int screenX = screenPositionX;
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    drawBlock(screenX, screenY, blockSize, tetramino.color1, tetramino.color2);
                }
                screenX += blockSize;
            }
            screenY += blockSize;
        }
    }
}