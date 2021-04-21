package com.home.neil.connectfour.knowledgebase.operations;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.appconfig.AppConfig;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsReadTask;
import com.home.neil.cachesegment.threads.operations.CompressableCacheSegmentOperationsWriteTask;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.FileDetails;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.IndexDetails;
import com.home.neil.connectfour.ConnectFourBoardConfig;
import com.home.neil.knowledgebase.KnowledgeBaseCompressableCacheSegmentConfig;
import com.home.neil.knowledgebase.KnowledgeBaseException;

import old.com.home.neil.connectfour.boardstate.OldBoardState;


public class ConnectFourOperationsTaskFactory {

	public static final String CLASS_NAME = ConnectFourOperationsTaskFactory.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	public static KnowledgeBaseCompressableCacheSegmentConfig sCompressableCacheSegmentConfig = null;

	public static ConnectFourBoardConfig sConnectFourBoardConfig = null;

	
	static {
		try {
			sCompressableCacheSegmentConfig = AppConfig.bind(KnowledgeBaseCompressableCacheSegmentConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sCompressableCacheSegmentConfig = null;
		}
		
		try {
			sConnectFourBoardConfig = AppConfig.bind(ConnectFourBoardConfig.class);
		} catch (NumberFormatException | NoSuchElementException | URISyntaxException | ConfigurationException | IOException e) {
			sConnectFourBoardConfig = null;
		}
	}
	
	public static CompressableCacheSegmentOperationsReadTask constructReadOperation (OldBoardState pBoardState) {
		FileDetails lFileDetails = getFileDetails (pBoardState);
		IndexDetails lIndexDetails = getIndexDetails (pBoardState);
		String lPoolItemId = getPoolItemId (pBoardState);
	}
	
	public static CompressableCacheSegmentOperationsWriteTask constructWriteOperation (OldBoardState pBoardState) {
		
	}
	
	public static FileDetails getFileDetails (OldBoardState pBoardState) throws KnowledgeBaseException {
		String [] lStatePaths;
		String lFileName;
		int lFileSize = 0;
		
		if (sCompressableCacheSegmentConfig.getDecisionPathsPerDecision() != sConnectFourBoardConfig.getColumns()) {
			
		}
				
		int lFileMoveStringCount = pMoveString.length() / sCompressableCacheSegmentConfig.getActionCountPerStateCache();
		int lFileMoveStringModulus = pMoveString.length() % sCompressableCacheSegmentConfig.getActionCountPerStateCache();
		
		
		
		
		
		FileDetails lFileDetails = new FileDetails (lStatePaths, lFileName, lFileSize);
		
		return lFileDetails;
	}
	
	public static IndexDetails getIndexDetails (String pMoveString, String pGameStateString, byte [] pBytes) throws KnowledgeBaseException{
		int lIndex = 0;
		int lIndexSize = 0;
		
		IndexDetails lIndexDetails = new IndexDetails(lIndex, lIndexSize, pBytes);
		return lIndexDetails;
	}
	
	public static String getPoolItemId  (String pMoveString, String pGameStateString) throws KnowledgeBaseException {
		String lPoolItemId;
		
		return lPoolItemId;
	}

}
