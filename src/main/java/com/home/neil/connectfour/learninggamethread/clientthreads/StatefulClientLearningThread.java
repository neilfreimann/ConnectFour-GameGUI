package com.home.neil.connectfour.learninggamethread.clientthreads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.management.openmbean.OpenDataException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.appconfig.Connect4PropertiesConfiguration;
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.InvalidMoveException;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.performancemetrics.ThreadPerformanceMetricsMBean;


public abstract class StatefulClientLearningThread extends ClientLearningThread implements StatefulClientLearningThreadMBean {
	public static final String CLASS_NAME = StatefulClientLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILE_LOCATION_CONFIG_KEY = CLASS_NAME + ".FileLocation";

	private String mFileLocation = null;

	protected BoardState mCurrentBoardState = null;

	protected String mBreadCrumbs = null;

	protected String mCurrentMoveEvaluated = null;

	public StatefulClientLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pFileName, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);
		sLogger.trace("Entering");

		Connect4PropertiesConfiguration lConfig = Connect4PropertiesConfiguration.getInstance();

		mFileLocation = lConfig.getString(FILE_LOCATION_CONFIG_KEY) + pFileName;

		sLogger.trace("Exiting");
	}

	public String getCurrentMoveEvaluated() throws IOException, ConfigurationException {
		sLogger.trace("Entering");
		
		String lCurrentMoveEvaluated = mCurrentMoveEvaluated.split("\\|",2)[0];

		if (lCurrentMoveEvaluated.startsWith("0")) {
			lCurrentMoveEvaluated = lCurrentMoveEvaluated.substring(1);
		}

		List<String> lMovesList = new ArrayList<String>((lCurrentMoveEvaluated.length() + KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)
				/ KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile());

		for (int start = 0; start < lCurrentMoveEvaluated.length(); start += KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) {
			lMovesList.add(lCurrentMoveEvaluated.substring(start, Math.min(lCurrentMoveEvaluated.length(), start + KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile())));
		}

		String lReconstructedString = new String();

		for (Iterator<String> lIterator = lMovesList.iterator(); lIterator.hasNext();) {
			lReconstructedString += lIterator.next();
			if (lIterator.hasNext()) {
				lReconstructedString += "-";
			}
		}

		sLogger.trace("Exiting");
		return lReconstructedString;
	}

	public void readLastMoveEvaluated() throws IOException, InvalidMoveException, KnowledgeBaseException, NumberFormatException, ConfigurationException {
		sLogger.trace("Entering");

		File lFile = new File(mFileLocation);

		sLogger.debug("File Location: " + mFileLocation);

		if (!lFile.exists()) {
			mBreadCrumbs = "0";
			mCurrentBoardState = new BoardState(mKnowledgeBaseFilePool, BoardState.Move.OPPONENT_NOMOVE, true, mLogContext);
			sLogger.trace("Exiting");
			return;
		}

		sLogger.debug("File Exists: " + mFileLocation);

		BufferedReader lBR = new BufferedReader(new FileReader(lFile));

		String lLine = lBR.readLine();

		lBR.close();

		if (lLine == null || lLine.trim().length() == 0) {
			mBreadCrumbs = "0";
			mCurrentBoardState = new BoardState(mKnowledgeBaseFilePool, BoardState.Move.OPPONENT_NOMOVE, true, mLogContext);
			sLogger.trace("Exiting");
			return;
		}

		lLine = lLine.trim();

		sLogger.debug("Line Read, Current File Index: " + lLine);

		mBreadCrumbs = lLine;
		mCurrentBoardState = new BoardState(mKnowledgeBaseFilePool, BoardState.Move.OPPONENT_NOMOVE, true, mLogContext);

		sLogger.trace("Exiting");
	}

	public void writeLastMoveEvaluated(String pLastMoveEvaluated) {
		sLogger.trace("Entering");

		mCurrentMoveEvaluated = pLastMoveEvaluated;

		sLogger.debug("Line Cached, Last File Index: " + pLastMoveEvaluated);

		sLogger.trace("Exiting");
	}

	public void writeToDiskLastMoveEvaluated() throws IOException {
		sLogger.trace("Entering");

		File lFile = new File(mFileLocation);

		BufferedWriter lBW = new BufferedWriter(new FileWriter(lFile, false));

		lBW.write(mCurrentMoveEvaluated);
		lBW.newLine();

		lBW.flush();
		lBW.close();
		sLogger.debug("Line Written, Last File Index: " + mCurrentMoveEvaluated);

		sLogger.trace("Exiting");
	}

	public void run() {
		sLogger.trace("Entering");

		mThreadStartTime = new GregorianCalendar().getTimeInMillis();
		sLogger.debug("Thread: " + this.getName() + " is starting at " + mThreadStartTime);

		try {
			try {
				readLastMoveEvaluated();

				sLogger.debug("Last Board Evaluated: " + mBreadCrumbs);

				mStartTime = new GregorianCalendar().getTimeInMillis();
				mEndTime = mStartTime + mDurationToRunInMs;

				runLearningThread();

				writeToDiskLastMoveEvaluated();
			} catch (NumberFormatException e1) {
				sLogger.error("Number format exception on reading input file: " + mFileLocation);
				writeToDiskLastMoveEvaluated();
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
			} catch (IOException e1) {
				sLogger.error("IO exception on reading input file: " + mFileLocation);
				writeToDiskLastMoveEvaluated();
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
			} catch (InvalidMoveException e1) {
				sLogger.error("Invalid Move Exception on reading input file: " + mFileLocation);
				writeToDiskLastMoveEvaluated();
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
			} catch (KnowledgeBaseException e1) {
				sLogger.error("Knowledge Base Exception on reading input file: " + mFileLocation);
				writeToDiskLastMoveEvaluated();
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
			} catch (ConfigurationException e) {
				sLogger.error("Knowledge Base Exception on reading input file: " + mFileLocation);
				writeToDiskLastMoveEvaluated();
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
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

		mTransactionFinished = true;

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

	public abstract void runLearningThread();

}