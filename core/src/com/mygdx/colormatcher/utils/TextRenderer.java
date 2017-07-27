package com.mygdx.colormatcher.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRenderer {
	private BitmapFont bitmapFont;
	private Texture texture;
	private GlyphLayout layout;
	private float alpha;
	
	public TextRenderer(){
		bitmapFont = new BitmapFont(Gdx.files.internal("font/black.fnt"), false);
		texture = new Texture(Gdx.files.internal("img/download.jpg"));	
		layout = new GlyphLayout();
		alpha = 1;
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	public void renderText(SpriteBatch batch, String text, float rectX, float rectY, float rectWidth, float rectHeight){		
		bitmapFont.getData().setScale(1f);
		
		layout.setText(bitmapFont, text);
		
		if((layout.width > 0.8f * rectWidth || layout.width < 0.75 * rectWidth) && layout.width / rectWidth > layout.height / rectHeight){
			bitmapFont.getData().setScale(0.79f * rectWidth / layout.width);
		}
		if((layout.height > 0.8f * rectHeight || layout.height < 0.75 * rectHeight) && layout.height / rectHeight > layout.width / rectWidth){
			bitmapFont.getData().setScale(0.79f * rectHeight / layout.height);
		}
		
		layout.setText(bitmapFont, text);

		float textX = rectX + (rectWidth - layout.width) / 2;
		float textY = rectY + (rectHeight - layout.height) / 2 + layout.height;

		bitmapFont.setColor(0, 0, 0, alpha);
		bitmapFont.getData().setScale(0.1f);
		bitmapFont.draw(batch, "!!!", rectX, rectY);
		
		batch.draw(texture, rectX, rectY, .1f, .1f);
	}
}
