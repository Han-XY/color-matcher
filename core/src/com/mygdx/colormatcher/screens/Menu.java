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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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

	@Override
	public void show(){
		FitViewport viewport = new FitViewport(ColorMatcher.REF_WIDTH * 2, ColorMatcher.REF_HEIGHT * 2);
		this.stage = new Stage(viewport);

		this.atlas = new TextureAtlas("ui/colorUi.pack");
		this.skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);
		this.table = new Table(skin);
		this.table.setBounds(0, 0, ColorMatcher.REF_WIDTH * 2, ColorMatcher.REF_HEIGHT * 2);

		this.heading = new Label("Color!", skin);
		this.heading.setFontScale(1.6f);

		this.buttonPlay = new TextButton("start", skin, "startButton");
		this.buttonPlay.getLabel().setFontScale(.6f);
		this.buttonExit = new TextButton("exit", skin, "exitButton");
		this.buttonExit.getLabel().setFontScale(.6f);

		this.buttonPlay.setTouchable(Touchable.disabled);
		this.buttonExit.setTouchable(Touchable.disabled);

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
						buttonPlay.setTouchable(Touchable.disabled);

						Timer.schedule(new Timer.Task(){
							@Override
							public void run(){
								colorMatcher.enterState(GameStateManager.StateEnum.PLAY, 40);
							}
						}, .6f);
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

		this.table.add(this.heading).width(800f).height(400f).pad(100f);
		this.table.row();
		this.table.add(this.buttonPlay).width(500f).height(260f).pad(40f);
		this.table.row();
		this.table.add(this.buttonExit).width(500f).height(260f).pad(40f);

		this.RGBList = new int[]{255, 0, 0};

		this.tweenManager = new TweenManager();

		Tween.registerAccessor(Actor.class, new ActorAccessor());

		this.timeline = Timeline.createSequence();

		this.timeline.beginSequence()
				.push(Tween.set(this.heading, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(this.buttonPlay, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(this.buttonExit, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(this.heading, ActorAccessor.ALPHA, 1.8f).target(1))
				.push(Tween.to(this.buttonPlay, ActorAccessor.ALPHA, .5f).target(1))
				.push(Tween.to(this.buttonExit, ActorAccessor.ALPHA, .5f).target(1))
				.end().start(this.tweenManager);

		this.applyUiDelay();

		Timer.schedule(new Timer.Task(){
			@Override
			public void run(){
				buttonPlay.setTouchable(Touchable.enabled);
			}
		}, 2f);

		Timer.schedule(new Timer.Task(){
			@Override
			public void run(){
				buttonExit.setTouchable(Touchable.enabled);
			}
		}, 2.5f);


		this.stage.addActor(this.table);
	}
	
	@Override
	public void update(float delta) {
		this.getNextRGBValues();
		this.heading.setColor(this.RGBList[0]/255f, this.RGBList[1]/255f, this.RGBList[2]/255f, this.heading.getColor().a);
		this.tweenManager.update(delta);
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(40 / 255f, 36 / 255f, 36 / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height, true);
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
