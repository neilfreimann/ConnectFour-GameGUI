package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class PositionSet extends ConnectFourBoardAttributeSet {
	public static final String CLASS_NAME = PositionSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static PositionSet sPositionSet = null;
	
	static {
		sPositionSet = new PositionSet(Position.class);
		
		List <Row> lRows = RowSet.getRows();
		List <Column> lColumns = ColumnSet.getColumns();
		int lReferenceId = 0;
		
		for (Row lRow : lRows) {
			for (Column lColumn : lColumns) {
				Position lPosition = new Position (lColumn, lRow, lReferenceId);
				sPositionSet.addGameAttribute(lPosition);
				sLogger.info("Position Loaded: {{}} : {{}} : {{}}", lPosition.getAttributeName(), lPosition.getAttributeBitSet().getBitSetString(), lPosition.getReferenceId());
				lReferenceId++;
			}
		}
	}
	
	private PositionSet(Class <?> pClass) {
		super(pClass, RowSet.getInstance().getGameAttributeMaxBitSize() + ColumnSet.getInstance().getGameAttributeMaxBitSize());
	}
	
	public static PositionSet getInstance () {
		return sPositionSet;
	}
	
	
	public static Position getPosition (BitSet pBitSet) {
		return (Position) sPositionSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Position getPosition (String pPositionName) {
		return (Position) sPositionSet.getGameAttribute(pPositionName);
	}
	
	public static Position getPosition (Column pColumn, Row pRow) {
		return (Position) sPositionSet.getGameAttribute(Position.constructAttributeName(pColumn, pRow));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Position> getPositions () {
		return (List<Position>) sPositionSet.getGameAttributes();
	}
}
