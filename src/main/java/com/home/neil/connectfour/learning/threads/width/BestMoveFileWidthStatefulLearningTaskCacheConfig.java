package com.home.neil.connectfour.learning.threads.width;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.task.BasicAppTaskCacheConfig;

public class BestMoveFileWidthStatefulLearningTaskCacheConfig extends BasicAppTaskCacheConfig {
	public static final String CLASS_NAME = BestMoveFileWidthStatefulLearningTaskCacheConfig.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public BestMoveFileWidthStatefulLearningTaskCacheConfig(BestMoveFileWidthStatefulLearningTaskConfig pConfig) {
		super (pConfig);
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
}
