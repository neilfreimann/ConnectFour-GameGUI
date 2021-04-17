package com.home.neil.connectfour.boardstate.junit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.home.neil.connectfour.boardstate.MovePosition;
import com.home.neil.connectfour.boardstate.WinningCombination;
import com.home.neil.junit.sandbox.SandboxTest;

class BoardStateTest extends SandboxTest {
	public static final String CLASS_NAME = BoardStateTest.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	
	
	@BeforeAll
	protected static void setUpBeforeClass() throws Exception {
		SandboxTest.setUpBeforeClass();
	}

	@AfterAll
	protected static void tearDownAfterClass() throws Exception {
		SandboxTest.tearDownAfterClass();
	}

	@BeforeEach
	protected void setUp() throws Exception {
		super.setUp();
	}

	@AfterEach
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	void testBoardStateInit() {
		String lCurrentMethodName = new Object(){}.getClass().getEnclosingMethod().getName();

		setTestPropertiesFileLocation(CLASS_NAME, lCurrentMethodName);
		
		WinningCombination.init();
		MovePosition.init();
		
		WinningCombination.setWinningCombinationMovePositions();
		
		assertNotNull(WinningCombination.getAllWinningCombinations());
		
		assertFalse(WinningCombination.getAllWinningCombinations().isEmpty());
	}

	
	
}