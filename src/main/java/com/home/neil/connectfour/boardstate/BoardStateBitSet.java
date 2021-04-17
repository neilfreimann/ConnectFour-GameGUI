package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.boardstate.old.InvalidMoveException;
import com.home.neil.knowledgebase.KnowledgeBaseException;
import com.home.neil.pool.IPool;

public class BoardState {
	public static final String CLASS_NAME = BoardState.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	IPool mPool = null;
	BoardState mPreviousBoardState = null;
	
	
	public BoardState (IPool pPool, BoardState pPreviousBoardState, Move pMove, boolean pEvaluation, String pLogContext) throws InvalidMoveException, KnowledgeBaseException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mPool = pPool;
		
		mPreviousBoardState = pPreviousBoardState;
		
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	
	
	public BoardState (IPool pPool, Move pMove, boolean pEvaluation, String pLogContext) throws InvalidMoveException, KnowledgeBaseException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mPool = pPool;
		
		mPreviousBoardState = null;
		
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
}
