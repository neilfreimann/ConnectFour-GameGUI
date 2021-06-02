package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
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
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score.BoardStateKnowledgeBaseFileIndex;

public class BoardStateMinimumMemory extends BoardStateBase implements IBoardState {
	public static final String CLASS_NAME = BoardStateMinimumMemory.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static int sBoardStateBitSetSize = 0;
	protected static int sOccupancyPositionsBitSetIndex = 0;
	protected static int sMoveBitSetIndex = 0;
	protected static int sGameStateBitSetIndex = 0;
	protected static int sWinningCombinationBitSetIndex = 0;
	
	protected static int sPlayerSetMaxBitSize = PlayerSet.getInstance().getGameAttributeMaxBitSize();
	protected static int sMoveSetMaxBitSize = MoveSet.getInstance().getGameAttributeMaxBitSize();
	protected static int sGameStateSetMaxBitSize = GameStateSet.getInstance().getGameAttributeMaxBitSize();

	static {
		sOccupancyPositionsBitSetIndex = 0;
		sLogger.debug("OccupancyPosition BitSet Index {}", sOccupancyPositionsBitSetIndex);

		sMoveBitSetIndex = sOccupancyPositionsBitSetIndex + PlayerSet.getInstance().getGameAttributeMaxBitSize() * sConnectFourBoardConfig.getNumberOfColumns()
				* sConnectFourBoardConfig.getNumberOfRows();
		sLogger.debug("Move BitSet Index {}", sMoveBitSetIndex);

		sGameStateBitSetIndex = sMoveBitSetIndex + MoveSet.getInstance().getGameAttributeMaxBitSize();
		sLogger.debug("GameState BitSet Index {}", sGameStateBitSetIndex);

		sWinningCombinationBitSetIndex = sGameStateBitSetIndex + GameStateSet.getInstance().getGameAttributeMaxBitSize();
		sLogger.debug("Winning Combination BitSet Index {}", sWinningCombinationBitSetIndex);

		sBoardStateBitSetSize = sWinningCombinationBitSetIndex + WinningCombinationSet.getWinningCombinations().size();
		sLogger.debug("Boardstate BitSet Size {}", sBoardStateBitSetSize);
		
		sColumnCount = sConnectFourBoardConfig.getNumberOfColumns();
		sRowCount = sConnectFourBoardConfig.getNumberOfRows();
	}

	// 16 bytes basic
	private BitSet mBoardStateBitSet = null; // 8 bytes + 229 bits (32 bytes)
	// total 64 bytes

	private BoardStateKnowledgeBaseFileIndex mBoardStateKnowledgeBaseFileIndex = null;
	
	public List<OccupancyPosition> decodeOccupancyPositions() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		ArrayList<OccupancyPosition> lOccupancyPositions = new ArrayList<>();

		for (int j = 0; j < sRowCount; j++) {
			Row lRow = RowSet.getRow(j);
			for (int i = 0; i < sColumnCount; i++) {
				Column lColumn = ColumnSet.getColumn(i);
				Position lPosition = PositionSet.getPosition(lColumn, lRow);

				BitSet lPlayerBitSet = new BitSet();

				int lPositionReferenceId = lPosition.getReferenceId();
				
				for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
					lPlayerBitSet.set(l, mBoardStateBitSet.get(lPositionReferenceId * sPlayerSetMaxBitSize + l));
				}

				Player lPlayer = PlayerSet.getAllPlayer(lPlayerBitSet);
				OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);

				lOccupancyPositions.add(lOccupancyPosition);
			}
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lOccupancyPositions;
	}

	public class OccupancyPositionsWithMove {
		private OccupancyPosition mMoveOccupancyPosition;
		private Map <BitSet, OccupancyPosition> mOccupancyPositions;
		
		public OccupancyPosition getMoveOccupancyPosition() {
			return mMoveOccupancyPosition;
		}
		public void setMoveOccupancyPosition(OccupancyPosition pMoveOccupancyPosition) {
			mMoveOccupancyPosition = pMoveOccupancyPosition;
		}
		public Map <BitSet, OccupancyPosition> getOccupancyPositions() {
			return mOccupancyPositions;
		}
		public void setOccupancyPositions(Map <BitSet, OccupancyPosition> pOccupancyPositions) {
			mOccupancyPositions = pOccupancyPositions;
		}
	}
	

	private OccupancyPositionsWithMove decodeOccupancyPositionsWithMove(Move pMove) throws InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		OccupancyPositionsWithMove lOccupancyPositionsWithMove = new OccupancyPositionsWithMove();
		
		Player lMovePlayer = pMove.getPlayer();
		Column lMoveColumn = pMove.getColumn();
		OccupancyPosition lMoveOccupancyPosition = null;

		HashMap<BitSet, OccupancyPosition> lOccupancyPositions = new HashMap<>(64);

		int lMoveColumnValue = lMoveColumn.getColumn();
		for (int i = lMoveColumnValue; i < lMoveColumnValue + sColumnCount; i++) {
			Column lColumn = ColumnSet.getColumn(i % sColumnCount);
			for (int j = 0; j < sRowCount; j++) {
			Row lRow = RowSet.getRow(j);
				Position lPosition = PositionSet.getPosition(lColumn, lRow);

				BitSet lPlayerBitSet = new BitSet();
				
				int lPositionReferenceBitPosition = lPosition.getReferenceId() * sPlayerSetMaxBitSize;

				for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
					lPlayerBitSet.set(l, mBoardStateBitSet.get(lPositionReferenceBitPosition + l));
				}

				Player lPlayer = PlayerSet.getAllPlayer(lPlayerBitSet);
				
				if (lMoveOccupancyPosition == null && lMoveColumn == lColumn && lPlayer == PlayerSet.NULL_PLAYER) {
					lMoveOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lMovePlayer, lPosition);
					lOccupancyPositions.put(lMoveOccupancyPosition.getAttributeBitSet().getBitSet(), lMoveOccupancyPosition);
				} else {
					OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);
					lOccupancyPositions.put(lOccupancyPosition.getAttributeBitSet().getBitSet(), lOccupancyPosition);
				}
				
			}
		}
		
		if (lMoveOccupancyPosition == null) {
			sLogger.debug("Invalid Move!  There are no available columns in Column {}", lMoveColumn);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}
		
		lOccupancyPositionsWithMove.setMoveOccupancyPosition(lMoveOccupancyPosition);
		lOccupancyPositionsWithMove.setOccupancyPositions(lOccupancyPositions);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lOccupancyPositionsWithMove;
	}
	
	public void encodeOccupancyPositions(Collection<OccupancyPosition> pOccupancyPositions) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		for (OccupancyPosition lOccupancyPosition : pOccupancyPositions) {
			Position lPosition = lOccupancyPosition.getPosition();
			Player lPlayer = lOccupancyPosition.getPlayer();
			BitSet lPlayerBitSet = lPlayer.getAttributeBitSet().getBitSet();

			int lPositionReferenceId = lPosition.getReferenceId();

			for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
				mBoardStateBitSet.set(lPositionReferenceId * sPlayerSetMaxBitSize + l, lPlayerBitSet.get(l));
			}

		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public Move decodeMove() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		Move lMove = null;

		BitSet lMoveBitSet = new BitSet();
		for (int l = 0; l < sMoveSetMaxBitSize; l++) {
			lMoveBitSet.set(l, mBoardStateBitSet.get(sMoveBitSetIndex + l));
		}

		lMove = MoveSet.getMove(lMoveBitSet);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lMove;
	}

	public void encodeMove(Move pMove) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		BitSet lMoveBitSet = pMove.getAttributeBitSet().getBitSet();
		for (int l = 0; l < sMoveSetMaxBitSize; l++) {
			mBoardStateBitSet.set(sMoveBitSetIndex + l, lMoveBitSet.get(l));
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	}

	public GameState decodeGameState() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		GameState lGameState = null;

		BitSet lGameStateBitSet = new BitSet();
		for (int l = 0; l < sGameStateSetMaxBitSize; l++) {
			lGameStateBitSet.set(l, mBoardStateBitSet.get(sGameStateBitSetIndex + l));
		}

		lGameState = GameStateSet.getGameState(lGameStateBitSet);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		return lGameState;
	}

	public void encodeGameState(GameState pGameState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		BitSet lGameStateBitSet = pGameState.getAttributeBitSet().getBitSet();
		for (int l = 0; l < sGameStateSetMaxBitSize; l++) {
			mBoardStateBitSet.set(sGameStateBitSetIndex + l, lGameStateBitSet.get(l));
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	}

	public Map <BitSet, WinningCombination> decodeWinningCombinationsAndGraft(Player pPlayer, BoardStateMinimumMemory pNewBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		HashMap <BitSet,WinningCombination> lWinningCombinations = new HashMap<>(256);

		List<WinningCombination> lAllWinningCombinations = WinningCombinationSet.getPlayerWinningCombinations(pPlayer);

		for (WinningCombination lWinningCombination : lAllWinningCombinations) {
			int lWinningCombinationReferenceId = lWinningCombination.getReferenceId();
			
			boolean lWinningCombinationRuledOut = mBoardStateBitSet.get(sWinningCombinationBitSetIndex + lWinningCombinationReferenceId);
			if (!lWinningCombinationRuledOut) {
				lWinningCombinations.put(lWinningCombination.getAttributeBitSet().getBitSet(), lWinningCombination);
			} else {
				pNewBoardState.mBoardStateBitSet.set (sWinningCombinationBitSetIndex + lWinningCombinationReferenceId);
			}
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return lWinningCombinations;
	}

	public void ruleOutWinningCombination(WinningCombination pWinningCombination) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Setting Win Combo BoardStateBitSet index {{}}", sWinningCombinationBitSetIndex + pWinningCombination.getReferenceId());
		}
		
		mBoardStateBitSet.set(sWinningCombinationBitSetIndex + pWinningCombination.getReferenceId(), true);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	public BoardStateMinimumMemory () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mBoardStateBitSet = new BitSet();
		mPreviousBoardState = null;
		

		sLogger.debug("Creating Empty Board Occupancy Positions with No Moves");
		HashMap<BitSet, OccupancyPosition> lOccupancyPositions = new HashMap<>(64);

		for (int j = 0; j < sRowCount; j++) {
			Row lRow = RowSet.getRow(j);
			for (int i = 0; i < sColumnCount; i++) {
				Column lColumn = ColumnSet.getColumn(i);
				Position lPosition = PositionSet.getPosition(lColumn, lRow);
				OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(PlayerSet.NULL_PLAYER, lPosition);
				lOccupancyPositions.put(lOccupancyPosition.getAttributeBitSet().getBitSet(), lOccupancyPosition);
			}
		}

		encodeOccupancyPositions(lOccupancyPositions.values());
		encodeMove(MoveSet.NULLMOVE);
		encodeGameState(GameStateSet.UNDECIDED);

		mMoveScore = 0;
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	

	public BoardStateMinimumMemory(BoardStateMinimumMemory pPreviousBoardState, Move pMove) throws InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mBoardStateBitSet = new BitSet();
		mPreviousBoardState = pPreviousBoardState;
		
		sLogger.debug("Checking the Move");
		if (pMove == null || pMove.getPlayer() == null || pMove.getColumn() == null) {
			sLogger.error("Invalid Move!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new InvalidMoveException();
		}
		
		if (pPreviousBoardState == null && pMove.getPlayer() != sPlayerOne) {
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
			 (lLastPlayerToMove == sPlayerTwo && pMove.getPlayer() == sPlayerTwo) ) {
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
		
		
		OccupancyPositionsWithMove lOccupancyPositionsWithMove = ((BoardStateMinimumMemory)mPreviousBoardState).decodeOccupancyPositionsWithMove(pMove);
		
		encodeOccupancyPositions(lOccupancyPositionsWithMove.getOccupancyPositions().values());
		encodeMove(pMove);
		
		GameState lGameState = evaluateMove (lOccupancyPositionsWithMove.getMoveOccupancyPosition(), lOccupancyPositionsWithMove.getOccupancyPositions());

		encodeGameState(lGameState);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	private GameState evaluateMove(OccupancyPosition pMoveOccupancyPosition, Map<BitSet, OccupancyPosition> pBoardStateOccupancyPositions) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		Map <BitSet, WinningCombination> lPlayerOneWinningCombinations = ((BoardStateMinimumMemory)mPreviousBoardState).decodeWinningCombinationsAndGraft(sPlayerOne, this);
		Map <BitSet, WinningCombination> lPlayerTwoWinningCombinations = ((BoardStateMinimumMemory)mPreviousBoardState).decodeWinningCombinationsAndGraft(sPlayerTwo, this);

		
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
				if (lPlayerMoving == PlayerSet.getAllPlayer(1)) {
					lPlayerTwoWinningCombinations.remove(lWinningCombinationToRuleOut.getAttributeBitSet().getBitSet());
					ruleOutWinningCombination(lWinningCombinationToRuleOut);
				} else {
					lPlayerOneWinningCombinations.remove(lWinningCombinationToRuleOut.getAttributeBitSet().getBitSet());
					ruleOutWinningCombination(lWinningCombinationToRuleOut);
				}
			}
		}
		
		lPlayerOneWinningMoves = lPlayerOneWinningCombinations.size();
		lPlayerTwoWinningMoves = lPlayerTwoWinningCombinations.size();
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

	public String getBitSetString() {
		StringBuilder lBuffer = new StringBuilder(mBoardStateBitSet.length());
		for (int i = 0; i < sBoardStateBitSetSize; i++) {
			lBuffer.append(mBoardStateBitSet.get(i) ? "1" : "0");
		}
		return lBuffer.toString();
	}


	public byte getMoveScore() {
		return mMoveScore;
	}

	public void setMoveScore(byte pMoveScore) {
		mMoveScore = pMoveScore;
	}

	
	public String toString() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
		return "BoardState: " + constructBoardStateStrings(false)[0] + " (" + mMoveScore + ")";
	}

	
	public String [] constructBoardStateStrings (boolean pRetain) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		StringBuilder lBoardStateStringBuffer = new StringBuilder ();
		StringBuilder lReverseBoardStateStringBuffer = new StringBuilder ();
		StringBuilder lReverseRowStringBuffer = new StringBuilder ();
		
		List <OccupancyPosition> lOccupancyPositions = decodeOccupancyPositions();
		
		for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
			Player lPlayer = lOccupancyPosition.getPlayer();
			if (lPlayer == PlayerSet.getAllPlayer(1)) {
				lBoardStateStringBuffer.append("X");
				lReverseRowStringBuffer.insert(0, "X");
			} else if (lPlayer == PlayerSet.getAllPlayer(2)) {
				lBoardStateStringBuffer.append("O");
				lReverseRowStringBuffer.insert(0, "O");
			} else {
				lBoardStateStringBuffer.append("E");
				lReverseRowStringBuffer.insert(0, "E");
			}
			if (lReverseRowStringBuffer.length() >= sColumnCount) {
				lReverseBoardStateStringBuffer.append (lReverseRowStringBuffer.toString());
				lReverseRowStringBuffer = new StringBuilder ();
			}
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
		String [] lBoardStateStrings =  new String [] {lBoardStateStringBuffer.toString(), lReverseBoardStateStringBuffer.toString()};
		
		return lBoardStateStrings;
	}
	
	public String [] constructMoveStrings (boolean pRetain) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		StringBuilder lBuffer = new StringBuilder();
		StringBuilder lReverseBuffer = new StringBuilder ();
		BoardStateMinimumMemory lCurrentBoardState = this;
		BoardStateMinimumMemory lPreviousBoardState = (BoardStateMinimumMemory)this.getPreviousBoardState();
		
		Move lCurrentMove = lCurrentBoardState.decodeMove();
		if (lCurrentMove != MoveSet.NULLMOVE) {
			int lColumn = lCurrentBoardState.decodeMove().getColumn().getColumn() + 1;
			lBuffer.insert(0, lColumn);
			int lReverseColumn = sColumnCount - lColumn + 1;
			lReverseBuffer.insert (0, lReverseColumn);
		}

		if (lPreviousBoardState != null) {
			lBuffer.insert(0, ((BoardStateMinimumMemory)mPreviousBoardState).constructMoveStrings(pRetain)[0]);
			lReverseBuffer.insert(0, ((BoardStateMinimumMemory)mPreviousBoardState).constructMoveStrings(pRetain)[1]);
		}
		
		String [] lMoveStrings = new String [] {lBuffer.toString(), lReverseBuffer.toString()};
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return lMoveStrings;
	}

	public BoardStateKnowledgeBaseFileIndex getBoardStateKnowledgeBaseFileIndex() {
		return mBoardStateKnowledgeBaseFileIndex;
	}

	public void setBoardStateKnowledgeBaseFileIndex(BoardStateKnowledgeBaseFileIndex pBoardStateKnowledgeBaseFileIndex) {
		mBoardStateKnowledgeBaseFileIndex = pBoardStateKnowledgeBaseFileIndex;
	}

	@Override
	public List<OccupancyPosition> getCurrentOrderedOccupancyPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<BitSet, OccupancyPosition> getCurrentOccupancyPositionsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BoardStateBase instantiateBoardState(Move pMove) throws InvalidMoveException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Position> getAvailableMovePositions() {
		// TODO Auto-generated method stub
		return null;
	}

	public GameState getCurrentGameState() {
		return decodeGameState();
	}

	public Move getCurrentMove() {
		return decodeMove();
	}

	@Override
	public String[] getCurrentBoardStateStrings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getCurrentMoveStrings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<BitSet, WinningCombination> getPlayerOneRemainingWinningCombinations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<BitSet, WinningCombination> getPlayerTwoRemainingWinningCombinations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WinningCombination> getOrderedPlayerOneRemainingWinningCombinations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WinningCombination> getOrderedPlayerTwoRemainingWinningCombinations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OccupancyPosition getMoveOccupancyPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
