package com.games.tetris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Grid {

    private int height;
    private int width;
    public HashMap<Integer, ArrayList<GridCell>> gridArray = new HashMap<>();

    // arg-less constructor
    public Grid() {

    }

    // parameterized constructur
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
    }

    private void printGrid() {
        System.out.println(new String(new char[width + 2]).replace('\0', '-'));
        for (int i = 0; i < gridArray.size(); i++) {
            ArrayList<GridCell> row = gridArray.get(i);
            System.out.print('|');
            for (GridCell cell : row) {
                System.out.print(cell.isOccupied() ? 1 : 0);
            }
            System.out.print('|');
            System.out.print("\n");
        }
        System.out.println(new String(new char[width + 2]).replace('\0', '-'));
        System.out.print("\n");
    }

    public GridCell getGridCell(int row, int column) {
        ArrayList<GridCell> selectedRow = gridArray.get(row);
        return selectedRow.get(column);
    }

    public void addTetramino(Tetramino tetramino) {
        for (int rowIndex = 0; rowIndex < tetramino.tetramino.length; rowIndex++) {
            for (int blockIndex = 0; blockIndex < tetramino.tetramino[rowIndex].length; blockIndex++) {
                if (tetramino.tetramino[rowIndex][blockIndex] > 0) {
                    int posX = tetramino.x + blockIndex;
                    int posY = tetramino.y + rowIndex;
                    GridCell cell = getGridCell(posY, posX);
                    cell.setOccupied(true);
                    cell.setColor1(tetramino.color1);
                    cell.setColor2(tetramino.color2);
                    cell.setPosX(posX);
                    cell.setPosY(posY);
//                    gridArray[tetramino.y + rowIndex][tetramino.x + blockIndex] = 1;
                }
            }
        }
        printGrid();
        clearCompletedRows();
    }

    private void clearCompletedRows() {
        // CONTINUE HERE
        for (int i = 0; i < gridArray.size(); i++) {
            ArrayList<GridCell> currentRow = gridArray.get(i);
            Predicate<GridCell> CellIsOccupied = GridCell::isOccupied;
            if (currentRow.stream().allMatch(CellIsOccupied)) {
                shiftGridRows(i);
                break;
            }
        }
    }

    private void shiftGridRows(int rowIndex) {
        if (rowIndex == 0) {
            return;
        }
        for (int i = rowIndex; i > -1; i--) {
            gridArray.put(i, gridArray.get(i - 1));
        }
        clearCompletedRows();
    }

    public void initializeGrid() {
        for (int i = 0; i < height; i++) {
            ArrayList<GridCell> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(new GridCell());
            }
            gridArray.put(i, row);
        }
        printGrid();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
