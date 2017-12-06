package com.theredmajora.botw.blocks;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.tileentities.BlockBOTWTE;
import com.theredmajora.botw.tileentities.TileEntityTempIce;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTempIce extends BlockBOTWTE
{
	public BlockTempIce()
	{
        super(Material.ICE, "temp_ice");
        this.slipperiness = 0.98F;
        this.setSoundType(SoundType.GLASS);
        this.setBlockUnbreakable();
		setCreativeTab(BOTW.botwTab);
	}

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
    {
    	Block south = world.getBlockState(pos.south()).getBlock();
    	Block west = world.getBlockState(pos.west()).getBlock();
    	Block east = world.getBlockState(pos.east()).getBlock();
    	Block north = world.getBlockState(pos.north()).getBlock();
    	Block up = world.getBlockState(pos.up()).getBlock();
    	Block down = world.getBlockState(pos.down()).getBlock();
    	
    	if(south == Blocks.WATER || south == Blocks.FLOWING_WATER || west == Blocks.WATER || west == Blocks.FLOWING_WATER || east == Blocks.WATER || east == Blocks.FLOWING_WATER || north == Blocks.WATER || north == Blocks.FLOWING_WATER)
    	{
    		world.setBlockState(pos, Blocks.WATER.getDefaultState());
    	}
    	else
    	{
    		world.destroyBlock(pos, false);
    	}
    	if(south instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.south(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	if(west instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.west(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	if(east instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.east(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	if(north instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.north(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	if(up instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.up(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	if(down instanceof BlockTempIce)
    	{
    		world.addBlockEvent(pos.down(), BOTWBlocks.temp_ice, 0, 0);
    	}
    	
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
        Block block = iblockstate.getBlock();

        return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }
    
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

	@Override
	public Class getTileEntityClass()
	{
		return TileEntityTempIce.class;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
        return new TileEntityTempIce();
	}
}
