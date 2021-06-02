package com.home.neil.connectfour.board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private List <OccupancyPosition> mWinningCombinationsOccupancyPositions = new ArrayList <> ();
	private ArrayList <WinningCombination> mOpponentWinningCombinationsToRuleOut = new ArrayList <> ();
	private List <OccupancyPosition> mWinningCombinationsOccupancyPositionsToRuleOut = new ArrayList <> ();
	private List <Position> mAffectedPositions = new ArrayList <> ();

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
		for (OccupancyPosition lOccupancyPosition : pWinningCombination.getOccupancyPositions() ) {
			mWinningCombinationsOccupancyPositions.add(lOccupancyPosition);
			mAffectedPositions.add(lOccupancyPosition.getPosition());
		}
		mWinningCombinationsOccupancyPositions = mWinningCombinationsOccupancyPositions.stream().distinct().collect(Collectors.toList());
		mAffectedPositions = mAffectedPositions.stream().distinct().collect(Collectors.toList());
	}
	
	public List <WinningCombination> getWinningCombinations () {
		return mWinningCombinations;
	}
	
	public List<OccupancyPosition> getWinningCombinationsOccupancyPositions() {
		return mWinningCombinationsOccupancyPositions;
	}

	public void addWinningCombinationToRuleOut (WinningCombination pWinningCombinationToRuleOut) {
		mOpponentWinningCombinationsToRuleOut.add(pWinningCombinationToRuleOut);
		for (OccupancyPosition lOccupancyPosition : pWinningCombinationToRuleOut.getOccupancyPositions() ) {
			mWinningCombinationsOccupancyPositionsToRuleOut.add(lOccupancyPosition);
			mAffectedPositions.add(lOccupancyPosition.getPosition());
		}
		mWinningCombinationsOccupancyPositionsToRuleOut = mWinningCombinationsOccupancyPositionsToRuleOut.stream().distinct().collect(Collectors.toList());
		mAffectedPositions = mAffectedPositions.stream().distinct().collect(Collectors.toList());
	}

	public List <WinningCombination> getWinningCombinationsToRuleOut () {
		return mOpponentWinningCombinationsToRuleOut;
	}

	public List<OccupancyPosition> getWinningCombinationsOccupancyPositionsToRuleOut() {
		return mWinningCombinationsOccupancyPositionsToRuleOut;
	}
	
	public static String constructAttributeName (Player pPlayer, Position pStartingPosition) {
		return pPlayer.getAttributeName() + "_" + pStartingPosition.getAttributeName();
	}
	
	public List<Position> getAffectedPositions() {
		return mAffectedPositions;
	}
	
}
