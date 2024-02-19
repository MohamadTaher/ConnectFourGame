package com.example.learn2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameWindow extends AppCompatActivity {


	//setting up the game screen to the xml file
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_window);
	}
}

