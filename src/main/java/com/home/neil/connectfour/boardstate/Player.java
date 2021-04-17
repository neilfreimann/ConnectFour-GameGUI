package com.home.neil.connectfour.boardstate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Player.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private int mPlayer = -1;
	
	public Player (int pPlayer) {
		super (pPlayer, sConnectFourBoardConfig.getMaximumNumberOfPlayers(), constructAttributeName(pPlayer));
		mPlayer = pPlayer;
	}
	
	public int getPlayer () {
		return mPlayer;
	}
	
	public static String constructAttributeName (int pPlayer) {
		return "P" + pPlayer;
	}
}
