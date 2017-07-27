package com.mygdx.colormatcher.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.colormatcher.screens.Play;
import com.mygdx.colormatcher.screens.State;
import com.mygdx.colormatcher.utils.InputHandler;
import com.mygdx.colormatcher.utils.SoundManager;

/** Core game module. **/
public class ColorMatcher extends Game {

	private GameStateManager stateManager;

	/* Resources */
	private BitmapFont fontWhite, fontBlack;
	private SoundManager soundManager;

	/* Reference dimensions used to create stage properly */
	public static final int REF_WIDTH = 540;
	public static final int REF_HEIGHT = 960;
	
	@Override
	public void create () {
		this.loadResources();
		Gdx.input.setInputProcessor(new InputHandler(this));

		this.stateManager = new GameStateManager(this);
		this.stateManager.enterState(GameStateManager.StateEnum.MENU);
	}

	@Override
	public void dispose(){
		super.dispose();
		this.fontBlack.dispose();
		this.fontWhite.dispose();
		this.soundManager.dispose();
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

		super.render();

		this.update(Gdx.graphics.getDeltaTime());
	}
	
	private void update(float delta){
		this.stateManager.update(delta);
	}
	
	public void enterState(GameStateManager.StateEnum stateEnum, int transitionTicks){
		this.stateManager.transitionTo(stateEnum, transitionTicks);
	}

	private void loadResources() {
		this.fontWhite = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
		this.fontBlack = new BitmapFont(Gdx.files.internal("font/black.fnt"), false);
		this.soundManager = new SoundManager();
	}

	public State getCurrentState() {
		return this.stateManager.getCurrentState();
	}

	public Play getPlayState() {
		return (Play) this.stateManager.getState(GameStateManager.StateEnum.PLAY);
	}

	public SoundManager getSoundManager() {
		return this.soundManager;
	}

	public BitmapFont getFontWhite() {
		return this.fontWhite;
	}

	public BitmapFont getFontBlack() {
		return this.fontBlack;
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);
	}
	
	@Override
	public void pause(){
		super.pause();
	}
	
	@Override
	public void resume(){
		super.resume();
	}
	
	
} 
