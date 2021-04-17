package com.home.neil.game.attribute;

import java.util.List;

public interface IGameAttributeSet {
	public void addGameAttribute (GameAttribute pGameAttribute);
	
	public GameAttribute getGameAttribute (GameAttributeBitSet pGameAttributeBitSet);
	
	public List <? extends GameAttribute> getGameAttributes ();
	
	public Class<?> getGameAttributeClass();
	

}
