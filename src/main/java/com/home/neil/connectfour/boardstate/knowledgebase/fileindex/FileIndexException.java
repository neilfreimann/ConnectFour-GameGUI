package com.home.neil.connectfour.boardstate.knowledgebase.fileindex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.GameException;

public class FileIndexException extends GameException {
	private static final long serialVersionUID = -3575154056313779593L;
	public static final String CLASS_NAME = FileIndexException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public FileIndexException () {
		super ("FileIndexException occurred.");
	}
	
	public FileIndexException (Exception pE) {
		super ("FileIndexException occurred.  Underlying "+ pE.getClass().getName(), pE);
	}
	
	public FileIndexException (String pMessage, Exception pE) {
		super (pMessage, pE);
	}
	
	public FileIndexException (String pMessage) {
		super (pMessage);
	}       
}