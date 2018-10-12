package com.theredmajora.botw.tileentity;

import com.theredmajora.botw.block.BOTWBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityTempIce extends TileEntity implements ITickable
{
	private int time;
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("time", this.time);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.time = compound.getInteger("time");
    }

    public void update()
    {
    	time++;
    	
    	if(time > 120)
    	{
    		this.worldObj.addBlockEvent(this.pos, BOTWBlocks.temp_ice, 0, 0);
    	}
    }
}
