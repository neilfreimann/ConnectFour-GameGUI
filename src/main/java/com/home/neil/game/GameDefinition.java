package com.home.neil.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.game.attribute.GameAttribute;

public class GameDefinition extends GameAttribute {
	public static final String CLASS_NAME = GameDefinition.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static GameDefinitionConfigClassConfig sGameDefinitionConfigClassConfig = null;
	private static int sNumberOfGameDefinitions = Integer.MAX_VALUE; 
		
	public static void init () throws GameException {
		HashMap <String, GameAttribute> lStringToGameDefinitionMap = new HashMap<> ();
		HashMap <BitSet, GameAttribute> lBitSetToGameDefinitionMap = new HashMap<> ();
		
		try {
			sGameDefinitionConfigClassConfig = AppConfig.bind(GameDefinitionConfigClassConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			throw new GameException(e);
		}

		String [] lGameDefinitionConfigClasses = sGameDefinitionConfigClassConfig.getGameConfigClass();
		
		for (String lGameDefinitionConfigClassName : lGameDefinitionConfigClasses) {
			try {
				Class <?> lGameDefinitionConfigClass = Class.forName(lGameDefinitionConfigClassName); 
				GameDefinitionConfig lGameDefinitionConfig = AppConfig.bind(lGameDefinitionConfigClass);
				
				GameDefinition lGameDefinitionAttribute = new GameDefinition (lGameDefinitionConfig, lGameDefinitionConfigClassName);
				
				lStringToGameDefinitionMap.put (lGameDefinitionAttribute.getAttributeStringRepresentation(), lGameDefinitionAttribute);
				lBitSetToGameDefinitionMap.put (lGameDefinitionAttribute.getAttributeBitEncoding(), lGameDefinitionAttribute);
				
			} catch (ClassNotFoundException | NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
				throw new GameException(e);
			}
		}
		
		addStringAttributeMap(GameDefinition.class, lStringToGameDefinitionMap);
		addBitSetAttributeMap(GameDefinition.class, lBitSetToGameDefinitionMap);
	}
	
	public static GameDefinition getGameDefinition (BitSet pBitSet) {
		return (GameDefinition) getAttribute(GameDefinition.class, pBitSet);
	}

	public static GameDefinition getGameDefinition (String pStringRepresentation) {
		return (GameDefinition) getAttribute(GameDefinition.class, pStringRepresentation);
	}
	
	public static Collection <GameDefinition> getGameDefinitions () {
		return (Collection <GameDefinition>) getAttributes(GameDefinition.class);
	}	
	
	
	
	
	private GameDefinitionConfig mGameDefinitionConfig = null;
	private ArrayList <GamePlayerSpotDefinition> mGamePlayerSpotDefinitions = null;
	
	
	private GameDefinition (GameDefinitionConfig pGameDefinitionConfig, String pAttributeStringRepresentation) {
		super (pGameDefinitionConfig.getGameDefinitionId(), getEncodingSize(sNumberOfGameDefinitions), pAttributeStringRepresentation);
		mGameDefinitionConfig = pGameDefinitionConfig;
		
		
	}

	public GameDefinitionConfig getGameDefinitionConfig() {
		return mGameDefinitionConfig;
	}
	


}
