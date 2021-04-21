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
	
	private static PlayerSet sRealPlayerSet = null;
	private static PlayerSet sAllPlayerSet = null;

	public static final Player NULL_PLAYER =  new Player (0);

	static {
		sRealPlayerSet = new PlayerSet(Player.class);
		sAllPlayerSet = new PlayerSet(Player.class);
		for (int j = 1; j <= sConnectFourBoardConfig.getMaximumNumberOfPlayers(); j++) {
			Player lPlayer = new Player (j);

			sRealPlayerSet.addGameAttribute(lPlayer);
			sAllPlayerSet.addGameAttribute(lPlayer);
			sLogger.info("Player Loaded: {{}} : {{}}", lPlayer.getAttributeName(), lPlayer.getAttributeBitSet().getBitSetString());
		}
		
		sAllPlayerSet.addGameAttribute(NULL_PLAYER);
	}
	
	
	private PlayerSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(sConnectFourBoardConfig.getMaximumNumberOfPlayers()));
	}
	
	public static PlayerSet getInstance () {
		return sRealPlayerSet;
	}
	
	public static Player getRealPlayer (BitSet pBitSet) {
		return (Player) sRealPlayerSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Player getRealPlayer (String pPlayerName) {
		return (Player) sRealPlayerSet.getGameAttribute(pPlayerName);
	}
	
	public static Player getRealPlayer (int pPlayer) {
		return (Player) sRealPlayerSet.getGameAttribute(Player.constructAttributeName(pPlayer));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Player> getRealPlayers () {
		return (List<Player>) sRealPlayerSet.getGameAttributes();
	}
	
	
	public static Player getAllPlayer (BitSet pBitSet) {
		return (Player) sAllPlayerSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Player getAllPlayer (String pPlayerName) {
		return (Player) sAllPlayerSet.getGameAttribute(pPlayerName);
	}
	
	public static Player getAllPlayer (int pPlayer) {
		return (Player) sAllPlayerSet.getGameAttribute(Player.constructAttributeName(pPlayer));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Player> getAllPlayers () {
		return (List<Player>) sAllPlayerSet.getGameAttributes();
	}
	
	
}
