package com.example.learn2;

import android.graphics.Color;
import android.graphics.Paint;

public class Cell {

    private final int row;
    private final int col;

    private int cellSize;
    private boolean occupied;
    private int left;
    private int top;


    public Cell(int row, int col) {
        this.row = row;
        this.col = col;

        this.occupied = false;

    }

    public void updateCellSize(int cellSize) {
        this.left = col * cellSize;
        this.top = row * cellSize;
        this.cellSize = cellSize;
    }


    public Paint getCirclePaint() {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public int getCenter(){
        return cellSize / 2;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return left + cellSize;
    }

    public int getBottom() {
        return top + cellSize;
    }

}
