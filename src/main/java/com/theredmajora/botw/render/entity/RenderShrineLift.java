package com.theredmajora.botw.render.entity;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.block.BOTWBlocks;
import com.theredmajora.botw.entity.EntityShrineLift;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderShrineLift extends Render<EntityShrineLift>
{
	public RenderShrineLift(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShrineLift entity)
	{
		return new ResourceLocation(BOTW.MODID, "textures/entity/shrine_lift.png");	//May not bind textures from MODID:textures/blocks/ for some reasons
	}
	
	@Override
	public void doRender(EntityShrineLift entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		IBlockState iblockstate=BOTWBlocks.shrine_lift.getDefaultState();
		World world = entity.worldObj;
		
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
        GlStateManager.disableLighting();
		
        GlStateManager.translate(x, entity.getEntityBoundingBox().maxY+y, z);

		bindTexture(getEntityTexture(entity));
		
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(entity.getEntityBoundingBox().minX, 0, entity.getEntityBoundingBox().minZ);

        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(entity.getEntityBoundingBox().minX, 0, entity.getEntityBoundingBox().maxZ);

        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(entity.getEntityBoundingBox().maxX, 0, entity.getEntityBoundingBox().maxZ);

        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(entity.getEntityBoundingBox().maxX, 0, entity.getEntityBoundingBox().minZ);
		
		GL11.glEnd();

        GlStateManager.enableLighting();
		GlStateManager.enableCull();
        GlStateManager.popMatrix();
	}
}
