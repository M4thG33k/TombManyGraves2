package com.m4thg33k.tombmanygraves.tiles;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {
	@SuppressWarnings("deprecation")
	public static void init() {
		String prefix = "tile." + Names.MODID;
		GameRegistry.registerTileEntity(TileGrave.class, prefix + Names.GRAVE_BLOCK); //Using new method will break existing graves
	}
}
