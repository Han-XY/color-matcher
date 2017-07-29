package com.mygdx.colormatcher.game;
import java.util.*;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.colormatcher.gameobject.AnswerBall;
import com.mygdx.colormatcher.gameobject.Ball;
import com.mygdx.colormatcher.gameobject.QuestionBall;
import com.mygdx.colormatcher.gameobject.RedBall;

/**
 * Sets the quiz problems, and checks if answers are right
 */
public class QuizManager {

	private int score;
	private int highScore;
	private int timer;
	private boolean gameEnded;

	private final int QUESTION_DURATION = 200;

	/** The theme color for the game. */
	private Color color;
	private float segments;
	private final float MAX_SEGMENTS = 100f;

	/** Keep transient to avoid circular references being serialised */
	private transient ColorMatcher colorMatcher;

	/** Stores all the questions so far. The correct answer is always the 0th element of the answer array. */
	private transient Map<QuestionBall, ArrayList<AnswerBall>> problems;

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

		if(this.isAnswerCorrect(answerBall)) {

			this.onCorrectAnswer(answerBall);

		}else {

			this.onIncorrectAnswer(answerBall);

		}

	}

	/**
	 * Called when a question has been answered correctly.
	 * @param answerBall The answer-ball chosen.
	 */
	private void onCorrectAnswer(AnswerBall answerBall){

		for(QuestionBall questionBall : this.problems.keySet()) {

			ArrayList<AnswerBall> answerBalls = this.problems.get(questionBall);

			if(!answerBalls.contains(answerBall)) continue;

			ArrayList<Ball> balls = new ArrayList<Ball>(answerBalls);

			balls.add(questionBall);

			for(Ball ball : balls) {

				this.colorMatcher.getPlayState().removeBall(ball);

			}

		}

		this.score += 1;

		this.colorMatcher.getSoundManager().playSound(new Random().nextInt(3) + 1);

	}

	/**
	 * Called when a question has been incorrectly answered.
	 * @param answerBall The chosen answer-ball.
	 */
	private void onIncorrectAnswer(AnswerBall answerBall){

		ArrayList<AnswerBall> answerBallArrayList = this.getArrayOfAnswers(answerBall);

		if(answerBallArrayList == null) return;

		for(AnswerBall answer : answerBallArrayList) {

			for(int i = 0; i < new Random().nextInt(2) + 1; i ++){

				this.colorMatcher.getPlayState().addBall(
						new RedBall(answer.getMeterPosition(true).x, answer.getMeterPosition(true).y,
								.3f, this.colorMatcher)
				);

			}

		}

		this.colorMatcher.getSoundManager().playSound(4);

	}

	/**
	 * Moves onto the next question, choosing the question color properties and queueing them to be added to the game.
	 */
	private void onNextQuestion(){

		Random random = new Random();

		int spawns = random.nextInt(3) + 3;

		AnswerBall[] answerBalls = new AnswerBall[spawns];

		QuestionBall questionBall = new QuestionBall(
				2 + random.nextFloat(), random.nextInt(5) + 10, 1f, this.color, this.colorMatcher
		);

		this.colorMatcher.getPlayState().addBall(questionBall);

		boolean[] directions = new boolean[]{random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};

		for(int i = 0; i < spawns; i ++) {

			float[] rgb = new float[]{this.color.r, this.color.g, this.color.b};

			for(int j = 0; j < 3; j ++) {

				rgb[j] = directions[j] ? rgb[j] + (1f - rgb[j]) / this.segments
						: rgb[j] - rgb[j] / this.segments;

			}

			Color newColor = new Color(rgb[0], rgb[1], rgb[2], 1f);

			answerBalls[i] = new AnswerBall(2 + random.nextFloat(), random.nextInt(5) + 10,
					(float) (random.nextInt(3) + 4) / 10,
					newColor, this.colorMatcher);

			this.colorMatcher.getPlayState().addBall(answerBalls[i]);

			this.color = newColor;

		}

		this.problems.put(questionBall, new ArrayList<AnswerBall>(Arrays.asList(answerBalls)));

		this.segments *= 1.1f;

		if(this.segments > this.MAX_SEGMENTS) this.segments = this.MAX_SEGMENTS;

	}

	/* Utilities */

	/**
	 * Determines whether a selected answer is correct.
	 * @param chosenAnswer The selected answer-ball.
	 * @return Whether the answer at the selected index is correct.
	 */
	private boolean isAnswerCorrect(AnswerBall chosenAnswer){

		for(ArrayList<AnswerBall> answerBallArrayList : this.problems.values()) {

			if(chosenAnswer == answerBallArrayList.get(0)) return true;

		}

		return false;

	}

	/**
	 * Returns the answer-ball array-list in which the answer-ball belongs.
	 * @param answerBall The answer-ball.
	 * @return The array-list of itself and all the other answers to the same problem.
	 * @throws NullPointerException if answer does not belong in any problem.
	 */
	private ArrayList<AnswerBall> getArrayOfAnswers(AnswerBall answerBall) {

		for(ArrayList<AnswerBall> answerBallArrayList : this.problems.values()) {

			if(answerBallArrayList.contains(answerBall)) return answerBallArrayList;

		}

		return null;
	}

	/* State management */

	/**
	 * Called when the quiz starts.
	 */
	public void onStart(){
		this.timer = 10;
		this.score = 0;
		this.gameEnded = false;

		Random random = new Random();
		this.color = new Color(
				.2f + random.nextFloat() * .8f,
				.2f + random.nextFloat() * .8f,
				.2f + random.nextFloat() * .8f,
				1f
		);

		this.segments = 10;

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

	public void initAfterLoad(ColorMatcher colorMatcher) {
		this.colorMatcher = colorMatcher;
		this.problems = new HashMap<QuestionBall, ArrayList<AnswerBall>>();
	}

	public int getScore(){
		return this.score;
	}

	public int getHighScore(){
		return this.highScore;
	}

}
