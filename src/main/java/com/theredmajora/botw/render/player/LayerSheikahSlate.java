package com.theredmajora.botw.render.player;

import com.theredmajora.botw.items.ItemSheikahSlate;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerSheikahSlate implements LayerRenderer<EntityPlayer>
{
	private static final ResourceLocation TEXTURE_SLATE = new ResourceLocation("botw:textures/model/sheikah_slate.png");
    private final ModelSheikahSlate modelSlate = new ModelSheikahSlate();
    private final RenderPlayer renderPlayer;
    
    public LayerSheikahSlate(RenderPlayer renderPlayer)
    {
    	this.renderPlayer = renderPlayer;
	}

	public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {	
		renderPlayer.bindTexture(TEXTURE_SLATE);
    	EntityPlayer player = (EntityPlayer) entitylivingbaseIn;
        for (ItemStack stack : player.inventory.mainInventory)
        {
        	if (stack != null && stack.getItem() instanceof ItemSheikahSlate)
    		{
                stack = player.inventory.getCurrentItem();
                if(!(stack != null && stack.getItem() instanceof ItemSheikahSlate))
                {
                	{
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.enableBlend();
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(0.4, 0.4, 0.4);
                        GlStateManager.translate(0.65F, 1.1F, 0F);
                        GlStateManager.rotate(270F, 0F, 1F, 0F);
                        if(entitylivingbaseIn.isSneaking())
                        {
                        	GlStateManager.rotate(40F, 0F, 0F, -1F);
                        	GlStateManager.translate(0.0F, 0.45F, 0.0F);
                        }
                        RenderHelper.enableStandardItemLighting();
                        this.modelSlate.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
                        this.modelSlate.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.popMatrix();
                    }
                }
    		}
    	}
    }
    
    public boolean shouldCombineTextures()
    {
        return false;
    }
}