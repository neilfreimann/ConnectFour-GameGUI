package com.home.neil.connectfour.board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;

public class Move extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = Move.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private Column mColumn = null;
	private Player mPlayer = null;
	
	
	public Move (Player pPlayer, Column pColumn) {
		super (new GameAttribute [] {pPlayer, pColumn}, constructAttributeName (pPlayer, pColumn));
		mPlayer = pPlayer;
		mColumn = pColumn;
	}
	
	public Player getPlayer () {
		return mPlayer;
	}

	public Column getColumn () {
		return mColumn;
	}
	
	
	public static String constructAttributeName (Player pPlayer, Column pColumn) {
		return pPlayer.getAttributeName() + "_" + pColumn.getAttributeName();
	}
}
