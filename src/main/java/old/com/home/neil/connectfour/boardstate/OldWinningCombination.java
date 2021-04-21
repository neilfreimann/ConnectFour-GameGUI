package old.com.home.neil.connectfour.boardstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.ConnectFourBoardAttribute;
import com.home.neil.connectfour.boardstate.Position;

public class OldWinningCombination extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = OldWinningCombination.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private OldPlayer mPlayer = OldPlayer.SELF;
	private OldDirection mDirection = OldDirection.DIAGONAL;
	private Position mStartingPosition = null;
	private ArrayList<MovePosition> mMovePositions = new ArrayList<>();

	private String mWinningCombinationString;

	private static HashMap<String, OldWinningCombination> sAllWinningCombinations = new HashMap<>();
	private static HashMap<String, OldWinningCombination> sSelfWinningCombinations = new HashMap<>();
	private static HashMap<String, OldWinningCombination> sOpponentWinningCombinations = new HashMap<>();
	
	public static void init () {
		
	}

	static {

		for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
			for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
				OldWinningCombination lSelfWinningCombination = new OldWinningCombination(OldPlayer.SELF, OldDirection.VERTICAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				OldWinningCombination lOpponentWinningCombination = new OldWinningCombination(OldPlayer.OPPONENT, OldDirection.VERTICAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = 0; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
				OldWinningCombination lSelfWinningCombination = new OldWinningCombination(OldPlayer.SELF, OldDirection.HORIZONTAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				OldWinningCombination lOpponentWinningCombination = new OldWinningCombination(OldPlayer.OPPONENT, OldDirection.HORIZONTAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = 0; j <= (sConnectFourBoardConfig.getNumberOfRows() - sConnectFourBoardConfig.getWinningCombinationLength()); j++) {
				OldWinningCombination lSelfWinningCombination = new OldWinningCombination(OldPlayer.SELF, OldDirection.DIAGONAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				OldWinningCombination lOpponentWinningCombination = new OldWinningCombination(OldPlayer.OPPONENT, OldDirection.DIAGONAL, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}

		for (int i = 0; i <= (sConnectFourBoardConfig.getNumberOfColumns() - sConnectFourBoardConfig.getWinningCombinationLength()); i++) {
			for (int j = sConnectFourBoardConfig.getWinningCombinationLength() - 1; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
				OldWinningCombination lSelfWinningCombination = new OldWinningCombination(OldPlayer.SELF, OldDirection.OPPOSITE, Position.getPosition(i, j));
				sAllWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sSelfWinningCombinations.put(lSelfWinningCombination.mWinningCombinationString, lSelfWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lSelfWinningCombination.mWinningCombinationString);

				OldWinningCombination lOpponentWinningCombination = new OldWinningCombination(OldPlayer.OPPONENT, OldDirection.OPPOSITE, Position.getPosition(i, j));
				sAllWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sOpponentWinningCombinations.put(lOpponentWinningCombination.mWinningCombinationString, lOpponentWinningCombination);
				sLogger.info("Winning Combination Loaded: {}", lOpponentWinningCombination.mWinningCombinationString);
			}
		}
	}

	private OldWinningCombination(OldPlayer pPlayer, OldDirection pDirection, Position pStartingPosition) {
		mPlayer = pPlayer;
		mDirection = pDirection;
		mStartingPosition = pStartingPosition;
		mWinningCombinationString = pPlayer.getPlayerString() + "_" + pDirection.getDirectionString() + "_" + pStartingPosition.getPositionString();
	}

	public OldPlayer getPlayer() {
		return mPlayer;
	}

	public OldDirection getDirection() {
		return mDirection;
	}

	public Position getStartingPosition() {
		return mStartingPosition;
	}

	public static OldWinningCombination getWinningCombination(OldPlayer pPlayer, OldDirection pDirection, Position pPosition) {
		String lWinningCombinationString = pPlayer.getPlayerString() + "_" + pDirection.getDirectionString() + "_" + pPosition.getPositionString();
		return sAllWinningCombinations.get(lWinningCombinationString);
	}

	public static Collection<OldWinningCombination> getAllWinningCombinations() {
		return sAllWinningCombinations.values();
	}

	public static Collection<OldWinningCombination> getSelfWinningCombinations() {
		return sSelfWinningCombinations.values();
	}

	public static Collection<OldWinningCombination> getOpponentWinningCombinations() {
		return sOpponentWinningCombinations.values();
	}

	public static void setWinningCombinationMovePositions() {
		for (OldWinningCombination lWinningCombination : sAllWinningCombinations.values()) {
			OldDirection lDirection = lWinningCombination.getDirection();

			sLogger.info("==================================================================================================================================================");
			
			Position lStartingPosition = lWinningCombination.getStartingPosition();
			MovePosition lStartingMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lStartingPosition);
			lWinningCombination.mMovePositions.add(lStartingMovePosition);
			sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lStartingMovePosition.getMovePositionString());
			lStartingMovePosition.addWinningCombination(lWinningCombination);

			for (int o = 1; o < sConnectFourBoardConfig.getWinningCombinationLength(); o++) {
				if (lDirection == OldDirection.HORIZONTAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn() + o, lStartingPosition.getRow());
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == OldDirection.VERTICAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn(), lStartingPosition.getRow() + o);
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == OldDirection.DIAGONAL) {
					Position lNextPosition = Position.getPosition(lStartingPosition.getColumn() + o, lStartingPosition.getRow() + o);
					MovePosition lNextMovePosition = MovePosition.getMovePosition(lWinningCombination.getPlayer(), lNextPosition);
					lWinningCombination.mMovePositions.add(lNextMovePosition);
					sLogger.info("Winning Combination  {} MovePosition Loaded: {}", lWinningCombination.mWinningCombinationString, lNextMovePosition.getMovePositionString());
					lNextMovePosition.addWinningCombination(lWinningCombination);
				} else if (lDirection == OldDirection.OPPOSITE) {
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
