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

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 6;

    private final Paint cellPaint, gridPaint;
    private final Cell[][] cells;
    private final Handler handler = new Handler();
    private final Runnable updateCircle = // redraw the view
            this::invalidate;
    public boolean finishedDrawing;
    RectF mRect; // A reusable RectF object
    int currentY;
    int targetY;
    private int viewWidth, viewHeight;

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);


        mRect = new RectF();
        cells = new Cell[NUM_ROWS][NUM_COLUMNS];

        finishedDrawing = true;

        cellPaint = new Paint();
        cellPaint.setColor(Color.WHITE);

        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(3);

        // Create the cells
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Calculate the required size of the view based on the number of cells and the size of each cell
        int cellSize = Math.min(MeasureSpec.getSize(widthMeasureSpec) / NUM_COLUMNS, MeasureSpec.getSize(heightMeasureSpec) / NUM_ROWS);
        int width = cellSize * NUM_COLUMNS;
        int height = cellSize * NUM_ROWS;


        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLUMNS; col++) {
                cells[row][col].updateCellSize(cellSize);
            }
        }

        // Save the dimensions of the view
        viewWidth = width;
        viewHeight = height;

        // Set the dimensions of the view
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the background color of the board
        canvas.drawColor(Color.BLUE);

        float mCornerRadius = 20f; // The radius of the corners

        // Draw the blue stars
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                Cell cell = cells[i][j];
                mRect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
                canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellPaint);

            }
        }

        // Draw the grid lines
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

        // Draw the cells
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                if (cells[i][j].isOccupied()) {
                    canvas.drawCircle(cells[i][j].getLeft() + cells[i][j].getCenter(), cells[i][j].getTop() + cells[i][j].getCenter(), (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f), cells[i][j].getCirclePaint());
                }
            }
        }

        if (Cell.lastMovePlayed) {
            TextView textView = ((Activity) getContext()).findViewById(R.id.textView);

            int i = Cell.getLastRow();
            int j = Cell.getLastColumn();

            if (cells[i][j].getY() == cells[0][j].getY()) {
                canvas.drawCircle(cells[i][j].getLeft() + cells[i][j].getCenter(), cells[i][j].getTop() + cells[i][j].getCenter(), (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f), cells[i][j].getCirclePaint());
                cells[i][j].setOccupied(true);
                finishedDrawing = true;
                textView.setText("topY:" + currentY + ", yClicked:" + targetY);
            } else if (currentY < targetY) {
                currentY += 50;
                canvas.drawCircle(cells[i][j].getLeft() + cells[i][j].getCenter(), currentY * 1.65f, (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f), cells[i][j].getCirclePaint2());
                handler.postDelayed(updateCircle, 16); // wait for 50 milliseconds before redrawing the view
            } else if (targetY <= currentY) {
                canvas.drawCircle(cells[i][j].getLeft() + cells[i][j].getCenter(), cells[i][j].getTop() + cells[i][j].getCenter(), (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize() * 0.06f), cells[i][j].getCirclePaint2());
                createToast("finished");
                cells[i][j].setOccupied(true);
                finishedDrawing = true;
            }

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN && finishedDrawing) {
            finishedDrawing = false;
            // Get the touch coordinates
            float x = event.getX();
            float y = event.getY();

            // Determine the row and column of the cell that was touched
            int row = (int) (y / getHeight() * NUM_ROWS);
            int column = (int) (x / getWidth() * NUM_COLUMNS);

            currentY = -cells[0][column].getY();
            targetY = cells[row][column].getY();


            Cell.setLastMove(row, column);

            TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
            textView.setText("Cell pressed: (" + cells[row][column].getRow() + ")" + " (" + cells[row][column].getCol() + ")");

            // Call performClick() to handle the click event
            performClick();

            invalidate();
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
}
