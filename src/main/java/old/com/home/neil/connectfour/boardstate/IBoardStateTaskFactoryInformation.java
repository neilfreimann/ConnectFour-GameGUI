package old.com.home.neil.connectfour.boardstate;

public interface IBoardStateTaskFactoryInformation {
	
	public String getGameStateStringForKBCache ();
	
	public String getReciprocalGameStateStringForKBCache();
	
	public String getMoveStringForKB ();
	
	public String getReciprocalMoveStringForKB();

}
