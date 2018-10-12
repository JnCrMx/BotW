package com.theredmajora.botw.item;

import com.theredmajora.botw.BOTW;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemHeartContainer extends ItemBOTW
{

	public ItemHeartContainer()
	{
		super("heart_container");
		setMaxStackSize(1);
		setCreativeTab(BOTW.botwTab);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		IAttributeInstance maxHealth = playerIn.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
		if(maxHealth.getBaseValue()<=38)
		{
			if (!playerIn.capabilities.isCreativeMode)
	        {
	            --itemStackIn.stackSize;
	        }
			
			if(!worldIn.isRemote)
			{
				maxHealth.setBaseValue(maxHealth.getBaseValue()+2);
				playerIn.setHealth((float) maxHealth.getBaseValue());
			}
			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}
}
