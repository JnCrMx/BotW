package com.theredmajora.botw.items;

import com.theredmajora.botw.BOTW;
import net.minecraft.item.Item;

public class ItemBOTW extends Item
{	
	private String name;

	public ItemBOTW(String name)
	{
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
	}

	public void registerItemModel()
	{
		BOTW.proxy.registerItemRenderer(this, 0, name);
	}
}