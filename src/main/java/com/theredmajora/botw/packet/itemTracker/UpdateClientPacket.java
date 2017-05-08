package com.theredmajora.botw.packet.itemTracker;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.proxy.ClientProxy;
import com.theredmajora.botw.util.ItemTrackerPacketData;

public class UpdateClientPacket implements IMessage, IMessageHandler<UpdateClientPacket, IMessage> { 


	int howManyPlayers;
	List<ItemTrackerPacketData> nearbyPlayers;

	public UpdateClientPacket(){ }

	public UpdateClientPacket(List<ItemTrackerPacketData> nearbyPlayers)
	{
		this.nearbyPlayers = nearbyPlayers;
		this.howManyPlayers = this.nearbyPlayers.size();
	}

	@Override
	public IMessage onMessage(final UpdateClientPacket message, MessageContext ctx) {

		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {

			@Override
			public void run() {
				EntityPlayer p = Minecraft.getMinecraft().thePlayer; //now this player instance is real
				World world = p.worldObj;	

				for(ItemTrackerPacketData data : message.nearbyPlayers)
				{
					if(data != null)
					{
						boolean render[] = data.getRender();
						EntityPlayer player = (EntityPlayer) world.getEntityByID(data.getPlayerID());
						ClientProxy.addShouldRender(player.getName(), render);
					}
				}
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int entityId;
		boolean render[] = {false, false, false};
		this.howManyPlayers = buf.readInt();

		for(int i =0; i < howManyPlayers; i ++)
		{
			entityId = buf.readInt();

			for(int j =0; j < render.length; j ++)
			{
				render[j] = buf.readBoolean();
			}
			this.nearbyPlayers.add(new ItemTrackerPacketData(entityId, render));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		//	buf.writeBoolean(this.hasSlate);

		buf.writeInt(howManyPlayers);
		for(ItemTrackerPacketData player : nearbyPlayers)
		{
			buf.writeInt(player.getPlayer().getEntityId());

			for(int i =0; i < player.getRender().length; i ++)
			{
				buf.writeBoolean(player.getRender()[i]);
			}
		}
	}
}
