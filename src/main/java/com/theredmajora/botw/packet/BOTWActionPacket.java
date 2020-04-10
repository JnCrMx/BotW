package com.theredmajora.botw.packet;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.util.BOTWActionHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
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
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			World world = player.worldObj;
			
			if(message.action==BOTWPlayerAction.THROW_ENTITY)
			{
				BOTWActionHelper.Carrying.serverThrowEntity(player);
			}
			else if(message.action==BOTWPlayerAction.DROP_ENTITY)
			{
				BOTWActionHelper.Carrying.serverDropEntity(player);
			}
			else if(message.action==BOTWPlayerAction.CLIMB_UP)
			{
				BOTWActionHelper.Climbing.serverClimbUp(player, world);
			}
			else if(message.action==BOTWPlayerAction.CLIMB_DROP)
			{	
				BOTWActionHelper.Climbing.serverClimbDrop(player, world);
			}
			else if(message.action==BOTWPlayerAction.CLIMB_JUMP)
			{
				BOTWActionHelper.Climbing.serverClimbJump(player, world);
			}
			else if(message.action==BOTWPlayerAction.SELECT_SHEIKAH_RUNE)
			{
				BOTWActionHelper.SheikahSlate.serverRuneSelect(player, message.argument);
			}
			else if(message.action==BOTWPlayerAction.STASIS_ENTITY)
			{
				BOTWActionHelper.SheikahSlate.serverStasisUse(player, world, message.argument);
			}
			else if(message.action==BOTWPlayerAction.BACKFLIP)
			{
				BOTWActionHelper.FightMoves.serverBackflip(player);
			}
			else
			{
				BOTW.logger.warn("Unknown BOTWPlayerAction "+message.action+" with argument "+message.argument+"!");
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
		STASIS_ENTITY,
		BACKFLIP
	}
}
