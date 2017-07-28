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
import com.mygdx.colormatcher.utils.TextRenderer;

public class AnswerBall extends Ball{
	
	private int answerIndex;

	public AnswerBall(String answer, int questionID, int answerIndex, int x, int y, float radius, ColorMatcher colorMatcher){
		super(answer, questionID, x, y, radius, Color.WHITE, colorMatcher);

		this.answerIndex = answerIndex;
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
		
		alpha = 1;

		maxDeathTime = 5;
	}

	@Override
	public void render(SpriteBatch batch) {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.drawBody(batch);
	    this.drawLabel(batch);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(dead){
			radius *= 1.1f;
			this.alpha -= .1f;
			
			deathTimer --;			
			if(deathTimer <= 0)
				shouldRemove = true;
		}	
	}
	
	public int getAnswerIndex(){
		return answerIndex;
	}
	
	@Override
	public void dispose() {

	}
}