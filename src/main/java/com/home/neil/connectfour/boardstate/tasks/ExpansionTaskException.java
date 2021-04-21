package com.home.neil.connectfour.boardstate.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.task.TaskException;

public class ExpansionTaskException extends TaskException {
	private static final long serialVersionUID = -2774648241511871601L;
	public static final String CLASS_NAME = ExpansionTaskException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public ExpansionTaskException () {
		super ("ExpansionTaskException occurred.");
	}
	
	public ExpansionTaskException (Exception pE) {
		super ("ExpansionTaskException occurred.  Underlying "+ pE.getClass().getName(), pE);
	}
	
	public ExpansionTaskException (String pMessage, Exception pE) {
		super (pMessage, pE);
	}
}
