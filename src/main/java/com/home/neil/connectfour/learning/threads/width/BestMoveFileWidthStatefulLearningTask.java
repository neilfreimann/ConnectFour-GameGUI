package com.home.neil.connectfour.learning.threads.width;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsTaskCacheConfig;
import com.home.neil.connectfour.board.GameStateSet;
import com.home.neil.connectfour.boardstate.BoardStateBase;
import com.home.neil.connectfour.boardstate.BoardStateMaximumPerformance;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.tasks.ExpansionPerformanceTask;
import com.home.neil.connectfour.boardstate.tasks.performance.BoardStateMetaDataPerformanceTask;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbExpansionLevel;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbExpansionSet;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbNode;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbs;
import com.home.neil.pool.Pool;
import com.home.neil.task.BasicAppTask;
import com.home.neil.task.BasicAppTaskCacheConfig;
import com.home.neil.task.TaskException;
import com.home.neil.workentity.WorkEntityException;

public class BestMoveFileWidthStatefulLearningTask extends BasicAppTask {
	public static final String CLASS_NAME = BestMoveFileWidthStatefulLearningTask.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	

	private static BestMoveFileWidthStatefulLearningTaskConfig sBestMoveFileWidthStatefulLearningTaskConfig;
	private static BestMoveFileWidthStatefulLearningTaskCacheConfig sBestMoveFileWidthStatefulLearningTaskCacheConfig;

	static {
		try {
			sBestMoveFileWidthStatefulLearningTaskConfig = AppConfig
					.bind(BestMoveFileWidthStatefulLearningTaskConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException
				| IOException e) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
		}
		sBestMoveFileWidthStatefulLearningTaskCacheConfig = new BestMoveFileWidthStatefulLearningTaskCacheConfig(sBestMoveFileWidthStatefulLearningTaskConfig);
	}

	protected BestMoveFileWidthStatefulLearningThread mBestMoveFileWidthStatefulLearningThread = null;
	protected BreadCrumbs mBreadCrumbs = null;
	protected Pool mPool = null;
	protected int mThreadMetaDataBitLocation = 0;

	protected BestMoveFileWidthStatefulLearningTask(BestMoveFileWidthStatefulLearningThread pBestMoveFileWidthStatefulLearningThread, BreadCrumbs pBreadCrumbs,
			Pool pPool) {
		super(sBestMoveFileWidthStatefulLearningTaskCacheConfig);
		mBreadCrumbs = pBreadCrumbs;
		mPool = pPool;
		mBestMoveFileWidthStatefulLearningThread = pBestMoveFileWidthStatefulLearningThread;
		mThreadMetaDataBitLocation = mBestMoveFileWidthStatefulLearningThread.getThreadMetaDataBitLocation();

	}

	protected void executeTask() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		do {
			int lInitialLevel = 1; // The initial level is always 1

			BreadCrumbExpansionSet lBreadCrumbExpansionSet = mBreadCrumbs.getCurrentBreadCrumbExpansionSet();
			if (lBreadCrumbExpansionSet == null) {
				mTaskSuccessful = true;
				mTaskFinished = true;

				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				sLogger.info("No further BreadCrumbExpansion Set can be found.");
				mBestMoveFileWidthStatefulLearningThread.setTerminate();
				return;
			}

			BoardStateBase lRootBoardState;
			try {
				lRootBoardState = new BoardStateMaximumPerformance();
			} catch (FileIndexException e) {
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				mTaskSuccessful = false;
				mTaskFinished = true;
				throw new TaskException(e);
			} // Creating the Root Board State

			List<BreadCrumbExpansionLevel> lBreadCrumbExpansionLevels = null;
			try {
				lBreadCrumbExpansionLevels = expandBoardStateMaximumPerformance(lRootBoardState, mBreadCrumbs.getBreadCrumbExpansionLevels(), lInitialLevel,
						lBreadCrumbExpansionSet.getMetaDataCheckStartingExpansionLevel(), lBreadCrumbExpansionSet.getMaximumExpansionLevel(),
						lBreadCrumbExpansionSet.getMovesPerExpansionLevel());
			} catch (WorkEntityException | FileIndexException e) {
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				mTaskSuccessful = false;
				mTaskFinished = true;
				throw new TaskException(e);
			}

			if (lBreadCrumbExpansionLevels != null) { // The Expansion Set is complete
				mBreadCrumbs.setBreadCrumbExpansionLevels(lBreadCrumbExpansionLevels);
				break;
			}

			mBreadCrumbs.incCurrentExpansionSet();
			mBreadCrumbs.setBreadCrumbExpansionLevels(null);
		} while (!mBestMoveFileWidthStatefulLearningThread.isTimeToTerminate());

		try {
			mBestMoveFileWidthStatefulLearningThread.populateJsonObjectFromStatefulVariables();
		} catch (WorkEntityException e) {
			mTaskSuccessful = false;
			mTaskFinished = true;
			throw new TaskException(e);
		}
		
		mTaskSuccessful = true;
		mTaskFinished = true;
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	

	}

	public LinkedList<BreadCrumbExpansionLevel> expandBoardStateMaximumPerformance (BoardStateBase lBoardStateToExpand, 
			List<BreadCrumbExpansionLevel> pInitializationBreadCrumbsExpansionLevels, 
			int pLevel,	int pStartCheckingLevel, int pMaxLevel, int pMovesPerExpansion) throws WorkEntityException, FileIndexException {
		
		if (pLevel > pMaxLevel) {
			if (mBestMoveFileWidthStatefulLearningThread.isTimeToTerminate()) {
				return new LinkedList <BreadCrumbExpansionLevel> ();
			} else {
				return null;
			}
		}
		
		List <BoardStateBase> lExpandedOrderedBoardStates = null;

		if (pInitializationBreadCrumbsExpansionLevels != null && !pInitializationBreadCrumbsExpansionLevels.isEmpty()) {
			BreadCrumbExpansionLevel lBreadCrumbExpansionLevel = pInitializationBreadCrumbsExpansionLevels.remove(0);
			ExpansionPerformanceTask lExpansionTask = null;

			lExpansionTask = new ExpansionPerformanceTask(mPool, lBoardStateToExpand, false, true, 2);
			lExpansionTask.runTask();

			List <BoardStateBase> lExpandedUnorderedBoardStates = lExpansionTask.getExpandedBoardStates();
			lExpandedOrderedBoardStates = new ArrayList <> ();
			
			BreadCrumbNode lCurrentBreadCrumbNode = lBreadCrumbExpansionLevel.getCurrentExpansionNode();
			boolean lCurrentBreadCrumbNodeFound = false;
			for (BreadCrumbNode lBreadCrumbNode : lBreadCrumbExpansionLevel.getExpandedSubNodes()) {
				if (!lCurrentBreadCrumbNodeFound && !lBreadCrumbNode.getNode().equals(lCurrentBreadCrumbNode.getNode())) {
					continue; //Did not find current node yet
				} else if (lCurrentBreadCrumbNodeFound || lBreadCrumbNode.getNode().equals(lCurrentBreadCrumbNode.getNode())) {
					lCurrentBreadCrumbNodeFound = true;
					for (BoardStateBase lExpandedBoardStateBase : lExpandedUnorderedBoardStates) {
						if (lExpandedBoardStateBase.getCurrentMoveStrings()[0].equals(lBreadCrumbNode.getNode())) {
							lExpandedOrderedBoardStates.add(lExpandedBoardStateBase);
							break;
						}
					}
				}
			}
		}
		
		if (lBoardStateToExpand.getCurrentGameState() == GameStateSet.UNDECIDED) {
			ExpansionPerformanceTask lExpansionTask = null;
			
			BoardStateMetaDataPerformanceTask lReadMetaDataTask = null;
			if (pLevel >= pStartCheckingLevel) {
				lReadMetaDataTask = new BoardStateMetaDataPerformanceTask(mPool, lBoardStateToExpand, BoardStateMetaDataPerformanceTask.METADATAOPERATION.READ, mThreadMetaDataBitLocation);
				lReadMetaDataTask.runTask();
			}

			if (lReadMetaDataTask == null || !lReadMetaDataTask.isValue()) {
				//Move was not evaluated.

				if (lExpandedOrderedBoardStates == null) {
					lExpansionTask = new ExpansionPerformanceTask(mPool, lBoardStateToExpand, false, true, 2);
					lExpansionTask.runTask();
					lExpandedOrderedBoardStates = lExpansionTask.getExpandedBoardStates();
				}

				LinkedList<BreadCrumbExpansionLevel> lBreadCrumbExpansionLevels = null;
				BreadCrumbExpansionLevel lBreadCrumbExpansionLevel = null;
				
				for (BoardStateBase lBoardState : lExpandedOrderedBoardStates) {
					if (lBreadCrumbExpansionLevels == null) {
						lBreadCrumbExpansionLevels = expandBoardStateMaximumPerformance(lBoardState, pInitializationBreadCrumbsExpansionLevels, pLevel + 1, pStartCheckingLevel, pMaxLevel, pMovesPerExpansion);
						if (lBreadCrumbExpansionLevels != null) {
							ArrayList <BoardStateBase> lRemainingBreadCrumbNodes = new ArrayList <> ();
							lRemainingBreadCrumbNodes.add(lBoardState);
							lBreadCrumbExpansionLevel = new BreadCrumbExpansionLevel (pLevel, lBoardState, lRemainingBreadCrumbNodes);
						}
					} else {
						lBreadCrumbExpansionLevel.addExpandedSubNode(lBoardState);
					}		
				} 
				
				if (lBreadCrumbExpansionLevel != null) {
					lBreadCrumbExpansionLevels.addFirst(lBreadCrumbExpansionLevel);
					return lBreadCrumbExpansionLevels;
				}

				if (lReadMetaDataTask != null) {
					BoardStateMetaDataPerformanceTask lWriteMetaDataTask = new BoardStateMetaDataPerformanceTask(lReadMetaDataTask,
							BoardStateMetaDataPerformanceTask.METADATAOPERATION.SET);
					lWriteMetaDataTask.runTask();
				}
			} else {
				sLogger.info("Move Already Evaluated: {{}}", lBoardStateToExpand.getCurrentMoveStrings()[0]);
			}
		}
		
		return null;
	}

	public void initializeImplementation() throws WorkEntityException {
	}

	public void finishImplementation() throws WorkEntityException {
	}

	public void checkImplementation() throws WorkEntityException {
	}


}
