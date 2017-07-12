package com.m4thg33k.tombmanygraves.blocks;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModBlocks {

    public static BlockGrave blockGrave = new BlockGrave();

    public static void preInit()
    {
        ForgeRegistries.BLOCKS.register(blockGrave);
    }
}
