package com.hbm.inventory.gui;

import org.lwjgl.opengl.GL11;

import com.hbm.inventory.container.ContainerElectrolyser;
import com.hbm.inventory.recipes.ElectrolysisRecipes.Metals;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityElectrolyser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GUIElectrolyser extends GuiInfoContainer {
	
	public static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/processing/gui_electrolyser.png");
	private TileEntityElectrolyser electrolyser;
	
	public GUIElectrolyser(InventoryPlayer invPlayer, TileEntityElectrolyser electrolyser) {
		super(new ContainerElectrolyser(invPlayer, electrolyser));
		this.electrolyser = electrolyser;

		this.xSize = 210;
		this.ySize = 247;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);
		
		electrolyser.tanks[0].renderTankInfo(this, mouseX, mouseY, guiLeft + 42, guiTop + 18, 16, 52);
		electrolyser.tanks[1].renderTankInfo(this, mouseX, mouseY, guiLeft + 96, guiTop + 18, 16, 52);
		electrolyser.tanks[2].renderTankInfo(this, mouseX, mouseY, guiLeft + 116, guiTop + 18, 16, 52);
		
		this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 186, guiTop + 18, 240, 90, electrolyser.power, electrolyser.maxPower);
	}
	
	protected void mouseClicked(int x, int y, int i) {
		super.mouseClicked(x, y, i);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int i = (int)((electrolyser.power * 89) / electrolyser.maxPower);
		drawTexturedModalRect(guiLeft + 186, guiTop + 107 - i, 240, 90 - i, 16, i);
		
		if(i > 0)
			drawTexturedModalRect(guiLeft + 190, guiTop + 4, 240, 90, 9, 12);
		
		int k = (int)((electrolyser.progressOre * 27) / electrolyser.maxProgress);
		drawTexturedModalRect(guiLeft + 5, guiTop + 112, 213, 40, 27, k);
		
		int niter = (int)((electrolyser.niterTank * 34) / electrolyser.maxNiter);
		drawTexturedModalRect(guiLeft + 37, guiTop + 120 - niter, 240, 148 - niter, 16, niter);
		
		electrolyser.tanks[0].renderTank(guiLeft + 42, guiTop + 70, this.zLevel, 16, 52);
		electrolyser.tanks[1].renderTank(guiLeft + 96, guiTop + 70, this.zLevel, 16, 52);
		electrolyser.tanks[2].renderTank(guiLeft + 116, guiTop + 70, this.zLevel, 16, 52);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		
		Metals[] tanks = {electrolyser.primaryMetal, electrolyser.secondaryMetal};
		int[] fill = {electrolyser.primaryMetalTank, electrolyser.secondaryMetalTank};
		for(int x = 0; x < 2; x++) {
			int height = (int)((fill[x] * 42) / electrolyser.maxMetal);
			int colour = tanks[x].colour;
			drawTexturedModalRect(guiLeft + 60 + x*38, guiTop + 128 - height, 222, 190 - height, 34, height);
			
			if(height > 18) {
				drawRectangle(78+x*38, 110, 16, 18, colour);
				drawRectangle(60+x*38, 110-(height-18), 34, height-18, colour);
			} else {
				drawRectangle(78 + x*38, 128-height, 16, height, colour);
			}
		}
		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.electrolyser.hasCustomInventoryName() ? this.electrolyser.getInventoryName() : I18n.format(this.electrolyser.getInventoryName());

		this.fontRendererObj.drawString(name, (this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2) - 16, 7, 0xffffff);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 94, 4210752);
	}
	
	private void drawRectangle(int x, int y, int width, int height, int colour) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorRGBA_I(colour, 0xFFFFFF);
		tess.addVertex(guiLeft + x, guiTop + y, this.zLevel);
		tess.addVertex(guiLeft + x + width, guiTop + y, this.zLevel);
		tess.addVertex(guiLeft + x + width, guiTop + y + height, this.zLevel);
		tess.addVertex(guiLeft + x, guiTop + y + height, this.zLevel);
		tess.draw();
		/*if(height > 18) {
			drawRect(78, 110, 94, 128, colour);
			drawRect(60, 110-(height-18), 94, 110, colour);
		} else {
			drawRect(guiLeft+78, guiTop+128-height, guiLeft+94, guiTop+128, 0xFFFFFF);
		}*/
		//drawRect(guiLeft+50, guiTop+50, guiLeft+55, guiTop+55, 0xFFFFFF);
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		
	}
}
