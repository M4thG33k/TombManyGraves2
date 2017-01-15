package com.m4thg33k.tombmanygraves.proxy;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.blocks.itemblocks.ModItemBlocks;
import com.m4thg33k.tombmanygraves.events.CommonEvents;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.tiles.ModTiles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent e)
    {
        ModConfigs.preInit(e);
        ModBlocks.preInit();
        ModItemBlocks.createItemblocks();
    }

    public void init(FMLInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        ModTiles.init();
    }

    public void postinit(FMLPostInitializationEvent e)
    {

    }
}
