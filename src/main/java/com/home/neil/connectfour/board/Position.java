package com.home.neil.connectfour.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;

public class Position extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Position.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private Column mColumn = null;
	private Row mRow = null;
	private int mReferenceId = 0;
	
	public Position (Column pColumn, Row pRow, int pReferenceId) {
		super (new GameAttribute [] {pColumn, pRow}, constructAttributeName(pColumn, pRow));
		mRow = pRow;
		mColumn = pColumn;
		mReferenceId = pReferenceId;
	}
	
	public Row getRow () {
		return mRow;
	}

	public Column getColumn () {
		return mColumn;
	}
	
	public int getReferenceId () {
		return mReferenceId;
	}
	
	public static String constructAttributeName (Column pColumn, Row pRow) {
		return pColumn.getAttributeName() + "_" + pRow.getAttributeName();
	}
	
	

}
