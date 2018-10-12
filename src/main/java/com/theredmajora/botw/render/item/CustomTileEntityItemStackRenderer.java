package com.theredmajora.botw.render.item;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CustomTileEntityItemStackRenderer extends TileEntityItemStackRenderer 
{	
	@Override
	public void renderByItem(ItemStack itemStackIn) 
	{
		Item item=itemStackIn.getItem();
		if(item instanceof CustomItemRenderer)
		{
			((CustomItemRenderer)item).render(itemStackIn);
		}
		else
		{
			super.renderByItem(itemStackIn);	//Don't forget this
		}
	}
}
