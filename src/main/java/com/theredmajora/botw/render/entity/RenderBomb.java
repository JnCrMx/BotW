package com.theredmajora.botw.render.entity;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.entity.EntityBomb;
import com.theredmajora.botw.entity.EntityBomb.BombType;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBomb extends Render<EntityBomb>
{

	public RenderBomb(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBomb entity)
	{
		return null;
	}
	
	@Override
	public void doRender(EntityBomb entity, double x, double y, double z, float entityYaw, float partialTicks)
	{		
		GlStateManager.pushMatrix();
		
		GlStateManager.translate(x, y+0.25, z);
		
		if(entity.getBombType()==BombType.ROUND_BOMB)
		{
			bindTexture(new ResourceLocation(BOTW.MODID, "textures/entity/round_bomb.png"));

			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			
			double radius=1;
		
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2d(0, 0);
			
			for(double a1=0;a1<360;a1+=5)
			{
				for(double a2=0;a2<360;a2+=5)
				{
					double y1=Math.cos(Math.toRadians(a1))*radius;
					double y2=Math.cos(Math.toRadians(a1+5))*radius;

					double r1=Math.sin(Math.toRadians(a1))*radius;
					double r2=Math.sin(Math.toRadians(a1+5))*radius;

					double x1=Math.sin(Math.toRadians(a2))*r1;
					double x2=Math.sin(Math.toRadians(a2+5))*r2;
					
					double z1=Math.cos(Math.toRadians(a2))*r1;
					double z2=Math.cos(Math.toRadians(a2+5))*r2;

					GL11.glVertex3d(x1, y1, z1);					
					GL11.glVertex3d(x2, y1, z1);
					GL11.glVertex3d(x2, y2, z2);
					GL11.glVertex3d(x1, y2, z2);
				}
			}
			
			GL11.glEnd();
			
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
		}
		else if(entity.getBombType()==BombType.SQUARE_BOMB)
		{
			GL11.glBegin(GL11.GL_QUADS);
			
			{
				GL11.glVertex3d(0.0, 0.0, 0.0);
				GL11.glVertex3d(0.5, 0.0, 0.0);
				GL11.glVertex3d(0.5, 5.0, 0.0);
				GL11.glVertex3d(0.0, 5.0, 0.0);
			}
			
			GL11.glEnd();
		}
		
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
}
