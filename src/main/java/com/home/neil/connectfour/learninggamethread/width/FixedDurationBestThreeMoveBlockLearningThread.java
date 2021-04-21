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

import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.StatefulFixedDurationLearningThread;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;

import old.com.home.neil.connectfour.boardstate.OldBoardState;
import old.com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;


public class FixedDurationBestThreeMoveBlockLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = FixedDurationBestThreeMoveBlockLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + FixedDurationBestThreeMoveBlockLearningThread.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_WIDTH = FixedDurationBestThreeMoveBlockLearningThread.class.getSimpleName() + ".dat";

	private int mStartingDepth = 0;
	private int mEndingDepth = 0;

	private String mStartingIndex = null;
	
	private static FixedDurationBestThreeMoveBlockLearningThread sInstance = null;
	
	public synchronized void renameThread(String pLogContext) {
		sLogger.trace("Entering");
		sThreadNumber++;

		setName(FixedDurationBestThreeMoveBlockLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = FixedDurationBestThreeMoveBlockLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		sLogger.trace("Exiting");
	}



	public static synchronized FixedDurationBestThreeMoveBlockLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		if (sInstance == null) {
			sInstance = new FixedDurationBestThreeMoveBlockLearningThread(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread (sInstance,sInstance.getBeanName());
		}
		
		sLogger.trace("Exiting");
		return sInstance;
	}


	private FixedDurationBestThreeMoveBlockLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException {
		super(pKnowledgeBaseFilePool, pDurationToRunInMs, FILENAME_FIXED_DURATION_LINEAR_WIDTH, pLogContext);
		mBeanName = MBEAN_NAME;
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
	}

	
	public void runLearningThread() throws IOException, ConfigurationException {
		sLogger.trace("Entering");

		
		
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

		int lIncrementer = 1;

		mStartingIndex = lLastMoveEvaluated;
		
		while (!isTimeToTerminate()) {

			try {
				performExhaustiveSearch(mCurrentBoardState, null, lBreadCrumbsStack);
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
					mEndingDepth = ((int) (((lLastMoveEvaluated.length() - 1) / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() * lIncrementer);
				} else {
					mEndingDepth = ((int) (lLastMoveEvaluated.length() / KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile()) + 1) * KnowledgeBaseFilePool.getMasterInstance().getActionsPerFile() * lIncrementer;
				}
				sLogger.debug("Starting level is now: " + mStartingDepth);
				sLogger.debug("Ending level is now: " + mEndingDepth);
			}
		}
		sLogger.debug("Timer Exhausted");
		mTransactionSuccessful = true;

		sLogger.trace("Exiting");
	}

	private void performExhaustiveSearch(OldBoardState pBoardStateToExpand, String pStack, String pBreadCrumbs) throws KnowledgeBaseException {
		sLogger.trace("Entering");
		
		// We found the starting index, begin where we left off
		if (mStartingIndex != null && !mStartingIndex.trim().equals("") && mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
			sLogger.debug("The starting index equals current Board State set! Unsetting mStartingIndex: " + pBoardStateToExpand.getFileIndexString());
			mStartingIndex = null;
		// Still going to where we left off
		} else if (mStartingIndex != null && !mStartingIndex.trim().equals("") && !mStartingIndex.equals(pBoardStateToExpand.getFileIndexString())) {
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
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}
			
			sLogger.debug("The starting index is set! Initializing Expansion to: " + pBoardStateToExpand.getFileIndexString());

			//First Layer Expansion
			
			ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
			boolean lSuccess = lExpandNodeThread.executeTask();

			if (!lSuccess) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}

			sLogger.debug("The starting index is set! Expansion Successful to: " + pBoardStateToExpand.getFileIndexString());
			
			
			// Second Layer Expansion
			
			ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

			if (lSubBoardStates == null || lSubBoardStates.isEmpty()) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				sLogger.trace("Exiting");
				throw new KnowledgeBaseException();
			}

			ArrayList<OldBoardState> lSubBoardStatesLevel2 = new ArrayList <OldBoardState>();
			
			for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {

				OldBoardState lSubBoardState = lIterator.next();
				sLogger.debug("Level Two Expansion: " + lSubBoardState.getFileIndexString());

				ExpansionTask lExpandNodeThreadLevel2 = new ExpansionTask(lSubBoardState, mLogContext);
				lSuccess = lExpandNodeThreadLevel2.executeTask();

				if (!lSuccess) {
					mTransactionSuccessful = false;
					sLogger.error("Exhaustive Search was not successful");
					sLogger.trace("Exiting");
					throw new KnowledgeBaseException();
				}

				ArrayList<OldBoardState> lSubBoardStatesLevel2Sub = lExpandNodeThreadLevel2.getSubBoardStates();
				
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
			
			for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				OldBoardState lSubBoardState = lIterator.next();
				
				lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");
				
			}

			lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);
			
			String lStackList = lStackBuilder.toString();
			
			boolean lStartTraversing = false;

			for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				OldBoardState lSubBoardState = lIterator.next();

				
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

						performExhaustiveSearch(lSubBoardState, lStack, lNextLayers);

					} else {
						sLogger.debug("Timer has expired!");
						return;
					}
				}
			}

			sLogger.trace("Exiting");
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

		ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();
		
		
		if (lSubBoardStates != null) {
			sLogger.debug("Sorting SubBoard States:" + pBoardStateToExpand.getFileIndexString());

			sortSubBoardStateByScore (lSubBoardStates);
			
			ArrayList<OldBoardState> lSubBoardStatesSublist = new ArrayList<OldBoardState>();
			
			int count = 0;
			for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				OldBoardState lBoardState = lIterator.next();
				lSubBoardStatesSublist.add (lBoardState);
				if (count >= 3) {
					break;
				}
				count++;
			}
			
			// Second Level Expansion

			ArrayList<OldBoardState> lSubBoardStatesLevel2 = new ArrayList <OldBoardState>();
			
			for (Iterator<OldBoardState> lIterator = lSubBoardStatesSublist.iterator(); lIterator.hasNext();) {

				OldBoardState lSubBoardState = lIterator.next();
				sLogger.debug("Level Two Expansion: " + lSubBoardState.getFileIndexString());

				ExpansionTask lExpandNodeThreadLevel2 = new ExpansionTask(lSubBoardState, mLogContext);
				lSuccess = lExpandNodeThreadLevel2.executeTask();

				if (!lSuccess) {
					mTransactionSuccessful = false;
					sLogger.error("Exhaustive Search was not successful");
					sLogger.trace("Exiting");
					throw new KnowledgeBaseException();
				}

				ArrayList<OldBoardState> lSubBoardStatesLevel2Sub = lExpandNodeThreadLevel2.getSubBoardStates();
				
				if (lSubBoardStatesLevel2Sub != null) {
					sLogger.debug("Sorting SubBoard Level Two States:" + pBoardStateToExpand.getFileIndexString());

					sortSubBoardStateByReverseScore (lSubBoardStatesLevel2Sub);

					ArrayList<OldBoardState> lSubBoardStatesLevel2SubList = new ArrayList<OldBoardState>();
					
					count = 0;
					for (Iterator<OldBoardState> lIterator2 = lSubBoardStatesLevel2Sub.iterator(); lIterator2.hasNext();) {
						OldBoardState lBoardState = lIterator2.next();
						lSubBoardStatesLevel2SubList.add (lBoardState);
						if (count >= 3) {
							break;
						}
						count++;
					}
					
					
					lSubBoardStatesLevel2.addAll(lSubBoardStatesLevel2SubList);
				}
				

				sLogger.debug("Level Two Expansion Complete: " + lSubBoardState.getFileIndexString());
			}


			if (lSubBoardStatesLevel2 != null && !lSubBoardStatesLevel2.isEmpty()) {


				StringBuilder lStackBuilder = new StringBuilder();
				
				for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					OldBoardState lSubBoardState = lIterator.next();
					
					lStackBuilder.append(lSubBoardState.getFileIndexString()).append(",");
					
				}

				lStackBuilder = lStackBuilder.deleteCharAt(lStackBuilder.length() - 1);
				
				String lStackList = lStackBuilder.toString();
				
				
				
				for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					OldBoardState lSubBoardState = lIterator.next();

					sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
					if (!isTimeToTerminate()) {
						String lStack = pStack != null ? 
								pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList : 
									lSubBoardState.getFileIndexString() + ":" + lStackList;
				
				
						sLogger.debug("Write Last Move Stack Written:" + lStack);
						
						String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;

						writeLastMoveEvaluated(lLastMoveEvaluated);
						
						performExhaustiveSearch(lSubBoardState, lStack, null);
						
					} else {
						sLogger.debug("Timer has expired!");
						return;
					}
					sLogger.debug("Recursive Call Complete: " + lSubBoardState.getFileIndexString());


					
				}
			} else {
				sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
			}
			
		}  else {
			sLogger.debug("Sorting SubBoard States is empty!:" + pBoardStateToExpand.getFileIndexString());
		}

		sLogger.trace("Exiting");
	}


	public void sortSubBoardState(List <OldBoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<OldBoardState>() {
			public int compare(OldBoardState p1, OldBoardState p2) {
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



	
	public void sortSubBoardState(List <OldBoardState> pBoardStates, HashMap <String, Integer>  pOrderedBoardStates) {
		
		Collections.sort(pBoardStates, new Comparator<OldBoardState>() {
			public int compare(OldBoardState p1, OldBoardState p2) {
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


	
	
	
	public void sortSubBoardStateByScore(List <OldBoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<OldBoardState>() {
			public int compare(OldBoardState p1, OldBoardState p2) {
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
	
	
	public void sortSubBoardStateByReverseScore(List <OldBoardState> pBoardStates) {
		Collections.sort(pBoardStates, new Comparator<OldBoardState>() {
			public int compare(OldBoardState p1, OldBoardState p2) {
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
