package com.theredmajora.botw.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.theredmajora.botw.block.BOTWBlocks;
import com.theredmajora.botw.tileentity.TileEntityShrineLift;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityShrineLift extends EntityGolem
{
	protected static final AxisAlignedBB LIFT_AABB = new AxisAlignedBB(-4d/16d, 0.0D, -4d/16d, 20d/16d, 0d, 20d/16d);
	
	private static final DataParameter<Float> HEIGHT = EntityDataManager.<Float>createKey(EntityShrineLift.class, DataSerializers.FLOAT);
	private static final DataParameter<BlockPos> SHRINE = EntityDataManager.<BlockPos>createKey(EntityShrineLift.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<EnumFacing> DIRECTION = EntityDataManager.<EnumFacing>createKey(EntityShrineLift.class, DataSerializers.FACING);

	public EntityShrineLift(World worldIn)
	{
		super(worldIn);
		setSize(1.0f, 1.0f);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		
		this.dataManager.register(HEIGHT, 50.0f);
		this.dataManager.register(SHRINE, new BlockPos(0, 0, 0));
		this.dataManager.register(DIRECTION, EnumFacing.DOWN);
	}
	
	public float getHeight()
	{
		return this.dataManager.get(HEIGHT);
	}

	public void setHeight(float height)
	{
		this.dataManager.set(HEIGHT, height);
		this.dataManager.setDirty(HEIGHT);
	}

	public BlockPos getShrine()
	{
		return this.dataManager.get(SHRINE);
	}

	public void setShrine(BlockPos shrine)
	{
		this.dataManager.set(SHRINE, shrine);
		this.dataManager.setDirty(SHRINE);
	}

	public EnumFacing getDirection()
	{
		return this.dataManager.get(DIRECTION);
	}

	public void setDirection(EnumFacing direction)
	{
		this.dataManager.set(DIRECTION, direction);
		this.dataManager.setDirty(DIRECTION);
	}

	@Override
	public void applyEntityCollision(Entity entityIn)
	{
	}
	
    public float getCollisionBorderSize()
    {
        return 0.0F;
    }
    
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.isEntityAlive() ? this.getEntityBoundingBox() : null;
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
    	super.writeEntityToNBT(compound);
    	
    	if(getDirection()==null)
    		setDirection(EnumFacing.DOWN);
    	
    	compound.setFloat("Height", getHeight());
    	compound.setString("Direction", getDirection().getName());
    	compound.setInteger("ShrineX", getShrine().getX());
    	compound.setInteger("ShrineY", getShrine().getY());
    	compound.setInteger("ShrineZ", getShrine().getZ());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
    	super.readEntityFromNBT(compound);
    	
    	this.setHeight(compound.getFloat("Height"));
    	this.setDirection(EnumFacing.byName(compound.getString("Direction")));
    	this.setShrine(new BlockPos(compound.getInteger("ShrineX"), compound.getInteger("ShrineY"), compound.getInteger("ShrineZ")));
    	
    	if(getDirection()==null)
    		setDirection(EnumFacing.DOWN);
    }
    
    @Override
    public void onUpdate()
    {
    	//super.onUpdate();
		
		double y1 = getEntityBoundingBox().maxY;
		
		if(getDirection()==EnumFacing.DOWN)
		{
			if(getHeight()>0)
				setHeight(getHeight()-0.1f);
			else
			{
				worldObj.setBlockState(getPosition(), BOTWBlocks.shrine_lift.getDefaultState());
				TileEntityShrineLift tile = (TileEntityShrineLift) worldObj.getTileEntity(getPosition());
				tile.shrineX=getShrine().getX();
				tile.shrineY=getShrine().getY();
				tile.shrineZ=getShrine().getZ();
				
				List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0, 1, 0));
		        if (!list.isEmpty())
		        {
		            for (Entity entity : list)
		            {
		                if (!entity.noClip)
		                {
		                    entity.setPosition(entity.posX, entity.posY+0.1, entity.posZ);
		                }
		            }
		        }
		        
				setDead();
			}
		}	
		else if(getDirection()==EnumFacing.UP)
		{
			setHeight(getHeight()+0.1f);
		}
		
		setEntityBoundingBox(LIFT_AABB.offset(getPosition()).expand(0, getHeight(), 0));

		double y2 = getEntityBoundingBox().maxY;
		
		if((y2-y1)>0)
		{
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
	        if (!list.isEmpty())
	        {
	            for (Entity entity : list)
	            {
	                if (!entity.noClip)
	                {
	                    entity.moveEntity(entity.motionX, y2-y1, entity.motionZ);
	                }
	            }
	        }
		}
    }
}
