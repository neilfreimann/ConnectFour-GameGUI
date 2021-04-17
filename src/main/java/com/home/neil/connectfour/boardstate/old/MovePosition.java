package com.home.neil.connectfour.boardstate.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.ConnectFourBoardAttribute;
import com.home.neil.connectfour.boardstate.Position;

public class MovePosition extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = MovePosition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static HashMap <String, MovePosition> sMovePositions = new HashMap <> ();	
	
	private OldPlayer mPlayer = null;
	private Position mPosition = null;
	
	private String mMovePositionString;
	
	private ArrayList <OldWinningCombination> mPlayerWinningCombinations = new ArrayList <> ();
	private ArrayList <OldWinningCombination> mOppositePlayerWinningCombinations = new ArrayList <> ();
	
	public static void init () {
		
	}


	static {
		Collection <Position> lPositions = Position.getPositions();
		
		for (Position lPosition : lPositions) {
			MovePosition lSelfMovePosition = new MovePosition (OldPlayer.SELF, lPosition);
			sMovePositions.put(lSelfMovePosition.getMovePositionString(), lSelfMovePosition);
			sLogger.info("Move Position Loaded: {}", lSelfMovePosition.getMovePositionString());

			MovePosition lOpponentMovePosition = new MovePosition (OldPlayer.OPPONENT, lPosition);
			sMovePositions.put(lOpponentMovePosition.getMovePositionString(), lOpponentMovePosition);
			sLogger.info("Move Position Loaded: {}", lOpponentMovePosition.getMovePositionString());

		}

	}

	public static MovePosition getMovePosition (OldPlayer pPlayer, Position pPosition) {
		String lMovePositionString = constructMovePositionString(pPlayer, pPosition);
		return getMovePosition (lMovePositionString);
	}
	
	public static MovePosition getMovePosition (String pMovePositionString) {
		return sMovePositions.get(pMovePositionString);
	}
	
	
	public static String constructMovePositionString(OldPlayer pPlayer, Position pPosition) {
		return pPlayer.getPlayerString() + "_" + pPosition.getPositionString();
	}


	private MovePosition(OldPlayer pPlayer, Position pPosition) {
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

	public OldPlayer getPlayer() {
		return mPlayer;
	}
	
	public static Collection <MovePosition> getMovePositions () {
		return sMovePositions.values();
	}
	
	public void addWinningCombination (OldWinningCombination pWinningCombination) {
		ArrayList <MovePosition> lWinningCombinationMovePositions = pWinningCombination.getMovePositions();
		if (lWinningCombinationMovePositions.contains(this)) {
			if  ((pWinningCombination.getPlayer() == OldPlayer.SELF && mPlayer == OldPlayer.SELF) || 
					(pWinningCombination.getPlayer() == OldPlayer.OPPONENT && mPlayer == OldPlayer.OPPONENT)) {
				mPlayerWinningCombinations.add(pWinningCombination);
				sLogger.info("MovePosition {} Winning Combination Loaded: {}", mMovePositionString, pWinningCombination.getWinningCombinationString());
			} else {
				mOppositePlayerWinningCombinations.add(pWinningCombination);
				sLogger.info("MovePosition {} Winning Combination Loaded: {}", mMovePositionString, pWinningCombination.getWinningCombinationString());
			}
		}
	}
	
	
	

	
}
