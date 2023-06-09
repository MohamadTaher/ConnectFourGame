package com.example.learn2;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.learn2.gameLogic.Player;

public class Cell {
	
	public static boolean lastMovePlayed = false;
	static int lastColumn;
	static Paint paint;
	private static int lastRow;
	private final int row, col;
	private boolean occupied;
	private int cellSize, left, top;
	private Player playerType;
	
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
		playerType = null;
		lastColumn = 0;
		
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		
		this.occupied = false;
	}
	
	public static void setHuman1LastPaint() {
		paint.setColor(Color.RED);
	}
	
	public static void setHuman2LastPaint() {
		paint.setColor(Color.GREEN);
	}
	
	public static void setAILastPaint() {
		paint.setColor(Color.BLUE);
	}
	
	public static Paint getLastPaint() {
		return paint;
	}
	
	public static void setLastMove(int row, int col) {
		lastRow = row;
		lastColumn = col;
	}
	
	public static int getLastColumn() {
		return lastColumn;
	}
	
	public static int getLastRow() {
		return lastRow;
	}
	
	public Paint getHumanPaint1() {
		Paint humanPaint = new Paint();
		humanPaint.setColor(Color.RED);
		return humanPaint;
	}
	
	public Paint getHumanPaint2() {
		Paint humanPaint = new Paint();
		humanPaint.setColor(Color.GREEN);
		return humanPaint;
	}
	
	public Paint getAIPaint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.BLUE);
		return paint1;
	}
	
	public Paint getDrawingPaint() {
		Paint tempPaint = new Paint();
		tempPaint.setColor(Color.YELLOW);
		return tempPaint;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public Player getPlayerType() {
		return playerType;
	}
	
	public void setPlayerType(Player playerType) {
		this.playerType = playerType;
	}
	
	public void updateCellSize(int cellSize) {
		this.left = col * cellSize;
		this.top = row * cellSize;
		this.cellSize = cellSize;
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
	
	public int getCellSize() {
		return cellSize;
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
