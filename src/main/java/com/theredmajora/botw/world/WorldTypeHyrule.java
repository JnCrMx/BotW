package com.theredmajora.botw.world;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldType;

public class WorldTypeHyrule extends WorldType
{
	public WorldTypeHyrule()
	{
		super("hyrule");
	}
	
	@Override
	public void onGUICreateWorldPress()
	{
		System.out.println(Minecraft.getMinecraft().thePlayer);
	}
}
