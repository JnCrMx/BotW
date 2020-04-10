package com.theredmajora.botw.render.player;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker.BOTWRenderAction;
import com.theredmajora.botw.entity.IEntityCarriable;
import com.theredmajora.botw.item.ItemParaglider;
import com.theredmajora.botw.item.ItemSheikahSlate;

import api.player.model.ModelPlayerAPI;
import api.player.model.ModelPlayerBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ModelRendererBase extends ModelPlayerBase
{
	public ModelRendererBase(ModelPlayerAPI modelPlayerAPI)
	{
		super(modelPlayerAPI);
	}
	
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{		
		EntityPlayer player = (EntityPlayer) entity;
		IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
		
		if(tracker.getRenderAction()==BOTWRenderAction.BACKFLIP)
		{
			int backflipTime = tracker.getBackflipTime();
			GL11.glRotated(backflipTime*10+(f2%10), 1, 0, 0);
		}
		
		super.render(entity, f, f1, f2, f3, f4, f5);
	}
	
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float f3, float f4, float f5, Entity entity)
	{		
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, f3, f4, f5, entity);
		
		EntityPlayer player = (EntityPlayer) entity;
		ItemStack stack = player.inventory.getCurrentItem();

		IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
		
		if (stack != null)
		{
			if (stack.getItem() instanceof ItemParaglider)
			{
				modelBiped.bipedRightArm.rotateAngleY = -0.2F + modelBiped.bipedBody.rotateAngleY;
				modelBiped.bipedRightArm.rotateAngleX = -((float) Math.PI) + modelBiped.bipedBody.rotateAngleX;
				modelBiped.bipedLeftArm.rotateAngleY = 0.2F + modelBiped.bipedBody.rotateAngleY;
				modelBiped.bipedLeftArm.rotateAngleX = -((float) Math.PI) + modelBiped.bipedBody.rotateAngleX;
				
				if (!entity.onGround)
				{
					if (entity == null || !((EntityPlayer) entity).capabilities.isFlying)
					{
						modelBiped.bipedLeftLeg.rotateAngleX = modelBiped.bipedBody.rotateAngleX + 0.2F
								+ MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F
										/ 30.0F;
						modelBiped.bipedLeftLeg.rotateAngleY = modelBiped.bipedBody.rotateAngleY;
						modelBiped.bipedLeftLeg.rotateAngleZ = modelBiped.bipedBody.rotateAngleZ - 0.1F
								- MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F
										/ 60.0F;
						modelBiped.bipedRightLeg.rotateAngleX = modelBiped.bipedBody.rotateAngleX + 0.2F
								+ MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F
										/ 30.0F;
						modelBiped.bipedRightLeg.rotateAngleY = modelBiped.bipedBody.rotateAngleY;
						modelBiped.bipedRightLeg.rotateAngleZ = modelBiped.bipedBody.rotateAngleZ + 0.1F
								- MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F
										/ 60.0F;						
					}
				}
			}
			if (stack.getItem() instanceof ItemSheikahSlate)
			{
				modelBiped.bipedRightArm.rotateAngleY = -0.2F + modelBiped.bipedBody.rotateAngleY;
				modelBiped.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + modelBiped.bipedHead.rotateAngleX + 0.075F;
				modelBiped.bipedLeftArm.rotateAngleY = 0.2F + modelBiped.bipedBody.rotateAngleY;
				modelBiped.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + modelBiped.bipedHead.rotateAngleX + 0.075F;
			}
		}
		
		if (!player.getPassengers().isEmpty())
		{
			Entity firstPassenger = player.getPassengers().get(0);
			if (firstPassenger instanceof IEntityCarriable)
			{
				modelBiped.bipedRightArm.rotateAngleY = 0;
				modelBiped.bipedRightArm.rotateAngleX = (float) Math.toRadians(180);
				
				modelBiped.bipedLeftArm.rotateAngleY = 0;
				modelBiped.bipedLeftArm.rotateAngleX = (float) Math.toRadians(180);
			}
		}
		
		if (player.onGround)
		{
			modelBiped.bipedLeftArm.offsetY=0;
			modelBiped.bipedRightArm.offsetY=0;
				
			modelBiped.bipedLeftLeg.offsetY=0;
			modelBiped.bipedRightLeg.offsetY=0;
		}
		else
		{
			World world = player.worldObj;
			
			float yaw = player.rotationYaw;
			double x = player.posX - MathHelper.sin((float) Math.toRadians(yaw)) * 0.6;
			double z = player.posZ + MathHelper.cos((float) Math.toRadians(yaw)) * 0.6;
			
			BlockPos pos = new BlockPos(x, player.posY, z);
			IBlockState state1 = world.getBlockState(pos);
			IBlockState state2 = world.getBlockState(pos.add(0, 1, 0));
			if (state2.isFullBlock())	//Climb with arms
			{				
				if(player.isSneaking())
				{
					modelBiped.bipedLeftArm.rotateAngleX  = 	-(float) Math.toRadians(170);
					modelBiped.bipedRightArm.rotateAngleX = 	-(float) Math.toRadians(170);

					double sinX = ageInTicks*0.5;
					modelBiped.bipedLeftArm.offsetY		= -(MathHelper.cos((float) sinX)+1)*0.1f;
					modelBiped.bipedRightArm.offsetY	= -(MathHelper.sin((float) sinX)+1)*0.1f;
					
					modelBiped.isSneak=false;
				}
				else if(tracker.getRenderAction()==BOTWRenderAction.CLIMB_UP)
				{
					modelBiped.bipedLeftArm.rotateAngleX  = 	-(float) Math.toRadians(170);
					modelBiped.bipedRightArm.rotateAngleX = 	-(float) Math.toRadians(170);
					
					double sinX = ageInTicks*0.5;
					modelBiped.bipedLeftArm.offsetY		= -(MathHelper.sin((float) sinX)+1)*0.1f;
					modelBiped.bipedRightArm.offsetY	= -(MathHelper.cos((float) sinX)+1)*0.1f;
				}
				else
				{
					modelBiped.bipedLeftArm.rotateAngleX  = 	-(float) Math.toRadians(170);
					modelBiped.bipedRightArm.rotateAngleX = 	-(float) Math.toRadians(170);
					
					modelBiped.bipedLeftArm.offsetY  =	0.0f;
					modelBiped.bipedRightArm.offsetY =	0.0f;
				}
			}
			if(state1.isFullBlock())	//Climb with legs
			{
				if(player.isSneaking())
				{
					modelBiped.bipedLeftLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedLeftLeg.rotateAngleZ	=	-(float) Math.toRadians(10);
					
					modelBiped.bipedRightLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedRightLeg.rotateAngleZ	=	(float) Math.toRadians(10);

					double sinX = ageInTicks*0.5;
					modelBiped.bipedLeftLeg.offsetY	= -(MathHelper.sin((float) sinX)+1)*0.1f;
					modelBiped.bipedRightLeg.offsetY	= -(MathHelper.cos((float) sinX)+1)*0.1f;
					
					modelBiped.isSneak=false;
				}
				else if(tracker.getRenderAction()==BOTWRenderAction.CLIMB_UP)
				{
					modelBiped.bipedLeftLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedLeftLeg.rotateAngleZ	=	-(float) Math.toRadians(10);
					
					modelBiped.bipedRightLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedRightLeg.rotateAngleZ	=	(float) Math.toRadians(10);

					double sinX = ageInTicks*0.5;
					modelBiped.bipedLeftLeg.offsetY	= -(MathHelper.cos((float) sinX)+1)*0.1f;
					modelBiped.bipedRightLeg.offsetY	= -(MathHelper.sin((float) sinX)+1)*0.1f;
				}
				else
				{
					modelBiped.bipedLeftLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedLeftLeg.rotateAngleZ	=	-(float) Math.toRadians(10);
					
					modelBiped.bipedRightLeg.rotateAngleX	=	-(float) Math.toRadians(10);
					modelBiped.bipedRightLeg.rotateAngleZ	=	(float) Math.toRadians(10);
				}
			}
		}

		if(tracker.getRenderAction()==BOTWRenderAction.BACKFLIP)
		{
			modelBiped.bipedBody.rotateAngleX		=	(float) Math.toRadians(45);
			modelBiped.bipedHead.rotateAngleX		=	(float) Math.toRadians(60);
			
			modelBiped.bipedLeftLeg.rotateAngleX	=	(float) Math.toRadians(-40);
			modelBiped.bipedRightLeg.rotateAngleX	=	(float) Math.toRadians(-40);
			modelBiped.bipedLeftLeg.offsetZ			=	0.5f;
			modelBiped.bipedRightLeg.offsetZ		=	0.5f;
			modelBiped.bipedLeftLeg.offsetY			=	-0.25f;
			modelBiped.bipedRightLeg.offsetY		=	-0.25f;
		}
		else
		{
			modelBiped.bipedLeftLeg.offsetZ 		=	0.0f;
			modelBiped.bipedRightLeg.offsetZ 		= 	0.0f;
		}
	}
}