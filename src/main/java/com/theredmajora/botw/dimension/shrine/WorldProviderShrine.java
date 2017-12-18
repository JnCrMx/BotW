package com.theredmajora.botw.dimension.shrine;

import com.theredmajora.botw.dimension.BOTWDimensions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderShrine extends WorldProvider 
{
	@Override
	public IChunkGenerator createChunkGenerator() 
	{
		return new ChunkGeneratorShrine(worldObj);
	}
	
	@Override
	public Biome getBiomeForCoords(BlockPos pos)
	{
		return Biomes.VOID;
	}
	
	@Override
	public boolean canRespawnHere() 
	{
		return true;
	}
	
	@Override
	public int getRespawnDimension(EntityPlayerMP player) 
	{
		return BOTWDimensions.SHRINE.getId();
	}
	
	@Override
	public boolean isSurfaceWorld()
	{
		return true;
	}
	
	@Override
	public DimensionType getDimensionType() 
	{
		return BOTWDimensions.SHRINE;
	}

}
