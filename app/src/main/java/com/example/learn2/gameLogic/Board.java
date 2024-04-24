package com.example.learn2.gameLogic;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that holds the logic and date for the whole board such as winning condition and keeping track of players locations.
 */
public class Board {

	private final Player[][] boardData; //The default two dimensional boardData that holds players

	private final int rowSize, columnSize; //Storing the size of the rows and columns in the array (we get this one from constructor)

	private final List<Point> winningLocations;


	/**
	 * public constructor that doesn't take anything and only initialize boardData and set row/columns to default
	 * @param rowSize    the rows I will take as the board length
	 * @param columnSize the rows I will take as the board width
	 */
	public Board(int rowSize, int columnSize) {
		this.rowSize = rowSize;
		this.columnSize = columnSize;
		this.boardData = new Player[rowSize][columnSize];
		winningLocations = new ArrayList<>();
		initializeDefaultBoardData(boardData);
	}
	
	/**
	 * This method is for making the move logic for when we use it inside the
	 * connect four class
	 * @param moveInputColumn the column we will make the move at
	 * @param player          the player that will make the move for
	 * @return currentRow if it possible to make a move
	 */
	public int boardMakeMove(int moveInputColumn, Player player) {
		
		
		int moveColumn = moveInputColumn - 1; // the column that we will use inside the board (the place we play at)
		int currentRow = rowSize - 1; // the value is the one we have inside the class
		
		// make sure we are in bound
		if (moveColumn < 0 || moveColumn >= columnSize) {
			return -1;
		}
		
		if (boardData[currentRow][moveColumn] == null) { // check if the place to play is empty or not
			boardData[currentRow][moveColumn] = player; // if empty then play on the board by changing to player
			return currentRow;
		} else {
			// the current row to keep track on where we are on the board
			currentRow = rowSize - 1;
			
			while (currentRow >= 0 && !(boardData[currentRow][moveColumn] == null)) { // while the move position is not empty
				
				// check if we are out of bounds
				if (currentRow <= 0) {
					return -1;
				}
				
				// decrease the current row by one to be used as counter to check the above row
				// if empty or not
				currentRow--;
				
				// after decreasing the current row by one, we need to check if the above row is
				// empty or not
				if (boardData[currentRow][moveColumn] == null) {
					// if empty then the player move on the board
					boardData[currentRow][moveColumn] = player;
					
					return currentRow; // the loop will stop after finding a none empty spot and printing the player
					// move
				}
			}
			
			return -1;
		}
	}

	/**
	 * Checking if the column at @col is empty.
	 * @param col the column we want to check.
	 * @return true if it is empty and false if it is full.
	 */
	public boolean isColumnEmpty(int col) {
		return boardData[0][col] == null;
	}

	/**
	 * This method will access the board to get the how many Columns
	 * @return the length of second dimension of the array that represent Columns
	 */
	public int getColumnSize() {
		return columnSize;
	}
	
	/**
	 * getting the player at the current location for boardShow to use as output
	 * @param row    this one will return the row of the player at the current location
	 * @param column this one will return the row of the player at the current location
	 * @return player at location
	 */
	public Player getPlayerAtLocation(int row, int column) {
		return boardData[row][column];
	}
	
	/**
	 * initialize the boardData to null
	 * @param player the boardData we want to initialize as parameter
	 */
	private void initializeDefaultBoardData(Player[][] player) {
		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < columnSize; j++) {
				player[i][j] = null;
			}
		}
	}

	
	/**
	 * Checking if the board is full or not
	 * @return true if the board is full and false otherwise.
	 */
	public boolean isFull() {

		for (int i = 0; i < rowSize; i++) {
			for (int j = 0; j < columnSize; j++) {
				if(boardData[i][j] == null){
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * This method is to check if any player won
	 * check for for any winning condition by checking condition (horizontal/vertical/diagonal/inverse diagonal)
	 * @return return which player among the players won as player type
	 */
	public Player win() {

		// Check for 4 pieces across (horizontal)
		for (int i = 0; i < boardData.length; i++) {
			for (int j = 0; j < boardData[0].length - 3; j++) {
				if (boardData[i][j] == boardData[i][j + 1] && boardData[i][j] == boardData[i][j + 2] && boardData[i][j] == boardData[i][j + 3] && boardData[i][j] != null) {
					// Store winning coordinates
					winningLocations.clear();
					for (int k = 0; k < 4; k++) {
						winningLocations.add(new Point(i, j + k));
					}
					return boardData[i][j];
				}
			}
		}

		// Check for 4 up and down (vertical)
		for (int i = 0; i < boardData.length - 3; i++) {
			for (int j = 0; j < boardData[0].length; j++) {
				if (boardData[i][j] == boardData[i + 1][j] && boardData[i][j] == boardData[i + 2][j] && boardData[i][j] == boardData[i + 3][j] && boardData[i][j] != null) {
					// Store winning coordinates
					winningLocations.clear();
					for (int k = 0; k < 4; k++) {
						winningLocations.add(new Point(i + k, j));
					}
					return boardData[i][j];
				}
			}
		}

		// Check upward diagonal
		for (int i = 3; i < boardData.length; i++) {
			for (int j = 0; j < boardData[0].length - 3; j++) {
				if (boardData[i][j] == boardData[i - 1][j + 1] && boardData[i][j] == boardData[i - 2][j + 2] && boardData[i][j] == boardData[i - 3][j + 3] && boardData[i][j] != null) {
					// Store winning coordinates
					winningLocations.clear();
					for (int k = 0; k < 4; k++) {
						winningLocations.add(new Point(i - k, j + k));
					}
					return boardData[i][j];
				}
			}
		}

		// Check downward diagonal
		for (int i = 0; i < boardData.length - 3; i++) {
			for (int j = 0; j < boardData[0].length - 3; j++) {
				if (boardData[i][j] == boardData[i + 1][j + 1] && boardData[i][j] == boardData[i + 2][j + 2] && boardData[i][j] == boardData[i + 3][j + 3] && boardData[i][j] != null) {
					// Store winning coordinates
					winningLocations.clear();
					for (int k = 0; k < 4; k++) {
						winningLocations.add(new Point(i + k, j + k));
					}
					return boardData[i][j];
				}
			}
		}

		return null; // No winner found
	}

	public List<Point> getWinningLocations() {
		return winningLocations;
	}



}

