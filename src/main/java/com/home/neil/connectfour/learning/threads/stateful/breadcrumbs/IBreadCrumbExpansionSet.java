package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

public interface IBreadCrumbExpansionSet {
	public void setExpansionSetId (int pExpansionSetId);
	public int getExpansionSetId ();
		
	public void setMetaDataCheckStartingExpansionLevel (int pMetaDataCheckStartingExpansionLevel);
	public int getMetaDataCheckStartingExpansionLevel();
	
	public void setMaximumExpansionLevel (int pMaximumExpansionLevel);
	public int getMaximumExpansionLevel ();
	
	public void setMovesPerExpansionLevel (int pMovesPerExpansionLevel);
	public int getMovesPerExpansionLevel ();
	
}
