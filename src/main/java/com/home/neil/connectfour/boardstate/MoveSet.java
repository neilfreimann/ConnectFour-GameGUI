package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class MoveSet extends ConnectFourBoardAttributeSet {

	public static final String CLASS_NAME = MoveSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static MoveSet sMoveSet = null;
	
	static {
		sMoveSet = new MoveSet(Move.class);
		
		List <Player> lPlayers = PlayerSet.getRealPlayers();
		List <Column> lColumns = ColumnSet.getColumns();
		
		
		Move lNullMove = new Move (PlayerSet.NULL_PLAYER, ColumnSet.getColumn (new BitSet()));
		NULLMOVE = lNullMove;
		
		sMoveSet.addGameAttribute(lNullMove);
		sLogger.info("Move Loaded: {{}} : {{}}", lNullMove.getAttributeName(), lNullMove.getAttributeBitSet().getBitSetString());
		
		for (Player lPlayer : lPlayers) {
			for (Column lColumn : lColumns) {
				Move lMove = new Move (lPlayer, lColumn);
				sMoveSet.addGameAttribute(lMove);
				sLogger.info("Move Loaded: {{}} : {{}}", lMove.getAttributeName(), lMove.getAttributeBitSet().getBitSetString());
			}
		}
	}
	
	public static final Move NULLMOVE;
	
	private MoveSet(Class <?> pClass) {
		super(pClass, PlayerSet.getInstance().getGameAttributeMaxBitSize() + ColumnSet.getInstance().getGameAttributeMaxBitSize());
	}
	
	public static MoveSet getInstance () {
		return sMoveSet;
	}
	
	
	public static Move getMove (BitSet pBitSet) {
		return (Move) sMoveSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	
	public static Move getMove (String pMoveName) {
		return (Move) sMoveSet.getGameAttribute(pMoveName);
	}

	public static Move getMove (Player pPlayer, Column pColumn) {
		return (Move) sMoveSet.getGameAttribute(Move.constructAttributeName(pPlayer, pColumn));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Move> getMoves () {
		return (List<Move>) sMoveSet.getGameAttributes();
	}
}
