package com.home.neil.connectfour.controller;

import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class GameController implements ActionInterface {
	public static final String CLASS_NAME = GameController.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + GameController.class.getSimpleName();
	
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private boolean mHumanXCheatFlag = false;
	private boolean mHumanOCheatFlag = false;
	private boolean mComputerXCheatFlag = false;
	private boolean mComputerOCheatFlag = false;
	
	private long mMaximumMoveDuration = 0; // 0 is forever
	private long mGameStart = 0;
	private long mGameEnd = 0;

	private Game mGame = null;
	private Timer mMoveTimer = null;
	
	private Player mPlayerX = null;
	private Player mPlayerO = null;
	
	@Override
	public void undoMove(Player pPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resign(Player pPlayer) {
		// TODO Auto-generated method stub
		
	}

	public Game getGame() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mGame;
	}

	@Override
	public void submitMove(Player pPlayer, int pLMove) {
		// TODO Auto-generated method stub
		
	}
	
	
}
