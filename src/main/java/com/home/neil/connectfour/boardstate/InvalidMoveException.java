package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;


public class InvalidMoveException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7873760239082493278L;
	
	public static final String CLASS_NAME = InvalidMoveException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	public InvalidMoveException () {
		super ();
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACELOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}
	
    public Throwable fillInStackTrace()
    {
        return this;
    }
}
