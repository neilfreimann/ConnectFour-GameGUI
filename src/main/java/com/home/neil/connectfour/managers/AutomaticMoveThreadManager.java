package com.home.neil.connectfour.managers;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.appconfig.Connect4PropertiesConfiguration;
import com.home.neil.connectfour.gamethreads.AutomaticMoveThread;



public class AutomaticMoveThreadManager implements AutomaticMoveThreadManagerMBean {
	public static final String CLASS_NAME = AutomaticMoveThreadManager.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + AutomaticMoveThreadManager.class.getSimpleName();

	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private final static String WRANGLER_TIMED_TASK_TIME_CONFIGKEY = CLASS_NAME + ".wranglertask.timeinms";
	
	private static AutomaticMoveThreadManager sInstance = null;
	
	private boolean mTerminate = false;
	
	private ServerSocket mServerSocket = null;
	
	private Timer mTimer = null;
	
	private HashMap <String, AutomaticMoveThread> mThreadsToManage = null;	
	
	private AutomaticMoveThreadManager () throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		mThreadsToManage = new HashMap <String, AutomaticMoveThread> ();
		
		Connect4PropertiesConfiguration lConfig;
		try {
			lConfig = Connect4PropertiesConfiguration.getInstance();
		} catch (ConfigurationException e1) {
			sLogger.error("Could not initialize the AutomaticMoveThreadManager Thread as could not read Configuration.");
			sLogger.trace("Exiting");
			throw e1;
		}
		sLogger.info("Configuration obtained.");
		
		long lWranglerTimer = 0;
		try {
			lWranglerTimer = lConfig.getLong(WRANGLER_TIMED_TASK_TIME_CONFIGKEY);
		} catch (NoSuchElementException eNSEE) {
			sLogger.error("Element " + WRANGLER_TIMED_TASK_TIME_CONFIGKEY + " Not found in Connect4 Configuration.");
			sLogger.trace("Exiting");
			throw new ConfigurationException();
		} catch (ConversionException  eCE) {
			sLogger.error("Element " + WRANGLER_TIMED_TASK_TIME_CONFIGKEY + " has a Conversion Exception in Connect4 Configuration.");
			sLogger.trace("Exiting");
			throw new ConfigurationException();
		}
		
		mTimer = new Timer ();
		mTimer.schedule(new AutomaticMoveThreadWrangler(), 0, lWranglerTimer);
		
		sLogger.trace("Exiting");
	}
	
	public synchronized static AutomaticMoveThreadManager getInstance() throws ConfigurationException, IOException {
		sLogger.trace("Entering");

		if (sInstance == null) {
			sInstance = new AutomaticMoveThreadManager();

			MBeanServer lMBS = ManagementFactory.getPlatformMBeanServer(); 
	        ObjectName lBeanName;
			try {
				lBeanName = new ObjectName(MBEAN_NAME);
				lMBS.registerMBean(sInstance, lBeanName);
			} catch (MalformedObjectNameException e2) {
				sLogger.error("Could not register the MBean");
			} catch (InstanceAlreadyExistsException e1) {
				sLogger.error("Could not register the MBean");
			} catch (MBeanRegistrationException e1) {
				sLogger.error("Could not register the MBean");
			} catch (NotCompliantMBeanException e1) {
				sLogger.error("Could not register the MBean");
			} 
			
		}
		
		sLogger.trace("Exiting");
		return sInstance;
	}
	
	public synchronized void registerAutomaticMoveThread (AutomaticMoveThread pAutomaticMoveThread, String pBeanName) {
		sLogger.trace("Entering");
		
		sLogger.info ("YO " + pAutomaticMoveThread.getName() + " Bitch!  You'ze my bitch now!");

		mThreadsToManage.put(pBeanName, pAutomaticMoveThread);
		
		
		sLogger.info ("YO " + pAutomaticMoveThread.getName() + " Bitch!  I'm gonna tag ya!");
		
		MBeanServer lMBS = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName lBeanName;
		try {
			lBeanName = new ObjectName(pBeanName);
			lMBS.registerMBean(pAutomaticMoveThread, lBeanName);
		} catch (MalformedObjectNameException e2) {
			sLogger.error("Could not register the MBean");
			return;
		} catch (InstanceAlreadyExistsException e1) {
			sLogger.error("Could not register the MBean");
			return;
		} catch (MBeanRegistrationException e1) {
			sLogger.error("Could not register the MBean");
			return;
		} catch (NotCompliantMBeanException e1) {
			sLogger.error("Could not register the MBean");
			return;
		} 
		
		sLogger.trace("Exiting");
	}


	public synchronized void deregisterAutomaticMoveThread (String pAutomaticMoveThreadMBean) {
		sLogger.trace("Entering");
		
		AutomaticMoveThread lAutomaticThread = mThreadsToManage.remove(pAutomaticMoveThreadMBean);
		
		lAutomaticThread.setTerminate();
		
		sLogger.info ("YO " + lAutomaticThread.getName() + " Bitch!  Get da fuck outta here!");

		MBeanServer lMBS = ManagementFactory.getPlatformMBeanServer(); 
        ObjectName lBeanName;
		try {
			lBeanName = new ObjectName(pAutomaticMoveThreadMBean);
			lMBS.unregisterMBean(lBeanName);
		} catch (MalformedObjectNameException e2) {
			sLogger.error("Could not register the MBean");
			return;
		} catch (MBeanRegistrationException e1) {
			sLogger.error("Could not register the MBean");
			return;
		} catch (InstanceNotFoundException e) {
			sLogger.error("Could not register the MBean");
			return;
		} 
		
		sLogger.trace("Exiting");
	}
	
	
	public synchronized void deregisterAutomaticMoveThread (AutomaticMoveThread pAutomaticMoveThread) {
		sLogger.trace("Entering");
		deregisterAutomaticMoveThread (pAutomaticMoveThread.getBeanName());
		sLogger.trace("Exiting");
	}
	

	public synchronized void bitchSlapAllThreads () {
		sLogger.trace("Entering");
		
		sLogger.info("Setting the Termination Flag.");
		mTerminate = true;

		sLogger.info("Stop the Wrangler.");
		mTimer.cancel();
		
		sLogger.info("Closing Socket Server.");
		try {
			if (mServerSocket != null) {
				mServerSocket.close();
			}
		} catch (IOException e) {

		}
		
		sLogger.info("Bitch Slapping the Threads.");
		for (Iterator <String> lIterator = mThreadsToManage.keySet().iterator(); lIterator.hasNext(); ) {
			String lCurrentThreadMBean = lIterator.next();
			AutomaticMoveThread lCurrentThread = mThreadsToManage.get(lCurrentThreadMBean);
			sLogger.info ("YO " + lCurrentThread.getName() + " Bitch! Stop dat SHIT!");
			deregisterAutomaticMoveThread(lCurrentThreadMBean);
		}
		
		sLogger.trace("Exiting");
	}
	

	
	public synchronized void checkAllThreads () {
		sLogger.trace("Entering");

		Vector <String> lThreadsToDeregister = new Vector <String> ();
		
		for (Iterator <String> lIterator = mThreadsToManage.keySet().iterator(); lIterator.hasNext(); ) {
			String lCurrentThreadMBean = lIterator.next();
			AutomaticMoveThread lCurrentThread = mThreadsToManage.get(lCurrentThreadMBean);

			sLogger.info ("YO " + lCurrentThread.getName() + " Bitch! Are you taking care of shit?");
			
			State lThreadState = lCurrentThread.getState();
			
			if (lThreadState == Thread.State.TERMINATED) {
				sLogger.warn ("YO " + lCurrentThread.getName() + " Oh you dead?  Okay I gotta find me otha bitches.");
				
				lThreadsToDeregister.add(lCurrentThreadMBean);
			}
		}

		for (Iterator <String> lIterator = lThreadsToDeregister.iterator(); lIterator.hasNext(); ) {
			String lCurrentThreadMBean = lIterator.next();
			deregisterAutomaticMoveThread(lCurrentThreadMBean);
		}
		
		sLogger.trace("Exiting");
	}

	public class AutomaticMoveThreadWrangler extends TimerTask {
		public void run() {
			sLogger.trace("Entering");
			sLogger.info("Gotta check myz bitches!");
			checkAllThreads();
			sLogger.trace("Exiting");
		}
	};
	
	public void setTerminate() {
		sLogger.trace("Entering");

		bitchSlapAllThreads();
		
		sLogger.trace("Exiting");
	}


	public boolean getTerminate() {
		sLogger.trace("Entering");
		sLogger.trace("Exiting");
		return mTerminate;
	}
}
