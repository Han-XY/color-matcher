package com.mygdx.colormatcher.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.colormatcher.game.ColorMatcher;
import com.mygdx.colormatcher.utils.TextRenderer;

import java.util.Random;

public class AnswerBall extends Ball{

	private static float permanentChance = .5f;

	private int generation;

	public AnswerBall(float x, float y, float radius, Color color, int generation, ColorMatcher colorMatcher){
		super("", x, y, radius, color, colorMatcher);

		this.generation = generation;

		if(generation <= 0) return;

		this.setRadius();
	}

	/**
	 * Sets the radius of this answer-ball based on its generation number. Higher generation ones are larger.
	 */
	private void setRadius() {

		this.radius *= 1 + this.generation / 10f;

		if(this.radius > 2.5f) this.radius = 2.5f;

	}
	
	@Override
	public void init() {
		this.bodyDef = new BodyDef();
		this.bodyDef.type = BodyType.DynamicBody;
		this.bodyDef.position.set(new Vector2(x, y));

		this.shape = new CircleShape();
		this.shape.setRadius(this.radius);

		this.fixtureDef = new FixtureDef();
		this.fixtureDef.shape = shape;
		this.fixtureDef.density = 2.5f;
		this.fixtureDef.friction = .25f;
		this.fixtureDef.restitution = .5f;

		this.alpha = 1;

		this.maxDeathTime = 5;
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

	public int getGeneration() {
		return this.generation;
	}
}
