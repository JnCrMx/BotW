package com.theredmajora.botw.item;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemStaminaVessel extends ItemBOTW
{

	public ItemStaminaVessel()
	{
		super("stamina_vessel");
		setMaxStackSize(1);
		setCreativeTab(BOTW.botwTab);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		IPlayerTracker playerTracker = playerIn.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
		if(playerTracker.getMaxStamina()<=2800)
		{
	        if (!playerIn.capabilities.isCreativeMode)
	        {
	            --itemStackIn.stackSize;
	        }
	        
	        if(!worldIn.isRemote)
	        {
	        	playerTracker.setMaxStamina(playerTracker.getMaxStamina()+200);
	        }
	        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}
}
