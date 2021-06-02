package com.home.neil.connectfour.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.GameException;

public class InvalidMoveException extends Exception {
	private static final long serialVersionUID = -7873760239082493278L;
	public static final String CLASS_NAME = InvalidMoveException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public InvalidMoveException () {
		super ("InvalidMoveException occurred.", null, false, false);
		sLogger.debug ("InvalidMoveException occurred.");
	}
	
}
