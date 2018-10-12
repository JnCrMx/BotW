package com.theredmajora.botw.capability.itemtracker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.Storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityItemTracker {

	@CapabilityInject(IItemTracker.class)
	public static final Capability<IItemTracker> BOTW_ITEMTRACKER_CAP = null;

	public static final EnumFacing DEFAULT_FACING = null;

	/**
	 * The ID of the capability.
	 */
	public static final ResourceLocation ID = new ResourceLocation(BOTW.MODID, "ItemTracker");

	/**
	 * Register the capability.
	 */
	public static void register() {
		CapabilityManager.INSTANCE.register(IItemTracker.class, new Storage<IItemTracker>(), ItemTracker.class);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}


	public static class ItemTracker implements IItemTracker
	{		
		boolean shouldRenderSlate, shouldRenderGlider;
		int arrowCount;
		List<ItemStack> stacks = new ArrayList<>();
		BOTWRenderAction renderAction = BOTWRenderAction.NONE;

		EntityPlayer clientPlayer;
		HashMap<Integer, Boolean> dirty=new HashMap<>();

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
		public void setShouldRenderSlate(boolean bool)
		{
			if(this.shouldRenderSlate!=bool)
			{
				this.shouldRenderSlate = bool;
				setDirty();
			}
		}

		@Override
		public void setShouldRenderGlider(boolean bool)
		{
			if(this.shouldRenderGlider!=bool)
			{
				this.shouldRenderGlider = bool;
				setDirty();
			}
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
			tagCompound.setTag("Items", list);
			
			tagCompound.setInteger("RenderAction", renderAction.ordinal());
			
			return tagCompound;
		}

		@Override
		public void readNBT(NBTTagCompound tag)
		{
			this.shouldRenderSlate=tag.getBoolean("RenderSlate");
			this.shouldRenderGlider=tag.getBoolean("RenderGlider");	
			this.arrowCount=tag.getInteger("ArrowCount");
			
			NBTTagList list=tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
			stacks=new ArrayList<>();
			for(int i=0; i<list.tagCount(); i++)
			{
				NBTTagCompound item=list.getCompoundTagAt(i);
				ItemStack stack=new ItemStack(Items.GOLDEN_APPLE);
				stack.readFromNBT(item);
				
				stacks.add(stack);
			}
			
			this.renderAction=BOTWRenderAction.values()[tag.getInteger("RenderAction")];
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
			if(!this.stacks.equals(stacks))
			{
				this.stacks=stacks;
				setDirty();
			}
		}

		@Override
		public void setArrowCount(int count)
		{
			if(this.arrowCount!=count)
			{
				this.arrowCount=count;
				setDirty();
			}
		}

		@Override
		public Field getCapabilityField() throws NoSuchFieldException, SecurityException
		{
			return CapabilityItemTracker.class.getDeclaredField("BOTW_ITEMTRACKER_CAP");
		}

		@Override
		public boolean isDirty(EntityPlayer player)
		{
			return dirty.getOrDefault(player.getEntityId(), true);
		}

		@Override
		public void setDirty(EntityPlayer player, boolean dirty)
		{
			this.dirty.put(player.getEntityId(), dirty);
		}

		@Override
		public void setDirty()
		{
			this.dirty.replaceAll(new BiFunction<Integer, Boolean, Boolean>()
			{
				@Override
				public Boolean apply(Integer t, Boolean u)
				{
					return true;
				}
			});;
		}

		@Override
		public BOTWRenderAction getRenderAction()
		{
			return renderAction;
		}

		@Override
		public void setRenderAction(BOTWRenderAction renderAction)
		{
			if(this.renderAction!=renderAction)
			{
				this.renderAction=renderAction;
				setDirty();
			}
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

			if(e.isWasDeath())
			{
				if(e.getOriginal().hasCapability(BOTW_ITEMTRACKER_CAP, null))
				{
					IItemTracker oldTracker = e.getOriginal().getCapability(BOTW_ITEMTRACKER_CAP, null);
					IItemTracker newTracker = e.getEntityPlayer().getCapability(BOTW_ITEMTRACKER_CAP, null);
					
					newTracker.readNBT(oldTracker.writeNBT());
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
			return (NBTTagCompound)BOTW_ITEMTRACKER_CAP.getStorage().writeNBT(BOTW_ITEMTRACKER_CAP, IItemTracker, EnumFacing.NORTH);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			BOTW_ITEMTRACKER_CAP.getStorage().readNBT(BOTW_ITEMTRACKER_CAP, IItemTracker, EnumFacing.NORTH, nbt);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == BOTW_ITEMTRACKER_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == BOTW_ITEMTRACKER_CAP) {
				return BOTW_ITEMTRACKER_CAP.cast(this.IItemTracker);
			}
			return null;
		}
	}

}
