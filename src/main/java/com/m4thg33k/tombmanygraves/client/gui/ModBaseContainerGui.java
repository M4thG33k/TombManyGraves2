package com.m4thg33k.tombmanygraves.client.gui;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class ModBaseContainerGui extends GuiContainer {

    public ModBaseContainerGui(int xSize, int ySize, Container container)
    {
        super(container);
        this.xSize = xSize;
        this.ySize = ySize;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }

    public void bindTexture(String filename)
    {
        bindTexture(Names.MODID, filename);
    }

    public void bindTexture(String base, String filename)
    {
        mc.getTextureManager().bindTexture(new ResourceLocation(base, "textures/gui/" + filename));
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height)
    {
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public int getGuiLeft()
    {
        return (this.width - this.xSize) / 2;
    }

    public int getGuiTop()
    {
        return (this.height - this.ySize) / 2;
    }

    public boolean isInBounds(int x, int y, int w, int h, int ox, int oy)
    {
        return ox - getGuiLeft() >= x && ox - getGuiLeft() <= x + w &&
                oy - getGuiTop() >= y && oy - getGuiTop() <= y + h;
    }
}
