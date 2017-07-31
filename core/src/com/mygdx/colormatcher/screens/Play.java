package com.mygdx.colormatcher.screens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.colormatcher.game.ColorMatcher;
import com.mygdx.colormatcher.game.GameStateManager;
import com.mygdx.colormatcher.game.QuizManager;
import com.mygdx.colormatcher.gameobject.*;
import com.mygdx.colormatcher.tween.ActorAccessor;


/** The game-state for actual gameplay. **/
public class Play extends State{

	/* These are used for the game-world, and are measured in metres */
	private OrthographicCamera gameCamera;
	private Viewport gameViewport;

	/* Used for rendering text at an appropriate resolution */
	private Viewport referenceUnitViewport;

	private World world;

	/* Box2D physics */
	private final float TIMESTEP = 1/60f;
	private final int VELOCITY_ITERATIONS = 8;
	private final int POSITION_ITERATIONS = 3;
	private final float WORLD_WIDTH = 5.4f;
	private final float WORLD_HEIGHT = 9.6f;

	/* Stores scores etc. */
	private QuizManager quizManager;
	
	private ArrayList<Ball> balls;

	/* For queuing object addition/removal */
	private Queue<GameObject> objectsToAdd;
	private Queue<GameObject> objectsToRemove;
	private final int OBJECT_CREATION_COOLDOWN = 3;

	/* UI */
	private Label labelScore;
	private GlyphLayout glyphLayout;
	private Label gameOverMessage;
	private Label highScoreLabel;
	private Button buttonHome;
	private Button buttonRetry;

	/* Necessary to prevent lag */
	private int objectCreationCooldown;

	/* World */
	private ArrayList<Fixture> wallFixtures;

	/* States */
	private boolean gameEnded;

	public Play(ColorMatcher colorMatcher){
		super(colorMatcher);

		this.spriteBatch = new SpriteBatch();
		this.world = new World(new Vector2(0, -9.81f), true);

		this.quizManager = new Json().fromJson(QuizManager.class, Gdx.files.internal("data/quiz.json"));
		this.quizManager.initAfterLoad(colorMatcher);

		this.objectsToAdd = new LinkedList<GameObject>();
		this.objectsToRemove = new LinkedList<GameObject>();

		this.balls = new ArrayList<Ball>();

		this.gameCamera = new OrthographicCamera();
		this.gameViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera);
		this.referenceUnitViewport = new FitViewport(ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);
	}

	@Override
	public void show(){
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		this.tweenManager = new TweenManager();

		this.stage = new Stage(this.referenceUnitViewport);
		this.atlas = new TextureAtlas("ui/button.pack");
		this.skin = new Skin(Gdx.files.internal("ui/playSkin.json"), this.atlas);
		this.table = new Table(this.skin);
		this.table.setBounds(0, 0, this.stage.getWidth(), this.stage.getHeight());

		this.glyphLayout = new GlyphLayout();
		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), Integer.toString(this.quizManager.getScore()));
		this.labelScore = new Label(Integer.toString(this.quizManager.getScore()), this.skin);
		this.labelScore.setColor(this.quizManager.getColor());

		this.table.add(this.labelScore).width(this.glyphLayout.width).height(this.glyphLayout.height).pad(100).colspan(2);
		this.table.row();
		this.table.top();

		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), "game over");
		this.gameOverMessage = new Label("game over", skin);
		this.gameOverMessage.setFontScale(.5f);
		this.table.add(this.gameOverMessage).width(this.glyphLayout.width * .5f).height(this.glyphLayout.height * .5f).pad(50).
				colspan(2);

		this.table.row();

		this.highScoreLabel = new Label("", this.skin);
		this.table.add(this.highScoreLabel);

		this.table.row();

		this.buttonHome = new Button(this.skin);
		this.buttonHome.setStyle(skin.get("buttonHome", Button.ButtonStyle.class));

		this.buttonHome.addListener(
				new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y){
						if(isTransitioning)
							return;
						colorMatcher.getSoundManager().playSound(0);
						colorMatcher.enterState(GameStateManager.StateEnum.MENU, 40);
					}
				}
		);

		this.table.add(this.buttonHome).width(128).height(128).pad(50);

		this.buttonRetry = new Button(this.skin);
		this.buttonRetry.setStyle(this.skin.get("buttonRetry", Button.ButtonStyle.class));

		this.buttonRetry.addListener(
				new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y){
						colorMatcher.getSoundManager().playSound(0);
						retry();
					}
				}
		);
		this.table.add(this.buttonRetry).width(128).height(128).pad(50);

		this.togglePostGameUiVisibility(false);
		this.togglePostGameUiFunctionality(false);

		this.tweenShowScore();

		this.applyUiDelay();

		this.stage.addActor(this.table);
	}

	@Override
	public void update(float delta) {
		this.objectCreationCooldown --;

		/* Adds objects in queue to the world, if cooldown is over */
		if(this.objectCreationCooldown <= 0 && !this.gameEnded && this.objectsToAdd.peek() != null) {
			this.objectsToAdd.poll().addToWorld(this.world);
			this.objectCreationCooldown = this.OBJECT_CREATION_COOLDOWN;
		}

		/* Removes all objects in destroy queue from the world */
		for(Iterator<GameObject> iterator = this.objectsToRemove.iterator(); iterator.hasNext();){
			GameObject gameObject = iterator.next();

			if(!(gameObject.canRemove())) continue;

			gameObject.removeFromWorld();

			iterator.remove();
		}

		for(Ball ball : this.balls){
			ball.update();

			// Checks condition for ending the game
			if(ball.getMeterPosition(true).y + ball.getRadius() > 9.6 && ball.getAliveTime() >= 100){
				this.endGame();
			}
		}

		this.quizManager.update();

		this.world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

		this.updateUI();

		this.tweenManager.update(delta);

		this.gameCamera.update();
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_ZERO);

		this.gameViewport.apply();

		this.gameViewport.getCamera().position.set(this.WORLD_WIDTH / 2, this.WORLD_HEIGHT / 2, 0);
		this.spriteBatch.setProjectionMatrix(this.gameViewport.getCamera().combined);

		for(Ball ball : balls){
			this.spriteBatch.begin();
			ball.render(this.spriteBatch);
			this.spriteBatch.end();
		}

		this.referenceUnitViewport.apply();

		super.render(delta);

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void resize(int width, int height) {
		this.gameViewport.update(width, height);
		this.referenceUnitViewport.update(width, height);
	}

	/**
	 * Updates the UI with the latest labelScore.
	 */
	private void updateUI() {
		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), Integer.toString(this.quizManager.getScore()));
		this.labelScore.setText(Integer.toString(this.quizManager.getScore()));
		this.table.getCell(this.labelScore).width(this.glyphLayout.width).height(this.glyphLayout.height);

		Color gameColor = this.quizManager.getColor();
		Color labelColor = labelScore.getColor();

		float[] rgbGame = new float[]{gameColor.r, gameColor.g, gameColor.b};
		float[] rgbLabel = new float[]{labelColor.r, labelColor.g, labelColor.b};

		for(int i = 0; i < 3; i ++) {

			rgbLabel[i] += (rgbGame[i] - rgbLabel[i]) / 12;

		}

		this.labelScore.setColor(rgbLabel[0], rgbLabel[1], rgbLabel[2], .8f);
	}

	private void tweenShowScore() {
		this.timeline = Timeline.createSequence();

		this.timeline.beginSequence()
				.push(Tween.set(this.labelScore, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(this.labelScore, ActorAccessor.ALPHA, 2f).target(1))
				.end().start(this.tweenManager);
	}

	private void tweenHideAllUi() {
		this.timeline.kill();
		this.timeline = Timeline.createSequence();
		this.timeline.beginParallel()
				.push(Tween.to(this.buttonHome, ActorAccessor.ALPHA, .5f).target(0))
				.push(Tween.to(this.buttonRetry, ActorAccessor.ALPHA, .5f).target(0))
				.push(Tween.to(this.gameOverMessage, ActorAccessor.ALPHA, .5f).target(0))
				.push(Tween.to(this.highScoreLabel, ActorAccessor.ALPHA, .5f).target(0))
				.push(Tween.to(this.labelScore, ActorAccessor.ALPHA, .5f).target(0))
				.end().start(this.tweenManager);
	}

	private void tweenShowPostGameUI() {
		this.timeline = Timeline.createSequence();
		this.timeline.beginSequence()
				.push(Tween.set(this.gameOverMessage, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(this.highScoreLabel, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(this.buttonHome, ActorAccessor.ALPHA).target(0))
				.push(Tween.set(this.buttonRetry, ActorAccessor.ALPHA).target(0))
				.push(Tween.to(this.gameOverMessage, ActorAccessor.ALPHA, 1f).target(1))
				.push(Tween.to(this.highScoreLabel, ActorAccessor.ALPHA, 1f).target(1))
				.beginParallel()
				.push(Tween.to(this.buttonHome, ActorAccessor.ALPHA, 1f).target(1))
				.push(Tween.to(this.buttonRetry, ActorAccessor.ALPHA, 1f).target(1))
				.end().start(this.tweenManager);
	}

	/* States */

	@Override
	public void onEnter(){
		this.buildEnvironment();
	}

	@Override
	public void onExit(){
		this.destroyEnvironment();
	}

	/**
	 * Initialises the game.
	 */
	private void buildEnvironment() {
		this.gameEnded = false;
		this.quizManager.onStart();
		this.initObjects();
		this.objectCreationCooldown = 2;
	}

	/**
	 * Destroys the game-world to ready it to be initialised again.
	 */
	private void destroyEnvironment() {
		/* Destroy all bodies in world */
		Array<Body> bodies = new Array<Body>();
		this.world.getBodies(bodies);

		for(Body body : bodies){
			this.world.destroyBody(body);
		}

		/* Clears data */
		this.balls.clear();
		this.objectsToAdd.clear();
		this.objectsToRemove.clear();

		this.tweenHideAllUi();
	}

	/**
	 * Retries the game.
	 */
	private void retry(){
		this.destroyEnvironment();

		this.togglePostGameUiFunctionality(false);
		
		Timer.schedule(new Task(){

			@Override
			public void run(){

				buildEnvironment();

				tweenShowScore();

				togglePostGameUiVisibility(false);

			}

		}, 1f);
		
	}
	
	private void endGame(){
		if(this.gameEnded)
			return;

		this.gameEnded = true;
		
		Array<Body> bodies = new Array<Body>();

		this.world.getBodies(bodies);

		for(Body body : bodies){
			if(this.wallFixtures.get(0).getBody() == body)
				this.world.destroyBody(body);
		}

		this.wallFixtures.clear();

		this.timeline.kill();

		int oldHighScore = this.quizManager.getHighScore();
		this.quizManager.onEnd();

		Json json = new Json();
		json.setOutputType(JsonWriter.OutputType.json);
		String jsonString = json.prettyPrint(this.quizManager);
		Gdx.files.local("data/quiz.json").writeString(jsonString, false);

		String highScoreMessage =  this.quizManager.getHighScore() > oldHighScore ? "new highscore - " +
				this.quizManager.getHighScore() : "highscore - " + quizManager.getHighScore();
		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), highScoreMessage);
		this.highScoreLabel.setText(highScoreMessage);
		this.highScoreLabel.setFontScale(.25f);
		this.table.getCell(this.highScoreLabel).width(this.glyphLayout.width * .25f).
				height(this.glyphLayout.height * .25f).pad(50).colspan(2);

		this.tweenShowPostGameUI();

		this.togglePostGameUiVisibility(true);

		Timer.schedule(new Task(){

			@Override
			public void run(){

				togglePostGameUiFunctionality(true);

			}

		}, 3f);
	}
	
	@Override 
	public void dispose(){
		world.dispose();
	}
	
	private void initObjects(){
		BodyDef wallDef = new BodyDef();
		wallDef.type = BodyType.StaticBody;
		
		ChainShape wall = new ChainShape();
		ChainShape wall2 = new ChainShape();
		ChainShape wall3 = new ChainShape();
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.friction = 0;
		fixtureDef.restitution = 0;
		
		wallDef.position.set(0, 0);
		
		wall.createChain(new Vector2[]{new Vector2(0, 0), new Vector2(WORLD_WIDTH, 0)});	
		wall2.createChain(new Vector2[]{new Vector2(0, 0), new Vector2(0, WORLD_HEIGHT)});
		wall3.createChain(new Vector2[]{new Vector2(WORLD_WIDTH, 0), new Vector2(WORLD_WIDTH, WORLD_HEIGHT)});
		
		wallFixtures = new ArrayList<Fixture>();
	
		fixtureDef.shape = wall;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(0).setUserData(new float[]{WORLD_WIDTH, 0});
		
		fixtureDef.shape = wall2;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(1).setUserData(new float[]{0, WORLD_HEIGHT});

		fixtureDef.shape = wall3;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(2).setUserData(new float[]{0, WORLD_HEIGHT});
	}

	private void togglePostGameUiVisibility(boolean on) {
		this.gameOverMessage.setVisible(on);
		this.highScoreLabel.setVisible(on);
		this.buttonHome.setVisible(on);
		this.buttonRetry.setVisible(on);
	}

	private void togglePostGameUiFunctionality(boolean on) {
		Touchable touchable = on ? Touchable.enabled : Touchable.disabled;
		this.buttonHome.setTouchable(touchable);
		this.buttonRetry.setTouchable(touchable);
	}

	/* Units and conversions */

	/**
	 * Converts screen coordinates to world coordinates.
	 * @param screenCoordinates The screen coordinates.
	 * @return The world coordinates.
	 */
	public Vector2 screenCoordinatesToWorld(Vector2 screenCoordinates) {
		Vector3 unprojectedCoordinates = this.gameCamera.unproject(
				new Vector3(screenCoordinates.x, screenCoordinates.y, 0f),
				gameViewport.getScreenX(), gameViewport.getScreenY(), gameViewport.getScreenWidth(),
				gameViewport.getScreenHeight()
		);

		return new Vector2(
				unprojectedCoordinates.x ,
				unprojectedCoordinates.y
		);
	}

	/**
	 * Converts metres to reference units.
	 * @param metres The value in metres.
	 * @return The value in reference units.
	 */
	public float metresToReferenceUnits(float metres) {
		return ColorMatcher.REF_WIDTH / WORLD_WIDTH * metres;
	}

	/**
	 * Returns a fixture's position in reference units.
	 * @param fixture The fixture.
	 * @param centre Whether to return the centre position.
	 * @return A vector of the fixture's position in reference units.
	 */
	public Vector2 getReferenceUnitPosition(Fixture fixture, boolean centre){
		Body body = fixture.getBody();
		Shape shape = fixture.getShape();
		float x = metresToReferenceUnits(body.getPosition().x) - (centre ? 0 : metresToReferenceUnits(shape.getRadius()));
		float y = metresToReferenceUnits(body.getPosition().y) - (centre ? 0 : metresToReferenceUnits(shape.getRadius()));
		return new Vector2(x, y);
	}

	/**
	 * Returns a fixture's position in metres.
	 * @param fixture The fixture.
	 * @param centre Whether to return the centre position.
	 * @return A vector of the fixture's position in metres.
	 */
	public Vector2 getMeterPosition(Fixture fixture, boolean centre){
		Body body = fixture.getBody();
		Shape shape = fixture.getShape();
		float x = body.getPosition().x - (centre ? 0 : shape.getRadius());
		float y = body.getPosition().y - (centre ? 0 : shape.getRadius());
		return new Vector2(x, y);
	}

	/* Events */

	@Override
	public void touchDown(int x, int y, int pointer, int button){

		this.stage.touchDown(x, y, pointer, button);

		if(this.gameEnded) return;

		Vector2 touchedPosition = this.screenCoordinatesToWorld(new Vector2(x, y));

		/* Checks whether the touched position is in proximity of a game-object */
		for(int i = 0; i < balls.size(); i ++){

			Ball ball = balls.get(i);

			if(!(ball instanceof AnswerBall)) continue;

			Vector2 ballPosition = ball.getMeterPosition(true);

			if(Math.pow(touchedPosition.x - ballPosition.x, 2) + Math.pow(touchedPosition.y - ballPosition.y, 2) >
					Math.pow(ball.getRadius(), 2)) {
				continue;
			}

			quizManager.onAnswerSelect((AnswerBall) ball);

		}

	}

	@Override
	public void touchUp(int x, int y, int pointer, int button){
		this.stage.touchUp(x, y, pointer, button);
	}

	/* Setters and getters */

	/**
	 * Adds a new ball to the world.
	 * @param ball The ball.
	 */
	public void addBall(Ball ball){
		objectsToAdd.add(ball);
	}

	/**
	 * Removes a ball from the world.
	 * @param ball The ball to be removed.
	 */
	public void removeBall(Ball ball){
		ball.destroy();
		objectsToRemove.add(ball);
	}

	/**
	 * Returns the world.
	 * @return The world.
	 */
	public World getWorld(){
		return world;
	}

	/**
	 * Returns the game-world camera.
	 * @return The game-world camera.
	 */
	public OrthographicCamera getGameCamera() {
		return this.gameCamera;
	}

	/**
	 * Returns the reference unit viewport.
	 * @return The reference unit viewport.
	 */
	public Viewport getReferenceUnitViewport() {
		return this.referenceUnitViewport;
	}

	/**
	 * Returns the balls loaded in the world.
	 * @return The balls.
	 */
	public ArrayList<Ball> getBalls(){
		return balls;
	}

}
