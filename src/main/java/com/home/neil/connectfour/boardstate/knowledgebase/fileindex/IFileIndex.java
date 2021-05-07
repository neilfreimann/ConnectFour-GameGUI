package com.home.neil.connectfour.boardstate.knowledgebase.fileindex;

import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.FileDetails;
import com.home.neil.cachesegment.threads.operations.ICompressableCacheSegmentOperationsTask.IndexDetails;
import com.home.neil.connectfour.boardstate.BoardState;

public interface IFileIndex {
	public BoardState getBoardState();

	public IndexDetails getIndexDetails();

	public FileDetails getFileDetails();

	public String getPoolItemId();

	public IndexDetails getReciprocalIndexDetails();

	public FileDetails getReciprocalFileDetails();

	public String getReciprocalPoolItemId();

	public String getReciprocalAddress();
	
	public String getAddress();
}
