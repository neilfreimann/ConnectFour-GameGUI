package com.home.neil.connectfour.boardstate.logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.ColumnSet;
import com.home.neil.connectfour.boardstate.OccupancyPosition;
import com.home.neil.connectfour.boardstate.Player;
import com.home.neil.connectfour.boardstate.Position;

public class BoardStateLogger {
	public static final String CLASS_NAME = BoardStateLogger.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;

	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}
	}

	public static void logBoardState(BoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (sLogger.isDebugEnabled()) {
			String lBoardStateLog;
	
			StringBuilder lLineBuffer = new StringBuilder ();

			StringBuilder lLogBuffer = new StringBuilder ();
			
			
			List <OccupancyPosition> lOccupancyPositions = pBoardState.decodeOccupancyPositions();
	
			for (OccupancyPosition lOccupancyPosition : lOccupancyPositions) {
				Player lPlayer = lOccupancyPosition.getPlayer();
				Position lPosition = lOccupancyPosition.getPosition();
				
				if (lPosition.getColumn() == ColumnSet.getColumn(0)) {
					lLineBuffer.append ("|   ");
				}
				
				switch (lPlayer.getPlayer()) {
				case 0: 
					lLineBuffer.append ("E   ");
					break;
				case 1: 
					lLineBuffer.append ("X   ");
					break;
				case 2: 
					lLineBuffer.append ("O   ");
					break;
				}
				
				if (lPosition.getColumn() == ColumnSet.getColumn(sConnectFourBoardConfig.getNumberOfColumns()-1)) {
					lLineBuffer.append ("|\n");
					lLogBuffer.insert(0,  lLineBuffer.toString());
					lLineBuffer = new StringBuilder();
				}
			}

			
			
			
			
			lLineBuffer.append ("\n");
			
			lLineBuffer.append ("----");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLineBuffer.append ("----");
			}
			lLineBuffer.append ("-\n");
			
			lLineBuffer.append ("|   ");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLineBuffer.append ("    ");
			}
			lLineBuffer.append ("|\n");

			
			lLogBuffer.insert(0,  lLineBuffer.toString());
			lLineBuffer = new StringBuilder();
			
			
			lLogBuffer.append ("----");
			for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				lLogBuffer.append ("----");
			}
			lLogBuffer.append ("-\n");
	
			lLogBuffer.append ("\n");
			
			lBoardStateLog = "Current Board Layout:\n" + lLogBuffer.toString();
	
			sLogger.debug(lBoardStateLog);
		}
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	private BoardStateLogger () {
		
	}

}
