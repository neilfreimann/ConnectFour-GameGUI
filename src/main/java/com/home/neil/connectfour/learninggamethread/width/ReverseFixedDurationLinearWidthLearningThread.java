package com.home.neil.connectfour.learninggamethread.width;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.StatefulFixedDurationLearningThread;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;



public class ReverseFixedDurationLinearWidthLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = ReverseFixedDurationLinearWidthLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + ReverseFixedDurationLinearWidthLearningThread.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_WIDTH = ReverseFixedDurationLinearWidthLearningThread.class.getSimpleName() + ".dat";

	private int mStartingDepth = 0;
	private int mEndingDepth = 0;

	private String mStartingIndex = null;
	
	private static ReverseFixedDurationLinearWidthLearningThread sInstance = null;
	
	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		setName(ReverseFixedDurationLinearWidthLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = ReverseFixedDurationLinearWidthLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}



	public static synchronized ReverseFixedDurationLinearWidthLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		if (sInstance == null) {
			sInstance = new ReverseFixedDurationLinearWidthLearningThread(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread (sInstance,sInstance.getBeanName());
		}
		
		sLogger.trace("Exiting");
		return sInstance;
	}


	private ReverseFixedDurationLinearWidthLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, FILENAME_FIXED_DURATION_LINEAR_WIDTH, pLogContext);
		mBeanName = MBEAN_NAME;
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
	}

	
	public void runLearningThread() throws IOException, ConfigurationException {
		sLogger.trace("Entering");

		mStartingDepth = 0;
		if (mBreadCrumbs.startsWith("0")) {
			mEndingDepth = ((int) (((mBreadCrumbs.length() - 1) / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1;
		} else {
			mEndingDepth = ((int) (mBreadCrumbs.length() / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile();
		}

			
		sLogger.debug("Last Starting level should be: " + mStartingDepth);
		sLogger.debug("Last Ending level should be: " + mEndingDepth);

		int lIncrementer = 1;

		mStartingIndex = mBreadCrumbs;
		
		while (!isTimeToTerminate()) {

			try {
				performExhaustiveSearch(mCurrentBoardState);
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

			if (!isTimeToTerminate()) {
				mStartingDepth = 0;
				lIncrementer++;
				if (mBreadCrumbs.startsWith("0")) {
					mEndingDepth = ((int) (((mBreadCrumbs.length() - 1) / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() * lIncrementer) + 1;
				} else {
					mEndingDepth = ((int) (mBreadCrumbs.length() / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() * lIncrementer;
				}
				sLogger.debug("Starting level is now: " + mStartingDepth);
				sLogger.debug("Ending level is now: " + mEndingDepth);
			}
		}
		sLogger.debug("Timer Exhausted");
		mTransactionSuccessful = true;

		sLogger.trace("Exiting");
	}

	private void performExhaustiveSearch(BoardState pBoardStateToExpand) throws KnowledgeBaseException {
		sLogger.trace("Entering");
		

		if (mStartingIndex != null && !mStartingIndex.trim().equals("") && mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index equals current Board State set! Unsetting mStartingIndex: " + pBoardStateToExpand.getFileIndexString());
			mStartingIndex = null;
		} else if (mStartingIndex != null && !mStartingIndex.trim().equals("") && !mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index is set! ");

			String lBoardStateToExpandFileIndex = pBoardStateToExpand.getFileIndexString();

			sLogger.debug ("Starting Index: " + mStartingIndex);
			sLogger.debug ("Board State to Expand Index: " + lBoardStateToExpandFileIndex);

			if (!mStartingIndex.startsWith(lBoardStateToExpandFileIndex)) {
				mTransactionSuccessful = false;
				sLogger.error("Exhaustive Search was not successful");
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}

			sLogger.debug("The starting index is set! Initializing Expansion to: " + pBoardStateToExpand.getFileIndexString());

			ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
			boolean lSuccess = lExpandNodeThread.executeTask();

			if (!lSuccess) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}

			sLogger.debug("The starting index is set! Expansion Successful to: " + pBoardStateToExpand.getFileIndexString());

			ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

			if (lSubBoardStates == null || lSubBoardStates.isEmpty()) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}

			sortSubBoardState(lSubBoardStates);

			boolean lStartTraversing = false;
			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();
				if (mStartingIndex == null || mStartingIndex.startsWith(lSubBoardState.getFileIndexString())) {
					lStartTraversing = true;
				}
				if (lStartTraversing) {
					sLogger.debug("Traversing to: " + lSubBoardState.getFileIndexString());

					if (!isTimeToTerminate()) {
						performExhaustiveSearch(lSubBoardState);
					} else {
						sLogger.debug("Timer has expired!");
						return;
					}
				}
			}

			sLogger.trace("Exiting");
			return;
		}

		if (pBoardStateToExpand.getFileIndexString().length() >= mEndingDepth) {
			sLogger.debug("Reached Maximum Depth:" + pBoardStateToExpand.getFileIndexString());
			sLogger.trace("Exiting");
			return;
		}

		sLogger.debug("Expanding Node:" + pBoardStateToExpand.getFileIndexString());

		ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
		boolean lSuccess = lExpandNodeThread.executeTask();

		if (!lSuccess) {
			mTransactionSuccessful = false;
			sLogger.error("Exhaustive Search was not successful");
			sLogger.trace("Exiting");
			throw new KnowledgeBaseException();
		}

		sLogger.debug("Node Expansion Successful:" + pBoardStateToExpand.getFileIndexString());

		writeLastMoveEvaluated(pBoardStateToExpand.getFileIndexString());
		
		sLogger.debug("Write Last Move Successful:" + pBoardStateToExpand.getFileIndexString());

		ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

		if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {

			sLogger.debug("Sorting SubBoard States:" + pBoardStateToExpand.getFileIndexString());

			sortSubBoardState (lSubBoardStates);
			
			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();
				sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
				if (!isTimeToTerminate()) {
					performExhaustiveSearch(lSubBoardState);
				} else {
					sLogger.debug("Timer has expired!");
					return;
				}
				sLogger.debug("Recursive Call Complete: " + lSubBoardState.getFileIndexString());
			}
		} else {
			sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
		}

		sLogger.trace("Exiting");
	}


	public void sortSubBoardState(List <BoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				int lMove1Value = p1.getMove().getMoveIntValue();
				int lMove2Value = p2.getMove().getMoveIntValue();
				if (lMove1Value > lMove2Value)
					return -1;
				else if (lMove1Value < lMove2Value)
					return 1;
				else
					return 0;
			}
		});
	}

	@Override
	public long getThreadMemoryFootprint() {
		return 0;
	}



}
