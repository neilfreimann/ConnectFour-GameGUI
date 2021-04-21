package com.home.neil.connectfour.boardstate.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.ColumnSet;
import com.home.neil.connectfour.boardstate.Direction;
import com.home.neil.connectfour.boardstate.DirectionSet;
import com.home.neil.connectfour.boardstate.Move;
import com.home.neil.connectfour.boardstate.MoveSet;
import com.home.neil.connectfour.boardstate.OccupancyPosition;
import com.home.neil.connectfour.boardstate.OccupancyPositionSet;
import com.home.neil.connectfour.boardstate.Player;
import com.home.neil.connectfour.boardstate.PlayerSet;
import com.home.neil.connectfour.boardstate.Position;
import com.home.neil.connectfour.boardstate.PositionSet;
import com.home.neil.connectfour.boardstate.WinningCombination;
import com.home.neil.connectfour.boardstate.WinningCombinationSet;
import com.home.neil.connectfour.boardstate.logger.BoardStateLogger;
import com.home.neil.game.GameException;
import com.home.neil.junit.sandbox.SandboxTest;

class BoardStateTest extends SandboxTest {
	public static final String CLASS_NAME = BoardStateTest.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	
	
	@BeforeAll
	protected static void setUpBeforeClass() throws Exception {
		SandboxTest.setUpBeforeClass();
	}

	@AfterAll
	protected static void tearDownAfterClass() throws Exception {
		SandboxTest.tearDownAfterClass();
	}

	@BeforeEach
	protected void setUp() throws Exception {
		super.setUp();
	}

	@AfterEach
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	void testRowColumnInit() {
		String lCurrentMethodName = new Object(){}.getClass().getEnclosingMethod().getName();

		setTestPropertiesFileLocation(CLASS_NAME, lCurrentMethodName);
		
		List <Position> lPositions = PositionSet.getPositions();
	
		assertNotNull(lPositions);
		
		assertFalse(lPositions.isEmpty());
		
		for (Position lPosition : lPositions) {
			sLogger.info("Position Found : {{}} , {{}}", lPosition.getAttributeName(), lPosition.getAttributeBitSet().getBitSetString());
			
			Position lPositionSearch = PositionSet.getPosition(((BitSet)lPosition.getAttributeBitSet().getBitSet().clone()).get(0, lPosition.getAttributeBitSet().getMaxBitSetSize()));

			sLogger.info("Position Lookup: {{}} , {{}}", lPositionSearch.getAttributeName(), lPositionSearch.getAttributeBitSet().getBitSetString());

			assertNotNull(lPositionSearch);
		}
		
		List <Player> lPlayers = PlayerSet.getRealPlayers();
		
		assertNotNull(lPlayers);
		
		assertFalse(lPlayers.isEmpty());
		
		assertNotNull(PlayerSet.NULL_PLAYER);
		
		
		List <Move> lMoves = MoveSet.getMoves();
		
		assertNotNull(lMoves);
		
		assertFalse(lMoves.isEmpty());
		
		List <OccupancyPosition> lOccupancyPositions = OccupancyPositionSet.getOccupancyPositions();
		
		assertNotNull(lOccupancyPositions);
		
		assertFalse(lOccupancyPositions.isEmpty());
		
		List <Direction> lDirections = DirectionSet.getDirections();
		
		assertNotNull(lDirections);
		
		assertFalse(lDirections.isEmpty());
		
		List <WinningCombination> lWinningCombinations = WinningCombinationSet.getWinningCombinations();
		
		assertNotNull(lWinningCombinations);
		
		assertFalse(lWinningCombinations.isEmpty());
		try {
		try {
			BoardState lCleanBoardState = new BoardState (false);
			byte lScore = lCleanBoardState.getMoveScore();
			assertEquals(0, lScore);
			
			BoardStateLogger.logBoardState(lCleanBoardState);
			
			BoardState lPreviousBoardState = lCleanBoardState;
			BoardState lNextBoardState = null;
			for (int i = 0; i < 3; i++) {
				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(3)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(3)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;
			}
			
			for (int i = 0; i < 3; i++) {
				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(4)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(5)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(6)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(4)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(5)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(6)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

			}
			
			for (int i = 0; i < 3; i++) {
				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(0)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(1)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(2)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(0)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(1),ColumnSet.getColumn(1)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

				lNextBoardState = new BoardState(lPreviousBoardState, MoveSet.getMove(PlayerSet.getRealPlayer(2),ColumnSet.getColumn(2)), false);
				lScore = lNextBoardState.getMoveScore();

				BoardStateLogger.logBoardState(lNextBoardState);
				
				lPreviousBoardState = lNextBoardState;

			}
			
			
			
			
		} catch (Exception e) {
			throw new GameException (e);
		}
		} catch (GameException e) {
			assertFalse (true);
		}
	}
	
	
	
//	@Test
//	void testBoardStateInit() {
//		String lCurrentMethodName = new Object(){}.getClass().getEnclosingMethod().getName();
//
//		setTestPropertiesFileLocation(CLASS_NAME, lCurrentMethodName);
//		
//		WinningCombination.init();
//		MovePosition.init();
//		
//		WinningCombination.setWinningCombinationMovePositions();
//		
//		assertNotNull(WinningCombination.getAllWinningCombinations());
//		
//		assertFalse(WinningCombination.getAllWinningCombinations().isEmpty());
//	}

	
	
}