package com.theredmajora.botw.capability.itemtracker;

import com.theredmajora.botw.util.ENUMClientPacketState;

public interface IItemTracker {

	public boolean shouldRenderBow();
	
	public boolean shouldRenderSlate();
	
	public boolean shouldRenderGlider();
	
	public void setShouldRenderBow(boolean bool);
	
	public void setShouldRenderSlate(boolean bool);
	
	public void setShouldRenderGlider(boolean bool);
	
	public ENUMClientPacketState getPacketState();
	
	public void setPacketState(ENUMClientPacketState state);
	
	public void updateServer();
		
	
}
