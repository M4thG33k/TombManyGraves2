package com.m4thg33k.tombmanygraves.client.render.tiles;

import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Random;


public class TileGraveRenderer extends TileEntitySpecialRenderer {

    RenderItem itemRenderer;
    private int skullMeta = ModConfigs.GRAVE_SKULL_RENDER_TYPE;
    private static final ItemStack defaultSkull = new ItemStack(Items.SKULL, 1, ModConfigs.GRAVE_SKULL_RENDER_TYPE);
    private int deathAngle;
    private boolean shouldRenderGround;
    private ItemStack skull;
    private boolean initialized = false;
    private static final ResourceLocation TEX = TextureMap.LOCATION_BLOCKS_TEXTURE;
    private Random rand;



    public TileGraveRenderer()
    {
        itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    private void initialize(TileGrave grave)
    {
        deathAngle = grave.getAngle();
        shouldRenderGround = grave.getShouldRenderGround();
        skull = skullMeta==3 ? grave.getSkull() : defaultSkull;
        rand = new Random(grave.getPos().hashCode());
        initialized = true;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!(te instanceof TileGrave))
        {
            return;
        }

        TileGrave grave = (TileGrave)te;

        if (!initialized)
        {
            this.initialize(grave);
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(TEX);

        if (this.shouldRenderGround)
        {
            this.renderGround(x, y, z, grave.isLocked());
        }
        else
        {
            this.renderFloatingHead(x, y, z, grave.isLocked());
        }
    }

    private void renderGround(double x, double y, double z, boolean isLocked)
    {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translate(x+0.5,y+0.5,z+0.5);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-deathAngle, 0, 1, 0);
        if (isLocked)
        {
            GlStateManager.translate(0, -0.1, 0);
            GlStateManager.rotate(90, 1, 0, 0);
        }
        else
        {
            GlStateManager.rotate(45, 1, 0, 0);
        }
        GlStateManager.scale(0.75, 0.75, 0.75);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        itemRenderer.renderItem(skull, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -0.25, 0);
        GlStateManager.scale(2, 1, 2);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void renderFloatingHead(double x, double y, double z, boolean isLocked)
    {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translate(x+0.5,y+0.5,z+0.5);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(this.getNextRandomAngle(), 0, 1, 0);
        GlStateManager.rotate(this.getNextRandomAngle(), 1, 0, 0);
        GlStateManager.rotate(this.getNextRandomAngle(), 0, 0, 1);

        double scale = isLocked ? 0.25 : 0.75;

        GlStateManager.scale(scale, scale, scale);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        itemRenderer.renderItem(skull, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private float getNextRandomAngle()
    {
        return (float) (360.0 * (System.currentTimeMillis() * 0x3FFFL) / 0x3FFFL) + rand.nextInt(360);
    }
}
