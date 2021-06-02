package com.home.neil.connectfour.board;

import java.util.BitSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttributeBitSet;

public class ColumnSet extends ConnectFourBoardAttributeSet {
	public static final String CLASS_NAME = ColumnSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static ColumnSet sColumnSet = null;
	
	static {
		sColumnSet = new ColumnSet(Row.class);
		for (int j = 0; j < sConnectFourBoardConfig.getNumberOfColumns(); j++) {
			Column lColumn = new Column (j);
			sColumnSet.addGameAttribute(lColumn);
			sLogger.info("Column Loaded: {{}} : {{}}", lColumn.getAttributeName(), lColumn.getAttributeBitSet().getBitSetString());
		}
	}
	
	private ColumnSet(Class <?> pClass) {
		super(pClass, GameAttributeBitSet.getEncodingSize(sConnectFourBoardConfig.getNumberOfColumns()));
	}
	
	public static ColumnSet getInstance () {
		return sColumnSet;
	}

	
	public static Column getColumn (BitSet pBitSet) {
		return (Column) sColumnSet.getGameAttribute(new GameAttributeBitSet (pBitSet));
	}
	
	public static Column getColumn (String pColumnName) {
		return (Column) sColumnSet.getGameAttribute(pColumnName);
	}
	
	public static Column getColumn (int pColumn) {
		return (Column) sColumnSet.getGameAttribute(Column.constructAttributeName(pColumn));
	}
	
	@SuppressWarnings("unchecked")
	public static List <Column> getColumns () {
		return (List<Column>) sColumnSet.getGameAttributes();
	}
}
