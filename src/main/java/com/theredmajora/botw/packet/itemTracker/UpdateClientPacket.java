package com.theredmajora.botw.packet.itemTracker;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.packet.ExampleClientPacket;
import com.theredmajora.botw.packet.ExampleServerPacket;

public class UpdateClientPacket implements IMessage, IMessageHandler<UpdateClientPacket, IMessage>
{ 
	int howManyPlayers;
	int[] entityIds;
	NBTTagCompound[] nbts;

	public UpdateClientPacket(){ }

	public UpdateClientPacket(IItemTracker[] trackers)
	{
		this.howManyPlayers = trackers.length;
		this.entityIds = new int[this.howManyPlayers];
		this.nbts = new NBTTagCompound[this.howManyPlayers];
		
		for(int i=0; i<trackers.length; i++)
		{
			this.entityIds[i]=trackers[i].getEntityId();
			this.nbts[i]=trackers[i].writeNBT();
		}
	}

	@Override
	public IMessage onMessage(final UpdateClientPacket message, MessageContext ctx) {
		
		if(ctx.side==Side.CLIENT)	//Check for being on client side
		{
			//To get the player instance you must schedule the main thread to run the runnable code
			//reason is because packets are not run on the minecraft thread! therefore Minecraft.getMinecraft().thePlayer is null! 
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
	
				@Override
				public void run() {
					EntityPlayer p = Minecraft.getMinecraft().thePlayer; //now this player instance is real
					World world = p.worldObj;	
					
					for(int i=0; i<message.howManyPlayers; i++)
					{
						EntityPlayer player = (EntityPlayer) world.getEntityByID(message.entityIds[i]);
						if(player != null)
						{
							IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_CAP, null);
							tracker.readNBT(message.nbts[i]);
						}
					}
	
				}
			});
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buffer) 
	{
		PacketBuffer buf=new PacketBuffer(buffer);
		
		howManyPlayers = buf.readInt();
		entityIds = new int[howManyPlayers];
		nbts = new NBTTagCompound[howManyPlayers];
		for(int i =0; i < howManyPlayers; i ++)
		{
			entityIds[i] = buf.readInt();
			try
			{
				nbts[i] = buf.readNBTTagCompoundFromBuffer();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		PacketBuffer buf=new PacketBuffer(buffer);
		
		buf.writeInt(howManyPlayers);
		for(int i=0; i<howManyPlayers; i++)
		{
			buf.writeInt(entityIds[i]);
			buf.writeNBTTagCompoundToBuffer(nbts[i]); //let Minecraft do the work
		}
	}
}
