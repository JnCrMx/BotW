package com.theredmajora.botw.render;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.capability.playertracker.CapabilityPlayerTracker;
import com.theredmajora.botw.capability.playertracker.IPlayerTracker;
import com.theredmajora.botw.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHook extends Render
{
	private Render render;

	/**
	 * A list of {@link net.minecraft.client.model.ModelBase} classes this hook might not affect.
	 * If the target render of this hook appears in this list the <code>mainModel</code> of the render (if it extends {@link net.minecraft.client.renderer.entity.RenderLivingBase})
	 * will not be replaced by {@link com.theredmajora.botw.render.ModelHook}.
	 * This may prevent the animation from stopping while an {@link net.minecraft.entity.EntityLivingBase} is affected by Stasis.
	 */
	public static final List<Class<? extends ModelBase>> FORBIDDEN_MODELS = new ArrayList<>();
	/**
	 * A list of {@link net.minecraft.client.renderer.entity.Render} classes this hook might not affect.
	 * If the target {@link net.minecraft.client.renderer.entity.Render} of this hook appears in this list {@link #doRender(Entity, double, double, double, float, float)}
	 * will result in <code>render.doRender(entity, x, y, z, entityYaw, partialTicks);</code>
	 * where <code>render</code> is the registered instance of the target {@link net.minecraft.client.renderer.entity.Render}.
	 * The {@link net.minecraft.client.renderer.entity.Render} still will be replaced by {@link com.theredmajora.botw.render.RenderHook}.
	 * This will prevent the animation from stopping while an {@link net.minecraft.entity.EntityLivingBase} is affected by Stasis.
	 */
	public static final List<Class<? extends ModelBase>> FORBIDDEN_RENDERS = new ArrayList<>();
	
	static
	{
		/*
		 * net.minecraft.client.renderer.entity.RenderGuardian.doRender(EntityGuardian, double, double, double, float, float) tries to
		 * cast com.theredmajora.botw.render.ModelHook to net.minecraft.client.model.ModelGuardian
		 * which would result in a java.lang.ClassCastException and would crash the game.
		 * Replacing net.minecraft.client.renderer.entity.RenderGuardian seems to be okay.
		 */
		FORBIDDEN_MODELS.add(ModelGuardian.class); 
		/*
		 * net.minecraft.client.renderer.entity.RenderWitch.doRender(EntityWitch, double, double, double, float, float) tries to
		 * cast com.theredmajora.botw.render.ModelHook to net.minecraft.client.model.ModelWitch
		 * which would result in a java.lang.ClassCastException and would crash the game.
		 * Replacing net.minecraft.client.renderer.entity.RenderWitch seems to be okay.
		 */
		FORBIDDEN_MODELS.add(ModelWitch.class);
		/*
		 * net.minecraft.client.renderer.entity.RenderShulker.doRender(EntityShulker, double, double, double, float, float) tries to
		 * cast com.theredmajora.botw.render.ModelHook to net.minecraft.client.model.ModelShulker
		 * which would result in a java.lang.ClassCastException and would crash the game.
		 * Replacing net.minecraft.client.renderer.entity.RenderShulker seems to be okay.
		 */
		FORBIDDEN_MODELS.add(ModelShulker.class);
	}

	private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(BOTW.MODID,"textures/effect/stasis_chain.png");
	private static final ResourceLocation MOTION_ARROW_TEXTURE = new ResourceLocation(BOTW.MODID,"textures/effect/stasis_motion_arrow.png");
	
	public RenderHook(RenderManager renderManager, Render render)
	{
		super(renderManager);
		this.render=render;
	}
	
	public void setRenderOutlines(boolean renderOutlinesIn)
	{
		render.setRenderOutlines(renderOutlinesIn);
	}

	public boolean shouldRender(Entity livingEntity, ICamera camera, double camX, double camY, double camZ)
	{
		return render.shouldRender(livingEntity, camera, camX, camY, camZ);
	}

	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		if(render instanceof RenderPlayer)
		{
			System.out.println(render);
		}
		
		if(!FORBIDDEN_RENDERS.contains(render.getClass()))	
		{
			if(entity instanceof EntityLivingBase)
			{
				//TODO: Test this on server; Should work...; Tested on local loopback LAN world
				//We might need a packet for this
				EntityLivingBase living = (EntityLivingBase) entity;
				if(BOTW.proxy instanceof ClientProxy)
				{
					ClientProxy proxy = (ClientProxy) BOTW.proxy;
					//Stasis applies
					
					IPlayerTracker tracker=Minecraft.getMinecraft().thePlayer.getCapability(CapabilityPlayerTracker.BOTW_PLAYERTRACKER_CAP, null);
					
					if(tracker.getCurrentStasisEntity()==entity.getEntityId())
					{
						entity.ticksExisted=0;	//Stop animation
						
						// In game (BotW) every entity affected by stasis has something like a yellow filter with outlines
						// This doesn't look too bad, but it's replacing the Entity texture, which makes the Entity look weird
					//	entity.setGlowing(true);
						setRenderOutlines(true);				
						
						//For outline color
						//We just want this to exist on client side! Not on server side!
						if(entity.worldObj.getScoreboard().getTeam("stasis")==null)
						{
							ScorePlayerTeam team = entity.worldObj.getScoreboard().createTeam("stasis");
							//Outline color
							team.setNamePrefix("§e");
						}
						entity.worldObj.getScoreboard().addPlayerToTeam(entity.getCachedUniqueIdString(), "stasis");
						
						// Idea: Make MathHelper.sin(float) = 0
						// Won't work because MathHelper.SIN_TABLE is filled up in a static{} block
						// Would theoretically work but could cause problems when using MathHelper.sin(float) for other purposes
						
						// Idea: Make Render.handleRotationFloat(T<extends EntityLivingBase>, float) = 0
						// Won't work because it's very hard to reflect into methods
						
						// Idea: Replace mainModel and set parameter ageInTicks (computed by Render.handleRotationFloat(EntityChicken, float)) to 0
						// Works
						// FIXME: Make ageInTicks constant to it's value on Stasis start -> smooth animation suspension
						
						if(render instanceof RenderLivingBase<?>)
						{
							try
							{
								Field fMainModel = RenderLivingBase.class.getDeclaredField("mainModel");
								fMainModel.setAccessible(true);
								
								ModelBase mainModel = (ModelBase) fMainModel.get(render);
								
								if(!FORBIDDEN_MODELS.contains(mainModel.getClass()))
								{
									if(!(mainModel instanceof ModelHook))
									{
										ModelHook hook = new ModelHook(mainModel);
										
										fMainModel.set(render, hook);
									}
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}

						GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
						render.doRender(entity, x, y, z, entityYaw, 0);
						GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
						
						//TODO: Render effects (pulsing, etc.)
						
						//Render chains
						GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
						bindTexture(CHAIN_TEXTURE);
		
						GlStateManager.pushMatrix();
						
						GlStateManager.disableOutlineMode();

						GlStateManager.disableLighting();
						GlStateManager.disableCull();
						
						GlStateManager.translate(x, y, z);
						
						for(int h=0;h<4;h++)
						{
							GlStateManager.pushMatrix();
							
							GlStateManager.rotate((h+1)*95, 1, 1, 1);
							
							for(int i=0;i<100;i++)
							{
								GlStateManager.pushMatrix();
								
								if(i%2==0)
								{
									GlStateManager.translate(i*0.075, 0.1+0.05, -0.05);
									GlStateManager.rotate(90, 1, 0, 0);
								}
								else
								{
									GlStateManager.translate(i*0.075, 0.1, 0);
								}
													
								GlStateManager.scale(0.1, 0.1, 0.1);
		
								GL11.glBegin(GL11.GL_QUADS);
				
								GL11.glTexCoord2d(0, 0);
								GL11.glVertex3d(0, 0, 0);
								GL11.glTexCoord2d(0, 1);
								GL11.glVertex3d(0, 1, 0);
								GL11.glTexCoord2d(1, 1);
								GL11.glVertex3d(1, 1, 0);
								GL11.glTexCoord2d(1, 0);
								GL11.glVertex3d(1, 0, 0);
								
								GL11.glEnd();
								
								GlStateManager.popMatrix();
							}
							
							GlStateManager.popMatrix();
						}
						
						//Render motion arrow
						bindTexture(MOTION_ARROW_TEXTURE);
						
						Vec3d motion = proxy.stasisEntityMotions.get(living.getEntityId());
						if(motion==null)
							motion=new Vec3d(0, 0, 0);
						
						GlStateManager.color((float) (0.5f+motion.lengthVector()*0.1f), (float) (0.5f-motion.lengthVector()*0.1f), 0.0f);
						
							GlStateManager.pushMatrix();
							GlStateManager.translate(0, (entity.getRenderBoundingBox().maxY-entity.getRenderBoundingBox().minY)/2, 0);

							GlStateManager.depthFunc(GL11.GL_LEQUAL);
			                GlStateManager.depthMask(false);
			                GlStateManager.enableBlend();
							
							GL11.glBegin(GL11.GL_QUADS);
							
							GL11.glTexCoord2d(0, 1);
							GL11.glVertex3d(-0.5, 0, 0);
							GL11.glTexCoord2d(1, 1);
							GL11.glVertex3d(0.5, 0, 0);
							GL11.glTexCoord2d(1, 0);
							GL11.glVertex3d(motion.xCoord*2.5+0.5, motion.yCoord*2.5, motion.zCoord*2.5);
							GL11.glTexCoord2d(0, 0);
							GL11.glVertex3d(motion.xCoord*2.5-0.5, motion.yCoord*2.5, motion.zCoord*2.5);
							
							GL11.glEnd();

			                GlStateManager.disableBlend();
			                GlStateManager.depthMask(true);
							GlStateManager.depthFunc(GL11.GL_LEQUAL);
						
							GlStateManager.popMatrix();

						GlStateManager.enableCull();
						GlStateManager.enableLighting();
						
						GlStateManager.popMatrix();
						
						return;
					}
					else if(entity.getTeam()!=null)
					{		
						ScorePlayerTeam team = (ScorePlayerTeam) entity.getTeam();
						if(team.getRegisteredName().equals("stasis"))
						{
							//Remove glowing and outline team
							entity.setGlowing(false);
							entity.worldObj.getScoreboard().removePlayerFromTeam(entity.getCachedUniqueIdString(), team);
							
							if(render instanceof RenderLivingBase<?>)
							{
								try
								{
									Field fMainModel = RenderLivingBase.class.getDeclaredField("mainModel");
									fMainModel.setAccessible(true);
									
									ModelBase mainModel = (ModelBase) fMainModel.get(render);
									if(mainModel instanceof ModelHook)
									{
										ModelHook hook = (ModelHook) mainModel;
										ModelBase original = hook.getModel();
										
										fMainModel.set(render, original);
									}
								}
								catch (NoSuchFieldException e)
								{
									e.printStackTrace();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		render.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks)
	{
		render.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
	}

	public boolean isMultipass()
	{
		return render.isMultipass();
	}

	public void renderMultipass(Entity p_188300_1_, double p_188300_2_, double p_188300_4_, double p_188300_6_,
			float p_188300_8_, float p_188300_9_)
	{
		render.renderMultipass(p_188300_1_, p_188300_2_, p_188300_4_, p_188300_6_, p_188300_8_, p_188300_9_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		try
		{
			Method origin = render.getClass().getMethod("getEntityTexture", Entity.class);
			origin.setAccessible(true);
			
			return (ResourceLocation) origin.invoke(render, entity);
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
