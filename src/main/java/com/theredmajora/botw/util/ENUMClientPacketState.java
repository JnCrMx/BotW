package com.theredmajora.botw.util;

public enum ENUMClientPacketState {

	OLD(0), NEW(1);
	
	
	int id;
	private ENUMClientPacketState(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}
