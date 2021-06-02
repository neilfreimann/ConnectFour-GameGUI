package com.home.neil.connectfour.boardstate.logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.FileDetails;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.IndexDetails;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.connectfour.board.ColumnSet;
import com.home.neil.connectfour.board.OccupancyPosition;
import com.home.neil.connectfour.board.Player;
import com.home.neil.connectfour.board.Position;
import com.home.neil.connectfour.board.WinningCombination;
import com.home.neil.connectfour.boardstate.IBoardState;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score.BoardStateKnowledgeBaseFileIndex;

public class BoardStateLogger {
	public static final String CLASS_NAME = BoardStateLogger.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;

	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}
	}
	
	public static void logBoardLayout (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			String lBoardStateLog;
	
			StringBuilder lLineBuffer = new StringBuilder ();

			StringBuilder lLogBuffer = new StringBuilder ();
			
			List <OccupancyPosition> lOccupancyPositions = pBoardState.getCurrentOrderedOccupancyPositions();
	
			for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
				Player lPlayer = lOccupancyPosition.getPlayer();
				Position lPosition = lOccupancyPosition.getPosition();
				
				if (lPosition.getColumn() == ColumnSet.getColumn(0)) {
					lLineBuffer.append ("|   ");
				}
				
				switch (lPlayer.getPlayer()) {
				case 0: 
					lLineBuffer.append ("E   ");
					break;
				case 1: 
					lLineBuffer.append ("X   ");
					break;
				case 2: 
					lLineBuffer.append ("O   ");
					break;
				}
				
				if (lPosition.getColumn() == ColumnSet.getColumn(sConnectFourBoardConfig.getNumberOfColumns()-1)) {
					lLineBuffer.append ("|\n");
					lLogBuffer.insert(0,  lLineBuffer.toString());
					lLineBuffer = new StringBuilder();
				}
			}

			lLineBuffer.append ("\n");
			
			lLineBuffer.append ("----");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLineBuffer.append ("----");
			}
			lLineBuffer.append ("-\n");
			
			lLineBuffer.append ("|   ");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLineBuffer.append ("    ");
			}
			lLineBuffer.append ("|\n");

			
			lLogBuffer.insert(0,  lLineBuffer.toString());
			lLineBuffer = new StringBuilder();
			
			
			lLogBuffer.append ("----");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLogBuffer.append ("----");
			}
			lLogBuffer.append ("-\n");
	
			lLogBuffer.append ("\n");
			
			lBoardStateLog = "Current Board Layout:\n" + lLogBuffer.toString();
	
			sLogger.debug(lBoardStateLog);
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
	}
	
	public static void logAvailableMoves (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			String lBoardStateLog;
	
			StringBuilder lLineBuffer = new StringBuilder ();

			List <Position> lPositions = pBoardState.getAvailableMovePositions();
			lLineBuffer.append ("|   ");
			
			for (Position lPosition : lPositions) {
				if (lPosition == null) {
					lLineBuffer.append("null ");
					lLineBuffer.append("    ");

				} else {
					lLineBuffer.append(lPosition.getAttributeName());
					lLineBuffer.append("    ");
				}

				
			}
			lLineBuffer.append ("|\n");
			lLineBuffer.append ("\n");
			
			lBoardStateLog = "Current Available Moves:\n" + lLineBuffer.toString();
	
			sLogger.debug(lBoardStateLog);
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	
	public static void logDetails (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			sLogger.debug("MoveOccupancyPosition: {{}} Move: {{}}  GameState: {{}}  MoveScore: {{}}", 
					pBoardState.getMoveOccupancyPosition(),
					pBoardState.getCurrentMove().getAttributeName(),
					pBoardState.getCurrentGameState().getAttributeName(),
					pBoardState.getMoveScore());
		}		

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	public static void logMoveBoardStateStrings (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			String [] lMoveStrings = pBoardState.getCurrentMoveStrings();
			String [] lBoardStateStrings = pBoardState.getCurrentBoardStateStrings();
			
			sLogger.debug ("MoveString: {{}}", lMoveStrings[0]);
			sLogger.debug ("BoardState: {{}}", lBoardStateStrings[0]);

			sLogger.debug ("ReciprocalMoveString: {{}}", lMoveStrings[1]);
			sLogger.debug ("ReciprocalBoardState: {{}}", lBoardStateStrings[1]);
		
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	
	
	public static void logFilesAndIndexes (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			BoardStateKnowledgeBaseFileIndex lFileIndex = pBoardState.getBoardStateKnowledgeBaseFileIndex ();
		
			String lAddress = lFileIndex.getAddress();
			String lReciprocalAddress = lFileIndex.getReciprocalAddress();
			
			String lPoolItemId = lFileIndex.getPoolItemId();
			String lReciprocalPoolItemId = lFileIndex.getReciprocalPoolItemId();
			
			FileDetails lFileDetails = lFileIndex.getFileDetails();
			FileDetails lReciprocalFileDetails = lFileIndex.getReciprocalFileDetails();
			
			IndexDetails lIndexDetails = lFileIndex.getIndexDetails();
			IndexDetails lReciprocalIndexDetails = lFileIndex.getReciprocalIndexDetails();
			
			sLogger.debug ("FileIndex Address: {{}}", lAddress);
			sLogger.debug ("FileIndex PoolItemId: {{}}", lPoolItemId);
			sLogger.debug ("FileIndex FileDetails StatePaths: {{}}  Filename: {{}}  FileSize: {{}}", lFileDetails.getStatePaths(), lFileDetails.getFileName(), lFileDetails.getFileSize());
			sLogger.debug ("FileIndex IndexDetails Index: {{}}  Segment: {{}}  Size: {{}}", lIndexDetails.getIndex(), lIndexDetails.getIndexSegment(), lIndexDetails.getIndexSize());
			
			sLogger.debug ("FileIndex ReciprocalAddress: {{}}", lReciprocalAddress);
			sLogger.debug ("FileIndex ReciprocalPoolItemId: {{}}", lReciprocalPoolItemId);
			sLogger.debug ("FileIndex ReciprocalFileDetails StatePaths: {{}}  Filename: {{}}  FileSize: {{}}", lReciprocalFileDetails.getStatePaths(), lReciprocalFileDetails.getFileName(), lReciprocalFileDetails.getFileSize());
			sLogger.debug ("FileIndex ReciprocalIndexDetails Index: {{}}  Segment: {{}}  Size: {{}}", lReciprocalIndexDetails.getIndex(), lReciprocalIndexDetails.getIndexSegment(), lReciprocalIndexDetails.getIndexSize());
			
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	public static void logRemainingWinningCombinations (IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			List <WinningCombination> lPlayerOneRemainingWinningCombinations = pBoardState.getOrderedPlayerOneRemainingWinningCombinations();
			Map <BitSet, WinningCombination> lPlayerOneWinComboMap = pBoardState.getPlayerOneRemainingWinningCombinations();

			String lBoardStateLog;
		
			StringBuilder lLineBuffer = new StringBuilder ();

			lLineBuffer.append ("|   " + lPlayerOneWinComboMap.size() + "   ");
			
			for (WinningCombination lWinningCombo : lPlayerOneRemainingWinningCombinations) {
				lLineBuffer.append(lWinningCombo != null ? lWinningCombo.getAttributeName() : "null");
				lLineBuffer.append("    ");
			}
			lLineBuffer.append ("|\n");
			lLineBuffer.append ("\n");
				
			lBoardStateLog = "Current Player One Remaining Winning Combos:\n" + lLineBuffer.toString();
		
			sLogger.debug(lBoardStateLog);
		}
			
		if (sLogger.isDebugEnabled()) {
			List <WinningCombination> lPlayerTwoRemainingWinningCombinations = pBoardState.getOrderedPlayerTwoRemainingWinningCombinations();
			Map <BitSet, WinningCombination> lPlayerTwoWinComboMap = pBoardState.getPlayerTwoRemainingWinningCombinations();

			String lBoardStateLog;
		
			StringBuilder lLineBuffer = new StringBuilder ();

			lLineBuffer.append ("|   " + lPlayerTwoWinComboMap.size() + "   ");
			
			for (WinningCombination lWinningCombo : lPlayerTwoRemainingWinningCombinations) {
				lLineBuffer.append(lWinningCombo != null ? lWinningCombo.getAttributeName() : "null      ");
				lLineBuffer.append("    ");
			}
			lLineBuffer.append ("|\n");
			lLineBuffer.append ("\n");
				
			lBoardStateLog = "Current Player One Remaining Winning Combos:\n" + lLineBuffer.toString();
		
			sLogger.debug(lBoardStateLog);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	public static void logBoardState(IBoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		logBoardLayout(pBoardState);
		
		logAvailableMoves(pBoardState);
		
		logDetails(pBoardState);
		
		logMoveBoardStateStrings(pBoardState);
		
		logFilesAndIndexes(pBoardState);
		
		logRemainingWinningCombinations(pBoardState);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	private BoardStateLogger () {
		
	}

}
