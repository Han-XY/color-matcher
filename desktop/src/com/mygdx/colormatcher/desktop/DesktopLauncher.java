package com.mygdx.colormatcher.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.colormatcher.game.ColorMatcher;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 729;
		config.height = 1296;
		new LwjglApplication(new ColorMatcher(), config);
	}
}
