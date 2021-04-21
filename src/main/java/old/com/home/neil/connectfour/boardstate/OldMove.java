package old.com.home.neil.connectfour.boardstate;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.home.neil.connectfour.boardstate.ConnectFourBoardAttribute;

public class OldMove extends ConnectFourBoardAttribute {
	public static final String CLASS_NAME = OldMove.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private static int sColumnBooleanEncodingSize = 0;

	

	private static HashMap <String, OldMove> sMoves = new HashMap <> ();
	
	
	private OldPlayer mPlayer = null;
	private BitSet mColumnBooleanEncoding = null;
	private int mColumn = 0;
	private String mMoveString;

	public static final OldMove NOMOVE = new OldMove (OldPlayer.NULLPLAYER, -1);
	
	static {
		int lColumns = sConnectFourBoardConfig.getNumberOfColumns();
		while (lColumns > 0) {
			lColumns = lColumns >> 1;
			sColumnBooleanEncodingSize++;
		}

		sMoves.put(NOMOVE.getMoveString(), NOMOVE);
		
		for (int i = 0; i < sConnectFourBoardConfig.getNumberOfColumns(); i++) {
				OldMove lSelfMove = new OldMove (OldPlayer.SELF, i);
				sMoves.put(lSelfMove.getMoveString(), lSelfMove);
				OldMove lOpponentMove = new OldMove (OldPlayer.OPPONENT, i);
				sMoves.put(lOpponentMove.getMoveString(), lOpponentMove);
		}

		
	}

	public static OldMove getMove (OldPlayer pPlayer, int pColumn) {
		String lMoveString = constructMoveString(pPlayer, pColumn);
		return getMove (lMoveString);
	}
	
	public static OldMove getMove (String pMoveString) {
		return sMoves.get(pMoveString);
	}
	
	
	public static String constructMoveString(OldPlayer pPlayer, int pColumn) {
		return pPlayer.getPlayerString() + "_" + String.valueOf(pColumn);
	}


	private OldMove(OldPlayer pPlayer, int pColumn) {
		mColumn = pColumn;
		mPlayer = pPlayer;
		
		mColumnBooleanEncoding = encodeToBitSet(pColumn, sColumnBooleanEncodingSize);

		mMoveString = constructMoveString(pPlayer, pColumn);

	}

	public BitSet getColumnBooleanEncoding() {
		return mColumnBooleanEncoding;
	}


	public int getColumn() {
		return mColumn;
	}

	
	

	public String getMoveString() {
		return mMoveString;
	}
	
	public static Collection <OldMove> getMoves () {
		return sMoves.values();
	}

	public OldPlayer getPlayer() {
		return mPlayer;
	}
	
}
