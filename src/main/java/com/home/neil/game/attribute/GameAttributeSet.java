package com.home.neil.game.attribute;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GameAttributeSet implements IGameAttributeSet {
	public static final String CLASS_NAME = GameAttributeSet.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);

	private HashMap<BitSet, GameAttribute> mBitSetToAttributeMap = null;
	private HashMap<String, GameAttribute> mNameToAttributeMap = null;
	private ArrayList<GameAttribute> mGameAttributes = null;
	protected Class<?> mGameAttributeClass = null;
	protected int mGameAttributeMaxBitSetSize = 0;

	protected GameAttributeSet(Class<?> pGameAttributeClass, int pGameAttributeMaxBitSetSize) {
		mGameAttributeClass = pGameAttributeClass;
		mGameAttributeMaxBitSetSize = pGameAttributeMaxBitSetSize;
		mGameAttributes = new ArrayList<>();
		mBitSetToAttributeMap = new HashMap<>();
		mNameToAttributeMap = new HashMap<>();
	}

	public static IGameAttributeSet getInstance() {
		throw new IllegalStateException("Game Attribute Set getInstance hasn't been set up in the subclass");
	}

	public synchronized void addGameAttribute(GameAttribute pGameAttribute) {
		mGameAttributes.add(pGameAttribute);
		mBitSetToAttributeMap.put(pGameAttribute.getAttributeBitSet().getBitSet(), pGameAttribute);
		mNameToAttributeMap.put(pGameAttribute.getAttributeName(), pGameAttribute);
	}

	public GameAttribute getGameAttribute(GameAttributeBitSet pBitSet) {
		return mBitSetToAttributeMap.get(pBitSet.getBitSet());
	}

	public GameAttribute getGameAttribute(String pAttributeName) {
		return mNameToAttributeMap.get(pAttributeName);
	}

	
	public List<? extends GameAttribute> getGameAttributes() {
		return mGameAttributes;
	}

	public Class<?> getGameAttributeClass() {
		return mGameAttributeClass;
	}

	public int getGameAttributeMaxBitSize() {
		return mGameAttributeMaxBitSetSize;
	}
}
