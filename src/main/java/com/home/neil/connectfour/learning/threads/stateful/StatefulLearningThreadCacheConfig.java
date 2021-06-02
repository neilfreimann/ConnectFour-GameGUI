package com.home.neil.connectfour.learning.threads.stateful;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.thread.terminating.throttle.persistent.PersistentThrottledAppThreadCacheConfig;

public class StatefulLearningThreadCacheConfig extends PersistentThrottledAppThreadCacheConfig {
	public static final String CLASS_NAME = StatefulLearningThreadCacheConfig.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected String mStatefulFileName;
	protected String mStatefulFileJsonClass;
	
	public StatefulLearningThreadCacheConfig(StatefulLearningThreadConfig pConfig) {
		super (pConfig);
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mStatefulFileName = pConfig.getStatefulFileName();
		mStatefulFileJsonClass = pConfig.getStatefulFileJsonClass();
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public String getStatefulFileName() {
		return mStatefulFileName;
	}
	
	public String getStatefulFileJsonClass() {
		return mStatefulFileJsonClass;
	}

	
	
}
