package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.home.neil.connectfour.board.GameState;
import com.home.neil.connectfour.board.InvalidMoveException;
import com.home.neil.connectfour.board.Move;
import com.home.neil.connectfour.board.OccupancyPosition;
import com.home.neil.connectfour.board.Position;
import com.home.neil.connectfour.board.WinningCombination;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score.BoardStateKnowledgeBaseFileIndex;

public interface IBoardState {
	public IBoardState getPreviousBoardState();

	public byte getMoveScore();
	public void setMoveScore(byte pMoveScore);
	
	public List <OccupancyPosition> getCurrentOrderedOccupancyPositions();
	public Map <BitSet, OccupancyPosition> getCurrentOccupancyPositionsMap();
	
	public BoardStateBase instantiateBoardState (Move pMove) throws InvalidMoveException, FileIndexException;

	public List <Position> getAvailableMovePositions ();

	public GameState getCurrentGameState ();
	public Move getCurrentMove ();
	
	public OccupancyPosition getMoveOccupancyPosition ();
	
	public String toString();
	
	public String [] getCurrentBoardStateStrings ();
	public String [] getCurrentMoveStrings ();
	
	public Map <BitSet, WinningCombination> getPlayerOneRemainingWinningCombinations();
	public Map <BitSet, WinningCombination> getPlayerTwoRemainingWinningCombinations();
	
	public List <WinningCombination> getOrderedPlayerOneRemainingWinningCombinations();
	public List <WinningCombination> getOrderedPlayerTwoRemainingWinningCombinations();
	
	public BoardStateKnowledgeBaseFileIndex getBoardStateKnowledgeBaseFileIndex ();
	public void setBoardStateKnowledgeBaseFileIndex(BoardStateKnowledgeBaseFileIndex pBoardStateKnowledgeBaseFileIndex); 
}
