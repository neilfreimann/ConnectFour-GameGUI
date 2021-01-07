package com.home.neil.connectfour.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Connect4PropertiesConfiguration extends PropertiesConfiguration {
	
	public static final String CLASS_NAME = Connect4PropertiesConfiguration.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static Connect4PropertiesConfiguration sInstance = null;
	
	public static synchronized Connect4PropertiesConfiguration getInstance () throws ConfigurationException, FileNotFoundException, IOException {
		sLogger.trace("Entering");
		if (sInstance == null) {
			sInstance = new Connect4PropertiesConfiguration();
		}
		sLogger.trace("Exiting");
		return sInstance;
	}
	
	
	private Connect4PropertiesConfiguration () throws ConfigurationException, FileNotFoundException, IOException {
		super ();
		
		
		sLogger.trace("Entering");
		sLogger.info("Properties File location: " + System.getProperty("conf.properties.location"));
		
		read(new FileReader( System.getProperty("conf.properties.location")));
		
		
		
		sLogger.trace("Exiting");
	}

}
