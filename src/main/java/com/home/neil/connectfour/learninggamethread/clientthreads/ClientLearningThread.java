package com.home.neil.connectfour.learninggamethread.clientthreads;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.management.openmbean.OpenDataException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.performancemetrics.ThreadPerformanceMetricsMBean;

public abstract class ClientLearningThread extends Thread implements ClientLearningThreadMBean {
	public static final String CLASS_NAME = ClientLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected String mBeanName = null;

	protected OldBoardState mCurrentBoardState = null;

	protected static int sThreadNumber = 1;

	protected long mStartTime = 0;
	protected long mEndTime = 0;
	protected long mDurationToRunInMs = 0;

	private long mThrottleTime = 0;
	private boolean mPauseActive = false;
	private boolean mAbort = false;

	protected boolean mTransactionSuccessful = false;
	protected boolean mTransactionFinished = false;

	protected long mThreadStartTime = 0;
	protected long mThreadEndTime = 0;

	protected String mLogContext = null;

	KnowledgeBaseFilePool mKnowledgeBaseFilePool = null;
	
	public ClientLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException {
		super();
		sLogger.trace("Entering");

		mKnowledgeBaseFilePool = pKnowledgeBaseFilePool;
		mLogContext = pLogContext;

		renameThread(mLogContext);

		mDurationToRunInMs = pDurationToRunInMs;

		sLogger.trace("Exiting");
	}

	public String getBeanName() {
		sLogger.trace("Entering");

		sLogger.trace("Exiting");
		return mBeanName;
	}

	public abstract void renameThread(String pLogContext);

	public void run() {
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

	public void setAbort() {
		sLogger.trace("Entering");
		mAbort = true;
		sLogger.trace("Exiting");
	}

	public boolean getTerminate() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mAbort;
	}

	public boolean isTimeToAbort() {
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

		while (mPauseActive && !mAbort) {
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
		if ((mDurationToRunInMs != 0 && lCurrentTime > mEndTime) || mAbort) {
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

	public abstract void runLearningThread();

}
