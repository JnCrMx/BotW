package com.theredmajora.botw.capability.itemtracker;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.omg.IOP.TAG_CODE_SETS;

import com.theredmajora.botw.BOTW;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityItemTracker {

	@CapabilityInject(IItemTracker.class)
	public static final Capability<IItemTracker> BOTW_CAP = null;

	public static final EnumFacing DEFAULT_FACING = null;

	/**
	 * The ID of the capability.
	 */
	public static final ResourceLocation ID = new ResourceLocation(BOTW.MODID, "ItemTracker");

	/**
	 * Register the capability.
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(IItemTracker.class, new Storage(), ItemTracker.class);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}


	public static class ItemTracker implements IItemTracker
	{
		boolean shouldRenderBow, shouldRenderSlate, shouldRenderGlider;
		int arrowCount;
		List<ItemStack> stacks = new ArrayList<>();
		EntityPlayer clientPlayer;

		public ItemTracker(EntityPlayer player) {
			this.clientPlayer = player;
		}

		@Override
		public boolean shouldRenderSlate() {
			return shouldRenderSlate;
		}

		@Override
		public boolean shouldRenderGlider() {
			return shouldRenderGlider;
		}

		@Override
		public void setShouldRenderSlate(boolean bool) {
			this.shouldRenderSlate = bool;
		}

		@Override
		public void setShouldRenderGlider(boolean bool) {
			this.shouldRenderGlider = bool;
		}

		@Override
		public int getEntityId() {
			return clientPlayer.getEntityId();
		}

		@Override
		public NBTTagCompound writeNBT()
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			
			tagCompound.setBoolean("RenderSlate", shouldRenderSlate());
			tagCompound.setBoolean("RenderGlider", shouldRenderGlider());
			tagCompound.setInteger("ArrowCount", getArrowCount());
			
			NBTTagList list=new NBTTagList();
			for(int i=0; i<stacks.size(); i++)
			{
				ItemStack stack=stacks.get(i);
				list.appendTag(stack.writeToNBT(new NBTTagCompound()));
			}
			tagCompound.setTag("items", list);
			
			return tagCompound;
		}

		@Override
		public void readNBT(NBTTagCompound tag)
		{
			setShouldRenderSlate(tag.getBoolean("RenderSlate"));
			setShouldRenderGlider(tag.getBoolean("RenderGlider"));	
			setArrowCount(tag.getInteger("ArrowCount"));
			
			NBTTagList list=tag.getTagList("items", Constants.NBT.TAG_COMPOUND);
			stacks=new ArrayList<>();
			for(int i=0; i<list.tagCount(); i++)
			{
				NBTTagCompound item=list.getCompoundTagAt(i);
				ItemStack stack=new ItemStack(Items.GOLDEN_APPLE);
				stack.readFromNBT(item);
				
				stacks.add(stack);
			}
		}

		@Override
		public List<ItemStack> getRenderingItemStacks()
		{
			return stacks;
		}

		@Override
		public int getArrowCount()
		{
			return arrowCount;
		}

		@Override
		public void setRenderingItemStacks(List<ItemStack> stacks)
		{
			this.stacks=stacks;
		}

		@Override
		public void setArrowCount(int count)
		{
			this.arrowCount=count;
		}
	}

	/**
	 * Storage for the IWCustomInventory capability
	 *
	 */
	public static class Storage implements Capability.IStorage<IItemTracker>
	{

		@Override
		public NBTBase writeNBT(Capability<IItemTracker> capability, IItemTracker instance, EnumFacing side) {
			return instance.writeNBT();
		}

		@Override
		public void readNBT(Capability<IItemTracker> capability, IItemTracker instance, EnumFacing side, NBTBase nbt) {
			NBTTagCompound tagCompound = (NBTTagCompound) nbt;
			instance.readNBT(tagCompound);
		}

	}


	public static class EventHandler {

		@SubscribeEvent
		public void attachCapabilities(AttachCapabilitiesEvent.Entity event) {
			if (event.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getEntity();
				event.addCapability(ID, new Provider(player));
			}
		}

		@SubscribeEvent
		public void onPlayerCloned(PlayerEvent.Clone e) {
			NBTTagCompound nbt = new NBTTagCompound();

			if(e.isWasDeath())
			{
				if(e.getOriginal().hasCapability(BOTW_CAP, null))
				{
					IItemTracker newTracker = e.getEntityPlayer().getCapability(BOTW_CAP, null); //FIXME		   
				}
			}
		}
	}

	/**
	 * Provider for the {@link IItemTracker} capability.
	 */
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private final IItemTracker IItemTracker;

		public Provider(EntityPlayer player)
		{
			this(new ItemTracker(player));
		}

		public Provider(IItemTracker itemTracker) {
			this.IItemTracker = itemTracker;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound)BOTW_CAP.getStorage().writeNBT(BOTW_CAP, IItemTracker, EnumFacing.NORTH);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			BOTW_CAP.getStorage().readNBT(BOTW_CAP, IItemTracker, EnumFacing.NORTH, nbt);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == BOTW_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == BOTW_CAP) {
				return BOTW_CAP.cast(this.IItemTracker);
			}
			return null;
		}
	}

}
