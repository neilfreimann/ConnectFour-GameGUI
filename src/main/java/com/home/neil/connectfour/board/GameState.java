package com.home.neil.connectfour.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameState extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = GameState.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public static final int MAX_GAME_STATE_VALUE = 3;
	
	public GameState (int pGameState, String pGameStateString) {
		super (pGameState, MAX_GAME_STATE_VALUE, pGameStateString);
	}
}
