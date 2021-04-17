package com.home.neil.game;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;

public class GameException extends Exception {

	private static final long serialVersionUID = -31808105812604868L;
	public static final String CLASS_NAME = GameException.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public GameException() {
		super("GameException occurred.");
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);

		StringWriter lSW = new StringWriter();
		PrintWriter lPW = new PrintWriter(lSW);
		this.printStackTrace(lPW);
		lSW.toString(); // stack trace as a string
		sLogger.error("StackTrace: {} ", lSW);

		sLogger.error("GameException occurred.");
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
	}

	public GameException(String pMessage) {
		super(pMessage);
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);

		StringWriter lSW = new StringWriter();
		PrintWriter lPW = new PrintWriter(lSW);
		this.printStackTrace(lPW);
		lSW.toString(); // stack trace as a string
		sLogger.error("StackTrace: {} ", lSW);

		sLogger.error("{} occurred.", this.getClass().getSimpleName());
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
	}

	public GameException(Exception pE) {
		super("GameException occurred.  Underlying Exception: " + pE.getClass().getName());
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		StringWriter lSW = new StringWriter();
		PrintWriter lPW = new PrintWriter(lSW);
		pE.printStackTrace(lPW);
		lSW.toString(); // stack trace as a string
		sLogger.error("StackTrace: {}", lSW);
		sLogger.info("{} occurred: Underlying Exception: {} Message: {}", this.getClass().getSimpleName(), pE.getClass().getName(), pE.getMessage());
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
	}

	public GameException(String pMessage, Exception pE) {
		super(pMessage);
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		StringWriter lSW = new StringWriter();
		PrintWriter lPW = new PrintWriter(lSW);
		pE.printStackTrace(lPW);
		lSW.toString(); // stack trace as a string
		sLogger.error("StackTrace: {}", lSW);
		sLogger.info("{} occurred: Underlying Exception: {} Message: {}: ", this.getClass().getSimpleName(), pE.getClass().getName(), pE.getMessage());
		sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
	}

}

