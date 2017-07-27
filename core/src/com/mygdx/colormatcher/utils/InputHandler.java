package com.mygdx.colormatcher.utils;

import com.badlogic.gdx.InputProcessor;
import com.mygdx.colormatcher.game.ColorMatcher;

public class InputHandler implements InputProcessor{

	private ColorMatcher colorMatcher;

	public InputHandler(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		this.colorMatcher.getCurrentState().touchDown(screenX, screenY, pointer, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		this.colorMatcher.getCurrentState().touchUp(screenX, screenY, pointer, button);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.colorMatcher.getCurrentState().touchDragged(screenX, screenY, pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		this.colorMatcher.getCurrentState().mouseMoved(screenX, screenY);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
