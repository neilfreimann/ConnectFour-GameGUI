package com.home.neil.connectfour.boardstate.tasks;

import com.home.neil.appconfig.DefaultConfigValue;

public interface BoardStateMetaDataTaskConfig {
	@DefaultConfigValue (value = "false")
	public boolean getRecordStatistics ();
}
