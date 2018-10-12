package com.theredmajora.botw.render.player;

import com.theredmajora.botw.capability.itemtracker.CapabilityItemTracker;
import com.theredmajora.botw.capability.itemtracker.IItemTracker;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ModelQuiver extends ModelBase
{
	ModelRenderer ARROW11;
    ModelRenderer ARROW12;
    ModelRenderer ARROW13;
    ModelRenderer ARROW14;
    ModelRenderer ARROW15;
    ModelRenderer ARROW16;
    ModelRenderer ARROW17;
    ModelRenderer ARROW18;
    ModelRenderer ARROW19;
    ModelRenderer ARROW31;
    ModelRenderer ARROW32;
    ModelRenderer ARROW33;
    ModelRenderer ARROW34;
    ModelRenderer ARROW35;
    ModelRenderer ARROW36;
    ModelRenderer ARROW37;
    ModelRenderer ARROW38;
    ModelRenderer ARROW39;
    ModelRenderer ARROW21;
    ModelRenderer ARROW22;
    ModelRenderer ARROW23;
    ModelRenderer ARROW24;
    ModelRenderer ARROW25;
    ModelRenderer ARROW26;
    ModelRenderer ARROW27;
    ModelRenderer ARROW28;
    ModelRenderer ARROW29;
    ModelRenderer QuiverMetal;
    ModelRenderer QuiverPart1;
    ModelRenderer QuiverPart2;
    ModelRenderer Quiver;
  
    public ModelQuiver()
    {
  	  	textureWidth = 128;
  	  	textureHeight = 32;
      
        ARROW11 = new ModelRenderer(this, 0, 30);
        ARROW11.addBox(11.3F, 3F, -7F, 10, 1, 1);
        ARROW11.setRotationPoint(0F, 11F, 4.5F);
        ARROW11.setTextureSize(128, 32);
        ARROW12 = new ModelRenderer(this, 0, 28);
        ARROW12.addBox(17.3F, 4F, -7F, 3, 1, 1);
        ARROW12.setRotationPoint(0F, 11F, 4.5F);
        ARROW12.setTextureSize(128, 32);
        ARROW13 = new ModelRenderer(this, 0, 28);
        ARROW13.addBox(17.3F, 3F, -6F, 3, 1, 1);
        ARROW13.setRotationPoint(0F, 11F, 4.5F);
        ARROW13.setTextureSize(128, 32);
        ARROW14 = new ModelRenderer(this, 0, 28);
        ARROW14.addBox(17.3F, 2F, -7F, 3, 1, 1);
        ARROW14.setRotationPoint(0F, 11F, 4.5F);
        ARROW14.setTextureSize(128, 32);
        ARROW15 = new ModelRenderer(this, 0, 28);
        ARROW15.addBox(18.3F, 3F, -5F, 3, 1, 1);
        ARROW15.setRotationPoint(0F, 11F, 4.5F);
        ARROW15.setTextureSize(128, 32);
        ARROW16 = new ModelRenderer(this, 0, 28);
        ARROW16.addBox(17.3F, 3F, -8F, 3, 1, 1);
        ARROW16.setRotationPoint(0F, 11F, 4.5F);
        ARROW16.setTextureSize(128, 32);
        ARROW17 = new ModelRenderer(this, 0, 28);
        ARROW17.addBox(18.3F, 3F, -9F, 3, 1, 1);
        ARROW17.setRotationPoint(0F, 11F, 4.5F);
        ARROW17.setTextureSize(128, 32);
        ARROW18 = new ModelRenderer(this, 0, 28);
        ARROW18.addBox(18.3F, 1F, -7F, 3, 1, 1);
        ARROW18.setRotationPoint(0F, 11F, 4.5F);
        ARROW18.setTextureSize(128, 32);
        ARROW19 = new ModelRenderer(this, 0, 28);
        ARROW19.addBox(18.3F, 5F, -7F, 3, 1, 1);
        ARROW19.setRotationPoint(0F, 11F, 4.5F);
        ARROW19.setTextureSize(128, 32);
        ARROW31 = new ModelRenderer(this, 0, 30);
        ARROW31.addBox(12F, 6.5F, -4F, 10, 1, 1);
        ARROW31.setRotationPoint(0F, 11F, 4.5F);
        ARROW31.setTextureSize(128, 32);
        ARROW32 = new ModelRenderer(this, 0, 28);
        ARROW32.addBox(18F, 6.5F, -3F, 3, 1, 1);
        ARROW32.setRotationPoint(0F, 11F, 4.5F);
        ARROW32.setTextureSize(128, 32);
        ARROW33 = new ModelRenderer(this, 0, 28);
        ARROW33.addBox(19F, 6.5F, -2F, 3, 1, 1);
        ARROW33.setRotationPoint(0F, 11F, 4.5F);
        ARROW33.setTextureSize(128, 32);
        ARROW34 = new ModelRenderer(this, 0, 28);
        ARROW34.addBox(18F, 6.5F, -5F, 3, 1, 1);
        ARROW34.setRotationPoint(0F, 11F, 4.5F);
        ARROW34.setTextureSize(128, 32);
        ARROW35 = new ModelRenderer(this, 0, 28);
        ARROW35.addBox(19F, 6.5F, -6F, 3, 1, 1);
        ARROW35.setRotationPoint(0F, 11F, 4.5F);
        ARROW35.setTextureSize(128, 32);
        ARROW36 = new ModelRenderer(this, 0, 28);
        ARROW36.addBox(18F, 7.5F, -4F, 3, 1, 1);
        ARROW36.setRotationPoint(0F, 11F, 4.5F);
        ARROW36.setTextureSize(128, 32);
        ARROW37 = new ModelRenderer(this, 0, 28);
        ARROW37.addBox(19F, 8.5F, -4F, 3, 1, 1);
        ARROW37.setRotationPoint(0F, 11F, 4.5F);
        ARROW37.setTextureSize(128, 32);
        ARROW38 = new ModelRenderer(this, 0, 28);
        ARROW38.addBox(18F, 5.5F, -4F, 3, 1, 1);
        ARROW38.setRotationPoint(0F, 11F, 4.5F);
        ARROW38.setTextureSize(128, 32);
        ARROW39 = new ModelRenderer(this, 0, 28);
        ARROW39.addBox(19F, 4.5F, -4F, 3, 1, 1);
        ARROW39.setRotationPoint(0F, 11F, 4.5F);
        ARROW39.setTextureSize(128, 32);
        ARROW21 = new ModelRenderer(this, 0, 30);
        ARROW21.addBox(10F, 2.5F, -4.5F, 10, 1, 1);
        ARROW21.setRotationPoint(0F, 11F, 4.5F);
        ARROW21.setTextureSize(128, 32);
        ARROW22 = new ModelRenderer(this, 0, 28);
        ARROW22.addBox(16F, 1.5F, -4.5F, 3, 1, 1);
        ARROW22.setRotationPoint(0F, 11F, 4.5F);
        ARROW22.setTextureSize(128, 32);
        ARROW23 = new ModelRenderer(this, 0, 28);
        ARROW23.addBox(16F, 3.5F, -4.5F, 3, 1, 1);
        ARROW23.setRotationPoint(0F, 11F, 4.5F);
        ARROW23.setTextureSize(128, 32);
        ARROW24 = new ModelRenderer(this, 0, 28);
        ARROW24.addBox(17F, 4.5F, -4.5F, 3, 1, 1);
        ARROW24.setRotationPoint(0F, 11F, 4.5F);
        ARROW24.setTextureSize(128, 32);
        ARROW25 = new ModelRenderer(this, 0, 28);
        ARROW25.addBox(17F, 0.5F, -4.5F, 3, 1, 1);
        ARROW25.setRotationPoint(0F, 11F, 4.5F);
        ARROW25.setTextureSize(128, 32);
        ARROW26 = new ModelRenderer(this, 0, 28);
        ARROW26.addBox(16F, 2.5F, -3.5F, 3, 1, 1);
        ARROW26.setRotationPoint(0F, 11F, 4.5F);
        ARROW26.setTextureSize(128, 32);
        ARROW27 = new ModelRenderer(this, 0, 28);
        ARROW27.addBox(17F, 2.5F, -2.5F, 3, 1, 1);
        ARROW27.setRotationPoint(0F, 11F, 4.5F);
        ARROW27.setTextureSize(128, 32);
        ARROW28 = new ModelRenderer(this, 0, 28);
        ARROW28.addBox(16F, 2.5F, -5.5F, 3, 1, 1);
        ARROW28.setRotationPoint(0F, 11F, 4.5F);
        ARROW28.setTextureSize(128, 32);
        ARROW29 = new ModelRenderer(this, 0, 28);
        ARROW29.addBox(17F, 2.5F, -6.5F, 3, 1, 1);
        ARROW29.setRotationPoint(0F, 11F, 4.5F);
        ARROW29.setTextureSize(128, 32);
        QuiverMetal = new ModelRenderer(this, 96, 0);
        QuiverMetal.addBox(-15F, 0.5F, -9.5F, 6, 10, 10);
        QuiverMetal.setRotationPoint(0F, 11F, 4.5F);
        QuiverMetal.setTextureSize(128, 32);
        QuiverPart1 = new ModelRenderer(this, 74, 0);
        QuiverPart1.addBox(12F, 2.5F, -9F, 2, 7, 9);
        QuiverPart1.setRotationPoint(0F, 11F, 4.5F);
        QuiverPart1.setTextureSize(128, 32);
        QuiverPart2 = new ModelRenderer(this, 38, 19);
        QuiverPart2.addBox(14F, 5.5F, -9F, 2, 4, 9);
        QuiverPart2.setRotationPoint(0F, 11F, 4.5F);
        QuiverPart2.setTextureSize(128, 32);
        Quiver = new ModelRenderer(this, 0, 0);
        Quiver.addBox(-9F, 0.5F, -9F, 21, 9, 9);
        Quiver.setRotationPoint(0F, 11F, 4.5F);
        Quiver.setTextureSize(128, 32);

        setRotation(ARROW11, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW12, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW13, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW14, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW15, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW16, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW17, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW18, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW19, 0.1189716F, 0.0594858F, -0.0872665F);
        setRotation(ARROW31, 0F, 0F, -0.0277807F);
        setRotation(ARROW32, 0F, 0F, -0.0277807F);
        setRotation(ARROW33, 0F, 0F, -0.0277807F);
        setRotation(ARROW34, 0F, 0F, -0.0277807F);
        setRotation(ARROW35, 0F, 0F, -0.0277807F);
        setRotation(ARROW36, 0F, 0F, -0.0277807F);
        setRotation(ARROW37, 0F, 0F, -0.0277807F);
        setRotation(ARROW38, 0F, 0F, -0.0277807F);
        setRotation(ARROW39, 0F, 0F, -0.0277807F);
        setRotation(ARROW21, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW22, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW23, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW24, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW25, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW26, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW27, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW28, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(ARROW29, 0.0297429F, -0.1189716F, -0.1170094F);
        setRotation(QuiverMetal, 0F, 0F, -0.0872665F);
        setRotation(QuiverPart1, 0F, 0F, -0.0872665F);
        setRotation(QuiverPart2, 0F, 0F, -0.0872665F);
        setRotation(Quiver, 0F, 0F, -0.0872665F);
    }
  
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
    	super.render(entity, f, f1, f2, f3, f4, f5);
    	setRotationAngles(f, f1, f2, f3, f4, f5, entity);

    	QuiverMetal.render(f5);
    	QuiverPart1.render(f5);
    	QuiverPart2.render(f5);
    	Quiver.render(f5);
    	
    	int arrows=countArrows((EntityPlayer) entity);
    	
    	if(arrows >= 1)
    	{
			ARROW31.render(f5);
			ARROW32.render(f5);
			ARROW33.render(f5);
			ARROW34.render(f5);
			ARROW35.render(f5);
			ARROW36.render(f5);
			ARROW37.render(f5);
			ARROW38.render(f5);
			ARROW39.render(f5);
    	}
		if(arrows >= 2)
		{
			ARROW11.render(f5);
    		ARROW12.render(f5);
    		ARROW13.render(f5);
    		ARROW14.render(f5);
    		ARROW15.render(f5);
    		ARROW16.render(f5);
        	ARROW17.render(f5);
        	ARROW18.render(f5);
        	ARROW19.render(f5);
		}
    	if(arrows >= 3)
    	{
    		ARROW21.render(f5);
    		ARROW22.render(f5);
    		ARROW23.render(f5);
    		ARROW24.render(f5);
    		ARROW25.render(f5);
    		ARROW26.render(f5);
    		ARROW27.render(f5);
    		ARROW28.render(f5);
    		ARROW29.render(f5);
    	}
    }
  
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
    	model.rotateAngleX = x;
    	model.rotateAngleY = y;
    	model.rotateAngleZ = z;
    }
    
    public int countArrows(EntityPlayer player)
	{
    	IItemTracker tracker = player.getCapability(CapabilityItemTracker.BOTW_ITEMTRACKER_CAP, null);
		return tracker.getArrowCount();
	}
  
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
    	super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    	
    	Quiver.rotateAngleZ = MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * f1 * 0.5F / 10.0F;
    	Quiver.rotateAngleX = -0.2F + MathHelper.cos(f * 0.6662F + (float)Math.PI) * 2.0F * (f1 / 1) * 0.5F / 5.0F;this.copyModelAngles(Quiver, ARROW11);
    	this.copyModelAngles(Quiver, ARROW12);
    	this.copyModelAngles(Quiver, ARROW13);
		this.copyModelAngles(Quiver, ARROW14);
		this.copyModelAngles(Quiver, ARROW15);
		this.copyModelAngles(Quiver, ARROW16);
		this.copyModelAngles(Quiver, ARROW17);
		this.copyModelAngles(Quiver, ARROW18);
		this.copyModelAngles(Quiver, ARROW19);
		this.copyModelAngles(Quiver, ARROW21);
		this.copyModelAngles(Quiver, ARROW22);
		this.copyModelAngles(Quiver, ARROW23);
		this.copyModelAngles(Quiver, ARROW24);
		this.copyModelAngles(Quiver, ARROW25);
		this.copyModelAngles(Quiver, ARROW26);
		this.copyModelAngles(Quiver, ARROW27);
		this.copyModelAngles(Quiver, ARROW28);
		this.copyModelAngles(Quiver, ARROW29);
		this.copyModelAngles(Quiver, ARROW31);
		this.copyModelAngles(Quiver, ARROW32);
		this.copyModelAngles(Quiver, ARROW33);
		this.copyModelAngles(Quiver, ARROW34);
		this.copyModelAngles(Quiver, ARROW35);
		this.copyModelAngles(Quiver, ARROW36);
		this.copyModelAngles(Quiver, ARROW37);
		this.copyModelAngles(Quiver, ARROW38);
		this.copyModelAngles(Quiver, ARROW39);
		this.copyModelAngles(Quiver, QuiverMetal);
		this.copyModelAngles(Quiver, QuiverPart1);
		this.copyModelAngles(Quiver, QuiverPart2);
	}
}
