package com.home.neil.connectfour.boardstate.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsReadTask;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsWriteTask;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.Column;
import com.home.neil.connectfour.boardstate.ColumnSet;
import com.home.neil.connectfour.boardstate.GameState;
import com.home.neil.connectfour.boardstate.GameStateSet;
import com.home.neil.connectfour.boardstate.InvalidMoveException;
import com.home.neil.connectfour.boardstate.Move;
import com.home.neil.connectfour.boardstate.MoveSet;
import com.home.neil.connectfour.boardstate.PlayerSet;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score.BoardStateKnowledgeBaseFileIndex;
import com.home.neil.connectfour.boardstate.knowledgebase.locks.AddressLockHolderTask;
import com.home.neil.connectfour.boardstate.knowledgebase.locks.AddressLocks;
import com.home.neil.connectfour.boardstate.logger.BoardStateLogger;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;
import com.home.neil.pool.Pool;
import com.home.neil.task.TaskException;

public class ExpansionTask extends AddressLockHolderTask {

	public static final String CLASS_NAME = ExpansionTask.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	protected static KnowledgeBaseCompressableCacheSegmentConfig sKnowledgeBaseConfig = null;

	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
			sKnowledgeBaseConfig = AppConfig.bind(KnowledgeBaseCompressableCacheSegmentConfig.class);

		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	private BoardState mFirstBoardStateToExpand = null;
	private ArrayList<BoardState> mFinalExpandedBoardStates = null;
	private int mLevelsToExpand = 2;

	public ExpansionTask(Pool pPool, BoardState pBoardStateToExpand, boolean pRecordContext, boolean pRecordTaskStatistics, int pLevelsToExpand) {
		super(pPool, pRecordContext, pRecordTaskStatistics);
		mFirstBoardStateToExpand = pBoardStateToExpand;
		mLevelsToExpand = pLevelsToExpand;
	}

	public ExpansionTask(Pool pPool, boolean pRecordContext, boolean pRecordTaskStatistics, int pLevelsToExpand) throws TaskException {
		super(pPool, pRecordContext, pRecordTaskStatistics);
		mFirstBoardStateToExpand = new BoardState();
		mLevelsToExpand = pLevelsToExpand;
	}

	@Override
	protected void executeTask() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mFinalExpandedBoardStates = new ArrayList<BoardState>();

		try {
			ArrayList<BoardState> lBoardStatesToEvaluate = new ArrayList<>();
			lBoardStatesToEvaluate.add(mFirstBoardStateToExpand);

			for (int i = 0; i < mLevelsToExpand && !lBoardStatesToEvaluate.isEmpty(); i++) {
				ArrayList<BoardState> lNextBoardStates = new ArrayList<>();
				for (BoardState lBoardStateToEvaluate : lBoardStatesToEvaluate) {
					lNextBoardStates.addAll(expandAndRescoreNode(lBoardStateToEvaluate));
				}
				lBoardStatesToEvaluate = lNextBoardStates;
			}

			mFinalExpandedBoardStates = lBoardStatesToEvaluate;

		} catch (Exception e) {
			mTaskSuccessful = false;
			mTaskFinished = false;
			throw e;
		}

		mTaskSuccessful = true;
		mTaskFinished = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}

	@SuppressWarnings("squid:S3776")
	private ArrayList<BoardState> expandAndRescoreNode(BoardState lBoardStateToExpand) throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		ArrayList<BoardState> lExpandedBoardStatesToReturn = new ArrayList<>();

		BoardState lCurrentBoardState = lBoardStateToExpand;
		BoardState lParentBoardState = lBoardStateToExpand.getPreviousBoardState();

		BoardStateKnowledgeBaseFileIndex lParentBoardStateKnowledgeBaseFileIndex = null;
		BoardStateKnowledgeBaseFileIndex lCurrentBoardStateKnowledgeBaseFileIndex = null;

		try {
			lCurrentBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lCurrentBoardState);
			if (lParentBoardState != null) {
				lParentBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lParentBoardState);
			}
		} catch (FileIndexException e) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}
		byte lRescoreValue = 0;

		String lCurrentBoardStateMoveString = "";
		lCurrentBoardStateMoveString = lCurrentBoardState.constructMoveStrings(true)[0];

		sLogger.debug("Reserving the Current BoardState: {{}}", lCurrentBoardStateMoveString);
		boolean lStateReserved = reserveAddress(lCurrentBoardStateKnowledgeBaseFileIndex);
		if (!lStateReserved) {
			sLogger.error("COULD NOT GET THE BOARDSTATE RESERVED!");
			mTaskSuccessful = false;
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Unable to reserve the boardstate");
		}
		sLogger.debug("Current BoardState is reserved: {{}}", lCurrentBoardStateMoveString);

		sLogger.debug("Expanding SubBoard States: {{}}", lCurrentBoardStateMoveString);
		lExpandedBoardStatesToReturn = expandSubBoardStates(lCurrentBoardState);
		sLogger.debug("Current BoardState SubBoard States Expanded: {{}}", lCurrentBoardStateMoveString);

		if (lParentBoardState == null) {
//			sLogger.debug("Not Rescoring BoardState: {{}}", lCurrentBoardStateMoveString);
//			lRescoreValue = rescoreNode(lCurrentBoardState, lExpandedBoardStatesToReturn);
//
//			if (lCurrentBoardState.getMoveScore() != lRescoreValue) {
//				lCurrentBoardState.setMoveScore(lRescoreValue);
//				writeMoveScoreToKnowledge(lCurrentBoardStateKnowledgeBaseFileIndex);
//				sLogger.info("BoardState is rescored: {{}} to {{}}", lCurrentBoardStateMoveString, lRescoreValue);
//			} else {
//				sLogger.info("BoardState is   scored: {{}} to {{}}", lCurrentBoardStateMoveString, lRescoreValue);
//			}
		}

		sLogger.debug("Releasing the Current BoardState: {{}}", lCurrentBoardStateMoveString);
		releaseAddress(lCurrentBoardStateKnowledgeBaseFileIndex);
		sLogger.debug("Current BoardState is Released: {{}}", lCurrentBoardStateMoveString);

		ArrayList<BoardState> lExpandedBoardStates = lExpandedBoardStatesToReturn;

		while (lParentBoardState != null && !lExpandedBoardStates.isEmpty()) {
			lCurrentBoardStateMoveString = lCurrentBoardState.constructMoveStrings(true)[0];
			String lParentBoardStateMoveString = "";
			lParentBoardStateMoveString = lParentBoardState.constructMoveStrings(true)[0];

			sLogger.debug("Reserving the Parent BoardState for Current BoardState Rescore: {{}}", lParentBoardStateMoveString);
			boolean lRescoreStateReserved = reserveAddress(lParentBoardStateKnowledgeBaseFileIndex);
			if (!lRescoreStateReserved) {
				sLogger.error("COULD NOT GET THE PARENT BOARDSTATE RESERVED!");
				mTaskSuccessful = false;
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				throw new TaskException("Unable to reserve the boardstate");
			}
			sLogger.debug("Parent BoardState is reserved: {{}}", lParentBoardStateMoveString);

			sLogger.debug("Rescoring BoardState: {{}}", lCurrentBoardStateMoveString);
			lRescoreValue = rescoreNode(lCurrentBoardState, lExpandedBoardStates);

			if (lCurrentBoardState.getMoveScore() == lRescoreValue) {
				sLogger.debug("BoardState is   scored: {{}} to {{}}", lCurrentBoardStateMoveString, lRescoreValue);
				// Rescore value is the same, No need to rescore beyond this
				sLogger.debug("Releasing the Parent BoardState: {{}}", lParentBoardStateMoveString);
				releaseAddress(lParentBoardStateKnowledgeBaseFileIndex);
				sLogger.debug("Parent BoardState is Released: {{}}", lParentBoardStateMoveString);

				lCurrentBoardState = lParentBoardState;
				lCurrentBoardStateKnowledgeBaseFileIndex = lParentBoardStateKnowledgeBaseFileIndex;
//				lParentBoardState = lCurrentBoardState.getPreviousBoardState();
//				if (lParentBoardState != null) {
//					try {
//						lParentBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lParentBoardState);
//					} catch (FileIndexException e) {
//						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
//							sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
//						}
//						throw new TaskException(e);
//					}
//				}
				break;
			}

			lCurrentBoardState.setMoveScore(lRescoreValue);
			writeMoveScoreToKnowledge(lCurrentBoardStateKnowledgeBaseFileIndex);
			sLogger.debug("BoardState is rescored: {{}} to {{}}", lCurrentBoardStateMoveString, lRescoreValue);

			sLogger.debug("Expanding SubBoard States: {{}}", lParentBoardStateMoveString);
			lExpandedBoardStates = expandSubBoardStates(lParentBoardState);
			sLogger.debug("Current BoardState SubBoard States Expanded: {{}}", lParentBoardStateMoveString);

//			if (lParentBoardState.getPreviousBoardState() == null) {
//				sLogger.debug("Rescoring Root BoardState: {{}}", lParentBoardStateMoveString);
//				lRescoreValue = rescoreNode(lParentBoardState, lExpandedBoardStates);
//				lParentBoardState.setMoveScore(lRescoreValue);
//				writeMoveScoreToKnowledge(lParentBoardStateKnowledgeBaseFileIndex);
//				sLogger.debug("Root BoardState is rescored: {{}}", lParentBoardStateMoveString);
//			}

			sLogger.debug("Releasing the Parent BoardState: {{}}", lParentBoardStateMoveString);
			releaseAddress(lParentBoardStateKnowledgeBaseFileIndex);
			sLogger.debug("Parent BoardState is Released: {{}}", lParentBoardStateMoveString);

			lCurrentBoardState = lParentBoardState;
			lCurrentBoardStateKnowledgeBaseFileIndex = lParentBoardStateKnowledgeBaseFileIndex;
			lParentBoardState = lCurrentBoardState.getPreviousBoardState();
			if (lParentBoardState != null) {
				try {
					lParentBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lParentBoardState);
				} catch (FileIndexException e) {
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
					}
					throw new TaskException(e);
				}
			}

		}

		mTaskSuccessful = true;
		mTaskFinished = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return lExpandedBoardStatesToReturn;
	}

	private byte rescoreNode(BoardState pBoardState, ArrayList<BoardState> pSubBoardStates) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		// highest or lowest scoring SubBoardState
		// depth of the current object
		Move lMove = pBoardState.decodeMove();

		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Move: {{}}", lMove.getAttributeName());
		}

//		if (lMove.getPlayer() == PlayerSet.getRealPlayer(2) || lMove.getPlayer() == PlayerSet.NULL_PLAYER) { // current move is opponent move, reorder sub
//			// moves by highest score
//			pSubBoardStates
//					.sort((BoardState p1, BoardState p2) -> (p2.getMoveScore() == p1.getMoveScore() ? 0 : (p2.getMoveScore() > p1.getMoveScore() ? 1 : -1)));
//		} else { // current move is a self move, reorder sub moves by lowest
//					// score
//			pSubBoardStates
//					.sort((BoardState p1, BoardState p2) -> (p2.getMoveScore() == p1.getMoveScore() ? 0 : (p2.getMoveScore() < p1.getMoveScore() ? 1 : -1)));
//		}

		byte lScore = pSubBoardStates.get(0).getMoveScore();

		sLogger.debug("CurrentNode: Move: {{}} Score: {{}}", lMove.getColumn(), pSubBoardStates.get(0).getMoveScore());

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lScore;
	}

	@SuppressWarnings("squid:S3776")
	private ArrayList<BoardState> expandSubBoardStates(BoardState pNodeToExpand) throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		ArrayList<BoardState> lSubBoardState = new ArrayList<>();

		GameState lGameState = pNodeToExpand.decodeGameState();

		if (lGameState != GameStateSet.UNDECIDED) {
			sLogger.debug("Invalid GameState.  Cannot Expand Move");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return lSubBoardState;
		}

		Move lMove = pNodeToExpand.decodeMove();

		// Current Move was an opponent move
		if (lMove.getPlayer() == PlayerSet.getAllPlayer(2) || lMove.getPlayer() == PlayerSet.NULL_PLAYER) {
			for (Column lColumn : ColumnSet.getColumns()) {
				try {
					BoardState lNewBoardState = new BoardState(pNodeToExpand, MoveSet.getMove(PlayerSet.getAllPlayer(1), lColumn));

					BoardStateKnowledgeBaseFileIndex lNewBoardStateKnowledgeBaseFileIndex = null;
					try {
						lNewBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lNewBoardState);
					} catch (FileIndexException e) {
						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
							sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
						}
						throw new TaskException(e);
					}

					readMoveScoreFromKnowledgeBase(lNewBoardStateKnowledgeBaseFileIndex);

					BoardStateLogger.logBoardState(lNewBoardState);

					lSubBoardState.add(lNewBoardState);
				} catch (InvalidMoveException eE) {
					if (sLogger.isDebugEnabled()) {
						sLogger.debug("Invalid Evaluated Move: {{}}", lColumn.getColumn());
					}
				}
			}
			
			lSubBoardState.sort((BoardState p1, BoardState p2) -> (p2.getMoveScore() == p1.getMoveScore() ? 0 : (p2.getMoveScore() > p1.getMoveScore() ? 1 : -1)));
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return lSubBoardState;
		} else if (lMove.getPlayer() == PlayerSet.getAllPlayer(1)) {
			for (Column lColumn : ColumnSet.getColumns()) {
				try {
					BoardState lNewBoardState = new BoardState(pNodeToExpand, MoveSet.getMove(PlayerSet.getAllPlayer(2), lColumn));

					BoardStateKnowledgeBaseFileIndex lNewBoardStateKnowledgeBaseFileIndex = null;
					try {
						lNewBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(lNewBoardState);
					} catch (FileIndexException e) {
						if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
							sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
						}
						throw new TaskException(e);
					}

					readMoveScoreFromKnowledgeBase(lNewBoardStateKnowledgeBaseFileIndex);

					BoardStateLogger.logBoardState(lNewBoardState);

					lSubBoardState.add(lNewBoardState);
				} catch (InvalidMoveException eE) {
					if (sLogger.isDebugEnabled()) {
						sLogger.debug("Invalid Evaluated Move: {{}}", lColumn.getColumn());
					}
				}
			}
			lSubBoardState.sort((BoardState p1, BoardState p2) -> (p2.getMoveScore() == p1.getMoveScore() ? 0 : (p2.getMoveScore() < p1.getMoveScore() ? 1 : -1)));
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return lSubBoardState;
		} else {
			sLogger.error("Could not get an optimized move!");

			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return lSubBoardState;
		}

	}

	public BoardState getBoardStateToExpand() {
		return mFirstBoardStateToExpand;
	}

	public List<BoardState> getExpandedBoardStates() {
		return mFinalExpandedBoardStates;
	}

	private static final byte SURROGATE_ZERO = 120;
	private static final byte NULL_VALUE = 0;

	public void readMoveScoreFromKnowledgeBase(BoardStateKnowledgeBaseFileIndex pBoardStateKnowledgeBaseFileIndexToUnlock) throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for reading");
		CompressableCacheSegmentOperationsReadTask lCompressableCacheSegmentOperationsReadTask = new CompressableCacheSegmentOperationsReadTask(mPool,
				pBoardStateKnowledgeBaseFileIndexToUnlock.getPoolItemId(), pBoardStateKnowledgeBaseFileIndexToUnlock.getFileDetails(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getIndexDetails());
		lCompressableCacheSegmentOperationsReadTask.runTask();

		if (!lCompressableCacheSegmentOperationsReadTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsReadTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred!");
		}

		byte[] lScoreSegment = lCompressableCacheSegmentOperationsReadTask.getIndexDetails().getIndexSegment();
		if (lScoreSegment == null || lScoreSegment.length == 0) {
			sLogger.error("Knowledge Base Error occurred! Score Segment is null or empty!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred! Score Segment is null or empty!!");
		}

		byte lScore = lScoreSegment[0];

		if (lScore != NULL_VALUE) {
			if (lScore == SURROGATE_ZERO) {
				lScore = 0;
			}
			sLogger.debug("Score is found, setting to BoardState");
			pBoardStateKnowledgeBaseFileIndexToUnlock.getBoardState().setMoveScore(lScore);
		} else {
			sLogger.debug("Score is not found, writing from Current Board State");
			sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");

			pBoardStateKnowledgeBaseFileIndexToUnlock.setScoreToWriteFromBoardState(SURROGATE_ZERO);

			CompressableCacheSegmentOperationsWriteTask lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
					pBoardStateKnowledgeBaseFileIndexToUnlock.getPoolItemId(), pBoardStateKnowledgeBaseFileIndexToUnlock.getFileDetails(),
					pBoardStateKnowledgeBaseFileIndexToUnlock.getIndexDetails());
			lCompressableCacheSegmentOperationsWriteTask.runTask();
			if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
				sLogger.error("Knowledge Base Error occurred!");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				throw new TaskException("Knowledge Base Error occurred!");
			}

			if (!pBoardStateKnowledgeBaseFileIndexToUnlock.getAddress().equals(pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalAddress())) {
				lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
						pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalPoolItemId(),
						pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalFileDetails(),
						pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalIndexDetails());
				lCompressableCacheSegmentOperationsWriteTask.runTask();
				if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
					sLogger.error("Knowledge Base Error occurred!");
					if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
						sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
					}
					throw new TaskException("Knowledge Base Error occurred!");
				}
			}

		}

		sLogger.debug("Move: {{}} Move Score {{}}", pBoardStateKnowledgeBaseFileIndexToUnlock.getAddress(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getBoardState().getMoveScore());
		sLogger.debug("Move: {{}} Move Score {{}}", pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalAddress(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getBoardState().getMoveScore());

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public void writeMoveScoreToKnowledge(BoardStateKnowledgeBaseFileIndex pBoardStateKnowledgeBaseFileIndexToUnlock) throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");

		pBoardStateKnowledgeBaseFileIndexToUnlock.setScoreToWriteFromBoardState(SURROGATE_ZERO);

		CompressableCacheSegmentOperationsWriteTask lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
				pBoardStateKnowledgeBaseFileIndexToUnlock.getPoolItemId(), pBoardStateKnowledgeBaseFileIndexToUnlock.getFileDetails(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getIndexDetails());
		lCompressableCacheSegmentOperationsWriteTask.runTask();
		if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred!");
		}

		if (!pBoardStateKnowledgeBaseFileIndexToUnlock.getAddress().equals(pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalAddress())) {
			lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
					pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalPoolItemId(), pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalFileDetails(),
					pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalIndexDetails());
			lCompressableCacheSegmentOperationsWriteTask.runTask();
			if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
				sLogger.error("Knowledge Base Error occurred!");
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				throw new TaskException("Knowledge Base Error occurred!");
			}
		}

		sLogger.debug("Move: {{}} Move Score {{}}", pBoardStateKnowledgeBaseFileIndexToUnlock.getAddress(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getBoardState().getMoveScore());
		sLogger.debug("Move: {{}} Move Score {{}}", pBoardStateKnowledgeBaseFileIndexToUnlock.getReciprocalAddress(),
				pBoardStateKnowledgeBaseFileIndexToUnlock.getBoardState().getMoveScore());

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

}
