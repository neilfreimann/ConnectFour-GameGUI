package com.home.neil.connectfour.boardstate;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Direction {
	public static final String CLASS_NAME = Direction.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private boolean mDirection [] = new boolean [] {false,false};
	
	private String mDirectionString;

	private Direction (boolean [] pDirection, String pDirectionString) {
		mDirection = pDirection;
		mDirectionString = pDirectionString;
	}
	
	public static final Direction VERTICAL = new Direction (new boolean [] {false, false}, "V");
	public static final Direction HORIZONTAL = new Direction (new boolean [] {false, true}, "H");
	public static final Direction DIAGONAL = new Direction (new boolean [] {true, false}, "D");
	public static final Direction OPPOSITE = new Direction (new boolean [] {true, true}, "O");
	
	public Direction getDirection (boolean [] pDirection) {
		if (!pDirection [0] && !pDirection[1]) {
			return VERTICAL;
		} else if (!pDirection [0] && pDirection[1]) {
			return HORIZONTAL;
		} else if (pDirection [0] && !pDirection[1]) {
			return DIAGONAL;
		} else {
			return OPPOSITE;
		}
	}

	public boolean [] getBoolean () {
		return mDirection;
	}
	
	public BitSet getDirectionBooleanEncoding () {
		BitSet lBitSet = new BitSet (2);
		lBitSet.set(0, mDirection[0]);
		lBitSet.set(1, mDirection[1]);
		return lBitSet;
	}
	
	public String getDirectionString () {
		return mDirectionString;
	}
	
}
