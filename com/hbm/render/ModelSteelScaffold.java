// Date: 10.12.2015 21:15:17
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX






package com.hbm.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSteelScaffold extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    ModelRenderer Shape7;
    ModelRenderer Shape8;
    ModelRenderer Shape9;
    ModelRenderer Shape10;
    ModelRenderer Shape11;
    ModelRenderer Shape12;
  
  public ModelSteelScaffold()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Shape1 = new ModelRenderer(this, 0, 0);
      Shape1.addBox(0F, 0F, 0F, 2, 16, 2);
      Shape1.setRotationPoint(6F, 8F, -6F);
      Shape1.setTextureSize(64, 32);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 0, 0);
      Shape2.addBox(0F, 0F, 0F, 2, 16, 2);
      Shape2.setRotationPoint(-8F, 8F, -6F);
      Shape2.setTextureSize(64, 32);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new ModelRenderer(this, 0, 0);
      Shape3.addBox(0F, 0F, 0F, 2, 16, 2);
      Shape3.setRotationPoint(-8F, 8F, 4F);
      Shape3.setTextureSize(64, 32);
      Shape3.mirror = true;
      setRotation(Shape3, 0F, 0F, 0F);
      Shape4 = new ModelRenderer(this, 0, 0);
      Shape4.addBox(0F, 0F, 0F, 2, 16, 2);
      Shape4.setRotationPoint(6F, 8F, 4F);
      Shape4.setTextureSize(64, 32);
      Shape4.mirror = true;
      setRotation(Shape4, 0F, 0F, 0F);
      Shape5 = new ModelRenderer(this, 8, 0);
      Shape5.addBox(-8F, -0.5F, 0F, 16, 1, 1);
      Shape5.setRotationPoint(0F, 15.5F, 4.5F);
      Shape5.setTextureSize(64, 32);
      Shape5.mirror = true;
      setRotation(Shape5, 0F, 0F, -0.6108652F);
      Shape6 = new ModelRenderer(this, 8, 0);
      Shape6.addBox(-8F, -0.5F, 0F, 16, 1, 1);
      Shape6.setRotationPoint(0F, 15.5F, 4.5F);
      Shape6.setTextureSize(64, 32);
      Shape6.mirror = true;
      setRotation(Shape6, 0F, 0F, 0.6108652F);
      Shape7 = new ModelRenderer(this, 8, 0);
      Shape7.addBox(-8F, -0.5F, 0F, 16, 1, 1);
      Shape7.setRotationPoint(0F, 15.5F, -5.5F);
      Shape7.setTextureSize(64, 32);
      Shape7.mirror = true;
      setRotation(Shape7, 0F, 0F, 0.6108652F);
      Shape8 = new ModelRenderer(this, 8, 0);
      Shape8.addBox(-8F, 0F, 0F, 16, 1, 1);
      Shape8.setRotationPoint(0F, 15.5F, -5.5F);
      Shape8.setTextureSize(64, 32);
      Shape8.mirror = true;
      setRotation(Shape8, 0F, 0F, -0.6108652F);
      Shape9 = new ModelRenderer(this, 0, 19);
      Shape9.addBox(-0.5F, -0.5F, -6F, 1, 1, 12);
      Shape9.setRotationPoint(-7F, 15.5F, 0F);
      Shape9.setTextureSize(64, 32);
      Shape9.mirror = true;
      setRotation(Shape9, 0.6108652F, 0F, 0F);
      Shape10 = new ModelRenderer(this, 0, 19);
      Shape10.addBox(-0.5F, -0.5F, -6F, 1, 1, 12);
      Shape10.setRotationPoint(-7F, 15.5F, 0F);
      Shape10.setTextureSize(64, 32);
      Shape10.mirror = true;
      setRotation(Shape10, -0.6108652F, 0F, 0F);
      Shape11 = new ModelRenderer(this, 0, 19);
      Shape11.addBox(-0.5F, -0.5F, -6F, 1, 1, 12);
      Shape11.setRotationPoint(7F, 15.5F, 0F);
      Shape11.setTextureSize(64, 32);
      Shape11.mirror = true;
      setRotation(Shape11, 0.6108652F, 0F, 0F);
      Shape12 = new ModelRenderer(this, 0, 19);
      Shape12.addBox(-0.5F, -0.5F, -6F, 1, 1, 12);
      Shape12.setRotationPoint(7F, 15.5F, 0F);
      Shape12.setTextureSize(64, 32);
      Shape12.mirror = true;
      setRotation(Shape12, -0.6108652F, 0F, 0F);
  }
  
  @Override
public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Shape1.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
    Shape4.render(f5);
    Shape5.render(f5);
    Shape6.render(f5);
    Shape7.render(f5);
    Shape8.render(f5);
    Shape9.render(f5);
    Shape10.render(f5);
    Shape11.render(f5);
    Shape12.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void renderModel(float f)
  {
	    Shape1.render(f);
	    Shape2.render(f);
	    Shape3.render(f);
	    Shape4.render(f);
	    Shape5.render(f);
	    Shape6.render(f);
	    Shape7.render(f);
	    Shape8.render(f);
	    Shape9.render(f);
	    Shape10.render(f);
	    Shape11.render(f);
	    Shape12.render(f);
  }
  
  @Override
public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
