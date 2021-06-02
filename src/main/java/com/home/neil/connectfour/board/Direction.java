package com.home.neil.connectfour.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Direction extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Direction.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public Direction (int pDirectionValue, String pDirectionString) {
		super (pDirectionValue, 3, pDirectionString);
	}
}
