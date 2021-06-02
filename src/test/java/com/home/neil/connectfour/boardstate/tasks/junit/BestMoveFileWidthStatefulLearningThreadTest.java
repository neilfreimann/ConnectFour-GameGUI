package com.home.neil.connectfour.boardstate.tasks.junit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
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
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.learning.threads.width.BestMoveFileWidthStatefulLearningThread;
import com.home.neil.junit.sandbox.SandboxTest;
import com.home.neil.pool.IPoolConfig;
import com.home.neil.pool.Pool;
import com.home.neil.pool.PoolException;
import com.home.neil.task.TaskException;

class BestMoveFileWidthStatefulLearningThreadTest extends SandboxTest {
	public static final String CLASS_NAME = BestMoveFileWidthStatefulLearningThreadTest.class.getName();
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
		//super.setUp();
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
	void testBestMoveFileWidthStatefulLearningThread() {
		String lCurrentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();

		IPoolConfig lPoolConfig = component_readConfig(lCurrentMethodName);

		Pool lPool = component_instantiatePool(lCurrentMethodName, lPoolConfig);

		component_initPool(lCurrentMethodName, lPool);

		Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> lPool.checkRetiringThreadsAllRunning());


		BestMoveFileWidthStatefulLearningThread lBestMoveFileWidthStatefulLearningThread = new BestMoveFileWidthStatefulLearningThread (lPool);

		lBestMoveFileWidthStatefulLearningThread.start();
		try {
			lBestMoveFileWidthStatefulLearningThread.join();
		} catch (InterruptedException e) {
		}
		
		component_retirePool(lCurrentMethodName, lPool);

		Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> lPool.checkRetiringThreadsAllStopped());

	}

}