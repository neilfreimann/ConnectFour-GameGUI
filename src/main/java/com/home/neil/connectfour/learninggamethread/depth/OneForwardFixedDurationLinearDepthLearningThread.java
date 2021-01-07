package com.home.neil.connectfour.learninggamethread.depth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.InvalidMoveException;
import com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.StatefulFixedDurationLearningThread;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;



public class OneForwardFixedDurationLinearDepthLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = OneForwardFixedDurationLinearDepthLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + OneForwardFixedDurationLinearDepthLearningThread.class.getSimpleName();

	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_DEPTH = OneForwardFixedDurationLinearDepthLearningThread.class.getSimpleName() + ".dat";

	private static OneForwardFixedDurationLinearDepthLearningThread sInstance = null;

	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		setName(OneForwardFixedDurationLinearDepthLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = OneForwardFixedDurationLinearDepthLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}

	public static synchronized OneForwardFixedDurationLinearDepthLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		if (sInstance == null) {
			sInstance = new OneForwardFixedDurationLinearDepthLearningThread(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread (sInstance,MBEAN_NAME);
		}
		
		sLogger.trace("Exiting");
		return sInstance;
	}
	
	private OneForwardFixedDurationLinearDepthLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException{
		super (pKnowledgeBaseFilePool, pDurationToRunInMs, FILENAME_FIXED_DURATION_LINEAR_DEPTH, pLogContext);
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
	}
	
	public void runLearningThread() {
		sLogger.trace("Entering");
				
		for (int i = 0; i < mBreadCrumbs.length(); i++) {
			String lChar = mBreadCrumbs.substring(i, i + 1);
			sLogger.debug("Tracing Last Move: " + lChar);
			int lMoveInt = Integer.parseInt(lChar);
			BoardState.Move lMove = null;
			if (i % 2 == 0) {
				lMove = BoardState.Move.getOpponentMove(lMoveInt);
			} else {
				lMove = BoardState.Move.getSelfMove(lMoveInt);
			}
			if (mCurrentBoardState == null) {
				try {
					mCurrentBoardState = new BoardState(mKnowledgeBaseFilePool, lMove, true, mLogContext);
				} catch (InvalidMoveException e) {
					sLogger.error("Could not expand node, exiting");
					mTransactionSuccessful = false;
					sLogger.trace("Exiting");
					return;
				} catch (KnowledgeBaseException e) {
					sLogger.error("Could not expand node, exiting");
					mTransactionSuccessful = false;
					sLogger.trace("Exiting");
					return;
				} catch (ConfigurationException e) {
					sLogger.error("Could not expand node, exiting");
					mTransactionSuccessful = false;
					sLogger.trace("Exiting");
					return;
				}
			} else {
				ExpansionTask lExpandNodeThread = new ExpansionTask(mCurrentBoardState, mLogContext);
				boolean lSuccess = lExpandNodeThread.executeTask();

				if (!lSuccess) {
					sLogger.error("Could not expand node, exiting");
					mTransactionSuccessful = false;
					sLogger.trace("Exiting");
					return;
				}

				ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

				for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					BoardState lCurrentBoardState = lIterator.next();
					if (lCurrentBoardState.getMove() == lMove) {
						mCurrentBoardState = lCurrentBoardState;
						break;
					}
				}
			}

			sLogger.debug("Creating Initial States: " + mCurrentBoardState.getFileIndexString());
			mCurrentBoardState.logBoardState(Level.DEBUG);

		}

		while (!isTimeToTerminate()) {
			sLogger.debug ("Expanding Node: " + mCurrentBoardState.getFileIndexString());
			
			ExpansionTask lExpandNodeThread = new ExpansionTask(mCurrentBoardState, mLogContext);
			boolean lSuccess = lExpandNodeThread.executeTask();

			if (!lSuccess) {
				sLogger.error("Could not expand node, exiting");
				mTransactionSuccessful = false;
				sLogger.trace("Exiting");
				return;
			}

			sLogger.debug ("Expansion Successful, Writing to File.");

			writeLastMoveEvaluated(mCurrentBoardState.getFileIndexString());

			sLogger.debug ("Expansion Written to File.");

			sLogger.debug ("Looking for next state");
			
			ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

			if (lSubBoardStates == null || lSubBoardStates.isEmpty()) {


				BoardState lLastSubState = mCurrentBoardState;

				mCurrentBoardState = mCurrentBoardState.getParentBoardState();

				sLogger.debug ("Sub Board States are empty.  Must go to parent node:" + mCurrentBoardState.getFileIndexString());
				
				boolean lFoundNextToEvaluate = false;

				while (!lFoundNextToEvaluate && mCurrentBoardState != null) {

					sLogger.debug ("Expanding Parent State:" + mCurrentBoardState.getFileIndexString());

					ExpansionTask lExpandNodeThread2 = new ExpansionTask(mCurrentBoardState, mLogContext);
					lSuccess = lExpandNodeThread2.executeTask();

					if (!lSuccess) {
						sLogger.debug("Could not expand node, exiting");
						mTransactionSuccessful = false;
						sLogger.trace("Exiting");
						return;
					}
					
					sLogger.debug ("Expansion Successful on Parent, Getting Sub Board States and Sorting to find next one.");

					lSubBoardStates = lExpandNodeThread2.getSubBoardStates();

					sortSubBoardState(lSubBoardStates);

					for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
						BoardState lCurrentBoardState = lIterator.next();
						if (lCurrentBoardState.getMove() == lLastSubState.getMove()) {
							if (lIterator.hasNext()) {
								mCurrentBoardState = lIterator.next();
								sLogger.debug ("Found the next State to evaluate: " + mCurrentBoardState.getFileIndexString());
								lFoundNextToEvaluate = true;
								break;
							} else {
								lLastSubState = mCurrentBoardState;
								mCurrentBoardState = mCurrentBoardState.getParentBoardState();
								sLogger.debug ("Could not find the next state in subboardStates, going to parent state and trying again: " + mCurrentBoardState.getFileIndexString());
							}
						}
					}
				}

				if (!lFoundNextToEvaluate) {
					sLogger.debug("We've reached the end!");
					sLogger.trace("Exiting");
					return;
				}
			} else {
				sortSubBoardState(lSubBoardStates);

				mCurrentBoardState = lSubBoardStates.get(0);
			}
		}
		
		mTransactionSuccessful = true;
		sLogger.trace("Exiting");
	}


	public void sortSubBoardState(List <BoardState> pBoardStates) {
		sLogger.trace("Entering");
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				int lMove1Value = p1.getMove().getMoveIntValue();
				int lMove2Value = p2.getMove().getMoveIntValue();
				if (lMove1Value > lMove2Value)
					return 1;
				else if (lMove1Value < lMove2Value)
					return -1;
				else
					return 0;
			}
		});
		sLogger.trace("Exiting");
	}
	
	
	@Override
	public long getThreadMemoryFootprint() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return 0;
	}
}
