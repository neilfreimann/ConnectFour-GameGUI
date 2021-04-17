package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovePosition extends BoardAttribute {
	public static final String CLASS_NAME = MovePosition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static HashMap <String, MovePosition> sMovePositions = new HashMap <> ();	
	
	private Player mPlayer = null;
	private Position mPosition = null;
	
	private String mMovePositionString;
	
	private ArrayList <WinningCombination> mPlayerWinningCombinations = new ArrayList <> ();
	private ArrayList <WinningCombination> mOppositePlayerWinningCombinations = new ArrayList <> ();
	
	public static void init () {
		
	}


	static {
		Collection <Position> lPositions = Position.getPositions();
		
		for (Position lPosition : lPositions) {
			MovePosition lSelfMovePosition = new MovePosition (Player.SELF, lPosition);
			sMovePositions.put(lSelfMovePosition.getMovePositionString(), lSelfMovePosition);
			sLogger.info("Move Position Loaded: {}", lSelfMovePosition.getMovePositionString());

			MovePosition lOpponentMovePosition = new MovePosition (Player.OPPONENT, lPosition);
			sMovePositions.put(lOpponentMovePosition.getMovePositionString(), lOpponentMovePosition);
			sLogger.info("Move Position Loaded: {}", lOpponentMovePosition.getMovePositionString());

		}

	}

	public static MovePosition getMovePosition (Player pPlayer, Position pPosition) {
		String lMovePositionString = constructMovePositionString(pPlayer, pPosition);
		return getMovePosition (lMovePositionString);
	}
	
	public static MovePosition getMovePosition (String pMovePositionString) {
		return sMovePositions.get(pMovePositionString);
	}
	
	
	public static String constructMovePositionString(Player pPlayer, Position pPosition) {
		return pPlayer.getPlayerString() + "_" + pPosition.getPositionString();
	}


	private MovePosition(Player pPlayer, Position pPosition) {
		mPlayer = pPlayer;
		mPosition = pPosition;
		
		mMovePositionString = constructMovePositionString(pPlayer, pPosition);

	}
	
	public String getMovePositionString() {
		return mMovePositionString;
	}

	public Position getPosition() {
		return mPosition;
	}

	public Player getPlayer() {
		return mPlayer;
	}
	
	public static Collection <MovePosition> getMovePositions () {
		return sMovePositions.values();
	}
	
	public void addWinningCombination (WinningCombination pWinningCombination) {
		ArrayList <MovePosition> lWinningCombinationMovePositions = pWinningCombination.getMovePositions();
		if (lWinningCombinationMovePositions.contains(this)) {
			if  ((pWinningCombination.getPlayer() == Player.SELF && mPlayer == Player.SELF) || 
					(pWinningCombination.getPlayer() == Player.OPPONENT && mPlayer == Player.OPPONENT)) {
				mPlayerWinningCombinations.add(pWinningCombination);
				sLogger.info("MovePosition {} Winning Combination Loaded: {}", mMovePositionString, pWinningCombination.getWinningCombinationString());
			} else {
				mOppositePlayerWinningCombinations.add(pWinningCombination);
				sLogger.info("MovePosition {} Winning Combination Loaded: {}", mMovePositionString, pWinningCombination.getWinningCombinationString());
			}
		}
	}
	
	
	

	
}
