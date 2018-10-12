package com.theredmajora.botw.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.BitSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.BOTWKeyHandler;
import com.theredmajora.botw.packet.BOTWActionPacket;
import com.theredmajora.botw.packet.BOTWActionPacket.BOTWPlayerAction;
import com.theredmajora.botw.packet.BOTWPacketHandler;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiSheikahSlate extends GuiScreen
{
	public static final int GUI_ID = 0;
	private static final ResourceLocation SHEIKAH_SLATE_GUI_TEXTURES = new ResourceLocation(BOTW.MODID, "textures/gui/sheikah_slate.png");
	
	private ItemStack slate;
	
	private boolean hasCamera;
	private int remoteBombLevel;
	private boolean hasMagnesis;
	private int stasisLevel;
	private boolean hasCryonis;
	private boolean hasMasterCycleZero;
	
	private int selection;
	
	public GuiSheikahSlate(ItemStack slate)
	{
		this.slate=slate;
		
		// Every change to "ItemStack slate" or "NBTTagCompound tag" is just temporary.
		// Changes should be submitted by BOTWActionPacket in onGuiClosed()
		if (!slate.hasTagCompound())
			slate.setTagCompound(new NBTTagCompound());
		NBTTagCompound tag = slate.getTagCompound();
		
		hasCamera = tag.getBoolean("hasCamera");
		remoteBombLevel = tag.getInteger("remoteBombLevel");
		hasMagnesis = tag.getBoolean("hasMagnesis");
		stasisLevel = tag.getInteger("stasisLevel");
		hasCryonis = tag.getBoolean("hasCryonis");
		hasMasterCycleZero = tag.getBoolean("hasMasterCycleZero");	
		
		if (tag.getString("mode").isEmpty())
			tag.setString("mode", "camera");
		
		String mode=tag.getString("mode");
		switch (mode)
		{
			case "roundBomb":
				selection=0;
				break;
			case "squareBomb":
				selection=1;
				break;
			case "magnesis":
				selection=2;
				break;
			case "stasis":
				selection=3;
				break;
			case "cryonis":
				selection=4;
				break;
			case "camera":
				selection=5;
				break;
			case "masterCycleZero":
				selection=6;
				break;
			default:
				break;
		}
		
		if(!tag.hasKey("hasCamera"))
			hasCamera=true;
	}
	
	@Override
	public void handleKeyboardInput() throws IOException
	{
		if(!Keyboard.isKeyDown(BOTWKeyHandler.keyBindings[3].getKeyCode()))
		{
			mc.displayGuiScreen(null);
		}
		
		super.handleKeyboardInput();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode==Keyboard.KEY_LEFT)
		{
			//Skip over not available runes
			int selection2 = selection-1;
			while(!checkRune(selection2) && selection2>=0)
				selection2--;
			
			if(selection2>=0)
				selection=selection2;
		}
		if(keyCode==Keyboard.KEY_RIGHT)
		{
			//Skip over not available runes
			int selection2 = selection+1;
			while(!checkRune(selection2) && selection2<=6)
				selection2++;
			
			if(selection2<=6)
				selection=selection2;
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	private void drawRune(int x, int y, int textureX, int textureY, boolean selected, int level)
	{
		if(level==0)
		{
			textureX=0;
			textureY=0;
		}
		if(level==2)
		{
			textureX+=64;
		}

		if(selected)
		{
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		}
		else
		{
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		}
		
		drawTexturedModalRect(x, y, textureX, textureY, 64, 64);
		
		if(selected)
		{
			drawTexturedModalRect(x, y, 64, 0, 64, 64);
		}
	}
	
	@Override
	public void onGuiClosed()
	{					
		BOTWActionPacket packet = new BOTWActionPacket(BOTWPlayerAction.SELECT_SHEIKAH_RUNE, selection);
		BOTWPacketHandler.INSTANCE.sendToServer(packet);
	}
	
	public boolean checkRune(int selection)
	{
		switch (selection)
		{
			case 0:
				return remoteBombLevel>0;
			case 1:
				return remoteBombLevel>0;
			case 2:
				return hasMagnesis;
			case 3:
				return stasisLevel>0;
			case 4:
				return hasCryonis;
			case 5:
				return hasCamera;
			case 6:
				return hasMasterCycleZero;
			default:
				return false;
		}
	}

	/*
	 * 	int selection;
	 *  +------------+-----------+----------+--------+---------+--------+-------------------+
	 * 	|      0     |     1     |     2    |    3   |    4    |   5    |         6         |
	 *  +------------+-----------+----------+--------+---------+--------+-------------------+
	 * 	| Round Bomb | Cube Bomb | Magnesis | Stasis | Cryonis | Camera | Master Cycle Zero |  
	 * 	+------------+-----------+----------+--------+---------+--------+-------------------+
	 */
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{		
		Mouse.setGrabbed(true);
		
		int centerX = mouseX - 32;
		int centerY = this.height/2 - 32; 
		
		int baseX = centerX - selection * 74; 

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		
		this.mc.getTextureManager().bindTexture(SHEIKAH_SLATE_GUI_TEXTURES);
		
		drawRune(baseX + 0*74, centerY, 128, 128, selection==0, remoteBombLevel);			// Round Remote Bomb
		drawRune(baseX + 1*74, centerY, 0,   128, selection==1, remoteBombLevel);			// Cube Remote Bomb
		drawRune(baseX + 2*74, centerY, 64,   64, selection==2, hasMagnesis?1:0);			// Magnesis
		drawRune(baseX + 3*74, centerY, 0,   192, selection==3, stasisLevel);				// Stasis
		drawRune(baseX + 4*74, centerY, 128,  64, selection==4, hasCryonis?1:0);			// Cryonis
		drawRune(baseX + 5*74, centerY, 0,    64, selection==5, hasCamera?1:0);				// Camera
		drawRune(baseX + 6*74, centerY, 192,  64, selection==6, hasMasterCycleZero?1:0);	// Master Cycle Zero
		
		GlStateManager.popMatrix();
		
		int selection2 = -1;
		for(int i=0;i<7;i++)
		{
			int start = baseX + i * 74;
			int end = baseX + i * 74 + 64;
			
			if((this.width/2)>start && (this.width/2)<end)
			{
				selection2=i;
				break;
			}
		}
		
		if(selection2!=-1 && selection2!=selection)
		{
			if(checkRune(selection2))
			{
				int centerOffset = mouseX-centerX;
				if(selection2>selection)
					Mouse.setCursorPosition(mc.displayWidth/2-centerOffset+96, mc.displayHeight/2);	// I'm not sure why I need THIS value (+96), but it works
				else
					Mouse.setCursorPosition(mc.displayWidth/2-centerOffset-32, mc.displayHeight/2);
				
				selection = selection2;
			}
		}
	}
}
