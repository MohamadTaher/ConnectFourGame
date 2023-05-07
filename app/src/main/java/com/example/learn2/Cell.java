package com.example.learn2;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.learn2.gameLogic.HumanPlayer;
import com.example.learn2.gameLogic.Player;
import com.example.learn2.gameLogic.RandomPlayer;

public class Cell {
	
	public static boolean lastMovePlayed = false;
	static int lastColumn;
	static Paint paint;
	private static int lastRow;
	private final int row, col;
	HumanPlayer humanPlayer = new HumanPlayer();
	RandomPlayer randomPlayer = new RandomPlayer();
	private int cellSize;
	private boolean occupied;
	private int left, top;
	private Player playerType;
	
	public Cell() {
		this(6, 6);
	}
	
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
	
	public static int getLastColumn() {
		return lastColumn;
	}
	
	public static int getLastRow() {
		return lastRow;
	}
	
	public static void setLastMove(int row, int col) {
		lastRow = row;
		lastColumn = col;
	}
	
	public static Paint getLastPaint() {
		return paint;
	}
	
	public static void setHumanPaint() {
		paint.setColor(Color.RED);
	}
	
	public static void setRandomPaint() {
		paint.setColor(Color.BLUE);
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
	
	public Paint getHumanPaint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.RED);
		return paint1;
	}
	
	public Paint getRandomPaint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.BLUE);
		return paint1;
	}
	
	public Paint getDrawingPaint() {
		Paint paint1 = new Paint();
		paint1.setColor(Color.YELLOW);
		return paint1;
	}
	
	public int getCenter() {
		return cellSize / 2;
	}
	
	public int getX() {
		if (row == 0) {
			return getCenter();
		} else {
			return getCenter() * (col + 1);
		}
	}
	
	public int getY() {
		if (row == 0) {
			return getCenter();
		} else {
			return getCenter() * (row + 1);
		}
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
