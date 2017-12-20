package com.theredmajora.botw.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BOTWItems
{
	public static ItemSheikahSlate sheikah_slate;
	public static ItemParaglider paraglider;
	
	public static ItemBOTWShield hyliaShield;
	public static ItemBOTWShield gerudoShield;
	
	public static void init()
	{
		sheikah_slate = registerItem(new ItemSheikahSlate());
		paraglider = registerItem(new ItemParaglider());
		hyliaShield = registerItem(new ItemBOTWShield("hylia", 90));
		gerudoShield = registerItem(new ItemBOTWShield("gerudo", 20));
	}
	
	private static <O extends Item> O registerItem(O item)
	{
		GameRegistry.register(item);

		if (item instanceof ItemBOTW) {
			((ItemBOTW)item).registerItemModel();
		}
		
		return item;
	}
}
