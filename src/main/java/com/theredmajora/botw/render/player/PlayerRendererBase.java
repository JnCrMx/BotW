package com.theredmajora.botw.render.player;

import java.util.ArrayList;
import java.util.List;

import api.player.render.RenderPlayerAPI;
import api.player.render.RenderPlayerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;

public class PlayerRendererBase extends RenderPlayerBase
{
	public boolean createdTextureThisSession = false;
	private List<LayerRenderer> layerRenderer = new ArrayList<LayerRenderer>();
	private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
	
	public PlayerRendererBase(RenderPlayerAPI renderPlayerAPI)
	{
		super(renderPlayerAPI);
		layerRenderer.add(new LayerQuiver(this.renderPlayer));
		layerRenderer.add(new LayerSheikahSlate(this.renderPlayer));
		layerRenderer.add(new LayerWeapon(this.itemRenderer));
	}
	
	@Override
	public void renderLayers(AbstractClientPlayer entity, float f1, float f2, float f3, float f4, float f5, float f6, float f7)
	{		
		super.renderLayers(entity, f1, f2, f3, f4, f5, f6, f7);
		for (LayerRenderer renderer : layerRenderer)
		{
			renderer.doRenderLayer((EntityLivingBase) entity, f1, f2, f3, f4, f5, f6, f7);
		}
	}
	
	@Override
	public void renderModel(AbstractClientPlayer entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.renderModel(entity, f, f1, f2, f3, f4, f5);	
		
		//Caused some weird render bugs... or maybe not? idk
		//this.renderLayers(entity, (float) f, (float) f1, (float) f2, f3, f4, f5, 0.0625F);
	}
}