package com.theredmajora.botw;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.theredmajora.botw.blocks.BOTWBlocks;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.items.BOTWItems;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.proxy.CommonProxy;
import com.theredmajora.botw.render.player.ModelRendererBase;
import com.theredmajora.botw.render.player.PlayerRendererBase;
import com.theredmajora.botw.tileentities.TileEntityTempIce;

import api.player.model.ModelPlayerAPI;
import api.player.render.RenderPlayerAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = BOTW.MODID, name = BOTW.MODNAME, version = BOTW.VERSION)
public class BOTW
{
	@Instance
	public static BOTW instance = new BOTW();
	
    public static final String MODID = "botw";
    public static final String MODNAME = "Breath of the Wild";
    public static final String VERSION = "1.0";
    
    @SidedProxy(clientSide="com.theredmajora.botw.proxy.ClientProxy", serverSide="com.theredmajora.botw.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    public static final BOTWTab botwTab = new BOTWTab();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {	
    	RenderPlayerAPI.register(BOTW.MODID, PlayerRendererBase.class);
    	ModelPlayerAPI.register(BOTW.MODID, ModelRendererBase.class);
    	BOTWBlocks.init();
    	BOTWItems.init();
    	BOTWKeyHandler.init();
    	BOTWPacketHandler.init();
    	CapabilityItemTracker.register();
    	proxy.init();
		MinecraftForge.EVENT_BUS.register(new BOTWEvents());
		
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit();
    }
}
