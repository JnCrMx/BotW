package com.theredmajora.botw.blocks;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.dimension.BOTWDimensions;
import com.theredmajora.botw.dimension.shrine.TeleporterShrine;
import com.theredmajora.botw.tabs.BOTWTab;
import com.theredmajora.botw.tileentities.BlockBOTWTE;
import com.theredmajora.botw.tileentities.TileEntityShrineLift;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockShrineLift extends BlockBOTWTE<TileEntityShrineLift>
{
	protected static final AxisAlignedBB LIFT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
	
	public BlockShrineLift()
	{
		super(Material.ROCK, "shrine_lift");
		this.setBlockUnbreakable();
		this.setCreativeTab(BOTW.botwTab);
	}

	@Override
	public Class<TileEntityShrineLift> getTileEntityClass()
	{
		return TileEntityShrineLift.class;
	}

	@Override
	public TileEntityShrineLift createTileEntity(World world, IBlockState state)
	{
		return new TileEntityShrineLift();
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return LIFT_AABB;
	}
	
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP ? true : (blockAccess.getBlockState(pos.offset(side)).getBlock() == this ? true : super.shouldSideBeRendered(blockState, blockAccess, pos, side));
    }
    
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
    	if(entityIn instanceof EntityPlayerMP)
    	{
    		EntityPlayerMP player=(EntityPlayerMP) entityIn;
    		if(player.isSneaking())
    		{
    			player.timeUntilPortal=1000;
    			worldIn.getMinecraftServer().getPlayerList().transferPlayerToDimension(player, BOTWDimensions.SHRINE.getId(), new TeleporterShrine(worldIn.getMinecraftServer().worldServerForDimension(BOTWDimensions.SHRINE.getId()), (TileEntityShrineLift) worldIn.getTileEntity(pos)));
    		}
    	}
    }
}
