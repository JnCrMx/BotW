package com.theredmajora.botw.entity;

import net.minecraft.entity.player.EntityPlayer;

public interface IEntityCarriable
{
	/**
	 * @return Whether the player may throw the entity
	 */
	public boolean isThrowable();
	
	/**
	 * @return Whether the player may drop the entity
	 */
	public boolean isDroppable();
	
	/**
	 * <p>Called when a player drops the entity.<br>Called on server <b>and</b> client side.</p>
	 * <p>Following methods <b>MUST</b> be called:
	 * <ul>
	 * <li>{@link net.minecraft.entity.Entity#dismountRidingEntity()}</li>
	 * <li>{@link net.minecraft.entity.Entity#setPositionAndUpdate(double, double, double)}</li>
	 * </ul></p>
	 * @param player
	 */
	public void dropEntity(EntityPlayer player);
	
	/**
	 * <p>Called when a player throws the entity.<br>Called on server <b>and</b> client side.</p>
	 * <p>Following methods <b>MUST</b> be called:
	 * <ul>
	 * <li>{@link net.minecraft.entity.Entity#dismountRidingEntity()}</li>
	 * <li>{@link net.minecraft.entity.Entity#setPositionAndUpdate(double, double, double)}</li>
	 * </ul></p>
	 * @param player
	 */
	public void throwEntity(EntityPlayer player);
}
