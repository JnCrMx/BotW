package com.theredmajora.botw.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class Storage<T extends ITracker> implements Capability.IStorage<T>
{
	@Override
	public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
		return instance.writeNBT();
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tagCompound = (NBTTagCompound) nbt;
		instance.readNBT(tagCompound);
	}
}
