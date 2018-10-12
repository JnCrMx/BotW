package com.theredmajora.botw.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class GuiStaminaOverlay extends Gui
{
	private Minecraft client;
	
	public GuiStaminaOverlay(Minecraft clientIn)
    {
        this.client = clientIn;
    }
	
	public boolean shouldRender()
	{
		EntityPlayer player=client.thePlayer;
		IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
		
		return playerTracker.getStamina()!=playerTracker.getMaxStamina();
	}
	
	public void render()
	{
		EntityPlayer player=client.thePlayer;
		IPlayerTracker playerTracker = player.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
		
		int centerX=(int) (client.displayWidth/4.75);
		int centerY=client.displayHeight/4;		
		if(client.gameSettings.thirdPersonView==0)
		{
			centerX=(int) (client.displayWidth/9);
			centerY=(int) (client.displayHeight/2.25);
		}		
		int baseRadius = client.displayWidth/95;
		
		int color = Color.GREEN.getRGB();
		if(playerTracker.isExhausted())
		{
			color = Color.RED.getRGB();
		}
		final int WHITE = Color.WHITE.getRGB();
		
		if(playerTracker.getMaxStamina()==1000)
		{
			double stamina1 = playerTracker.getStamina();
			stamina1/=1000;
			stamina1*=360;

			drawRing(centerX, centerY, baseRadius, baseRadius, (int)stamina1, color);
		}
		else if(playerTracker.getMaxStamina()>1000 && playerTracker.getMaxStamina()<=2000)
		{
			double stamina1 = playerTracker.getStamina();
			if(stamina1>=1000)
			{
				drawRing(centerX, centerY, baseRadius, baseRadius, 360, color);
				
				double stamina2 = playerTracker.getStamina();
				if(stamina2==2000)
				{
					stamina2=360;
				}
				else
				{
					stamina2%=1000;
					stamina2/=1000;
					stamina2*=360;
				}
				drawRing(centerX, centerY, baseRadius*2, baseRadius/3, (int)stamina2, color);
			}
			else
			{
				stamina1/=1000;
				stamina1*=360;
				drawRing(centerX, centerY, baseRadius, baseRadius, (int)stamina1, color);
			}
		}
		else if(playerTracker.getMaxStamina()>2000 && playerTracker.getMaxStamina()<=3000)
		{
			double stamina1 = playerTracker.getStamina();
			if(stamina1>=1000)
			{
				drawRing(centerX, centerY, baseRadius, baseRadius, 360, color);
				
				double stamina2 = playerTracker.getStamina();
				if(stamina2>=2000)
				{
					drawRing(centerX, centerY, baseRadius*2, baseRadius/3, 360, color);
					
					double stamina3 = playerTracker.getStamina();
					if(stamina3==3000)
					{
						drawRing(centerX, centerY, baseRadius*2+baseRadius/3, baseRadius/3, 360, color);
					}
					else
					{
						stamina2%=1000;
						stamina2/=1000;
						stamina2*=360;
						drawRing(centerX, centerY, baseRadius*2+baseRadius/3, baseRadius/3, (int)stamina2, color);
					}
				}
				else
				{
					stamina2%=1000;
					stamina2/=1000;
					stamina2*=360;
					drawRing(centerX, centerY, baseRadius*2, baseRadius/3, (int)stamina2, color);
				}
			}
			else
			{
				stamina1/=1000;
				stamina1*=360;
				drawRing(centerX, centerY, baseRadius, baseRadius, (int)stamina1, color);
			}
		}
	}
	
	private static void drawRing(int cx, int cy, int radius, int width, int degrees, int color)
	{
		float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        
        GL11.glBegin(GL11.GL_QUADS);
        for(double i=0;i<degrees;i++)
        {
        	double x1 = cx + (MathHelper.sin((float) Math.toRadians(360-i))*((double)radius));
        	double y1 = cy - (MathHelper.cos((float) Math.toRadians(360-i))*((double)radius));
        	
        	double x2 = cx + (MathHelper.sin((float) Math.toRadians(360-i))*((double)radius+width));
        	double y2 = cy - (MathHelper.cos((float) Math.toRadians(360-i))*((double)radius+width));
        	
        	double x3 = cx + (MathHelper.sin((float) Math.toRadians(360-i+1))*((double)radius+width));
        	double y3 = cy - (MathHelper.cos((float) Math.toRadians(360-i+1))*((double)radius+width));
        	
        	double x4 = cx + (MathHelper.sin((float) Math.toRadians(360-i+1))*((double)radius));
        	double y4 = cy - (MathHelper.cos((float) Math.toRadians(360-i+1))*((double)radius));

        	GL11.glVertex3d(x3, y3, 0.0D);
        	GL11.glVertex3d(x2, y2, 0.0D);
        	GL11.glVertex3d(x1, y1, 0.0D);
        	GL11.glVertex3d(x4, y4, 0.0D);
        }
        GL11.glEnd();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
	}
}
