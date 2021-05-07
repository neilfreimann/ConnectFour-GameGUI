package com.home.neil.connectfour.boardstate.tasks.junit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.GameStateSet;
import com.home.neil.connectfour.boardstate.tasks.BoardStateMetaDataTask;
import com.home.neil.connectfour.boardstate.tasks.ExpansionTask;
import com.home.neil.connectfour.boardstate.tasks.BoardStateMetaDataTask.METADATAOPERATION;
import com.home.neil.junit.sandbox.SandboxTest;
import com.home.neil.pool.IPoolConfig;
import com.home.neil.pool.Pool;
import com.home.neil.pool.PoolException;
import com.home.neil.task.TaskException;

class ExpansionTaskTest extends SandboxTest {
	public static final String CLASS_NAME = ExpansionTaskTest.class.getName();
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

	public IPoolConfig component_readConfig(String pCurrentMethodName) {

		sLogger.info("#################################### Start Read Config" + pCurrentMethodName + " ####################################");

		setTestPropertiesFileLocation(CLASS_NAME, pCurrentMethodName);

		IPoolConfig lPoolConfig = null;

		try {
			lPoolConfig = AppConfig.bind(IPoolConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sLogger.info("Unable to Instantiate CompressableCacheSegmentConfig");
			e.printStackTrace();
			assertTrue(false);
		}

		assertNotNull(lPoolConfig);

		sLogger.info("#################################### End Read Config" + pCurrentMethodName + " ####################################");

		return lPoolConfig;
	}

	public Pool component_instantiatePool(String pCurrentMethodName, IPoolConfig pConfig) {

		sLogger.info("#################################### Start Instantiate Pool" + pCurrentMethodName + " ####################################");

		Pool lPool = null;
		try {
			lPool = new Pool(pConfig);
		} catch (PoolException e) {
			sLogger.info("Unable to Instantiate Pool");
			e.printStackTrace();
			assertTrue(false);
		}

		sLogger.info("####################################   End Instantiate Pool" + pCurrentMethodName + " ####################################");
		return lPool;
	}

	public void component_initPool(String pCurrentMethodName, Pool pPool) {

		sLogger.info("#################################### Start Init Pool" + pCurrentMethodName + " ####################################");

		try {
			pPool.init();
		} catch (PoolException e) {
			sLogger.info("Unable to Init Pool");
			e.printStackTrace();
			assertTrue(false);
		}

		sLogger.info("####################################   End Init Pool" + pCurrentMethodName + " ####################################");
	}

	public void component_retirePool(String pCurrentMethodName, Pool pPool) {

		sLogger.info("#################################### Start Retire Pool" + pCurrentMethodName + " ####################################");

		try {
			pPool.retire();
		} catch (PoolException e) {
			sLogger.info("Unable to Retire Pool");
			e.printStackTrace();
			assertTrue(false);
		}

		sLogger.info("####################################   End Retire Pool" + pCurrentMethodName + " ####################################");
	}

	@Test
	void testExpansionTask() {
		String lCurrentMethodName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		IPoolConfig lPoolConfig = component_readConfig(lCurrentMethodName);

		Pool lPool = component_instantiatePool(lCurrentMethodName, lPoolConfig);

		component_initPool(lCurrentMethodName, lPool);

		Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> lPool.checkRetiringThreadsAllRunning());


		try {
			expandBoardState(lPool, null, 1, 1, 4);
			
			expandBoardState(lPool, null, 1, 5, 9);

			//expandBoardState(lPool, null, 1, 9, 13);

			//expandBoardState(lPool, null, 1, 1, 21);

			//expandBoardState(lPool, null, 1, 1, 21);

		} catch (TaskException e) {
			e.printStackTrace();
			assertTrue(false);
		}

		component_retirePool(lCurrentMethodName, lPool);

		Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> lPool.checkRetiringThreadsAllStopped());

	}

	public void expandBoardState(Pool lPool, BoardState lBoardStateToExpand, int pLevel, int pStartCheckingLevel, int pMaxLevel) throws TaskException {
		ExpansionTask lExpansionTask = null;

		if (lBoardStateToExpand == null) {
			lBoardStateToExpand = new BoardState ();
		}

		if (pLevel > pMaxLevel) {
			//sLogger.info("Reached Maximum Expansion Level: {{}} for Move: {{}}", pLevel, lBoardStateToExpand.constructMoveStrings(true)[0]);
			return;
		}
		
		if (lBoardStateToExpand.decodeGameState() == GameStateSet.UNDECIDED) {
			BoardStateMetaDataTask lReadMetaDataTask = null;
			if (pLevel >= pStartCheckingLevel) {
				lReadMetaDataTask = new BoardStateMetaDataTask(lPool, lBoardStateToExpand, METADATAOPERATION.READ, 0);
				lReadMetaDataTask.runTask();
			}

			if (lReadMetaDataTask == null || !lReadMetaDataTask.isValue()) {
				//sLogger.info("Move Not Evaluated: {{}}", lBoardStateToExpand.constructMoveStrings(true)[0]);
				lExpansionTask = new ExpansionTask(lPool, lBoardStateToExpand, false, true, 2);
				lExpansionTask.runTask();

				List<BoardState> lExpandedBoardStates = lExpansionTask.getExpandedBoardStates();

				for (BoardState lBoardState : lExpandedBoardStates) {
					expandBoardState(lPool, lBoardState, pLevel + 1, pStartCheckingLevel, pMaxLevel);
				} 
				
				if (lReadMetaDataTask != null) {
					BoardStateMetaDataTask lWriteMetaDataTask = new BoardStateMetaDataTask(lReadMetaDataTask, METADATAOPERATION.SET);
					lWriteMetaDataTask.runTask();
				}
			} else {
				sLogger.info("Move Already Evaluated: {{}}", lBoardStateToExpand.constructMoveStrings(true)[0]);
			}
		}
	}		
}