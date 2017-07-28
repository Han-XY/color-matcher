package com.mygdx.colormatcher.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.colormatcher.game.ColorMatcher;

public abstract class State implements Screen{

	protected Stage stage;
	protected Skin skin;
	protected TextureAtlas atlas;
	protected Table table;
	protected TweenManager tweenManager;
	protected SpriteBatch spriteBatch;
	protected boolean transitioning;
	protected Timeline timeline;
	public static Viewport viewport;
	protected ColorMatcher colorMatcher;

	public State(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}
	
	public abstract void update(float delta);

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
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
		stage.dispose();
		skin.dispose();
		atlas.dispose();
	}
	
	public void touchDown(int x, int y, int pointer, int button){
		
	}
	public void touchUp(int x, int y, int pointer, int button){
		
	}
	public void touchDragged(int x, int y, int pointer){
		
	}
	public void mouseMoved(int x, int y){
		
	}
	
	public void setTransitioning(boolean b){
		transitioning = b;
	}

}
