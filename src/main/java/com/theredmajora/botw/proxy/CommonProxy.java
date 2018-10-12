package com.theredmajora.botw.proxy;

import java.lang.reflect.Field;
import java.util.Map;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.particle.ParticleBlueSplash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy implements IGuiHandler
{	
	public void registerItemRenderer(Item item, int meta, String name) {}

	public void init()
	{
	}

	public void postInit()
	{		
		registerParticle("blue_splash", 301, new ParticleBlueSplash.Factory());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(BOTW.instance, this);
	}
	
	/**
	 * Call in <b>{@link #postInit()}</b>
	 */
	public void registerParticle(String name, int id, IParticleFactory factory)
	{
		EnumParticleTypes particleType = EnumHelper.addEnum(EnumParticleTypes.class, name.toUpperCase(), new Class[] {String.class, int.class, boolean.class}, new Object[] {name, id, false});
		
		//Reflecting into the universe ^^
		try
		{
			Field field = particleType.getClass().getDeclaredField("PARTICLES");
			field.setAccessible(true);
			Map<Integer, EnumParticleTypes> map = (Map<Integer, EnumParticleTypes>) field.get(null);
			map.put(id, particleType);
			
			field = particleType.getClass().getDeclaredField("BY_NAME");
			field.setAccessible(true);
			Map<String, EnumParticleTypes> map2 = (Map<String, EnumParticleTypes>) field.get(null);
			map2.put(name, particleType);
			
			ParticleManager particleManager=Minecraft.getMinecraft().effectRenderer;			
			field=particleManager.getClass().getDeclaredField("particleTypes");
			field.setAccessible(true);
			Map<Integer, IParticleFactory> map3 = (Map<Integer, IParticleFactory>) field.get(particleManager);
			map3.put(id, factory);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{				
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
