package com.theredmajora.botw.capability.itemtracker;

import java.util.List;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemTracker {
	
	public boolean shouldRenderSlate();
	public boolean shouldRenderGlider();
	public List<ItemStack> getRenderingItemStacks();
	public int getArrowCount();
	
	public void setShouldRenderSlate(boolean bool);
	public void setShouldRenderGlider(boolean bool);
	public void setRenderingItemStacks(List<ItemStack> stacks);
	public void setArrowCount(int count);
	
	public int getEntityId();

	public NBTTagCompound writeNBT();
	public void readNBT(NBTTagCompound tag);
}
