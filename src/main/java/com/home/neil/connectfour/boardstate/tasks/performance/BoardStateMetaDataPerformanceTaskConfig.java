package com.home.neil.connectfour.boardstate.tasks.performance;

import com.home.neil.appconfig.DefaultConfigValue;
import com.home.neil.task.BasicAppTaskConfig;

public interface BoardStateMetaDataPerformanceTaskConfig extends BasicAppTaskConfig {
	@DefaultConfigValue (value = "false")
	public boolean getSocketServerWorkEntityLock();
	
	@DefaultConfigValue (value = "0")
	public int getSocketServerPortNumber();
	
	@DefaultConfigValue (value = "false")
	public boolean getActivateCheckTimer();
	
	@DefaultConfigValue (value = "180000")
	public int getCheckTimeIntervalInMS();
	
	@DefaultConfigValue (value = "false")
	public boolean getRecordContext();
	
	@DefaultConfigValue (value = "TaskLogContext")
	public String getLogContextLabel();
	
	@DefaultConfigValue (value = "")
	public String getLogContext();
	
	@DefaultConfigValue (value = "false")
	public boolean getRecordStatistics();
	
	@DefaultConfigValue (value = "false")
	public boolean getRegisterMBean();

	@DefaultConfigValue (value = "")
	public String getBeanName();
	
	@DefaultConfigValue (value = "")
	public String getWorkEntityName();
	
}
