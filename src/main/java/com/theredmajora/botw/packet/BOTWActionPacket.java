package com.theredmajora.botw.packet;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker.BOTWRenderAction;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;
import com.theredmajora.botw.entity.EntityBomb;
import com.theredmajora.botw.entity.IEntityCarriable;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Just used by the client to send the server client events
 * @author JCM
 *
 */
public class BOTWActionPacket implements IMessage, IMessageHandler<BOTWActionPacket, IMessage>
{
	/**
	 * entityId of the <b>player</b>
	 */
	private BOTWPlayerAction action;
	private int argument;
	
	public BOTWActionPacket() {	}
	
	/**
	 * @param entityId Entity id of the <b>player</b>
	 * @param action Action to perform
	 */
	public BOTWActionPacket(BOTWPlayerAction action, int argument)
	{
		this.action=action;
		this.argument=argument;
	}
	
	@Override
	public IMessage onMessage(BOTWActionPacket message, MessageContext ctx)
	{
		if(ctx.side.isServer())
		{
			EntityPlayer player = (EntityPlayer) ctx.getServerHandler().playerEntity;
			World world = player.worldObj;
			
			if(message.action==BOTWPlayerAction.THROW_ENTITY)
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
			else if(message.action==BOTWPlayerAction.DROP_ENTITY)
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
			else if(message.action==BOTWPlayerAction.CLIMB_UP)
			{
				//Climb physics
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
			else if(message.action==BOTWPlayerAction.CLIMB_DROP)
			{		
				//Climb physics
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
			else if(message.action==BOTWPlayerAction.CLIMB_JUMP)
			{
				//Climb physics
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
			else if(message.action==BOTWPlayerAction.SELECT_SHEIKAH_RUNE)
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
					switch (message.argument)
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
			else if(message.action==BOTWPlayerAction.STASIS_ENTITY)
			{
				//TODO: Check cooldown & rune level
				
				Entity entity = world.getEntityByID(message.argument);
				if(entity!=null)
				{
					BOTW.eventHandler.stasisEntityTimes.put(message.argument, 200);
					BOTW.eventHandler.stasisEntityPositions.put(message.argument, new Vec3d(entity.posX, entity.posY, entity.posZ));
					BOTW.eventHandler.stasisEntityRotations.put(message.argument, new Vec2f(entity.rotationYaw, entity.rotationPitch));
					BOTW.eventHandler.stasisEntityTicksExisted.put(message.argument, entity.ticksExisted);
					BOTW.eventHandler.stasisEntityMotions.put(message.argument, new Vec3d(0, 0, 0));
					BOTW.eventHandler.stasisEntityPlayers.put(message.argument, player.getEntityId());
					
					IPlayerTracker tracker=player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
					tracker.setCurrentStasisEntity(message.argument);
					tracker.setCurrentStasisTime(200);
					System.out.println(tracker.isDirty(player));
				}
			}
			else
			{
				System.out.println("Unknown BOTWPlayerAction "+message.action+" with argument "+message.argument+"!");
			}
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.action=BOTWPlayerAction.values()[buf.readInt()];
		this.argument=buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(action.ordinal());
		buf.writeInt(argument);
	}
	
	public static enum BOTWPlayerAction
	{
		THROW_ENTITY,
		DROP_ENTITY,
		CLIMB_UP,
		CLIMB_DROP,
		CLIMB_JUMP,
		SELECT_SHEIKAH_RUNE,
		STASIS_ENTITY
	}
}
