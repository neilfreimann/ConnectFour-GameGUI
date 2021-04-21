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

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.connectfour.boardstate.knowledgebase.BoardStateKnowledgeBaseFileIndex;
import com.home.neil.connectfour.boardstate.tasks.ExpansionTask;

public class BoardStateLocks implements BoardStateLocksMBean {
	public static final String CLASS_NAME = BoardStateLocks.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final String MBEAN_NAME = PACKAGE_NAME + ":type=" + BoardStateLocks.class.getSimpleName();
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private HashMap<String, BoardState> mCurrentBoardStateLocks = null;
	private HashMap<String, LinkedList<ExpansionTask>> mCurrentBoardStateLockReservations = null;		

	private static BoardStateLocks sInstance = null;

	
	private BoardStateLocks () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mCurrentBoardStateLocks = new HashMap<> ();
		mCurrentBoardStateLockReservations = new HashMap<> ();

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
	}
	
	public static synchronized BoardStateLocks getInstance () {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
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
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
		return sInstance;
	}
	
	
	public synchronized boolean reserveBoardState (BoardState pBoardState, ExpansionTask pTask) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		//Reserve the BoardState
		String lParentMove = pBoardStateToReserve;
		
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
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
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
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}		
				return false;
			} else {
				lWaitingThreads.add(lTask);
				if (sLogger.isDebugEnabled()) {
					sLogger.debug("State3: Lock Reserved: " + lParentMove + " for Thread: " + Thread.currentThread().getName());
				}
				if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
					sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
				}		
				return false;
			}
		}
	}
	
	public synchronized void releaseBoardState (BoardState pBoardStateToRelease) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		//Reserve the BoardState
		String lParentMove = pBoardStateToRelease.getFileIndexString();
		
		LinkedList <ExpansionTask> lWaitingThreads = mCurrentBoardStateLockReservations.get(lParentMove);
		if (lWaitingThreads == null) {
			mCurrentBoardStateLocks.remove(lParentMove);
			if (sLogger.isDebugEnabled()) {
				sLogger.debug("State4: Lock Released: " + lParentMove);
			}
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
			return;
		} else if (lWaitingThreads.isEmpty()) {
			mCurrentBoardStateLockReservations.remove(lParentMove);
			mCurrentBoardStateLocks.remove(lParentMove);
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
		} else {
			ExpansionTask lWaitingTask = lWaitingThreads.pop();
			lWaitingTask.setBoardStateLock(pBoardStateToRelease);
			//lWaitingTask.interrupt();
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}		
		}		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}		
	}


	public String getCurrentBoardStateLocksSize() {
		return String.valueOf(mCurrentBoardStateLocks.size());
	}

	public String getCurrentBoardStateLockReservationsSize() {
		return String.valueOf(mCurrentBoardStateLockReservations.size());
	}

}
