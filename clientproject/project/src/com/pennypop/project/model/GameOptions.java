package com.pennypop.project.model;

/**
 * Created by ananth on 11/3/16.
 */
public class GameOptions {
    /* Manages and stores all modes and variable rules of the game.
     */

    // Please change all the Game options here
    private static int rows = 6;
    private static int cols = 7;
    private static int numToWin = 4;
    private static boolean aiMode = false;

    /** SETTERS **/

    /* Sets rows and returns the GameOptions object
     */
    public GameOptions setRows(int rows) {
        this.rows = rows;
        return this;
    }

    /* Sets columns and returns the GameOptions object
     */
    public GameOptions setCols(int cols) {
        this.cols = cols;
        return this;
    }

    /* Sets the number of pieces a player needs in a row to win the game
        and returns the GameOptions object.
     */
    public GameOptions setNumToWin(int numToWin) {
        this.numToWin = numToWin;
        return this;
    }

    /* Sets Artificial intelligence mode and returns the GameOptions
        object
     */
    public GameOptions setAiMode(boolean aiMode) {
        this.aiMode = aiMode;
        return this;
    }


    /** GETTERS **/

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getNumToWin() {
        return numToWin;
    }

    public boolean isAiMode() {
        return aiMode;
    }
}
