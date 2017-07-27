package com.mygdx.colormatcher.game;

public class Problem {
	private String question;
	private String[] answers;
	private int correctPosition;
	private int points;
	
	public String[] getAnswers(){
		return answers;
	}
	
	public int getCorrectPosition(){
		return correctPosition;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public int getPoints(){
		return points;
	}
}
