package com.m4thg33k.tombmanygraves.events;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEvents {

    public Minecraft mc;

    private static final int FORCE = ModConfigs.NAME_FORCE;
    private static final int YIELD = ModConfigs.NAME_YIELD;

    public RenderEvents()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event)
    {
        if (! ModConfigs.DISPLAY_GRAVE_NAME)
        {
            return;
        }

        RayTraceResult trace = event.getTarget();
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            World world = mc.world;
            IBlockState state = world.getBlockState(trace.getBlockPos());

            if (state.getBlock() == ModBlocks.blockGrave)
            {
                TileEntity tile = world.getTileEntity(trace.getBlockPos());
                if (tile != null && tile instanceof TileGrave)
                {
                    String name = ((TileGrave) tile).getPlayerName();
                    boolean giveGraveItemsPriority = ((TileGrave) tile).areGraveItemsForced();
                    this.renderPlayerName(trace.getBlockPos(), event.getPartialTicks(), name, giveGraveItemsPriority);
                }
            }
        }
    }

    private void renderPlayerName(BlockPos pos, float partialTicks, String name, boolean giveGravePriority)
    {
        if (name.length() > 0)
        {
            GlStateManager.alphaFunc(516, 0.1f);
            renderPlayerName(name, this.mc.player, pos, partialTicks, giveGravePriority);
        }
    }

    private void renderPlayerName(String name, EntityPlayer player, BlockPos pos,
                                  float partialTicks, boolean giveGravePriority)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        float angleH = player.rotationYawHead;
        float angleV = 0f;


        this.renderLabel(name, x - dx, y -dy, z - dz, angleH, angleV, giveGravePriority);
        this.renderLabel(giveGravePriority ? "force" : "yield", x - dx, y - dy - 0.25, z - dz, angleH, angleV, giveGravePriority);
//        if (giveGravePriority) {
//            this.renderLabel("force", x - dx, y - dy - 0.25, z - dz, angleH, angleV, giveGravePriority);
//        }
//        else {
//            this.renderLabel("yield", x - dx, y - dy - 0.25, z - dz, angleH, angleV, giveGravePriority);
//        }
        if (ModConfigs.GRAVE_POS_ENABLED)
        {
            this.renderLabel(posToString(pos), x - dx, y - dy - 0.5, z - dz, angleH, angleV, giveGravePriority);
        }
    }

    private String posToString(BlockPos pos)
    {
        return "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
    }

    protected void renderLabel(String name, double x, double y, double z, float angleH, float angleV, boolean giveGravePriority)
    {
        FontRenderer fontRenderer = this.mc.fontRenderer;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5, y+1.5, z+0.5);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleH, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleV, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        int strLenHalved = fontRenderer.getStringWidth(name) / 2;

        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(-strLenHalved - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos(-strLenHalved - 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

//        fontRenderer.drawString(name, -strLenHalved, 0, giveGravePriority ? 0xFFFFFF : 0x000000);
        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);
        fontRenderer.drawString(name, -strLenHalved, 0, giveGravePriority ? FORCE : YIELD);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.popMatrix();
    }
}
