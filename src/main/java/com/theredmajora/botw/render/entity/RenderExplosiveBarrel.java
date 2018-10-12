package com.theredmajora.botw.render.entity;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.entity.EntityExplosiveBarrel;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderExplosiveBarrel extends Render<EntityExplosiveBarrel>
{

	public RenderExplosiveBarrel(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityExplosiveBarrel entity)
	{
		return new ResourceLocation(BOTW.MODID, "textures/entity/explosive_barrel.png");
	}
	
	@Override
	public void doRender(EntityExplosiveBarrel entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.disableCull();
		
		if(renderOutlines)
		{
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		
		bindTexture(getEntityTexture(entity));
		
		GL11.glBegin(GL11.GL_QUADS);

		//Front
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3d(-0.5, 0, 0.5);

		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3d(0.5, 0, 0.5);

		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3d(0.5, 1, 0.5);

		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(-0.5, 1, 0.5);
		
		//Back
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3d(-0.5, 0, -0.5);

		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3d(0.5, 0, -0.5);

		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3d(0.5, 1, -0.5);

		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(-0.5, 1, -0.5);
		
		//Left
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3d(-0.5, 0, -0.5);

		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3d(-0.5, 0, 0.5);

		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3d(-0.5, 1, 0.5);

		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(-0.5, 1, -0.5);
		
		//Right
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex3d(0.5, 0, -0.5);

		GL11.glTexCoord2d(1, 1);
		GL11.glVertex3d(0.5, 0, 0.5);

		GL11.glTexCoord2d(1, 0);
		GL11.glVertex3d(0.5, 1, 0.5);

		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(0.5, 1, -0.5);
		
		GL11.glEnd();
		
		if(renderOutlines)
		{
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
}
