package com.pennypop.project;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.pennypop.project.screens.MainScreen;

/**
 * The {@link ApplicationListener} for this project, create(), resize() and
 * render() are the only methods that are relevant
 * 
 * @author Richard Taylor
 * */
public class ProjectApplication extends Game implements ApplicationListener {

	private Screen screen;
	private Game game;
	private Assets assets;

	public static void main(String[] args) {
		new LwjglApplication(new ProjectApplication(), "PennyPop", 1280, 720,
				true);
	}

	public ProjectApplication() {
		game = this;
		assets = new Assets();
	}

	@Override
	public void create() {
		// Load assets for and MainScreen and show screen
		assets.loadMainAssets();
		screen = new MainScreen(game, assets);
		game.setScreen(screen);

		// Load assets for GameScreen
		assets.loadGameAssets();
	}

	@Override
	public void dispose() {
		game.getScreen().hide();
		game.getScreen().dispose();
	}

	@Override
	public void pause() {
		game.getScreen().pause();
	}

	@Override
	public void render() {
		clearWhite();
		game.getScreen().render(Gdx.graphics.getDeltaTime());
		super.render();
	}

	/** Clears the screen with a white color */
	private void clearWhite() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height) {
		game.getScreen().resize(width, height);
	}

	@Override
	public void resume() {
		game.getScreen().resume();
	}
}
