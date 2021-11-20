package com.games.tetris;

import java.util.ArrayList;

public class Grid {

    private int height;
    private int width;
    private ArrayList<ArrayList<GridCell>> gridArray = new ArrayList<>();

    // parameterized constructur
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
    }

    // For debugging
    public void printGrid() {
        System.out.println(new String(new char[width + 2]).replace('\0', '-'));
        for (int i = 0; i < gridArray.size(); i++) {
            ArrayList<GridCell> row = gridArray.get(i);
            System.out.print(String.valueOf(i) + '\t' + '|');
            for (GridCell cell : row) {
                System.out.print(cell.isOccupied() ? 1 : 0);
            }
            System.out.print('|');
            System.out.print("\n");
        }
        System.out.println(new String(new char[width + 2]).replace('\0', '-'));
        System.out.print("\n");
    }

    public ArrayList<GridCell> getRow(int index) {
        return gridArray.get(index);
    }

    public GridCell getGridCell(int row, int column) {
        ArrayList<GridCell> selectedRow = gridArray.get(row);
        return selectedRow.get(column);
    }

    public void addTetramino(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    int posX = tetramino.getX() + blockIndex;
                    int posY = tetramino.getY() + rowIndex;
                    GridCell cell = getGridCell(posY, posX);
                    cell.setOccupied(true);
                    cell.setColor1(tetramino.color1);
                    cell.setColor2(tetramino.color2);
                }
            }
        }
        clearCompletedRows();
    }

    protected boolean rowCompleted(ArrayList<GridCell> row) {
        for (GridCell i : row) {
            if (!i.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    private void clearCompletedRows() {
        int counter = 0;
        int[] completedRows = new int[4]; // Maximum possible rows completed in 1 shot
        for (int i = 0; i < gridArray.size(); i++) {
            if (rowCompleted(gridArray.get(i))) {
                completedRows[counter] = i;
                counter++;
            }
        }

        if (counter > 0) {
            shiftGridRows(completedRows);
        }
    }

    private void shiftGridRows(int[] rows) {
        for (int rowIndex : rows) {
            if (rowIndex > 0) {
                // Shift tetramino 'y' position down
                for (int i = rowIndex; i > 0; i--) {
                    ArrayList<GridCell> row = gridArray.get(i);
                    ArrayList<GridCell> replacement = new ArrayList<>();
                    for (int j = 0; j < row.size(); j++) {
                        GridCell cell = row.get(j);
                        replacement.add(j, cell);
                    }
                    gridArray.set(i, replacement);
                }
            }
        }
        // Remove completed rows
        for (int rowIndex : rows) {
            if (rowIndex > 0) {
                gridArray.remove(rowIndex);
                gridArray.add(0, createEmptyRow());
            }
        }
    }

    public ArrayList<GridCell> createEmptyRow() {
        ArrayList<GridCell> row = new ArrayList<>();
        for (int j = 0; j < width; j++) {
            row.add(new GridCell());
        }
        return row;
    }

    public void initializeGrid() {
        for (int i = 0; i < height; i++) {
            gridArray.add(i, createEmptyRow());
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
