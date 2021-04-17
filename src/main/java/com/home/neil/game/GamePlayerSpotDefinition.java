package com.home.neil.game;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.game.attribute.GameAttribute;


public class GamePlayerSpotDefinition extends GameAttribute {
	public static final String CLASS_NAME = GamePlayerSpotDefinition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public static void init () throws GameException {
	
	}
	
	public Collection <GamePlayerSpotDefinition> getGamePlayerSpots () {
		return sGamePlayerSpots.values();
	}
	
	public Collection <GamePlayerSpotDefinition> getGamePlayerSpots (int pNumberOfPlayers) throws GameException {
		if (pNumberOfPlayers < sGameConfig.getMinimumNumberOfPlayers()) {
			throw new GameException ("Requested Game Player Spots is less than the minimum for a game");
		} else if (pNumberOfPlayers < sGameConfig.getMaximumNumberOfPlayers()) {
			throw new GameException ("Requested Game Player Spots is greater than the maximum for a game");
		}
		ArrayList <GamePlayerSpotDefinition> lGamePlayerSpots  = new ArrayList<> ();
		
		for (int i = 1; i <= pNumberOfPlayers; i++) {
			lGamePlayerSpots.add(getGamePlayerSpot(i));
		}
		
		return lGamePlayerSpots;
		
	}
	
	public GamePlayerSpotDefinition getNullGamePlayerSpot () {
		return sGamePlayerSpots.get("0");
	}
	
	public GamePlayerSpotDefinition getGamePlayerSpot (int pPlayerId) {
		return sGamePlayerSpots.get(String.valueOf(pPlayerId));
	}
	
	
	private GameDefinition mGameDefinition = null;
	
	private GamePlayerSpotDefinition (GameDefinition pGameDefinition, int pPlayerId, String pPlayerString) {
		mGameDefinition = pGameDefinition;
		GameDefinitionConfig lGameDefinitionConfig = pGameDefinition.getGameDefinitionConfig();
		
		HashMap <String, GameAttribute> lStringToGameDefinitionMap = new HashMap<> ();
		HashMap <BitSet, GameAttribute> lBitSetToGameDefinitionMap = new HashMap<> ();
		
		GameDefinition.encodeToBitSet(pPlayerId, getEncodingSize(sGameConfig.getMaximumNumberOfPlayers()));
		mStringRepresentation = pPlayerString;
	}

}
