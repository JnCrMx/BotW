package com.theredmajora.botw.dimension.shrine;

import com.theredmajora.botw.block.BOTWBlocks;
import com.theredmajora.botw.block.BlockShrineLift;
import com.theredmajora.botw.dimension.BOTWDimensions;
import com.theredmajora.botw.entity.EntityShrineLift;
import com.theredmajora.botw.tileentity.TileEntityShrineLift;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterShrine extends Teleporter
{
	private TileEntityShrineLift tileEntityShrineLift;
	private WorldServer worldServer;
	
	public TeleporterShrine(WorldServer worldIn, TileEntityShrineLift tileEntityShrineLift)
	{
		super(worldIn);
		this.tileEntityShrineLift=tileEntityShrineLift;
		this.worldServer=worldIn;
	}
	
	@Override
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw)
	{		
		BlockPos postion = new BlockPos(tileEntityShrineLift.shrineX, tileEntityShrineLift.shrineY, tileEntityShrineLift.shrineZ);
		BlockPos oldPos = tileEntityShrineLift.getPos();
		
		if(worldServer.provider.getDimensionType()==BOTWDimensions.SHRINE)
		{
			worldServer.setBlockState(postion, Blocks.AIR.getDefaultState());	//Required for some reason
			
			EntityShrineLift lift = new EntityShrineLift(worldServer);
			lift.setShrine(oldPos);
			lift.setHeight(19);
			lift.setDirection(EnumFacing.DOWN);
			lift.setPosition(postion.getX(), postion.getY(), postion.getZ());
			worldServer.spawnEntityInWorld(lift);
			
			entityIn.setPosition(postion.getX()+0.5, postion.getY()+20, postion.getZ()+0.5);
		}
		else
		{			
			entityIn.setPosition(postion.getX()+0.5, postion.getY()+0.1, postion.getZ()+0.5);
			
			worldServer.setBlockState(postion, BOTWBlocks.shrine_lift.getDefaultState());
			TileEntityShrineLift tileEntity=(TileEntityShrineLift)worldServer.getTileEntity(postion);
	
			tileEntity.shrineId=tileEntityShrineLift.shrineId;
			tileEntity.shrineName=tileEntityShrineLift.shrineName;
			tileEntity.shrineX=oldPos.getX();
			tileEntity.shrineY=oldPos.getY();
			tileEntity.shrineZ=oldPos.getZ();
		}
		
//		System.out.print("TeleporterShrine.placeInExistingPortal()    ");
//		System.out.print(oldPos+"    ");
//		System.out.println(worldServer.provider.getDimensionType().toString());
		
		return true;
	}
}
