package com.theredmajora.botw.proxy;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.gui.GuiSheikahSlate;
import com.theredmajora.botw.item.BOTWItems;
import com.theredmajora.botw.packet.BOTWPacketHandler;
import com.theredmajora.botw.particle.ParticleBlueSplash;
import com.theredmajora.botw.render.RenderHook;
import com.theredmajora.botw.render.item.CustomTileEntityItemStackRenderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelArmorStand;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.network.IGuiHandler;

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
		
		System.out.println("Hooking entity renders");
		Map < Class <? extends Entity > , Render <? extends Entity >> entityRenderMap = Minecraft.getMinecraft().getRenderManager().entityRenderMap;
		for(Class<? extends Entity> clazz : entityRenderMap.keySet())
		{
			Render render = entityRenderMap.get(clazz);
			entityRenderMap.replace(clazz, new RenderHook(render.getRenderManager(), render));
			System.out.println("Replaced render for "+clazz.getName()+" with "+RenderHook.class.getName());
		}
		System.out.println("Hooking complete");
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
