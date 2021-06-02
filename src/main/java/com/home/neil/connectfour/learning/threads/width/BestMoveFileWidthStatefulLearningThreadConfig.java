package com.home.neil.connectfour.learning.threads.width;

import com.home.neil.appconfig.DefaultConfigValue;
import com.home.neil.connectfour.learning.threads.stateful.StatefulLearningThreadConfig;

public interface BestMoveFileWidthStatefulLearningThreadConfig extends StatefulLearningThreadConfig {

	@DefaultConfigValue (value = "0")
	public int getThreadMetaDataBitLocation ();
	
	public int getInitialMetaDataCheckStartingExpansionLevel(int pExpansionSetId);

	public int getInitialMaximumExpansionLevel(int pExpansionSetId);

	public int getInitialMovesPerExpansionLevel(int pExpansionSetId);

	
	
	// Inherited
	@DefaultConfigValue (value = "com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbs")
	public String getStatefulFileJsonClass ();

	// Inherited
	@DefaultConfigValue (value = "0")
	public long getThrottleValue();
	
	@DefaultConfigValue (value = "true")
	public boolean getTerminateOnFailure();
	
	@DefaultConfigValue (value = "true")
	public boolean getFixedDuration ();
	
	@DefaultConfigValue (value = "3600000")
	public long getDurationToRunInMs();
	
	@DefaultConfigValue (value = "ThreadLogContext")
	public String getLogContextLabel();
	
	@DefaultConfigValue (value = "true")
	public boolean getSocketServerWorkEntityLock();
	
	@DefaultConfigValue (value = "9001")
	public int getSocketServerPortNumber();
	
	@DefaultConfigValue (value = "false")
	public boolean getActivateCheckTimer();
	
	@DefaultConfigValue (value = "180000")
	public int getCheckTimeIntervalInMS();
	
	@DefaultConfigValue (value = "false")
	public boolean getRecordContext();
	
	@DefaultConfigValue (value = "")
	public String getLogContext();
	
	@DefaultConfigValue (value = "false")
	public boolean getRecordStatistics();
	
	@DefaultConfigValue (value = "true")
	public boolean getRegisterMBean();

	@DefaultConfigValue (value = "")
	public String getBeanName();
	
	@DefaultConfigValue (value = "")
	public String getWorkEntityName();
	
}
