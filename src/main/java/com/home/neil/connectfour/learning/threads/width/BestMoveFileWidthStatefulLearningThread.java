package com.home.neil.connectfour.learning.threads.width;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.learning.threads.stateful.StatefulLearningThread;
import com.home.neil.connectfour.learning.threads.stateful.breadcrumbs.BreadCrumbs;
import com.home.neil.pool.Pool;
import com.home.neil.task.BasicAppTask;
import com.home.neil.workentity.WorkEntityException;

public class BestMoveFileWidthStatefulLearningThread extends StatefulLearningThread {

	public static final String CLASS_NAME = StatefulLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private static BestMoveFileWidthStatefulLearningThreadConfig sBestMoveFileWidthStatefulLearningThreadConfig;
	private static BestMoveFileWidthStatefulLearningThreadCacheConfig sBestMoveFileWidthStatefulLearningThreadCacheConfig;

	static {
		try {
			sBestMoveFileWidthStatefulLearningThreadConfig = AppConfig.bind(BestMoveFileWidthStatefulLearningThreadConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sLogger.error("Binding Exception: {{}}", e.getMessage());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
		}
		sBestMoveFileWidthStatefulLearningThreadCacheConfig = new BestMoveFileWidthStatefulLearningThreadCacheConfig(sBestMoveFileWidthStatefulLearningThreadConfig);
	}

	
	private BreadCrumbs mBreadCrumbs = null;
	private Pool mPool = null;
	private int mThreadMetaDataBitLocation = 0;
	
	private BestMoveFileWidthStatefulLearningTask mOneTask = null;
	
	public BestMoveFileWidthStatefulLearningThread(Pool pPool) {
		super(sBestMoveFileWidthStatefulLearningThreadCacheConfig);
		mPool = pPool;
		mThreadMetaDataBitLocation = sBestMoveFileWidthStatefulLearningThreadCacheConfig.getThreadMetaDataBitLocation();
	}

	
	@Override
	public BasicAppTask setNewAppTask() {
		if (mOneTask == null) {
			mOneTask = new BestMoveFileWidthStatefulLearningTask(this, mBreadCrumbs, mPool);
			return mOneTask;
		}
		return null;
		
	}

	@Override
	protected void initializeNewStatefulJsonObject() throws WorkEntityException {
		mBreadCrumbs = new BreadCrumbs(0, sBestMoveFileWidthStatefulLearningThreadCacheConfig.getBreadCrumbExpansionSets(), null);
		
		
	}

	@Override
	protected void populateStatefulVariablesFromJsonObject() throws WorkEntityException {
		mBreadCrumbs = (BreadCrumbs) mStatefulJsonObj;
	}

	@Override
	protected void populateJsonObjectFromStatefulVariables() throws WorkEntityException {
		mStatefulJsonObj = mBreadCrumbs;
	}
	
	public Pool getPool () {
		return mPool;
	}


	public int getThreadMetaDataBitLocation() {
		return mThreadMetaDataBitLocation;
	}

}
