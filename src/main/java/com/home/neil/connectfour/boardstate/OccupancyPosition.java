package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;

public class OccupancyPosition extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = OccupancyPosition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private Player mPlayer = null;
	private Position mPosition = null;
	private ArrayList <WinningCombination> mWinningCombinations = new ArrayList <> ();
	private ArrayList <WinningCombination> mOpponentWinningCombinationsToRuleOut = new ArrayList <> ();

	public OccupancyPosition(Player pPlayer, Position pPosition) {
		super(new GameAttribute[] { pPlayer, pPosition }, constructAttributeName(pPlayer, pPosition));
		mPlayer = pPlayer;
		mPosition = pPosition;
	}

	public Player getPlayer() {
		return mPlayer;
	}

	public Position getPosition() {
		return mPosition;
	}
	
	public void addWinningCombination (WinningCombination pWinningCombination) {
		mWinningCombinations.add(pWinningCombination);
	}
	
	public List <WinningCombination> getWinningCombinations () {
		return mWinningCombinations;
	}
	
	public void addWinningCombinationToRuleOut (WinningCombination pWinningCombinationToRuleOut) {
		mOpponentWinningCombinationsToRuleOut.add(pWinningCombinationToRuleOut);
	}
	
	public List <WinningCombination> getWinningCombinationsToRuleOut () {
		return mOpponentWinningCombinationsToRuleOut;
	}
	
	public static String constructAttributeName (Player pPlayer, Position pStartingPosition) {
		return pPlayer.getAttributeName() + "_" + pStartingPosition.getAttributeName();
	}
	
	
}
