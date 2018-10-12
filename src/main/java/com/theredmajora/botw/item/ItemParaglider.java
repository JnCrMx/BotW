package com.theredmajora.botw.item;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemParaglider extends ItemBOTW
{
	public ItemParaglider()
	{
		super("paraglider");
		this.setMaxStackSize(1);
		setCreativeTab(BOTW.botwTab);
	}
	
	@Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld)
    {	
    	EntityPlayer player = (entity instanceof EntityPlayer ? (EntityPlayer) entity : null);
    	
    	if (isHeld && player.fallDistance > 1.0F && player.motionY < 0)
    	{
    		if (player == null || !player.capabilities.isFlying)
    		{
				player.motionX = -(Math.sin(Math.toRadians(player.getRotationYawHead())) * 0.3);
				player.motionZ = (Math.cos(Math.toRadians(player.getRotationYawHead())) * 0.3);

				IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
    			if(!player.isSneaking() && !playerTracker.isExhausted())
    			{
    				player.motionY = (player.motionY < -0.05D ? -0.05D : player.motionY);
    				player.fallDistance = 1.0F;
    				
    				if(!world.isRemote)
    				{
    					playerTracker.setStamina(playerTracker.getStamina()-2);
    				}
    			}
    				
    			boolean flag = false;
    				
    			for (int i = 0; i < 5; ++i)
    			{
    				if(world.getBlockState(new BlockPos(player.posX, player.posY - i, player.posZ)).getBlock() == Blocks.FIRE)
    				{
    					flag = true;
    				}
    				else if(i == 0)
    				{
    					flag = false;
    				}
    			}
    				
    			if(flag)
    			{
    				entity.motionY = player.motionY + 0.75D;
    			}
    		}
    	}
    }
}
