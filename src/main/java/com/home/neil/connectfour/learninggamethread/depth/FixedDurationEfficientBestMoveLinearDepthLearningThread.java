package com.home.neil.connectfour.learninggamethread.depth;

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



public class FixedDurationEfficientBestMoveLinearDepthLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = FixedDurationEfficientBestMoveLinearDepthLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type="
			+ FixedDurationEfficientBestMoveLinearDepthLearningThread.class.getSimpleName();

	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_WIDTH = FixedDurationEfficientBestMoveLinearDepthLearningThread.class
			.getSimpleName() + ".dat";

	private int mStartingDepth = 0;

	private static final int LOWER_LAYERS_DEPTH = 1;
	private static final int UPPER_LAYERS_DEPTH = 5;
	private static final int UPPER_LAYERS_THRESHOLD = 30;
	private static final int MAX_BOARDSTATES_UPPER_LEVEL = 5000;
	
	private String mStartingIndex = null;

	private static FixedDurationEfficientBestMoveLinearDepthLearningThread sInstance = null;

	public synchronized void renameThread(String pLogContext) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		sThreadNumber++;

		setName(FixedDurationEfficientBestMoveLinearDepthLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = FixedDurationEfficientBestMoveLinearDepthLearningThread.class.getSimpleName() + "."
					+ sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public static synchronized FixedDurationEfficientBestMoveLinearDepthLearningThread getInstance(
			KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext)
			throws ConfigurationException, IOException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		if (sInstance == null) {
			sInstance = new FixedDurationEfficientBestMoveLinearDepthLearningThread(pKnowledgeBaseFilePool,
					pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager
					.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread(sInstance,
					sInstance.getBeanName());
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return sInstance;
	}

	private FixedDurationEfficientBestMoveLinearDepthLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool,
			long pDurationToRunInMs, String pLogContext)
			throws ConfigurationException, FileNotFoundException, IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, FILENAME_FIXED_DURATION_LINEAR_WIDTH, pLogContext);
		mBeanName = MBEAN_NAME;
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public void runLearningThread() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		String[] lBreadCrumbs = mBreadCrumbs.split("\\|");

		String lBreadCrumbsStack = null;

		if (lBreadCrumbs.length > 1) {
			lBreadCrumbsStack = lBreadCrumbs[1];
		}

		String lLastMoveEvaluated = lBreadCrumbs[0];

		mStartingDepth = 0;

		sLogger.debug("Last Starting level should be: " + mStartingDepth);

		int lIncrementer = 1;

		mStartingIndex = lLastMoveEvaluated;

		
		//TODO: track the layers, best move up to 28, then ordered by statestring afterwards for file efficiency
		while (!isTimeToTerminate()) {

			try {
				performExhaustiveSearch(mCurrentBoardState, null, lBreadCrumbsStack, LOWER_LAYERS_DEPTH);
			} catch (KnowledgeBaseException eKBE) {
				sLogger.error("Knowledge Base exception occurred when performing exhaustive search");
				sLogger.error("Message: " + eKBE.getMessage());

				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				eKBE.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.debug("StackTrace: " + lSW);

				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				mTransactionSuccessful = false;
				return;
			}

			lBreadCrumbs = mBreadCrumbs.split("\\|");

			lLastMoveEvaluated = lBreadCrumbs[0];

			if (!isTimeToTerminate()) {
				mStartingDepth = 0;
				lIncrementer++;
				sLogger.debug("Starting level is now: " + mStartingDepth);
			}
		}
		sLogger.debug("Timer Exhausted");
		mTransactionSuccessful = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	private void performExhaustiveSearch(BoardState pBoardStateToExpand, String pStack, String pBreadCrumbs, int pLayersToRead)
			throws KnowledgeBaseException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		// Check for recursion
		boolean lEvaluatedAlready = false;
		try {
			lEvaluatedAlready = pBoardStateToExpand.getEvaluationThreadInfoBit((byte) 0, mLogContext);

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
		if (mStartingIndex != null && !mStartingIndex.trim().equals("")
				&& mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index equals current Board State set! Unsetting mStartingIndex: "
					+ pBoardStateToExpand.getFileIndexString());
			mStartingIndex = null;
			// Still going to where we left off
		} else if (mStartingIndex != null && !mStartingIndex.trim().equals("")
				&& !mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index is set! ");

			String[] lLayers = pBreadCrumbs.split(";", 2);

			String lLayer = lLayers[0];

			String lNextLayers = null;

			if (lLayers.length > 1) {
				lNextLayers = lLayers[1];
			}

			String[] lLayerBreadCrumbs = lLayer.split(":");

			String lCurrentLayerNode = lLayerBreadCrumbs[0];

			String lCurrentLayerOrderedNodes = lLayerBreadCrumbs[1];

			String[] lCurrentLayerOrderedNodesList = lCurrentLayerOrderedNodes.split(",");

			HashMap<String, Integer> lCurrentLayerOrderedNodesHashmap = new HashMap<String, Integer>();

			for (int index = 0; index < lCurrentLayerOrderedNodesList.length; index++) {
				lCurrentLayerOrderedNodesHashmap.put(lCurrentLayerOrderedNodesList[index], index);
			}

			String lBoardStateToExpandFileIndex = pBoardStateToExpand.getFileIndexString();

			sLogger.debug("Starting Index: " + mStartingIndex);
			sLogger.debug("Board State to Expand Index: " + lBoardStateToExpandFileIndex);

			if (!mStartingIndex.startsWith(lBoardStateToExpandFileIndex)) {
				mTransactionSuccessful = false;
				sLogger.error("Exhaustive Search was not successful");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				throw new KnowledgeBaseException();
			}

			sLogger.debug("The starting index is set! Initializing Expansion to: "
					+ pBoardStateToExpand.getFileIndexString());


			ArrayList<BoardState> lSubBoardStates = expandBoardStates(pBoardStateToExpand, pLayersToRead, true);

			if (pBoardStateToExpand.getFileIndexString().length() >= UPPER_LAYERS_THRESHOLD + 1) {
				sLogger.error("SubBoardStatesCount: "
						+ lSubBoardStates.size());
				
				sortSubBoardStateByStateString (lSubBoardStates);

				int lUniqueCount = 0;
				String lCurrentSubBoardStateFileString = null;
				for (Iterator <BoardState> lIter = lSubBoardStates.iterator(); lIter.hasNext();) {
					BoardState lCurrentSubBoardState = lIter.next();
					if (lCurrentSubBoardStateFileString == null ) {
						lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
						lUniqueCount++;
					} else if (!lCurrentSubBoardStateFileString.equals(lCurrentSubBoardState.getBoardStateString())) {
						lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
						lUniqueCount++;
					}
				}

				sLogger.error("SubBoardStatesUniqueCount: "
						+ lUniqueCount);

			} 

			// Done

			
			if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {

				//Order by the file
				sortSubBoardState (lSubBoardStates, lCurrentLayerOrderedNodesHashmap);

				
				StringBuilder lStackBuilder = new StringBuilder();
	
				for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();
	
					lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");
	
				}
	
				lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);
	
				String lStackList = lStackBuilder.toString();
	
				boolean lStartTraversing = false;
	
				for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					BoardState lSubBoardState = lIterator.next();
	
					if (mStartingIndex == null || lCurrentLayerNode.startsWith(lSubBoardState.getFileIndexString())) {
						lStartTraversing = true;
					}
					if (lStartTraversing) {
						sLogger.debug("Traversing to: " + lSubBoardState.getFileIndexString());

						if (pBoardStateToExpand.getFileIndexString().length() > UPPER_LAYERS_THRESHOLD) {
							sLogger.error("BoardState List has Reached End, Draining:" + lSubBoardStates.size());

							stopAndDrainBoardStates(lSubBoardStates);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
							
							sLogger.debug("Write Last Move Stack Written:" + pStack);
							String lLastMoveEvaluated = pBoardStateToExpand.getFileIndexString() + "|" + pStack;
							writeLastMoveEvaluated(lLastMoveEvaluated);
							
							if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
								sLogger.trace("Exiting");
							}
							
							return;
						}

						if (!isTimeToTerminate()) {
							String lStack = pStack != null
									? pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList
									: lSubBoardState.getFileIndexString() + ":" + lStackList;

							
							if (!lEvaluatedAlready) {
								sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
								
	
								int lNextLayersToRead = 1;
								if (pBoardStateToExpand.getFileIndexString().length() < UPPER_LAYERS_THRESHOLD) {
									sLogger.debug("Write Last Move Stack Written:" + lStack);
									String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;
									writeLastMoveEvaluated(lLastMoveEvaluated);

									lNextLayersToRead = LOWER_LAYERS_DEPTH;
									performExhaustiveSearch(lSubBoardState, lStack, lNextLayers, lNextLayersToRead);
									sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());

								} else if (pBoardStateToExpand.getFileIndexString().length() == UPPER_LAYERS_THRESHOLD) {
									sLogger.debug("Write Last Move Stack Written:" + lStack);
									String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;
									writeLastMoveEvaluated(lLastMoveEvaluated);

									lNextLayersToRead = UPPER_LAYERS_DEPTH;
									performExhaustiveSearch(lSubBoardState, lStack, lNextLayers, lNextLayersToRead);
									sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
									
									// Mark Completion of evaluation for SubBoardState
									markSubBoardsComplete(lSubBoardState, pLayersToRead);

								} 	
							} else {
								sLogger.error("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
							}
	
						} else {
							sLogger.debug("Timer has expired!");

							if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
								sLogger.trace("Exiting");
							}
							return;
						}
					}
				}
				if (!isTimeToTerminate()) {
					// Mark Completion of evaluation for SubBoardStates
					markSubBoardsComplete(pBoardStateToExpand, pLayersToRead);
				}
			} else {
				sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
			}

			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return;
		}

		sLogger.debug("Expanding Node:" + pBoardStateToExpand.getFileIndexString());

		ArrayList<BoardState> lSubBoardStates = expandBoardStates(pBoardStateToExpand, pLayersToRead, false);
	
		
		if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {

			if (pBoardStateToExpand.getFileIndexString().length() >= UPPER_LAYERS_THRESHOLD + 1) {
				sLogger.error("SubBoardStatesCount: "
						+ lSubBoardStates.size());
				

				sortSubBoardStateByStateString (lSubBoardStates);

				int lUniqueCount = 0;
				String lCurrentSubBoardStateFileString = null;
				for (Iterator <BoardState> lIter = lSubBoardStates.iterator(); lIter.hasNext();) {
					BoardState lCurrentSubBoardState = lIter.next();
					if (lCurrentSubBoardStateFileString == null ) {
						lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
						lUniqueCount++;
					} else if (!lCurrentSubBoardStateFileString.equals(lCurrentSubBoardState.getBoardStateString())) {
						lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
						lUniqueCount++;
					}
				}

				sLogger.error("SubBoardStatesUniqueCount: "
						+ lUniqueCount);

			} 

			
			

			StringBuilder lStackBuilder = new StringBuilder();

			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();

				lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");

			}

			lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);

			String lStackList = lStackBuilder.toString();

			for (Iterator<BoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lSubBoardState = lIterator.next();

				if (pBoardStateToExpand.getFileIndexString().length() > UPPER_LAYERS_THRESHOLD) {

					sLogger.error("BoardState List has Reached End, Draining:" + lSubBoardStates.size());

					sLogger.error("SubBoardStatesCount: "
								+ lSubBoardStates.size());
					stopAndDrainBoardStates(lSubBoardStates);

					sLogger.debug("Write Last Move Stack Written:" + pStack);
					String lLastMoveEvaluated = pBoardStateToExpand.getFileIndexString() + "|" + pStack;
					writeLastMoveEvaluated(lLastMoveEvaluated);
					
					sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					return;
				}
				
				if (!isTimeToTerminate()) {
					String lStack = pStack != null
							? pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList
							: lSubBoardState.getFileIndexString() + ":" + lStackList;


					if (!lEvaluatedAlready) {
						sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
						int lNextLayersToRead = 1;
						if (pBoardStateToExpand.getFileIndexString().length() < UPPER_LAYERS_THRESHOLD) {
							sLogger.debug("Write Last Move Stack Written:" + lStack);
							String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;
							writeLastMoveEvaluated(lLastMoveEvaluated);

							lNextLayersToRead = LOWER_LAYERS_DEPTH;
							performExhaustiveSearch(lSubBoardState, lStack, null, lNextLayersToRead);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());

						} else if (pBoardStateToExpand.getFileIndexString().length() == UPPER_LAYERS_THRESHOLD) {
							sLogger.debug("Write Last Move Stack Written:" + lStack);
							String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;
							writeLastMoveEvaluated(lLastMoveEvaluated);

							lNextLayersToRead = UPPER_LAYERS_DEPTH;
							performExhaustiveSearch(lSubBoardState, lStack, null, lNextLayersToRead);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());

							// Mark Completion of evaluation for SubBoardStates
							markSubBoardsComplete(lSubBoardState, pLayersToRead);
						} 
					} else {
						sLogger.error("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
					}

				} else {
					sLogger.debug("Timer has expired!");
					
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
					return;
				}
				sLogger.debug("Recursive Call Complete: " + lSubBoardState.getFileIndexString());
			}
			
			
			if (!isTimeToTerminate()) {
				// Mark Completion of evaluation for SubBoardStates
				markSubBoardsComplete(pBoardStateToExpand, pLayersToRead);
			}

		} else {
			sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
		}


		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}

	public ArrayList<BoardState> expandBoardStates(BoardState pBoardStateToExpand, int pLayers, boolean pCheckBreadCrumbs) throws KnowledgeBaseException {

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		if (pLayers <=0 ) {
			return null;
		} else {
			pLayers--;
		}
		
		sLogger.debug("Executing Expansion of: " + pBoardStateToExpand.getFileIndexString());

		boolean lEvaluatedAlready = false;
		try {
			lEvaluatedAlready = pBoardStateToExpand.getEvaluationThreadInfoBit((byte) 0, mLogContext);

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

		BoardState lCurrentBoardStateToExpand = pBoardStateToExpand;

		ExpansionTask lExpandNodeThread = new ExpansionTask(lCurrentBoardStateToExpand, mLogContext);
		boolean lSuccess = lExpandNodeThread.executeTask();

		if (!lSuccess) {
			mTransactionSuccessful = false;
			sLogger.debug("Exhaustive Search was not successful");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}

		sLogger.debug("The starting index is set! Expansion Successful to: "
				+ lCurrentBoardStateToExpand.getFileIndexString());

		if (lEvaluatedAlready) {
			sLogger.error("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return null;
		}

		ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();
		
		if (pCheckBreadCrumbs && (lSubBoardStates == null || lSubBoardStates.isEmpty())) {
			mTransactionSuccessful = false;
			sLogger.debug("Exhaustive Search was not successful");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}
		
		
		

		if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {

			if (pBoardStateToExpand.getFileIndexString().length() < UPPER_LAYERS_THRESHOLD) {
				if (pBoardStateToExpand.getFileIndexString().length() % 2 == 1) {
					sortSubBoardStateByScore(lSubBoardStates);
				} else {
					sortSubBoardStateByReverseScore(lSubBoardStates);
				}
			}
	
			if (pLayers > 0) {
				ArrayList<BoardState> lAllExpandedBoardStates = new ArrayList<BoardState>();
				for (Iterator <BoardState>lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					BoardState lNextBoardStateToExpand = lIterator.next();
					ArrayList<BoardState> lExpandedBoardStates = expandBoardStates (lNextBoardStateToExpand, pLayers, false);
					if (lExpandedBoardStates != null && !lExpandedBoardStates.isEmpty() ) {
						lAllExpandedBoardStates.addAll(lExpandedBoardStates);
					}
					if (lAllExpandedBoardStates.size() >= MAX_BOARDSTATES_UPPER_LEVEL) {
						
						sLogger.error("AllExpandedBoardStates: "
								+ lAllExpandedBoardStates.size());

							
						
						sortSubBoardStateByStateString (lAllExpandedBoardStates);

						int lUniqueCount = 0;
						String lCurrentSubBoardStateFileString = null;
						for (Iterator <BoardState> lIter = lAllExpandedBoardStates.iterator(); lIter.hasNext();) {
							BoardState lCurrentSubBoardState = lIter.next();
							if (lCurrentSubBoardStateFileString == null ) {
								lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
								lUniqueCount++;
							} else if (!lCurrentSubBoardStateFileString.equals(lCurrentSubBoardState.getBoardStateString())) {
								lCurrentSubBoardStateFileString = lCurrentSubBoardState.getBoardStateString();
								lUniqueCount++;
							}
						}

						sLogger.error("AllExpandedBoardStates UniqueCount: "
								+ lUniqueCount);

						
						sLogger.error("lAllExpandedBoardStates has Reached Capacity, Draining:" + lAllExpandedBoardStates.size());

						stopAndDrainBoardStates(lAllExpandedBoardStates);

						lAllExpandedBoardStates = new ArrayList<BoardState> ();
					}
				}
				
				return lAllExpandedBoardStates;
			} else {
				return lSubBoardStates;
			}
		
		} else {
			return null;
		}
		
	}

	
	public void stopAndDrainBoardStates (ArrayList<BoardState> pBoardStatesToDrain) throws KnowledgeBaseException {

		for (Iterator <BoardState> lDrainIterator = pBoardStatesToDrain.iterator(); lDrainIterator.hasNext();) {
			BoardState lCurrentBoardStateToDrain = lDrainIterator.next();
			
			ArrayList<BoardState> lSubBoardStates = expandBoardStates(lCurrentBoardStateToDrain, 1, false);
			
			if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {
				stopAndDrainBoardStates (lSubBoardStates);
			}
			
			markSubBoardsComplete(lCurrentBoardStateToDrain, 1);
			
		}
		
		
	}
	
	
	
	
	
	public void markSubBoardsComplete(BoardState pBoardStateToMark, int pLayers) throws KnowledgeBaseException {

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		
		if (pLayers <=0 ) {
			return;
		} else {
			pLayers--;
		}
		
		sLogger.debug("Executing Marking of: " + pBoardStateToMark.getFileIndexString());

		BoardState lCurrentBoardStateToExpand = pBoardStateToMark;

		ExpansionTask lExpandNodeThread = new ExpansionTask(lCurrentBoardStateToExpand, mLogContext);
		boolean lSuccess = lExpandNodeThread.executeTask();

		if (!lSuccess) {
			mTransactionSuccessful = false;
			sLogger.debug("Exhaustive Search was not successful");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}

		sLogger.debug("The starting index is set! Expansion Successful to: "
				+ lCurrentBoardStateToExpand.getFileIndexString());


		ArrayList<BoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

		
		
		if (lSubBoardStates != null && !lSubBoardStates.isEmpty()) {
			for (Iterator <BoardState>lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				BoardState lNextBoardStateToMark = lIterator.next();
				
				markSubBoardsComplete(lNextBoardStateToMark, pLayers);
				
				try {
					lNextBoardStateToMark.setEvaluationThreadInfoBit((byte) 0, true, mLogContext);
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
		}
		
		return;
	}

	
	
	
	public void sortSubBoardState(List<BoardState> pBoardStates) {
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

	public void sortSubBoardState(List<BoardState> pBoardStates, HashMap<String, Integer> pOrderedBoardStates) {

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
	

	public void sortSubBoardStateByStateString(List<BoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<BoardState>() {
			public int compare(BoardState p1, BoardState p2) {
				String lMove1StateString = p1.getBoardStateString();
				String lMove2StateString = p2.getBoardStateString();
				return lMove1StateString.compareTo(lMove2StateString);
			}
		});
	}
	

	public void sortSubBoardStateByScore(List<BoardState> pBoardStates) {
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

	public void sortSubBoardStateByReverseScore(List<BoardState> pBoardStates) {
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
