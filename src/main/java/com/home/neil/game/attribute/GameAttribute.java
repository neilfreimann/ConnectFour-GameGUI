package com.home.neil.game.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class GameAttribute {
	public static final String CLASS_NAME = GameAttribute.class.getName();
	public static final String PACKAGE_NAME = CLASS_NAME.substring(0, CLASS_NAME.lastIndexOf("."));
	public static final Logger sLogger = LogManager.getLogger(PACKAGE_NAME);
	
	protected GameAttributeBitSet mGameAttributeBitSet = null;
	protected String mAttributeName = null;
	protected GameAttribute [] mGameAttributeComponents = null;
	
	protected GameAttribute (int pAttributeId, int pAttributeMaxValue, String pAttributeName) {
		mGameAttributeBitSet = new GameAttributeBitSet (pAttributeId, pAttributeMaxValue);
		mGameAttributeComponents = null;
		mAttributeName = pAttributeName;
	}
	
	protected GameAttribute (GameAttribute [] pGameAttributes, String pAttributeName) {
		mGameAttributeBitSet = new GameAttributeBitSet(pGameAttributes);
		mGameAttributeComponents = pGameAttributes;
		mAttributeName = pAttributeName;
	}
	
	public GameAttributeBitSet getAttributeBitSet() {
		return mGameAttributeBitSet;
	}

	public String getAttributeName() {
		return mAttributeName;
	}

}
