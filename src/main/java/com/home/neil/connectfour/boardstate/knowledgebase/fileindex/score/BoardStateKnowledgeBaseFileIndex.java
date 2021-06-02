package com.home.neil.connectfour.boardstate.knowledgebase.fileindex.score;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.appmanager.ApplicationPrecompilerSettings;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.FileDetails;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.IndexDetails;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.connectfour.boardstate.IBoardState;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.FileIndexException;
import com.home.neil.connectfour.boardstate.knowledgebase.fileindex.IFileIndex;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;

public class BoardStateKnowledgeBaseFileIndex implements IFileIndex {
	public static final String CLASS_NAME = BoardStateKnowledgeBaseFileIndex.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	protected static KnowledgeBaseCompressableCacheSegmentConfig sKnowledgeBaseConfig = null;
	protected static int sActionCountPerStateCache = 0;
	protected static int sDecisionPathsPerDecision = 0;
	protected static int sDecisionScoreSizeInBytes = 0;
	protected static int sMetaDataSizeInBytesPerDecision = 0;
	protected static int sRadix = 0;
	protected static int sFileSize = 0;
	protected static int sIndexSize = 0;
	
	static {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
			sKnowledgeBaseConfig = AppConfig.bind(KnowledgeBaseCompressableCacheSegmentConfig.class);
			
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}
		
		sActionCountPerStateCache = sKnowledgeBaseConfig.getActionCountPerStateCache();
		sDecisionScoreSizeInBytes = sKnowledgeBaseConfig.getDecisionScoreSizeInBytes();
		sDecisionPathsPerDecision = sKnowledgeBaseConfig.getDecisionPathsPerDecision();
		sMetaDataSizeInBytesPerDecision = sKnowledgeBaseConfig.getMetaDataSizeInBytesPerDecision();
		sRadix = ((sDecisionScoreSizeInBytes *
				sDecisionPathsPerDecision) + 
				sMetaDataSizeInBytesPerDecision);
		sFileSize = (int) Math.pow (sRadix, sActionCountPerStateCache);
		sIndexSize = sDecisionScoreSizeInBytes;
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}

	private IBoardState mBoardStateBase = null;
	
	private IndexDetails mIndexDetails = null;
	private FileDetails mFileDetails = null;
	private String mPoolItemId = null;
	private String mAddress = null;

	private IndexDetails mReciprocalIndexDetails = null;
	private FileDetails mReciprocalFileDetails = null;
	private String mReciprocalPoolItemId = null;
	private String mReciprocalAddress = null;
	
	public BoardStateKnowledgeBaseFileIndex (IBoardState pBoardState) throws FileIndexException {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mBoardStateBase = pBoardState;
		
		String [] lMoveStrings = mBoardStateBase.getCurrentMoveStrings();

		String lMoveString = lMoveStrings[0];
		String lReciprocalMoveString = lMoveStrings[1];

		int lActionCountPastState = lMoveString.length() % sActionCountPerStateCache;
		if (lActionCountPastState == 0 ) {
			lActionCountPastState = sActionCountPerStateCache;
		}
		int lStateCount = ((lMoveString.length() + sActionCountPerStateCache - 1) / sActionCountPerStateCache) - 1;
		if (lStateCount < 0) {
			lStateCount = 0;
		}
		
		String lActionsPastState = lMoveString.substring (lStateCount * sActionCountPerStateCache);
		if (lActionsPastState.length() == 0) {
			lActionsPastState = "0";
		}
		String lReciprocalActionsPastState = lReciprocalMoveString.substring (lStateCount * sActionCountPerStateCache);
		if (lReciprocalActionsPastState.length() == 0) {
			lReciprocalActionsPastState = "0";
		}
		
		if (lMoveString.length() == 0) { // covers 0
			String [] lStatePaths = new String [] {"."};
			String lFileName = "XMovePrime";
			int lFileIndex = 0;
			mFileDetails = new FileDetails (lStatePaths, lFileName, sFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, sIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {"."};
			String lReciprocalFileName = "XMovePrime";
			int lReciprocalFileIndex = 0;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, sFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, sIndexSize);
		} else if (lMoveString.length() <= sActionCountPerStateCache) { // covers 1 - 9
			String [] lStatePaths = new String [] {"."};
			String lFileName = "XMovePrime";
			int lFileIndex = Integer.parseInt(lActionsPastState, sRadix);
			mFileDetails = new FileDetails (lStatePaths, lFileName, sFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, sIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {"."};
			String lReciprocalFileName = "XMovePrime";
			int lReciprocalFileIndex = Integer.parseInt(lReciprocalActionsPastState, sRadix);;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, sFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, sIndexSize);
		} else {
			IBoardState lBoardState = pBoardState; 
			for (int i = 0; i < lActionCountPastState; i++) {
				lBoardState = lBoardState.getPreviousBoardState();
			}
			String [] lStateBoardStateStrings = lBoardState.getCurrentBoardStateStrings();
			
			String [] lStatePaths = new String [] {"L" + lStateCount};
			String lFileName = "XMove" + lStateBoardStateStrings [0];
			
			int lFileIndex = Integer.parseInt(lActionsPastState, sRadix);
			mFileDetails = new FileDetails (lStatePaths, lFileName, sFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, sIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {"L" + lStateCount};
			String lReciprocalFileName = "XMove" + lStateBoardStateStrings [1];
			int lReciprocalFileIndex = Integer.parseInt(lReciprocalActionsPastState, sRadix);;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, sFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, sIndexSize);
		}

		mPoolItemId = mFileDetails.getFileName();
		mReciprocalPoolItemId = mReciprocalFileDetails.getFileName();
		
		StringBuilder lScoreAddressBuffer = new StringBuilder();
		for (String lStatePath : mFileDetails.getStatePaths()) {
			lScoreAddressBuffer.append(lStatePath);
			lScoreAddressBuffer.append("/");
		}
		lScoreAddressBuffer.append(mFileDetails.getFileName());
		lScoreAddressBuffer.append("/");
		lScoreAddressBuffer.append(mIndexDetails.getIndex());
		mAddress = lScoreAddressBuffer.toString();

		StringBuilder lReciprocalScoreAddressBuffer = new StringBuilder();
		for (String lReciprocalStatePath : mReciprocalFileDetails.getStatePaths()) {
			lReciprocalScoreAddressBuffer.append(lReciprocalStatePath);
			lReciprocalScoreAddressBuffer.append("/");
		}
		lReciprocalScoreAddressBuffer.append(mReciprocalFileDetails.getFileName());
		lReciprocalScoreAddressBuffer.append("/");
		lReciprocalScoreAddressBuffer.append(mReciprocalIndexDetails.getIndex());
		mReciprocalAddress = lReciprocalScoreAddressBuffer.toString();

		sLogger.debug("MoveString: {{}} File Directory: {{}} File Name: {{}} FileIndex: {{}}", 
				lMoveString, mFileDetails.getStatePaths()[0], mFileDetails.getFileName(), mIndexDetails.getIndex());
		sLogger.debug("MoveString: {{}} File Directory: {{}} File Name: {{}} FileIndex: {{}}", 
				lReciprocalMoveString, mReciprocalFileDetails.getStatePaths()[0], 
				mReciprocalFileDetails.getFileName(), mReciprocalIndexDetails.getIndex());
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}
	
	public IBoardState getBoardState() {
		return mBoardStateBase;
	}

	public IndexDetails getIndexDetails() {
		return mIndexDetails;
	}

	public FileDetails getFileDetails() {
		return mFileDetails;
	}

	public String getPoolItemId() {
		return mPoolItemId;
	}

	public IndexDetails getReciprocalIndexDetails() {
		return mReciprocalIndexDetails;
	}

	public FileDetails getReciprocalFileDetails() {
		return mReciprocalFileDetails;
	}

	public String getReciprocalPoolItemId() {
		return mReciprocalPoolItemId;
	}

	public String getReciprocalAddress() {
		return mReciprocalAddress;
	}
	
	public String getAddress() {
		return mAddress;
	}	

	public void setScoreToWriteFromBoardState (byte pSURROGATEZERO) {
		mIndexDetails.setIndexSegment(new byte[] { (mBoardStateBase.getMoveScore() == 0) ? pSURROGATEZERO : mBoardStateBase.getMoveScore() });
		mReciprocalIndexDetails.setIndexSegment(new byte[] { (mBoardStateBase.getMoveScore() == 0) ? pSURROGATEZERO : mBoardStateBase.getMoveScore() });
	}

}
