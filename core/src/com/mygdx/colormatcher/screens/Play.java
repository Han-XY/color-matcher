package com.mygdx.colormatcher.screens;

import java.util.ArrayList;
import java.util.Iterator;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
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

	private OrthographicCamera gameCamera;

	public static Viewport gameViewport;
	public static Viewport pixelViewport;
	public static float PPM = 100f * getScale();
	
	private World world;

	private final float TIMESTEP = 1/60f;
	private final int VELOCITYITERATIONS = 8;
	private final int POSITIONITERATIONS = 3;
	private static float width = 5.4f;
	private static float height = 9.6f;
	
	private QuizManager quizManager;
	
	private ArrayList<Ball> balls;
	
	private ArrayList<GameObject> objectsToAdd;
	private ArrayList<GameObject> objectsToRemove;

	private Label score;
	
	private GlyphLayout glyphLayout;
	
	private int objectCreationCooldown;
	
	private ArrayList<Fixture> wallFixtures;
	
	private boolean gameEnded;
	private boolean gameRestarting;
	private Label gameOverMessage;
	private Label highScoreLabel;
	private Button buttonHome;
	private Button buttonRetry;

	public Play(ColorMatcher colorMatcher){
		super(colorMatcher);

		this.spriteBatch = new SpriteBatch();
		this.world = new World(new Vector2(0, -9.81f), true);

		this.quizManager = new Json().fromJson(QuizManager.class, Gdx.files.internal("data/quiz.json"));
		this.quizManager.setPlay(colorMatcher);

		this.objectsToAdd = new ArrayList<GameObject>();
		this.objectsToRemove = new ArrayList<GameObject>();
		this.balls = new ArrayList<Ball>();

		this.gameCamera = new OrthographicCamera();
		this.gameViewport = new FitViewport(width, height, gameCamera);
		this.pixelViewport = new FitViewport(ColorMatcher.REF_WIDTH, ColorMatcher.REF_HEIGHT);

		onEnter();
	}

	@Override
	public void onEnter(){
		gameEnded = false;
		quizManager.onStart();
		initObjects();
		objectCreationCooldown = 2;

	}
	
	@Override
	public void onExit(){
		destroyEnvironment();
	}
	
	private void destroyEnvironment(){		
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		
		for(Body body : bodies){
			world.destroyBody(body);
		}
		
		balls.clear();
		objectsToAdd.clear();
		objectsToRemove.clear();
		
		timeline.kill();
		timeline = Timeline.createSequence();
		timeline.beginParallel()
		.push(Tween.to(buttonHome, ActorAccessor.ALPHA, .5f).target(0))
		.push(Tween.to(buttonRetry, ActorAccessor.ALPHA, .5f).target(0))
		.push(Tween.to(gameOverMessage, ActorAccessor.ALPHA, .5f).target(0))
		.push(Tween.to(highScoreLabel, ActorAccessor.ALPHA, .5f).target(0))
		.push(Tween.to(score, ActorAccessor.ALPHA, .5f).target(0))
		.end().start(tweenManager);
	}
	
	@Override
	public void show(){
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		this.tweenManager = new TweenManager();

		this.stage = new Stage(this.pixelViewport);

		this.atlas = new TextureAtlas("ui/button.pack");
		this.skin = new Skin(Gdx.files.internal("ui/playSkin.json"), this.atlas);
		this.table = new Table(this.skin);
		this.table.setBounds(0, 0, this.stage.getWidth(), this.stage.getHeight());

		this.glyphLayout = new GlyphLayout();

		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), Integer.toString(this.quizManager.getScore()));
		this.score = new Label(Integer.toString(this.quizManager.getScore()), this.skin);

		this.table.add(this.score).width(this.glyphLayout.width).height(this.glyphLayout.height).pad(50).colspan(2);
		this.table.row();
		this.table.top();
		this.stage.addActor(this.table);

		if(this.timeline != null) this.timeline.kill();

		this.timeline = Timeline.createSequence();
		this.timeline.beginSequence()
		.push(Tween.set(this.score, ActorAccessor.ALPHA).target(0))
		.push(Tween.to(this.score, ActorAccessor.ALPHA, 1f).target(1))
		.end().start(this.tweenManager);
	}
	
	@Override
	public void update(float delta) {
		this.objectCreationCooldown --;
		
		for(Iterator<GameObject> it = objectsToAdd.iterator(); it.hasNext();){
			GameObject o = it.next();
			
			if((objectCreationCooldown >= 0 && !(o instanceof RedBall)) || gameEnded)
				break;
						
			if(o instanceof Ball){
				balls.add((Ball) o);
				it.remove();
			}
			
			o.addToWorld(world);
			
			objectCreationCooldown = 10;
			break;
		}
		
		for(Ball ball : balls){
			ball.update();
			
			if(ball.getMeterPosition(true).y + ball.getRadius() > 9.6 && ball.getAliveTime() >= 100){
				endGame();
			}
		}
		
		for(Iterator<GameObject> it = objectsToRemove.iterator(); it.hasNext();){
			GameObject o = it.next();
			
			if(!(o.canRemove()))
				continue;
			
			if(o instanceof Ball){
				balls.remove(o);
				o.dispose();
				world.destroyBody(o.getFixture().getBody());
			}
			
			it.remove();
		}

		this.quizManager.update();

		this.world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
		
		float scale = getScale();
		this.glyphLayout.setText(this.colorMatcher.getFontWhite(), Integer.toString(quizManager.getScore()));
		this.score.setText(Integer.toString(quizManager.getScore()));
		this.table.getCell(score).width(glyphLayout.width * scale).height(glyphLayout.height * scale);

		this.gameCamera.update();
		this.tweenManager.update(delta);
	}
	
	private void detectCollisions(){
		for(Ball ball : balls){
			boolean initialCollisionStatus = ball.isCollidingForFirst();
			int collisionCount = 0;
			for(Fixture wallFixture : wallFixtures){
				Body wallBody = wallFixture.getBody();
				if(ball.isColliding(wallBody.getPosition().x, wallBody.getPosition().y, ((float[]) wallFixture.getUserData())[0], 
						((float[]) wallFixture.getUserData())[1], 2)){
					collisionCount ++;
					if(!ball.isCollidingForFirst())
						ball.setIsCollidingForFirst(true);
					else
						break;
				}
			}
			if(collisionCount == 0)
				ball.setIsCollidingForFirst(false);
			if(!initialCollisionStatus && ball.isCollidingForFirst()){
				this.colorMatcher.getSoundManager().playSound(5);
			}
		}
	}
	
	@Override
	public void render(float delta){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameViewport.apply();

	    gameViewport.getCamera().position.set(width / 2, height / 2, 0);
		spriteBatch.setProjectionMatrix(gameViewport.getCamera().combined);

		for(Ball b : balls){
			spriteBatch.begin();
			b.render(spriteBatch);
			spriteBatch.end();
		}

		pixelViewport.apply();

		stage.act();
		stage.draw();	
	}
	
	@Override
	public void resize(int width, int height) {
		//super.resize(width, height);
		gameViewport.update(width, height);
		pixelViewport.update(width, height);
	}

	
	private void retry(){
		gameRestarting = true;
		
		onExit();
		
		Timer.schedule(new Task(){
			@Override
			public void run(){
				onEnter();
				show();
				gameRestarting = false;
			}
		}, .5f);
		
	}
	
	private void endGame(){
		if(gameEnded)
			return;
		gameEnded = true;
		
		Array<Body> bodies = new Array<Body>();
		
		world.getBodies(bodies);
		for(Body body : bodies){
			if(wallFixtures.get(0).getBody() == body)
				world.destroyBody(body);
		}
		
		wallFixtures.clear();

		glyphLayout.setText(this.colorMatcher.getFontWhite(), "game over");
		gameOverMessage = new Label("game over", skin);
		gameOverMessage.setFontScale(.5f);
		table.add(gameOverMessage).width(glyphLayout.width * .5f).height(glyphLayout.height * .5f).pad(50).
		colspan(2);
		
		table.row();
		
		int oldHighScore = quizManager.getHighScore();
		quizManager.endGame();
		
		Json json = new Json();
		String jsonString = json.prettyPrint(quizManager);
		Gdx.files.local("data/quiz.json").writeString(jsonString, false);
		
		String highScoreMessage =  quizManager.getHighScore() > oldHighScore ? "new highscore - " + quizManager.getHighScore() : "highscore - " + quizManager.getHighScore();
		glyphLayout.setText(this.colorMatcher.getFontWhite(), highScoreMessage);
		highScoreLabel = new Label(highScoreMessage, skin);
		highScoreLabel.setFontScale(.25f);
		table.add(highScoreLabel).width(glyphLayout.width * .25f).height(glyphLayout.height * .25f).pad(50).colspan(2);

		table.row();
		
		buttonHome = new Button(skin);
		buttonHome.setStyle(skin.get("buttonHome", Button.ButtonStyle.class));

		buttonHome.addListener(
				new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y){
						if(transitioning)
							return;
						colorMatcher.getSoundManager().playSound(0);
						colorMatcher.enterState(GameStateManager.StateEnum.MENU, 40);
					}
				}
		);

		table.add(buttonHome).width(128).height(128).pad(50);
		
		buttonRetry = new Button(skin);
		buttonRetry.setStyle(skin.get("buttonRetry", Button.ButtonStyle.class));
		buttonRetry.addListener(
				new ClickListener(){
					@Override
					public void clicked(InputEvent event, float x, float y){
						if(gameRestarting)
							return;
						colorMatcher.getSoundManager().playSound(0);
						retry();
					}
				}
		);
		table.add(buttonRetry).width(128).height(128).pad(50);
		
		timeline.kill();
		
		timeline = Timeline.createSequence();
		timeline.beginSequence()
		.push(Tween.set(gameOverMessage, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(highScoreLabel, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonHome, ActorAccessor.ALPHA).target(0))
		.push(Tween.set(buttonRetry, ActorAccessor.ALPHA).target(0))
		.push(Tween.to(gameOverMessage, ActorAccessor.ALPHA, 1f).target(1))
		.push(Tween.to(highScoreLabel, ActorAccessor.ALPHA, 1f).target(1))
		.beginParallel()
		.push(Tween.to(buttonHome, ActorAccessor.ALPHA, 1f).target(1))
		.push(Tween.to(buttonRetry, ActorAccessor.ALPHA, 1f).target(1))
		.end().start(tweenManager);
		
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
		
		wall.createChain(new Vector2[]{new Vector2(0, 0), new Vector2(width, 0)});	
		wall2.createChain(new Vector2[]{new Vector2(0, 0), new Vector2(0, height)});	
		wall3.createChain(new Vector2[]{new Vector2(width, 0), new Vector2(width, height)});
		
		wallFixtures = new ArrayList<Fixture>();
	
		fixtureDef.shape = wall;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(0).setUserData(new float[]{width, 0});
		
		fixtureDef.shape = wall2;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(1).setUserData(new float[]{0, height});

		fixtureDef.shape = wall3;
		wallFixtures.add(world.createBody(wallDef).createFixture(fixtureDef));
		wallFixtures.get(2).setUserData(new float[]{0, height});
	}

	
	public void addBall(Ball b){
		objectsToAdd.add(b);
	}
	
	public void removeBall(Ball b){
		b.destroy();
		objectsToRemove.add(b);
	}
	
	public ArrayList<Ball> getBalls(){
		return balls;
	}
	
	@Override
	public void touchDown(int x, int y, int pointer, int button){
		stage.touchDown(x, y, pointer, button);
		
		if(gameEnded)
			return;

		Vector2 touchedPosition = this.screenCoordinatesToWorld(new Vector2(x, y));

		for(int i = 0; i < balls.size(); i ++){
			Ball ball = balls.get(i);

			if(!(ball instanceof AnswerBall)) continue;
			
			Vector2 ballPosition = ball.getMeterPosition(false);
			System.out.println(touchedPosition.x + " " +touchedPosition.y + " " + ballPosition.x +  " " + ballPosition.y);

			if(touchedPosition.x < ballPosition.x || touchedPosition.x > touchedPosition.x + ball.getRadius() * 2
					|| touchedPosition.y < ballPosition.y || touchedPosition.y > ballPosition.y + ball.getRadius() * 2){
				continue;
			}

			quizManager.selectAnswer((AnswerBall) ball);
		}
	};
	@Override
	public void touchUp(int x, int y, int pointer, int button){
		stage.touchUp(x, y, pointer, button);
	}

	public static float ptm(float pixels){
		return pixels / PPM;
	}
	
	public static float mtp(float m){
		return m * PPM;
	}

	public static float metresToReferenceUnits(float metres) {
		return ColorMatcher.REF_WIDTH / width * metres;
	}

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
	
	public World getWorld(){
		return world;
	}
	
	public Body getBody(String name){
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);
		for(Body b : bodies){
			if(b.getUserData() == name){
				return b;
			}
		}
		return null;
	}

	public OrthographicCamera getGameCamera() {
		return this.gameCamera;
	}

	public Vector2 getReferenceUnitPosition(Fixture fixture, boolean centre){
		Body body = fixture.getBody();
		Shape shape = fixture.getShape();
		float x = metresToReferenceUnits(body.getPosition().x) - (centre ? 0 : metresToReferenceUnits(shape.getRadius()));
		float y = metresToReferenceUnits(body.getPosition().y) - (centre ? 0 : metresToReferenceUnits(shape.getRadius()));
		return new Vector2(x, y);
	}
	
	public Vector2 getMeterPosition(Fixture fixture, boolean centre){
		Body body = fixture.getBody();
		Shape shape = fixture.getShape();
		float x = body.getPosition().x - (centre ? 0 : shape.getRadius());
		float y = body.getPosition().y - (centre ? 0 : shape.getRadius());
		return new Vector2(x, y);
	}

	public static float getScale() {
		float ratio = ColorMatcher.REF_HEIGHT / ColorMatcher.REF_WIDTH;
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
		if(screenHeight / screenWidth > ratio) {
			return (float) screenWidth / ColorMatcher.REF_WIDTH ;
		}
		
		return (float) screenHeight / ColorMatcher.REF_HEIGHT;
	}
}
