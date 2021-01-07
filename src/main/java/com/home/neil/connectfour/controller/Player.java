package com.home.neil.connectfour.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.openmbean.OpenDataException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.performancemetrics.ThreadPerformanceMetricsMBean;



public abstract class Player extends Thread implements ActionInterface {
	public static final String CLASS_NAME = Game.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + Game.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static int sThreadNumber = 1;
	protected String mBeanName = null;
	protected String mLogContext = null;

	protected GameController mGameController = null;

	protected BoardState mCurrentBoardState = null;
	
	protected PLAYER mPlayerType = null;

	protected long mStartTime = 0;
	protected long mEndTime = 0;
	protected long mDurationToRunInMs = 0;

	private long mThrottleTime = 0;
	private boolean mPauseActive = false;
	private boolean mTerminate = false;

	protected boolean mTransactionSuccessful = false;
	protected boolean mTransactionFinished = false;

	protected long mThreadStartTime = 0;
	protected long mThreadEndTime = 0;
	
	public static enum PLAYER {
		HUMAN_XMOVE (0, "X-Human"),
		HUMAN_OMOVE (1, "O-Human"),
		COMPUTER_XMOVE (2, "X-Computer"),
		COMPUTER_OMOVE (3, "O-Computer");
		
		private int mPlayer = 0;
		private String mPlayerString = new String();

		PLAYER (int pPlayer, String pPlayerString) {
			mPlayer = pPlayer;
			mPlayerString = pPlayerString;
		}

		public String getPlayerString () {
			return mPlayerString;
		}

		public int getPlayer() {
			return mPlayer;
		}
	}
	

	
	public Player (PLAYER pPlayer, GameController pGameController) {
		sLogger.trace("Entering");

		mPlayerType = pPlayer;
		mGameController = pGameController;
		
		sLogger.trace("Exiting");
	}
	
	public void run () {
		sLogger.trace("Entering");

		mThreadStartTime = new GregorianCalendar().getTimeInMillis();
		sLogger.debug("Thread: " + this.getName() + " is starting at " + mThreadStartTime);

		try {
			mStartTime = new GregorianCalendar().getTimeInMillis();
			mEndTime = mStartTime + mDurationToRunInMs;
			
			runLearningThread();
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
	

	public String getBeanName() {
		sLogger.trace("Entering");

		sLogger.trace("Exiting");
		return mBeanName;
	}



	public void setThrottle(long pThrottleValue) {
		sLogger.trace("Entering");
		mThrottleTime = pThrottleValue;
		sLogger.trace("Exiting");
	}

	public long getThrottle() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mThrottleTime;
	}

	public void togglePause() {
		sLogger.trace("Entering");
		if (mPauseActive) {
			mPauseActive = false;
		} else {
			mPauseActive = true;
		}
		sLogger.trace("Exiting");

	}

	public boolean getPause() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mPauseActive;
	}

	public boolean isTransactionSuccessful() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mTransactionSuccessful;
	}

	public void setTerminate() {
		sLogger.trace("Entering");
		mTerminate = true;
		sLogger.trace("Exiting");
	}

	public boolean getTerminate() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mTerminate;
	}

	public boolean isTimeToTerminate() {
		sLogger.trace("Entering");
		long lCurrentTime = new GregorianCalendar().getTimeInMillis();

		if (mThrottleTime > 0) {
			try {
				Thread.sleep(mThrottleTime);
			} catch (InterruptedException eIE) {
				sLogger.warn("Unanticipated Interrupt exception occurred!");
				
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eIE.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.debug("StackTrace: " + lSW);
			}
		}

		while (mPauseActive && !mTerminate) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException eIE) {
				sLogger.warn("Unanticipated Interrupt exception occurred!");
				
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eIE.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.debug("StackTrace: " + lSW);
			}
		}

		lCurrentTime = new GregorianCalendar().getTimeInMillis();
		if ((mDurationToRunInMs != 0 && lCurrentTime > mEndTime) || mTerminate) {
			sLogger.trace("Exiting");
			return true;
		} else {
			sLogger.trace("Exiting");
			return false;
		}
	}

	public String getThreadTimeStatistics() {
		sLogger.trace("Entering");

		String lReturnString = new String();
		if (mDurationToRunInMs == 0) {
			lReturnString = "Thread " + this.getName()
					+ " is not configured to terminate at any time.  To Stop Thread, you must manually terminate it in JConsole using setTerminate()";
		} else {
			DateFormat lFormatter = new SimpleDateFormat("HH:mm:ss:SSS");

			Date lStartDate = new Date(mStartTime);
			String lStartDateFormatted = lFormatter.format(lStartDate);

			Date lEndDate = new Date(mEndTime);
			String lEndDateFormatted = lFormatter.format(lEndDate);

			long lCurrentTime = new GregorianCalendar().getTimeInMillis();
			long lRemainingTime = mEndTime - lCurrentTime;

			Date lTimeRemainingDate = new Date(lRemainingTime);
			String lRemainingTimeFormatted = lFormatter.format(lTimeRemainingDate);

			lReturnString = "Thread " + this.getName() + " Start Time: " + lStartDateFormatted + " End Time: " + lEndDateFormatted + " Remaining Time: "
					+ lRemainingTimeFormatted;
		}

		sLogger.trace("Exiting");

		return lReturnString;
	}

	public abstract void renameThread(String pLogContext);

	public abstract void runLearningThread();
	
}
