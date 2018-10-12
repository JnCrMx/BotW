package com.theredmajora.botw.entity;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.render.entity.RenderBomb;
import com.theredmajora.botw.render.entity.RenderExplosiveBarrel;
import com.theredmajora.botw.render.entity.RenderShrineLift;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BOTWEntities
{
	public static void init()
	{
		//Register entities
		EntityRegistry.registerModEntity(EntityBomb.class, "SheikahBomb", 0, BOTW.instance, 100, 1, true);
		EntityRegistry.registerModEntity(EntityExplosiveBarrel.class, "ExplosiveBarrel", 1, BOTW.instance, 100, 1, true);
		EntityRegistry.registerModEntity(EntityGoddessItem.class, "GoddessItem", 2, BOTW.instance, 100, 1, false);
		EntityRegistry.registerModEntity(EntityShrineLift.class, "ShrineLift", 3, BOTW.instance, 100, 1, false);
		
		//Register rendering handlers for entities
		RenderingRegistry.registerEntityRenderingHandler(EntityBomb.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager manager)
			{
				return new RenderBomb(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityExplosiveBarrel.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager manager)
			{
				return new RenderExplosiveBarrel(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityShrineLift.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager manager)
			{
				return new RenderShrineLift(manager);
			}
		});
	}
}
