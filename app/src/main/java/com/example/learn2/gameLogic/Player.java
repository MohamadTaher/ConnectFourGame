package com.example.learn2.gameLogic;

public abstract class Player {
	
	// those two abstract methods that will be given body inside the child class
	abstract Player getPlayerType();
	
	abstract int makePlayerMove(Board board);
}
