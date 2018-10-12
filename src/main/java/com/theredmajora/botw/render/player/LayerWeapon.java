package com.theredmajora.botw.render.player;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.item.ItemBOTWShield;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerWeapon implements LayerRenderer<EntityPlayer>
{
    private final RenderItem itemRenderer;
	private float stacksRendered = 0;
    
    public LayerWeapon(RenderItem itemRenderer)
    {
    	this.itemRenderer = itemRenderer;
	}

	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {	
		//System.out.println(entitylivingbaseIn);
		
    	EntityPlayer player = entitylivingbaseIn;
    	IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);

        for (ItemStack stack : tracker.getRenderingItemStacks())
        {
        	if (stack != null)
    		{
        		ItemStack current = player.inventory.getCurrentItem();
        		if(!(stack == current))
        		{
        			//if(stacksRendered < 6)
    				{
        				if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow)
        				{
    						stacksRendered += 1F;
        					renderNormalItem(stack, player, stacksRendered);
        				}
        				else
        				{
        					if(stack.getItem() instanceof ItemShield || stack.getItem() instanceof ItemBOTWShield)
        					{
        						stacksRendered += 1F;
            					renderShieldItem(stack, player, stacksRendered);
        					}
        				}
    				}
        		}
        		
    		}
    	}
        
        stacksRendered = 0;
    }
    
    public boolean shouldCombineTextures()
    {
        return false;
    }

	private void renderNormalItem(ItemStack item, Entity entity, float stacksRendered)
    {
        if (item != null)
        {
            item.stackSize = 1;
            GlStateManager.pushMatrix();
            //GlStateManager.disableLighting();

            {
                GlStateManager.scale(1.0F, 1.0F, 1.0F);
                
                float posOffset = stacksRendered / 20;
                
                GlStateManager.translate(0.0F, 0.3F, 0.1F + posOffset);
                GlStateManager.rotate(90.0F - (90.0F * stacksRendered), 0.0F, 0.0F, 1.0F);
                
                if (!this.itemRenderer.shouldRenderItemIn3D(item) || item.getItem() instanceof ItemSkull)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.pushAttrib();
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();
            }
            //GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            
        }
    }
	
	private void renderShieldItem(ItemStack item, Entity entity, float stacksRendered)
    {
        if (item != null)
        {
            item.stackSize = 1;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            //TODO: Minecraft ItemShield is too big
            {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (!this.itemRenderer.shouldRenderItemIn3D(item) || item.getItem() instanceof ItemSkull)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }
                
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-0.1, 0.3, -0.1);

                GlStateManager.pushAttrib();
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(item, ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
