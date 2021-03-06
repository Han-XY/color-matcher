package com.mygdx.colormatcher.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.colormatcher.game.ColorMatcher;

public class QuestionBall extends Ball{
				
	public QuestionBall(float x, float y, float radius, Color color, ColorMatcher colorMatcher){
		super("", x, y, radius, color, colorMatcher);
	}
	
	@Override
	public void init() {
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(x, y));

		shape = new CircleShape();
		shape.setRadius(radius);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 2.5f;
		fixtureDef.friction = .25f;
		fixtureDef.restitution = .5f;	

		alpha = 1f;
		maxDeathTime = 10;
		
	}

	@Override
	public void render(SpriteBatch batch) {
		this.drawBody(batch);
	}
	
	@Override
	public void update() {
		super.update();

		this.updateCreationAnimation();
		this.updateDeathAnimation();
	}
	
	@Override
	public void dispose() {

	}
}
