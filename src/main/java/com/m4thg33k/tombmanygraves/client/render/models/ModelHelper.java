package com.m4thg33k.tombmanygraves.client.render.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;

public class ModelHelper {

    public static TextureAtlasSprite getTextureFromBlockstate(IBlockState state)
    {
        if (state == null)
        {
            state = Blocks.DIRT.getDefaultState();
        }

        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }

}
