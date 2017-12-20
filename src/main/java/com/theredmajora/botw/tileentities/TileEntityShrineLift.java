package com.theredmajora.botw.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityShrineLift extends TileEntity
{
	public int shrineId;
	public String shrineName;
	public double shrineX, shrineY, shrineZ;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		compound.setInteger("shrineId", shrineId);
		compound.setString("shrineName", shrineName);
		compound.setDouble("shrineX", shrineX);
		compound.setDouble("shrineY", shrineY);
		compound.setDouble("shrineZ", shrineZ);
		
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);

		this.shrineId=compound.getInteger("shrineId");
		this.shrineName=compound.getString("shrineName");
		this.shrineX=compound.getDouble("shrineX");
		this.shrineY=compound.getDouble("shrineY"); if(this.shrineY==0) this.shrineY=70;
		this.shrineZ=compound.getDouble("shrineZ");
	}
}
