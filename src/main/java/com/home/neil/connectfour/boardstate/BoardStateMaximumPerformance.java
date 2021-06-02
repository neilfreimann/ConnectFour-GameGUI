package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.board.Column;
import com.home.neil.connectfour.board.ColumnSet;
import com.home.neil.connectfour.board.GameState;
import com.home.neil.connectfour.board.GameStateSet;
import com.home.neil.connectfour.board.InvalidMoveException;
import com.home.neil.connectfour.board.Move;
import com.home.neil.connectfour.board.MoveSet;
import com.home.neil.connectfour.board.OccupancyPosition;
import com.home.neil.connectfour.board.OccupancyPositionSet;
import com.home.neil.connectfour.board.Player;
import com.home.neil.connectfour.board.PlayerSet;
import com.home.neil.connectfour.board.Position;
import com.home.neil.connectfour.board.PositionSet;
import com.home.neil.connectfour.board.Row;
import com.home.neil.connectfour.board.RowSet;
import com.home.neil.connectfour.board.WinningCombination;
import com.home.neil.connectfour.board.WinningCombinationSet;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score.BoardStateKnowledgeBaseFileIndex;
import com.home.neil.connectfour.boardstate.logger.BoardStateLogger;

public class BoardStateMaximumPerformance extends BoardStateBase implements IBoardState {
	public static final String CLASS_NAME = BoardStateMaximumPerformance.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private List <Position> mAvailableMovePositions = null;
	private Map <BitSet, OccupancyPosition> mOccupancyPositions = null;
	private List <OccupancyPosition> mOrderedOccupancyPositions = null;
	private Move mMove = null;
	private OccupancyPosition mMoveOccupancyPosition = null;
	private GameState mGameState = null;
	
	Map <BitSet, WinningCombination> mPlayerOneWinningCombinations = null;
	Map <BitSet, WinningCombination> mPlayerTwoWinningCombinations = null;
	
	private String [] mBoardStateStrings = null;
	private String [] mMoveStrings = null;
	private BoardStateKnowledgeBaseFileIndex mBoardStateKnowledgeBaseFileIndex = null;
		
	protected void initializeRootAvailableMovePositions () {
		Position [] lAvailableMovePositions = new Position [sColumnCount];
		mAvailableMovePositions = new ArrayList <> ();
		Row lRow = RowSet.getRow(0);
		for (int i = 0; i < sColumnCount; i++) {
			Column lColumn = ColumnSet.getColumn(i);
			Position lPosition = PositionSet.getPosition(lColumn, lRow);

			lAvailableMovePositions[i] = lPosition;
		}
		mAvailableMovePositions = Arrays.asList(lAvailableMovePositions);
	}
	
	
	protected void initializeRootWinningCombinations () {
		mPlayerOneWinningCombinations = WinningCombinationSet.getPlayerWinningCombinationsMap(sPlayerOne);
		mPlayerTwoWinningCombinations = WinningCombinationSet.getPlayerWinningCombinationsMap(sPlayerTwo);
	}

	protected void initRootOccupancyPositions () {
		sLogger.debug("Creating Empty Board Occupancy Positions with No Moves");
		mOccupancyPositions = new HashMap<>(64);

		for (int j = 0; j < sRowCount; j++) {
			Row lRow = RowSet.getRow(j);
			for (int i = 0; i < sColumnCount; i++) {
				Column lColumn = ColumnSet.getColumn(i);
				Position lPosition = PositionSet.getPosition(lColumn, lRow);
				OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(PlayerSet.NULL_PLAYER, lPosition);
				mOccupancyPositions.put(lOccupancyPosition.getAttributeBitSet().getBitSet(), lOccupancyPosition);
			}
		}
	}
	
	
	public BoardStateMaximumPerformance () throws FileIndexException{
		super();
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		initializeRootAvailableMovePositions();
		initializeRootWinningCombinations();
		initRootOccupancyPositions ();
		
		mMove = MoveSet.NULLMOVE;
		mGameState = GameStateSet.UNDECIDED;
		mMoveScore = 0;

		mBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(this);
		
		BoardStateLogger.logBoardState(this);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	
	protected void checkMoveValidity (Move pMove) throws InvalidMoveException {
		sLogger.debug("Checking the Move");
		if (pMove == null || pMove.getPlayer() == null || pMove.getColumn() == null || pMove.getPlayer() == PlayerSet.NULL_PLAYER) {
			sLogger.error("Invalid Move!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}
		
		if (mPreviousBoardState == null) {
			sLogger.error("Invalid Move!  Previous Board State is null");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}

		
		if (mPreviousBoardState.getPreviousBoardState() == null && pMove.getPlayer() != sPlayerOne) {
			sLogger.error("Invalid Move!  First Move must be Player 1");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		} 

		Move lLastMove = mPreviousBoardState.getCurrentMove();
		if (lLastMove == null) {
			sLogger.error("Invalid Move!  Last Move is Null");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}

		Player lLastPlayerToMove = lLastMove.getPlayer();
		if ( (lLastPlayerToMove == sPlayerOne && pMove.getPlayer() == sPlayerOne) ||
			 (lLastPlayerToMove == sPlayerTwo && pMove.getPlayer() == sPlayerTwo) ||
			 (pMove.getPlayer() == PlayerSet.NULL_PLAYER)) {
			sLogger.error("Invalid Move!  Player {} is attempting to move twice", lLastPlayerToMove.getAttributeName());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}

		GameState lLastGameState = mPreviousBoardState.getCurrentGameState();
		if (lLastGameState == null) {
			sLogger.error("Invalid Move!  Last Game State is Null");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}
		
		
		if (lLastGameState != GameStateSet.UNDECIDED) {
			sLogger.error("Invalid Move!  Game is already Over {{}}", lLastGameState.getAttributeName());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}

		if (mAvailableMovePositions.get(pMove.getColumn().getColumn()) == null) {
			sLogger.debug("Invalid Move!  Column {{}} is already full", pMove.getColumn().getColumn());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}
	}
	
	
	public void makeMove(Move pMove) throws InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mMove = pMove;
		
		Player lMovePlayer = pMove.getPlayer();
		Column lMoveColumn = pMove.getColumn();
		Position lMovePosition = mAvailableMovePositions.get(lMoveColumn.getColumn());
		Row lNewAvailableRow = RowSet.getRow(lMovePosition.getRow().getRow() + 1);
		
		if (lNewAvailableRow != null) {
			mAvailableMovePositions.set(lMoveColumn.getColumn(), PositionSet.getPosition(lMoveColumn, lNewAvailableRow));
		} else {
			mAvailableMovePositions.set(lMoveColumn.getColumn(), null);
		}
		
		mOccupancyPositions = new HashMap <> (mPreviousBoardState.getCurrentOccupancyPositionsMap());
		
		mMoveOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lMovePlayer, lMovePosition);
		
		mOccupancyPositions.remove(OccupancyPositionSet.getOccupancyPosition(PlayerSet.NULL_PLAYER, lMovePosition).getAttributeBitSet().getBitSet());
		mOccupancyPositions.put (mMoveOccupancyPosition.getAttributeBitSet().getBitSet(), mMoveOccupancyPosition);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	}
	

	public BoardStateMaximumPerformance(BoardStateMaximumPerformance pPreviousBoardState, Move pMove) throws InvalidMoveException, FileIndexException {
		super (pPreviousBoardState);
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mAvailableMovePositions = new ArrayList <> (pPreviousBoardState.mAvailableMovePositions);
		
		checkMoveValidity(pMove);

		makeMove (pMove);
		
		mGameState = evaluateMove (mMoveOccupancyPosition, mOccupancyPositions);

		mBoardStateKnowledgeBaseFileIndex = new BoardStateKnowledgeBaseFileIndex(this);
		
		BoardStateLogger.logBoardState(this);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	

	protected void getWinningCombinationsAndGraft() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mPlayerOneWinningCombinations = new HashMap<>(mPreviousBoardState.getPlayerOneRemainingWinningCombinations());
		mPlayerTwoWinningCombinations = new HashMap<>(mPreviousBoardState.getPlayerTwoRemainingWinningCombinations());
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	protected GameState evaluateMove(OccupancyPosition pMoveOccupancyPosition, Map<BitSet, OccupancyPosition> pBoardStateOccupancyPositions) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		getWinningCombinationsAndGraft();
		
		Player lPlayerMoving = pMoveOccupancyPosition.getPlayer();

		int lPlayerOneWinningMoves = 0;
		int lPlayerTwoWinningMoves = 0;
		byte lMoveScore = 0;

		boolean lPlayerOneWinner = false;
		boolean lPlayerOneLoser = false;
		boolean lDraw = false;

		List<WinningCombination> lMoveWinningCombinations = pMoveOccupancyPosition.getWinningCombinations();

		//Check for Win
		for (WinningCombination lMoveWinningCombination : lMoveWinningCombinations) {
			if ((lPlayerMoving == sPlayerOne && mPlayerOneWinningCombinations.get(lMoveWinningCombination.getAttributeBitSet().getBitSet()) == null) || 
				(lPlayerMoving == sPlayerTwo && mPlayerTwoWinningCombinations.get(lMoveWinningCombination.getAttributeBitSet().getBitSet()) == null)) {
				//Winning Combo was ruled out
				continue;
			} 
			
			List<OccupancyPosition> lOccupancyPositionsToCheckForWin = lMoveWinningCombination.getOccupancyPositions();

			boolean lFoundWin = true;

			for (OccupancyPosition lOccupancyPositionToCheckForWin : lOccupancyPositionsToCheckForWin) {
				OccupancyPosition lFoundForWin = pBoardStateOccupancyPositions.get(lOccupancyPositionToCheckForWin.getAttributeBitSet().getBitSet());
				if (lFoundForWin == null) {
					lFoundWin = false;
					break;
				}
			}

			if (lFoundWin && lPlayerMoving == sPlayerOne) {
				lPlayerOneWinner = true;
				break;
			} else if (lFoundWin && lPlayerMoving == sPlayerTwo) {
				lPlayerOneLoser = true;
				break;
			}
		}

		if (!lPlayerOneWinner && !lPlayerOneLoser) {
			List<WinningCombination> lWinningCombinationsToRuleOut = pMoveOccupancyPosition.getWinningCombinationsToRuleOut();
			
			for (WinningCombination lWinningCombinationToRuleOut : lWinningCombinationsToRuleOut) {
				if (lPlayerMoving == sPlayerOne) {
					mPlayerTwoWinningCombinations.remove(lWinningCombinationToRuleOut.getAttributeBitSet().getBitSet());
				} else {
					mPlayerOneWinningCombinations.remove(lWinningCombinationToRuleOut.getAttributeBitSet().getBitSet());
				}
			}
		}
		
		lPlayerOneWinningMoves = mPlayerOneWinningCombinations.size();
		lPlayerTwoWinningMoves = mPlayerTwoWinningCombinations.size();
		lMoveScore = (byte) (lPlayerOneWinningMoves - lPlayerTwoWinningMoves);
		
		if (lPlayerOneWinningMoves == 0 && lPlayerTwoWinningMoves == 0) {
			lDraw = true;
		}
		
		if (lPlayerOneWinner) {
			mMoveScore = SCORE_PLAYERONE_WIN;

			sLogger.debug("Move: {{}} MoveScore: {{}}", pMoveOccupancyPosition.getAttributeName(), mMoveScore);
			sLogger.debug("Player One wins, Player Two loses! Game Over");
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return GameStateSet.WIN;
		} else if (lPlayerOneLoser) {
			mMoveScore = SCORE_PLAYERTWO_WIN;

			sLogger.debug("Move: {{}} MoveScore: {{}}", pMoveOccupancyPosition.getAttributeName(), mMoveScore);
			sLogger.debug("Player Two wins, Player One loses! Game Over");
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return GameStateSet.LOSS;
		} else if (lDraw) {
			mMoveScore = SCORE_DRAW;

			sLogger.debug("Move: {{}} MoveScore: {{}}", pMoveOccupancyPosition.getAttributeName(), mMoveScore);
			sLogger.debug("Draw! Game Over");
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return GameStateSet.DRAW;
		} else {
			mMoveScore = lMoveScore;

			sLogger.debug("Player Two Grid Score: {{}}", lPlayerTwoWinningMoves);
			sLogger.debug("Player One Grid Score: {{}}", lPlayerOneWinningMoves);
			sLogger.debug("Move: {{}} MoveScore: {{}}", pMoveOccupancyPosition.getAttributeName(), mMoveScore);
			sLogger.debug("Game Continues!");
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return GameStateSet.UNDECIDED;
		}
	}

	public BoardStateKnowledgeBaseFileIndex getBoardStateKnowledgeBaseFileIndex() {
		return mBoardStateKnowledgeBaseFileIndex;
	}

	public void setBoardStateKnowledgeBaseFileIndex(BoardStateKnowledgeBaseFileIndex pBoardStateKnowledgeBaseFileIndex) {
		mBoardStateKnowledgeBaseFileIndex = pBoardStateKnowledgeBaseFileIndex;
	}

	public List<OccupancyPosition> getCurrentOrderedOccupancyPositions() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (mOrderedOccupancyPositions != null) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return mOrderedOccupancyPositions;
		}
				
		OccupancyPosition [] lOrderedOccupancyPositions = new OccupancyPosition [sRowCount*sColumnCount];
		
		for (OccupancyPosition lOccupancyPosition : mOccupancyPositions.values()) {
			Position lPosition = lOccupancyPosition.getPosition();
			Row lRow = lPosition.getRow();
			Column lColumn = lPosition.getColumn();
			
			lOrderedOccupancyPositions[lColumn.getColumn() + lRow.getRow() * sColumnCount] = lOccupancyPosition;
		}
		
		
		mOrderedOccupancyPositions = Arrays.asList(lOrderedOccupancyPositions);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return mOrderedOccupancyPositions;
	}

	public Map<BitSet, OccupancyPosition> getCurrentOccupancyPositionsMap() {
		return mOccupancyPositions;
	}

	public List<Position> getAvailableMovePositions() {
		return mAvailableMovePositions;
	}

	public GameState getCurrentGameState() {
		return mGameState;
	}

	public Move getCurrentMove() {
		return mMove;
	}


	public Map<BitSet, WinningCombination> getPlayerOneRemainingWinningCombinations() {
		return mPlayerOneWinningCombinations;
	}

	public Map<BitSet, WinningCombination> getPlayerTwoRemainingWinningCombinations() {
		return mPlayerTwoWinningCombinations;
	}

	public List<WinningCombination> getOrderedPlayerOneRemainingWinningCombinations() {
		int lPlayerOneWinningCombinationsSize = WinningCombinationSet.getPlayerWinningCombinations(sPlayerOne).size();
		WinningCombination [] lWinningCombinationsRemaining = new WinningCombination [lPlayerOneWinningCombinationsSize];
		
		for (WinningCombination lWinningCombinationRemaining : mPlayerOneWinningCombinations.values()) {
			lWinningCombinationsRemaining[lWinningCombinationRemaining.getReferenceId() % lPlayerOneWinningCombinationsSize] = lWinningCombinationRemaining;
		}
		
		return Arrays.asList(lWinningCombinationsRemaining);
	}

	public List<WinningCombination> getOrderedPlayerTwoRemainingWinningCombinations() {
		int lPlayerTwoWinningCombinationsSize = WinningCombinationSet.getPlayerWinningCombinations(sPlayerTwo).size();
		WinningCombination [] lWinningCombinationsRemaining = new WinningCombination [lPlayerTwoWinningCombinationsSize];
		
		for (WinningCombination lWinningCombinationRemaining : mPlayerTwoWinningCombinations.values()) {
			lWinningCombinationsRemaining[lWinningCombinationRemaining.getReferenceId() % lPlayerTwoWinningCombinationsSize] = lWinningCombinationRemaining;
		}
		
		return Arrays.asList(lWinningCombinationsRemaining);
	}

	public String[] getCurrentBoardStateStrings() {
		if (mBoardStateStrings == null) {
			mBoardStateStrings = constructBoardStateStrings();
		}
		return mBoardStateStrings;
	}

	public String[] getCurrentMoveStrings() {
		if (mMoveStrings == null) {
			mMoveStrings = constructMoveStrings();
		}
		return mMoveStrings;
	}
	
	
	public BoardStateBase instantiateBoardState (Move pMove) throws InvalidMoveException, FileIndexException {
		return new BoardStateMaximumPerformance (this, pMove);
	}


	public OccupancyPosition getMoveOccupancyPosition() {
		return mMoveOccupancyPosition;
	}

}
