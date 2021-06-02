package com.home.neil.connectfour.learning.threads.width;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.learning.threads.stateful.StatefulLearningThreadCacheConfig;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbExpansionSet;

public class BestMoveFileWidthStatefulLearningThreadCacheConfig extends StatefulLearningThreadCacheConfig {
	public static final String CLASS_NAME = BestMoveFileWidthStatefulLearningThreadCacheConfig.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected int mThreadMetaDataBitLocation = 0;
	
	public List <BreadCrumbExpansionSet> mBreadCrumbExpansionSets = null;
	
	public BestMoveFileWidthStatefulLearningThreadCacheConfig(BestMoveFileWidthStatefulLearningThreadConfig pConfig) {
		super (pConfig);
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		mThreadMetaDataBitLocation = pConfig.getThreadMetaDataBitLocation();
		mBreadCrumbExpansionSets = new ArrayList <> ();
		int i = 0;
		boolean lFound = true;
		do {
			try {
				int lInitialMetaDataCheckStartingExpansionLevel = pConfig.getInitialMetaDataCheckStartingExpansionLevel(i);
				int lInitialMaximumExpansionLevel = pConfig.getInitialMaximumExpansionLevel(i);
				int lInitialMovesPerExpansionLevel = pConfig.getInitialMovesPerExpansionLevel(i);
				BreadCrumbExpansionSet lBreadCrumbExpansionSet = new BreadCrumbExpansionSet(i, lInitialMetaDataCheckStartingExpansionLevel, lInitialMaximumExpansionLevel, lInitialMovesPerExpansionLevel);
				mBreadCrumbExpansionSets.add(lBreadCrumbExpansionSet);
				sLogger.info("Found initial Expansion set: {{}} {{}} {{}} {{}}", i, lInitialMetaDataCheckStartingExpansionLevel, lInitialMaximumExpansionLevel, lInitialMovesPerExpansionLevel);
				i++;
			}catch (NoSuchElementException e) {
				lFound = false;
			}
		} while (lFound);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public int getThreadMetaDataBitLocation() {
		return mThreadMetaDataBitLocation;
	}
	
	public List <BreadCrumbExpansionSet> getBreadCrumbExpansionSets() {
		return mBreadCrumbExpansionSets;
	}

	
	
}
