package com.pennypop.project;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by ananth on 11/2/16.
 */
public class Assets {
    /* Manages all assets for the multiple game screens
     */

    /* MAINSCREEN ASSET FILEPATHS */
    public static final String sfxButtonFilepath = "data/sfxButton.png";
    public static final String apiButtonFilepath = "data/apiButton.png";
    public static final String gameButtonFilepath = "data/gameButton.png";
    public static final String buttonClickFilepath = "data/button_click.wav";
    public static final String fontFilepath = "data/font.fnt";
    public static final String blackScreenFilepath = "data/blackScreen.png";
    public static final String blueBackgroundFilepath = "data/blueBackground.jpg";

    /* GAMESCREEN ASSET FILEPATHS */
    public static final String yellowPieceFilepath = "data/yellow.png";
    public static final String redPieceFilepath = "data/red.png";
    public static final String tilePieceFilepath = "data/tileTransparent.png";

    public static final String clearButtonFilepath = "data/clearButton2.png";
    public static final String aiButtonFilepath = "data/aiButton2.png";
    public static final String mainButtonFilepath = "data/mainButton2.png";



    private AssetManager assetManager;

    public Assets() {
        this.assetManager = new AssetManager();
    }

    /* Returns assetManager with all previous loaded assets
     */
    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    /* Loads all necessary assets for MainScreen into assetManager
    */
    public void loadMainAssets() {
        assetManager.load(sfxButtonFilepath, Texture.class);
        assetManager.load(apiButtonFilepath, Texture.class);
        assetManager.load(gameButtonFilepath, Texture.class);
        assetManager.load(buttonClickFilepath, Sound.class);
        assetManager.load(fontFilepath, BitmapFont.class);
        assetManager.load(blackScreenFilepath, Texture.class);
        assetManager.load(blueBackgroundFilepath, Texture.class);
        assetManager.finishLoading();
    }

    /* Loads all necessary assets for MainScreen into assetManager
    */
    public void loadGameAssets() {
        assetManager.load(yellowPieceFilepath, Texture.class);
        assetManager.load(redPieceFilepath, Texture.class);
        assetManager.load(tilePieceFilepath, Texture.class);
        assetManager.load(blueBackgroundFilepath, Texture.class);
        assetManager.load(mainButtonFilepath, Texture.class);
        assetManager.load(clearButtonFilepath, Texture.class);
        assetManager.load(aiButtonFilepath, Texture.class);
        assetManager.finishLoading();
    }

    /* Returns Button with Texture at thi Asset filepath
 */
    public static Button assetFilepathToButton(AssetManager assetManager, String filepath) {
        Texture buttonTexture = assetManager.get(filepath, Texture.class);
        return new Button(new Image(buttonTexture).getDrawable());
    }


}
