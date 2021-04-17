package com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class RowSet extends ConnectFourBoardAttributeSet {

	public static final String CLASS_NAME = RowSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static RowSet sRowSet = null;
	
	static {
		sRowSet = new RowSet(Row.class);
		for (int j = 0; j < sConnectFourBoardConfig.getNumberOfRows(); j++) {
			Row lRow = new Row (j);
			sRowSet.addGameAttribute(lRow);
			sLogger.info("Row Loaded: {{}} : {{}}", lRow.getAttributeName(), lRow.getAttributeBitSet().getBitSetString());
		}
	}
	
	private RowSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(sConnectFourBoardConfig.getNumberOfRows()));
	}
	
	public static RowSet getInstance () {
		return sRowSet;
	}
	
	public static Row getRow (BitSet pBitSet) {
		return (Row) sRowSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Row getRow (String pRowName) {
		return (Row) sRowSet.getGameAttribute(pRowName);
	}
	
	public static Row getRow (int pRow) {
		return (Row) sRowSet.getGameAttribute(Row.constructAttributeName(pRow));
	}
	
	
	@SuppressWarnings("unchecked")
	public static List <Row> getRows () {
		return (List<Row>) sRowSet.getGameAttributes();
	}
	
	
}
