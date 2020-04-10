package com.theredmajora.botw.block;

import com.theredmajora.botw.BOTW;
import net.minecraft.block.material.Material;

public class BlockShrineWall extends BlockBOTW
{
	public BlockShrineWall()
	{
		super(Material.ROCK, "shrine_wall");
		this.setBlockUnbreakable();
		this.setCreativeTab(BOTW.botwTab);
	}
}
