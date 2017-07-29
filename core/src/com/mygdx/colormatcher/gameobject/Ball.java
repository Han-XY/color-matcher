package com.mygdx.colormatcher.gameobject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.colormatcher.game.ColorMatcher;

/**
 * A Ball is a clickable entity of the game.
 */
public abstract class Ball extends GameObject{

	protected String label;
	protected float x, y;
	protected boolean dead;
	protected int deathTimer;
	protected int maxDeathTime;
	protected float radius;
	protected float tempRadius; //stores the original radius while the radius is being modified.
	protected int aliveTime;

	/* Rendering */
	private Pixmap pixmap;
	private Texture texture;
	protected Color color;

	/** Used to determine if alpha has been modified */
	private float lastAlpha;
	
	public Ball(String label, float x, float y, float radius, Color color, ColorMatcher colorMatcher){
		super(colorMatcher);

		this.label = label;
		this.x = x;
		this.y = y;
		this.width = radius * 2;
		this.radius = radius;
		this.color =  color;
		
		this.initPixmap();
		this.updateTexture();
	}

	/**
	 * Initialises a pixmap for rendering the shape.
	 */
	private void initPixmap() {
		this.pixmap = new Pixmap(200, 200, Pixmap.Format.RGBA8888);
	}

	/** Updates the shape texture with new alpha */
	private void updateTexture() {
		this.lastAlpha = this.alpha;
		this.pixmap.setBlending(Blending.None);
		this.pixmap.setColor(this.color.r, this.color.g, this.color.b, this.alpha);
		this.pixmap.fillCircle(100, 100, 100);
		this.texture = new Texture(this.pixmap);
	}
	
	@Override
	public void update(){
		this.aliveTime ++;
	}
	
	public float getRadius(){
		return this.radius;
	}

	public void destroy(){
		this.dead = true;
		this.deathTimer = maxDeathTime;
	}
	
	@Override
	public void dispose() {
		this.pixmap.dispose();
	}
	
	public int getAliveTime(){
		return aliveTime;
	}
	
	protected void drawBody(SpriteBatch batch) {
		if(lastAlpha != this.alpha) updateTexture();

		Vector2 metrePosition = this.getMeterPosition(true);
		batch.draw(this.texture, metrePosition.x - this.radius, metrePosition.y - this.radius, this.radius * 2, this.radius * 2);
	}
	
	protected void drawLabel(SpriteBatch batch) {
		GlyphLayout glyphLayout = new GlyphLayout();
		BitmapFont bitmapFont = this.colorMatcher.getFontBlack();
		bitmapFont.getData().setScale(1f);
		glyphLayout.setText(bitmapFont, this.label);
		
		/* The pixel side length of the square available for the text to be drawn inside. */
		float availableSideUnits = 0.8f * 2 * this.colorMatcher.getPlayState().metresToReferenceUnits(this.radius);

		/* Calculates by determining the side that meets the limit the first */
		float scale = availableSideUnits / (glyphLayout.width > glyphLayout.height ? glyphLayout.width : glyphLayout.height);
		
		bitmapFont.getData().setScale(scale);
		bitmapFont.setColor(0f, 0f, 0f, this.alpha);
		Vector2 referenceUnitPosition = this.getReferenceUnitPosition(true);

		/* Changes projection matrix to reference units so that the font is visible */
		batch.setProjectionMatrix(this.colorMatcher.getPlayState().getReferenceUnitViewport().getCamera().combined);
		
		float scaledWidth = scale * glyphLayout.width;
		float scaledHeight = scale * glyphLayout.height;
		
		bitmapFont.draw(batch, label, referenceUnitPosition.x - scaledWidth / 2, referenceUnitPosition.y + scaledHeight / 2);
		
		batch.setProjectionMatrix(this.colorMatcher.getPlayState().getGameCamera().combined);
		
		bitmapFont.getData().setScale(1f);
	}

	protected void updateCreationAnimation() {
		if(this.tempRadius == 0) {
			this.tempRadius = this.radius;
			this.radius *= .2f;
			this.alpha = .2f;
		}

		this.radius *= 1.3f;
		this.alpha += .1f;

		if(this.radius >= this.tempRadius) {
			this.radius = this.tempRadius;
			this.alpha = 1f;
		}
	}

	/**
	 * Updates the death animation of the ball.
	 */
	protected void updateDeathAnimation() {
		if(this.dead){
			this.radius *= 1.1f;
			this.alpha -= .1f;

			this.deathTimer --;
			if(this.deathTimer <= 0)
				this.shouldRemove = true;
		}
	}

	@Override
	public void addToWorld(World world) {
		super.addToWorld(world);
		this.colorMatcher.getPlayState().getBalls().add(this);
	}

	@Override
	public void removeFromWorld() {
		super.removeFromWorld();
		this.colorMatcher.getPlayState().getBalls().remove(this);
	}
		
}
