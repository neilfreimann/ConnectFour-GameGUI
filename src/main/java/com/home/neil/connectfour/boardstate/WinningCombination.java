package com.home.neil.connectfour.boardstate;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;

public class WinningCombination extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = OccupancyPosition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private OccupancyPosition mStartingOccupancyPosition = null;
	private Direction mDirection = null;
	private List <OccupancyPosition> mOccupancyPositions = null;
	private int mReferenceId = 0;
	
	public WinningCombination (OccupancyPosition pOccupancyPosition, Direction pDirection, List <OccupancyPosition> pOccupancyPositions, int pReferenceId) {
		super (new GameAttribute [] {pOccupancyPosition, pDirection}, constructAttributeName(pOccupancyPosition, pDirection));
		mStartingOccupancyPosition = pOccupancyPosition;
		mDirection = pDirection;
		mOccupancyPositions = pOccupancyPositions;
		mReferenceId = pReferenceId;
		
	}
	
	public OccupancyPosition getStartingOccupancyPosition () {
		return mStartingOccupancyPosition;
	}

	public Direction getDirection () {
		return mDirection;
	}
	
	public List <OccupancyPosition> getOccupancyPositions () {
		return mOccupancyPositions;
	}
	
	public static String constructAttributeName (OccupancyPosition pStartingOccupancyPosition, Direction pDirection) {
		return pStartingOccupancyPosition.getAttributeName() + "_" + pDirection.getAttributeName();
	}

	public int getReferenceId() {
		return mReferenceId;
	}
	
	
}
