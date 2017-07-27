package com.mygdx.colormatcher.game;

import java.util.HashMap;
import java.util.Map;

import com.mygdx.colormatcher.screens.Menu;
import com.mygdx.colormatcher.screens.Play;
import com.mygdx.colormatcher.screens.Splash;
import com.mygdx.colormatcher.screens.State;

/**
 * Manages the states of the game.
 */
public class GameStateManager {

	private Map<StateEnum, State> states;

	private StateEnum currentStateEnum;

	/* Used for state transitions */

	/** Whether a transition is occurring. **/
	private boolean transitioning;

	/** Duration remaining for the transition. **/
	private int transitionTicksLeft;

	/** Destination state. **/
	private StateEnum transitionDestination;

	private ColorMatcher colorMatcher;

	public enum StateEnum {
		DEFAULT, SPLASH, MENU, PLAY
	}
	
	public GameStateManager(ColorMatcher colorMatcher){
		this.colorMatcher = colorMatcher;

		this.states = new HashMap<StateEnum, State>();

		this.states.put(StateEnum.SPLASH, new Splash(colorMatcher));
		this.states.put(StateEnum.MENU, new Menu(colorMatcher));
		this.states.put(StateEnum.PLAY, new Play(colorMatcher));

		this.currentStateEnum = StateEnum.DEFAULT;
	}

	/** Updates the current state. */
	public void update(float delta){
		if(this.currentStateEnum == StateEnum.DEFAULT) throw new IllegalStateException("Current state not yet set.");

		State currentState = this.states.get(this.currentStateEnum);

		currentState.update(delta);

		/* Updates the state transition */
		if(this.transitioning){

			this.transitionTicksLeft --;

			if(this.transitionTicksLeft <= 0){
				this.transitioning = false;
				currentState.setTransitioning(false);
				this.enterState(this.transitionDestination);
				currentState.onEnter();
			}
		}
	}

	/** Begins a transition to a new state.
	 * @param destination The destination state.
	 * @param transitionTicks The transition duration.
	 */
	public void transitionTo(StateEnum destination, int transitionTicks){
		if(this.transitioning) return;

		this.transitioning = true;
		this.states.get(this.currentStateEnum).onExit();
		this.states.get(this.currentStateEnum).setTransitioning(true);
		this.transitionTicksLeft = transitionTicks;
		this.transitionDestination = destination;
	}

	/** Enters a new state.
	 * @param stateEnum The next state.
	 */
	public void enterState(StateEnum stateEnum){
		this.currentStateEnum = stateEnum;
		this.colorMatcher.setScreen(this.states.get(stateEnum));
	}

	public State getState(StateEnum stateEnum) {
		return this.states.get(stateEnum);
	}

	public State getCurrentState() {
		return this.states.get(this.currentStateEnum);
	}

}
