package com.theredmajora.botw.item;

import com.theredmajora.botw.BOTW;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BOTWItems
{
	//Special items
	public static ItemSheikahSlate sheikahSlate;
	public static ItemParaglider paraglider;
	public static ItemStaminaVessel staminaVessel;
	public static ItemHeartContainer heartContainer;
	public static ItemDebugTool debugTool;
	
	//Standard items
	public static Item spiritOrb;
	
	//Shield items
	public static ItemBOTWShield hyliaShield;
	public static ItemBOTWShield gerudoShield;
	
	public static void init()
	{
		//Special items
		sheikahSlate = registerItem(new ItemSheikahSlate());
		paraglider = registerItem(new ItemParaglider());
		staminaVessel = registerItem(new ItemStaminaVessel());
		heartContainer = registerItem(new ItemHeartContainer());
		debugTool = registerItem(new ItemDebugTool());
		
		//Standard items
		spiritOrb = registerItem(new ItemBOTW("spirit_orb").setCreativeTab(BOTW.botwTab));
		
		//Shield items
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
