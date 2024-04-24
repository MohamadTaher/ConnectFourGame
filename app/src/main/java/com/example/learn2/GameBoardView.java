package com.example.learn2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//the game visualization and drawing
public class GameBoardView extends View {

    //creating variables that will used
    private final int NUM_ROWS, NUM_COLUMNS;
    private final HumanPlayer humanPlayer1, humanPlayer2, humanPlayer3;
    private final RandomPlayer randomPlayer1, randomPlayer2;
    private final Cell[][] cells;
    private final Board boardObject;
    private int viewHeight, targetY, currentY, column;
    int currentPlayerIndex = 0;
    private boolean finishedDrawing;
    private final boolean startedWinningAnimation;
    private Cell currentCell;
    AtomicBoolean isTouchEnabled;
    private Canvas canvas;
    Handler handler;
    Player[] players;

    //initialize the variable and start the drawing when the view is created for the first time
    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Intent intent = ((Activity) context).getIntent();

        NUM_ROWS = NUM_COLUMNS = intent.getIntExtra("gridSize", 6);

        int humanPlayerCount = intent.getIntExtra("humanSize", 2), randomPlayerCount = intent.getIntExtra("AISize", 1);

        humanPlayer1 = new HumanPlayer();
        humanPlayer2 = new HumanPlayer();
        humanPlayer3 = new HumanPlayer();
        randomPlayer1 = new RandomPlayer();
        randomPlayer2 = new RandomPlayer();

        players = new Player[(humanPlayerCount + randomPlayerCount)];

        handler = new Handler();


        switch (humanPlayerCount) {
            case 1:
                players[0] = humanPlayer1;
                break;
            case 2:
                players[0] = humanPlayer1;
                players[1] = humanPlayer2;
                break;
            case 3:
                players[0] = humanPlayer1;
                players[1] = humanPlayer2;
                players[2] = humanPlayer3;
                break;
            default:
        }

        switch (randomPlayerCount) {
            case 1:
                players[players.length - 1] = randomPlayer1;
                break;
            case 2:
                players[players.length - 1] = randomPlayer1;
                players[players.length - 2] = randomPlayer2;
                break;
            default:
        }

        isTouchEnabled = new AtomicBoolean(true);


        boardObject = new Board(NUM_ROWS, NUM_COLUMNS);
        cells = new Cell[NUM_ROWS][NUM_COLUMNS];

        startedWinningAnimation = false;

        finishedDrawing = true;
        createCells();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && finishedDrawing && isTouchEnabled.get()) {


            if (boardObject.win() == null) {
                playMoves(event);
            }

            if (boardObject.win() != null && !startedWinningAnimation) {
                List<Point> winningPositions = boardObject.getWinningLocations();
                createToast("" + winningPositions);
                startWinningAnimation(winningPositions);
//				startedWinningAnimation = true;

                TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
                if (boardObject.win() == humanPlayer1) {
                    textView.setText("Red player won.");
                } else if (boardObject.win() == humanPlayer2) {
                    textView.setText("Blue Player won.");
                } else if (boardObject.win() == humanPlayer3) {
                    textView.setText("Green player won.");
                } else if (boardObject.win() == randomPlayer1) {
                    textView.setText("Yellow won.");
                } else if (boardObject.win() == randomPlayer2) {
                    textView.setText("Light Blue player won.");
                }


            }
        }
        return super.onTouchEvent(event);

    }

    private void playMoves(MotionEvent event) {
        isTouchEnabled.set(false);

        if (players[currentPlayerIndex] instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) players[currentPlayerIndex];
            createPlayerMove(event, humanPlayer);
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array
        }

        //return before playing the random move if human player won
        if(boardObject.win() != null){
            postDelayed(() -> isTouchEnabled.set(true), 700); // Re-enable touch after 700ms
            return;
        }

        if (players[currentPlayerIndex] instanceof RandomPlayer) { //random player 1
            if (players[(currentPlayerIndex + 1) % players.length] instanceof RandomPlayer) {

                RandomPlayer randomPlayer = (RandomPlayer) players[currentPlayerIndex];

                postDelayed(() -> {
                    createPlayerMove(null, randomPlayer);
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array

                    //Bug here right now.
                    RandomPlayer randomPlayer2 = (RandomPlayer) players[currentPlayerIndex];
                    postDelayed(() -> {
                        createPlayerMove(null, randomPlayer2);
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array
                    }, 800);
                }, 800);
                postDelayed(() -> isTouchEnabled.set(true), 1600); // Re-enable touch after 700ms

            } else {
                RandomPlayer randomPlayer2 = (RandomPlayer) players[currentPlayerIndex];
                postDelayed(() -> createPlayerMove(null, randomPlayer2), 800);
                currentPlayerIndex = (currentPlayerIndex + 1) % players.length; // Circular array
            }

        }
        postDelayed(() -> isTouchEnabled.set(true), 700); // Re-enable touch after 700ms
    }

    private void createPlayerMove(MotionEvent event, Player player) {


        finishedDrawing = false;

        RandomPlayer randomPlayer;
        HumanPlayer humanPlayer;

        if (boardObject.isFull()) {
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
                } else {
                    Cell.setHuman3LastPaint();
                }
            }
        } else {
            randomPlayer = (RandomPlayer) player;
            do {
                column = randomPlayer.makePlayerMove(boardObject);
            } while (!boardObject.isColumnEmpty(column));

            if (randomPlayer == randomPlayer1) {
                Cell.setAI1LastPaint();
            } else if (randomPlayer == randomPlayer2) {
                Cell.setAI2LastPaint();
            }

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

    private void drawLastMovePlayed(Canvas canvas, Cell currentCell) {

        int circleDropSpeed = 35;


        Runnable updateCircle = this::invalidate;


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
                canvas.drawCircle(currentCell.getCenterX(), currentY * 1.65f, currentCell.getRadius(), Cell.getLastPaint());
                handler.postDelayed(updateCircle, 13); // wait for 16 milliseconds before redrawing the view
            } else {
                canvas.drawCircle(currentCell.getCenterX(), currentCell.getCenterY(), currentCell.getRadius(), Cell.getLastPaint());
                currentCell.setOccupied(true);
                finishedDrawing = true;
            }

        }
    }

    private void startWinningAnimation(List<Point> winningPositions) {

//		Runnable updateCircle = this::invalidate;
//
//
//		int colorIndex = 0; // Index for color array
//		Paint[] winningColors = new Paint[] {
//				new Paint(Color.RED),
//				new Paint(Color.GREEN),
//				new Paint(Color.BLUE),
//				new Paint(Color.YELLOW)
//		};
//
//		// Draw circles with changing colors
//		for (Point winningPosition : winningPositions) {
//			Cell winningCell = cells[winningPosition.x][winningPosition.y];
//			canvas.drawCircle(winningCell.getCenterX(), winningCell.getCenterY(), winningCell.getRadius(), winningColors[colorIndex]);
//		}
//
//		colorIndex = (colorIndex + 1) % winningColors.length;
//		handler.postDelayed(updateCircle, 13); // wait for 16 milliseconds before redrawing the view


    }

    private void drawOldBoard(Canvas canvas) {

        canvas.drawColor(Color.BLUE);

        RectF mRect = new RectF();

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(3);

        Paint cellBackGroundPaint = new Paint();
        cellBackGroundPaint.setColor(Color.WHITE);


        //draw the cell background
        float mCornerRadius = 20f;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                Cell cell = cells[i][j];
                mRect.set(cell.getLeft(), cell.getTop(), cell.getRight(), cell.getBottom());
                canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellBackGroundPaint);
            }
        }


        //draw horizontal lines
        for (int i = 0; i <= NUM_COLUMNS; i++) {
            int x;
            if (i == 0) {
                x = (int) (cells[0][0].getCellSize() * 0.01f);
            } else {
                x = i * cells[0][0].getCellSize();
            }
            canvas.drawLine(x, 0, x, viewHeight, gridPaint);
        }


        //draw vertical lines
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
                    if (boardObject.getPlayerAtLocation(i, j) == humanPlayer1) {
                        canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint1());
                    } else if (boardObject.getPlayerAtLocation(i, j) == humanPlayer2) {
                        canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint2());
                    } else if (boardObject.getPlayerAtLocation(i, j) == humanPlayer3) {
                        canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getHumanPaint3());
                    } else if (boardObject.getPlayerAtLocation(i, j) == randomPlayer1) {
                        canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getAI1Paint());
                    } else if (boardObject.getPlayerAtLocation(i, j) == randomPlayer2) {
                        canvas.drawCircle(cells[i][j].getCenterX(), cells[i][j].getCenterY(), cells[i][j].getRadius(), cells[i][j].getAI2Paint());
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
                if (boardObject.getPlayerAtLocation(i, j) == humanPlayer1) {
                    temp.append("X ");
                } else if (boardObject.getPlayerAtLocation(i, j) == humanPlayer2) {
                    temp.append("Y ");
                } else if (boardObject.getPlayerAtLocation(i, j) == humanPlayer3) {
                    temp.append("Z ");
                } else if (boardObject.getPlayerAtLocation(i, j) == randomPlayer1) {
                    temp.append("R ");
                } else if (boardObject.getPlayerAtLocation(i, j) == randomPlayer2) {
                    temp.append("T ");
                } else temp.append("0 ");
            }
            temp.append("\n");
        }
        TextView textView = ((Activity) getContext()).findViewById(R.id.textView);
        textView.setText(temp.toString());

    }

}
