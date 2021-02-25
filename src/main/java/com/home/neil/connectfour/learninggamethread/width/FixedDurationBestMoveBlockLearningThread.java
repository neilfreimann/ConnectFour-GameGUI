package com.home.neil.connectfour.learninggamethread.width;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;
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
import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;



public class FixedDurationBestMoveBlockLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = FixedDurationBestMoveBlockLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + FixedDurationBestMoveBlockLearningThread.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_WIDTH = FixedDurationBestMoveBlockLearningThread.class.getSimpleName() + ".dat";

	private int mStartingDepth = 0;
	private int mEndingDepth = 0;

	private String mStartingIndex = null;
	
	private static FixedDurationBestMoveBlockLearningThread sInstance = null;
	
	public synchronized void renameThread(String pLogContext) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		sThreadNumber++;

		setName(FixedDurationBestMoveBlockLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = FixedDurationBestMoveBlockLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}



	public static synchronized FixedDurationBestMoveBlockLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, IOException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		if (sInstance == null) {
			sInstance = new FixedDurationBestMoveBlockLearningThread(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread (sInstance,sInstance.getBeanName());
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return sInstance;
	}


	private FixedDurationBestMoveBlockLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, FILENAME_FIXED_DURATION_LINEAR_WIDTH, pLogContext);
		mBeanName = MBEAN_NAME;
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	
	public void runLearningThread() throws ConfigurationException, IOException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		
		
		String [] lBreadCrumbs = mBreadCrumbs.split("\\|");
		
		String lBreadCrumbsStack = null;
				
		if (lBreadCrumbs.length > 1) {
			lBreadCrumbsStack = lBreadCrumbs[1];
		}
		

		String lLastMoveEvaluated = lBreadCrumbs[0];
		
		mStartingDepth = 0;
		if (lLastMoveEvaluated.startsWith("0")) {
			mEndingDepth = ((int) (((lLastMoveEvaluated.length() - 1) / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile());
		} else {
			mEndingDepth = ((int) (lLastMoveEvaluated.length() / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile();
		}

			
		sLogger.debug("Last Starting level should be: " + mStartingDepth);
		sLogger.debug("Last Ending level should be: " + mEndingDepth);

		int lIncrementer = mEndingDepth / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile();

		mStartingIndex = lLastMoveEvaluated;
		
		while (!isTimeToTerminate()) {

			try {
				performExhaustiveSearch(mCurrentBoardState, null, lBreadCrumbsStack, lIncrementer);
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
			

			lBreadCrumbs = mBreadCrumbs.split("\\|");

			lLastMoveEvaluated = lBreadCrumbs[0];
			
			if (!isTimeToTerminate()) {
				mStartingDepth = 0;
				lIncrementer++;
				if (lLastMoveEvaluated.startsWith("0")) {
					mEndingDepth = ((int) (((lLastMoveEvaluated.length() - 1) / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + lIncrementer) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile());
				} else {
					mEndingDepth = ((int) (lLastMoveEvaluated.length() / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + lIncrementer) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile();
				}
				sLogger.debug("Starting level is now: " + mStartingDepth);
				sLogger.debug("Ending level is now: " + mEndingDepth);
			}
		}
		sLogger.debug("Timer Exhausted");
		mTransactionSuccessful = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	private void performExhaustiveSearch(BoardState pBoardStateToExpand, String pStack, String pBreadCrumbs, int pIncrementer) throws KnowledgeBaseException, IOException, ConfigurationException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		//Check for recursion
		boolean lEvaluatedAlready = true;
		
		try {
			for (int i = 0; i < (pIncrementer); i++){
				if (!pBoardStateToExpand.getEvaluationThreadInfoBit((byte)(1 + i), mLogContext)) {
					lEvaluatedAlready = false;
					break;
				}
			}
		} catch (ConfigurationException | InvalidMoveException e) {
			// TODO Auto-generated catch block
			StringWriter lSW = new StringWriter();
			PrintWriter lPW = new PrintWriter(lSW);
			e.printStackTrace(lPW);
			lSW.toString(); // stack trace as a string

			sLogger.debug("StackTrace: " + lSW);
			mTransactionSuccessful = false;
			sLogger.debug("Exhaustive Search was not successful");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}

		
		
		// We found the starting index, begin where we left off
		if (lEvaluatedAlready || (mStartingIndex != null && !mStartingIndex.trim().equals("") && mStartingIndex.equals(pBoardStateToExpand.getFileIndexString()))) {
			sLogger.debug("The starting index equals current Board State set! Unsetting mStartingIndex: " + pBoardStateToExpand.getFileIndexString());
			mStartingIndex = null;
		// Still going to where we left off
		} else if (!lEvaluatedAlready && mStartingIndex != null && !mStartingIndex.trim().equals("") && !mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index is set! ");

			String [] lLayers = pBreadCrumbs.split(";",2);
			
			String lLayer = lLayers[0];
			
			String lNextLayers = null;
			
			if (lLayers.length > 1) {
				lNextLayers = lLayers [1];
			}
			
			String [] lLayerBreadCrumbs = lLayer.split(":");
			
			String lCurrentLayerNode = lLayerBreadCrumbs[0];
			
			String lCurrentLayerOrderedNodes = lLayerBreadCrumbs[1];
			
			String [] lCurrentLayerOrderedNodesList = lCurrentLayerOrderedNodes.split(","); 
			
			HashMap <String, Integer> lCurrentLayerOrderedNodesHashmap = new HashMap <String, Integer> ();
			
			for (int index = 0; index < lCurrentLayerOrderedNodesList.length; index++) {
				lCurrentLayerOrderedNodesHashmap.put(lCurrentLayerOrderedNodesList[index], index);
			}
			
			
			String lBoardStateToExpandFileIndex = pBoardStateToExpand.getFileIndexString();

			sLogger.debug ("Starting Index: " + mStartingIndex);
			sLogger.debug ("Board State to Expand Index: " + lBoardStateToExpandFileIndex);

			if (!mStartingIndex.startsWith(lBoardStateToExpandFileIndex)) {
				mTransactionSuccessful = false;
				sLogger.error("Exhaustive Search was not successful");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}
			
			sLogger.debug("The starting index is set! Initializing Expansion to: " + pBoardStateToExpand.getFileIndexString());

			//First Layer Expansion
			
			ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
			boolean lSuccess = lExpandNodeThread.executeTask();

			if (!lSuccess) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}

			sLogger.debug("The starting index is set! Expansion Successful to: " + pBoardStateToExpand.getFileIndexString());
			
			if (lEvaluatedAlready && pBoardStateToExpand.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
				sLogger.debug("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return;
			}
			
			
			// Second Layer Expansion
			
			ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

			if (lSubBoardStates == null || lSubBoardStates.isEmpty()) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}

			ArrayList<BoardState> lSubBoardStatesLevel2 = new ArrayList <BoardState>();
			
			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {

				BoardState lSubBoardState = lIterator.next();
				sLogger.debug("Level Two Expansion: " + lSubBoardState.getFileIndexString());

				ExpansionTask lExpandNodeThreadLevel2 = new ExpansionTask(lSubBoardState, mLogContext);
				lSuccess = lExpandNodeThreadLevel2.executeTask();

				if (!lSuccess) {
					mTransactionSuccessful = false;
					sLogger.error("Exhaustive Search was not successful");
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					throw new KnowledgeBaseException();
				}

				ArrayList<BoardState> lSubBoardStatesLevel2Sub = lExpandNodeThreadLevel2.getSubBoardStates();
				
				if (lSubBoardStatesLevel2Sub != null) {
					sLogger.debug("Sorting SubBoard Level Two States:" + pBoardStateToExpand.getFileIndexString());

					lSubBoardStatesLevel2.addAll(lSubBoardStatesLevel2Sub);
				}
				

				sLogger.debug("Level Two Expansion Complete: " + lSubBoardState.getFileIndexString());
			}
			
			
			// Resort and hit 
			
			sortSubBoardState (lSubBoardStatesLevel2, lCurrentLayerOrderedNodesHashmap);
			
			// Done

			StringBuilder lStackBuilder = new StringBuilder();
			
			for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();
				
				lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");
				
			}

			lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);
			
			String lStackList = lStackBuilder.toString();
			
			boolean lStartTraversing = false;

			for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();

				
				if (mStartingIndex == null || lCurrentLayerNode.startsWith(lSubBoardState.getFileIndexString())) {
					lStartTraversing = true;
				}
				if (lStartTraversing) {
					sLogger.debug("Traversing to: " + lSubBoardState.getFileIndexString());

					if (!isTimeToTerminate()) {
						String lStack = pStack != null ? 
								pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList : 
									lSubBoardState.getFileIndexString() + ":" + lStackList;
				
				
						sLogger.debug("Write Last Move Stack Written:" + lStack);
						
						String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;

						writeLastMoveEvaluated(lLastMoveEvaluated);

						if (!lEvaluatedAlready || pBoardStateToExpand.getFileIndexString().length() <= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
							sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
							performExhaustiveSearch(lSubBoardState, lStack, lNextLayers, pIncrementer);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
						} else {
							sLogger.debug("Already Evaluated Recursive Call Aborted");
						}

					} else {
						sLogger.debug("Timer has expired!");
						return;
					}
				}
			}
			
			
			
			//Mark Completion of evaluation for SubBoardStates and SubBoardStatesLevel2
			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();
				
				try{
//					if (lSubBoardState.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
						for (int i = 0; i < (pIncrementer); i++){
							lSubBoardState.setEvaluationThreadInfoBit((byte)(1+i), true, mLogContext);
						}
//					}
				} catch (ConfigurationException | InvalidMoveException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string

					sLogger.debug("StackTrace: " + lSW);
					mTransactionSuccessful = false;
					sLogger.debug("Exhaustive Search was not successful");
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					throw new KnowledgeBaseException();
				}
			}
			
			for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();
				try{
//					if (lSubBoardState.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
						for (int i = 0; i < (pIncrementer); i++){
							lSubBoardState.setEvaluationThreadInfoBit((byte)(1+i), true, mLogContext);
//						}
					}
				} catch (ConfigurationException | InvalidMoveException e) {
					// TODO Auto-generated catch block
					StringWriter lSW = new StringWriter();
					PrintWriter lPW = new PrintWriter(lSW);
					e.printStackTrace(lPW);
					lSW.toString(); // stack trace as a string

					sLogger.debug("StackTrace: " + lSW);
					mTransactionSuccessful = false;
					sLogger.debug("Exhaustive Search was not successful");
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					throw new KnowledgeBaseException();
				}
			}


			
			
			

			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return;
		}

		
		
		
		
		
		// Reached the maximum depth, now return
		if (pBoardStateToExpand.getFileIndexString().length() >= (mEndingDepth - 1)) {
			sLogger.debug("Reached Maximum Depth:" + pBoardStateToExpand.getFileIndexString());
			sLogger.trace("Exiting");
			return;
		}

		
		
		
		// First Level Expansion
		
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
		
		if (lEvaluatedAlready && pBoardStateToExpand.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
			sLogger.error("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return;
		}

		ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();
		
		
		if (lSubBoardStates != null) {
			sLogger.debug("Sorting SubBoard States:" + pBoardStateToExpand.getFileIndexString());

			sortSubBoardStateByScore (lSubBoardStates);
			
			// Second Level Expansion

			ArrayList<BoardState> lSubBoardStatesLevel2 = new ArrayList <BoardState>();
			
			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {

				BoardState lSubBoardState = lIterator.next();
				/*
				if(lSubBoardState.getMoveScore().getMoveScore() == 100) {
					sLogger.debug("A winning move detected in odd : " + pBoardStateToExpand.getFileIndexString());
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					return;
				}
				*/
				
				sLogger.debug("Level Two Expansion: " + lSubBoardState.getFileIndexString());

				ExpansionTask lExpandNodeThreadLevel2 = new ExpansionTask(lSubBoardState, mLogContext);
				lSuccess = lExpandNodeThreadLevel2.executeTask();

				if (!lSuccess) {
					mTransactionSuccessful = false;
					sLogger.error("Exhaustive Search was not successful");
					sLogger.trace("Exiting");
					throw new KnowledgeBaseException();
				}

				ArrayList<BoardState> lSubBoardStatesLevel2Sub = lExpandNodeThreadLevel2.getSubBoardStates();
				
				if (lSubBoardStatesLevel2Sub != null) {
					sLogger.debug("Sorting SubBoard Level Two States:" + pBoardStateToExpand.getFileIndexString());

					sortSubBoardStateByReverseScore (lSubBoardStatesLevel2Sub);
					
					/*
					if(lSubBoardState.getMoveScore().getMoveScore() == -100) {
						sLogger.debug("A winning move detected in even : " + pBoardStateToExpand.getFileIndexString());
						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
							sLogger.trace("Exiting");
						}
						return;
					}
					*/

					lSubBoardStatesLevel2.addAll(lSubBoardStatesLevel2Sub);
				}
				

				sLogger.debug("Level Two Expansion Complete: " + lSubBoardState.getFileIndexString());
			}


			if (lSubBoardStatesLevel2 != null && !lSubBoardStatesLevel2.isEmpty()) {


				StringBuilder lStackBuilder = new StringBuilder();
				
				for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();
					
					lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");
					
				}

				lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);
				
				String lStackList = lStackBuilder.toString();
				
				
				
				for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();

					sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
					if (!isTimeToTerminate()) {
						String lStack = pStack != null ? 
								pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList : 
									lSubBoardState.getFileIndexString() + ":" + lStackList;
				
				
						sLogger.debug("Write Last Move Stack Written:" + lStack);
						
						String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;

						writeLastMoveEvaluated(lLastMoveEvaluated);
						
						
						if (!lEvaluatedAlready || pBoardStateToExpand.getFileIndexString().length() <= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
							sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
							performExhaustiveSearch(lSubBoardState, lStack, null, pIncrementer);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
						} else {
							sLogger.debug("Already Evaluated Recursive Call Aborted");
						}
						
					} else {
						sLogger.debug("Timer has expired!");
						return;
					}
					sLogger.debug("Recursive Call Complete: " + lSubBoardState.getFileIndexString());
				}
				
				//Mark Completion of evaluation for SubBoardStates and SubBoardStatesLevel2
				for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();
					try{
						//if (lSubBoardState.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
							for (int i = 0; i < (pIncrementer); i++){
								lSubBoardState.setEvaluationThreadInfoBit((byte)(1+i), true, mLogContext);
							}
						//}
					} catch (ConfigurationException | InvalidMoveException e) {
						// TODO Auto-generated catch block
						StringWriter lSW = new StringWriter();
						PrintWriter lPW = new PrintWriter(lSW);
						e.printStackTrace(lPW);
						lSW.toString(); // stack trace as a string

						sLogger.debug("StackTrace: " + lSW);
						mTransactionSuccessful = false;
						sLogger.debug("Exhaustive Search was not successful");
						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
							sLogger.trace("Exiting");
						}
						throw new KnowledgeBaseException();
					}
				}
				
				for (Iterator<BoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();
					try{
						//if (lSubBoardState.getFileIndexString().length() >= (mEndingDepth - KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() - 1)) {
							for (int i = 0; i < (pIncrementer); i++){
								lSubBoardState.setEvaluationThreadInfoBit((byte)(1+i), true, mLogContext);
							}
						//}
					} catch (ConfigurationException | InvalidMoveException e) {
						// TODO Auto-generated catch block
						StringWriter lSW = new StringWriter();
						PrintWriter lPW = new PrintWriter(lSW);
						e.printStackTrace(lPW);
						lSW.toString(); // stack trace as a string

						sLogger.debug("StackTrace: " + lSW);
						mTransactionSuccessful = false;
						sLogger.debug("Exhaustive Search was not successful");
						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
							sLogger.trace("Exiting");
						}
						throw new KnowledgeBaseException();
					}
				}
				
			} else {
				sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
			}
			
		}  else {
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
					return 1;
				else if (lMove1Value < lMove2Value)
					return -1;
				else
					return 0;
			}
		});
	}



	
	public void sortSubBoardState(List <BoardState> pBoardStates, HashMap <String, Integer>  pOrderedBoardStates) {
		
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				int lMove1Value = pOrderedBoardStates.get(p1.getFileIndexString());
				int lMove2Value = pOrderedBoardStates.get(p2.getFileIndexString());
				
				if (lMove1Value > lMove2Value)
					return 1;
				else if (lMove1Value < lMove2Value)
					return -1;
				else
					return 0;
			}
		});
	}


	
	
	
	public void sortSubBoardStateByScore(List <BoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				byte lMove1ScoreValue = p1.getMoveScore().getMoveScore();
				byte lMove2ScoreValue = p2.getMoveScore().getMoveScore();
				if (lMove1ScoreValue > lMove2ScoreValue)
					return -1;
				else if (lMove1ScoreValue < lMove2ScoreValue)
					return 1;
				else
					return 0;
			}
		});
	}
	
	
	public void sortSubBoardStateByReverseScore(List <BoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				byte lMove1ScoreValue = p1.getMoveScore().getMoveScore();
				byte lMove2ScoreValue = p2.getMoveScore().getMoveScore();
				if (lMove1ScoreValue > lMove2ScoreValue)
					return 1;
				else if (lMove1ScoreValue < lMove2ScoreValue)
					return -1;
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
