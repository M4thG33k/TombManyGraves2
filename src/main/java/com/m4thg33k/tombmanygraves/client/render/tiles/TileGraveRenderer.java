package com.m4thg33k.tombmanygraves.client.render.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.Names;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockTags.Wrapper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.shapes.VoxelShapes;


public class TileGraveRenderer extends TileEntityRenderer<TileGrave> {

	ItemRenderer itemRenderer;
    private static final ItemStack defaultSkull = new ItemStack(Items.PLAYER_HEAD);
    private int deathAngle;
    private boolean shouldRenderGround;
    private ItemStack skull = defaultSkull;
    private static final ResourceLocation TEX = TextureMap.LOCATION_BLOCKS_TEXTURE;
    private Random rand;
    private static float pixel = 1 / 16F;

    private void initialize(TileGrave grave)
    {
    	itemRenderer = Minecraft.getInstance().getItemRenderer();
        deathAngle = grave.getAngle()+180;
        shouldRenderGround = grave.getShouldRenderGround();
        rand = new Random(grave.getPos().hashCode());
    }
    
    @Override
    public void render(TileGrave te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (!ModConfigs.GRAVE_RENDERING_ENABLED) return;
        
        initialize(te);

        if (this.shouldRenderGround)
        {
            this.renderGround(x, y, z, te.isLocked(), te);
        }
        else
        {
            this.renderFloatingHead(x, y, z, te.isLocked());
        }
    }

    @SuppressWarnings("deprecation")
	private void renderGround(double x, double y, double z, boolean isLocked, TileGrave te)
    {
    	
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translated(x+0.5,y+0.5,z+0.5);

        GlStateManager.pushMatrix();
        GlStateManager.rotatef(-deathAngle, 0, 1, 0);
        if (isLocked)
        {
            GlStateManager.translated(0, -0.1, 0);
            GlStateManager.rotatef(90, 1, 0, 0);
        }
        else
        {
            GlStateManager.rotatef(45, 1, 0, 0);
        }
        GlStateManager.scaled(0.75, 0.75, 0.75);
        RenderHelper.enableStandardItemLighting();
        try {
            itemRenderer.renderItem(skull, TransformType.FIXED);
        }
        catch (Exception e)
        {
            itemRenderer.renderItem(new ItemStack(Blocks.COMMAND_BLOCK), TransformType.FIXED);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translated(0, -0.25, 0);
        GlStateManager.scaled(2, 1, 2);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        Minecraft.getInstance().textureManager.bindTexture(TEX);
        IBlockState state = te.getWorld().getBlockState(te.getPos().down());
        if(ModConfigs.FORCE_DIRT_RENDER || state == null || !state.isBlockNormalCube() || state.hasTileEntity() || !state.isFullCube() || 
        		!state.isSolid() || !state.getShape(te.getWorld(), te.getPos().down()).equals(VoxelShapes.fullCube()) ||
        		state.getRenderType() != EnumBlockRenderType.MODEL){
        	state = Blocks.DIRT.getDefaultState();
        }
        if(te.getPlayerName().toLowerCase().equals("tiffit")){
        	Wrapper tags = new BlockTags.Wrapper(new ResourceLocation(Names.MODID, "gravecycle"));
        	List<Block> blocks = new ArrayList<>(tags.getAllElements());
        	int index = (int)((System.currentTimeMillis() % (blocks.size()*250))/ 250);
        	state = blocks.get(index).getDefaultState();
        }
        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        GlStateManager.color4f(1, 1, 1, 1);
		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		float pixelBelow = (sprite.getMaxU()-sprite.getMinU())/16;
		int height = 8;
		builder.pos(0, pixel * height, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
		builder.pos(0, pixel * height, 1).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
		builder.pos(1, pixel * height, 1).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
		builder.pos(1, pixel * height, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();

		builder.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
		builder.pos(1, 0, 0).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
		builder.pos(1, 0, 1).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
		builder.pos(0, 0, 1).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();

		builder.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV() + pixelBelow).endVertex();
		builder.pos(0, pixel * height, 0).tex(sprite.getMinU(), sprite.getMinV() + pixelBelow * height).endVertex();
		builder.pos(1, pixel * height, 0).tex(sprite.getMaxU(), sprite.getMinV() + pixelBelow * height).endVertex();
		builder.pos(1, 0, 0).tex(sprite.getMaxU(), sprite.getMinV() + pixelBelow).endVertex();

		builder.pos(0, 0, 1).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow).endVertex();
		builder.pos(1, 0, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow).endVertex();
		builder.pos(1, pixel * height, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow * height).endVertex();
		builder.pos(0, pixel * height, 1).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow * height).endVertex();

		builder.pos(0, 0, 0).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow).endVertex();
		builder.pos(0, 0, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow).endVertex();
		builder.pos(0, pixel * height, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow * height).endVertex();
		builder.pos(0, pixel * height, 0).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow * height).endVertex();

		builder.pos(1, 0, 0).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow).endVertex();
		builder.pos(1, pixel * height, 0).tex(sprite.getMinU(), sprite.getMinV()+pixelBelow * height).endVertex();
		builder.pos(1, pixel * height, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow * height).endVertex();
		builder.pos(1, 0, 1).tex(sprite.getMaxU(), sprite.getMinV()+pixelBelow).endVertex();
		Tessellator.getInstance().draw();
		GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    @SuppressWarnings("deprecation")
	private void renderFloatingHead(double x, double y, double z, boolean isLocked)
    {
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translated(x+0.5,y+0.5,z+0.5);

        GlStateManager.pushMatrix();
        GlStateManager.rotatef(getNextRandomAngle(), 0, 1, 0);
        GlStateManager.rotatef(getNextRandomAngle(), 1, 0, 0);
        GlStateManager.rotatef(getNextRandomAngle(), 0, 0, 1);

        double scaled = isLocked ? 0.25 : 0.75;

        GlStateManager.scaled(scaled, scaled, scaled);
        RenderHelper.enableStandardItemLighting();
        try {
            itemRenderer.renderItem(skull, TransformType.FIXED);
        }
        catch (Exception e)
        {
            itemRenderer.renderItem(new ItemStack(Blocks.COMMAND_BLOCK), TransformType.FIXED);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private float getNextRandomAngle()
    {
        return (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) + rand.nextInt(360);
    }
}
