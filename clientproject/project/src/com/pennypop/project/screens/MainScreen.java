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
import com.pennypop.project.Assets;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;


/**
 * This is where you screen code will go, any UI should be in here
 *
 * @author Richard Taylor
 */
public class MainScreen implements Screen {

	private static final String CITY_NAME = "San Francisco";
	private static final String COUNTRY = "US";

	// Padding between close screen elements in pixels
	private static final int PADDING = 10;
	private final Stage stage;
	private final SpriteBatch spriteBatch;

	private Button sfxButton;
	private Button apiButton;
	private Button gameButton;
	private Sound buttonClick;
	private BitmapFont titleFont;

	private Game game;
	private Assets assets;
	private Screen thisScreen;
	private AssetManager assetManager;

	public MainScreen(Game game, Assets assets) {
		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);

		this.game = game;
		this.assets = assets;
		assetManager = assets.getAssetManager();
		thisScreen = this;

		getAndSetupAssets();
	}


	/* Gets and positions all assets for this screen
	 */
	private void getAndSetupAssets() {
		// Get all assets
		Image blueBackground = new Image(assetManager.get(Assets.blueBackgroundFilepath, Texture.class));
		Image blackScreen = new Image(assetManager.get(Assets.blackScreenFilepath, Texture.class));
		sfxButton = Assets.assetFilepathToButton(assetManager, Assets.sfxButtonFilepath);
		apiButton = Assets.assetFilepathToButton(assetManager, Assets.apiButtonFilepath);
		gameButton = Assets.assetFilepathToButton(assetManager, Assets.gameButtonFilepath);
		buttonClick = assetManager.get(Assets.buttonClickFilepath, Sound.class);

		titleFont = assetManager.get(Assets.fontFilepath, BitmapFont.class);
		Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.RED);
		Label pennyPopTitle = new Label("PennyPop", titleStyle);
		pennyPopTitle.setPosition((stage.getWidth() / 4) - pennyPopTitle.getWidth() / 2 ,
				(stage.getHeight() / 2) + apiButton.getHeight() / 2 + ((float) 2.5)*PADDING);

		setupButtonListener(sfxButton);
		setupButtonListener(apiButton);
		setupButtonListener(gameButton);

		// Position elements on stage
		blueBackground.setPosition(0, 0);
		blackScreen.setPosition(2 * (stage.getWidth() / 3) - (blackScreen.getWidth() / 2), ( stage.getHeight() / 2) -
				(blackScreen.getHeight() / 2));
		apiButton.setPosition((stage.getWidth() / 4 ) - (apiButton.getWidth() / 2),
				(stage.getHeight() / 2 ) - (apiButton.getHeight() / 2));
		gameButton.setPosition((stage.getWidth() / 4 ) + (apiButton.getWidth() / 2) + 2*PADDING,
				(stage.getHeight() / 2 ) - (gameButton.getHeight() / 2));
		sfxButton.setPosition((stage.getWidth() / 4 ) - (apiButton.getWidth() / 2) - sfxButton.getWidth() - 2*PADDING,
				(stage.getHeight() / 2 ) - (sfxButton.getHeight() / 2));

		// Add all Actors
		stage.addActor(blueBackground);
		stage.addActor(blackScreen);
		stage.addActor(pennyPopTitle);
		stage.addActor(sfxButton);
		stage.addActor(apiButton);
		stage.addActor(gameButton);
	}


	/* Set listeners for the MainScreen buttons
	*/
	private void setupButtonListener(final Button button) {
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent e, float x, float y) {
				buttonClick.play();
				if (button.equals(sfxButton)) {
					// Do nothing else

				} else if (button.equals(apiButton)) {
					// Show weather
					showWeatherOnStage();

				} else if (button.equals(gameButton)) {
					// Change to GameScreen and hide this one
					GameScreen gameScreen = new GameScreen(game, assets, thisScreen);
					thisScreen.hide();
					game.setScreen(gameScreen);
				}
			}
		});
	}


	/* Calls OpenWeatherMap API, parses JSON obejct,
		and positions Label Actors on Stage
	 */
	private void showWeatherOnStage() {
		String sUrl = "http://api.openweathermap.org/data/2.5/weather?q=" +
				CITY_NAME.replace(" ", "%20") + "," + COUNTRY + "&appid=2e32d2b4b825464ec8c677a49531e9ae";

		// Call OpenWeatherMap API using a URLConnection
		try {
			URL url = new URL(sUrl);
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();

			JSONObject jsonWeather = convertInputStreamToJSONObject(request.getInputStream());

			// See method description for the JSON Object structure
			String weatherDesc = jsonWeather.getJSONArray("weather")
					.getJSONObject(0)
					.getString("description");
			weatherDesc = weatherDesc.substring(0, 1).toUpperCase() +
					weatherDesc.substring(1);

			double tempFahrenheit = kelvinToFahrenheit(jsonWeather.getJSONObject("main")
					.getDouble("temp"));
			DecimalFormat df = new DecimalFormat("#.#");
			tempFahrenheit = Double.valueOf(df.format(tempFahrenheit));
			double windSpeedMPH = jsonWeather.getJSONObject("wind")
					.getDouble("speed");

			Label currWeatherTitle = new Label("Current Weather", new Label.LabelStyle(titleFont, Color.GREEN));
			Label cityTitle = new Label(CITY_NAME, new Label.LabelStyle(titleFont, Color.GREEN));
			Label weatherDescTitle = new Label(weatherDesc, new Label.LabelStyle(titleFont, Color.GREEN));

			// Used a FreeTypeFontGenerator to resize the given font
			BitmapFont tempTitleFont = assetManager.get(Assets.fontFilepath);
			Label tempTitle = new Label( tempFahrenheit + " degrees, " + windSpeedMPH + " MPH wind",
					new Label.LabelStyle(tempTitleFont, Color.GREEN));

			float offsetFactor = (float) 2;
			tempTitle.setPosition(2 * stage.getWidth() / 3 - tempTitle.getWidth() / 2,
					4 * stage.getHeight() / 7 - tempTitle.getHeight() - offsetFactor * PADDING);

			weatherDescTitle.setPosition(2 * stage.getWidth() / 3 - weatherDescTitle.getWidth() / 2,
					tempTitle.getY() + tempTitle.getHeight());

			cityTitle.setPosition(2 * stage.getWidth() / 3 - cityTitle.getWidth() / 2,
					weatherDescTitle.getY() + weatherDescTitle.getHeight() + 2 * PADDING);

			currWeatherTitle.setPosition(2 * stage.getWidth() / 3 - currWeatherTitle.getWidth() / 2,
					 cityTitle.getY() + cityTitle.getHeight());

			stage.addActor(currWeatherTitle);
			stage.addActor(cityTitle);
			stage.addActor(weatherDescTitle);
			stage.addActor(tempTitle);

		} catch(Exception exception) {
			System.out.println("Wrong URL!" + exception.getMessage());
		}
	}


	/*	Converts Kelvin to Fahrenheit
	 */
	private static double kelvinToFahrenheit(double kelvin) {
		double celsius = kelvin - 273.15;
		double fahrenheit = ((celsius * 9) / 5) + 32;
		return fahrenheit;
	}


	/* Takes in an inputStream and creates a String version.
		Returns: JSONObject of the resulting String

		Made with the help of StackOverflow:
		http://stackoverflow.com/questions/21753953/convert-input-stream-to-json-to-display
	 */
	private static JSONObject convertInputStreamToJSONObject(InputStream inputStream)
			throws Exception {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return new JSONObject(result);
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
