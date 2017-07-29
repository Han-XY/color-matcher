package com.mygdx.colormatcher.game;

import java.util.Random;

import com.mygdx.colormatcher.gameobject.AnswerBall;
import com.mygdx.colormatcher.gameobject.Ball;
import com.mygdx.colormatcher.gameobject.QuestionBall;
import com.mygdx.colormatcher.gameobject.RedBall;

/**
 * Sets the quiz problems, and checks if answers are right
 */
public class QuizManager {

	private Problem[] problems;
	private int score;
	private int highScore;
	private int timer;
	private boolean gameEnded;

	private final int QUESTION_DURATION = 100;

	/** Keep transient to avoid circular references being serialised */
	private transient ColorMatcher colorMatcher;

	public void update(){

		this.timer --;

		if(this.timer <= 0){

			if(!this.gameEnded) this.onNextQuestion();

			this.timer = QUESTION_DURATION;
		}

	}

	/**
	 * Called to select an answer.
	 * @param answerBall The answer that is selected.
	 */
	public void onAnswerSelect(AnswerBall answerBall){

		if(this.isAnswerCorrect(answerBall.getAnswerIndex(), answerBall.getQuestionID())){

			this.onCorrectAnswer(answerBall.getQuestionID());

		}else{

			this.onIncorrectAnswer(answerBall.getQuestionID());

		}

	}

	/**
	 * Called when a question has been answered correctly.
	 * @param questionId The ID of the question.
	 */
	private void onCorrectAnswer(int questionId){

		for(Ball ball : this.colorMatcher.getPlayState().getBalls()){

			if(ball.getQuestionID() == questionId){

				this.colorMatcher.getPlayState().removeBall(ball);

			}

		}

		this.score += 1;

		this.colorMatcher.getSoundManager().playSound(new Random().nextInt(3) + 1);

	}

	/**
	 * Called when a question has been incorrectly answered.
	 * @param questionId The ID of the question.
	 */
	private void onIncorrectAnswer(int questionId){

		int numberOfBalls = this.colorMatcher.getPlayState().getBalls().size();

		for(int i = 0; i < numberOfBalls; i ++){

			Ball ball = this.colorMatcher.getPlayState().getBalls().get(i);

			if(ball.getQuestionID() == questionId && ball instanceof AnswerBall){

				for(int j = 0; j < new Random().nextInt(2) + 1; j ++){

					this.colorMatcher.getPlayState().addBall(
							new RedBall("", questionId,
							.3f, ball.getMeterPosition(true).x, ball.getMeterPosition(true).y, this.colorMatcher)
					);

				}

			}

		}

		this.colorMatcher.getSoundManager().playSound(4);

	}

	/**
	 * Moves onto the next question.
	 */
	private void onNextQuestion(){

		Random random = new Random();

		int questionIndex = random.nextInt(this.problems.length);
		
		Problem problem = this.problems[questionIndex];
			
		QuestionBall questionBall = new QuestionBall(problem.getQuestion(), questionIndex, random.nextInt(2) + 1, random.nextInt(5) + 10, 1f, this.colorMatcher);

		this.colorMatcher.getPlayState().addBall(questionBall);
		
		
		for(int i = 0; i < 3; i ++){

			this.colorMatcher.getPlayState().addBall(new AnswerBall(problem.getAnswers()[i]
					, questionIndex, i, random.nextInt(2) + 1, random.nextInt(5) + 10, (float) (random.nextInt(3) + 4) / 10, this.colorMatcher));

		}

	}

	/* Utilities */

	/**
	 * Determines whether a selected answer is correct.
	 * @param userAnswerIndex The selected answer index.
	 * @param questionIndex The question index.
	 * @return Whether the answer at the selected index is correct.
	 */
	private boolean isAnswerCorrect(int userAnswerIndex, int questionIndex){

		return (userAnswerIndex == this.problems[questionIndex].getCorrectPosition());

	}

	/* State management */

	/**
	 * Called when the quiz starts.
	 */
	public void onStart(){
		this.timer = 10;
		this.score = 0;
		this.gameEnded = false;
	}

	/**
	 * Called when the game ends and updates the player data.
	 */
	public void onEnd(){
		this.gameEnded = true;

		if(this.score > this.highScore){
			this.highScore = this.score;
		}
	}

	/* Setters and getters */

	public void setPlay(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
	}

	public int getScore(){
		return this.score;
	}

	public int getHighScore(){
		return this.highScore;
	}

}
