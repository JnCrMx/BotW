package com.theredmajora.botw.capability.playertracker;

import com.theredmajora.botw.capability.ITracker;

public interface IPlayerTracker extends ITracker
{
	public int getMaxStamina();
	public int getStamina();
	public boolean isExhausted();
	public int getCurrentStasisEntity();
	public int getCurrentStasisTime();
	
	public void setMaxStamina(int maxStamina);
	public void setStamina(int stamina);
	public void setExhausted(boolean exhausted);
	public void setCurrentStasisEntity(int entityId);
	public void setCurrentStasisTime(int time);
}
