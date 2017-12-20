package com.theredmajora.botw.util;

import net.minecraft.entity.player.EntityPlayer;

public class ItemTrackerPacketData {

	EntityPlayer player;
	boolean render[];
	int playerID;

	public ItemTrackerPacketData(EntityPlayer player, boolean render[]) {

		this.player = player;
		this.render = render;
	}

	public ItemTrackerPacketData(int playerID, boolean render[]) {

		this.playerID = playerID;
		this.render = render;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	public boolean[] getRender() {
		return render;
	}

	public void setRender(boolean[] render) {
		this.render = render;
	}
	
	public int getPlayerID() {
		return playerID;
	}
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
}
