package com.theredmajora.botw.items;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.theredmajora.botw.BOTW;
import com.theredmajora.botw.render.item.CustomItemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
		GlStateManager.pushMatrix();
		
		/*GlStateManager.translate(0.15, 0.6, 0.6);
		
		GL11.glBegin(GL11.GL_POLYGON);
		{
			for(double angle=0.0;angle<2*Math.PI;angle+=0.1)
			{
				GL11.glVertex3d(Math.cos(angle)*0.5, 0, Math.sin(angle)*0.5);
			}
		}
		GL11.glEnd();*/
		
        Minecraft.getMinecraft().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
        new ModelShield().render();
		
		GlStateManager.popMatrix();
	}
}
