package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class DirectionSet extends ConnectFourBoardAttributeSet {
	public static final String CLASS_NAME = DirectionSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static DirectionSet sDirectionSet = null;
	
	static {
		sDirectionSet = new DirectionSet(Direction.class);
		
		Direction lDirection = new Direction (0, "V");
		sDirectionSet.addGameAttribute(lDirection);
		sLogger.info("Direction Loaded: {{}} : {{}}", lDirection.getAttributeName(), lDirection.getAttributeBitSet().getBitSetString());
		
		lDirection = new Direction (1, "H");
		sDirectionSet.addGameAttribute(lDirection);
		sLogger.info("Direction Loaded: {{}} : {{}}", lDirection.getAttributeName(), lDirection.getAttributeBitSet().getBitSetString());

		lDirection = new Direction (2, "D");
		sDirectionSet.addGameAttribute(lDirection);
		sLogger.info("Direction Loaded: {{}} : {{}}", lDirection.getAttributeName(), lDirection.getAttributeBitSet().getBitSetString());

		lDirection = new Direction (3, "O");
		sDirectionSet.addGameAttribute(lDirection);
		sLogger.info("Direction Loaded: {{}} : {{}}", lDirection.getAttributeName(), lDirection.getAttributeBitSet().getBitSetString());

	}
	
	public static final Direction VERTICAL = getDirection ("V");
	public static final Direction HORIZONTAL = getDirection ("H");
	public static final Direction DIAGONAL = getDirection ("D");
	public static final Direction OPPOSITE = getDirection ("O");
	
	private DirectionSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(3));
	}
	
	public static DirectionSet getInstance () {
		return sDirectionSet;
	}
	
	public static Direction getDirection (BitSet pBitSet) {
		return (Direction) sDirectionSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Direction getDirection (String pDirectionName) {
		return (Direction) sDirectionSet.getGameAttribute(pDirectionName);
	}
	
	@SuppressWarnings("unchecked")
	public static List <Direction> getDirections () {
		return (List<Direction>) sDirectionSet.getGameAttributes();
	}
}
