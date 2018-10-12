package com.theredmajora.botw.item;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.render.item.CustomItemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBOTWShield extends ItemBOTW implements CustomItemRenderer
{
	public ItemBOTWShield(String name, int strength) 
	{
		super(name+"_shield");
		setMaxStackSize(1);
		setMaxDamage(strength);
        addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
		setCreativeTab(BOTW.botwTabShields);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) 
	{
		return EnumAction.BLOCK;
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        playerIn.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }
	
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

	@Override
	public void render(ItemStack itemStackIn)
	{
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(BOTW.MODID, "textures/items/shields/"+getRegistryName().getResourcePath()+".png"));
		
		GlStateManager.pushMatrix();
		
		/*GL11.glBegin(GL11.GL_POLYGON);
		{
			for(double angle=0.0;angle<2*Math.PI;angle+=0.1)
			{
				GL11.glTexCoord2d(0.3, 0.3);
				GL11.glVertex3d(Math.cos(angle)*0.5, 0, Math.sin(angle)*0.5);
			}
		}
		GL11.glEnd();*/
		
		RenderHelper.enableGUIStandardItemLighting();
		
		GL11.glBegin(GL11.GL_QUADS);
		{
			{
				GL11.glTexCoord2d(0, 0);
				GL11.glVertex3d(0, 0, 0);
	
				GL11.glTexCoord2d(1, 0);
				GL11.glVertex3d(1, 0, 0);
	
				GL11.glTexCoord2d(1, 1);
				GL11.glVertex3d(1, 1, 0);
	
				GL11.glTexCoord2d(0, 1);
				GL11.glVertex3d(0, 1, 0);
			}
		}
		GL11.glEnd();
		
		GlStateManager.popMatrix();
	}
}
