package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import java.util.List;

public interface IBreadCrumbs {
	public void setCurrentExpansionSet (int pCurrentExpansionSet);
	public int getCurrentExpansionSet();

	public void setBreadCrumbExpansionSets (List <BreadCrumbExpansionSet> pBreadCrumbExpansionSet);
	public List <BreadCrumbExpansionSet> getBreadCrumbExpanionSets ();
	
	public void setBreadCrumbExpansionLevels (List <BreadCrumbExpansionLevel> pBreadCrumbExpansionLevels);
	public List <BreadCrumbExpansionLevel> getBreadCrumbExpansionLevels ();
}
