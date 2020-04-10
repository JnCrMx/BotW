package com.theredmajora.botw.proxy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.gui.GuiSheikahSlate;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.render.RenderHook;
import com.theredmajora.botw.render.item.CustomTileEntityItemStackRenderer;

import com.theredmajora.botw.util.BOTWActionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxy extends CommonProxy
{
	// entityId -> motion
	public HashMap<Integer, Vec3d> stasisEntityMotions = new HashMap<>();
	
	@Override
	public void registerItemRenderer(Item item, int meta, String name)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(BOTW.MODID + ":" + name, "inventory"));
	}
	
	@Override
	public void init()
	{
		super.init();
		
		OBJLoader.INSTANCE.addDomain(BOTW.MODID);
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		
		TileEntityItemStackRenderer.instance=new CustomTileEntityItemStackRenderer();
		
		BOTW.logger.info("Hooking entity renders");
		Map < Class <? extends Entity > , Render <? extends Entity >> entityRenderMap = Minecraft.getMinecraft().getRenderManager().entityRenderMap;
		for(Class<? extends Entity> clazz : entityRenderMap.keySet())
		{
			if(BOTWActionHelper.SheikahSlate.isStasisTarget(clazz))  // only hook into stasis targets
			{
				Render<?> render = entityRenderMap.get(clazz);
				entityRenderMap.replace(clazz, new RenderHook(render.getRenderManager(), render));
				BOTW.logger.info("Replaced render for " + clazz.getName() + " with " + RenderHook.class.getName());
			}
		}
		BOTW.logger.info("Hooking complete");
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				ItemStack slate = null;
				//Search for sheikah slate	//TODO: Move this maybe?
				for (ItemStack stack : player.inventory.mainInventory)
				{
					if(stack != null && stack.getItem() == BOTWItems.sheikahSlate)
					{
						slate=stack;
						break;
					}
				}
				if(slate != null)
				{
					return new GuiSheikahSlate(slate);
				}
				break;
			
			default:
				break;
		}
		return null;
	}
}
