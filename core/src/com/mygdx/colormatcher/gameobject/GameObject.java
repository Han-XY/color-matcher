package com.mygdx.colormatcher.gameobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.colormatcher.game.ColorMatcher;

public abstract class GameObject {
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	protected Shape shape;
	protected Fixture fixture;
	protected Body body;
	protected Sprite sprite;
	protected boolean shouldRemove;
	protected float alpha;
	protected ShapeRenderer shapeRenderer;
	protected float width;
	protected float height;
	protected boolean colliding;

	protected ColorMatcher colorMatcher;

	public GameObject(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}
	
	public abstract void init();
	
	public abstract void render(SpriteBatch batch);

	public abstract void update();
		
	public void addToWorld(World world){
		init();
		body = this.colorMatcher.getPlayState().getWorld().createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
	}
	
	public Vector2 getMeterPosition(boolean centre){
		return new Vector2(
				this.colorMatcher.getPlayState().getMeterPosition(fixture, centre).x,
				this.colorMatcher.getPlayState().getMeterPosition(fixture, centre).y
		);
	}

	public Vector2 getReferenceUnitPosition(boolean centre) {
		return new Vector2(
				this.colorMatcher.getPlayState().getReferenceUnitPosition(fixture, centre).x,
				this.colorMatcher.getPlayState().getReferenceUnitPosition(fixture, centre).y
		);
	}
	
	public Fixture getFixture(){
		return fixture;
	}
	
	public boolean canRemove(){
		return shouldRemove;
	}
	
	public boolean isColliding(float x2, float y2, float w2, float h2, int overlapping){
		float x1 = getMeterPosition(false).x;
		float y1 = getMeterPosition(false).y;
		float w1 = this instanceof Ball ? ((Ball) this).getRadius() * 2 : width;
		float h1 = this instanceof Ball ? ((Ball) this).getRadius() * 2 : height;

		if((x1 > x2 && x1 < x2 + w2) || (x2 > x1 && x2 < x1 + w1)){
		
			if(overlapping == 0 || overlapping == 2){
				if(y1 >= y2 + h2 - .05f && y1 <= y2 + h2 + .05f){
					return true;
				}
				if(y1 + h1 >= y2 - .05f && y1 + h1 <= y2 + .05f){
					return true;
				}
				if(overlapping == 0)
					return false;
			}
			
			if((y1 > y2 && y1 < y2 + h2) || (y2 > y1 && y2 < y1 + h1)){
				return true;
			}
		}

		if((y1 > y2 && y1 < y2 + h2) || (y2 > y1 && y2 < y1 + h1)){
			
			if(overlapping == 0 || overlapping == 2){
				if(x1 >= x2 + w2 - .05f && x1 <= x2 + w2 + .05f){
					return true;
				}
				if(x1 + w1 >= x2 - .05f && x1 + w1 <= x2 + .05f){
					return true;
				}
				return false;
			}
			
		}

		return false;
	}
	
	public boolean isCollidingForFirst(){
		return colliding;
	}
	
	public void setIsCollidingForFirst(boolean colliding){
		this.colliding = colliding;
	}
	
	public abstract void dispose();
}
