package com.home.neil.connectfour.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.BoardState;

public abstract class Game {
	public static final String CLASS_NAME = Game.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + Game.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private GameController mGameController = null;

	private GAMESTATE mCurrentGameState = null;
	private BoardState mCurrentBoardState = null;
	

	public GameController getGameController() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mGameController;
	}

	public GAMESTATE getCurrentGameState() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mCurrentGameState;
	}

	public static enum GAMESTATE {
		GAME_UNSTARTED (0, "Game Unstarted."), 
		GAME_STARTED_COMPUTER_FIRST_X_MOVE (1, "X-Computer Moves First."), 
		GAME_STARTED_HUMAN_FIRST_X_MOVE (2, "X-Human Moves First."), 
		COMPUTER_X_MOVE (3, "X-Computer Moves Next."), 
		COMPUTER_O_MOVE (4, "O-Computer Moves Next."), 
		HUMAN_X_MOVE (5, "X-Human Moves Next."), 
		HUMAN_O_MOVE (6, "O-Human Moves Next."), 
		COMPUTER_X_WON_GAME (7, "X-Computer Won Game."), 
		COMPUTER_O_WON_GAME (8, "O-Computer Won Game."), 
		HUMAN_X_WON_GAME (9, "X-Human Won Game."), 
		HUMAN_O_WON_GAME (10, "O-Human Won Game."), 
		COMPUTER_X_WON_GAME_BY_TIMEOUT (11, "X-Computer Won Game By Timeout."), 
		COMPUTER_O_WON_GAME_BY_TIMEOUT (12, "O-Computer Won Game By Timeout."), 
		HUMAN_X_WON_GAME_BY_TIMEOUT (13, "X-Human Won Game By Timeout."), 
		HUMAN_O_WON_GAME_BY_TIMEOUT (14, "O-Human Won Game By Timeout."), 
		COMPUTER_X_WON_GAME_BY_RESIGNATION (15, "X-Computer Won Game By Resignation."), 
		COMPUTER_O_WON_GAME_BY_RESIGNATION (16, "O-Computer Won Game By Resignation."), 
		HUMAN_X_WON_GAME_BY_RESIGNATION (17, "X-Human Won Game By Resignation."), 
		HUMAN_O_WON_GAME_BY_RESIGNATION (18, "O-Human Won Game By Resignation."), 
		COMPUTER_X_WON_GAME_BY_DISQUALIFIED (19, "X-Computer Won Game By Disqualification."), 
		COMPUTER_O_WON_GAME_BY_DISQUALIFIED (20, "O-Computer Won Game By Disqualification."), 
		HUMAN_X_WON_GAME_BY_DISQUALIFIED (21, "X-Human Won Game By Disqualification."), 
		HUMAN_O_WON_GAME_BY_DISQUALIFIED (22, "O-Human Won Game By Disqualification."), 
		DRAW_GAME (23, "Game is a draw."), 
		INTERNAL_ERROR_GAMESTATE_CORRUPT (99, "Internal Error Game State is Corrupt.");
		
		private int mGameState = 0;
		private String mGameStateString = new String();

		GAMESTATE (int pGameState, String pGameStateString) {
			mGameState = pGameState;
			mGameStateString = pGameStateString;
		}

		public String getGameStateString () {
			return mGameStateString;
		}

		public int getGameState() {
			return mGameState;
		}
	}

}
