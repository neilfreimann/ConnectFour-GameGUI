package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.GameException;

public class InvalidMoveException extends GameException {
	private static final long serialVersionUID = -7873760239082493278L;
	public static final String CLASS_NAME = InvalidMoveException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public InvalidMoveException () {
		super ("InvalidMoveException occurred.");
	}
	
	public InvalidMoveException (Exception pE) {
		super ("InvalidMoveException occurred.  Underlying "+ pE.getClass().getName(), pE);
	}
	
	public InvalidMoveException (String pMessage, Exception pE) {
		super (pMessage, pE);
	}
}
