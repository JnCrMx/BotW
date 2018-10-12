package com.theredmajora.botw.render.entity;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.entity.EntityShrineLift;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderShrineLift extends Render<EntityShrineLift>
{
	public RenderShrineLift(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShrineLift entity)
	{
		return new ResourceLocation(BOTW.MODID, "textures/entity/shrine_lift.png");	//May not bind textures from MODID:textures/blocks/ for some reasons
	}
	
	@Override
	public void doRender(EntityShrineLift entity, double x, double y, double z, float entityYaw, float partialTicks)
	{		
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
        GlStateManager.disableLighting();
		
        GlStateManager.translate(x, entity.getEntityBoundingBox().maxY+y, z);

		bindTexture(getEntityTexture(entity));
		
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(entity.getEntityBoundingBox().minX, 0, entity.getEntityBoundingBox().minZ);

        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(entity.getEntityBoundingBox().minX, 0, entity.getEntityBoundingBox().maxZ);

        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(entity.getEntityBoundingBox().maxX, 0, entity.getEntityBoundingBox().maxZ);

        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(entity.getEntityBoundingBox().maxX, 0, entity.getEntityBoundingBox().minZ);
		
		GL11.glEnd();

        GlStateManager.enableLighting();
		GlStateManager.enableCull();
        GlStateManager.popMatrix();
	}
}
