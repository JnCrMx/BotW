package com.theredmajora.botw.item;

import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemDebugTool extends ItemBOTW
{
	public ItemDebugTool()
	{
		super("debug_tool");
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		if(!worldIn.isRemote)
		{
			IPlayerTracker playerTracker = playerIn.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
			playerTracker.setExhausted(false);
			playerTracker.setStamina(playerTracker.getMaxStamina());
			
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}
}
