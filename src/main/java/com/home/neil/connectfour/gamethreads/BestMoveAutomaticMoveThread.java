package com.home.neil.connectfour.gamethreads;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.management.openmbean.OpenDataException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.boardstate.old.expansiontask.ExpansionTask;
import com.home.neil.connectfour.gui.Connect4GUI;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.learninggamethread.givenmove.GivenMoveFixedDurationLearningThread;
import com.home.neil.connectfour.managers.AutomaticMoveThreadManager;
import com.home.neil.connectfour.performancemetrics.ThreadPerformanceMetricsMBean;

public class BestMoveAutomaticMoveThread extends AutomaticMoveThread {
	public static final String CLASS_NAME = BestMoveAutomaticMoveThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME_PREFIX = PACKAGE_NAME + ":type=" + BestMoveAutomaticMoveThread.class.getSimpleName();
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		Thread.currentThread().setName(BestMoveAutomaticMoveThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = BestMoveAutomaticMoveThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}

	private BestMoveAutomaticMoveThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pCurrentBoardState, long pDurationPerMoveInMs, Connect4GUI pGUI, String pContext)
			throws ConfigurationException, IOException {
		super(pKnowledgeBaseFilePool, pCurrentBoardState, pDurationPerMoveInMs, pGUI, pContext);
		sLogger.trace("Entering");

		mBeanName = MBEAN_NAME_PREFIX;

		AutomaticMoveThreadManager lAutomaticMoveThreadManager = AutomaticMoveThreadManager.getInstance();

		lAutomaticMoveThreadManager.registerAutomaticMoveThread(this, mBeanName);

		sLogger.trace("Exiting");
	}

	public static synchronized BestMoveAutomaticMoveThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pCurrentBoardState, long pDurationToRunInMs, Connect4GUI pGUI, String pContext)
			throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		BestMoveAutomaticMoveThread lInstance = new BestMoveAutomaticMoveThread(pKnowledgeBaseFilePool, pCurrentBoardState, pDurationToRunInMs, pGUI, pContext);

		sLogger.trace("Exiting");
		return lInstance;
	}

	public void run() {
		sLogger.trace("Entering");

		mThreadStartTime = new GregorianCalendar().getTimeInMillis();
		sLogger.debug("Thread: " + this.getName() + " is starting at " + mThreadStartTime);

		try {
			sLogger.info("Current Move is: " + mCurrentBoardState.getFileIndexString() + " Score: " + mCurrentBoardState.getMoveScore().getMoveScore());

			ExpansionTask lExpandNodeThread = null;
			try {
				mGameLearningThread = GivenMoveFixedDurationLearningThread.getInstance(mKnowledgeBaseFilePool, mCurrentBoardState, mDurationPerMoveInMs, mLogContext);
				mGameLearningThread.start();
				mGameLearningThread.join();

				lExpandNodeThread = new ExpansionTask(mCurrentBoardState, mLogContext);
				boolean lSuccess = lExpandNodeThread.executeTask();

			} catch (ConfigurationException eCE) {
				sLogger.error("Configuration Exception Occurred!");
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eCE.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.error("StackTrace: " + lSW);
				mTransactionSuccessful = false;
			} catch (InterruptedException eIE) {
				sLogger.error("Unanticipated Interrupted Exception Occurred!");
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eIE.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.error("StackTrace: " + lSW);
				mTransactionSuccessful = false;
			} catch (IOException eIO) {
				sLogger.error("IO Exception Occurred! " + eIO.getMessage());
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eIO.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.error("StackTrace: " + lSW);
				mTransactionSuccessful = false;
			}

			if (lExpandNodeThread != null) {
				ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

				sLogger.debug("SubBoardStates size : " + lSubBoardStates.size());

				if (lSubBoardStates.size() <= 0) {
					sLogger.error("Somethings wrong... BoardStates is continue and I can't");
					mTransactionSuccessful = false;
				} else {
					for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
						OldBoardState lCurrentBoardState = lIterator.next();
						sLogger.error("SubMoves: " + lCurrentBoardState.getFileIndexString() + " Score: " + lCurrentBoardState.getMoveScore().getMoveScore());
					}

					sortSubBoardState(lSubBoardStates);
					mNextMove = lSubBoardStates.get(0).getMove();
					mGUI.performAutomaticMove(mNextMove);
					mTransactionSuccessful = true;
				}
			} else {
				mTransactionSuccessful = false;
			}

		} catch (Exception eE) {
			sLogger.error("Unanticipated Exception occurred during Thread Execution!");
			sLogger.error("Exception Message: " + eE.getMessage());

			StringWriter lSW = new StringWriter();
			PrintWriter lPW = new PrintWriter(lSW);
			eE.printStackTrace(lPW);
			lSW.toString(); // stack trace as a string
			sLogger.error("StackTrace: " + lSW);

			mTransactionSuccessful = false;
		}

		mThreadEndTime = new GregorianCalendar().getTimeInMillis();
		sLogger.info("Thread: " + this.getName() + " is ending at " + mThreadEndTime);
		long lDuration = mThreadEndTime - mThreadStartTime;

		try {
			ThreadPerformanceMetricsMBean lThreadPerformanceMetricsMBean = ThreadPerformanceMetricsMBean.getInstance();
			lThreadPerformanceMetricsMBean
					.updateThreadStatistics(this.getClass().getSimpleName(), "run()", mThreadStartTime, lDuration, mTransactionSuccessful);
		} catch (OpenDataException eODE) {
			sLogger.error("Open Data Exception occurred during Thread Execution!");
			sLogger.error("Exception Message: " + eODE.getMessage());

			StringWriter lSW = new StringWriter();
			PrintWriter lPW = new PrintWriter(lSW);
			eODE.printStackTrace(lPW);
			lSW.toString(); // stack trace as a string
			sLogger.error("StackTrace: " + lSW);
		} catch (Exception eE) {
			sLogger.error("Exception Message: " + eE.getMessage());

			StringWriter lSW = new StringWriter();
			PrintWriter lPW = new PrintWriter(lSW);
			eE.printStackTrace(lPW);
			lSW.toString(); // stack trace as a string
			sLogger.error("StackTrace: " + lSW);
		}

		sLogger.trace("Exiting");

	}

	public void sortSubBoardState(List<OldBoardState> pBoardState) {
		sLogger.trace("Entering");

		sLogger.debug("Starting Sorting Phase");
		int lVirtualDepth = mCurrentBoardState.getFileIndexString().length();

		int lDepthModulus = lVirtualDepth % 2;

		if (lDepthModulus == 1) { // current move is opponent move, reorder
									// sub
									// moves by highest score
			Collections.sort(pBoardState, new Comparator<OldBoardState>() {
				public int compare(OldBoardState p1, OldBoardState p2) {
					byte lP1MoveScore = p1.getMoveScore().getMoveScore();
					byte lP2MoveScore = p2.getMoveScore().getMoveScore();
					if (lP2MoveScore > lP1MoveScore)
						return 1;
					else if (lP2MoveScore < lP1MoveScore)
						return -1;
					else
						return 0;
				}
			});
		} else { // current move is a self move, reorder sub moves by lowest
					// score
			Collections.sort(pBoardState, new Comparator<OldBoardState>() {
				public int compare(OldBoardState p1, OldBoardState p2) {
					byte lP1MoveScore = p1.getMoveScore().getMoveScore();
					byte lP2MoveScore = p2.getMoveScore().getMoveScore();
					if (lP2MoveScore < lP1MoveScore)
						return 1;
					else if (lP2MoveScore > lP1MoveScore)
						return -1;
					else
						return 0;
				}
			});
		}
		sLogger.debug("Sorting Phase Complete");
		sLogger.trace("Exiting");
	}

}
