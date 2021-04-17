package com.home.neil.game.attribute;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameAttributeBitSet {
	public static final String CLASS_NAME = GameAttributeBitSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	

	public static BitSet encodeToBitSet(int pNumber, int pMaxBitSetSize) {
		BitSet lBits = new BitSet(pMaxBitSetSize);
		lBits.set(0, pMaxBitSetSize, false);
		int lIndex = 0;
		while (pNumber != 0L) {
			if (pNumber % 2L != 0) {
				lBits.set(lIndex);
			}
			++lIndex;
			pNumber = pNumber >>> 1;
		}
		return lBits;
	}
	
	public static int convert(BitSet pBitSet) {
		int lValue = 0;
		for (int i = 0; i < pBitSet.length(); ++i) {
			lValue += pBitSet.get(i) ? (1L << i) : 0L;
		}
		return lValue;
	}

	public static int getEncodingSize (int pMaxValue) {
		int lEncodingSize = 0;
		int lMaxValue = pMaxValue;
		while (lMaxValue > 0) {
			lMaxValue = lMaxValue >> 1;
			lEncodingSize++;
		}
		return lEncodingSize;
	}
	
	protected BitSet mBitSet = null;
	protected int mMaxBitSetSize = 64;
	
	public GameAttributeBitSet (BitSet pBitSet) {
		mBitSet = (BitSet) pBitSet.clone();
		mMaxBitSetSize = mBitSet.size();
	}
	

	public GameAttributeBitSet (GameAttribute pGameAttribute) {
		mMaxBitSetSize = pGameAttribute.getAttributeBitSet().getMaxBitSetSize();
		mBitSet = (BitSet) pGameAttribute.getAttributeBitSet().getBitSet().clone();
	}
	
	public GameAttributeBitSet (GameAttribute [] pGameAttributes) {
		
		mBitSet = new BitSet();
		int lNewBitSetIndex = 0;
		mMaxBitSetSize = 0;
		for (GameAttribute lGameAttribute : pGameAttributes) {
			BitSet lBitSet = lGameAttribute.getAttributeBitSet().getBitSet();
			for (int lBitSetIndex = 0; lBitSetIndex < lGameAttribute.getAttributeBitSet().mMaxBitSetSize; lBitSetIndex++) {
				mBitSet.set(lNewBitSetIndex, lBitSet.get(lBitSetIndex));
				lNewBitSetIndex++;
			}
			mMaxBitSetSize += lGameAttribute.getAttributeBitSet().mMaxBitSetSize;
		}
	}
		
	public GameAttributeBitSet (int pAttributeId, int pMaxAttributeIdValue) {
		mMaxBitSetSize = getEncodingSize(pMaxAttributeIdValue);
		mBitSet = encodeToBitSet (pAttributeId, mMaxBitSetSize);
	}
	
	public int getMaxBitSetSize() {
		return mMaxBitSetSize;
	}

	public BitSet getBitSet() {
		return mBitSet;
	}
	
	public int getAttributeId () {
		return convert (mBitSet);
	}
	
	public String getBitSetString() {
        StringBuilder lBuffer = new StringBuilder(mBitSet.length());
        for (int i = 0; i < mMaxBitSetSize ; i++) {
        	lBuffer.append(mBitSet.get(i) ? "1" : "0");
        }
        return lBuffer.toString();
	}
	
	
	

}
