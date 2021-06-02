package com.home.neil.connectfour.boardstate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
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

public abstract class BoardStateBase implements IBoardState {
	public static final String CLASS_NAME = BoardStateBase.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	
	protected static int sRowCount = 0;
	protected static int sColumnCount = 0;
	
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

		
		sColumnCount = sConnectFourBoardConfig.getNumberOfColumns();
		sRowCount = sConnectFourBoardConfig.getNumberOfRows();
	}

	public static final int SCORE_PLAYERONE_WIN = 100;
	public static final int SCORE_PLAYERTWO_WIN = -100;
	public static final int SCORE_DRAW = 0;
	
	
	// 16 bytes basic
	protected BoardStateBase mPreviousBoardState = null; // 8 bytes
	protected byte mMoveScore = 0; // 1 byte
	
	
	public BoardStateBase () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mPreviousBoardState = null;
		mMoveScore = 0;
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public BoardStateBase (BoardStateBase pPreviousBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mPreviousBoardState = pPreviousBoardState;
		mMoveScore = 0;
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	
	public String toString() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
		return "BoardState: " + getCurrentBoardStateStrings()[0] + " (" + mMoveScore + ")";
	}

	
	protected String [] constructBoardStateStrings () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		StringBuilder lBoardStateStringBuffer = new StringBuilder ();
		StringBuilder lReverseBoardStateStringBuffer = new StringBuilder ();
		StringBuilder lReverseRowStringBuffer = new StringBuilder ();
		
		List <OccupancyPosition> lOccupancyPositions = getCurrentOrderedOccupancyPositions();
		
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
	
	protected String [] constructMoveStrings () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		StringBuilder lBuffer = new StringBuilder();
		StringBuilder lReverseBuffer = new StringBuilder ();
		BoardStateBase lPreviousBoardState = getPreviousBoardState();
		
		Move lCurrentMove = getCurrentMove();
		if (lCurrentMove != MoveSet.NULLMOVE) {
			int lColumn = getCurrentMove().getColumn().getColumn() + 1;
			lBuffer.insert(0, lColumn);
			int lReverseColumn = sColumnCount - lColumn + 1;
			lReverseBuffer.insert (0, lReverseColumn);
		}

		if (lPreviousBoardState != null) {
			lBuffer.insert(0, lPreviousBoardState.getCurrentMoveStrings()[0]);
			lReverseBuffer.insert(0, lPreviousBoardState.getCurrentMoveStrings()[1]);
		}
		
		String [] lMoveStrings = new String [] {lBuffer.toString(), lReverseBuffer.toString()};

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return lMoveStrings;
	}


	public byte getMoveScore() {
		return mMoveScore;
	}

	public void setMoveScore(byte pMoveScore) {
		mMoveScore = pMoveScore;
	}

	public BoardStateBase getPreviousBoardState() {
		return mPreviousBoardState;
	}
}
