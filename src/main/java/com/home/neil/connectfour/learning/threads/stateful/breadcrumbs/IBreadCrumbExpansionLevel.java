package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import java.util.List;

import com.home.neil.connectfour.boardstate.BoardStateBase;

public interface IBreadCrumbExpansionLevel {
	public void setExpansionLevel (int pExpansionLevel);
	public int getExpansionLevel ();
	
	public void setCurrentExpansionNode (BreadCrumbNode pCurrentExpansionNode);
	public void setCurrentExpansionBoardStateBase (BoardStateBase pCurrentBoardStateBase);
	public BreadCrumbNode getCurrentExpansionNode ();
	
	public void setExpandedSubNodes (List <BreadCrumbNode> pExpandedSubNodes);
	public void setExpandedBoardStateBase (List<BoardStateBase> pBoardStateBases);
	public List <BreadCrumbNode> getExpandedSubNodes ();
	
	
}
