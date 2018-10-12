package com.theredmajora.botw.capability.playertracker;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.BiFunction;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.capability.Storage;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker.ItemTracker;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @see CapabilityItemTracker
 */
public class CapabilityPlayerTracker
{
	@CapabilityInject(IPlayerTracker.class)
	public static final Capability<IPlayerTracker> BOTW_PLAYERTRACKER_CAP = null;

	public static final EnumFacing DEFAULT_FACING = null;

	/**
	 * The ID of the capability.
	 */
	public static final ResourceLocation ID = new ResourceLocation(BOTW.MODID, "PlayerTracker");

	/**
	 * Register the capability.
	 */
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IPlayerTracker.class, new Storage<IPlayerTracker>(), PlayerTracker.class);
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	/**
	 * @see CapabilityItemTracker.ItemTracker
	 */
	public static class PlayerTracker implements IPlayerTracker
	{
		private int maxStamina;
		private int stamina;
		private boolean exhausted;
		private int stasisEntity=-1;
		private int stasisTime=-1;
		
		private EntityPlayer clientPlayer;
		private HashMap<Integer, Boolean> dirty=new HashMap<>();

		public PlayerTracker(EntityPlayer player) {
			this.clientPlayer = player;
		}

		@Override
		public int getMaxStamina()
		{
			return maxStamina;
		}

		@Override
		public int getStamina()
		{
			return stamina;
		}

		@Override
		public void setMaxStamina(int maxStamina)
		{
			if(this.maxStamina!=maxStamina)
			{
				this.maxStamina=maxStamina;
				setDirty();
			}
		}

		@Override
		public void setStamina(int stamina)
		{
			if(this.stamina!=stamina)
			{
				this.stamina=stamina;
				setDirty();
			}
		}

		@Override
		public int getEntityId()
		{
			return clientPlayer.getEntityId();
		}

		@Override
		public NBTTagCompound writeNBT()
		{
			NBTTagCompound tag = new NBTTagCompound();
			
			tag.setInteger("MaxStamina", maxStamina);
			tag.setInteger("Stamina", stamina);
			tag.setBoolean("Exhausted", exhausted);
			tag.setInteger("StasisEntity", stasisEntity);
			tag.setInteger("StasisTime", stasisTime);
			
			return tag;
		}

		@Override
		public void readNBT(NBTTagCompound tag)
		{
			this.maxStamina = tag.getInteger("MaxStamina");
			this.stamina = tag.getInteger("Stamina");
			this.exhausted = tag.getBoolean("Exhausted");
			this.stasisEntity = tag.getInteger("StasisEntity");
			this.stasisTime = tag.getInteger("StasisTime");
		}

		@Override
		public Field getCapabilityField() throws NoSuchFieldException, SecurityException
		{
			return CapabilityPlayerTracker.class.getDeclaredField("BOTW_PLAYERTRACKER_CAP");
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
		public boolean isExhausted()
		{
			return exhausted;
		}

		@Override
		public void setExhausted(boolean exhausted)
		{
			if(this.exhausted!=exhausted)
			{
				this.exhausted=exhausted;
				setDirty();
			}
		}

		@Override
		public int getCurrentStasisEntity()
		{
			return stasisEntity;
		}

		@Override
		public int getCurrentStasisTime()
		{
			return stasisTime;
		}

		@Override
		public void setCurrentStasisEntity(int entityId)
		{
			if(this.stasisEntity!=entityId)
			{
				this.stasisEntity=entityId;
				setDirty();
			}
		}

		@Override
		public void setCurrentStasisTime(int time)
		{
			if(this.stasisTime!=time)
			{
				this.stasisTime=time;
				setDirty();
			}
		}
	}
	
	/**
	 * @see CapabilityItemTracker.EventHandler
	 */
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
				if(e.getOriginal().hasCapability(BOTW_PLAYERTRACKER_CAP, null))
				{
					//Copy stamina
					IPlayerTracker oldTracker = e.getOriginal().getCapability(BOTW_PLAYERTRACKER_CAP, null);
					IPlayerTracker newTracker = e.getEntityPlayer().getCapability(BOTW_PLAYERTRACKER_CAP, null);
					newTracker.readNBT(oldTracker.writeNBT());
					
					//Copy max health
					e.getEntityPlayer().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(
							e.getOriginal().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
					e.getEntityPlayer().setHealth((float)
							e.getEntityPlayer().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
				}
			}
		}
	}
	
	/**
	 * Provider for the {@link IPlayerTracker} capability.
	 * @see CapabilityItemTracker.Provider
	 */
	public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

		private final IPlayerTracker IPlayerTracker;

		public Provider(EntityPlayer player)
		{
			this(new PlayerTracker(player));
		}

		public Provider(IPlayerTracker playerTracker) {
			this.IPlayerTracker = playerTracker;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			return (NBTTagCompound)BOTW_PLAYERTRACKER_CAP.getStorage().writeNBT(BOTW_PLAYERTRACKER_CAP, IPlayerTracker, EnumFacing.NORTH);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			BOTW_PLAYERTRACKER_CAP.getStorage().readNBT(BOTW_PLAYERTRACKER_CAP, IPlayerTracker, EnumFacing.NORTH, nbt);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == BOTW_PLAYERTRACKER_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

			if (capability == BOTW_PLAYERTRACKER_CAP) {
				return BOTW_PLAYERTRACKER_CAP.cast(this.IPlayerTracker);
			}
			return null;
		}
	}
}
