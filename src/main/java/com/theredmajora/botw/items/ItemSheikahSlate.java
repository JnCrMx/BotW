package com.theredmajora.botw.items;

import com.theredmajora.botw.BOTW;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSheikahSlate extends ItemBOTW
{
	public ItemSheikahSlate()
	{
		super("sheikah_slate");
		this.setMaxStackSize(1);
		setCreativeTab(BOTW.botwTab);
	}

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
    	if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean("roundBombActive", true);
		}
		final NBTTagCompound tag = stack.getTagCompound();
		if(tag.getBoolean("roundBombActive")) { System.out.println("roundBomb"); }
		else if(tag.getBoolean("squareBombActive")) { System.out.println("squareBomb"); }
		else if(tag.getBoolean("magnesisActive")) { System.out.println("magnesis"); }
		else if(tag.getBoolean("stasisActive")) { System.out.println("stasis"); }
		else if(tag.getBoolean("cryonisActive")) { System.out.println("cryonis"); }

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
