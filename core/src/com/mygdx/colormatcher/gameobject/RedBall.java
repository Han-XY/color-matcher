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

public class RedBall extends Ball{

	public RedBall(float x, float y, float radius, ColorMatcher colorMatcher) {
		super("", x, y, radius, Color.RED, colorMatcher);
	}
	
	@Override
	public void init() {
		this.bodyDef = new BodyDef();
		this.bodyDef.type = BodyType.DynamicBody;
		this.bodyDef.position.set(new Vector2(x, y));

		this.shape = new CircleShape();
		this.shape.setRadius(this.radius);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = this.shape;
		this.fixtureDef.density = 2.5f;
		this.fixtureDef.friction = 0f;
		this.fixtureDef.restitution = .5f;

		this.alpha = 1f;
		this.maxDeathTime = 10;
	}

	@Override
	public void render(SpriteBatch batch) {
		this.drawBody(batch);
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	public void dispose() {

	}

}
