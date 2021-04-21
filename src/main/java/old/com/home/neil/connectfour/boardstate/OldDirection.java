package old.com.home.neil.connectfour.boardstate;

import java.util.BitSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldDirection {
	public static final String CLASS_NAME = OldDirection.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	private boolean mDirection [] = new boolean [] {false,false};
	
	private String mDirectionString;

	private OldDirection (boolean [] pDirection, String pDirectionString) {
		mDirection = pDirection;
		mDirectionString = pDirectionString;
	}
	
	public static final OldDirection VERTICAL = new OldDirection (new boolean [] {false, false}, "V");
	public static final OldDirection HORIZONTAL = new OldDirection (new boolean [] {false, true}, "H");
	public static final OldDirection DIAGONAL = new OldDirection (new boolean [] {true, false}, "D");
	public static final OldDirection OPPOSITE = new OldDirection (new boolean [] {true, true}, "O");
	
	public OldDirection getDirection (boolean [] pDirection) {
		if (!pDirection [0] && !pDirection[1]) {
			return VERTICAL;
		} else if (!pDirection [0] && pDirection[1]) {
			return HORIZONTAL;
		} else if (pDirection [0] && !pDirection[1]) {
			return DIAGONAL;
		} else {
			return OPPOSITE;
		}
	}

	public boolean [] getBoolean () {
		return mDirection;
	}
	
	public BitSet getDirectionBooleanEncoding () {
		BitSet lBitSet = new BitSet (2);
		lBitSet.set(0, mDirection[0]);
		lBitSet.set(1, mDirection[1]);
		return lBitSet;
	}
	
	public String getDirectionString () {
		return mDirectionString;
	}
	
}
