package com.theredmajora.botw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.theredmajora.botw.capability.ITracker;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker.BOTWRenderAction;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;
import com.theredmajora.botw.gui.GuiSheikahSlate;
import com.theredmajora.botw.gui.GuiStaminaOverlay;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.item.ItemBOTWShield;
import com.theredmajora.botw.item.ItemParaglider;
import com.theredmajora.botw.item.ItemSheikahSlate;
import com.theredmajora.botw.packet.BOTWActionPacket;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.packet.UpdateClientPacket;
import com.theredmajora.botw.util.BOTWActionHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BOTWEvents
{
	private int ticksInAir = 0;
	private int playerCheckCD = 5;
	
	@SideOnly(Side.CLIENT)
	private boolean wasFirstPerson;
	
	@SideOnly(Side.CLIENT)
	private GuiStaminaOverlay staminaOverlay;
	
	private LinkedHashMap<UUID, ArrayList<ItemStack>> deathSlates = new LinkedHashMap<>();

	// entityId -> remainingTicks
	public HashMap<Integer, Integer> stasisEntityTimes = new HashMap<>();
	// entityId -> position
	public HashMap<Integer, Vec3d> stasisEntityPositions = new HashMap<>();
	// entityId -> rotation
	public HashMap<Integer, Vec2f> stasisEntityRotations = new HashMap<>();
	// entityId -> ticksExisted
	public HashMap<Integer, Integer> stasisEntityTicksExisted = new HashMap<>();
	// entityId -> motion
	public HashMap<Integer, Vec3d> stasisEntityMotions = new HashMap<>();
	// entityId -> player entityId
	public HashMap<Integer, Integer> stasisEntityPlayers = new HashMap<>();
	
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onKeyInput(KeyInputEvent event)
	{	
		KeyBinding[] keyBindings = BOTWKeyHandler.keyBindings;
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		World world = player.worldObj;
		
		Minecraft mc = Minecraft.getMinecraft();

		if (keyBindings[0].isPressed()) // Swap?
		{
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
		else if(keyBindings[1].isPressed())	//Throw
		{
			BOTWActionHelper.Carrying.clientThrowEntity(player);
		}
		else if(keyBindings[2].isPressed())	//Drop
		{
			BOTWActionHelper.Carrying.clientDropEntity(player);
		}
		else if(keyBindings[3].isPressed())	//Sheikah gui
		{
			player.openGui(BOTW.instance, GuiSheikahSlate.GUI_ID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		//Climbing & Backflip "hook"
		else if(mc.gameSettings.keyBindJump.isPressed())	//"Hook" for Minecraft jump KeyBinding
		{						
			BOTWActionHelper.Climbing.clientClimbJump(player, world);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer entity = (EntityPlayer)event.getEntityLiving();
			World world = entity.worldObj;

			if(!entity.onGround)
			{
				ticksInAir++;
				
				if(!world.isRemote)
				{
					BOTWActionHelper.Climbing.serverClimbTick(entity, world);
				}
				else if(entity == Minecraft.getMinecraft().thePlayer)
				{
					BOTWActionHelper.Climbing.clientClimbTick(entity, world);
				}
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() == ElementType.ALL)
		{
			if(staminaOverlay==null)
				staminaOverlay=new GuiStaminaOverlay(Minecraft.getMinecraft());
			
			if(staminaOverlay.shouldRender())
				staminaOverlay.render();
			
			EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
			ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
			if (stack != null && stack.getItem()==BOTWItems.sheikahSlate && stack.getTagCompound() != null && stack.getTagCompound().hasKey("mode")
					&& stack.getTagCompound().getString("mode").equals("stasis"))
			{
	    		if(!renderer.isShaderActive())
	    		{
	                renderer.loadShader(new ResourceLocation(BOTW.MODID, "shaders/post/stasis.json"));
	    		}
	    	}
	    	else
	    	{
	    		if(renderer.isShaderActive())
	    		{
	    			renderer.stopUseShader();
	    		}
	    	}
		}
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityPlayerMP)
		{			
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			if(!player.worldObj.isRemote)
			{
				if(player.getStatFile().readStat(StatList.LEAVE_GAME)==0 &&
						player.getStatFile().readStat(StatList.DEATHS)==0 &&
						player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE)==0)
				{
					if(player.worldObj.getWorldType()==BOTW.hyruleWorldType)
					{
						//First join in Hyrule
						player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6);
						
						IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
						playerTracker.setMaxStamina(1000);
						playerTracker.setStamina(1000);
						
						NBTTagCompound tag = new NBTTagCompound();
						tag.setBoolean("hasCamera", true);
						tag.setString("mode", "camera");
						ItemStack sheikahSlate = new ItemStack(BOTWItems.sheikahSlate);
						player.inventory.addItemStackToInventory(sheikahSlate);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if(event.side.isServer() && event.phase==Phase.START)
		{
			BOTWActionHelper.SheikahSlate.serverStasisTick(event.world,
					stasisEntityTimes,
					stasisEntityPositions,
					stasisEntityRotations,
					stasisEntityTicksExisted,
					stasisEntityMotions,
					stasisEntityPlayers);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) 
	{
		//every half a second will check to see if players are close to each other 
		if(event.side.isServer())
		{
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			LinkedList<ITracker> trackers=new LinkedList<>();
				
			IPlayerTracker playerTracker = event.player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
			IItemTracker itemTracker = event.player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
			
			BOTWActionHelper.Stamina.serverStaminaTick(player, playerTracker);
			
			if(itemTracker.getBackflipTime()>0)
			{
				itemTracker.setBackflipTime(itemTracker.getBackflipTime()-1);
				if(itemTracker.getBackflipTime()==0)
				{
					itemTracker.setRenderAction(BOTWRenderAction.NONE);
					event.player.setEntityInvulnerable(false);
				}
			}
			
			trackers.add(playerTracker);
			
			if(this.playerCheckCD == 0)
			{
				Random rand = new Random();
				World worldobj = player.worldObj;
				BlockPos pos1 = new BlockPos(player.getPosition().getX() + 50, player.getPosition().getY() + 50, player.getPosition().getZ() + 50);
				BlockPos pos2 = new BlockPos(player.getPosition().getX() - 50, player.getPosition().getY() - 50, player.getPosition().getZ() - 50);

				List<EntityPlayer> allPlayers = worldobj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos1, pos2));
				this.playerCheckCD = 2 + rand.nextInt(3);// the random will smooth the load over ticks 

				if(!allPlayers.isEmpty())
				{					
					for(int i=0; i<allPlayers.size(); i++)
					{
						EntityPlayer current=allPlayers.get(i);
						IItemTracker tracker = current.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);

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
							trackers.add(tracker);
						}
					}
				}
			}else
				this.playerCheckCD--;
			
			ITracker[] trackerArray = trackers.parallelStream().filter(t -> t.isDirty(player)).toArray(ITracker[]::new);
			UpdateClientPacket packet = new UpdateClientPacket(trackerArray);
			BOTWPacketHandler.INSTANCE.sendTo(packet, player);
			
			//Removes dirty flag for every transmitted tracker
			for (ITracker iTracker : trackerArray)
			{
				iTracker.setDirty(player, false);
			}
		}
		else
		{
			IItemTracker itemTracker = event.player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
			if(itemTracker.getBackflipTime()>0)
			{
				if(event.player==Minecraft.getMinecraft().thePlayer)
				{
					if(Minecraft.getMinecraft().gameSettings.thirdPersonView==0)
					{
						Minecraft.getMinecraft().gameSettings.thirdPersonView=1;
						wasFirstPerson=true;
					}
				}
				itemTracker.setBackflipTime(itemTracker.getBackflipTime()-1);
			}
			if(itemTracker.getBackflipTime()==0)
			{
				itemTracker.setRenderAction(BOTWRenderAction.NONE);
				if(event.player==Minecraft.getMinecraft().thePlayer)
				{
					if(wasFirstPerson)
					{
						Minecraft.getMinecraft().gameSettings.thirdPersonView=0;
						wasFirstPerson=false;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDie(PlayerDropsEvent event)
	{
		ArrayList<ItemStack> slates=new ArrayList<>();
		event.getDrops().forEach(item->{if(item.getEntityItem().getItem()==BOTWItems.sheikahSlate) slates.add(item.getEntityItem());});
		event.getDrops().removeIf(item->item.getEntityItem().getItem()==BOTWItems.sheikahSlate);
		deathSlates.put(event.getEntityPlayer().getUniqueID(), slates);
	}
	
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		for(ItemStack stack : deathSlates.get(event.getOriginal().getUniqueID()))
		{
			if(stack.getItem()==BOTWItems.sheikahSlate)
			{
				event.getEntityPlayer().inventory.addItemStackToInventory(stack);
			}
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
					if(stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemShield || stack.getItem() instanceof ItemBOTWShield)
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
	
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event)
	{
		EntityLivingBase livingBase=event.getEntityLiving();
		
		// A blocking item is only damaged if it's type is net.minecraft.item.ItemShield so we've to do this manually here.
		if(livingBase.isActiveItemStackBlocking())
		{
			ItemStack stack=livingBase.getActiveItemStack();
			if(stack.getItem() instanceof ItemBOTWShield)
			{
				stack.damageItem(1, livingBase);
			}
		}
		
		//FIXME: Move!!!
		if(!livingBase.worldObj.isRemote)
		{
			BOTWActionHelper.SheikahSlate.serverStasisAttack(event, livingBase, stasisEntityMotions);
		}
		else
		{
			BOTWActionHelper.SheikahSlate.clientStasisAttack(event, livingBase);
		}
	}
	
	@SubscribeEvent
	public void onPlayerDestroyItem(PlayerDestroyItemEvent event)
	{
		ItemStack stack=event.getOriginal();
		if(stack.getItem() instanceof ItemBOTWShield)
		{
			EntityPlayer player=event.getEntityPlayer();
			if(player!=null)
			{
				if(!player.worldObj.isRemote)
				{
					WorldServer server=(WorldServer) player.worldObj;
					double posX = player.posX;
					double posY = player.posY;
					double posZ = player.posZ;
					
			        server.spawnParticle(EnumParticleTypes.getByName("blue_splash"), posX, posY, posZ, 1000, 0, 0, 0, 0, new int[0]);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.worldObj.isRemote)	//Never execute this on server side. Input is handled by client.
			{
				if(player.moveForward<0)
				{
					if(player.isActiveItemStackBlocking())
					{					
						BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.BACKFLIP, 0);
						BOTWPacketHandler.INSTANCE.sendToServer(packet);

						float yaw = player.rotationYaw;
						double x = + Math.sin((float) Math.toRadians(yaw))*0.5;
						double z = - Math.cos((float) Math.toRadians(yaw))*0.5;

						player.setVelocity(x, 0.5, z);
					}
				}
			}
		}
	}
}
