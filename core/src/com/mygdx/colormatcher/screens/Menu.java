package com.mygdx.colormatcher.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.colormatcher.game.ColorMatcher;
import com.mygdx.colormatcher.game.GameStateManager;
import com.mygdx.colormatcher.tween.ActorAccessor;

public class Menu extends State{

	private TextButton buttonPlay, buttonExit;
	private Label heading;
	private int[] RGBList;

	public Menu(ColorMatcher colorMatcher) {
		super(colorMatcher);
	}

	private void getNextRGBValues(){
		if(RGBList[0] == 255 && RGBList[1] < 255 && RGBList[2] == 0){
			RGBList[1] +=3;
		} 
		if(RGBList[0] <= 255 && RGBList[1] == 255 && RGBList[2] == 0){
			RGBList[0] -=3;
		}
		if(RGBList[0] == 0 && RGBList[1] == 255 && RGBList[2] < 255){
			RGBList[2] +=3;
		}
		if(RGBList[0] == 0 && RGBList[1] <= 255 && RGBList[2] == 255){
			RGBList[1] -=3;
		}
		if(RGBList[0] < 255 && RGBList[1] == 0 && RGBList[2] == 255){
			RGBList[0] +=3;
		}
		if(RGBList[0] == 255 && RGBList[1] == 0 && RGBList[2] <= 255){
			RGBList[2] -=3;
		}
	}
	
	@Override
	public void update(float delta) {
		getNextRGBValues();
		heading.setColor(RGBList[0]/255f, RGBList[1]/255f, RGBList[2]/255f, heading.getColor().a);
		tweenManager.update(delta);
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void show(){
        viewport = new FitViewport(ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);
		stage = new Stage(viewport);
				
		atlas = new TextureAtlas("ui/button.pack");
		skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
		table = new Table(skin);
		table.setBounds(0, 0, ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		heading = new Label("Game", skin);
		heading.setFontScale(.8f);

		buttonPlay = new TextButton("start", skin);
		buttonPlay.getLabel().setFontScale(.3f);
		buttonExit = new TextButton("exit", skin);
		buttonExit.getLabel().setFontScale(.3f);
		
		buttonPlay.addListener(
			new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					colorMatcher.getSoundManager().playSound(0);
					timeline.kill();
					timeline = Timeline.createSequence();
					timeline.beginParallel()
					.push(Tween.to(heading, ActorAccessor.ALPHA, .5f).target(0))
					.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .5f).target(0))
					.push(Tween.to(buttonExit, ActorAccessor.ALPHA, .5f).target(0))
					.end().start(tweenManager);
					colorMatcher.enterState(GameStateManager.StateEnum.PLAY, 40);
				}
			}
		);
		
		buttonExit.addListener(
			new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					colorMatcher.getSoundManager().playSound(0);
					Gdx.app.exit();
				}
			}
		);
		
		table.add(heading).width(400f).height(200f).pad(50f);
		table.row();
		table.add(buttonPlay).width(290f).height(140f).pad(20f);
		table.row();
		table.add(buttonExit).width(290f).height(140f).pad(20f);
		stage.addActor(table);
		
		RGBList = new int[]{255, 0, 0};
		
		tweenManager = new TweenManager();
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		timeline = Timeline.createSequence();
		timeline.beginSequence()
		.push(Tween.set(heading, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
		.push(Tween.to(heading, ActorAccessor.ALPHA, .5f).target(1))
		.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .5f).target(1))
		.push(Tween.to(buttonExit, ActorAccessor.ALPHA, .5f).target(1))
		.end().start(tweenManager);

	}
	
	@Override
	public void touchDown(int x, int y, int pointer, int button){
		if(stage != null){
			stage.touchDown(x, y, pointer, button);
		}
	}
	
	@Override
	public void touchUp(int x, int y, int pointer, int button){
		if(stage != null){
			stage.touchUp(x, y, pointer, button);
		}
	}

}
