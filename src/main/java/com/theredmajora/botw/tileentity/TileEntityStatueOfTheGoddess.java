package com.theredmajora.botw.tileentity;

import java.util.List;

import com.theredmajora.botw.block.BlockStatueOfTheGoddess;
import com.theredmajora.botw.block.BlockStatueOfTheGoddess.EnumPartType;
import com.theredmajora.botw.entity.EntityGoddessItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;

public class TileEntityStatueOfTheGoddess extends TileEntity implements ITickable
{
	public int talkPhase;
	public EntityPlayer player;
	public Item selection;
	
	@Override
	public void update()
	{
		if(!worldObj.isRemote)
		{
			if(worldObj.getBlockState(getPos()).getValue(BlockStatueOfTheGoddess.PART)==EnumPartType.BOTTOM)
			{
				return;
			}
			
			//Check if player is out of range
			List<EntityPlayer> players=worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-3, -3, -3), pos.add(3, 3, 3)));
			if(talkPhase != 0 && !players.contains(player))
			{
				AxisAlignedBB box = new AxisAlignedBB(pos.add(-2, -1, -2), pos.add(2, 1, 2));
				List<EntityGoddessItem> goddessItems = worldObj.getEntitiesWithinAABB(EntityGoddessItem.class, box);
				goddessItems.forEach(entity -> entity.setDead());
				
				player.addChatMessage(new TextComponentTranslation("message.goddess.exit", new Object[0]));
				
				talkPhase=0;
			}

			worldObj.setLightFor(EnumSkyBlock.BLOCK, pos, 0);
			
			//Clear up player object
			if(talkPhase==0)
				player=null;
		}
		else
		{
			worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos.add(1, 0, 0));
		}
	}
}
