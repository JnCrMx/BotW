package com.theredmajora.botw.entity;

import java.util.List;

import com.theredmajora.botw.block.BlockStatueOfTheGoddess;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.tileentity.TileEntityStatueOfTheGoddess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class EntityGoddessItem extends EntityItem
{
	private static final DataParameter<BlockPos> STATUE = EntityDataManager.<BlockPos>createKey(EntityGoddessItem.class, DataSerializers.BLOCK_POS);
	
	public EntityGoddessItem(World worldIn, double x, double y, double z, float yaw, ItemStack stack, BlockPos statue)
	{
		super(worldIn, x, y, z, stack);
		this.rotationYaw = yaw;
		
		setStatue(statue);
	}
	
	public EntityGoddessItem(World worldIn)
    {
		super(worldIn);
		setSize(0.5f, 0.5f);
    }
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(STATUE, new BlockPos(0, 0, 0));
	}
	
	@Override
	public void onUpdate()
	{		
		//Calculate "yaw" for RenderEntityItem
		this.hoverStart = this.rotationYaw / (180.0f / (float)Math.PI);
	}
	
	@Override
	public void onCollideWithPlayer(EntityPlayer entityIn)
	{
	}
	
	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}
	
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, ItemStack stack, EnumHand hand)
	{
		if(!worldObj.isRemote)
		{
			if(getEntityItem()==null)
				return EnumActionResult.FAIL;
			
			BlockPos pos = getStatue();
			IBlockState state = worldObj.getBlockState(pos);
			TileEntityStatueOfTheGoddess tileEntity = (TileEntityStatueOfTheGoddess)worldObj.getTileEntity(pos);
			
			if(state.getBlock()==Blocks.AIR)
			{
				AxisAlignedBB box = new AxisAlignedBB(getPosition().add(-2, -1, -2), getPosition().add(2, 1, 2));
				List<EntityGoddessItem> goddessItems = worldObj.getEntitiesWithinAABB(EntityGoddessItem.class, box);
				goddessItems.forEach(entity -> entity.setDead());
				
				player.addChatMessage(new TextComponentTranslation("message.goddess.exit", new Object[0]));
			}
			
			if(player.getEntityId() == tileEntity.player.getEntityId())
			{
				int count = 0;
				for (ItemStack itemStack : player.inventory.mainInventory)
				{
					if(itemStack != null)
						if(itemStack.getItem() == BOTWItems.spiritOrb)
							count += itemStack.stackSize;
				}
				
				if(count >= 4)
				{
					Item selection=getEntityItem().getItem();
					if(tileEntity.talkPhase==2)
					{
						if(getEntityItem().getItem()==BOTWItems.heartContainer)
							player.addChatMessage(new TextComponentTranslation("message.goddess.select.heart", new Object[0]));
						else if(getEntityItem().getItem()==BOTWItems.staminaVessel)
							player.addChatMessage(new TextComponentTranslation("message.goddess.select.stamina", new Object[0]));
						
						tileEntity.talkPhase=3;
						tileEntity.selection=selection;
					}
					else if(tileEntity.talkPhase==3)
					{
						if(tileEntity.selection == selection)
						{
							player.addChatMessage(new TextComponentTranslation("message.goddess.success", new Object[0]));
							
							AxisAlignedBB box = new AxisAlignedBB(getPosition().add(-2, -1, -2), getPosition().add(2, 1, 2));
							List<EntityGoddessItem> goddessItems = worldObj.getEntitiesWithinAABB(EntityGoddessItem.class, box);
							goddessItems.forEach(entity -> entity.setDead());
							
							BlockPos pos2 = pos.offset(state.getValue(BlockStatueOfTheGoddess.FACING), 1);
							EntityItem item = new EntityItem(worldObj, pos2.getX(), pos2.getY()+5, pos2.getZ(), new ItemStack(selection, 1))
							{
								@Override
								public void onUpdate()
								{
									this.setNoGravity(!this.hasNoGravity());
									super.onUpdate();
								}
							};
							
							item.setPositionAndRotation(pos2.getX()+0.5, pos2.getY()+5, pos2.getZ()+0.5, 0, 0);
							item.setVelocity(0, 0, 0);
							item.setPickupDelay(50);
							
							for(int i=0;i<4;i++)
							{
								for(int j=0;j<player.inventory.mainInventory.length;j++)
								{
									ItemStack stack2 = player.inventory.mainInventory[j];
									if(stack2 != null && stack2.getItem()==BOTWItems.spiritOrb)
									{
										player.inventory.decrStackSize(j, 1);
										break;
									}
								}
							}
							
							worldObj.spawnEntityInWorld(item);
							
							if(count >= 8)
							{
			    				player.addChatMessage(new TextComponentTranslation("message.goddess.go_on", new Object[0]));
								tileEntity.talkPhase=4;
							}
							else
							{
			    				player.addChatMessage(new TextComponentTranslation("message.goddess.exit", new Object[0]));
			    				
								tileEntity.talkPhase=0;
							}
						}
						else
						{
							if(getEntityItem().getItem()==BOTWItems.heartContainer)
								player.addChatMessage(new TextComponentTranslation("message.goddess.select.heart", new Object[0]));
							else if(getEntityItem().getItem()==BOTWItems.staminaVessel)
								player.addChatMessage(new TextComponentTranslation("message.goddess.select.stamina", new Object[0]));
							
							tileEntity.talkPhase=3;
							tileEntity.selection=selection;
						}
					}
				}
				else
				{
					player.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.1", new Object[0]));
					player.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.2", new Object[0]));
					player.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.3", new Object[0]));
					player.addChatMessage(new TextComponentTranslation("message.goddess.not_enough.4", new Object[0]));
					
					AxisAlignedBB box = new AxisAlignedBB(getPosition().add(-2, -1, -2), getPosition().add(2, 1, 2));
					List<EntityGoddessItem> goddessItems = worldObj.getEntitiesWithinAABB(EntityGoddessItem.class, box);
					goddessItems.forEach(entity -> entity.setDead());
				}
			}
			else
			{
				player.addChatMessage(new TextComponentTranslation("message.goddess.other", new Object[0]));
				player.attackEntityFrom(DamageSource.outOfWorld, 2.0f);
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public boolean canBeAttackedWithItem()
	{
		return false;
	}
	
	@Override
	public int getAge()
	{
		return 0;
	}
	
	@Override
	public AxisAlignedBB getEntityBoundingBox()
	{
		return super.getEntityBoundingBox().offset(0, 0.25, 0);
	}
	
	public void setStatue(BlockPos statue)
	{
		this.dataManager.set(STATUE, statue);
		this.dataManager.setDirty(STATUE);
	}
	
	public BlockPos getStatue()
	{
		return this.dataManager.get(STATUE);
	}
}
