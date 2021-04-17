package com.home.neil.connectfour;

import com.home.neil.appconfig.DefaultConfigValue;
import com.home.neil.game.GameDefinitionConfig;

public interface ConnectFourBoardConfig extends GameDefinitionConfig {
	@DefaultConfigValue (value = "6")
	public int getNumberOfRows ();
	
	@DefaultConfigValue (value = "7")
	public int getNumberOfColumns ();
	
	@DefaultConfigValue (value = "4")
	public int getWinningCombinationLength ();
}
