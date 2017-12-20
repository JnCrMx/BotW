package com.theredmajora.botw;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.items.ItemParaglider;
import com.theredmajora.botw.items.ItemSheikahSlate;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.packet.itemTracker.UpdateClientPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BOTWEvents
{
	private int ticksInAir = 0;
	private int playerCheckCD = 10;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onEvent(KeyInputEvent event)
	{
		KeyBinding[] keyBindings = BOTWKeyHandler.keyBindings;

		if (keyBindings[0].isPressed()) 
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.inventory.getCurrentItem();

			if(player.inventory.offHandInventory[0] == null)
			{
				player.inventory.offHandInventory[0] = ItemStack.copyItemStack(stack);
				player.inventory.deleteStack(stack);
			}
			else
			{
				ItemStack temp = ItemStack.copyItemStack(stack);
				stack = ItemStack.copyItemStack(player.inventory.offHandInventory[0]);
				player.inventory.offHandInventory[0] = ItemStack.copyItemStack(temp);
			}
		}
	}

	@SubscribeEvent
	public void livingUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer entity = (EntityPlayer)event.getEntity();

			if(!entity.onGround)
			{
				ticksInAir++;
			}
			else
			{
				ticksInAir = 0;
			}

			if(entity.isAirBorne && ticksInAir >= 60 && entity.inventory.getCurrentItem() != null && entity.inventory.getCurrentItem().getItem() instanceof ItemBow && entity.getItemInUseCount() >= 1)
			{
				if(entity.getItemInUseCount() <= 1)
				{
					ticksInAir = 0;
				}

				AxisAlignedBB axisalignedbb = new AxisAlignedBB(entity.posX - 15, entity.posY - 15, entity.posZ - 15, entity.posX + 15, entity.posY + 15, entity.posZ + 15);
				List<Entity> nearby = entity.worldObj.<Entity>getEntitiesWithinAABB(Entity.class, axisalignedbb);

				for (Entity entityFound: nearby)
				{
					if (entityFound instanceof Entity)
					{
						if(entityFound instanceof EntityArrow)
						{
							if(!(((EntityArrow)entityFound).shootingEntity instanceof EntityPlayer))
							{
								entityFound.motionX /= 2.5;
								entityFound.motionY /= 2.5;
								entityFound.motionZ /= 2.5;
							}
							else
							{
								/**I think it's because I have to override when the arrow shoots.
								 * There's a forge event for that.
								 * From what I found out, the arrow gets motionY from the player and shoots from that,
								 * so when motionY is getting divided, so is the arrows motion.
								 * 
								 * Look in the arrow class for clues!
								 */
							}
						}
						else
						{
							entityFound.motionX /= 2.5;
							entityFound.motionY /= 2.5;
							entityFound.motionZ /= 2.5;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.PlayerTickEvent event) 
	{
		//every half a second will check to see if players are close to each other 
		if(event.side.isServer())
		{
			if(this.playerCheckCD == 0)
			{
				Random rand = new Random();
				EntityPlayer player = event.player;
				World worldobj = event.player.worldObj;
				BlockPos pos = new BlockPos(player.getPosition().getX() + 50, player.getPosition().getY() + 50, player.getPosition().getZ() + 50);

				List<EntityPlayer> allPlayers = worldobj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(player.getPosition(), pos));
				this.playerCheckCD = 10 + rand.nextInt(10);// the random will smooth the load over ticks 

				if(!allPlayers.isEmpty())
				{
					IItemTracker[] trackers = new IItemTracker[allPlayers.size()];
					
					for(int i=0; i<trackers.length; i++)
					{
						EntityPlayer current=allPlayers.get(i);
						IItemTracker tracker = current.getCapability(CapabilityItemTracker.BOTW_CAP, null);

						if(tracker != null)
						{
							//if(tracker.getPacketState() == ENUMClientPacketState.OLD)
							{
								Object[] hasRender = this.generateItemTrackerInformation(current);
								
								tracker.setShouldRenderSlate((boolean) hasRender[0]);
								tracker.setShouldRenderGlider((boolean) hasRender[1]);
								tracker.setRenderingItemStacks(((List<ItemStack>) hasRender[2]));
								tracker.setArrowCount((int) hasRender[3]);
								
								//tracker.setPacketState(ENUMClientPacketState.NEW);
							}
						}
						trackers[i]=tracker;
					}
					UpdateClientPacket packet = new UpdateClientPacket(trackers);
					BOTWPacketHandler.INSTANCE.sendToAll(packet);
				}
			}else
				this.playerCheckCD--;
		}
	}

	private Object[] generateItemTrackerInformation(EntityPlayer player)
	{
		Object render[] = {false, false, new ArrayList<ItemStack>(), 0};

		for(ItemStack stack : player.inventory.mainInventory)
		{
			if(stack != null)
			{
				if(player.inventory.getCurrentItem() != stack)
				{
					if(stack.getItem() instanceof  ItemSheikahSlate)
					{
						render[0] = true;
					}
					if(stack.getItem() instanceof  ItemParaglider)
					{
						render[1] = true;
					}
					if(stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemShield)
					{
						((ArrayList)render[2]).add(stack);
					}
				}
				if(stack.getItem() instanceof ItemArrow)
				{
					render[3]=((int)render[3])+stack.stackSize;
				}
			}
		}
		if(player.inventory.getCurrentItem() != null)
		{
			if(player.inventory.getCurrentItem().getItem() instanceof ItemBow || player.inventory.getCurrentItem().getItem() instanceof ItemArrow)
			{
				if(((int)render[3])>0)
				{
					render[3]=((int)render[3])-1;
				}
			}
		}

		return render;
	}
}
