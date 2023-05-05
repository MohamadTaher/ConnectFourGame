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

public class GameBoardView extends View {
	
	private static final int NUM_ROWS = 6, NUM_COLUMNS = 6;
	
	private final Paint cellPaint, gridPaint;
	private final Cell[][] cells;
	private final Handler handler = new Handler();
	private final Runnable updateCircle = this::invalidate;
	public boolean ableToMakeMove;
	RectF mRect;
	
	int targetY, currentY;
	
	private int viewWidth, viewHeight;
	
	public GameBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRect = new RectF();
		cells = new Cell[NUM_ROWS][NUM_COLUMNS];
		ableToMakeMove = true;
		gridPaint = new Paint();
		cellPaint = new Paint();
		cellPaint.setColor(Color.WHITE);
		gridPaint.setColor(Color.BLACK);
		gridPaint.setStrokeWidth(3);
		createCells();
	}
	
	private void createCells() {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLUMNS; col++) {
				cells[row][col] = new Cell(row, col);
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int cellSize = Math.min(MeasureSpec.getSize(widthMeasureSpec) / NUM_COLUMNS, MeasureSpec.getSize(heightMeasureSpec) / NUM_ROWS);
		int width = cellSize * NUM_COLUMNS;
		int height = cellSize * NUM_ROWS;
		updateCellSizes(cellSize);
		viewWidth = width;
		viewHeight = height;
		setMeasuredDimension(viewWidth, viewHeight);
	}
	
	private void updateCellSizes(int cellSize) {
		for (int row = 0; row < NUM_ROWS; row++) {
			for (int col = 0; col < NUM_COLUMNS; col++) {
				cells[row][col].updateCellSize(cellSize);
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBackground(canvas);
		drawBlueStars(canvas);
		drawGridLines(canvas);
		drawOccupiedCells(canvas);
		drawLastMovePlayed(canvas);
	}
	
	private void drawBackground(Canvas canvas) {
		canvas.drawColor(Color.BLUE);
	}
	
	private void drawBlueStars(Canvas canvas) {
		float mCornerRadius = 20f;
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				Cell cell = cells[i][j];
				mRect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
				canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellPaint);
			}
		}
	}
	
	private void drawGridLines(Canvas canvas) {
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
	}
	
	
	private void drawOccupiedCells(Canvas canvas) {
		// Draw the cells
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLUMNS; j++) {
				if (cells[i][j].isOccupied()) {
					canvas.drawCircle(cells[i][j].getLeft() + cells[i][j].getCenter(), cells[i][j].getTop() + cells[i][j].getCenter(), (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f), cells[i][j].getCirclePaint());
				}
			}
		}
	}
	
	private void drawLastMovePlayed(Canvas canvas) {
		if (Cell.lastMovePlayed) {
			
			int circleDropSpeed = 35;
			
			int i = Cell.getLastRow(), j = Cell.getLastColumn();
			int centerX = cells[i][j].getLeft() + cells[i][j].getCenter(), centerY = cells[i][j].getTop() + cells[i][j].getCenter();
			float radius = (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f);
			
			if (currentY < targetY) {
				
				if (cells[i][j].getY() == cells[0][j].getY()) {
					targetY = (int) (cells[i][j].getY() - (cells[i][j].getY() * 0.65));
				} else if (cells[i][j].getY() == cells[1][j].getY()) {
					targetY = (int) (cells[i][j].getY() - (cells[i][j].getY() * 0.5));
				} else if (cells[i][j].getY() == cells[2][j].getY()) {
					targetY = (int) (cells[i][j].getY() - (cells[i][j].getY() * 0.35));
				} else {
					circleDropSpeed = 50;
				}
				currentY += circleDropSpeed;
				canvas.drawCircle(centerX, currentY * 1.65f, radius, cells[i][j].getCirclePaint2());
				handler.postDelayed(updateCircle, 13); // wait for 16 milliseconds before redrawing the view
			} else if (targetY <= currentY) {
				canvas.drawCircle(centerX, centerY, radius, cells[i][j].getCirclePaint2());
				createToast("finished");
				cells[i][j].setOccupied(true);
			}
			
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		if (action == MotionEvent.ACTION_DOWN && ableToMakeMove) {
			ableToMakeMove = false;
			// Get the touch coordinates
			float x = event.getX();
			float y = event.getY();
			
			// Determine the row and column of the cell that was touched
			int row = (int) (y / getHeight() * NUM_ROWS);
			int column = (int) (x / getWidth() * NUM_COLUMNS);
			
			currentY = -cells[0][column].getY();
			targetY = cells[row][column].getY();
			
			
			Cell.setLastMove(row, column);
			
			updatePlayerTurnView("Cell pressed: (" + cells[row][column].getRow() + ")" + " (" + cells[row][column].getCol() + ")");
			
			// Call performClick() to handle the click event
			performClick();
			
			drawAMove();
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}
	
	public void createToast(String output) {
		Context context = getContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, output, duration);
		toast.show();
	}
	
	public void drawAMove() {
		ableToMakeMove = true;
		invalidate();
	}
	
	public void updatePlayerTurnView(String input) {
		TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
		textView.setText(input);
		
	}
}
