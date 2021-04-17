package com.home.neil.connectfour.knowledgebase.operations;

import com.home.neil.appconfig.DefaultConfigValue;

public interface ConnectFourBoardConfig {
	@DefaultConfigValue (value = "6")
	public int getNumberOfRows ();
	
	@DefaultConfigValue (value = "7")
	public int getNumberOfColumns ();
	
	@DefaultConfigValue (value = "4")
	public int getWinningCombinationLength ();
}
