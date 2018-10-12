package com.theredmajora.botw;

import com.theredmajora.botw.block.BOTWBlocks;
import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.dimension.BOTWDimensions;
import com.theredmajora.botw.entity.BOTWEntities;
import com.theredmajora.botw.inventory.tab.BOTWTab;
import com.theredmajora.botw.inventory.tab.BOTWTabShields;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.proxy.CommonProxy;
import com.theredmajora.botw.render.player.ModelRendererBase;
import com.theredmajora.botw.render.player.PlayerRendererBase;
import com.theredmajora.botw.world.WorldTypeHyrule;

import api.player.model.ModelPlayerAPI;
import api.player.render.RenderPlayerAPI;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    public static final BOTWTabShields botwTabShields = new BOTWTabShields();
    
    public static WorldType hyruleWorldType;
    
    public static BOTWEvents eventHandler;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {	
    	RenderPlayerAPI.register(BOTW.MODID, PlayerRendererBase.class);
    	ModelPlayerAPI.register(BOTW.MODID, ModelRendererBase.class);
    	BOTWBlocks.init();
    	BOTWItems.init();
    	BOTWKeyHandler.init();
    	BOTWPacketHandler.init();
    	BOTWDimensions.init();
    	BOTWEntities.init();
    	
    	CapabilityItemTracker.register();
    	CapabilityPlayerTracker.register();
    	
    	proxy.init();
    	eventHandler=new BOTWEvents();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		
		hyruleWorldType = new WorldTypeHyrule();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit();
    }
}
