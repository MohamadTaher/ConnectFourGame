package com.example.learn2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class GameBoardView extends View {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 6;

    private final Paint cellPaint, gridPaint;
    RectF mRect; // A reusable RectF object

    private final Cell[][] cells = new Cell[NUM_ROWS][NUM_COLUMNS];;
    private int viewWidth, viewHeight;

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new RectF();

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

        // Draw the cells
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                Cell cell = cells[i][j];
                mRect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
                canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellPaint);

            }
        }

        // Draw the grid lines
        for (int i = 0; i <= NUM_COLUMNS; i++) {
            int x = i * cells[0][0].getCellSize();
            canvas.drawLine(x, 0, x, viewHeight, gridPaint);
        }
        for (int i = 0; i <= NUM_ROWS; i++) {
            int y = i * cells[0][0].getCellSize();
            canvas.drawLine(0, y, viewWidth, y, gridPaint);
        }

        // Draw the cells
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                if (cells[i][j].isOccupied()) {
                    TextView textView = (TextView) ((Activity) getContext()).findViewById(R.id.textView);
                    textView.setText("Cell pressed: at ("+ cells[i][j].getRow() + ")"+ " (" + cells[i][j].getCol() + ")");
                    canvas.drawCircle(cells[i][j].getLeft()+cells[i][j].getCenter(), cells[i][j].getTop() +cells[i][j].getCenter(),
                                      (cells[i][j].getCellSize() / 2f) - (cells[i][j].getCellSize()*0.06f), cells[i][j].getCirclePaint());
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // Get the touch coordinates
            float x = event.getX();
            float y = event.getY();

            // Determine the row and column of the cell that was touched
            int row = (int) (y / getHeight() * NUM_ROWS);
            int column = (int) (x / getWidth() * NUM_COLUMNS);

            // Update the last touched cell
            cells[row][column].setOccupied(true);

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
}
