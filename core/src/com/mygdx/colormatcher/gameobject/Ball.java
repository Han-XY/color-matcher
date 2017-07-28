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

public abstract class Ball extends GameObject{
	
	protected String label;
	protected float x, y;
	protected int questionID;
	protected boolean dead;
	protected int deathTimer;
	protected int maxDeathTime;
	protected float radius;
	protected int aliveTime;
	
	private Pixmap pixmap;
	private Texture texture;
	
	protected Color color;
	
	private float lastAlpha;
	
	public Ball(String label, int questionID, float x, float y, float radius, Color color, ColorMatcher colorMatcher){
		super(colorMatcher);

		this.label = label;
		this.questionID = questionID;
		this.x = x;
		this.y = y;
		this.width = radius * 2;
		this.radius = radius;
		this.color =  color;
		
		this.initPixmap();
		this.updateTexture();
	}
	
	private void initPixmap() {
		this.pixmap = new Pixmap(200, 200, Pixmap.Format.RGBA8888);
	}
	
	private void updateTexture() {
		lastAlpha = this.alpha;
		this.pixmap.setBlending(Blending.None);
		this.pixmap.setColor(this.color.r, this.color.g, this.color.b, this.alpha);
		this.pixmap.fillCircle(100, 100, 100);
		this.texture = new Texture(this.pixmap);
	}
	
	@Override
	public void update(){
		aliveTime ++;
	}
	
	public float getRadius(){
		return radius;
	}

	public String getLabel() {
		return this.label;
	}
	
	public int getQuestionID(){
		return questionID;
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
		
		/** The pixel side length of the square available for the text to be drawn inside. **/
		float availableSideUnits = 0.8f * 2 * this.colorMatcher.getPlayState().metresToReferenceUnits(this.radius);
						
		float scale = availableSideUnits / (glyphLayout.width > glyphLayout.height ? glyphLayout.width : glyphLayout.height);
		
		bitmapFont.getData().setScale(scale);
		bitmapFont.setColor(0f, 0f, 0f, this.alpha);
		Vector2 referenceUnitPosition = this.getReferenceUnitPosition(true);
		
		batch.setProjectionMatrix(this.colorMatcher.getPlayState().getReferenceUnitViewport().getCamera().combined);
		
		float scaledWidth = scale * glyphLayout.width;
		float scaledHeight = scale * glyphLayout.height;
		
		bitmapFont.draw(batch, label, referenceUnitPosition.x - scaledWidth / 2, referenceUnitPosition.y + scaledHeight / 2);
		
		batch.setProjectionMatrix(this.colorMatcher.getPlayState().getGameCamera().combined);
		
		bitmapFont.getData().setScale(1f);
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
