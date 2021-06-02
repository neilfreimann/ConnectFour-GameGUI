package com.home.neil.connectfour.board;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;
import com.home.neil.game.attribute.GameAttributeBitSet;

public class WinningCombinationSet  extends ConnectFourBoardAttributeSet {

	public static final String CLASS_NAME = WinningCombinationSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static WinningCombinationSet sWinningCombinationSet = null;
	private static HashMap <Player, WinningCombinationSet> sPlayerWinningCombinationSets = null;
	
	static {
		sWinningCombinationSet = new WinningCombinationSet (WinningCombination.class);
		
		sPlayerWinningCombinationSets = new HashMap <> ();
		
		int lReferenceId = 0;
		
		for (Player lPlayer : PlayerSet.getRealPlayers()) {
			Player lOpposingPlayer = (lPlayer == PlayerSet.getRealPlayer(1)) ? PlayerSet.getRealPlayer(2) : PlayerSet.getRealPlayer(1);
			
			WinningCombinationSet lPlayerSet = new WinningCombinationSet (WinningCombination.class);
			sPlayerWinningCombinationSets.put(lPlayer, lPlayerSet);
			
			
			//VERTICAL
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
					Column lStartingColumn = ColumnSet.getColumn(i);
					Row lStartingRow = RowSet.getRow(j);
					Position lStartingPosition = PositionSet.getPosition(lStartingColumn, lStartingRow);
					OccupancyPosition lStartingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lStartingPosition);
					
					ArrayList <OccupancyPosition> lOccupancyPositions = new ArrayList <> ();
					lOccupancyPositions.add(lStartingOccupancyPosition);
					
					for (int l = 1; l < sConnectFourBoardConfig.getWinningCombinationLength(); l++) {
						Column lColumn = ColumnSet.getColumn(i);
						Row lRow = RowSet.getRow(j + l);
						Position lPosition = PositionSet.getPosition(lColumn, lRow);
						OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);
						lOccupancyPositions.add(lOccupancyPosition);
					}
					
					WinningCombination lWinningCombination = new WinningCombination(lStartingOccupancyPosition, DirectionSet.VERTICAL, lOccupancyPositions, lReferenceId);
					lReferenceId++;
					sWinningCombinationSet.addGameAttribute(lWinningCombination);
					lPlayerSet.addGameAttribute(lWinningCombination);
					
					sLogger.info("Winning Combination Loaded: {{}} : {{}} : {{}}", lWinningCombination.getAttributeName(), lWinningCombination.getAttributeBitSet().getBitSetString(), lWinningCombination.getReferenceId());
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						lOccupancyPosition.addWinningCombination(lWinningCombination);
						sLogger.info("            with Positions: {{}}   : {{}}", lOccupancyPosition.getAttributeName(), lOccupancyPosition.getAttributeBitSet().getBitSetString());
					}

					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						Position lPosition = lOccupancyPosition.getPosition();
						OccupancyPosition lOpposingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lOpposingPlayer, lPosition);
						lOpposingOccupancyPosition.addWinningCombinationToRuleOut(lWinningCombination);
						sLogger.info("   and rules out Positions: {{}}   : {{}}", lOpposingOccupancyPosition.getAttributeName(), lOpposingOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
				}
			}
			
			//HORIZONTAL
			for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
				for (int j = 0; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
					Column lStartingColumn = ColumnSet.getColumn(i);
					Row lStartingRow = RowSet.getRow(j);
					Position lStartingPosition = PositionSet.getPosition(lStartingColumn, lStartingRow);
					OccupancyPosition lStartingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lStartingPosition);
					
					ArrayList <OccupancyPosition> lOccupancyPositions = new ArrayList <> ();
					lOccupancyPositions.add(lStartingOccupancyPosition);
					
					for (int l = 1; l < sConnectFourBoardConfig.getWinningCombinationLength(); l++) {
						Column lColumn = ColumnSet.getColumn(i + l);
						Row lRow = RowSet.getRow(j);
						Position lPosition = PositionSet.getPosition(lColumn, lRow);
						OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);
						lOccupancyPositions.add(lOccupancyPosition);
					}
					
					WinningCombination lWinningCombination = new WinningCombination(lStartingOccupancyPosition, DirectionSet.HORIZONTAL, lOccupancyPositions, lReferenceId);
					lReferenceId++;
					sWinningCombinationSet.addGameAttribute(lWinningCombination);
					lPlayerSet.addGameAttribute(lWinningCombination);
					
					sLogger.info("Winning Combination Loaded: {{}} : {{}} : {{}}", lWinningCombination.getAttributeName(), lWinningCombination.getAttributeBitSet().getBitSetString(), lWinningCombination.getReferenceId());
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						lOccupancyPosition.addWinningCombination(lWinningCombination);
						sLogger.info("            with Positions: {{}}   : {{}}", lOccupancyPosition.getAttributeName(), lOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						Position lPosition = lOccupancyPosition.getPosition();
						OccupancyPosition lOpposingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lOpposingPlayer, lPosition);
						lOpposingOccupancyPosition.addWinningCombinationToRuleOut(lWinningCombination);
						sLogger.info("   and rules out Positions: {{}}   : {{}}", lOpposingOccupancyPosition.getAttributeName(), lOpposingOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
				}
			}
			
			
			
			for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
				for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
					Column lStartingColumn = ColumnSet.getColumn(i);
					Row lStartingRow = RowSet.getRow(j);
					Position lStartingPosition = PositionSet.getPosition(lStartingColumn, lStartingRow);
					OccupancyPosition lStartingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lStartingPosition);
					
					ArrayList <OccupancyPosition> lOccupancyPositions = new ArrayList <> ();
					lOccupancyPositions.add(lStartingOccupancyPosition);
					
					for (int l = 1; l < sConnectFourBoardConfig.getWinningCombinationLength(); l++) {
						Column lColumn = ColumnSet.getColumn(i + l);
						Row lRow = RowSet.getRow(j + l);
						Position lPosition = PositionSet.getPosition(lColumn, lRow);
						OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);
						lOccupancyPositions.add(lOccupancyPosition);
					}
					
					WinningCombination lWinningCombination = new WinningCombination(lStartingOccupancyPosition, DirectionSet.DIAGONAL, lOccupancyPositions, lReferenceId);
					lReferenceId++;
					sWinningCombinationSet.addGameAttribute(lWinningCombination);
					lPlayerSet.addGameAttribute(lWinningCombination);
					
					sLogger.info("Winning Combination Loaded: {{}} : {{}} : {{}}", lWinningCombination.getAttributeName(), lWinningCombination.getAttributeBitSet().getBitSetString(), lWinningCombination.getReferenceId());
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						lOccupancyPosition.addWinningCombination(lWinningCombination);
						sLogger.info("            with Positions: {{}}   : {{}}", lOccupancyPosition.getAttributeName(), lOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						Position lPosition = lOccupancyPosition.getPosition();
						OccupancyPosition lOpposingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lOpposingPlayer, lPosition);
						lOpposingOccupancyPosition.addWinningCombinationToRuleOut(lWinningCombination);
						sLogger.info("   and rules out Positions: {{}}   : {{}}", lOpposingOccupancyPosition.getAttributeName(), lOpposingOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
				}
			}

			
			for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
				for (int j = sConnectFourBoardConfig.getWinningCombinationLength() - 1; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
					Column lStartingColumn = ColumnSet.getColumn(i);
					Row lStartingRow = RowSet.getRow(j);
					Position lStartingPosition = PositionSet.getPosition(lStartingColumn, lStartingRow);
					OccupancyPosition lStartingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lStartingPosition);
					
					ArrayList <OccupancyPosition> lOccupancyPositions = new ArrayList <> ();
					lOccupancyPositions.add(lStartingOccupancyPosition);
					
					for (int l = 1; l < sConnectFourBoardConfig.getWinningCombinationLength(); l++) {
						Column lColumn = ColumnSet.getColumn(i + l);
						Row lRow = RowSet.getRow(j - l);
						Position lPosition = PositionSet.getPosition(lColumn, lRow);
						OccupancyPosition lOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lPlayer, lPosition);
						lOccupancyPositions.add(lOccupancyPosition);
					}
					
					WinningCombination lWinningCombination = new WinningCombination(lStartingOccupancyPosition, DirectionSet.OPPOSITE, lOccupancyPositions, lReferenceId);
					lReferenceId++;
					sWinningCombinationSet.addGameAttribute(lWinningCombination);
					lPlayerSet.addGameAttribute(lWinningCombination);
					
					sLogger.info("Winning Combination Loaded: {{}} : {{}} : {{}}", lWinningCombination.getAttributeName(), lWinningCombination.getAttributeBitSet().getBitSetString(), lWinningCombination.getReferenceId());
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						lOccupancyPosition.addWinningCombination(lWinningCombination);
						sLogger.info("            with Positions: {{}}   : {{}}", lOccupancyPosition.getAttributeName(), lOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
					for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
						Position lPosition = lOccupancyPosition.getPosition();
						OccupancyPosition lOpposingOccupancyPosition = OccupancyPositionSet.getOccupancyPosition(lOpposingPlayer, lPosition);
						lOpposingOccupancyPosition.addWinningCombinationToRuleOut(lWinningCombination);
						sLogger.info("   and rules out Positions: {{}}   : {{}}", lOpposingOccupancyPosition.getAttributeName(), lOpposingOccupancyPosition.getAttributeBitSet().getBitSetString());
					}
				}
			}
		}
		
		
	}
	
	
	
	private WinningCombinationSet(Class<?> pClass) {
		super(pClass, OccupancyPositionSet.getInstance().getGameAttributeMaxBitSize() + DirectionSet.getInstance().getGameAttributeMaxBitSize());
	}
	
	
	public static WinningCombinationSet getInstance() {
		return sWinningCombinationSet;
	}
	
	
	public static WinningCombinationSet getInstance (Player pPlayer) {
		return sPlayerWinningCombinationSets.get(pPlayer);
	}
	
	
	public static WinningCombination getWinningCombination (BitSet pBitSet) {
		return (WinningCombination) sWinningCombinationSet.getGameAttribute(new GameAttributeBitSet(pBitSet));
	}

	public static WinningCombination getWinningCombination (String pWinningCombinationName) {
		return (WinningCombination) sWinningCombinationSet.getGameAttribute(pWinningCombinationName);
	}
	
	public static WinningCombination getWinningCombination (OccupancyPosition pStartingOccupancyPosition, Direction pDirection) {
		return (WinningCombination) sWinningCombinationSet.getGameAttribute(WinningCombination.constructAttributeName(pStartingOccupancyPosition, pDirection));
	}

	public static List<WinningCombination> getWinningCombinations() {
		List <WinningCombination> lWinningCombinations = new ArrayList <> ();
		for (GameAttribute lGameAttribute : sWinningCombinationSet.getGameAttributes()) {
			if (lGameAttribute instanceof WinningCombination) {
				lWinningCombinations.add((WinningCombination) lGameAttribute);
			}
		}
		return lWinningCombinations;
	}
	
	public static List<WinningCombination> getPlayerWinningCombinations (Player pPlayer) {
		List <WinningCombination> lWinningCombinations = new ArrayList <> ();
		for (GameAttribute lGameAttribute : sPlayerWinningCombinationSets.get(pPlayer).getGameAttributes()) {
			if (lGameAttribute instanceof WinningCombination) {
				lWinningCombinations.add((WinningCombination) lGameAttribute);
			}
		}
		return lWinningCombinations;
	}
	
	public static Map <BitSet, WinningCombination> getPlayerWinningCombinationsMap (Player pPlayer) {
		HashMap <BitSet, WinningCombination> lWinningCombinations = new HashMap <> ();
		for (GameAttribute lGameAttribute : sPlayerWinningCombinationSets.get(pPlayer).getGameAttributes()) {
			if (lGameAttribute instanceof WinningCombination) {
				WinningCombination lWinningCombination = (WinningCombination) lGameAttribute;
				
				lWinningCombinations.put(lWinningCombination.getAttributeBitSet().getBitSet(), lWinningCombination);
			}
		}
		return lWinningCombinations;
	}
	
}
