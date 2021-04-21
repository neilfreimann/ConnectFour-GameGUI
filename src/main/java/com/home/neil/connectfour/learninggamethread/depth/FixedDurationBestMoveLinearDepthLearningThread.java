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

import com.home.neil.connectfour.boardstate.InvalidMoveException;
import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.knowledgebase.exception.KnowledgeBaseException;
import com.home.neil.connectfour.learninggamethread.StatefulFixedDurationLearningThread;
import com.home.neil.connectfour.managers.FixedDurationLearningThreadManager;
import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;

import old.com.home.neil.connectfour.boardstate.OldBoardState;
import old.com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;


public class FixedDurationBestMoveLinearDepthLearningThread extends StatefulFixedDurationLearningThread {
	public static final String CLASS_NAME = FixedDurationBestMoveLinearDepthLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + FixedDurationBestMoveLinearDepthLearningThread.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String FILENAME_FIXED_DURATION_LINEAR_WIDTH = FixedDurationBestMoveLinearDepthLearningThread.class.getSimpleName() + ".dat";

	private int mStartingDepth = 0;

	private String mStartingIndex = null;
	
	private static FixedDurationBestMoveLinearDepthLearningThread sInstance = null;
	
	public synchronized void renameThread(String pLogContext) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		sThreadNumber++;

		setName(FixedDurationBestMoveLinearDepthLearningThread.class.getSimpleName() + "." + sThreadNumber);

		if (pLogContext == null || pLogContext.trim().isEmpty()) {
			mLogContext = FixedDurationBestMoveLinearDepthLearningThread.class.getSimpleName() + "." + sThreadNumber;
			ThreadContext.put("LogContext", mLogContext);
		} else {
			mLogContext = pLogContext;
			ThreadContext.put("LogContext", mLogContext);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}



	public static synchronized FixedDurationBestMoveLinearDepthLearningThread getInstance(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, IOException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		if (sInstance == null) {
			sInstance = new FixedDurationBestMoveLinearDepthLearningThread(pKnowledgeBaseFilePool, pDurationToRunInMs, pLogContext);

			FixedDurationLearningThreadManager lStatefulFixedDurationLearningThreadManager = FixedDurationLearningThreadManager.getInstance();
			lStatefulFixedDurationLearningThreadManager.registerFixedDurationLearningThread (sInstance,sInstance.getBeanName());
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return sInstance;
	}


	private FixedDurationBestMoveLinearDepthLearningThread(KnowledgeBaseFilePool pKnowledgeBaseFilePool, long pDurationToRunInMs, String pLogContext) throws ConfigurationException, FileNotFoundException, IOException {
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

		
		
		String [] lBreadCrumbs = mBreadCrumbs.split("\\|");
		
		String lBreadCrumbsStack = null;
				
		if (lBreadCrumbs.length > 1) {
			lBreadCrumbsStack = lBreadCrumbs[1];
		}
		

		String lLastMoveEvaluated = lBreadCrumbs[0];
		
		mStartingDepth = 0;
			
		sLogger.debug("Last Starting level should be: " + mStartingDepth);

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

	private void performExhaustiveSearch(OldBoardState pBoardStateToExpand, String pStack, String pBreadCrumbs) throws KnowledgeBaseException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		//Check for recursion
		boolean lEvaluatedAlready = false;
		try {
			lEvaluatedAlready = pBoardStateToExpand.getEvaluationThreadInfoBit((byte)0, mLogContext);
			
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
			
			if (lEvaluatedAlready) {
				sLogger.error("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return;
			}
			
			
			// Second Layer Expansion
			
			ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();

			if (lSubBoardStates == null || lSubBoardStates.isEmpty()) {
				mTransactionSuccessful = false;
				sLogger.debug("Exhaustive Search was not successful");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
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
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace("Exiting");
					}
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

						if (!lEvaluatedAlready) {
							sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
							performExhaustiveSearch(lSubBoardState, lStack, lNextLayers);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
						} else {
							sLogger.debug("Already Evaluated Recursive Call Aborted");
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

			//Mark Completion of evaluation for SubBoardStates and SubBoardStatesLevel2
			for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
				OldBoardState lSubBoardState = lIterator.next();
				try{
					lSubBoardState.setEvaluationThreadInfoBit((byte)0, true, mLogContext);
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
			
			for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
				OldBoardState lSubBoardState = lIterator.next();
				try{
					lSubBoardState.setEvaluationThreadInfoBit((byte)0, true, mLogContext);
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

		

		
		
		// First Level Expansion
		
		sLogger.debug("Expanding Node:" + pBoardStateToExpand.getFileIndexString());
		
		ExpansionTask lExpandNodeThread = new ExpansionTask(pBoardStateToExpand, mLogContext);
		boolean lSuccess = lExpandNodeThread.executeTask();

		if (!lSuccess) {
			mTransactionSuccessful = false;
			sLogger.error("Exhaustive Search was not successful");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			throw new KnowledgeBaseException();
		}

		sLogger.debug("Node Expansion Successful:" + pBoardStateToExpand.getFileIndexString());

		if (lEvaluatedAlready) {
			sLogger.debug("The index is evaluated already: " + pBoardStateToExpand.getFileIndexString());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return;
		}

		ArrayList<OldBoardState> lSubBoardStates = lExpandNodeThread.getSubBoardStates();
		
		
		if (lSubBoardStates != null) {
			sLogger.debug("Sorting SubBoard States:" + pBoardStateToExpand.getFileIndexString());

			sortSubBoardStateByScore (lSubBoardStates);
			
			// Second Level Expansion

			ArrayList<OldBoardState> lSubBoardStatesLevel2 = new ArrayList <OldBoardState>();
			
			for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {

				OldBoardState lSubBoardState = lIterator.next();
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

				ArrayList<OldBoardState> lSubBoardStatesLevel2Sub = lExpandNodeThreadLevel2.getSubBoardStates();
				
				if (lSubBoardStatesLevel2Sub != null) {
					sLogger.debug("Sorting SubBoard Level Two States:" + pBoardStateToExpand.getFileIndexString());

					sortSubBoardStateByReverseScore (lSubBoardStatesLevel2Sub);
					
					lSubBoardStatesLevel2.addAll(lSubBoardStatesLevel2Sub);
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

					if (!isTimeToTerminate()) {
						String lStack = pStack != null ? 
								pStack + ";" + lSubBoardState.getFileIndexString() + ":" + lStackList : 
									lSubBoardState.getFileIndexString() + ":" + lStackList;
				
				
						sLogger.debug("Write Last Move Stack Written:" + lStack);
						
						String lLastMoveEvaluated = lSubBoardState.getFileIndexString() + "|" + lStack;

						writeLastMoveEvaluated(lLastMoveEvaluated);
						
						if (!lEvaluatedAlready) {
							sLogger.debug("Recursive Call Start: " + lSubBoardState.getFileIndexString());
							performExhaustiveSearch(lSubBoardState, lStack, null);
							sLogger.debug("Recursive Call End: " + lSubBoardState.getFileIndexString());
						} else {
							sLogger.debug("Already Evaluated Recursive Call Aborted");
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

				
				//Mark Completion of evaluation for SubBoardStates and SubBoardStatesLevel2
				for (Iterator<OldBoardState> lIterator = lSubBoardStates.iterator(); lIterator.hasNext();) {
					OldBoardState lSubBoardState = lIterator.next();
					try{
						lSubBoardState.setEvaluationThreadInfoBit((byte)0, true, mLogContext);
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
				
				for (Iterator<OldBoardState> lIterator = lSubBoardStatesLevel2.iterator(); lIterator.hasNext();) {
					OldBoardState lSubBoardState = lIterator.next();
					try{
						lSubBoardState.setEvaluationThreadInfoBit((byte)0, true, mLogContext);
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

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
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
