package com.theredmajora.botw.render;

import java.util.Random;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHook extends ModelBase
{
	private ModelBase model;
	
	public ModelHook(ModelBase model)
	{
		this.model=model;
	}

	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		model.render(entityIn, limbSwing, limbSwingAmount, 0, netHeadYaw, headPitch, scale);
	}

	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		model.setRotationAngles(limbSwing, limbSwingAmount, 0, netHeadYaw, headPitch, scaleFactor, entityIn);
	}

	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
	{
		model.setLivingAnimations(entitylivingbaseIn, p_78086_2_, p_78086_3_, 0);
	}

	public ModelBase getModel()
	{
		return model;
	}

	public ModelRenderer getRandomModelBox(Random rand)
	{
		return model.getRandomModelBox(rand);
	}

	public TextureOffset getTextureOffset(String partName)
	{
		return model.getTextureOffset(partName);
	}

	public void setModelAttributes(ModelBase model)
	{
		model.setModelAttributes(model);
	}
}
