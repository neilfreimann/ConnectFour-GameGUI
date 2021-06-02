package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;
import com.home.neil.connectfour.boardstate.BoardStateBase;

public class BreadCrumbExpansionLevel implements IBreadCrumbExpansionLevel{
	public static final String CLASS_NAME = BreadCrumbExpansionLevel.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	@SerializedName(value="level")
	private int mExpansionLevel = 1;
	
	@SerializedName(value="currentnode")
	private BreadCrumbNode mCurrentExpansionNode = null; 
	
	@SerializedName(value="expandedsubnodes")
	private List <BreadCrumbNode> mExpandedSubNodes = null;
	
	public BreadCrumbExpansionLevel (int pExpansionLevel, BoardStateBase pCurrentBoardStateBase, 
			List<BoardStateBase> pBoardStateBases) {
		mExpansionLevel = pExpansionLevel;
		mCurrentExpansionNode = new BreadCrumbNode (pCurrentBoardStateBase);
		mExpandedSubNodes = new ArrayList<>();
		for (BoardStateBase lBoardStateBase : pBoardStateBases) {
			BreadCrumbNode lBreadCrumbNode = new BreadCrumbNode (lBoardStateBase);
			mExpandedSubNodes.add(lBreadCrumbNode);
		}
	}
	
	public void setExpansionLevel(int pExpansionLevel) {
		mExpansionLevel = pExpansionLevel;
	}

	public int getExpansionLevel() {
		return mExpansionLevel;
	}

	public void setCurrentExpansionNode(BreadCrumbNode pCurrentExpansionNode) {
		mCurrentExpansionNode = pCurrentExpansionNode;
	}
	
	public void setCurrentExpansionBoardStateBase (BoardStateBase pCurrentBoardStateBase) {
		mCurrentExpansionNode = new BreadCrumbNode (pCurrentBoardStateBase);
	}

	public BreadCrumbNode getCurrentExpansionNode() {
		return mCurrentExpansionNode;
	}

	public void setExpandedSubNodes(List<BreadCrumbNode> pExpandedSubNodes) {
		mExpandedSubNodes = pExpandedSubNodes;
	}

	public void setExpandedBoardStateBase(List<BoardStateBase> pBoardStateBases) {
		mExpandedSubNodes = new ArrayList<>();
		for (BoardStateBase lBoardStateBase : pBoardStateBases) {
			BreadCrumbNode lBreadCrumbNode = new BreadCrumbNode (lBoardStateBase);
			mExpandedSubNodes.add(lBreadCrumbNode);
		}
	}
	
	
	public List<BreadCrumbNode> getExpandedSubNodes() {
		return mExpandedSubNodes;
	}
	
	public void addExpandedSubNode (BoardStateBase pBoardStateBase) {
		mExpandedSubNodes.add (new BreadCrumbNode (pBoardStateBase));
	}

}
