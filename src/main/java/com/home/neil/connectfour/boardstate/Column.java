package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Column  extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Column.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private int mColumn = -1;
	
	public Column (int pColumn) {
		super (pColumn, sConnectFourBoardConfig.getNumberOfColumns(), constructAttributeName(pColumn));
		mColumn = pColumn;
	}
	
	public int getColumn () {
		return mColumn;
	}
	
	public static String constructAttributeName (int pColumn) {
		return "C" + pColumn;
	}
}
