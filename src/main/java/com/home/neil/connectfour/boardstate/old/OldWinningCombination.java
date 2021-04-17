package com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinningCombination extends BoardAttribute {
	public static final String CLASS_NAME = WinningCombination.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private Player mPlayer = Player.SELF;
	private Direction mDirection = Direction.DIAGONAL;
	private Position mStartingPosition = null;
	private ArrayList<MovePosition> mMovePositions = new ArrayList<>();

	private String mWinningCombinationString;

	private static HashMap<String, WinningCombination> sAllWinningCombinations = new HashMap<>();
	private static HashMap<String, WinningCombination> sSelfWinningCombinations = new HashMap<>();
	private static HashMap<String, WinningCombination> sOpponentWinningCombinations = new HashMap<>();
	
	public static void init () {
		
	}

	static {

		for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
			for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
				WinningCombination lSelfWinningCombination = new WinningCombination(Player.SELF, Direction.VERTICAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				WinningCombination lOpponentWinningCombination = new WinningCombination(Player.OPPONENT, Direction.VERTICAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = 0; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
				WinningCombination lSelfWinningCombination = new WinningCombination(Player.SELF, Direction.HORIZONTAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				WinningCombination lOpponentWinningCombination = new WinningCombination(Player.OPPONENT, Direction.HORIZONTAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
				WinningCombination lSelfWinningCombination = new WinningCombination(Player.SELF, Direction.DIAGONAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				WinningCombination lOpponentWinningCombination = new WinningCombination(Player.OPPONENT, Direction.DIAGONAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = sConnectFourBoardConfig.getWinningCombinationLength() - 1; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
				WinningCombination lSelfWinningCombination = new WinningCombination(Player.SELF, Direction.OPPOSITE, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				WinningCombination lOpponentWinningCombination = new WinningCombination(Player.OPPONENT, Direction.OPPOSITE, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}
	}

	private WinningCombination(Player pPlayer, Direction pDirection, Position pStartingPosition) {
		mPlayer = pPlayer;
		mDirection = pDirection;
		mStartingPosition = pStartingPosition;
		mWinningCombinationString = pPlayer.getPlayerString() + "_" + pDirection.getDirectionString() + "_" + pStartingPosition.getPositionString();
	}

	public Player getPlayer() {
		return mPlayer;
	}

	public Direction getDirection() {
		return mDirection;
	}

	public Position getStartingPosition() {
		return mStartingPosition;
	}

	public static WinningCombination getWinningCombination(Player pPlayer, Direction pDirection, Position pPosition) {
		String lWinningCombinationString = pPlayer.getPlayerString() + "_" + pDirection.getDirectionString() + "_" + pPosition.getPositionString();
		return sAllWinningCombinations.get(lWinningCombinationString);
	}

	public static Collection<WinningCombination> getAllWinningCombinations() {
		return sAllWinningCombinations.values();
	}

	public static Collection<WinningCombination> getSelfWinningCombinations() {
		return sSelfWinningCombinations.values();
	}

	public static Collection<WinningCombination> getOpponentWinningCombinations() {
		return sOpponentWinningCombinations.values();
	}

	public static void setWinningCombinationMovePositions() {
		for (WinningCombination lWinningCombination : sAllWinningCombinations.values()) {
			Direction lDirection = lWinningCombination.getDirection();

			sLogger.info("==================================================================================================================================================");
			
			Position lStartingPosition = lWinningCombination.getStartingPosition();
			MovePosition lStartingMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lStartingPosition);
			lWinningCombination.mMovePositions.add(lStartingMovePosition);
			sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lStartingMovePosition.getMovePositionString());
			lStartingMovePosition.addWinningCombination(lWinningCombination);

			for (int o = 1; o < sConnectFourBoardConfig.getWinningCombinationLength(); o++) {
				if (lDirection == Direction.HORIZONTAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn() + o, lStartingPosition.getRow());
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == Direction.VERTICAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn(), lStartingPosition.getRow() + o);
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == Direction.DIAGONAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn() + o, lStartingPosition.getRow() + o);
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == Direction.OPPOSITE) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn() + o, lStartingPosition.getRow() - o);
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				}
			}
		}
	}
	
	public ArrayList <MovePosition> getMovePositions () {
		return mMovePositions;
	}

	public String getWinningCombinationString() {
		return mWinningCombinationString;
	}
}
