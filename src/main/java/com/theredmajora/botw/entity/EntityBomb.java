package com.theredmajora.botw.entity;

import java.util.ArrayList;

import com.theredmajora.botw.item.ItemBOTWShield;
import com.theredmajora.botw.item.ItemParaglider;
import com.theredmajora.botw.packet.BOTWActionPacket;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBomb extends EntityLivingBase implements IEntityCarriable
{
	private static final DataParameter<Integer> BOMB_TYPE = EntityDataManager.<Integer>createKey(EntityBomb.class, DataSerializers.VARINT);
	
	public EntityBomb(World worldIn)
	{
		super(worldIn);
		setSize(0.5f, 0.5f);
	}
	
    public void explode()
    {
        if (!this.worldObj.isRemote)
        {
            this.dead = true;
            
           	Explosion explosion = new Explosion(worldObj, this, this.posX, this.posY, this.posZ, 2, false, true);
           	explosion.doExplosionA();
           	explosion.doExplosionB(false);
            
            WorldServer server=(WorldServer) worldObj;
            server.spawnParticle(EnumParticleTypes.getByName("blue_splash"), posX, posY, posZ, 1000, 0, 0, 0, 0, new int[0]);
            
            this.setDead();
        }
    }

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, ItemStack stack, EnumHand hand)
	{						
		if(!this.worldObj.isRemote)
		{
			if(player.getPassengers().isEmpty())
			{						
				this.startRiding(player, true);
				
				SPacketSetPassengers packet=new SPacketSetPassengers(player);
				((EntityPlayerMP)player).connection.sendPacket(packet);
				
				return EnumActionResult.SUCCESS;
			}
			else
			{
				//player.removePassengers();
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	public double getYOffset()
	{
		return 0.5;
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		
		this.dataManager.register(BOMB_TYPE, Integer.valueOf(0));
	}

	@Override
	public EnumHandSide getPrimaryHand()
	{
		return null;
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList()
	{
		return new ArrayList<ItemStack>();
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
	{
		return null;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
	{
		
	}
	
	@Override
	public void onUpdate()
	{				
		Entity riding = getRidingEntity();
		if(riding!=null && riding instanceof EntityPlayer)
		{
			EntityPlayer player=(EntityPlayer)riding;
				
			ItemStack stack=player.inventory.getCurrentItem();
			if(stack!=null)
			{
				Item item=stack.getItem();
				if(item instanceof ItemBow || item instanceof ItemSword || item instanceof ItemShield || 
						item instanceof ItemBOTWShield || item instanceof ItemParaglider)
				{					
					//We've to do this on client and server side
					dropEntity(player);
					
					return;
				}
			}
				
			player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 1, 1, false, false));
		}
		
		//TODO: Some little light (render) bugs
		//FIXME: Jumping, throwing, death
		
		worldObj.setLightFor(EnumSkyBlock.BLOCK, getPosition(), 15);
		
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(1, 0, 0));
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(1, 0, 1));
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(1, 0, -1));
		
		//Do NOT update own position (light would disappear)
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(0, 0, 1));
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(0, 0, -1));
		
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(-1, 0, 0));
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(-1, 0, 1));
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, getPosition().add(-1, 0, -1));
		
		//Force update on last position in order to update light even after teleport oder startRiding
		worldObj.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(lastTickPosX, lastTickPosY, lastTickPosZ));
		
		super.onUpdate();
	}
	
	public void setBombType(BombType type)
	{
		this.dataManager.set(BOMB_TYPE, Integer.valueOf(type.ordinal()));
	}
	
	public BombType getBombType()
	{
		return BombType.values()[((Integer)this.dataManager.get(BOMB_TYPE)).intValue()];
	}
	
	public static enum BombType
	{
		ROUND_BOMB,
		SQUARE_BOMB
	}

	@Override
	public boolean isThrowable()
	{
		return true;
	}

	@Override
	public boolean isDroppable()
	{
		return true;
	}

	@Override
	public void dropEntity(EntityPlayer player)
	{
		dismountRidingEntity();
		
		float yaw=player.rotationYawHead;
		
		double motionX = -MathHelper.sin((float) Math.toRadians(yaw))*0.1;
		double motionZ = MathHelper.cos((float) Math.toRadians(yaw))*0.1;
		
		setVelocity(motionX, 0.05, motionZ);
		setPositionAndUpdate(player.posX, player.posY, player.posZ);
	}

	@Override
	public void throwEntity(EntityPlayer player)
	{
		dismountRidingEntity();
		
		float yaw=player.rotationYawHead;
		float pitch=-player.rotationPitch;
		
		double motionX = -MathHelper.sin((float) Math.toRadians(yaw))*1;
		double motionZ = MathHelper.cos((float) Math.toRadians(yaw))*1;
		double motionY = MathHelper.sin((float) Math.toRadians(pitch))*1;

		motionX+=player.motionX;
		motionY+=player.motionY;
		motionZ+=player.motionZ;
		
		setPositionAndUpdate(player.posX, player.posY, player.posZ);
		setVelocity(motionX, motionY, motionZ);
	}
}
