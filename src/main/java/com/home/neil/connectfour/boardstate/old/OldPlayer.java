package com.home.neil.connectfour.boardstate.old;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldPlayer {
	public static final String CLASS_NAME = OldPlayer.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private boolean mPlayerIsSelf = false;
	private boolean mIsNullPlayer = false;
	
	private String mPlayerString;
	
	private OldPlayer (boolean pIsNullPlayer, boolean pPlayerIsSelf, String pPlayerString) {
		mIsNullPlayer = pIsNullPlayer;
		mPlayerIsSelf = pPlayerIsSelf;
		mPlayerString = pPlayerString;
	}
	
	public static final OldPlayer SELF = new OldPlayer (false, true, "S");
	public static final OldPlayer OPPONENT = new OldPlayer (false, false, "O");
	public static final OldPlayer NULLPLAYER = new OldPlayer (true, false, "N");
	
	public OldPlayer getPlayer (boolean pIsNullPlayer, boolean pPlayerIsSelf) {
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
