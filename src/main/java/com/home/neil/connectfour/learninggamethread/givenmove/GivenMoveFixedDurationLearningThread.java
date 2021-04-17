package com.home.neil.connectfour.learninggamethread.givenmove;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.home.neil.connectfour.boardstate.old.OldBoardState;
import com.home.neil.connectfour.boardstate.old.expansiontask.ExpansionTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.FixedDurationLearningThread;
import com.home.neil.connectfour.learninggamethread.FixedDurationLearningThreadMBean;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;



public class GivenMoveFixedDurationLearningThread extends FixedDurationLearningThread implements FixedDurationLearningThreadMBean {
	public static final String CLASS_NAME = GivenMoveFixedDurationLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME_PREFIX = PACKAGE_NAME + ":type=" + GivenMoveFixedDurationLearningThread.class.getSimpleName();
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public static final int MAX_EXHAUSTIVE_DEPTH = 3;

	public static final long MAX_MEMORY_FOOTPRINT = 250000;

	private OldBoardState mInitialBoardState = null;

	private String mCurrentMoveEvaluated = null;

	private int mStartingDepth = 0;
	private int mEndingDepth = 0;

	private LinkedList<OldBoardState> mResultantBoardStates = null;
	private LinkedList<OldBoardState> mResultantBoardStatesToEvaluate = null;

	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		setName(GivenMoveFixedDurationLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = GivenMoveFixedDurationLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}

	private GivenMoveFixedDurationLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pInitialBoardState, long pDurationToRunInMs, String pLogContext) throws ConfigurationException,
			IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);
		sLogger.trace("Entering");

		mInitialBoardState = pInitialBoardState;
		mResultantBoardStates = new LinkedList<OldBoardState>();
		mResultantBoardStatesToEvaluate = new LinkedList<OldBoardState>();

		mBeanName = MBEAN_NAME_PREFIX + sThreadNumber;

		FixedDurationLearningThreadManager lFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();

		lFixedDurationLearningThreadManager.registerFixedDurationLearningThread(this, mBeanName);

		sLogger.trace("Exiting");
	}

	public static synchronized GivenMoveFixedDurationLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, OldBoardState pInitialBoardState, long pDurationToRunInMs, String pLogContext)
			throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		GivenMoveFixedDurationLearningThread lInstance = new GivenMoveFixedDurationLearningThread(pKnowledgeBaseFilePool, pInitialBoardState, pDurationToRunInMs, pLogContext);

		sLogger.trace("Exiting");
		return lInstance;
	}

	public void runLearningThread() {
		sLogger.trace("Entering");

		mStartingDepth = mInitialBoardState.getFileIndexString().length();
		mEndingDepth = mStartingDepth + MAX_EXHAUSTIVE_DEPTH;

		try {
			performExhaustiveSearch(mInitialBoardState);
		} catch (KnowledgeBaseException eKBE) {
			sLogger.error("Knowledge Base exception occurred when performing exhaustive search");
			sLogger.error("Message: " + eKBE.getMessage());

			StringWriter lSW = new StringWriter();
			PrintWriter lPW = new PrintWriter(lSW);
			eKBE.printStackTrace(lPW);
			lSW.toString(); // stack trace as a string
			sLogger.debug("StackTrace: " + lSW);
			
			sLogger.trace("Exiting");
			mTransactionSuccessful = false;
			return;
		}

		sLogger.debug("First Exhaustive Search Phase Complete");

		while (!isTimeToTerminate()) {

			mResultantBoardStatesToEvaluate = mResultantBoardStates;
			mResultantBoardStates = new LinkedList<OldBoardState>();

			sortSubBoardState(mResultantBoardStatesToEvaluate);

			if (isTimeToTerminate()) {
				sLogger.info("Timer Exhausted");
				break;
			}

			while (!mResultantBoardStatesToEvaluate.isEmpty()) {
				sLogger.debug("Starting Exhaustive Search Phase");

				OldBoardState lCurrentBoardState = mResultantBoardStatesToEvaluate.pop();

				mStartingDepth = lCurrentBoardState.getFileIndexString().length();
				mEndingDepth = mStartingDepth + MAX_EXHAUSTIVE_DEPTH;

				try {
					performExhaustiveSearch(lCurrentBoardState);
				} catch (KnowledgeBaseException eKBE) {
					sLogger.error("Knowledge Base exception occurred when performing exhaustive search");
					sLogger.error("Message: " + eKBE.getMessage());

					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					eKBE.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string
					sLogger.debug("StackTrace: " + lSW);
					
					sLogger.trace("Exiting");
					mTransactionSuccessful = false;
					return;
				}

				sLogger.debug("Exhaustive Search Phase Complete");

				if (isTimeToTerminate()) {
					sLogger.error("Timer Exhausted");
					break;
				}

			}
		}
		sLogger.debug("Timer Exhausted");

		mTransactionSuccessful = true;
		
		sLogger.trace("Exiting");
	}

	private void performExhaustiveSearch(OldBoardState pBoardStateToExpand) throws KnowledgeBaseException {
		sLogger.trace("Entering");

		mCurrentMoveEvaluated = pBoardStateToExpand.getFileIndexString();

		if (pBoardStateToExpand.getFileIndexString().length() > mEndingDepth) {

			if (getThreadMemoryFootprint() < MAX_MEMORY_FOOTPRINT) {
				mResultantBoardStates.add(pBoardStateToExpand);
			}
			sLogger.trace("Exiting");
			return;
		}

		ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
		boolean lSuccess = lExpandNodeThread.executeTask();

		if (!lSuccess) {
			mTransactionSuccessful = false;
			sLogger.error("Exhaustive Search was not successful");
			sLogger.trace("Exiting");
			throw new KnowledgeBaseException();
		}

		ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

		if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {
			for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				OldBoardState lSubBoardState = lIterator.next();
				if (isTimeToTerminate()) {
					sLogger.debug("Timer Exhausted");
					break;
				}
				performExhaustiveSearch(lSubBoardState);
			}
		}

		sLogger.trace("Exiting");
	}

	public void sortSubBoardState(List<OldBoardState> pBoardState) {
		sLogger.trace("Entering");

		sLogger.debug("Starting Sorting Phase");
		int lVirtualDepth = mEndingDepth + 1;

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

	@Override
	public long getThreadMemoryFootprint() {

		return mResultantBoardStates.size() + mResultantBoardStatesToEvaluate.size();
	}


	public String getCurrentMoveEvaluated() throws IOException, ConfigurationException {
		sLogger.trace("Entering");

		List<String> lMovesList = new ArrayList<String>((mCurrentMoveEvaluated.length() + KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)
				/ KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile());

		for (int start = 0; start < mCurrentMoveEvaluated.length(); start += KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) {
			lMovesList.add(mCurrentMoveEvaluated.substring(start,
					Math.min(mCurrentMoveEvaluated.length(), start + KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile())));
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

}
