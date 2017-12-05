package com.theredmajora.botw.packet;

import java.util.UUID;

import com.theredmajora.botw.items.BOTWItems;
import com.theredmajora.botw.items.ItemSheikahSlate;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdatePlayerRenderPacket implements IMessage, IMessageHandler<UpdatePlayerRenderPacket, IMessage>
{
	private int id;
	private boolean hasSheikahSlate;
	
	public UpdatePlayerRenderPacket() {}
	
	public UpdatePlayerRenderPacket(EntityPlayer player) 
	{
		this.id=player.getEntityId();
		this.hasSheikahSlate=false;
		for (ItemStack stack : player.inventory.mainInventory)
        {
        	if (stack != null && stack.getItem() instanceof ItemSheikahSlate)
    		{
        		this.hasSheikahSlate=true;
    		}
        }
	}
	
	@Override
	public IMessage onMessage(UpdatePlayerRenderPacket message, MessageContext ctx)
	{		
		if(ctx.side==Side.CLIENT)
		{
			if(Minecraft.getMinecraft().thePlayer==null)
				return null;
			
			if(message.id==Minecraft.getMinecraft().thePlayer.getEntityId())
				return null;
			
			EntityPlayer player=(EntityPlayer) Minecraft.getMinecraft().theWorld.getEntityByID(message.id);
			
			if(player==null)
				return null;
			
			if(message.hasSheikahSlate)
			{
				player.inventory.mainInventory[33]=new ItemStack(BOTWItems.sheikah_slate, 1);
			}
			else
			{
				player.inventory.mainInventory[33]=null;
			}
		}
		else
		{
			BOTWPacketHandler.INSTANCE.sendToAll(message);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.id=buf.readInt();
		this.hasSheikahSlate=buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(id);
		buf.writeBoolean(hasSheikahSlate);
	}

}
