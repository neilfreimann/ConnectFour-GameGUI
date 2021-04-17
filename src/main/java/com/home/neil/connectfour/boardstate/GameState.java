package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameState extends BoardAttribute {
	public static final String CLASS_NAME = GameState.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private BitSet mGameStateEncoding = null;
	
	private String mGameStateString;
	
	private GameState (int pGameState, String pGameStateString) {
		mGameStateEncoding = encodeToBitSet(pGameState, 2);
		mGameStateString = pGameStateString;
	}
	
	public static final GameState WIN = new GameState (0, "W");
	public static final GameState LOSS = new GameState (1, "L");
	public static final GameState DRAW = new GameState (2, "D");
	public static final GameState UNDECIDED = new GameState (3, "U");
	
	
	public BitSet getGameStateBooleanEncoding () {
		return mGameStateEncoding;
	}

	public String getGameStateString () {
		return mGameStateString;
	}
	
}
