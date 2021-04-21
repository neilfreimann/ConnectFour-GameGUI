package com.home.neil.connectfour.gamethreads;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Random;

import javax.management.openmbean.OpenDataException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.home.neil.connectfour.gui.Connect4GUI;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.learninggamethread.givenmove.GivenMoveFixedDurationLearningThread;
import com.home.neil.connectfour.managers.AutomaticMoveThreadManager;
import com.home.neil.connectfour.performancemetrics.ThreadPerformanceMetricsMBean;

import old.com.home.neil.connectfour.boardstate.OldBoardState;
import old.com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;

public class RandomizedAutomaticMoveThread extends AutomaticMoveThread {
	public static final String CLASS_NAME = RandomizedAutomaticMoveThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME_PREFIX = PACKAGE_NAME + ":type=" + BestMoveAutomaticMoveThread.class.getSimpleName();
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		Thread.currentThread().setName(RandomizedAutomaticMoveThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = RandomizedAutomaticMoveThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}

	public RandomizedAutomaticMoveThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pCurrentBoardState, long pDurationPerMoveInMs, Connect4GUI pGUI, String pLogContext)
			throws ConfigurationException, IOException {
		super(pKnowledgeBaseFilePool, pCurrentBoardState, pDurationPerMoveInMs, pGUI, pLogContext);
		sLogger.trace("Entering");

		mBeanName = MBEAN_NAME_PREFIX + sThreadNumber;

		AutomaticMoveThreadManager lAutomaticMoveThreadManager = AutomaticMoveThreadManager.getInstance();

		lAutomaticMoveThreadManager.registerAutomaticMoveThread(this, mBeanName);

		sLogger.trace("Exiting");
	}

	public static synchronized RandomizedAutomaticMoveThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pCurrentBoardState, long pDurationToRunInMs, Connect4GUI pGUI,
			String pLogContext) throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		RandomizedAutomaticMoveThread lInstance = new RandomizedAutomaticMoveThread(pKnowledgeBaseFilePool, pCurrentBoardState, pDurationToRunInMs, pGUI, pLogContext);

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

					Random lRand = new Random();
					int lRandomNum = lRand.nextInt(lSubBoardStates.size());
					mNextMove = lSubBoardStates.get(lRandomNum).getMove();
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
}
