package com.home.neil.connectfour.boardstate.tasks.performance;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.BitSet;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsReadTask;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsWriteTask;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.connectfour.boardstate.IBoardState;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.metadata.BoardStateMetaDataKnowledgeBaseFileIndex;
import com.home.neil.connectfour.boardstate.knowledgebase.locks.AddressLockHolderTask;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;
import com.home.neil.pool.Pool;
import com.home.neil.task.TaskException;
import com.home.neil.workentity.WorkEntityException;

public class BoardStateMetaDataPerformanceTask extends AddressLockHolderTask {
	public static final String CLASS_NAME = BoardStateMetaDataPerformanceTask.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	protected static KnowledgeBaseCompressableCacheSegmentConfig sKnowledgeBaseConfig = null;
	protected static BoardStateMetaDataPerformanceTaskConfig sBoardStateMetaDataTaskConfig = null;
	protected static BoardStateMetaDataPerformanceTaskCacheConfig sBoardStateMetaDataTaskCacheConfig = null;
	
	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
			sKnowledgeBaseConfig = AppConfig.bind(KnowledgeBaseCompressableCacheSegmentConfig.class);
			sBoardStateMetaDataTaskConfig = AppConfig.bind(BoardStateMetaDataPerformanceTaskConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}

		sBoardStateMetaDataTaskCacheConfig = new BoardStateMetaDataPerformanceTaskCacheConfig(sBoardStateMetaDataTaskConfig);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}
	
	private IBoardState mBoardState = null;
	private METADATAOPERATION mMetaDataOperation;
	private int mMetaDataBitLocation;
	private boolean mNewValue;
	private BoardStateMetaDataKnowledgeBaseFileIndex mFileIndex = null;

	private boolean mValue;
	private BitSet mMetaData;
	private boolean mReciprocalValue;
	private BitSet mReciprocalMetaData;

	
	public enum METADATAOPERATION {
		READ, SET, CLEAR
	}

	
	public BoardStateMetaDataPerformanceTask (BoardStateMetaDataPerformanceTask pBoardStateMetaDataTask, METADATAOPERATION pMetaDataOperation) throws TaskException {
		super(pBoardStateMetaDataTask.mPool, sBoardStateMetaDataTaskCacheConfig);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		

		mBoardState = pBoardStateMetaDataTask.mBoardState;
		mMetaDataBitLocation = pBoardStateMetaDataTask.mMetaDataBitLocation;
		mFileIndex = pBoardStateMetaDataTask.mFileIndex;
		
		mMetaDataOperation = pMetaDataOperation;
		
		if (mMetaDataOperation == METADATAOPERATION.SET) {
			mNewValue = true;
		} else {
			mNewValue = false;
		}		
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	}
	
	public BoardStateMetaDataPerformanceTask (Pool pPool, IBoardState pBoardState, METADATAOPERATION pMetaDataOperation, int pMetaDataBitLocation) throws TaskException {
		super(pPool, sBoardStateMetaDataTaskCacheConfig);

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mBoardState = pBoardState;
		mMetaDataOperation = pMetaDataOperation;
		
		if (mMetaDataOperation == METADATAOPERATION.SET) {
			mNewValue = true;
		} else {
			mNewValue = false;
		}
		
		mMetaDataBitLocation = pMetaDataBitLocation;
		if (mMetaDataBitLocation < 0 || mMetaDataBitLocation >= (sKnowledgeBaseConfig.getMetaDataSizeInBytesPerDecision() * 8)) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException ("MetaDataBitLocation is invalid!");
		}

		try {
			mFileIndex = new BoardStateMetaDataKnowledgeBaseFileIndex(mBoardState);
		} catch (FileIndexException e) {
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}

	}
	
	

	@Override
	protected void executeTask() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		String lCurrentBoardStateMoveString = "";
		lCurrentBoardStateMoveString = mBoardState.getCurrentMoveStrings()[0];
		
		sLogger.debug("Reserving the Current BoardState: {{}}", lCurrentBoardStateMoveString);
		boolean lStateReserved = reserveAddress(mFileIndex);
		if (!lStateReserved) {
			sLogger.error("COULD NOT GET THE BOARDSTATE RESERVED!");
			mTaskSuccessful = false;
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Unable to reserve the boardstate");
		}
		sLogger.debug("Current BoardState is reserved: {{}}", lCurrentBoardStateMoveString);
		
		switch (mMetaDataOperation) {
		case SET:
			executeSetOperation();
			break;
		case CLEAR:
			executeClearOperation();
			break;
		case READ:
			executeReadOperation();
		default:
			break;
		}
		
		
		
		sLogger.debug("Releasing the Current BoardState: {{}}", lCurrentBoardStateMoveString);
		releaseAddress(mFileIndex);
		sLogger.debug("Current BoardState is Released: {{}}", lCurrentBoardStateMoveString);
		
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	
	
	
	private void executeSetOperation () throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		writeMetaDataToKnowledgeBase();
		writeReciprocalMetaDataToKnowledgeBase();
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	
	private void executeClearOperation () throws TaskException  {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		writeMetaDataToKnowledgeBase();
		writeReciprocalMetaDataToKnowledgeBase();
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
	}
	
	private void executeReadOperation () throws TaskException  {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		readMetaDataFromKnowledgeBase();
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
		
		
	}
	
	private void readMetaDataFromKnowledgeBase() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for reading");
		CompressableCacheSegmentOperationsReadTask lCompressableCacheSegmentOperationsReadTask = new CompressableCacheSegmentOperationsReadTask(mPool,
				mFileIndex.getPoolItemId(), mFileIndex.getFileDetails(),
				mFileIndex.getIndexDetails());
		try {
			lCompressableCacheSegmentOperationsReadTask.runTask();
		} catch (WorkEntityException e) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}

		if (!lCompressableCacheSegmentOperationsReadTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsReadTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			
			throw new TaskException("Knowledge Base Error occurred!");
		}

		byte[] lSegment = lCompressableCacheSegmentOperationsReadTask.getIndexDetails().getIndexSegment();
		if (lSegment == null || lSegment.length == 0) {
			sLogger.error("Knowledge Base Error occurred! Score Segment is null or empty!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred! Score Segment is null or empty!!");
		}

		mMetaData = BitSet.valueOf(lSegment);
		
		mValue = mMetaData.get(mMetaDataBitLocation);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	private void writeMetaDataToKnowledgeBase() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		readMetaDataFromKnowledgeBase();

		mMetaData.set(mMetaDataBitLocation, mNewValue);
				
		byte [] lMetaDataArray = mMetaData.toByteArray();
		if (lMetaDataArray.length == 0) {
			lMetaDataArray = new byte[] {0};
		}
		
		mFileIndex.getIndexDetails().setIndexSegment(lMetaDataArray);
		
		sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");

		CompressableCacheSegmentOperationsWriteTask lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
				mFileIndex.getPoolItemId(), mFileIndex.getFileDetails(),
				mFileIndex.getIndexDetails());
		try {
			lCompressableCacheSegmentOperationsWriteTask.runTask();
		} catch (WorkEntityException e) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}
		if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred!");
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	private void readReciprocalMetaDataFromKnowledgeBase() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}

		sLogger.debug("Creating and starting Knowledge Base File Access Thread for reading");
		CompressableCacheSegmentOperationsReadTask lCompressableCacheSegmentOperationsReadTask = new CompressableCacheSegmentOperationsReadTask(mPool,
				mFileIndex.getReciprocalPoolItemId(), mFileIndex.getReciprocalFileDetails(),
				mFileIndex.getReciprocalIndexDetails());
		try {
			lCompressableCacheSegmentOperationsReadTask.runTask();
		} catch (WorkEntityException e) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}

		if (!lCompressableCacheSegmentOperationsReadTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsReadTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			
			throw new TaskException("Knowledge Base Error occurred!");
		}

		byte[] lSegment = lCompressableCacheSegmentOperationsReadTask.getIndexDetails().getIndexSegment();
		if (lSegment == null || lSegment.length == 0) {
			sLogger.error("Knowledge Base Error occurred! Score Segment is null or empty!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred! Score Segment is null or empty!!");
		}

		mReciprocalMetaData = BitSet.valueOf(lSegment);
		
		mReciprocalValue = mReciprocalMetaData.get(mMetaDataBitLocation);
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	private void writeReciprocalMetaDataToKnowledgeBase() throws TaskException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		readReciprocalMetaDataFromKnowledgeBase();

		mReciprocalMetaData.set(mMetaDataBitLocation, mNewValue);
				
		byte [] lReciprocalMetaDataArray = mReciprocalMetaData.toByteArray();
		if (lReciprocalMetaDataArray.length == 0) {
			lReciprocalMetaDataArray = new byte[] {0};
		}
		
		mFileIndex.getReciprocalIndexDetails().setIndexSegment(lReciprocalMetaDataArray);
		
		sLogger.debug("Creating and starting Knowledge Base File Access Thread for writing");

		CompressableCacheSegmentOperationsWriteTask lCompressableCacheSegmentOperationsWriteTask = new CompressableCacheSegmentOperationsWriteTask(mPool,
				mFileIndex.getReciprocalPoolItemId(), mFileIndex.getReciprocalFileDetails(),
				mFileIndex.getReciprocalIndexDetails());
		try {
			lCompressableCacheSegmentOperationsWriteTask.runTask();
		} catch (WorkEntityException e) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException(e);
		}
		if (!lCompressableCacheSegmentOperationsWriteTask.isTaskSuccessful() || !lCompressableCacheSegmentOperationsWriteTask.isTaskFinished()) {
			sLogger.error("Knowledge Base Error occurred!");
			if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
				sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
			}
			throw new TaskException("Knowledge Base Error occurred!");
		}

		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	
	
	public IBoardState getBoardState() {
		return mBoardState;
	}

	public METADATAOPERATION getMetaDataOperation() {
		return mMetaDataOperation;
	}

	public int getMetaDataBitLocation() {
		return mMetaDataBitLocation;
	}

	public boolean isValue() {
		return mValue;
	}

	public BitSet getMetaData() {
		return mMetaData;
	}

	public BoardStateMetaDataKnowledgeBaseFileIndex getFileIndex() {
		return mFileIndex;
	}

	@Override
	public void initializeImplementation() throws WorkEntityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishImplementation() throws WorkEntityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkImplementation() throws WorkEntityException {
		// TODO Auto-generated method stub
		
	}
	
}
