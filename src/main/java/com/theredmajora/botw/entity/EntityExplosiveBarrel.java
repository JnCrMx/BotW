package com.theredmajora.botw.entity;

import java.util.ArrayList;

import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.item.ItemBOTWShield;
import com.theredmajora.botw.item.ItemParaglider;

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
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityExplosiveBarrel extends EntityLivingBase implements IEntityCarriable
{
	
	public EntityExplosiveBarrel(World worldIn)
	{
		super(worldIn);
		setSize(1.0f, 1.0f);
	}
	
	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, ItemStack stack, EnumHand hand)
	{						
		if(!this.worldObj.isRemote)
		{
			if((stack!=null && stack.getItem()!=BOTWItems.sheikahSlate) || stack==null)
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
			else
			{
				return EnumActionResult.PASS;
			}
		}
		if(stack!=null && stack.getItem()==BOTWItems.sheikahSlate)
			return EnumActionResult.PASS;
		else
			return EnumActionResult.FAIL;
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
		
		super.onUpdate();
	}
	
	@Override
	public double getYOffset()
	{
		return 0.5;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(super.attackEntityFrom(source, amount))
		{
			if(!DamageSource.inWall.equals(source))
			{
				explode();
			}
		}
		return false;
	}
	
	@Override
	public void setFire(int seconds)
	{
		explode();
	}
	
	public void explode()
    {
        if (!this.worldObj.isRemote)
        {
            this.dead = true;
            
           	Explosion explosion = new Explosion(worldObj, this, this.posX, this.posY, this.posZ, 5, false, true);
           	explosion.doExplosionA();
           	explosion.doExplosionB(false);
            
           	WorldServer server=(WorldServer) worldObj;
            server.spawnParticle(EnumParticleTypes.FLAME, posX, posY, posZ, 1000, 0, 0, 0, 0.1, new int[0]);
           	
            this.setDead();
        }
    }

	@Override
	public Iterable<ItemStack> getArmorInventoryList()
	{
		return new ArrayList<>();
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
	public EnumHandSide getPrimaryHand()
	{
		return null;
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
		
		double motionX = -MathHelper.sin((float) Math.toRadians(yaw))*0.5;
		double motionZ = MathHelper.cos((float) Math.toRadians(yaw))*0.5;
		double motionY = MathHelper.sin((float) Math.toRadians(pitch))*0.5;

		motionX+=player.motionX;
		motionY+=player.motionY;
		motionZ+=player.motionZ;
		
		setPositionAndUpdate(player.posX, player.posY, player.posZ);
		setVelocity(motionX, motionY, motionZ);
	}
	
}
