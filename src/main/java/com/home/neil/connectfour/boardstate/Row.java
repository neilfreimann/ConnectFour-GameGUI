package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Row extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Row.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private int mRow = -1;
	
	public Row (int pRow) {
		super (pRow, sConnectFourBoardConfig.getNumberOfRows(), constructAttributeName(pRow));
		mRow = pRow;
	}
	
	public int getRow () {
		return mRow;
	}
	
	public static String constructAttributeName (int pRow) {
		return "R" + pRow;
	}
}
