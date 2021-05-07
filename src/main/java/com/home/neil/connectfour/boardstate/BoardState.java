package com.home.neil.connectfour.boardstate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.ConnectFourBoardConfig;

public class BoardState {
	public static final String CLASS_NAME = BoardState.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	protected static int sBoardStateBitSetSize = 0;
	protected static int sOccupancyPositionsBitSetIndex = 0;
	protected static int sMoveBitSetIndex = 0;
	protected static int sGameStateBitSetIndex = 0;
	protected static int sWinningCombinationBitSetIndex = 0;
	
	protected static int sRowCount = 0;
	protected static int sColumnCount = 0;
	
	protected static int sPlayerSetMaxBitSize = PlayerSet.getInstance().getGameAttributeMaxBitSize();
	protected static int sMoveSetMaxBitSize = MoveSet.getInstance().getGameAttributeMaxBitSize();
	protected static int sGameStateSetMaxBitSize = GameStateSet.getInstance().getGameAttributeMaxBitSize();
	
	protected static Player sPlayerOne = PlayerSet.getAllPlayer(1);
	protected static Player sPlayerTwo = PlayerSet.getAllPlayer(2);

	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}

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

	public static void init() {

	}

	public static final int SCORE_PLAYERONE_WIN = 100;
	public static final int SCORE_PLAYERTWO_WIN = -100;
	public static final int SCORE_DRAW = 0;
	
	
	// 16 bytes basic
	private BitSet mBoardStateBitSet = null; // 8 bytes + 229 bits (32 bytes)
	private BoardState mPreviousBoardState = null; // 8 bytes
	private byte mMoveScore = 0; // 1 byte
	// total 64 bytes

	private String [] mBoardStateStrings = null;
	private String [] mMoveStrings = null;
	
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

				for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
					lPlayerBitSet.set(l, mBoardStateBitSet.get(lPosition.getReferenceId() * sPlayerSetMaxBitSize + l));
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
	

	public OccupancyPositionsWithMove decodeOccupancyPositionsWithMove(Move pMove) throws InvalidMoveException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		OccupancyPositionsWithMove lOccupancyPositionsWithMove = new OccupancyPositionsWithMove();
		
		Player lMovePlayer = pMove.getPlayer();
		Column lMoveColumn = pMove.getColumn();
		OccupancyPosition lMoveOccupancyPosition = null;

		HashMap<BitSet, OccupancyPosition> lOccupancyPositions = new HashMap<>(64);

		for (int j = 0; j < sRowCount; j++) {
			Row lRow = RowSet.getRow(j);
			for (int i = 0; i < sColumnCount; i++) {
				Column lColumn = ColumnSet.getColumn(i);
				Position lPosition = PositionSet.getPosition(lColumn, lRow);

				BitSet lPlayerBitSet = new BitSet();

				for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
					lPlayerBitSet.set(l, mBoardStateBitSet.get(lPosition.getReferenceId() * sPlayerSetMaxBitSize + l));
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
	
	
	
	
	public void encodeOccupancyPositions(List<OccupancyPosition> pOccupancyPositions) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		for (OccupancyPosition lOccupancyPosition : pOccupancyPositions) {
			encodeOccupancyPosition(lOccupancyPosition);
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public void encodeOccupancyPositions(Collection<OccupancyPosition> pOccupancyPositions) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		for (OccupancyPosition lOccupancyPosition : pOccupancyPositions) {
			encodeOccupancyPosition(lOccupancyPosition);
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public void encodeOccupancyPosition(OccupancyPosition pOccupancyPosition) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		Position lPosition = pOccupancyPosition.getPosition();
		Player lPlayer = pOccupancyPosition.getPlayer();
		BitSet lPlayerBitSet = lPlayer.getAttributeBitSet().getBitSet();

		for (int l = 0; l < sPlayerSetMaxBitSize; l++) {
			mBoardStateBitSet.set(lPosition.getReferenceId() * sPlayerSetMaxBitSize + l, lPlayerBitSet.get(l));
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

	public Map <BitSet, WinningCombination> decodeWinningCombinationsAndGraft(Player pPlayer, BoardState pNewBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		HashMap <BitSet,WinningCombination> lWinningCombinations = new HashMap<>(256);

		List<WinningCombination> lAllWinningCombinations = WinningCombinationSet.getPlayerWinningCombinations(pPlayer);

		for (WinningCombination lWinningCombination : lAllWinningCombinations) {
			boolean lWinningCombinationRuledOut = mBoardStateBitSet.get(sWinningCombinationBitSetIndex + lWinningCombination.getReferenceId());
			if (!lWinningCombinationRuledOut) {
				lWinningCombinations.put(lWinningCombination.getAttributeBitSet().getBitSet(), lWinningCombination);
			} else {
				pNewBoardState.mBoardStateBitSet.set (sWinningCombinationBitSetIndex + lWinningCombination.getReferenceId());
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
	
	public BoardState () {
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
	
	

	public BoardState(BoardState pPreviousBoardState, Move pMove) throws InvalidMoveException {
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
		
		Move lLastMove = mPreviousBoardState.decodeMove();
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

		GameState lLastGameState = mPreviousBoardState.decodeGameState();
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
		
		
		OccupancyPositionsWithMove lOccupancyPositionsWithMove = mPreviousBoardState.decodeOccupancyPositionsWithMove(pMove);
		
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

		Map <BitSet, WinningCombination> lPlayerOneWinningCombinations = mPreviousBoardState.decodeWinningCombinationsAndGraft(sPlayerOne, this);
		Map <BitSet, WinningCombination> lPlayerTwoWinningCombinations = mPreviousBoardState.decodeWinningCombinationsAndGraft(sPlayerTwo, this);

		
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

	
	public BoardState getPreviousBoardState() {
		return mPreviousBoardState;
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
		
		if (pRetain && mBoardStateStrings != null) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return mBoardStateStrings;
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
		if (pRetain) {
			mBoardStateStrings = lBoardStateStrings;
		}
		
		return mBoardStateStrings;
	}
	
	public String [] constructMoveStrings (boolean pRetain) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (pRetain && mMoveStrings != null) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return mMoveStrings;
		}
				
		StringBuilder lBuffer = new StringBuilder();
		StringBuilder lReverseBuffer = new StringBuilder ();
		BoardState lCurrentBoardState = this;
		do {
			Move lCurrentMove = lCurrentBoardState.decodeMove();
			if (lCurrentMove != MoveSet.NULLMOVE) {
				int lColumn = lCurrentBoardState.decodeMove().getColumn().getColumn() + 1;
				lBuffer.insert(0, lColumn);
				int lReverseColumn = sColumnCount - lColumn + 1;
				lReverseBuffer.insert (0, lReverseColumn);
			}
			lCurrentBoardState = lCurrentBoardState.getPreviousBoardState();
		} while (lCurrentBoardState != null);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		String [] lMoveStrings = new String [] {lBuffer.toString(), lReverseBuffer.toString()};
		if (pRetain) {
			mMoveStrings = lMoveStrings;
		}
		return lMoveStrings;
	}

	
	
}
