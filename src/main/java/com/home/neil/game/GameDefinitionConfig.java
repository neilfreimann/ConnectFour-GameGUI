package com.home.neil.game;

import com.home.neil.appconfig.DefaultConfigValue;

public interface GameDefinitionConfig {
	public String getGameName ();

	public int getGameDefinitionId ();
	
	
	// Game Player Spot Definition
	@DefaultConfigValue (value = "2")
	public int getMinimumNumberOfPlayers ();
	
	@DefaultConfigValue (value = "2")
	public int getMaximumNumberOfPlayers ();

	
	// Player Definition
	public String [] getPlayerDefinitionClasses ();
	public String getHumanPlayerDefinitionClass();
	
	
}
