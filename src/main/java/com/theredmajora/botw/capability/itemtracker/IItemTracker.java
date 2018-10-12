package com.theredmajora.botw.capability.itemtracker;

import java.util.List;

import com.theredmajora.botw.capability.ITracker;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemTracker extends ITracker{
	
	public boolean shouldRenderSlate();
	public boolean shouldRenderGlider();
	public List<ItemStack> getRenderingItemStacks();
	public int getArrowCount();
	public BOTWRenderAction getRenderAction();
	
	public void setShouldRenderSlate(boolean bool);
	public void setShouldRenderGlider(boolean bool);
	public void setRenderingItemStacks(List<ItemStack> stacks);
	public void setArrowCount(int count);
	public void setRenderAction(BOTWRenderAction renderAction);
	
	public static enum BOTWRenderAction
	{
		NONE,
		CLIMB_UP
	}
}
