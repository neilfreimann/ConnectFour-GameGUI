package com.home.neil.connectfour.boardstate.knowledgebase.locks;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.IFileIndex;
import com.home.neil.connectfour.boardstate.tasks.ExpansionTask;
import com.home.neil.task.BasicAppTask;

public class AddressLocks implements AddressLocksMBean {
	public static final String CLASS_NAME = AddressLocks.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + AddressLocks.class.getSimpleName();
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private HashMap<String, AddressLockHolderTask> mCurrentAddressLocks = null;
	private HashMap<String, LinkedList<AddressLockHolderTask>> mCurrentScoreAddressLockReservations = null;		

	private static AddressLocks sInstance = null;

	private String mCurrentAddressLock = null;
	
	private AddressLocks () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mCurrentAddressLocks = new HashMap<> ();
		mCurrentScoreAddressLockReservations = new HashMap<> ();

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
	}
	
	public static synchronized AddressLocks getInstance () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}		
		if (sInstance == null) {
			sInstance = new AddressLocks ();
			
			MBeanServer lMBS = ManagementFactory.getPlatformMBeanServer(); 
	        ObjectName lBeanName;
			try {
				lBeanName = new ObjectName(MBEAN_NAME);
				lMBS.registerMBean(sInstance, lBeanName);
			} catch (MalformedObjectNameException e2) {
				sLogger.error("Could not register the MBean");
				e2.printStackTrace();
			} catch (InstanceAlreadyExistsException e1) {
				sLogger.error("Could not register the MBean");
				e1.printStackTrace();
			} catch (MBeanRegistrationException e1) {
				sLogger.error("Could not register the MBean");
				e1.printStackTrace();
			} catch (NotCompliantMBeanException e1) {
				sLogger.error("Could not register the MBean");
				e1.printStackTrace();

			} 
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
		return sInstance;
	}
	
	
	public synchronized boolean reserveAddress (IFileIndex pFileIndex, AddressLockHolderTask pTask) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		//Reserve the ScoreAddress
		String lScoreAddress = pFileIndex.getAddress();
		//TODO  get rid of this
		if (pTask instanceof ExpansionTask) {
			ExpansionTask lTask = (ExpansionTask) pTask;
			mCurrentAddressLock = lTask.getBoardStateToExpand().constructMoveStrings(true)[0];
		}
		//TODO here
		String lReciprocalScoreAddress = pFileIndex.getReciprocalAddress();
		String lScoreAddressToUse;
		
		int lCompare = lScoreAddress.compareTo(lReciprocalScoreAddress); 
		if (lCompare < 0) {
			lScoreAddressToUse = lScoreAddress;
		} else {
			lScoreAddressToUse = lReciprocalScoreAddress;
		}
		
		AddressLockHolderTask lAddressLockHolderTask = mCurrentAddressLocks.get(lScoreAddress);
		if (lAddressLockHolderTask == null) {
			mCurrentAddressLocks.put(lScoreAddressToUse, pTask);
			pTask.setReservedAddress();

			sLogger.debug("State1: Lock Obtained: {{}} for Task {{}} for Thread {{}}", lScoreAddressToUse, pTask.getTaskName(), pTask.getTaskThreadName());
			
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
			return true;
		} else {
			LinkedList<AddressLockHolderTask> lWaitingTasks = mCurrentScoreAddressLockReservations.get(lScoreAddressToUse);
			if (lWaitingTasks == null) {
				lWaitingTasks = new LinkedList<> ();
				lWaitingTasks.add(pTask);
				mCurrentScoreAddressLockReservations.put(lScoreAddressToUse, lWaitingTasks);

				sLogger.debug("State2: Lock Reserved: {{}} for Task {{}} for Thread {{}}", lScoreAddressToUse, pTask.getTaskName(), pTask.getTaskThreadName());
				
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}		
				return false;
			} else {
				lWaitingTasks.add(pTask);
				sLogger.debug("State3: Lock Reserved: {{}} for Task {{}} for Thread {{}}", lScoreAddressToUse, pTask.getTaskName(), pTask.getTaskThreadName());
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}		
				return false;
			}
		}
	}
	
	public synchronized void releaseAddress (IFileIndex pFileIndex) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		//Release the ScoreAddress
		String lScoreAddress = pFileIndex.getAddress();
		String lReciprocalScoreAddress = pFileIndex.getReciprocalAddress();
		String lScoreAddressToUse;
		
		int lCompare = lScoreAddress.compareTo(lReciprocalScoreAddress); 
		if (lCompare < 0) {
			lScoreAddressToUse = lScoreAddress;
		} else {
			lScoreAddressToUse = lReciprocalScoreAddress;
		}
		
		AddressLockHolderTask lAddressLockHolderTask = mCurrentAddressLocks.remove(lScoreAddressToUse);
		LinkedList <AddressLockHolderTask> lWaitingThreads = mCurrentScoreAddressLockReservations.get(lScoreAddressToUse);
		if (lWaitingThreads == null || lWaitingThreads.isEmpty()) {
			sLogger.debug("State4: Lock Released: {{}} for Task {{}} for Thread {{}}", lScoreAddressToUse, lAddressLockHolderTask.getTaskName(), lAddressLockHolderTask.getTaskThreadName());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
			return;
		} else {
			AddressLockHolderTask lWaitingAddressLockHolderTask = lWaitingThreads.pop();
			mCurrentAddressLocks.put(lScoreAddressToUse, lWaitingAddressLockHolderTask);
			lWaitingAddressLockHolderTask.setReservedAddress();
			sLogger.debug("State5: Lock Reassigned: {{}} for Task {{}} for Thread {{}}", lScoreAddressToUse, lWaitingAddressLockHolderTask.getTaskName(), lWaitingAddressLockHolderTask.getTaskThreadName());
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
		}		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
	}


	public String getCurrentScoreAddressLocksSize() {
		return String.valueOf(mCurrentAddressLocks.size());
	}

	public String getCurrentScoreAddressLockReservationsSize() {
		return String.valueOf(mCurrentScoreAddressLockReservations.size());
	}

	@Override
	public String getCurrentAddressLock() {
		return mCurrentAddressLock;
	}

}
