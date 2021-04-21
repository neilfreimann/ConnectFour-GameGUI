package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class OccupancyPositionSet extends ConnectFourBoardAttributeSet {

	public static final String CLASS_NAME = OccupancyPositionSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static OccupancyPositionSet sOccupancyPositionSet = null;
	private static HashMap <Player, OccupancyPositionSet> sPlayerOccupancyPositionSets = null;
	

	static {
		sOccupancyPositionSet = new OccupancyPositionSet(OccupancyPosition.class);
		
		sPlayerOccupancyPositionSets = new HashMap <> ();

		List<Player> lPlayers = PlayerSet.getRealPlayers();
		List<Position> lPositions = PositionSet.getPositions();

		for (Position lPosition : lPositions) {
			OccupancyPosition lOccupancyPosition = new OccupancyPosition(PlayerSet.NULL_PLAYER, lPosition);
			sOccupancyPositionSet.addGameAttribute(lOccupancyPosition);
			sLogger.info("Occupancy Position Loaded: {{}} : {{}}", lOccupancyPosition.getAttributeName(),
					lOccupancyPosition.getAttributeBitSet().getBitSetString());
		}
		
		for (Player lPlayer : lPlayers) {
			OccupancyPositionSet lPlayerOccupancyPositionSet = new OccupancyPositionSet (OccupancyPosition.class);
			sPlayerOccupancyPositionSets.put(lPlayer, lPlayerOccupancyPositionSet);
			for (Position lPosition : lPositions) {
				OccupancyPosition lOccupancyPosition = new OccupancyPosition(lPlayer, lPosition);
				sOccupancyPositionSet.addGameAttribute(lOccupancyPosition);
				lPlayerOccupancyPositionSet.addGameAttribute(lOccupancyPosition);
				sLogger.info("Occupancy Position Loaded: {{}} : {{}}", lOccupancyPosition.getAttributeName(),
						lOccupancyPosition.getAttributeBitSet().getBitSetString());
			}
		}

		
	}

	private OccupancyPositionSet(Class<?> pClass) {
		super(pClass, PlayerSet.getInstance().getGameAttributeMaxBitSize() + PositionSet.getInstance().getGameAttributeMaxBitSize());
	}

	public static OccupancyPositionSet getInstance() {
		return sOccupancyPositionSet;
	}

	public static OccupancyPosition getOccupancyPosition(BitSet pBitSet) {
		return (OccupancyPosition) sOccupancyPositionSet.getGameAttribute(new GameAttributeBitSet(pBitSet));
	}

	public static OccupancyPosition getOccupancyPosition(String pOccupancyPositionName) {
		return (OccupancyPosition) sOccupancyPositionSet.getGameAttribute(pOccupancyPositionName);
	}

	public static OccupancyPosition getOccupancyPosition(Player pPlayer, Position pStartingPosition) {
		return (OccupancyPosition) sOccupancyPositionSet.getGameAttribute(OccupancyPosition.constructAttributeName(pPlayer, pStartingPosition));
	}

	
	@SuppressWarnings("unchecked")
	public static List<OccupancyPosition> getOccupancyPositions() {
		return (List<OccupancyPosition>) sOccupancyPositionSet.getGameAttributes();
	}

	@SuppressWarnings("unchecked")
	public static List<OccupancyPosition> getOccupancyPositions(Player pPlayer) {
		return (List<OccupancyPosition>) sPlayerOccupancyPositionSets.get(pPlayer).getGameAttributes();
	}


}
