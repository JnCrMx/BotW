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
import com.theredmajora.botw.entity.IEntityCarriable;
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
import com.theredmajora.botw.proxy.ClientProxy;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
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
	private int playerCheckCD = 10;
	
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
			if(!player.getPassengers().isEmpty())
			{
				for (Entity entity : player.getPassengers())
				{
					if(entity instanceof IEntityCarriable)
					{
						IEntityCarriable carriable=(IEntityCarriable)entity;
						if(carriable.isThrowable())
						{
							BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.THROW_ENTITY, 0);
							BOTWPacketHandler.INSTANCE.sendToServer(packet);
							
							carriable.throwEntity(player);
						}
					}
				}
			}
		}
		else if(keyBindings[2].isPressed())	//Drop
		{
			if(!player.getPassengers().isEmpty())
			{
				for (Entity entity : player.getPassengers())
				{
					if(entity instanceof IEntityCarriable)
					{
						IEntityCarriable carriable=(IEntityCarriable)entity;
						if(carriable.isDroppable())
						{
							BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.DROP_ENTITY, 0);
							BOTWPacketHandler.INSTANCE.sendToServer(packet);
							
							carriable.dropEntity(player);
						}
					}
				}
			}
		}
		else if(keyBindings[3].isPressed())	//Sheikah gui
		{
			player.openGui(BOTW.instance, GuiSheikahSlate.GUI_ID, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
		//Climbing "hook"
		else if(Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed())	//"Hook" for Minecraft jump KeyBinding
		{						
			if(!player.onGround)
			{		
				float yaw = player.rotationYaw;
				double x = player.posX - MathHelper.sin((float) Math.toRadians(yaw))*0.6;
				double z = player.posZ + MathHelper.cos((float) Math.toRadians(yaw))*0.6;
					
				BlockPos pos = new BlockPos(x, player.posY, z);
				IBlockState state1 = world.getBlockState(pos);
				IBlockState state2 = world.getBlockState(pos.add(0, 1, 0));
				if(state1.isFullBlock() || state2.isFullBlock())
				{	
					IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
					if(!playerTracker.isExhausted() && !player.isSneaking())
					{	
						BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.CLIMB_JUMP, 0);
						BOTWPacketHandler.INSTANCE.sendToServer(packet);
						
						playerTracker.setStamina(playerTracker.getStamina()-100);
						player.setVelocity(player.motionX, 0.5, player.motionZ);
					}
				}
			}
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
				
				//Climb physics
				float yaw = entity.rotationYaw;
				double x = entity.posX - Math.sin((float) Math.toRadians(yaw))*0.75;
				double z = entity.posZ + Math.cos((float) Math.toRadians(yaw))*0.75;
					
				BlockPos pos = new BlockPos(x, entity.posY, z);
				IBlockState state1 = world.getBlockState(pos);
				IBlockState state2 = world.getBlockState(pos.add(0, 1, 0));
				if(state1.isFullBlock() || state2.isFullBlock())
				{			
					IPlayerTracker playerTracker = entity.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
					
					boolean surface = world.getPrecipitationHeight(entity.getPosition()).subtract(entity.getPosition()).getY()<=0;
					boolean raining = world.getBiome(entity.getPosition()).canRain() && world.isRaining() && surface;	//Everybody loves rain in BotW!
					
					if(!playerTracker.isExhausted() && !raining)
					{
						entity.fallDistance=1.0f;

						if(entity.isSneaking())
						{
							playerTracker.setStamina(playerTracker.getStamina()-5);
							entity.setVelocity(entity.motionX, -0.1, entity.motionZ);
						}
						else
						{
							if(entity.moveForward>0 && entity.motionY<0.1)	//Won't be called serverside
							{
								BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.CLIMB_UP, 0);
								BOTWPacketHandler.INSTANCE.sendToServer(packet);			
									
								playerTracker.setStamina(playerTracker.getStamina()-10);
								entity.setVelocity(entity.motionX, 0.05, entity.motionZ);
							}
							else if(entity.moveForward<0)	//Won't be called serverside
							{
								BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.CLIMB_DROP, 0);
								BOTWPacketHandler.INSTANCE.sendToServer(packet);
								
								//Jump away
								entity.setVelocity(MathHelper.sin((float) Math.toRadians(yaw))*0.2, 0, -MathHelper.cos((float) Math.toRadians(yaw))*0.2);
							}
							else if(entity.motionY<0)	//Will be called clientside and serverside
							{								
								//Don't fall
								entity.setVelocity(entity.motionX, 0, entity.motionZ);
								
								entity.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null).setRenderAction(BOTWRenderAction.NONE);
							}
						}
						
						//Code for ItemDebugTool
						if(entity.getHeldItemMainhand()!=null && entity.getHeldItemMainhand().getItem()==BOTWItems.debugTool)
						{
							world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
							world.setBlockState(pos.add(0, 1, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK));
						}
						if(entity.getHeldItemOffhand()!=null && entity.getHeldItemOffhand().getItem()==BOTWItems.debugTool)
						{
							world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE));
							world.setBlockState(pos.add(0, 1, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE));
						}
					}
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
				if(player.getStatFile().readStat(StatList.LEAVE_GAME)==0 && player.getStatFile().readStat(StatList.DEATHS)==0)
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
			//Exceptions may occur if you remove entries from a map/list you're currently iterating over
			ArrayList<Integer> toRemove = new ArrayList<>();
			
			for(int entityId : stasisEntityTimes.keySet())
			{
				int remainingTicks = stasisEntityTimes.get(entityId);
				Entity entity = event.world.getEntityByID(entityId);
				
				if(entity!=null && event.world!=null)
				{
					EntityPlayer player = (EntityPlayer) event.world.getEntityByID(stasisEntityPlayers.get(entityId));
					IPlayerTracker tracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
					
					if(remainingTicks>0)
					{
						entity.setNoGravity(true);
						entity.setSilent(true);
						if(entity instanceof EntityLiving)
						{
							EntityLiving living = (EntityLiving)entity;
							
							living.setNoAI(true);
						}
						
						Vec3d position = stasisEntityPositions.get(entityId);
						Vec2f rotation = stasisEntityRotations.get(entityId);
						// rotation.x = yaw
						// rotation.y = pitch
						entity.setPositionAndRotation(position.xCoord, position.yCoord, position.zCoord, rotation.x, rotation.y);
						
						entity.ticksExisted = stasisEntityTicksExisted.get(entityId);
						
						remainingTicks--;
						stasisEntityTimes.replace(entityId, remainingTicks);
						tracker.setCurrentStasisTime(remainingTicks);
					}
					else
					{
						entity.setGlowing(false);
						entity.setNoGravity(false);
						entity.setSilent(false);
						
						if(entity instanceof EntityLiving)
						{
							EntityLiving living = (EntityLiving)entity;
							
							living.setNoAI(false);
						}
						
						Vec3d motion = stasisEntityMotions.get(entityId);
						entity.motionX = motion.xCoord;
						entity.motionY = motion.yCoord;
						entity.motionZ = motion.zCoord;

						toRemove.add(entityId);
						tracker.setCurrentStasisEntity(-1);
					}
				}
			}
			
			
			
			toRemove.forEach(entityId->
			{
				stasisEntityTimes.remove(entityId);
				stasisEntityPositions.remove(entityId);
				stasisEntityRotations.remove(entityId);
				stasisEntityTicksExisted.remove(entityId);
				stasisEntityMotions.remove(entityId);
			});
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
			
			if(playerTracker.getStamina()>=playerTracker.getMaxStamina())
			{
				playerTracker.setStamina(playerTracker.getMaxStamina());
				playerTracker.setExhausted(false);
			}
			if(playerTracker.getStamina()<=0)
			{
				playerTracker.setStamina(0);
				playerTracker.setExhausted(true);
			}
			
			if(player.isSprinting() && !playerTracker.isExhausted())
			{
				playerTracker.setStamina(playerTracker.getStamina()-10);
			}
			else if(playerTracker.getStamina()<playerTracker.getMaxStamina() && player.onGround)
			{
				if(playerTracker.isExhausted())
				{
					player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 1, 1, false, false));
					playerTracker.setStamina(playerTracker.getStamina()+5);
				}
				else
				{
					playerTracker.setStamina(playerTracker.getStamina()+10);
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
				this.playerCheckCD = 10 + rand.nextInt(10);// the random will smooth the load over ticks 

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
		
		if(!livingBase.worldObj.isRemote)
		{
			if(stasisEntityMotions.containsKey(livingBase.getEntityId()))
			{
				if(event.getSource().getDamageLocation()!=null)
				{
					event.setCanceled(true);
						
					Vec3d attacker = event.getSource().getDamageLocation();
					Vec3d target = livingBase.getPositionVector();
						
					Vec3d hit = attacker.subtract(target).normalize();
					Vec3d direction = hit.normalize().scale(-1.0d);
					Vec3d motion = direction.scale(event.getAmount()*0.05d);
					
					Vec3d oldMotion = stasisEntityMotions.get(livingBase.getEntityId());
					Vec3d newMotion = oldMotion.add(motion);
					
					stasisEntityMotions.replace(livingBase.getEntityId(), newMotion);
					
					System.out.println(livingBase.worldObj.isRemote);
					System.out.println(stasisEntityMotions);
				}
			}
		}
		else
		{
			if(BOTW.proxy instanceof ClientProxy)
			{
				ClientProxy proxy = (ClientProxy) BOTW.proxy;
				if(proxy.stasisEntityMotions.containsKey(livingBase.getEntityId()))
				{					
					Vec3d attacker = event.getSource().getDamageLocation();
					Vec3d target = livingBase.getPositionVector();
					
					if(target==null || attacker==null)
						return;
						
					Vec3d hit = attacker.subtract(target).normalize();
					Vec3d direction = hit.normalize().scale(-1.0d);
					Vec3d motion = direction.scale(event.getAmount()*0.05d);
					
					Vec3d oldMotion = proxy.stasisEntityMotions.get(livingBase.getEntityId());
					Vec3d newMotion = oldMotion.add(motion);
					
					proxy.stasisEntityMotions.replace(livingBase.getEntityId(), newMotion);
					
					System.out.println(livingBase.worldObj.isRemote);
					System.out.println(stasisEntityMotions);
				}
			}
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
}
