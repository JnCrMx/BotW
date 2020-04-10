package com.theredmajora.botw.render.player;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker.BOTWRenderAction;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerQuiver implements LayerRenderer<AbstractClientPlayer>
{
	private static final ResourceLocation TEXTURE_QUIVER = new ResourceLocation("botw:textures/model/quiver.png");
    private final ModelQuiver modelQuiver = new ModelQuiver();
    private final RenderPlayer renderPlayer;
    
    public LayerQuiver(RenderPlayer renderPlayer)
    {
    	this.renderPlayer = renderPlayer;
    }
    
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {	
		this.renderPlayer.bindTexture(TEXTURE_QUIVER);
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
    	GlStateManager.rotate(-10F, 1F, 0F, 0F);
        GlStateManager.scale(0.4, 0.4, 0.4);
    	GlStateManager.translate(0.0F, 0.4F, 0.75F);
    	GlStateManager.rotate(180F, 0.0F, 1.0F, 0.0F);
    	
    	IItemTracker tracker = entitylivingbaseIn.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
    	
    	if(tracker.getRenderAction()==BOTWRenderAction.BACKFLIP)
    	{
    		GlStateManager.rotate(-50F, 1F, 0F, 0F);
    		GlStateManager.translate(0.0f, -0.4f, -0.4f);
    	}
    	else if(renderPlayer.getMainModel().isSneak)	//Sneaking
        {
        	GlStateManager.rotate(-30F, 1F, 0F, 0F);
        	GlStateManager.translate(0.0F, 0.25F, 0.0F);
        }
    	if(!renderPlayer.getMainModel().isSneak && entitylivingbaseIn.isSneaking())	//Climbing down
        {
        	GlStateManager.translate(0.0F, 0.5F, 0.0F);
        }
        RenderHelper.enableStandardItemLighting();
        this.modelQuiver.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
        this.modelQuiver.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
    
    public boolean shouldCombineTextures()
    {
        return false;
    }
}