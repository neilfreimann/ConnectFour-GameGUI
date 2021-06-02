package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;

public class BreadCrumbExpansionSet implements IBreadCrumbExpansionSet{
	public static final String CLASS_NAME = BreadCrumbExpansionSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	
	@SerializedName(value="expansionsetid")
	private int mExpansionSetId = 1;

	@SerializedName(value="metadatacheckstartingexpansionlevel")
	private int mMetaDataCheckStartingExpansionLevel = 1;

	@SerializedName(value="maximumexpansionlevel")
	private int mMaximumExpansionLevel = 4;

	@SerializedName(value="movesperexpansionlevel")
	private int mMovesPerExpansionLevel = 2;

	
	
	public BreadCrumbExpansionSet(int pExpansionSetId, int pMetaDataCheckStartingExpansionLevel, int pMaximumExpansionLevel, int pMovesPerExpansionLevel) {
		mExpansionSetId = pExpansionSetId;
		mMetaDataCheckStartingExpansionLevel = pMetaDataCheckStartingExpansionLevel;
		mMaximumExpansionLevel = pMaximumExpansionLevel;
		mMovesPerExpansionLevel = pMovesPerExpansionLevel;
	}


	public void setExpansionSetId(int pExpansionSetId) {
		mExpansionSetId = pExpansionSetId;
	}

	
	@Override
	public int getExpansionSetId() {
		return mExpansionSetId;
	}

	@Override
	public void setMetaDataCheckStartingExpansionLevel(int pMetaDataCheckStartingExpansionLevel) {
		mMetaDataCheckStartingExpansionLevel = pMetaDataCheckStartingExpansionLevel;
	}

	@Override
	public int getMetaDataCheckStartingExpansionLevel() {
		return mMetaDataCheckStartingExpansionLevel;
	}

	@Override
	public void setMaximumExpansionLevel(int pMaximumExpansionLevel) {
		mMaximumExpansionLevel = pMaximumExpansionLevel;
		
	}

	@Override
	public int getMaximumExpansionLevel() {
		return mMaximumExpansionLevel;
	}

	@Override
	public void setMovesPerExpansionLevel(int pMovesPerExpansionLevel) {
		mMovesPerExpansionLevel = pMovesPerExpansionLevel;
	}

	@Override
	public int getMovesPerExpansionLevel() {
		return mMovesPerExpansionLevel;
	}

}
