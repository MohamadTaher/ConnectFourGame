package com.example.learn2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //setting up the main screen with the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar gridSeekBar = findViewById(R.id.gridSeekBar);
        TextView gridSize = findViewById(R.id.gridSize);
        gridSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress + 6; // As the first option is 6x6
                gridSize.setText(size + "×" + size);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });


        SeekBar humanSeekBar = findViewById(R.id.humanSeekBar);
        TextView humanSize = findViewById(R.id.humanSize);
        humanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                humanSize.setText(String.valueOf(progress + 1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        SeekBar AISeekBar = findViewById(R.id.AISeekBar);
        TextView AISize = findViewById(R.id.AISize);
        AISeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AISize.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });



    }


    //switching to gameView
    public void playButtonClick(View view) {
        Intent intent = new Intent(this, GameWindow.class);

        SeekBar humanSeekBar = findViewById(R.id.humanSeekBar);
        SeekBar AISeekBar = findViewById(R.id.AISeekBar);
        SeekBar gridSeekBar = findViewById(R.id.gridSeekBar);



        intent.putExtra("humanSize", humanSeekBar.getProgress()+1);
        intent.putExtra("AISize", AISeekBar.getProgress());
        intent.putExtra("gridSize", gridSeekBar.getProgress()+6);


        startActivity(intent);
    }


}

