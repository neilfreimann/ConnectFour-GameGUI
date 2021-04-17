package com.home.neil.connectfour.boardstate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.game.attribute.GameAttributeSet;

public abstract class ConnectFourBoardAttributeSet extends GameAttributeSet {
	public static final String CLASS_NAME = ConnectFourBoardAttribute.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	
	static {
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
			sLogger.fatal (e.getMessage());
			
		}
	}

	
	protected ConnectFourBoardAttributeSet(Class <?> pGameAttributeClass, int pGameAttributeMaxBitSetSize) {
		super(pGameAttributeClass, pGameAttributeMaxBitSetSize);
	}
}