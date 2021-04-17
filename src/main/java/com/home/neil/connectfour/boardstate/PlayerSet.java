package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class PlayerSet extends ConnectFourBoardAttributeSet {

	public static final String CLASS_NAME = PlayerSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static PlayerSet sPlayerSet = null;
	
	static {
		sPlayerSet = new PlayerSet(Player.class);
		for (int j = 1; j <= sConnectFourBoardConfig.getMaximumNumberOfPlayers(); j++) {
			Player lPlayer = new Player (j);

			sPlayerSet.addGameAttribute(lPlayer);
			sLogger.info("Player Loaded: {{}} : {{}}", lPlayer.getAttributeName(), lPlayer.getAttributeBitSet().getBitSetString());
		}
	}
	
	public static final Player NULL_PLAYER =  new Player (0);
	
	private PlayerSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(sConnectFourBoardConfig.getMaximumNumberOfPlayers()));
	}
	
	public static PlayerSet getInstance () {
		return sPlayerSet;
	}
	
	public static Player getPlayer (BitSet pBitSet) {
		return (Player) sPlayerSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Player getPlayer (String pPlayerName) {
		return (Player) sPlayerSet.getGameAttribute(pPlayerName);
	}
	
	public static Player getPlayer (int pPlayer) {
		return (Player) sPlayerSet.getGameAttribute(Player.constructAttributeName(pPlayer));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Player> getPlayers () {
		return (List<Player>) sPlayerSet.getGameAttributes();
	}
	
	
}
