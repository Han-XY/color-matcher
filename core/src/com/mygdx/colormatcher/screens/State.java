package com.mygdx.colormatcher.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.colormatcher.game.ColorMatcher;

public abstract class State implements Screen{

	protected Stage stage;
	protected Skin skin;
	protected TextureAtlas atlas;
	protected Table table;
	protected TweenManager tweenManager;
	protected SpriteBatch spriteBatch;
	protected boolean isTransitioning;
	protected Timeline timeline;
	protected ColorMatcher colorMatcher;

	protected boolean canDrawUI;

	public State(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}
	
	public abstract void update(float delta);

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		if(this.canDrawUI) {
			this.stage.act(delta);
			this.stage.draw();
		}
	}

	@Override
	public void resize (int width, int height) {

	}
	
	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	public void onEnter(){
		
	}
	
	public void onExit(){
		
	}

	@Override
	public void dispose() {
		this.stage.dispose();
		this.skin.dispose();
		this.atlas.dispose();
	}
	
	public void touchDown(int x, int y, int pointer, int button){
		
	}

	public void touchUp(int x, int y, int pointer, int button){
		
	}

	public void touchDragged(int x, int y, int pointer){
		
	}
	public void mouseMoved(int x, int y){
		
	}
	
	public void setTransitioning(boolean isTransitioning){
		this.isTransitioning = isTransitioning;
	}

	/**
	 * Begins a delay to prevent the UI from being drawn until the delay ends.
	 */
	protected void applyUiDelay() {
		this.canDrawUI = false;

		Timer.schedule(new Timer.Task(){
			@Override
			public void run(){
				canDrawUI = true;
			}
		}, .05f);
	}

}
