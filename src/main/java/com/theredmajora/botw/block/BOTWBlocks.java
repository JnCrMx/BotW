package com.theredmajora.botw.block;

import com.theredmajora.botw.tileentity.BlockBOTWTE;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BOTWBlocks
{
	public static Block temp_ice;
	public static Block shrine_lift;
	
	public static BlockStatueOfTheGoddess statueOfTheGoddess;
	
	public static void init()
	{
		temp_ice = register(new BlockTempIce());
		shrine_lift = register(new BlockShrineLift());
		statueOfTheGoddess = register(new BlockStatueOfTheGoddess());
	}
	
	private static <T extends Block> T register(T block, ItemBlock itemBlock)
	{
		GameRegistry.register(block);
		GameRegistry.register(itemBlock);

		if (block instanceof BlockBOTW)
		{
			((BlockBOTW)block).registerItemModel(itemBlock);
		}
		if (block instanceof BlockBOTWTE)
		{
			GameRegistry.registerTileEntity(((BlockBOTWTE<?>)block).getTileEntityClass(), block.getRegistryName().toString());
		}

		return block;
	}

	private static <T extends Block> T register(T block)
	{
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return register(block, itemBlock);
	}
}
