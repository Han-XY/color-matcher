package com.mygdx.colormatcher.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public class SoundManager {
	
	private List<Sound> sounds;
	
	public SoundManager() {
		sounds = new ArrayList<Sound>();
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/click.wav")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/swish-1.wav")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/swish-2.wav")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/swish-3.wav")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/wrong.wav")));
		sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/hit.wav")));
	}
	
	public void playSound(int id){	
		sounds.get(id).play();
	}
	
	public void dispose() {
		for(Sound s : sounds) {
			s.dispose();
		}
	}
}
