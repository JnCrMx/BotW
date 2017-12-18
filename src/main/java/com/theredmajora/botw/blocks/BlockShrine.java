package com.theredmajora.botw.blocks;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.tileentities.BlockBOTWTE;
import com.theredmajora.botw.tileentities.TileEntityShrine;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BlockShrine extends BlockBOTWTE
{
	public BlockShrine()
	{
		super(Material.ROCK, "shrine");
        this.setBlockUnbreakable();
		this.setCreativeTab(BOTW.botwTab);
	}

	@Override
	public Class getTileEntityClass() 
	{
		return TileEntityShrine.class;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) 
	{
		return new TileEntityShrine();
	}
}
