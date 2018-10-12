package com.theredmajora.botw.inventory.tab;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.item.BOTWItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BOTWTab extends CreativeTabs
{
	public BOTWTab()
	{
		super(BOTW.MODID);
	}

	@Override
	public Item getTabIconItem()
	{
		return BOTWItems.sheikahSlate;
	}

}
