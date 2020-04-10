package com.theredmajora.botw.util;

import java.util.ArrayList;
import java.util.Map;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker.BOTWRenderAction;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;
import com.theredmajora.botw.entity.IEntityCarriable;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.packet.BOTWActionPacket;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;
import com.theredmajora.botw.proxy.ClientProxy;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class BOTWActionHelper
{	
	public static class Carrying
	{
		public static void serverThrowEntity(EntityPlayer player)
		{
			for (Entity entity : player.getPassengers())
			{
				if(entity instanceof IEntityCarriable)
				{
					IEntityCarriable carriable=(IEntityCarriable)entity;
					if(carriable.isThrowable())
					{
						carriable.throwEntity(player);
					}
				}
			}
		}
		
		public static void clientThrowEntity(EntityPlayer player)
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
		
		public static void serverDropEntity(EntityPlayer player)
		{
			for (Entity entity : player.getPassengers())
			{
				if(entity instanceof IEntityCarriable)
				{
					IEntityCarriable carriable=(IEntityCarriable)entity;
					if(carriable.isDroppable())
					{
						carriable.dropEntity(player);
					}
				}
			}
		}
		
		public static void clientDropEntity(EntityPlayer player)
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
	
	public static class Climbing
	{
		public static void serverClimbUp(EntityPlayer player, World world)
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
						playerTracker.setStamina(playerTracker.getStamina()-10);
						player.setVelocity(player.motionX, 0.05, player.motionZ);
						
						player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null).setRenderAction(BOTWRenderAction.CLIMB_UP);
					}
				}
			}
		}
		
		public static void serverClimbDrop(EntityPlayer player, World world)
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
					if(!playerTracker.isExhausted())
					{
						//Jump away
						player.setVelocity(MathHelper.sin((float) Math.toRadians(yaw))*0.2, 0, -MathHelper.cos((float) Math.toRadians(yaw))*0.2);
					}
				}
			}
		}
		
		public static void serverClimbJump(EntityPlayer player, World world)
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
						playerTracker.setStamina(playerTracker.getStamina()-100);
						player.setVelocity(player.motionX, 0.5, player.motionZ);
					}
				}
			}
		}
		
		public static void clientClimbJump(EntityPlayer player, World world)
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
		
		public static void serverClimbTick(EntityPlayer player, World world)
		{
			//Climb physics
			float yaw = player.rotationYaw;
			double x = player.posX - Math.sin((float) Math.toRadians(yaw))*0.75;
			double z = player.posZ + Math.cos((float) Math.toRadians(yaw))*0.75;
				
			BlockPos pos = new BlockPos(x, player.posY, z);
			IBlockState state1 = world.getBlockState(pos);
			IBlockState state2 = world.getBlockState(pos.add(0, 1, 0));
			if(state1.isFullBlock() || state2.isFullBlock())
			{			
				IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
				
				boolean surface = world.getPrecipitationHeight(player.getPosition()).subtract(player.getPosition()).getY()<=0;
				boolean raining = world.getBiome(player.getPosition()).canRain() && world.isRaining() && surface;	//Everybody loves rain in BotW!
				
				if(!playerTracker.isExhausted() && !raining)
				{
					player.fallDistance=1.0f;

					if(player.isSneaking())
					{
						playerTracker.setStamina(playerTracker.getStamina()-5);
						player.setVelocity(player.motionX, -0.1, player.motionZ);
					}
					else
					{
						if(player.motionY<0)	//Will be called clientside and serverside
						{								
							//Don't fall
							player.setVelocity(player.motionX, 0, player.motionZ);
							
							player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null).setRenderAction(BOTWRenderAction.NONE);
						}
					}
					
					//Code for ItemDebugTool
					if(player.getHeldItemMainhand()!=null && player.getHeldItemMainhand().getItem()==BOTWItems.debugTool)
					{
						world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED));
						world.setBlockState(pos.add(0, 1, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK));
					}
					if(player.getHeldItemOffhand()!=null && player.getHeldItemOffhand().getItem()==BOTWItems.debugTool)
					{
						world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE));
						world.setBlockState(pos.add(0, 1, 0), Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE));
					}
				}
			}
		}
		
		public static void clientClimbTick(EntityPlayer player, World world)
		{
			//Climb physics
			float yaw = player.rotationYaw;
			double x = player.posX - Math.sin((float) Math.toRadians(yaw))*0.75;
			double z = player.posZ + Math.cos((float) Math.toRadians(yaw))*0.75;
				
			BlockPos pos = new BlockPos(x, player.posY, z);
			IBlockState state1 = world.getBlockState(pos);
			IBlockState state2 = world.getBlockState(pos.add(0, 1, 0));
			if(state1.isFullBlock() || state2.isFullBlock())
			{			
				IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
				
				boolean surface = world.getPrecipitationHeight(player.getPosition()).subtract(player.getPosition()).getY()<=0;
				boolean raining = world.getBiome(player.getPosition()).canRain() && world.isRaining() && surface;	//Everybody loves rain in BotW!
				
				if(!playerTracker.isExhausted() && !raining)
				{
					player.fallDistance=1.0f;

					if(player.isSneaking())
					{
						player.setVelocity(player.motionX, -0.1, player.motionZ);
					}
					else
					{
						if(player.moveForward>0 && player.motionY<0.1)	//Won't be called serverside
						{
							BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.CLIMB_UP, 0);
							BOTWPacketHandler.INSTANCE.sendToServer(packet);			
								
							player.setVelocity(player.motionX, 0.05, player.motionZ);
						}
						else if(player.moveForward<0)	//Won't be called serverside
						{
							BOTWActionPacket packet=new BOTWActionPacket(BOTWPlayerAction.CLIMB_DROP, 0);
							BOTWPacketHandler.INSTANCE.sendToServer(packet);
							
							//Jump away
							player.setVelocity(MathHelper.sin((float) Math.toRadians(yaw))*0.2, 0, -MathHelper.cos((float) Math.toRadians(yaw))*0.2);
						}
						else if(player.motionY<0)	//Will be called clientside and serverside
						{								
							//Don't fall
							player.setVelocity(player.motionX, 0, player.motionZ);
							
							player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null).setRenderAction(BOTWRenderAction.NONE);
						}
					}
				}
			}
		}
	}
	
	public static class SheikahSlate
	{
		public static void serverRuneSelect(EntityPlayer player, int rune)
		{
			ItemStack slate = null;
			//Search for sheikah slate	//TODO: Move this maybe?
			for (ItemStack stack : player.inventory.mainInventory)
			{
				if(stack != null && stack.getItem() == BOTWItems.sheikahSlate)
				{
					slate=stack;
					break;
				}
			}
			if(slate != null)
			{
				String mode = "camera";
				switch (rune)
				{
					case 0:
						mode="roundBomb";
						break;
					case 1:
						mode="squareBomb";
						break;
					case 2:
						mode="magnesis";
						break;
					case 3:
						mode="stasis";
						break;
					case 4:
						mode="cryonis";
						break;
					case 5:
						mode="camera";
						break;
					case 6:
						mode="masterCycleZero";
						break;
					default:
						break;
				}
				
				// Do not check rune level here; check it on execution of rune
				
				if(!slate.hasTagCompound())
					slate.setTagCompound(new NBTTagCompound());
				
				NBTTagCompound tag = slate.getTagCompound();
				tag.setString("mode", mode);
			}
		}

		public static void serverStasisUse(EntityPlayer player, World world, int entityId)
		{
			//TODO: Check cooldown & rune level
			
			Entity entity = world.getEntityByID(entityId);
			if(entity!=null && isStasisTarget(entity))
			{
				BOTW.eventHandler.stasisEntityTimes.put(entityId, getStasisTicks((EntityLiving) entity));
				BOTW.eventHandler.stasisEntityPositions.put(entityId, new Vec3d(entity.posX, entity.posY, entity.posZ));
				BOTW.eventHandler.stasisEntityRotations.put(entityId, new Vec2f(entity.rotationYaw, entity.rotationPitch));
				BOTW.eventHandler.stasisEntityTicksExisted.put(entityId, entity.ticksExisted);
				BOTW.eventHandler.stasisEntityMotions.put(entityId, new Vec3d(0, 0, 0));
				BOTW.eventHandler.stasisEntityPlayers.put(entityId, player.getEntityId());
				
				IPlayerTracker tracker=player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
				tracker.setCurrentStasisEntity(entityId);
				tracker.setCurrentStasisTime(getStasisTicks((EntityLiving) entity));
			}
		}
		
		public static void serverStasisTick(World world,
				//TODO: Move fields somewhere else
				Map<Integer, Integer> stasisEntityTimes,
				Map<Integer, Vec3d> stasisEntityPositions,
				Map<Integer, Vec2f> stasisEntityRotations,
				Map<Integer, Integer> stasisEntityTicksExisted,
				Map<Integer, Vec3d> stasisEntityMotions,
				Map<Integer, Integer> stasisEntityPlayers)
		{
			//Exceptions may occur if you remove entries from a map/list you're currently iterating over
			ArrayList<Integer> toRemove = new ArrayList<>();
			
			for(int entityId : stasisEntityTimes.keySet())
			{
				int remainingTicks = stasisEntityTimes.get(entityId);
				Entity entity = world.getEntityByID(entityId);
				
				if(entity!=null && world!=null)
				{
					EntityPlayer player = (EntityPlayer) world.getEntityByID(stasisEntityPlayers.get(entityId));
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
		
		public static boolean isStasisTarget(Entity entity)
		{
			return isStasisTarget(entity.getClass());
		}

		public static boolean isStasisTarget(Class<?> clazz)
		{
			return IMob.class.isAssignableFrom(clazz);
		}
		
		public static int getStasisTicks(EntityLiving entity)
		{
			double health = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
			double time = 6 * Math.pow(health, -0.3);	//time(health) = 6*health^(-0.3)
			return (int)time*30;
		}
		
		public static void serverStasisAttack(LivingAttackEvent event, EntityLivingBase livingBase,
				Map<Integer, Vec3d> stasisEntityMotions)
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
				}
			}
		}
		
		public static void clientStasisAttack(LivingAttackEvent event, EntityLivingBase livingBase)
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
				}
			}
		}
	}
	
	public static class FightMoves
	{
		public static void serverBackflip(EntityPlayer player)
		{
			if(player.hurtResistantTime==0)
			{					
				player.setEntityInvulnerable(true);
				
				IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
				tracker.setRenderAction(BOTWRenderAction.BACKFLIP);
				tracker.setBackflipTime(36);

				float yaw = player.rotationYaw;
				double x = + Math.sin((float) Math.toRadians(yaw))*0.5;
				double z = - Math.cos((float) Math.toRadians(yaw))*0.5;

				player.setVelocity(x, 0.5, z);
			}
		}
	}
	
	public static class Stamina
	{
		public static void serverStaminaTick(EntityPlayer player, IPlayerTracker playerTracker)
		{
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
		}
	}
}
