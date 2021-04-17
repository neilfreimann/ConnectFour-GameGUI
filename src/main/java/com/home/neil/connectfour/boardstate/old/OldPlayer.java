package com.home.neil.connectfour.boardstate;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Player {
	public static final String CLASS_NAME = Player.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private boolean mPlayerIsSelf = false;
	private boolean mIsNullPlayer = false;
	
	private String mPlayerString;
	
	private Player (boolean pIsNullPlayer, boolean pPlayerIsSelf, String pPlayerString) {
		mIsNullPlayer = pIsNullPlayer;
		mPlayerIsSelf = pPlayerIsSelf;
		mPlayerString = pPlayerString;
	}
	
	public static final Player SELF = new Player (false, true, "S");
	public static final Player OPPONENT = new Player (false, false, "O");
	public static final Player NULLPLAYER = new Player (true, false, "N");
	
	public Player getPlayer (boolean pIsNullPlayer, boolean pPlayerIsSelf) {
		if (!pIsNullPlayer) {
			if (pPlayerIsSelf) {
				return SELF;
			} else {
				return OPPONENT;
			}
		} else {
			return NULLPLAYER;
		}
	}

	public boolean isPlayerIsSelf () {
		return mPlayerIsSelf;
	}
	
	public boolean isNullPlayer () {
		return mIsNullPlayer;
	}
	
	public BitSet getPlayerBooleanEncoding () {
		BitSet lBitSet = new BitSet (2);
		lBitSet.set(0, mPlayerIsSelf);
		lBitSet.set(1, mIsNullPlayer);
		return lBitSet;
	}

	public String getPlayerString () {
		return mPlayerString;
	}
	
}
