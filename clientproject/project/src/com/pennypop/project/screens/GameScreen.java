package com.pennypop.project.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pennypop.project.model.AI;
import com.pennypop.project.Assets;
import com.pennypop.project.model.GameBoard;
import com.pennypop.project.model.GameOptions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ananth on 11/2/16.
 */
public class GameScreen implements Screen {
    /* Connect 4 Screen where the game is played.
     */

    // Padding between close screen elements in pixels
    private static final int PADDING = 10;

    /* GAME OPTIONS AND BACKEND */
    private GameOptions gameOptions;
    private GameBoard board;
    private AI ai;

    /* GAME BOARD BOUNDS */
    private final float START_X_BOARD;
    private final float START_Y_BOARD;
    private float END_X_BOARD;
    private float END_Y_BOARD;

    private final Stage stage;
    private final SpriteBatch spriteBatch;


    /* MANAGE SCREENS AND ASSETS */
    private AssetManager assetManager;
    private Assets assets;
    private Screen parentScreen;
    private Screen thisScreen;
    private Game game;

    private Button mainButton;
    private Button aiButton;
    private Button clearButton;
    private BitmapFont titleFont;
    private Sound buttonClick;
    private Label bottomNotif;
    private Label sideBar0;
    private Label sideBar1;
    private Label sideBar2;

    /* TILE AND PIECE TEXTURES */
    private Texture redPieceTexture;
    private Texture yellowPieceTexture;
    private Texture tilePieceTexture;
    Set<Image> piecesOnBoard;

    public GameScreen(Game game, Assets assets, Screen screen) {
        spriteBatch = new SpriteBatch();
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);
        this.game = game;
        this.assets = assets;
        this.assetManager = assets.getAssetManager();
        this.thisScreen = this;
        // Include reference to MainScreen so we can switch back to it
        this.parentScreen = screen;

        piecesOnBoard = new HashSet<>();
        getAndSetupAssets();
        gameOptions = new GameOptions();

        // Set board coordinates based on stage size
        float boardWidth = gameOptions.getCols() * tilePieceTexture.getWidth();
        START_X_BOARD = stage.getWidth() / 2 - boardWidth / 2;
        START_Y_BOARD = stage.getHeight() / 8;

        setupGameBoard();
        setupBoardClickListener();
    }


    /* Get all assets from AssetManager and setup event handling
     */
    private void getAndSetupAssets() {
        Image blueBackground = new Image(assetManager.get(Assets.blueBackgroundFilepath, Texture.class));
        blueBackground.setPosition(0, 0);
        stage.addActor(blueBackground);

        mainButton = Assets.assetFilepathToButton(assetManager, Assets.mainButtonFilepath);
        aiButton = Assets.assetFilepathToButton(assetManager, Assets.aiButtonFilepath);
        clearButton = Assets.assetFilepathToButton(assetManager, Assets.clearButtonFilepath);
        setupButtonClickListeners(mainButton);
        setupButtonClickListeners(aiButton);
        setupButtonClickListeners(clearButton);

        redPieceTexture = assetManager.get(Assets.redPieceFilepath, Texture.class);
        yellowPieceTexture = assetManager.get(Assets.yellowPieceFilepath, Texture.class);
        tilePieceTexture = assetManager.get(Assets.tilePieceFilepath, Texture.class);
        titleFont = assetManager.get(Assets.fontFilepath, BitmapFont.class);
        buttonClick = assetManager.get(Assets.buttonClickFilepath, Sound.class);

    }


    /* Handle click events for each type of button
     */
    private void setupButtonClickListeners(Button button) {

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                buttonClick.play();
                if (button.equals(mainButton)) {
                    // Change to MainScreen and reset AI mode
                    gameOptions.setAiMode(false);
                    thisScreen.hide();
                    game.setScreen(parentScreen);

                } else if (button.equals(clearButton) || button.equals(aiButton)) {
                    if (button.equals(aiButton)) {
                        // Flip AI support and restart the game
                        gameOptions.setAiMode(!gameOptions.isAiMode());
                    }
                    // Clear all pieces and restart game
                    board = new GameBoard(gameOptions.getRows(), gameOptions.getCols(), gameOptions.getNumToWin());
                    board.setAiMode(gameOptions.isAiMode());
                    if (gameOptions.isAiMode()) {
                        // Make a new AI
                        ai = new AI(board);
                    }

                    Iterator<Image> piecesOnBoardIter = piecesOnBoard.iterator();
                    while (piecesOnBoardIter.hasNext()) {
                        piecesOnBoardIter.next().remove();
                    }
                    piecesOnBoard.clear();
                    updateBottomNotif("Player " + board.getCurrPlayer() +"'s Turn");
                    updateAINotif();
                }
            }
        });
    }


    /* Handle click events within the board region
     */
    private void setupBoardClickListener() {
        stage.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

            // When mouseclick goes up, add a piece

                if (board.isInWinState() == false) {
                    // If someone hasn't won
                    addAndDrawPiece(x, y);
                    if (board.getWinningPlayer() != 0) {
                        updateBottomNotif("Player " + board.getWinningPlayer() + " wins!");
                    } else if (gameOptions.isAiMode() && board.getCurrPlayer() == board.PLAYER_AI) {
                        performAITurn();
                        if (board.getWinningPlayer() != 0) {
                            updateBottomNotif("Player " + board.getWinningPlayer() + " wins!");
                        }
                    }
                }
            }
        });
    }


    /* Make AI decision, change turns, update board, and draw piece
     */
    private void performAITurn() {
        // If AI mode and it's the AI's turn
        Image piece = new Image(playerToTexture(board.getCurrPlayer()));
        // If AI is on, and it's the AI's turn
        int col = ai.bestColumnDecision();
        int slot = board.getAvailableSlot(col);

        // Calculate x, y coordinates of Piece
        float newX = START_X_BOARD + (col * getWidthPerColumn()) + coordinateOffsetPiece('x');
        float newY = START_Y_BOARD + (slot * getHeightPerRow()) + coordinateOffsetPiece('y');
        piece.setPosition(newX, newY);
        stage.addActor(piece);
        buttonClick.play();
        piecesOnBoard.add(piece);
        board.addPiece(col);

        // Change bottom notification Label
        updateBottomNotif("Player " + board.getCurrPlayer() +"'s Turn");
    }


    /* Draw Board and initialize GamePlay logic elements.
        Add all actors to stage.
     */
    private void setupGameBoard() {
        float x = START_X_BOARD;
        float y = START_Y_BOARD;

        // Draw empty board
        Image test = new Image(tilePieceTexture);
        for (int rows = 0; rows < gameOptions.getRows(); rows++) {
            for (int cols = 0; cols < gameOptions.getCols(); cols++) {
                test = new Image(tilePieceTexture);
                test.setPosition(x, y);
                stage.addActor(test);
                x += test.getWidth();
            }
            END_X_BOARD = x;
            x = START_X_BOARD;
            y += test.getHeight();
        }

        // Send end bounds of board
        END_Y_BOARD = y;

        board = new GameBoard(gameOptions.getRows(), gameOptions.getCols(), gameOptions.getNumToWin());
        board.setAiMode(gameOptions.isAiMode());

        // Position buttons relative to board
        aiButton.setPosition(START_X_BOARD - aiButton.getWidth() - 3 * PADDING,  START_Y_BOARD + getBoardHeight() / 2
                                - aiButton.getHeight() / 2);
        mainButton.setPosition(aiButton.getX(), aiButton.getY() + mainButton.getHeight() + 2 * PADDING);
        clearButton.setPosition(aiButton.getX(), aiButton.getY() - clearButton.getHeight() - 2 * PADDING);

        stage.addActor(mainButton);
        stage.addActor(aiButton);
        stage.addActor(clearButton);

        setupLabels();
    }


    /* Setup labels for entire screen and position them
     */
    private void setupLabels() {
        // Make bottom notification Label
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.RED);
        bottomNotif = new Label("Player " + board.getCurrPlayer() + "'s Turn", titleStyle);
        bottomNotif.setPosition(stage.getWidth() / 2 - bottomNotif.getWidth() / 2,
                START_Y_BOARD - bottomNotif.getHeight() - 2 * PADDING);
        Label topTitle = new Label("Connect Four", titleStyle);
        topTitle.setPosition(stage.getWidth() / 2 - topTitle.getWidth() / 2, START_Y_BOARD + getBoardHeight() +
                3 * PADDING);
        stage.addActor(bottomNotif);
        stage.addActor(topTitle);

        // Labels for side of board
        sideBar0 = new Label("Click the board", titleStyle);
        sideBar1 = new Label("to drop a piece.", titleStyle);
        sideBar2 = new Label("This will be replaced", titleStyle);
        updateAINotif();

        sideBar0.setPosition(END_X_BOARD + 3 * PADDING, END_Y_BOARD - 2 * sideBar0.getHeight());
        sideBar1.setPosition(END_X_BOARD + 3 * PADDING, sideBar0.getY() - sideBar1.getHeight());
        sideBar2.setPosition(END_X_BOARD + 3 * PADDING, sideBar1.getY() - sideBar2.getHeight() - 2 * PADDING);
        stage.addActor(sideBar0);
        stage.addActor(sideBar1);
        stage.addActor(sideBar2);

    }


    /* Change AI label to ON / OFF
     */
    private void updateAINotif() {
        String aiMode;
        if (gameOptions.isAiMode()) {
            aiMode = "ON";
        } else {
            aiMode = "OFF";
        }

        sideBar2.setText("AI Mode: " + aiMode);
    }


    /* Add a piece, given the coordinates of the click
     */
    private void addAndDrawPiece(float x, float y) {
        int col = mapClickToColumn(x, y);
        if (col == -1) {
            // If click x or y is out of bounds, return
            return;
        }

        // Get the available row slot for a piece placement in this column
        // 0 Indexed
        int rowsFromBottom = board.getAvailableSlot(col);
        if (rowsFromBottom >= gameOptions.getRows()) {
            // If column is full, display label and return
            column_full();
            return;
        }

        Image piece = new Image(playerToTexture(board.getCurrPlayer()));
        float xOffsetFromStart = x - START_X_BOARD;
        float yOffsetFromStart = y - START_Y_BOARD;

        // Calculate x, y coordinates of Piece
        float newX = START_X_BOARD + (col * getWidthPerColumn()) + coordinateOffsetPiece('x');
        float newY = START_Y_BOARD + (rowsFromBottom * getHeightPerRow()) + coordinateOffsetPiece('y');
        piece.setPosition(newX, newY);
        stage.addActor(piece);
        buttonClick.play();
        piecesOnBoard.add(piece);
        board.addPiece(col);
        // Change bottom notification Label
        updateBottomNotif("Player " + board.getCurrPlayer() +"'s Turn");
    }


    /* Called when user attempts to add to a full column
     */
    private void column_full() {
        updateBottomNotif("This column is full!");
    }


    /* Update Bottom notifcation bar with inputted text
     */
    private void updateBottomNotif(CharSequence charsequence) {
        bottomNotif.setText(charsequence);
        bottomNotif.setPosition((stage.getWidth() / 2) - (bottomNotif.getWidth() / 2), bottomNotif.getY());
    }


    /* Alternates textures for each player (giving them
        different colored tiles
      */
    private Texture playerToTexture(int player) {
        if (player % 2 == 0) {
            // If player 2, return Yellow tile
            return yellowPieceTexture;
        } else {
            return redPieceTexture;
        }
    }


    /* Return the offset to the x coordinate of a Piece
        This distortion is caused by the tiles being larger
        than the pieces.
     */
    private float coordinateOffsetPiece(char xOrY) {
        if (xOrY == 'x') {
            float tileWidth = tilePieceTexture.getWidth();
            float pieceWidth = redPieceTexture.getWidth();
            return (tileWidth - pieceWidth) / 2;
        } else {
            float tileHeight = tilePieceTexture.getHeight();
            float pieceHeight = redPieceTexture.getHeight();
            return (tileHeight - pieceHeight) / 2;
        }
    }

    /* Return width, height, width/column,
        height/row of board in pixels
     */
    private float getBoardWidth() {
        return END_X_BOARD - START_X_BOARD;
    }

    private float getBoardHeight() {
        return END_Y_BOARD - START_Y_BOARD;
    }

    private float getWidthPerColumn() {
        return getBoardWidth() / gameOptions.getCols();
    }

    private float getHeightPerRow() {
        return getBoardHeight() / gameOptions.getRows();
    }


    private int mapClickToColumn(float x, float y) {

        float xOffsetFromStart = x - START_X_BOARD;
        float yOffsetFromStart = y - START_Y_BOARD;

        if (yOffsetFromStart < 0) {
            // If y click coordinate is out of bounds, return -1
            return -1;
        }

        if (xOffsetFromStart < 0 || xOffsetFromStart > getBoardWidth()) {
            // If x click is out of bounds, return -1
            return -1;
        }

        int col = (int) (xOffsetFromStart / getWidthPerColumn());
        return col;
    }


    @Override
    public void dispose() {
        spriteBatch.dispose();
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause() {
        // Irrelevant on desktop, ignore this
    }

    @Override
    public void resume() {
        // Irrelevant on desktop, ignore this
    }
}
