package com.m4thg33k.tombmanygraves.client.render;

import com.m4thg33k.tombmanygraves.client.render.tiles.TileGraveRenderer;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenders {

    public static void init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGrave.class, new TileGraveRenderer());
    }
}
