package com.theredmajora.botw.inventory.tab;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.item.BOTWItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BOTWTabShields extends CreativeTabs
{

	public BOTWTabShields() 
	{
		super(BOTW.MODID+".shields");
	}

	@Override
	public Item getTabIconItem() 
	{
		return BOTWItems.gerudoShield;
	}

}
