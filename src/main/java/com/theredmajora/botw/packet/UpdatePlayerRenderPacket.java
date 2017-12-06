package com.theredmajora.botw.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.theredmajora.botw.items.BOTWItems;
import com.theredmajora.botw.items.ItemBOTWShield;
import com.theredmajora.botw.items.ItemSheikahSlate;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdatePlayerRenderPacket implements IMessage, IMessageHandler<UpdatePlayerRenderPacket, IMessage>
{
	private int id;
	private boolean hasSheikahSlate;
	private boolean isHandgliding;
	private ArrayList<ItemStack> backItems;
	
	public UpdatePlayerRenderPacket() {}
	
	public UpdatePlayerRenderPacket(EntityPlayer player) 
	{
		this.id=player.getEntityId();
		this.hasSheikahSlate=false;
		this.backItems=new ArrayList<>();
		for (ItemStack stack : player.inventory.mainInventory)
        {
			if(stack != null)
			{
				ItemStack current = player.inventory.getCurrentItem();
        		if(!(stack == current))
        		{
		        	if(stack.getItem() instanceof ItemSheikahSlate)
		    		{
		        		this.hasSheikahSlate=true;
		    		}
		        	else if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow)
					{
		        		this.backItems.add(stack);
					}
					else if(stack.getItem() instanceof ItemShield || stack.getItem() instanceof ItemBOTWShield)
					{
			        	this.backItems.add(stack);
					}
					else if(stack.getItem() instanceof ItemArrow)
					{
						this.backItems.add(stack);
					}
        		}
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
			
			for(int i=10;i<player.inventory.mainInventory.length;i++)
				player.inventory.mainInventory[i]=null;
			
			if(message.hasSheikahSlate)
			{
				player.inventory.mainInventory[10]=new ItemStack(BOTWItems.sheikah_slate, 1);
			}
			else
			{
				player.inventory.mainInventory[10]=null;
			}
			
			for(int i=0;i<message.backItems.size();i++)
			{
				player.inventory.mainInventory[i+11]=message.backItems.get(i);
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
		PacketBuffer buffer=new PacketBuffer(buf);
		
		this.id=buffer.readInt();
		this.hasSheikahSlate=buffer.readBoolean();
		
		int size=buffer.readInt();
		this.backItems=new ArrayList<>();
		for(int i=0;i<size;i++)
		{
			try 
			{
				this.backItems.add(buffer.readItemStackFromBuffer());
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		PacketBuffer buffer=new PacketBuffer(buf);
		
		buffer.writeInt(id);
		buffer.writeBoolean(hasSheikahSlate);
		
		buffer.writeInt(backItems.size());
		for(int i=0;i<backItems.size();i++)
		{
			buffer.writeItemStackToBuffer(backItems.get(i));
		}
	}

}
