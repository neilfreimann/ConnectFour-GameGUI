package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Move extends BoardAttribute {
	public static final String CLASS_NAME = Move.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static int sColumnBooleanEncodingSize = 0;

	

	private static HashMap <String, Move> sMoves = new HashMap <> ();
	
	
	private Player mPlayer = null;
	private BitSet mColumnBooleanEncoding = null;
	private int mColumn = 0;
	private String mMoveString;

	public static final Move NOMOVE = new Move (Player.NULLPLAYER, -1);
	
	static {
		int lColumns = sConnectFourBoardConfig.getNumberOfColumns();
		while (lColumns > 0) {
			lColumns = lColumns >> 1;
			sColumnBooleanEncodingSize++;
		}

		sMoves.put(NOMOVE.getMoveString(), NOMOVE);
		
		for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				Move lSelfMove = new Move (Player.SELF, i);
				sMoves.put(lSelfMove.getMoveString(), lSelfMove);
				Move lOpponentMove = new Move (Player.OPPONENT, i);
				sMoves.put(lOpponentMove.getMoveString(), lOpponentMove);
		}

		
	}

	public static Move getMove (Player pPlayer, int pColumn) {
		String lMoveString = constructMoveString(pPlayer, pColumn);
		return getMove (lMoveString);
	}
	
	public static Move getMove (String pMoveString) {
		return sMoves.get(pMoveString);
	}
	
	
	public static String constructMoveString(Player pPlayer, int pColumn) {
		return pPlayer.getPlayerString() + "_" + String.valueOf(pColumn);
	}


	private Move(Player pPlayer, int pColumn) {
		mColumn = pColumn;
		mPlayer = pPlayer;
		
		mColumnBooleanEncoding = encodeToBitSet(pColumn, sColumnBooleanEncodingSize);

		mMoveString = constructMoveString(pPlayer, pColumn);

	}

	public BitSet getColumnBooleanEncoding() {
		return mColumnBooleanEncoding;
	}


	public int getColumn() {
		return mColumn;
	}

	
	

	public String getMoveString() {
		return mMoveString;
	}
	
	public static Collection <Move> getMoves () {
		return sMoves.values();
	}

	public Player getPlayer() {
		return mPlayer;
	}
	
}
