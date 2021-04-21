package com.home.neil.connectfour.boardstate.knowledgebase;

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
import com.home.neil.connectfour.boardstate.BoardState;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;

public class BoardStateKnowledgeBaseFileIndex {
	public static final String CLASS_NAME = BoardStateKnowledgeBaseFileIndex.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	protected static ConnectFourBoardConfig sConnectFourBoardConfig = null;
	protected static KnowledgeBaseCompressableCacheSegmentConfig sKnowledgeBaseConfig = null;
	
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
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
	}

	private BoardState mBoardState = null;
	
	private IndexDetails mIndexDetails = null;
	private FileDetails mFileDetails = null;
	private String mPoolItemId = null;
	
	private IndexDetails mReciprocalIndexDetails = null;
	private FileDetails mReciprocalFileDetails = null;
	private String mReciprocalPoolItemId = null;
		
	public BoardStateKnowledgeBaseFileIndex (BoardState pBoardState) {
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_ENTERING);
		}
		
		mBoardState = pBoardState;
		
		int lFileSize = ((sKnowledgeBaseConfig.getDecisionScoreSizeInBytes() *
				sKnowledgeBaseConfig.getDecisionPathsPerDecision()) + 
				sKnowledgeBaseConfig.getMetaDataSizeInBytesPerDecision()) *
				sKnowledgeBaseConfig.getActionCountPerStateCache();
		int lIndexSize = sKnowledgeBaseConfig.getDecisionScoreSizeInBytes();
		int lRadix = ((sKnowledgeBaseConfig.getDecisionScoreSizeInBytes() *
				sKnowledgeBaseConfig.getDecisionPathsPerDecision()) + 
				sKnowledgeBaseConfig.getMetaDataSizeInBytesPerDecision());
		
		String [] lMoveStrings = mBoardState.constructMoveStrings();

		String lMoveString = lMoveStrings[0];
		String lReciprocalMoveString = lMoveStrings[1];

		int lActionCountPastState = lMoveString.length() % sKnowledgeBaseConfig.getActionCountPerStateCache();
		int lStateCount = lMoveString.length() / sKnowledgeBaseConfig.getActionCountPerStateCache();
		
		String lActionsPastState = lMoveString.substring (lActionCountPastState * sKnowledgeBaseConfig.getActionCountPerStateCache());
		String lReciprocalActionsPastState = lReciprocalMoveString.substring (lActionCountPastState * sKnowledgeBaseConfig.getActionCountPerStateCache());
	
		if (lMoveString.length() == 0) {
			String [] lStatePaths = new String [] {};
			String lFileName = "XMove";
			int lFileIndex = 0;
			mFileDetails = new FileDetails (lStatePaths, lFileName, lFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, lIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {};
			String lReciprocalFileName = "XMove";
			int lReciprocalFileIndex = 0;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, lFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, lIndexSize);
		} else if (lMoveString.length() < sKnowledgeBaseConfig.getActionCountPerStateCache()) {
			String [] lStatePaths = new String [] {};
			String lFileName = "XMove";
			int lFileIndex = Integer.parseInt(lActionsPastState, lRadix);
			mFileDetails = new FileDetails (lStatePaths, lFileName, lFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, lIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {};
			String lReciprocalFileName = "XMove";
			int lReciprocalFileIndex = Integer.parseInt(lReciprocalActionsPastState, lRadix);;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, lFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, lIndexSize);
		} else {
			BoardState lBoardState = pBoardState; 
			for (int i = 0; i < lActionCountPastState; i++) {
				lBoardState = lBoardState.getPreviousBoardState();
			}
			String [] lStateBoardStateStrings = lBoardState.constructBoardStateStrings();
			
			String [] lStatePaths = new String [] {"L" + lStateCount};
			String lFileName = "XMove" + lStateBoardStateStrings [0];
			
			int lFileIndex = Integer.parseInt(lActionsPastState, lRadix);
			mFileDetails = new FileDetails (lStatePaths, lFileName, lFileSize);
			mIndexDetails = new IndexDetails (lFileIndex, lIndexSize);
			
			String [] lReciprocalStatePaths = new String [] {"L" + lStateCount};
			String lReciprocalFileName = "XMove" + lStateBoardStateStrings [1];
			int lReciprocalFileIndex = Integer.parseInt(lReciprocalActionsPastState, lRadix);;
			mReciprocalFileDetails = new FileDetails (lReciprocalStatePaths, lReciprocalFileName, lFileSize);
			mReciprocalIndexDetails = new IndexDetails (lReciprocalFileIndex, lIndexSize);
		}

		mPoolItemId = mFileDetails.getFileName();
		mReciprocalPoolItemId = mReciprocalFileDetails.getFileName();
		
		sLogger.debug("MoveString: {{}} File Directory: {{}} File Name: {{}} FileIndex: {{}}", 
				lMoveString, mFileDetails.getStatePaths()[0], mFileDetails.getFileName(), mIndexDetails.getIndex());
		sLogger.debug("MoveString: {{}} File Directory: {{}} File Name: {{}} FileIndex: {{}}", 
				lReciprocalMoveString, mReciprocalFileDetails.getStatePaths()[0], 
				mReciprocalFileDetails.getFileName(), mReciprocalIndexDetails.getIndex());
		
		if (ApplicationPrecompilerSettings.TRACE_LOGACTIVE) {
			sLogger.trace(ApplicationPrecompilerSettings.TRACE_EXITING);
		}
	}

	public BoardState getBoardState() {
		return mBoardState;
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
	
	
	
	
	
}
