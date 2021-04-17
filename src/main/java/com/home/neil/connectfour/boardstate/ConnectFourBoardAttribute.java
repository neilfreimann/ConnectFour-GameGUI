package com.home.neil.connectfour.boardstate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.connectfour.knowledgebase.operations.ConnectFourBoardConfig;

public abstract class BoardAttribute {
	public static final String CLASS_NAME = BoardAttribute.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	
	static {
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}
	}

	
	public static BitSet encodeToBitSet(int pNumber, int pMaxBitSetSize) {
		BitSet lBits = new BitSet(pMaxBitSetSize);
		int lIndex = 0;
		while (pNumber != 0L) {
			if (pNumber % 2L != 0) {
				lBits.set(lIndex);
			}
			++lIndex;
			pNumber = pNumber >>> 1;
		}
		return lBits;

	}

	public static int convert(BitSet pBitSet) {
		int lValue = 0;
		for (int i = 0; i < pBitSet.length(); ++i) {
			lValue += pBitSet.get(i) ? (1L << i) : 0L;
		}
		return lValue;
	}
	

	protected BoardAttribute () {
		
	}
	
}
