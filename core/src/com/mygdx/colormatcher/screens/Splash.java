package com.mygdx.colormatcher.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.colormatcher.game.ColorMatcher;
import com.mygdx.colormatcher.game.GameStateManager;
import com.mygdx.colormatcher.tween.SpriteAccessor;

public class Splash extends State{
	
	private Sprite splash;

	public Splash(ColorMatcher colorMatcher) {
		super(colorMatcher);
	}
	
	@Override
	public void show() {
		spriteBatch = new SpriteBatch();
		
		tweenManager = new TweenManager();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		
		Texture texture = new Texture("img/splash.png");
		splash = new Sprite(texture);
		splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		Tween.set(splash, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.to(splash, SpriteAccessor.ALPHA, .5f).target(1).repeatYoyo(1, .5f).setCallback(
				new TweenCallback(){

					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						colorMatcher.enterState(GameStateManager.StateEnum.MENU, 0);
					}
					
				}
		).start(tweenManager);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				
		spriteBatch.begin();
		splash.draw(spriteBatch);
		spriteBatch.end();
	}
	

	@Override
	public void update(float delta) {
		tweenManager.update(delta);		
	}

	@Override
	public void resize(int width, int height) {
		
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

	@Override
	public void dispose() {
		spriteBatch.dispose();
		splash.getTexture().dispose();
	}


}









