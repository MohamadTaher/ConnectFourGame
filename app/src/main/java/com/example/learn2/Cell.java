package com.example.learn2;

import android.graphics.Color;
import android.graphics.Paint;

public class Cell {


	private static Paint paint;
	private final int row, col;
	private boolean occupied;
	private int cellSize, left, top;

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;

		paint = new Paint();

		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		
		this.occupied = false;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}


	public static void setHuman1LastPaint() {
		paint.setColor(Color.rgb(255,0,0));
	}
	public static void setHuman2LastPaint() {
		paint.setColor(Color.rgb(0,0,255));
	}
	public static void setHuman3LastPaint() {
		paint.setColor(Color.rgb(0,255,0));
	}
	public static void setAI1LastPaint() {
		paint.setColor(Color.rgb(255,255,0));
	}
	public static void setAI2LastPaint() {
		paint.setColor(Color.rgb(0,255,255));
	}

	public static Paint getLastPaint() {
		return paint;
	}
	public Paint getHumanPaint1() {
		Paint humanPaint = new Paint();
		humanPaint.setColor(Color.rgb(255,0,0));
		return humanPaint;
	}
	
	public Paint getHumanPaint2() {
		Paint humanPaint = new Paint();
		humanPaint.setColor(Color.rgb(0,0,255));
		return humanPaint;
	}

	public Paint getHumanPaint3() {
		Paint humanPaint = new Paint();
		humanPaint.setColor(Color.rgb(0,255,0));
		return humanPaint;
	}

	public Paint getAI1Paint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.rgb(255,255,0));
		return paint1;
	}

	public Paint getAI2Paint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.rgb(0,255,255));
		return paint1;
	}
	public Paint getDrawingPaint() {
		Paint tempPaint = new Paint();
		tempPaint.setColor(Color.GRAY);
		return tempPaint;
	}
	public void updateCellSize(int cellSize) {
		this.left = col * cellSize;
		this.top = row * cellSize;
		this.cellSize = cellSize;
	}
	public int getCellSize() {
		return cellSize;
	}
	public int getCenterX() {
		return this.getLeft() + this.getCenter();
	}
	public int getCenterY() {
		return this.getTop() + this.getCenter();
	}
	public int getRadius() {
		return ((int) ((this.getCellSize() / 2f) - (this.getCellSize() * 0.06f)));
	}
	public int getCenter() {
		return cellSize / 2;
	}
	public int getY() {
		if (row == 0) {
			return getCenter();
		} else {
			return getCenter() * (row + 1);
		}
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
