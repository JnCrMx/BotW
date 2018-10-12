package com.theredmajora.botw.block;

import java.util.List;

import com.theredmajora.botw.entity.EntityGoddessItem;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.tileentity.BlockBOTWTE;
import com.theredmajora.botw.tileentity.TileEntityStatueOfTheGoddess;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class BlockStatueOfTheGoddess extends BlockBOTWTE<TileEntityStatueOfTheGoddess>
{
	public static final PropertyEnum<EnumPartType> PART = PropertyEnum.<EnumPartType>create("part", EnumPartType.class);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public static enum EnumPartType implements IStringSerializable
	{
        BOTTOM("bottom"),
		TOP("top");

        private final String name;

        private EnumPartType(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
	}
			
	public BlockStatueOfTheGoddess()
	{
		super(Material.ROCK, "statue_of_the_goddess");
		setBlockUnbreakable();
		setResistance(6000000.0F);
		
		setDefaultState(blockState.getBaseState().withProperty(PART, EnumPartType.TOP).withRotation(Rotation.NONE));
	}
	
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, PART});
    }
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(PART).ordinal() << 2 | state.getValue(FACING).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(PART, EnumPartType.values()[meta >> 2]).withRotation(Rotation.values()[meta & 0b011]);
	}
	
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	if(!worldIn.isRemote)
    	{
   			if(state.getValue(PART)==EnumPartType.BOTTOM)
			{
				pos=pos.add(0, 1, 0);
				
				if(worldIn.getBlockState(pos).getBlock()!=BOTWBlocks.statueOfTheGoddess)
					return false;
				
				state=worldIn.getBlockState(pos);
			}
    		
    		TileEntityStatueOfTheGoddess tileEntity = (TileEntityStatueOfTheGoddess)worldIn.getTileEntity(pos);
    		int phase = tileEntity.talkPhase;
    		if(phase==0)
    		{
				playerIn.addChatMessage(new TextComponentTranslation("message.goddess.start", new Object[0]));
				
				int count = 0;
    			for (ItemStack itemStack : playerIn.inventory.mainInventory)
    			{
    				if(itemStack != null)
    					if(itemStack.getItem() == BOTWItems.spiritOrb)
    						count += itemStack.stackSize;
    			}
    			if(count >= 4)
    			{
    				phase=1;
    			}
    			else
    			{
    				phase=10;
    			}
    			tileEntity.player=playerIn;
    		}
    		else if(phase==10)
    		{
    			playerIn.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.1", new Object[0]));
    			phase=11;
    		}
    		else if(phase==11)
    		{
    			playerIn.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.2", new Object[0]));
    			phase=12;
    		}
    		else if(phase==12)
    		{
    			playerIn.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.3", new Object[0]));
    			phase=13;
    		}
    		else if(phase==13)
    		{
    			playerIn.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.4", new Object[0]));
    			phase=0;
    		}
    		else if(phase==1 || phase==2 || phase==3 || phase==4)
    		{
    			List<EntityGoddessItem> goddessItems = worldIn.getEntitiesWithinAABB(EntityGoddessItem.class, new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(1, 1, 1)));
        		if(goddessItems.isEmpty())
        		{
        			playerIn.addChatMessage(new TextComponentTranslation("message.goddess.select", new Object[0]));
        			
    	    	    EnumFacing rotation = state.getValue(FACING);
    	    	    System.out.println(rotation);
    	    	    
    	    	    BlockPos pos1 = pos;
    	    	    BlockPos pos2 = pos;
    	    	    float yaw = 0.0f;
    	    	    
    	    	    switch (rotation)
					{
						case NORTH:
							pos1=pos1.add(1, 0, 0);
							break;
						case SOUTH:
							pos2=pos2.add(1, 0, 1);
							pos1=pos1.add(0, 0, 1);
							break;
						case EAST:
							pos1=pos1.add(1, 0, 1);
							pos2=pos2.add(1, 0, 0);
							yaw=90.0f;
							break;
						case WEST:
							pos2=pos2.add(0, 0, 1);
							yaw=90.0f;
							break;
						default:
							break;
					}
    	    	    
	    	    	EntityGoddessItem entityItemHeart = new EntityGoddessItem(worldIn, pos1.getX(), pos1.getY(), pos1.getZ(), yaw, new ItemStack(BOTWItems.heartContainer), pos);
	    	    	EntityGoddessItem entityItemStamina = new EntityGoddessItem(worldIn, pos2.getX(), pos2.getY(), pos2.getZ(), yaw, new ItemStack(BOTWItems.staminaVessel), pos);
	
	    	    	worldIn.spawnEntityInWorld(entityItemHeart);
	    	    	worldIn.spawnEntityInWorld(entityItemStamina);
    	    	    
    	    	    phase=2;
        		}
        		else
        		{
        			goddessItems.forEach(entity -> entity.setDead());
    				playerIn.addChatMessage(new TextComponentTranslation("message.goddess.exit", new Object[0]));
    				phase=0;
        		}
    		}
    		tileEntity.talkPhase=phase;
    	}
    	return true;
    }

	@Override
	public Class<TileEntityStatueOfTheGoddess> getTileEntityClass()
	{
		return TileEntityStatueOfTheGoddess.class;
	}

	@Override
	public TileEntityStatueOfTheGoddess createTileEntity(World world, IBlockState state)
	{
		return new TileEntityStatueOfTheGoddess();
	}
}
