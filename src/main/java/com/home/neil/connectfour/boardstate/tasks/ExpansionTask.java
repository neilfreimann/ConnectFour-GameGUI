package com.home.neil.connectfour.boardstate.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
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
import com.home.neil.connectfour.boardstate.locks.BoardStateLocks;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;
import com.home.neil.task.BasicAppTask;
import com.home.neil.task.TaskException;

public class ExpansionTask extends BasicAppTask {

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
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}

	private BoardState mBoardStateToExpand = null;
	private ArrayList<BoardState> mExpandedBoardStates = null;

	private BoardState mBoardStateLock = null;
	private Object mLock = new Object();

	protected ExpansionTask(BoardState pBoardStateToExpand, String pLogContext, boolean pRecordTaskStatistics) {
		super(pLogContext, pRecordTaskStatistics);
		mBoardStateToExpand = pBoardStateToExpand;
	}

	@Override
	protected void executeTask() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		try {
			expandAndRescoreNode();
		} catch (Exception e) {
			mTaskSuccessful = false;
			mTaskFinished = false;
			throw new ExpansionTaskException(e);
		}

		mTaskSuccessful = true;
		mTaskFinished = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}

	public void setReservedBoardState(BoardState pBoardState) {
		mBoardStateLock = pBoardState;
		synchronized (mLock) {
			mLock.notifyAll();
		}
	}

	protected boolean reserveBoardState(BoardState pBoardStateToLock) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		BoardStateLocks lBoardStateLocks = BoardStateLocks.getInstance();

		sLogger.debug("Reserving BoardState {}");

		try {
			mBoardStateLock = lBoardStateLocks.reserveBoardState(pBoardStateToLock, this);
		} catch (Exception e1) {
			sLogger.error("Could not reserve the BoardState {}");
			return false;
		}

		sLogger.debug("Waiting BoardState {}");
		synchronized (mLock) {
			while (mBoardStateLock == null) {
				try {
					mLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					sLogger.error("Thread interrupted", e);
				}
			}
		}
		sLogger.debug("Acquired BoardState {}");

		if (mBoardStateLock == null) {
			sLogger.error("COULD NOT GET THE BOARDSTATE RESERVED!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return false;
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return true;
	}

	protected void releaseBoardState(BoardState pBoardStateToUnlock) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		BoardStateLocks lBoardStateLocks = BoardStateLocks.getInstance();

		lBoardStateLocks.releaseBoardState(pBoardStateToUnlock);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}


	private void expandAndRescoreNode() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		BoardState lCurrentBoardState = mBoardStateToExpand;
		BoardState lParentBoardState = mBoardStateToExpand.getPreviousBoardState();

		byte lRescoreValue = 0;
		
		String lCurrentBoardStateMoveString = "";
		if (sLogger.isDebugEnabled()) {
			lCurrentBoardStateMoveString = lCurrentBoardState.constructMoveStrings()[0];
		}
		
		sLogger.debug("Reserving the Current BoardState: {{}}", lCurrentBoardStateMoveString);
		boolean lStateReserved = reserveBoardState(lCurrentBoardState);
		if (!lStateReserved) {
			sLogger.error("COULD NOT GET THE BOARDSTATE RESERVED!");
			mTaskSuccessful = false;
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return;
		}
		sLogger.debug("Current BoardState is reserved: {{}}", lCurrentBoardStateMoveString);
		
		sLogger.debug("Expanding SubBoard States: {{}}", lCurrentBoardStateMoveString);
		mExpandedBoardStates = expandSubBoardStates(lCurrentBoardState);
		sLogger.debug("Current BoardState SubBoard States Expanded: {{}}", lCurrentBoardStateMoveString);

		sLogger.debug("Releasing the Current BoardState: {{}}",lCurrentBoardStateMoveString);
		releaseBoardState(lCurrentBoardState);
		sLogger.debug("Current BoardState is Released: {{}}", lCurrentBoardStateMoveString);
		
		ArrayList <BoardState> lExpandedBoardStates = mExpandedBoardStates;

		while (lParentBoardState != null && !lExpandedBoardStates.isEmpty()) {
			String lParentBoardStateMoveString = "";
			if (sLogger.isDebugEnabled()) {
				lParentBoardStateMoveString = lParentBoardState.constructMoveStrings()[0];
			}
			
			sLogger.debug("Reserving the Parent BoardState for Current BoardState Rescore: {{}}", lParentBoardStateMoveString);
			boolean lRescoreStateReserved = reserveBoardState(lParentBoardState);
			if (!lRescoreStateReserved) {
				sLogger.error("COULD NOT GET THE PARENT BOARDSTATE RESERVED!");
				mTaskSuccessful = false;
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}
				return;
			}
			sLogger.debug("Parent BoardState is reserved: {{}}", lParentBoardStateMoveString);
			
			sLogger.debug("Rescoring BoardState: {{}}", lCurrentBoardStateMoveString);
			lRescoreValue = rescoreNode(lCurrentBoardState, lExpandedBoardStates);
			//TODO if the Move score has not changed.... do we really need to rescore beyond it?
			lCurrentBoardState.setMoveScore(lRescoreValue);
			//TODO 	lCurrentBoardState.writeMoveScoreToKnowledge(mLogContext);
			sLogger.debug("BoardState is rescored: {{}}", lCurrentBoardStateMoveString);

			sLogger.debug("Expanding SubBoard States: {{}}", lParentBoardStateMoveString);
			lExpandedBoardStates = expandSubBoardStates(lParentBoardState);
			sLogger.debug("Current BoardState SubBoard States Expanded: {{}}", lParentBoardStateMoveString);
		
			
			if (lParentBoardState.getPreviousBoardState() != null) {
				sLogger.debug("Rescoring Root BoardState: {{}}", lParentBoardStateMoveString);
				lRescoreValue = rescoreNode(lParentBoardState, lExpandedBoardStates);
				lParentBoardState.setMoveScore(lRescoreValue);
				//TODO 	lParentBoardState.writeMoveScoreToKnowledge(mLogContext);
				sLogger.debug("Root BoardState is rescored: {{}}", lParentBoardStateMoveString);
			}				

			sLogger.debug("Releasing the Parent BoardState: {{}}", lParentBoardStateMoveString);
			releaseBoardState(lParentBoardState);
			sLogger.debug("Parent BoardState is Released: {{}}", lParentBoardStateMoveString);

			lCurrentBoardState = lParentBoardState;
			lParentBoardState = lCurrentBoardState.getPreviousBoardState();
		}
		
		mTaskSuccessful = true;

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
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

		if (lMove.getPlayer() == PlayerSet.getRealPlayer(2)) { // current move is opponent move, reorder sub
			// moves by highest score
			Collections.sort(pSubBoardStates, new Comparator<BoardState>() {
				public int compare(BoardState p1, BoardState p2) {
					byte lP1MoveScore = p1.getMoveScore();
					byte lP2MoveScore = p2.getMoveScore();
					return (lP2MoveScore > lP1MoveScore) ? 1 : -1;
				}
			});
		} else { // current move is a self move, reorder sub moves by lowest
					// score
			Collections.sort(pSubBoardStates, new Comparator<BoardState>() {
				public int compare(BoardState p1, BoardState p2) {
					byte lP1MoveScore = p1.getMoveScore();
					byte lP2MoveScore = p2.getMoveScore();
					return (lP2MoveScore < lP1MoveScore) ? 1 : -1;
				}
			});
		}

		byte lScore = pSubBoardStates.get(0).getMoveScore();

		if (sLogger.isDebugEnabled()) {
			sLogger.debug("CurrentNode: Move: {{}} Score: {{}}", lMove.getColumn(), pSubBoardStates.get(0).getMoveScore());
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lScore;
	}

	private ArrayList<BoardState> expandSubBoardStates(BoardState pNodeToExpand) {
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
		if (lMove.getPlayer() == PlayerSet.getAllPlayer(2)) {
			for (Column lColumn : ColumnSet.getColumns()) {
				try {
					BoardState lNewBoardState = new BoardState(pNodeToExpand, MoveSet.getMove(PlayerSet.getAllPlayer(1), lColumn), true);
					lSubBoardState.add(lNewBoardState);
				} catch (InvalidMoveException eE) {
					if (sLogger.isDebugEnabled()) {
						sLogger.debug("Invalid Evaluated Move: {{}}", lColumn.getColumn());
					}
				}
			}
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return lSubBoardState;
		} else if (lMove.getPlayer() == PlayerSet.getAllPlayer(1)) {
			for (Column lColumn : ColumnSet.getColumns()) {
				try {
					BoardState lNewBoardState = new BoardState(pNodeToExpand, MoveSet.getMove(PlayerSet.getAllPlayer(2), lColumn), true);
					lSubBoardState.add(lNewBoardState);
				} catch (InvalidMoveException eE) {
					if (sLogger.isDebugEnabled()) {
						sLogger.debug("Invalid Evaluated Move: {{}}", lColumn.getColumn());
					}
				}
			}
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
		return mBoardStateToExpand;
	}

	public List<BoardState> getExpandedBoardStates() {
		return mExpandedBoardStates;
	}

	public void setBoardStateLock(BoardState pBoardStateLock) {
		mBoardStateLock = pBoardStateLock;
	}

}
