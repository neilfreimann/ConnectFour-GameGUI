package com.home.neil.connectfour.boardstate.knowledgebase.locks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.IFileIndex;
import com.home.neil.connectfour.boardstate.tasks.ExpansionTask;
import com.home.neil.pool.Pool;
import com.home.neil.task.BasicAppTask;

public abstract  class AddressLockHolderTask extends BasicAppTask implements IAddressLockHolder {

	public static final String CLASS_NAME = ExpansionTask.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected Pool mPool = null;
	
	private boolean mAddressLock = false;
	private Object mLock = new Object();

	protected AddressLockHolderTask(Pool pPool, boolean pRecordContext, boolean pRecordTaskStatistics) {
		super(pRecordContext, pRecordTaskStatistics);
		mPool = pPool;
	}

	@Override
	public void setReservedAddress() {
		mAddressLock = true;
		synchronized (mLock) {
			mLock.notifyAll();
		}
	}

	
	protected boolean reserveAddress(IFileIndex pFileIndexToLock) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		AddressLocks lAddressLocks = AddressLocks.getInstance();

		sLogger.debug("Reserving Address {{}} {{}}", pFileIndexToLock.getAddress(),
				pFileIndexToLock.getReciprocalAddress());

		try {
			mAddressLock = lAddressLocks.reserveAddress(pFileIndexToLock, this);
		} catch (Exception e1) {
			sLogger.error("Failed to reserve Address {{}} {{}}", pFileIndexToLock.getAddress(),
					pFileIndexToLock.getReciprocalAddress());

			return false;
		}

		sLogger.debug("Waiting Address {{}} {{}}", pFileIndexToLock.getAddress(),
				pFileIndexToLock.getReciprocalAddress());
		synchronized (mLock) {
			while (!mAddressLock) {
				try {
					mLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					sLogger.error("Thread interrupted", e);
				}
			}
		}
		sLogger.debug("Acquired Address {{}} {{}}", pFileIndexToLock.getAddress(),
				pFileIndexToLock.getReciprocalAddress());

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		return true;
	}

	protected void releaseAddress(IFileIndex pFileIndexToUnlock) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		AddressLocks lAddressLocks = AddressLocks.getInstance();

		lAddressLocks.releaseAddress(pFileIndexToUnlock);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
}
