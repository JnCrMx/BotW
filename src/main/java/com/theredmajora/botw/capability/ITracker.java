package com.theredmajora.botw.capability;

import java.lang.reflect.Field;

import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.packet.UpdateClientPacket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base interface for capability trackers
 * @author JCM
 */
public interface ITracker
{
	//TODO: Resource friendly solution
	/**
	 * 
	 * @return Whether some data has changed and this tracker should be send to the client with {@link UpdateClientPacket}
	 */
	public boolean isDirty(EntityPlayer player);
	
	/**
	 * This method should be called after transmitting the tracker in order to set dirty to false. This should be done <b>after</b> sending the packet.
	 * <p><code>
	 * setDirty(false);
	 * </code></p>
	 * @param dirty New value for {@link #isDirty(EntityPlayer)}
	 */
	public void setDirty(EntityPlayer player, boolean dirty);
	/**
	 * Sets the value for {@link #isDirty(EntityPlayer)} to <code>true</code> for all players.<br>
	 * This method should be called in a setter method if data has changed.
	 * <p><code>if(oldData!=newData)<br>{<br>	setDirty();<br>}</code></p>
	 */
	public void setDirty();
	
	/**
	 * @return entity id of client player
	 */
	public int getEntityId();

	/**
	 * Writes <b>every</b> information to a {@link NBTTagCompound}.
	 * @return the {@link NBTTagCompound}
	 */
	public NBTTagCompound writeNBT();
	/**
	 * Reads <b>every</b> information from a {@link NBTTagCompound}. Sets own variables.
	 * @param tag the {@link NBTTagCompound}
	 */
	public void readNBT(NBTTagCompound tag);
	
	/**
	 * 
	 * @return A reference to the field holding the {@link net.minecraftforge.common.capabilities.Capability}<{@link ITracker}> object. Used for reflection.
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public Field getCapabilityField() throws NoSuchFieldException, SecurityException;
}
