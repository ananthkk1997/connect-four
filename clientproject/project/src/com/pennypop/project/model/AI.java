package com.pennypop.project.model;

import com.pennypop.project.model.GameBoard;

/**
 * Created by ananth on 11/4/16.
 */
public class AI {
    /* A simple defense-playing Artificial Intelligence to play against.
        AI is default set to Player 2.
     */

    private GameBoard board;

    public AI(GameBoard board) {
        this.board = board;
    }

    /* Returns the AI's decision of which column to
        place a piece.
        Strategy: block the opponent by decreasing their ability to
                  create chains of pieces next to each other.
     */
    public int bestColumnDecision() {

        // 3 possible moves to block the opponent's last played piece
        int[] possibleColumns = new int[3];
        for(int offset = -1; offset < 2; offset++) {
            possibleColumns[offset + 1] = board.getLastCol() + offset;
        }

        // Get random index from (0, 2) inclusive
        int colIndex = (int) Math.floor(Math.random() * 3);

        // Get a column within the bounds and that's not full
        while (possibleColumns[colIndex] < 0 || possibleColumns[colIndex] >= board.getCols()
                || board.getAvailableSlot(possibleColumns[colIndex]) >= board.getRows()) {

            colIndex = (int) Math.floor(Math.random() * 3);
        }

        return possibleColumns[colIndex];
    }
}
