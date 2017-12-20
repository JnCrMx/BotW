package com.theredmajora.botw.dimension.shrine;

import com.theredmajora.botw.tileentities.TileEntityShrineLift;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterShrine extends Teleporter
{
	private TileEntityShrineLift tileEntityShrineLift;
	
	public TeleporterShrine(WorldServer worldIn, TileEntityShrineLift tileEntityShrineLift)
	{
		super(worldIn);
		this.tileEntityShrineLift=tileEntityShrineLift;
	}
	
	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw)
	{
		entityIn.posX=tileEntityShrineLift.shrineX;
		entityIn.posY=tileEntityShrineLift.shrineY;
		entityIn.posZ=tileEntityShrineLift.shrineZ;
	}
}
