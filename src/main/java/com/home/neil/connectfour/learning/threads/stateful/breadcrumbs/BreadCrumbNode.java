package com.home.neil.connectfour.learning.threads.stateful.breadcrumbs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;
import com.home.neil.connectfour.boardstate.BoardStateBase;

public class BreadCrumbNode implements IBreadCrumbNode {
	public static final String CLASS_NAME = BreadCrumbNode.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	@SerializedName(value="node")
	private String mNode = null;
	
	public BreadCrumbNode (BoardStateBase pBoardStateBase) {
		mNode = pBoardStateBase.getCurrentMoveStrings()[0];
	}
	
	public void setNode(String pNode) {
		mNode=pNode;
	}
	
	public String getNode() {
		return mNode;
	}
	
	
	
}
