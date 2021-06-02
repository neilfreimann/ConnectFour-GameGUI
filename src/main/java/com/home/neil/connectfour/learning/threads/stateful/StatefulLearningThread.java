package com.home.neil.connectfour.learning.threads.stateful;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.task.BasicAppTask;
import com.home.neil.thread.terminating.throttle.persistent.PersistentThrottledAppThread;
import com.home.neil.workentity.WorkEntityException;

public abstract class StatefulLearningThread extends PersistentThrottledAppThread {
	public static final String CLASS_NAME = StatefulLearningThread.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected String mStatefulFileName;
	protected File mStatefulFile;
	protected String mStatefulFileJsonClassString;
	protected Class<?> mStatefulFileJsonClass;
	protected Object mStatefulJsonObj;

	protected StatefulLearningThread(StatefulLearningThreadConfig pConfig) {
		super(pConfig);
		mStatefulFileName = pConfig.getStatefulFileName();
		mStatefulFileJsonClassString = pConfig.getStatefulFileJsonClass();
	}

	protected StatefulLearningThread(StatefulLearningThreadCacheConfig pConfig) {
		super(pConfig);
		mStatefulFileName = pConfig.getStatefulFileName();
		mStatefulFileJsonClassString = pConfig.getStatefulFileJsonClass();
	}



	public void initializeImplementation() throws WorkEntityException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		try {
			mStatefulFileJsonClass = Class.forName(mStatefulFileJsonClassString);
		} catch (ClassNotFoundException e) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new WorkEntityException(e);
		}

		boolean lFoundFile = readStateFile();

		if (lFoundFile) {
			populateStatefulVariablesFromJsonObject();
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return;
		}
		
		initializeNewStatefulJsonObject();

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

		mInitialized = true;
	}

	private boolean readStateFile() throws WorkEntityException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		GsonBuilder lBuilder = new GsonBuilder();
		lBuilder.setPrettyPrinting();

		Gson lGson = lBuilder.create();

		mStatefulFile = new File(mStatefulFileName);
		try (BufferedReader lBR = new BufferedReader(new FileReader(mStatefulFile))) {
			mStatefulJsonObj = lGson.fromJson(lBR, mStatefulFileJsonClass);
		} catch (FileNotFoundException e) {
			sLogger.info("Json Parsing File Not Found {}", mStatefulFileName);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return false;
		} catch (IOException e) {
			sLogger.info("IO Exception File: {}", mStatefulFileName);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new WorkEntityException (e);
		} catch (JsonParseException e) {
			sLogger.error("Json Parsing Error occurred with file {}", mStatefulFileName);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			return false;
		}

		sLogger.info("Json Parsing File Found {}", mStatefulFileName);

		if (sLogger.isDebugEnabled()) {
			sLogger.debug("Json read \n\n {}\n", lGson.toJson(mStatefulJsonObj));
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return true;
	}

	public void finishImplementation() throws WorkEntityException {
		populateJsonObjectFromStatefulVariables();
		
		writeStateFile();
	}
	
	private boolean writeStateFile() throws WorkEntityException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		GsonBuilder lBuilder = new GsonBuilder();
		lBuilder.setPrettyPrinting();

		Gson lGson = lBuilder.create();

		try (FileWriter lWriter = new FileWriter(mStatefulFile)) {
			String lJson = lGson.toJson(mStatefulJsonObj);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("Json to write \n\n {}\n", lJson);
			}
			lWriter.write(lJson);
			lWriter.close();
		} catch (JsonParseException e) {
			sLogger.error("Json Parsing Error occurred with file {}", mStatefulFileName);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new WorkEntityException (e);
		} catch (IOException e) {
			sLogger.error("Json IO Exception Error occurred with file {}", mStatefulFileName);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new WorkEntityException (e);
		}

		sLogger.info("Json Parsing File Written {}", mStatefulFileName);


		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return true;
	}

	protected abstract void initializeNewStatefulJsonObject() throws WorkEntityException;
	
	protected abstract void populateStatefulVariablesFromJsonObject() throws WorkEntityException;

	protected abstract void populateJsonObjectFromStatefulVariables() throws WorkEntityException;

	public void checkImplementation() throws WorkEntityException {

	}

}
