package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;


public class EvaluationThreadInfo {


	public static final String CLASS_NAME = EvaluationThreadInfo.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0,
			CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	public BoardState mBoardState;
	
	private byte mEvalationThreadInfo = 0;

	public EvaluationThreadInfo(BoardState pBoardState) {
		super();
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		mBoardState = pBoardState;
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public byte getEvaluationThreadInfo() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return mEvalationThreadInfo;
	}


	public void setEvaluationThreadInfo(byte pEvaluationThreadInfo) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		mEvalationThreadInfo = pEvaluationThreadInfo;
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}
}
