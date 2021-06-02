package com.home.neil.connectfour.board;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class GameStateSet extends ConnectFourBoardAttributeSet {
	public static final String CLASS_NAME = GameStateSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static GameStateSet sGameStateSet = null;
	
	static {
		sGameStateSet = new GameStateSet(GameState.class);
		UNDECIDED = new GameState (0, "U");
		WIN = new GameState (1, "W");
		LOSS =  new GameState (2, "L");
		DRAW = new GameState (3, "D");
		
		sGameStateSet.addGameAttribute(GameStateSet.WIN);
		sGameStateSet.addGameAttribute(GameStateSet.LOSS);
		sGameStateSet.addGameAttribute(GameStateSet.DRAW);
		sGameStateSet.addGameAttribute(GameStateSet.UNDECIDED);
		sLogger.info("GameState Loaded: {{}} : {{}}", GameStateSet.WIN.getAttributeName(), GameStateSet.WIN.getAttributeBitSet().getBitSetString());
		sLogger.info("GameState Loaded: {{}} : {{}}", GameStateSet.LOSS.getAttributeName(), GameStateSet.LOSS.getAttributeBitSet().getBitSetString());
		sLogger.info("GameState Loaded: {{}} : {{}}", GameStateSet.DRAW.getAttributeName(), GameStateSet.DRAW.getAttributeBitSet().getBitSetString());
		sLogger.info("GameState Loaded: {{}} : {{}}", GameStateSet.UNDECIDED.getAttributeName(), GameStateSet.UNDECIDED.getAttributeBitSet().getBitSetString());
	}
	
	public static final GameState WIN;
	public static final GameState LOSS;
	public static final GameState DRAW;
	public static final GameState UNDECIDED;
	
	private GameStateSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(3));
	}
	
	public static GameStateSet getInstance () {
		return sGameStateSet;
	}
	
	public static GameState getGameState (BitSet pBitSet) {
		return (GameState) sGameStateSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static GameState getGameState (String pGameState) {
		return (GameState) sGameStateSet.getGameAttribute(pGameState);
	}
	
	@SuppressWarnings("unchecked")
	public static List <Column> getColumns () {
		return (List<Column>) sGameStateSet.getGameAttributes();
	}
}
