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

	/**
	 * Gets the next RGB values for the heading.
	 */
	private void getNextRGBValues(){
		if(this.RGBList[0] == 255 && this.RGBList[1] < 255 && this.RGBList[2] == 0){
			this.RGBList[1] +=3;
		} 
		if(this.RGBList[0] <= 255 && this.RGBList[1] == 255 && this.RGBList[2] == 0){
			this.RGBList[0] -=3;
		}
		if(this.RGBList[0] == 0 && this.RGBList[1] == 255 && this.RGBList[2] < 255){
			this.RGBList[2] +=3;
		}
		if(this.RGBList[0] == 0 && this.RGBList[1] <= 255 && this.RGBList[2] == 255){
			this.RGBList[1] -=3;
		}
		if(this.RGBList[0] < 255 && this.RGBList[1] == 0 && this.RGBList[2] == 255){
			this.RGBList[0] +=3;
		}
		if(this.RGBList[0] == 255 && this.RGBList[1] == 0 && this.RGBList[2] <= 255){
			this.RGBList[2] -=3;
		}
	}
	
	@Override
	public void update(float delta) {
		this.getNextRGBValues();
		this.heading.setColor(this.RGBList[0]/255f, this.RGBList[1]/255f, this.RGBList[2]/255f, this.heading.getColor().a);
		this.tweenManager.update(delta);
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		this.stage.act(delta);
		this.stage.draw();
	}
	
	@Override
	public void show(){
		FitViewport viewport = new FitViewport(ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);
		this.stage = new Stage(viewport);

		this.atlas = new TextureAtlas("ui/button.pack");
		this.skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
		this.table = new Table(skin);
		this.table.setBounds(0, 0, ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);

		this.heading = new Label("Color!", skin);
		this.heading.setFontScale(.8f);

		this.buttonPlay = new TextButton("start", skin);
		this.buttonPlay.getLabel().setFontScale(.3f);
		this.buttonExit = new TextButton("exit", skin);
		this.buttonExit.getLabel().setFontScale(.3f);

		this.buttonPlay.addListener(
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

		this.buttonExit.addListener(
			new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					colorMatcher.getSoundManager().playSound(0);
					Gdx.app.exit();
				}
			}
		);

		this.table.add(this.heading).width(400f).height(200f).pad(50f);
		this.table.row();
		this.table.add(this.buttonPlay).width(290f).height(140f).pad(20f);
		this.table.row();
		this.table.add(this.buttonExit).width(290f).height(140f).pad(20f);
		this.stage.addActor(this.table);
		
		this.RGBList = new int[]{255, 0, 0};

		this.tweenManager = new TweenManager();
		
		Tween.registerAccessor(Actor.class, new ActorAccessor());

		this.timeline = Timeline.createSequence();
		this.timeline.beginSequence()
		.push(Tween.set(this.heading, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(this.buttonPlay, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(this.buttonExit, ActorAccessor.ALPHA).target(0))
		.push(Tween.to(this.heading, ActorAccessor.ALPHA, .5f).target(1))
		.push(Tween.to(this.buttonPlay, ActorAccessor.ALPHA, .5f).target(1))
		.push(Tween.to(this.buttonExit, ActorAccessor.ALPHA, .5f).target(1))
		.end().start(this.tweenManager);

	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void touchDown(int x, int y, int pointer, int button){
		if(this.stage != null){
			this.stage.touchDown(x, y, pointer, button);
		}
	}
	
	@Override
	public void touchUp(int x, int y, int pointer, int button){
		if(this.stage != null){
			this.stage.touchUp(x, y, pointer, button);
		}
	}

}
