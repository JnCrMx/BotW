package com.theredmajora.botw.dimension;

import com.theredmajora.botw.dimension.shrine.WorldProviderShrine;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class BOTWDimensions 
{
	public static DimensionType SHRINE = DimensionType.register("Shrine", "_shinre", DimensionManager.getNextFreeDimId(), WorldProviderShrine.class, false);
	
	public static void init()
	{
		DimensionManager.registerDimension(SHRINE.getId(), SHRINE);
	}
}
