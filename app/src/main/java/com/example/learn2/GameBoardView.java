package com.example.learn2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learn2.gameLogic.Board;
import com.example.learn2.gameLogic.HumanPlayer;
import com.example.learn2.gameLogic.RandomPlayer;

public class GameBoardView extends View {
	
	private static final int NUM_ROWS = 8, NUM_COLUMNS = 8;
	private final HumanPlayer humanPlayer1;
	private final HumanPlayer humanPlayer2;
	private final Paint cellPaint, gridPaint;
	private final Handler handler;
	private final Runnable updateCircle = this::invalidate;
	private final Cell[][] cells;
	private final RandomPlayer randomPlayer;
	private final Board boardObject;
	private final RectF mRect;
	private int targetY, currentY;
	private int viewHeight;
	private boolean finishedDrawing;
	
	private boolean switcher;
	
	public GameBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		finishedDrawing = true;
		switcher = false;
		boardObject = new Board(NUM_ROWS, NUM_COLUMNS);
		
		Cell.lastColumn = 0;
		humanPlayer1 = new HumanPlayer();
		humanPlayer2 = new HumanPlayer();
		randomPlayer = new RandomPlayer();
		handler = new Handler();
		mRect = new RectF();
		cells = new Cell[NUM_ROWS][NUM_COLUMNS];
		gridPaint = new Paint();
		cellPaint = new Paint();
		cellPaint.setColor(Color.WHITE);
		gridPaint.setColor(Color.BLACK);
		gridPaint.setStrokeWidth(3);
		createCells();
	}
	
	private void drawLastMovePlayed(Canvas canvas) {
		
		int circleDropSpeed = 35, i = Cell.getLastRow(), j = Cell.getLastColumn();
		Cell currentCell = cells[i][j];
		
		if (Cell.lastMovePlayed && !finishedDrawing) {
			
			if (currentY < targetY) {
				if (currentCell.getY() == cells[0][j].getY()) {
					targetY = (int) (currentCell.getY() - (currentCell.getY() * 0.65));
				} else if (currentCell.getY() == cells[1][j].getY()) {
					targetY = (int) (currentCell.getY() - (currentCell.getY() * 0.5));
				} else if (currentCell.getY() == cells[2][j].getY()) {
					targetY = (int) (currentCell.getY() - (currentCell.getY() * 0.35));
				} else {
					circleDropSpeed = 60;
				}
				currentY += circleDropSpeed;
				canvas.drawCircle(currentCell.getCenterX(), currentY * 1.65f, currentCell.getRadius(), currentCell.getDrawingPaint());
				handler.postDelayed(updateCircle, 13); // wait for 16 milliseconds before redrawing the view
			} else {
				canvas.drawCircle(currentCell.getCenterX(), currentCell.getCenterY(), currentCell.getRadius(), Cell.getLastPaint());
				currentCell.setOccupied(true);
				finishedDrawing = true;
			}
			printCellPlayerType();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN && finishedDrawing) {
			
			if (!switcher) {
				if (createHumanPlayerMove(event, humanPlayer1)) {
					return true;
				}
				switcher = true;
			} else {
				if (createHumanPlayerMove(event, humanPlayer2)) {
					return true;
				}
				switcher = false;
			}
			//			postDelayed(this::createRandomPlayerMove, 700);
			
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}
	
	private boolean createHumanPlayerMove(MotionEvent event, HumanPlayer humanPlayer) {
		
		int madeRowMove;
		if (boardObject.isColumnEmpty((int) (event.getX() / getWidth() * NUM_COLUMNS))) {
			
			if (humanPlayer == humanPlayer1) {
				Cell.setHuman1LastPaint();
			} else if (humanPlayer == humanPlayer2) {
				Cell.setHuman2LastPaint();
			}
			
			finishedDrawing = false;
			Cell.lastColumn = (int) (event.getX() / getWidth() * NUM_COLUMNS);
			currentY = -cells[0][Cell.lastColumn].getY();
			
			while (true) {
				madeRowMove = boardObject.boardMakeMove(Cell.getLastColumn() + 1, humanPlayer);
				if (madeRowMove != -1) {
					
					createToast("human created move");
					
					Cell.lastMovePlayed = true;
					int humanColumn = Cell.lastColumn;
					
					createToast("human registered move");
					
					Cell.setLastMove(madeRowMove, Cell.getLastColumn());
					updatePlayerTurnView("Human. row:" + madeRowMove + ", col:" + humanColumn);
					targetY = cells[madeRowMove][humanColumn].getY();
					cells[madeRowMove][humanColumn].setPlayerType(humanPlayer);
					invalidate();
					
					return false;
				}
			}
		} else return true;
		
	}
	
	private void createRandomPlayerMove() {
		int madeRowMove;
		
		int randomColumn = randomPlayer.makePlayerMove(boardObject);
		while (true) {
			if (!boardObject.isColumnEmpty(randomColumn)) {
				randomColumn = randomPlayer.makePlayerMove(boardObject);
			} else {
				currentY = -cells[0][randomColumn].getY();
				while (true) {
					madeRowMove = boardObject.boardMakeMove(randomColumn + 1, randomPlayer);
					if (madeRowMove != -1) {
						createToast("ai created move");
						finishedDrawing = false;
						Cell.lastMovePlayed = true;
						Cell.setAILastPaint();
						Cell.setLastMove(madeRowMove, randomColumn);
						updatePlayerTurnView("Random. row:" + madeRowMove + ", col:" + randomColumn);
						Cell.setLastMove(madeRowMove, randomColumn);
						targetY = cells[madeRowMove][randomColumn].getY();
						cells[madeRowMove][randomColumn].setPlayerType(randomPlayer);
						invalidate();
						
						break;
					}
				}
				return;
			}
		}
	}
	
	private void drawOldBoard(Canvas canvas) {
		
		//drawBlueStars
		float mCornerRadius = 20f;
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				Cell cell = cells[i][j];
				mRect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
				canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellPaint);
			}
		}
		
		//drawGridLines
		for (int i = 0; i <= NUM_COLUMNS; i++) {
			int x;
			if (i == 0) {
				x = (int) (cells[0][0].getCellSize() * 0.01f);
			} else {
				x = i * cells[0][0].getCellSize();
			}
			canvas.drawLine(x, 0, x, viewHeight, gridPaint);
		}
		for (int i = 0; i <= NUM_ROWS; i++) {
			int y;
			if (i == NUM_ROWS) {
				y = i * cells[0][0].getCellSize() - (int) (cells[0][0].getCellSize() * 0.005f);
			} else if (i == 0) {
				y = (int) (cells[0][0].getCellSize() * 0.01f);
			} else {
				y = i * cells[0][0].getCellSize();
			}
			canvas.drawLine(0, y, cells[0][0].getCellSize() * NUM_COLUMNS, y, gridPaint);
		}
		
		// drawOccupiedCells
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				if (cells[i][j].isOccupied()) {
					if (cells[i][j].getPlayerType() == humanPlayer1) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint1());
					} else if (cells[i][j].getPlayerType() == randomPlayer) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getAIPaint());
					} else if (cells[i][j].getPlayerType() == humanPlayer2) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint2());
					}
				}
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawOldBoard(canvas);
		drawLastMovePlayed(canvas);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int cellSize = Math.min(MeasureSpec.getSize(widthMeasureSpec) / NUM_COLUMNS, MeasureSpec.getSize(heightMeasureSpec) / NUM_ROWS);
		int width = cellSize * NUM_COLUMNS;
		int height = cellSize * NUM_ROWS;
		updateCellSizes(cellSize);
		viewHeight = height;
		setMeasuredDimension(width, viewHeight);
	}
	
	private void printCellPlayerType() {
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				if (cells[i][j].getPlayerType() == humanPlayer1) {
					temp.append("X ");
				} else if (cells[i][j].getPlayerType() == randomPlayer) {
					temp.append("O ");
				} else {
					temp.append("_ ");
				}
			}
			temp.append("\n");
		}
		updatePlayerTurnView(temp.toString());
		
	}
	
	private void createCells() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLUMNS; col++) {
				cells[row][col] = new Cell(row, col);
			}
		}
	}
	
	private void updateCellSizes(int cellSize) {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLUMNS; col++) {
				cells[row][col].updateCellSize(cellSize);
			}
		}
	}
	
	private void createToast(String output) {
		Toast toast = Toast.makeText(getContext(), output, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private void updatePlayerTurnView(String input) {
		TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
		textView.setText(input);
		
	}
}
