package com.example.learn2.gameLogic;

import java.util.Random;

public class RandomPlayer extends Player {
	
	/**
	 * @param board this parameter is a board object that we will use to get the board rows and columns from
	 * @return this will return the Move object after we modify the getting the random number
	 */
	int makePlayerMove(Board board) {
		Random random = new Random();
		return random.nextInt(board.getColumnSize());
	}
	
	Player getPlayerType() {
		return this;
	}
}