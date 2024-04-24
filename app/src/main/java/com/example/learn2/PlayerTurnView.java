package com.example.learn2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.learn2.gameLogic.HumanPlayer;
import com.example.learn2.gameLogic.Player;
import com.example.learn2.gameLogic.RandomPlayer;


public class PlayerTurnView extends View {


    private final HumanPlayer humanPlayer1, humanPlayer2, humanPlayer3;
    private final RandomPlayer randomPlayer1, randomPlayer2;
    Player[] players;


    int humanPlayerCount, randomPlayerCount, total_number_of_players, cellSize;

    private int viewHeight;
    private int viewWidth;

    public PlayerTurnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Intent intent = ((Activity) context).getIntent();
        humanPlayerCount = intent.getIntExtra("humanSize", 2);
        randomPlayerCount = intent.getIntExtra("AISize", 1);
        total_number_of_players = humanPlayerCount + randomPlayerCount;


        humanPlayer1 = new HumanPlayer();
        humanPlayer2 = new HumanPlayer();
        humanPlayer3 = new HumanPlayer();
        randomPlayer1 = new RandomPlayer();
        randomPlayer2 = new RandomPlayer();

        players = new Player[(humanPlayerCount + randomPlayerCount)];

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
    }

    /**
     * @param canvas Main canvas to draw
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        reDrawBoard(canvas);


    }

    private void reDrawBoard(Canvas canvas) {

        canvas.drawColor(Color.BLUE);

        RectF mRect = new RectF();

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(3);

        Paint cellBackGroundPaint = new Paint();
        cellBackGroundPaint.setColor(Color.WHITE);


        float mCornerRadius = 20f;


        for (int i = 0; i < total_number_of_players; i++) {
            mRect.set(cellSize * i, (float) (cellSize * 0.01), (float) ((cellSize * (i + 1)) - (cellSize * 0.01)), (float) (viewHeight - (cellSize * 0.01)));
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, cellBackGroundPaint);
        }



    }

    /**
     * @param widthMeasureSpec  Get the width of the view
     * @param heightMeasureSpec Get the height of the view
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        cellSize = MeasureSpec.getSize(widthMeasureSpec) / total_number_of_players;
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
//        updateCellSizes(cellSize);
        setMeasuredDimension(viewWidth, viewHeight);

    }

    /**
     * @param event The main event
     * @return super.onTouchEvent(event) the event that need to be processed
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        createToast("yuo" + viewWidth);
        return super.onTouchEvent(event);
    }

    private void createToast(String output) {
        Toast toast = Toast.makeText(getContext(), output, Toast.LENGTH_SHORT);
        toast.show();
    }


}
