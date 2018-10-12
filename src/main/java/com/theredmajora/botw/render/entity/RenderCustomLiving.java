package com.theredmajora.botw.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCustomLiving extends RenderLiving<EntityLiving>
{
	private ResourceLocation texture;

	public RenderCustomLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, ResourceLocation texture, float shadowsizeIn)
	{
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
		this.texture=texture;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity)
	{
		return texture;
	}
	
}
