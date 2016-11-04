package com.pennypop.project.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ananth on 11/3/16.
 */
public class GameBoard {
    /* Handles the backend Game logic and board without the GUI
     */
    public static int PLAYER1 = 1;
    public static int PLAYER2 = 2;
    public static int PLAYER_AI = 2;

    private int currPlayer;
    private int rows;
    private int cols;

    // Last placed piece
    private int lastRow;
    private int lastCol;

    // Number of pieces a player needs in a row to win
    private int numToWin;

    // True if a player has won the game after his/her move
    private boolean inWinState;
    private int winningPlayer;

    private boolean aiMode;

    private int[][] board;

    /* Maps column number to the next available slot in that column.
        Note: The available slot numbers start at 0 for each column. This
        is NOT EQUIVALENT to the next available row index for this column.
        See getAvailableRow(int column) for the correct index.
        */
    private int[] columnToSlot;

    public GameBoard(int rows, int cols, int numToWin) {
        this.rows = rows;
        this.cols = cols;
        this.numToWin = numToWin;
        this.board = new int[rows][cols];
        this.columnToSlot = new int[cols];

        // It is first Player1's turn
        this.currPlayer = PLAYER1;
        this.inWinState = false;
        this.aiMode = false;
    }


    /*  Changes the turn to the other player
            */
    public void changeTurns() {
        this.currPlayer = 3 - this.currPlayer;
    }


    /* Adds a piece to the board at column Index col for the
        currentPlayer's turn.

        If a piece was added successfully, change turns. Otherwise,
        alert the player and do not change turns.
     */
    public void addPiece(int col) {
        if (inWinState) {
            // If a player has won, do not add
            return;
        }
        if (col >= this.cols || col < 0) {
            // If col is out of bounds, alert the player and return
            System.out.println("Column out of bounds! Try again.");
            return;
        }

        int rowIndex = getAvailableRow(col);
        if (rowIndex == -1) {
            // If col is full, alert the player and return
            System.out.println("Column is full! Try again.");
        } else {
            // Save this piece's information
            this.lastRow = rowIndex;
            this.lastCol = col;
            board[rowIndex][col] = getCurrPlayer();
            columnToSlot[col] += 1;

            winningPlayer = getWinningPlayer();
            if (winningPlayer != 0) {
                // If a player has won, change the game state
                inWinState = true;
                System.out.println(winningPlayer + " has won!!");
            }
            // Switch turns to the other player
            changeTurns();
        }
    }


    /* Check whether the Player that has won during this move.
        Returns 0 if no win yet.
       */
    public int getWinningPlayer() {
        // Generate winning patterns for each player
        String player1WinningPattern = "";
        String player2WinningPattern = "";
        for (int i = 0; i < numToWin; i++) {
            player1WinningPattern += String.valueOf(PLAYER1);
            player2WinningPattern += String.valueOf(PLAYER2);
        }

        Set<String> horizontals = getHorizontals();
        Set<String> verticals = getVerticals();
        Set<String> forwardDiagonals = getForwardDiagonals();
        Set<String> backwardDiagonals = getBackwardDiagonals();

        if (horizontals.contains(player1WinningPattern)
                || verticals.contains(player1WinningPattern)
                || backwardDiagonals.contains(player1WinningPattern)
                || forwardDiagonals.contains(player1WinningPattern)) {
            // Player 1 wins

            return PLAYER1;
        } else if (horizontals.contains(player2WinningPattern)
                || verticals.contains(player2WinningPattern)
                || backwardDiagonals.contains(player2WinningPattern)
                || forwardDiagonals.contains(player2WinningPattern)) {
            // Player 2 wins

            return PLAYER2;

        } else {
            return 0;
        }
    }


    /* Maps the given column (and next available slot number)
        to the next available row index in that column.
        Returns -1 if no row is available.
        */
    public int getAvailableRow(int column) {
        int rowIndex = this.rows - columnToSlot[column] - 1;
        if (rowIndex >= this.rows) {
            // If the column is full
            return -1;
        } else {
            return rowIndex;
        }
    }


    /* Maps the given column index (and next available slot number)
        to the next available slot in that column.
        Returns -1 if column is invalid.
        */
    public int getAvailableSlot(int column) {
        if (column >= this.cols  || column < 0) {
            // If the column is full,
            return -1;
        } else {
            return columnToSlot[column];
        }
    }


    /* Returns a String set containing all horizontal patters of size numToWin
            containing the last placed piece.
         */
    public Set<String> getHorizontals() {
        Set<String> allHorizontals = new HashSet<>();
        for (int col = lastCol - numToWin + 1; col <= lastCol; col++) {
            // If col is out of bounds
            if (col < 0 || col >= this.cols) {
                continue;
            }

            String pattern = "";
            for (int offset = 0; offset < numToWin; offset++) {
                if (col + offset >= this.cols) {
                    continue;
                }
                pattern += board[lastRow][col + offset];
            }

            // If the pattern is long enough to win, add it
            if (pattern.length() == numToWin) {
                allHorizontals.add(pattern);
            }
        }
        return allHorizontals;
    }


    /* Returns a String set containing all vertical patters of size numToWin
        containing the last placed piece.

        (Note: there will only be a single
        string in this set, as a vertical win can only happen in one way.)
     */
    public Set<String> getVerticals() {
        Set<String> allVerticals = new HashSet<>();

        String pattern = "";
        for (int row = lastRow; row <= lastRow + numToWin - 1; row++) {
            // If row is out of bounds
            if (row < 0 || row >= this.rows) {
                continue;
            }
            pattern += board[row][lastCol];
        }
        allVerticals.add(pattern);
        return allVerticals;
    }


    /* Returns a String set containing all forward diagonal patters of size numToWin
        containing the last placed piece. A Forward diagonal looks like \ on the board.
     */
    public Set<String> getForwardDiagonals() {
        Set<String> allForwardDiagonals = new HashSet<>();

        for (int col = lastCol - numToWin + 1; col <= lastCol; col ++) {
            // If col is out of bounds
            if (col < 0 || col >= this.cols) {
                continue;
            }

            String pattern = "";
            // Add offset to the row and the column to increment them equally, accessing a diagonal in the array
            for (int offset = 0; offset < numToWin; offset++) {

                // Shift the row by the proper amount ( the difference between col and the lastCol
                // where the last piece was placed.
                int rowOffset = col - this.lastCol;

                if (col + offset >= this.cols || lastRow + rowOffset + offset < 0 || lastRow + rowOffset + offset >= this.rows) {
                    continue;
                }
                pattern += board[lastRow + rowOffset + offset][col + offset];
            }

            // If the pattern is long enough to win, add it
            if (pattern.length() == numToWin) {
                allForwardDiagonals.add(pattern);
            }
        }
        return allForwardDiagonals;
    }


    /* Returns a String set containing all backward diagonal patters of size numToWin
        containing the last placed piece. A Forward diagonal looks like / on the board.
     */
    public Set<String> getBackwardDiagonals() {
        Set<String> allBackwardDiagonals = new HashSet<>();

        for (int col = lastCol - numToWin + 1; col <= lastCol; col ++) {
            // If col is out of bounds
            if (col < 0 || col >= this.cols) {
                continue;
            }

            String pattern = "";
            // Add offset to the row and the column to increment them equally, accessing a diagonal in the array
            for (int offset = 0; offset < numToWin; offset++) {

                // Shift the row by the proper amount ( the difference between col and the lastCol
                // where the last piece was placed.
                int rowOffset = this.lastCol - col;

                if (col + offset >= this.cols || lastRow + rowOffset - offset < 0 || lastRow + rowOffset - offset >= this.rows) {
                    continue;
                }
                pattern += board[lastRow + rowOffset - offset][col + offset];
            }

            // If the pattern is long enough to win, add it
            if (pattern.length() == numToWin) {
                allBackwardDiagonals.add(pattern);
            }
        }
        return allBackwardDiagonals;
    }


    /* Change whether one player is AI
     */
    public void setAiMode(boolean aiMode) {
        this.aiMode = aiMode;
    }


    /* Returns: 1 if it's Player 1's turn
                2 if its Player 2's turn
        */
    public int getCurrPlayer() {
        return this.currPlayer;
    }


    /* Returns whether or the game has been won
     */
    public boolean isInWinState() {
        return inWinState;
    }


    /* Get column of last played piece
     */
    public int getLastCol() {
        return lastCol;
    }


    /* Get row of last played piece
     */
    public int getLastRow() {
        return lastRow;
    }


    public int getRows() {
        return rows;
    }


    public int getCols() {
        return cols;
    }


    /* Prints the board Array.
      Used for debugging purposes.
      */
    public void printBoard() {
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
