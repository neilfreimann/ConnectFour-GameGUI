package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Position extends BoardAttribute {
	public static final String CLASS_NAME = Position.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static int sColumnBooleanEncodingSize = 0;
	private static int sRowBooleanEncodingSize = 0;

	private static HashMap <String, Position> sPositions = new HashMap <> ();
	
	private BitSet mColumnBooleanEncoding = null;
	private BitSet mRowBooleanEncoding = null;
	private int mColumn = 0;
	private int mRow = 0;
	private String mPositionString;

	static {
		int lColumns = sConnectFourBoardConfig.getNumberOfColumns();
		while (lColumns > 0) {
			lColumns = lColumns >> 1;
			sColumnBooleanEncodingSize++;
		}

		int lRows = sConnectFourBoardConfig.getNumberOfRows();
		while (lRows > 0) {
			lRows = lRows >> 1;
			sRowBooleanEncodingSize++;
		}
		
		for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
			for (int j = 0; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
				Position lPosition = new Position (i, j);
				sPositions.put(lPosition.getPositionString(), lPosition);
			}
		}

	}

	public static Position getPosition (int pColumn, int pRow) {
		String lPositionString = constructPositionString(pColumn, pRow);
		return getPosition (lPositionString);
	}
	
	public static Position getPosition (String pPositionString) {
		return sPositions.get(pPositionString);
	}
	
	
	public static String constructPositionString(int pColumn, int pRow) {
		return String.valueOf(pColumn) + "_" + String.valueOf(pRow);
	}


	private Position(int pColumn, int pRow) {
		mColumn = pColumn;
		mRow = pRow;
		
		mColumnBooleanEncoding = encodeToBitSet(pColumn, sColumnBooleanEncodingSize);
		mRowBooleanEncoding = encodeToBitSet(pRow, sRowBooleanEncodingSize);

		mPositionString = constructPositionString(pColumn, pRow);

	}

	public BitSet getColumnBooleanEncoding() {
		return mColumnBooleanEncoding;
	}

	public BitSet getRowBooleanEncoding() {
		return mRowBooleanEncoding;
	}

	public int getColumn() {
		return mColumn;
	}

	public int getRow() {
		return mRow;
	}

	public String getPositionString() {
		return mPositionString;
	}
	
	public static Collection <Position> getPositions () {
		return sPositions.values();
	}

}
