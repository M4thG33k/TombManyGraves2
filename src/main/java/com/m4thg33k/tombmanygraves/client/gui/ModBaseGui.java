package com.m4thg33k.tombmanygraves.client.gui;

import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class ModBaseGui extends GuiScreen{

    protected int xSize;
    protected int ySize;

    public ModBaseGui(int xSize, int ySize)
    {
        this.xSize = xSize;
        this.ySize = ySize;
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
