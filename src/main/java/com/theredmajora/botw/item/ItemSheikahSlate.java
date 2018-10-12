package com.theredmajora.botw.item;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.entity.EntityBomb;
import com.theredmajora.botw.entity.EntityBomb.BombType;
import com.theredmajora.botw.packet.BOTWActionPacket;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;
import com.theredmajora.botw.proxy.ClientProxy;
import com.theredmajora.botw.packet.BOTWPacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ItemSheikahSlate extends ItemBOTW
{
	public ItemSheikahSlate()
	{
		super("sheikah_slate");
		setMaxStackSize(1);
		setCreativeTab(BOTW.botwTab);
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem)
	{
		ItemStack stack = entityItem.getEntityItem();
		World world = entityItem.worldObj;
		
		if(stack.hasTagCompound())
		{
			NBTTagCompound tag=stack.getTagCompound();
			if(tag.hasKey("roundBombId"))
			{
				int entityId=tag.getInteger("roundBombId");
				
				EntityBomb bomb = (EntityBomb) world.getEntityByID(entityId);
				if(bomb!=null)
				{
					bomb.setDead();
				}
				tag.removeTag("roundBombId");
			}
			if(tag.hasKey("squareBombId"))
			{
				int entityId=tag.getInteger("squareBombId");
				
				EntityBomb bomb = (EntityBomb) world.getEntityByID(entityId);
				if(bomb!=null)
				{
					bomb.explode();
				}
				tag.removeTag("squareBombId");
			}
		}
		
		return super.onEntityItemUpdate(entityItem);
	}

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {    	
    	if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setString("mode", "roundBomb");
		}    	
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag.getString("mode").isEmpty())
			tag.setString("mode", "camera");
		
		String mode=tag.getString("mode");
		if(mode.equals("roundBomb"))
		{
	    	if(world.isRemote)
	    		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			
			int level = tag.getInteger("remoteBombLevel");
			if(level==0)
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			
			if(tag.hasKey("roundBombId"))
			{
				int entityId=tag.getInteger("roundBombId");
				
				EntityBomb bomb = (EntityBomb) world.getEntityByID(entityId);
				if(bomb!=null)
				{
					bomb.explode();
				}
				tag.removeTag("roundBombId");
			}
			else
			{
				EntityBomb bomb=new EntityBomb(world);
				bomb.setBombType(BombType.ROUND_BOMB);
				
				bomb.setPositionAndUpdate(player.posX, player.posY, player.posZ);
				world.spawnEntityInWorld(bomb);
				
				if(player.onGround && player.getPassengers().isEmpty())
				{
					bomb.startRiding(player, true);
					
					SPacketSetPassengers packet=new SPacketSetPassengers(player);
					((EntityPlayerMP)player).connection.sendPacket(packet);
				}
				
				tag.setInteger("roundBombId", bomb.getEntityId());
			}
		}
		else if(mode.equals("squareBomb"))
		{
	    	if(world.isRemote)
	    		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			
			int level = tag.getInteger("remoteBombLevel");
			if(level==0)
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			
			if(tag.hasKey("squareBombId"))
			{
				int entityId=tag.getInteger("squareBombId");
				
				EntityBomb bomb = (EntityBomb) world.getEntityByID(entityId);
				if(bomb!=null)
				{
					bomb.explode();
				}
				tag.removeTag("squareBombId");
			}
			else
			{
				EntityBomb bomb=new EntityBomb(world);
				bomb.setBombType(BombType.SQUARE_BOMB);
				
				bomb.setPositionAndUpdate(player.posX, player.posY, player.posZ);
				world.spawnEntityInWorld(bomb);
				
				if(player.onGround)
				{
					bomb.startRiding(player, true);
					
					SPacketSetPassengers packet=new SPacketSetPassengers(player);
					((EntityPlayerMP)player).connection.sendPacket(packet);
				}
				
				tag.setInteger("squareBombId", bomb.getEntityId());
			}
		}
		else if(mode.equals("stasis"))
		{
			// We need to be on client side to access Minecraft.pointedEntity
			if(!world.isRemote)
	    		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
			
			Entity pointed = Minecraft.getMinecraft().pointedEntity;
			if(pointed!=null)
			{
				BOTWActionPacket packet = new BOTWActionPacket(BOTWPlayerAction.STASIS_ENTITY, pointed.getEntityId());
				BOTWPacketHandler.INSTANCE.sendToServer(packet);
				
				if(BOTW.proxy instanceof ClientProxy)
				{
					ClientProxy proxy = (ClientProxy) BOTW.proxy;
					proxy.stasisEntityMotions.put(pointed.getEntityId(), new Vec3d(0, 0, 0));
				}
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
