package com.home.neil.connectfour.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public interface ActionInterface {
	public static final String CLASS_NAME = ActionInterface.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + ActionInterface.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public abstract void submitMove (Player pPlayer, int lMove);

	public abstract void undoMove (Player pPlayer);

	public abstract void resign (Player pPlayer);
}
