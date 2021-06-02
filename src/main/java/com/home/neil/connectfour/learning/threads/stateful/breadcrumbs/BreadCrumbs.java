package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;

public class BreadCrumbs implements IBreadCrumbs {
	public static final String CLASS_NAME = BreadCrumbExpansionLevel.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	@SerializedName(value="currentexpansionset")
	private int mCurrentExpansionSet = 1;

	@SerializedName(value="expansionsets")
	private List<BreadCrumbExpansionSet> mBreadCrumbExpansionSets = null;
	
	@SerializedName(value="expansionlevels")
	private List<BreadCrumbExpansionLevel> mBreadCrumbExpansionLevels = null;

	public BreadCrumbs (int pCurrentExpansionSet, List <BreadCrumbExpansionSet> pBreadCrumbExpansionSets,
			List <BreadCrumbExpansionLevel> pBreadCrumbExpansionLevels) {
		mCurrentExpansionSet = pCurrentExpansionSet;
		mBreadCrumbExpansionSets = pBreadCrumbExpansionSets;
		mBreadCrumbExpansionLevels = pBreadCrumbExpansionLevels;
	}
	
	public void setCurrentExpansionSet(int pCurrentExpansionSet) {
		mCurrentExpansionSet = pCurrentExpansionSet;
	}

	public int getCurrentExpansionSet() {
		return mCurrentExpansionSet;
	}

	public void setBreadCrumbExpansionSets(List<BreadCrumbExpansionSet> pBreadCrumbExpansionSet) {
		mBreadCrumbExpansionSets = pBreadCrumbExpansionSet;
	}

	@Override
	public List<BreadCrumbExpansionSet> getBreadCrumbExpanionSets() {
		return mBreadCrumbExpansionSets;
	}

	@Override
	public void setBreadCrumbExpansionLevels(List<BreadCrumbExpansionLevel> pBreadCrumbExpansionLevels) {
		mBreadCrumbExpansionLevels = pBreadCrumbExpansionLevels;
	}

	@Override
	public List<BreadCrumbExpansionLevel> getBreadCrumbExpansionLevels() {
		return mBreadCrumbExpansionLevels;
	}

	public BreadCrumbExpansionSet getCurrentBreadCrumbExpansionSet() {
		if (mCurrentExpansionSet < 0) {
			mCurrentExpansionSet = 0;
		} else if (mCurrentExpansionSet > mBreadCrumbExpansionSets.size()) {
			return null;
		}

		for (BreadCrumbExpansionSet lCurrentBreadCrumbExpansionSet : mBreadCrumbExpansionSets) {
			if (lCurrentBreadCrumbExpansionSet.getExpansionSetId() == mCurrentExpansionSet) {
				return lCurrentBreadCrumbExpansionSet;
			}
		}
		
		return null;
	}
	
	public int incCurrentExpansionSet () {
		return mCurrentExpansionSet++;
	}
	
	
}
