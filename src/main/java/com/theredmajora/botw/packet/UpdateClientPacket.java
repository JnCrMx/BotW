package com.theredmajora.botw.packet;

import java.io.IOException;
import java.lang.reflect.Field;

import com.theredmajora.botw.capability.ITracker;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class UpdateClientPacket implements IMessage, IMessageHandler<UpdateClientPacket, IMessage>
{ 
	int howManyPlayers;
	int[] entityIds;
	NBTTagCompound[] nbts;
	
	//TODO: Find a better solution
	String[] classes;
	String[] fields;

	public UpdateClientPacket(){ }

	public UpdateClientPacket(ITracker[] trackers)
	{
		this.howManyPlayers = trackers.length;
		this.entityIds = new int[this.howManyPlayers];
		this.nbts = new NBTTagCompound[this.howManyPlayers];
		this.classes = new String[this.howManyPlayers];
		this.fields = new String[this.howManyPlayers];
		
		for(int i=0; i<trackers.length; i++)
		{
			this.entityIds[i]=trackers[i].getEntityId();
			this.nbts[i]=trackers[i].writeNBT();
			
			try
			{
				this.classes[i]=trackers[i].getCapabilityField().getDeclaringClass().getName();
				this.fields[i]=trackers[i].getCapabilityField().getName();
			}
			catch (NoSuchFieldException e)
			{
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				e.printStackTrace();
			}
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
							try
							{
								//Find capability field
								Field field = Class.forName(message.classes[i]).getDeclaredField(message.fields[i]);
								field.setAccessible(true);
								Capability<ITracker> capability = (Capability<ITracker>) field.get(null);
								
								ITracker tracker = player.getCapability(capability, null);
								tracker.readNBT(message.nbts[i]);
							}
							catch (NoSuchFieldException e)
							{
								e.printStackTrace();
							}
							catch (SecurityException e)
							{
								e.printStackTrace();
							}
							catch (ClassNotFoundException e)
							{
								e.printStackTrace();
							}
							catch (IllegalArgumentException e)
							{
								e.printStackTrace();
							}
							catch (IllegalAccessException e)
							{
								e.printStackTrace();
							}
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
		
		classes = new String[howManyPlayers];
		fields = new String[howManyPlayers];
		
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
			
			classes[i] = new String(buf.readByteArray());
			fields[i] = new String(buf.readByteArray());
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
			
			buf.writeByteArray(classes[i].getBytes());
			buf.writeByteArray(fields[i].getBytes());
		}
	}
}
