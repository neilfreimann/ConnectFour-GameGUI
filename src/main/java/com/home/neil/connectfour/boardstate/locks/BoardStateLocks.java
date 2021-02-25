package com.home.neil.connectfour.boardstate.locks;

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

import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.expansiontask.ExpansionTask;
import com.home.neil.connectfour.managers.appmanager.ApplicationPrecompilerSettings;



public class BoardStateLocks implements BoardStateLocksMBean {
	public static final String CLASS_NAME = BoardStateLocks.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + BoardStateLocks.class.getSimpleName();
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private HashMap<String, BoardState> mCurrentBoardStateLocks = null;
	private HashMap<String, LinkedList<ExpansionTask>> mCurrentBoardStateLockReservations = null;		

	private static BoardStateLocks sInstance = null;

	
	private BoardStateLocks () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		mCurrentBoardStateLocks = new HashMap<String, BoardState> ();
		mCurrentBoardStateLockReservations = new HashMap<String, LinkedList<ExpansionTask>> ();
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}
	
	public static synchronized BoardStateLocks getInstance () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (sInstance == null) {
			sInstance = new BoardStateLocks ();
			
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
			sLogger.trace("Exiting");
		}
		return sInstance;
	}
	
	
	public synchronized boolean reserveBoardState (BoardState pBoardStateToReserve, ExpansionTask lTask) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		//Reserve the BoardState
		String lParentMove = pBoardStateToReserve.getFileIndexString();
		String lParentMoveReciprocal = pBoardStateToReserve.getReciprocalFileIndexString();
		
		if (lParentMove.compareTo(lParentMoveReciprocal) == -1) {
			lParentMove = lParentMoveReciprocal;
		}
		
		BoardState lParentMoveLock = mCurrentBoardStateLocks.get(lParentMove);
		if (lParentMoveLock == null) {
			mCurrentBoardStateLocks.put(lParentMove, pBoardStateToReserve);
			lTask.setBoardStateLock(pBoardStateToReserve);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("State1: Lock Obtained: " + lParentMove + " for Thread: " + Thread.currentThread().getName());
			}
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return true;
		} else {
			LinkedList<ExpansionTask> lWaitingThreads = mCurrentBoardStateLockReservations.get(lParentMove);
			if (lWaitingThreads == null) {
				lWaitingThreads = new LinkedList<ExpansionTask> ();
				lWaitingThreads.add(lTask);
				mCurrentBoardStateLockReservations.put(lParentMove, lWaitingThreads);
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("State2: Lock Reserved: " + lParentMove + " for Thread: " + Thread.currentThread().getName());
				}
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return false;
			} else {
				lWaitingThreads.add(lTask);
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("State3: Lock Reserved: " + lParentMove + " for Thread: " + Thread.currentThread().getName());
				}
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace("Exiting");
				}
				return false;
			}
		}
	}
	
	public synchronized void releaseBoardState (BoardState pBoardStateToRelease) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}

		//Reserve the BoardState
		String lParentMove = pBoardStateToRelease.getFileIndexString();
		String lParentMoveReciprocal = pBoardStateToRelease.getReciprocalFileIndexString();
		
		if (lParentMove.compareTo(lParentMoveReciprocal) == -1) {
			lParentMove = lParentMoveReciprocal;
		}
		
		
		LinkedList <ExpansionTask> lWaitingThreads = mCurrentBoardStateLockReservations.get(lParentMove);
		if (lWaitingThreads == null) {
			mCurrentBoardStateLocks.remove(lParentMove);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("State4: Lock Released: " + lParentMove);
			}
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace("Exiting");
			}
			return;
		} else if (lWaitingThreads.isEmpty()) {
			mCurrentBoardStateLockReservations.remove(lParentMove);
			mCurrentBoardStateLocks.remove(lParentMove);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("State5: Lock Released: " + lParentMove);
			}
		} else {
			ExpansionTask lWaitingTask = lWaitingThreads.pop();
			lWaitingTask.setBoardStateLock(pBoardStateToRelease);
			//lWaitingTask.interrupt();
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("State6: Lock Released: " + lParentMove + " and Obtained to Thread: " + lWaitingTask.getName());
			}
		}		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
	}


	public String getCurrentBoardStateLocksSize() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return String.valueOf(mCurrentBoardStateLocks.size());
	}

	public String getCurrentBoardStateLockReservationsSize() {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Entering");
		}
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace("Exiting");
		}
		return String.valueOf(mCurrentBoardStateLockReservations.size());
	}

}
