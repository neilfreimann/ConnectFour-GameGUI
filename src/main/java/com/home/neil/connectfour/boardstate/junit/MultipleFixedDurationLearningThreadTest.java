package com.home.neil.connectfour.boardstate.junit;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.DataFormatException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.home.neil.connectfour.knowledgebase.KnowledgeBaseFilePool;
import com.home.neil.connectfour.learninggamethread.width.FixedDurationBestMoveBlockLearningThread;
import com.home.neil.connectfour.managers.appmanager.ApplicationManager;



public class MultipleFixedDurationLearningThreadTest {
	public static final String CLASS_NAME = MultipleFixedDurationLearningThreadTest.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));

	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ThreadContext.put("LogContext", "Test");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(timeout = 2140000000)
	public void testFixedDurationLinearDepthLearningThread() throws IOException, ConfigurationException {

		ThreadContext.put("LogContext", "TestThread");

		
//		FourForwardFixedDurationLinearWidthLearningThread lFourForwardFixedDurationLinearWidthLearningThread;
//		FixedDurationLinearWidthLearningThread lFixedDurationLinearWidthLearningThread;

		FixedDurationBestMoveBlockLearningThread lFixedDurationBestMoveBlockLearningThread;
//		FixedDurationEfficientBestMoveLinearDepthLearningThread lFixedDurationEfficientBestMoveLinearDepthLearningThread;
//		FixedDurationBestMoveLinearDepthLearningThread lFixedDurationBestMoveLinearDepthLearningThread;

//		ReverseFixedDurationLinearWidthLearningThread lReverseFixedDurationLinearWidthLearningThread;
//		FourForwardFixedDurationLinearDepthLearningThread lFourForwardFixedDurationLinearDepthLearningThread;
//		FourReverseFixedDurationLinearDepthLearningThread lFourReverseFixedDurationLinearDepthLearningThread;
//		OneForwardFixedDurationLinearDepthLearningThread lOneForwardFixedDurationLinearDepthLearningThread;
//		SevenReverseFixedDurationLinearDepthLearningThread lSevenReverseFixedDurationLinearDepthLearningThread;
//		ThreeForwardFixedDurationLinearDepthLearningThread lThreeForwardFixedDurationLinearDepthLearningThread;
//		ThreeReverseFixedDurationLinearDepthLearningThread lThreeReverseFixedDurationLinearDepthLearningThread;
//		FiveForwardFixedDurationLinearDepthLearningThread lFiveForwardFixedDurationLinearDepthLearningThread;
//		FiveReverseFixedDurationLinearDepthLearningThread lFiveReverseFixedDurationLinearDepthLearningThread;
		try {
			ApplicationManager lApplicationManager = ApplicationManager.getInstance();
			
			KnowledgeBaseFilePool lKnowledgeBaseFilePool = KnowledgeBaseFilePool.getMasterInstance();

//			lFourForwardFixedDurationLinearWidthLearningThread = FourForwardFixedDurationLinearWidthLearningThread.getInstance(0, null);
//			lFixedDurationLinearWidthLearningThread = FixedDurationLinearWidthLearningThread.getInstance(36000000, null);

			lFixedDurationBestMoveBlockLearningThread = FixedDurationBestMoveBlockLearningThread.getInstance(lKnowledgeBaseFilePool, 36000000, null);
//			lFixedDurationEfficientBestMoveLinearDepthLearningThread = FixedDurationEfficientBestMoveLinearDepthLearningThread.getInstance (lKnowledgeBaseFilePool, 36000000, null);
//			lFixedDurationBestMoveLinearDepthLearningThread = FixedDurationBestMoveLinearDepthLearningThread.getInstance(lKnowledgeBaseFilePool, 36000000, null);

//			lReverseFixedDurationLinearWidthLearningThread = ReverseFixedDurationLinearWidthLearningThread.getInstance(0, null);
//			lFourForwardFixedDurationLinearDepthLearningThread = FourForwardFixedDurationLinearDepthLearningThread.getInstance(3600000, null);
//			lFourReverseFixedDurationLinearDepthLearningThread = FourReverseFixedDurationLinearDepthLearningThread.getInstance(3600000, null);
//			lOneForwardFixedDurationLinearDepthLearningThread = OneForwardFixedDurationLinearDepthLearningThread.getInstance(0, null);
//			lSevenReverseFixedDurationLinearDepthLearningThread = SevenReverseFixedDurationLinearDepthLearningThread.getInstance(0, null);
//			lThreeForwardFixedDurationLinearDepthLearningThread = ThreeForwardFixedDurationLinearDepthLearningThread.getInstance(0, null);
//			lThreeReverseFixedDurationLinearDepthLearningThread = ThreeReverseFixedDurationLinearDepthLearningThread.getInstance(0, null);
//			lFiveForwardFixedDurationLinearDepthLearningThread = FiveForwardFixedDurationLinearDepthLearningThread.getInstance(0, null);
//			lFiveReverseFixedDurationLinearDepthLearningThread = FiveReverseFixedDurationLinearDepthLearningThread.getInstance(0, null);

		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		try {

//			lFourForwardFixedDurationLinearWidthLearningThread.start();
//			Thread.sleep(10000);
//			lReverseFixedDurationLinearWidthLearningThread.start();
//			Thread.sleep(10000);
//			lFixedDurationLinearWidthLearningThread.start();
//			Thread.sleep(10000);
			
			
			lFixedDurationBestMoveBlockLearningThread.start();
			Thread.sleep(10000);
//			lFixedDurationEfficientBestMoveLinearDepthLearningThread.start();
//			Thread.sleep(10000);

//			lFixedDurationBestMoveLinearDepthLearningThread.start();
//			Thread.sleep(10000);

			
			
//			lFourForwardFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(10000);
//			lFourReverseFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(10000);
//			lOneForwardFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(10000);
//			lSevenReverseFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(60000);
//			lThreeForwardFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(60000);
//			lThreeReverseFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(60000);
//			lFiveForwardFixedDurationLinearDepthLearningThread.start();
//			Thread.sleep(60000);
//			lFiveReverseFixedDurationLinearDepthLearningThread.start();
			 
//			lFourForwardFixedDurationLinearWidthLearningThread.join ();
			lFixedDurationBestMoveBlockLearningThread.join();
//			lFixedDurationEfficientBestMoveLinearDepthLearningThread.join();
//			lFixedDurationBestMoveLinearDepthLearningThread.join();
//			lReverseFixedDurationLinearWidthLearningThread.join();
//			lFourForwardFixedDurationLinearDepthLearningThread.join();
//			lFourReverseFixedDurationLinearDepthLearningThread.join();
//			lOneForwardFixedDurationLinearDepthLearningThread.join();
//			lSevenReverseFixedDurationLinearDepthLearningThread.join();
//			lThreeForwardFixedDurationLinearDepthLearningThread.join();
//			lThreeReverseFixedDurationLinearDepthLearningThread.join();
//			lFiveForwardFixedDurationLinearDepthLearningThread.join();
//			lFiveReverseFixedDurationLinearDepthLearningThread.join();


		} catch (InterruptedException e) {
			sLogger.info("Interrupted Exception Occurred!");
		}

		KnowledgeBaseFilePool lFilePool = KnowledgeBaseFilePool.getMasterInstance();

		try {
			try {
				lFilePool.cleanupAll();
			} catch (DataFormatException e) {
				// TODO Auto-generated catch block
				StringWriter lSW = new StringWriter();
				PrintWriter lPW = new PrintWriter(lSW);
				e.printStackTrace(lPW);
				lSW.toString(); // stack trace as a string
				sLogger.debug("StackTrace: " + lSW);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
