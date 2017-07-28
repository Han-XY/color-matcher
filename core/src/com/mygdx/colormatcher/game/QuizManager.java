package com.mygdx.colormatcher.game;

import java.util.Random;

import com.mygdx.colormatcher.gameobject.AnswerBall;
import com.mygdx.colormatcher.gameobject.Ball;
import com.mygdx.colormatcher.gameobject.QuestionBall;
import com.mygdx.colormatcher.gameobject.RedBall;

public class QuizManager {
	private Problem[] problems;
	private int score;
	private int highScore;

	private int timer;
	
	private boolean gameEnded;

	private transient ColorMatcher colorMatcher;

	public void setPlay(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}
	
	public void onStart(){
		timer = 10;
		score = 0;
		gameEnded = false;
	}
	
	public void update(){
		timer --;
		if(timer <= 0){
			if(!gameEnded) {
				nextQuestion();
			}
			timer = 100;
		}
	}

	public void selectAnswer(AnswerBall answerBall){
		if(isAnswerCorrect(answerBall.getAnswerIndex(), answerBall.getQuestionID())){
			onCorrectAnswer(answerBall.getQuestionID());
		}else{
			onIncorrectAnswer(answerBall.getQuestionID());
		}
	}
	
	private void onCorrectAnswer(int questionID){
		for(Ball b : this.colorMatcher.getPlayState().getBalls()){
			if(b.getQuestionID() == questionID){
				this.colorMatcher.getPlayState().removeBall(b);
			}
		}		
		
		score += 1; //problems[questionID].getPoints();
		this.colorMatcher.getSoundManager().playSound(new Random().nextInt(3) + 1);
	}
	
	private void onIncorrectAnswer(int questionID){
		int balls = this.colorMatcher.getPlayState().getBalls().size();
		for(int i = 0; i < balls; i ++){
			Ball b = this.colorMatcher.getPlayState().getBalls().get(i);
			if(b.getQuestionID() == questionID && b instanceof AnswerBall){
				for(int j = 0; j < new Random().nextInt(2) + 1; j ++){
					this.colorMatcher.getPlayState().addBall(
							new RedBall("", questionID,
							.3f, b.getMeterPosition(true).x, b.getMeterPosition(true).y, this.colorMatcher)
					);
				}
			}
		}

		this.colorMatcher.getSoundManager().playSound(4);
	}
	
	private boolean isAnswerCorrect(int userAnswerIndex, int questionNumber){
		if(userAnswerIndex == problems[questionNumber].getCorrectPosition()){
			return true;
		}
		return false;
	}

	private void nextQuestion(){
		Random random = new Random();

		int questionIndex = random.nextInt(problems.length);
		
		Problem problem = problems[questionIndex];
			
		QuestionBall questionBall = new QuestionBall(problem.getQuestion(), questionIndex, random.nextInt(2) + 1, random.nextInt(5) + 10, 1f, this.colorMatcher);
		this.colorMatcher.getPlayState().addBall(questionBall);
		
		
		for(int i = 0; i < 3; i ++){
			this.colorMatcher.getPlayState().addBall(new AnswerBall(problem.getAnswers()[i]
					, questionIndex, i, random.nextInt(2) + 1, random.nextInt(5) + 10, (float) (random.nextInt(3) + 4) / 10, this.colorMatcher));
		}

	}
	
	public void endGame(){
		gameEnded = true;
		if(score > highScore){
			highScore = score;
		}
	}
	public int getScore(){
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getHighScore(){
		return highScore;
	}

}
