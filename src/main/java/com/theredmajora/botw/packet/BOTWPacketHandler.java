package com.theredmajora.botw.packet;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.packet.itemTracker.UpdateClientPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BOTWPacketHandler {


	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(BOTW.MODID); 

	/* Here is where we will initialize all of our packets that can be sent by our mod! To register a packet
	 * its as simple as 
   INSTANCE.registerMessage(the message handler, the message type, a unique discriminator, the side that the handler will work on);

  Message Handler - The message handler is what minecraft will do once the packet has been received by the opposite side must implement 
  IMessageHandler<request type, reply type>
  
  Message Type - This is a class that will define what information the packet can hold, and will be the type that the handler recieves 
  must implement IMessage
  
  discriminator - has to be a unique ID just increment from 0
  Side - The side that the handler will be run on...if on server then will only run on server, if on client then only run on client 

   The message handler and the message type can be the same or you could make them separate...making them separate makes it easier to send the same data
   but perform different functions.
   
   The last thing to cover is how to send a packet:
   All we have to do is access the INSTANCE object: 
	 * INSTANCE.sendToServer(message); - will send the packet to the server from the client
	 * INSTANCE.sendToAll(message); - will send the packet to all other clients
	 * INSTANCE.sentTo(message, player) - will send that packet to that specific player
	 * INSTANCE.sentToDimension - will send that packet to all players in that dimension
	 */

	public static void init() 
	{	

		
		INSTANCE.registerMessage(UpdateClientPacket.class, UpdateClientPacket.class, 0, Side.CLIENT);
		
		//will send packet from client to server
		//INSTANCE.registerMessage(ExampleClientPacket.class, ExampleClientPacket.class, 0, Side.SERVER);

		//will send packet from server to client
		//INSTANCE.registerMessage(ExampleServerPacket.class, ExampleServerPacket.class, 1, Side.CLIENT); 


	}	
}
