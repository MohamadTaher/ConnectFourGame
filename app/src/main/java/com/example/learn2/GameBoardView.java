package com.example.learn2;

import android.annotation.SuppressLint;
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
import com.example.learn2.gameLogic.Player;
import com.example.learn2.gameLogic.RandomPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

//the game visualization and drawing
public class GameBoardView extends View {

	//creating variables that will used
	private static final int NUM_ROWS = 3, NUM_COLUMNS = 3;
	private final HumanPlayer humanPlayer1, humanPlayer2;
	private final RandomPlayer randomPlayer;

	private final Paint cellPaint, gridPaint;
	private final Handler handler;
	private final Runnable updateCircle = this::invalidate;
	private final Cell[][] cells;
	private final Board boardObject;
	private final RectF mRect;
	private int viewHeight, targetY, currentY, column;

	private boolean finishedDrawing;
	private Cell currentCell;

	AtomicBoolean isTouchEnabled;

	Player[] players;
	int currentPlayerIndex = 0;

	//initialize the variable and start the drawing when the view is created for the first time
	public GameBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		finishedDrawing = true;

		humanPlayer1 = new HumanPlayer();
		humanPlayer2 = new HumanPlayer();
		randomPlayer = new RandomPlayer();

		players = new Player[]{humanPlayer1, humanPlayer2, randomPlayer};


		handler = new Handler();
		isTouchEnabled = new AtomicBoolean(true);


		mRect = new RectF();

		boardObject = new Board(NUM_ROWS, NUM_COLUMNS);
		cells = new Cell[NUM_ROWS][NUM_COLUMNS];

		gridPaint = new Paint();
		cellPaint = new Paint();
		cellPaint.setColor(Color.WHITE);
		gridPaint.setColor(Color.BLACK);
		gridPaint.setStrokeWidth(3);

		createCells();
	}
	
	private void drawLastMovePlayed(Canvas canvas, Cell currentCell) {

		int circleDropSpeed = 35;



		if (!finishedDrawing) {
			
			if (currentY < targetY) {
				if (currentCell.getY() == cells[0][column].getY()) {
					targetY = (int) (currentCell.getY() - (currentCell.getY() * 0.65));
				} else if (currentCell.getY() == cells[1][column].getY()) {
					targetY = (int) (currentCell.getY() - (currentCell.getY() * 0.5));
				} else if (currentCell.getY() == cells[2][column].getY()) {
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
				finishedDrawing=true;

			}
			printCellPlayerType();

		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getActionMasked() == MotionEvent.ACTION_DOWN && finishedDrawing && isTouchEnabled.get()) {
			isTouchEnabled.set(false);

			if (players[currentPlayerIndex] instanceof HumanPlayer) {
				HumanPlayer humanPlayer = (HumanPlayer) players[currentPlayerIndex];
				createPlayerMove(event, humanPlayer);
				currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array
			}

			postDelayed(() -> isTouchEnabled.set(true), 700); // Re-enable touch after 700ms

			if (players[currentPlayerIndex] instanceof RandomPlayer) {
				RandomPlayer randomPlayer = (RandomPlayer) players[currentPlayerIndex];
				postDelayed(() -> createPlayerMove(null,randomPlayer), 700);
				currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array
			}

		}

		return super.onTouchEvent(event);

	}
	
	private void createPlayerMove(MotionEvent event, Player player) {


		finishedDrawing = false;

		RandomPlayer randomPlayer;
		HumanPlayer humanPlayer;

		if (boardObject.isFull()){
			createToast("Board is full");
			return;
		}

		if (player instanceof HumanPlayer) {

			column = (int) (event.getX() / getWidth() * NUM_COLUMNS);

			humanPlayer = (HumanPlayer) player;

			if (boardObject.isColumnEmpty(column)) {

				if (humanPlayer == humanPlayer1) {
					Cell.setHuman1LastPaint();
				} else if (humanPlayer == humanPlayer2) {
					Cell.setHuman2LastPaint();
				}
			}
		} else {
			randomPlayer = (RandomPlayer) player;
			do{
				column = randomPlayer.makePlayerMove(boardObject);
			}while (!boardObject.isColumnEmpty(column));
			Cell.setAILastPaint();

		}

		currentY = -cells[0][column].getY();


		while (true) {
			int madeRowMove = boardObject.boardMakeMove(column + 1, player);
			if (madeRowMove != -1) {
				targetY = cells[madeRowMove][column].getY();
				currentCell = cells[madeRowMove][column];
				invalidate();
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
		
		//draw occupied cells
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				if (cells[i][j].isOccupied()) {
					if (boardObject.getPlayerAtLocation(i,j) == humanPlayer1) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint1());
					} else if (boardObject.getPlayerAtLocation(i,j) == humanPlayer2) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint2());
					} else if (boardObject.getPlayerAtLocation(i,j) == randomPlayer) {
						canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getAIPaint());
					}
				}
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawOldBoard(canvas);
		drawLastMovePlayed(canvas, currentCell);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int cellSize = Math.min(MeasureSpec.getSize(widthMeasureSpec) / NUM_COLUMNS, MeasureSpec.getSize(heightMeasureSpec) / NUM_ROWS);
		int width = cellSize * NUM_COLUMNS;
		viewHeight = cellSize * NUM_ROWS;
		updateCellSizes(cellSize);
		setMeasuredDimension(width, viewHeight);
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

	private void printCellPlayerType() {
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				if (boardObject.getPlayerAtLocation(i,j) == humanPlayer1) {
					temp.append("X ");
				} else if (boardObject.getPlayerAtLocation(i,j) == randomPlayer) {
					temp.append("y ");
				} else {
					temp.append("0 ");
				}
			}
			temp.append("\n");
		}
		TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
		textView.setText(temp.toString());

	}


}
