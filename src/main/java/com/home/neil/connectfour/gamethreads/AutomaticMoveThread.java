package com.home.neil.connectfour.gamethreads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.gui.Connect4GUI;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.learninggamethread.givenmove.GivenMoveFixedDurationLearningThread;


public abstract class AutomaticMoveThread extends GameThread implements AutomaticMoveThreadMBean {
	public static final String CLASS_NAME = AutomaticMoveThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected String mBeanName = null;
	
	protected boolean mTransactionSuccessful = false;
	protected boolean mTransactionFinished = false;

	protected long mDurationPerMoveInMs = 600000;

	protected OldBoardState mCurrentBoardState = null;
	protected OldBoardState.Move mNextMove = null;

	protected GivenMoveFixedDurationLearningThread mGameLearningThread;
	
	protected Connect4GUI mGUI = null;

	protected long mThreadStartTime = 0;
	protected long mThreadEndTime = 0;

	protected static int sThreadNumber = 0;

	protected String mLogContext = null;
	
	protected KnowledgeBaseFilePool mKnowledgeBaseFilePool = null;

	public AutomaticMoveThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pCurrentBoardState, long pDurationPerMoveInMs, Connect4GUI pGUI, String pContext) {
		sLogger.trace("Entering");
				
		renameThread(pContext);
		
		mKnowledgeBaseFilePool = pKnowledgeBaseFilePool;
		
		mCurrentBoardState = pCurrentBoardState;

		mDurationPerMoveInMs = pDurationPerMoveInMs;

		mGUI = pGUI;
		
		sLogger.trace("Exiting");
	}

	public String getBeanName () {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mBeanName;
	}
	
	public OldBoardState.Move getNextMove() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mNextMove;
	}

	public boolean isTransactionSuccessful() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mTransactionSuccessful;
	}

	public void setTerminate () {
		sLogger.trace("Entering");
		if (mGameLearningThread != null) {
			mGameLearningThread.setTerminate();
		}
		sLogger.trace("Exiting");
	}
	
	@Override
	public boolean getTerminate() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mGameLearningThread.getTerminate();
	}


	public abstract void renameThread (String pContext);

	public abstract void run ();
}
