package com.theredmajora.botw.block;

import com.theredmajora.botw.BOTW;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

public class BlockBOTW extends Block
{
	private String name;

	public BlockBOTW(Material material, String name)
	{
		super(material);
		this.name = name;
		setUnlocalizedName(name);
		setRegistryName(name);
	}

	public void registerItemModel(ItemBlock itemBlock)
	{
		BOTW.proxy.registerItemRenderer(itemBlock, 0, name);
	}
}