package com.m4thg33k.tombmanygraves.proxy;

import java.util.List;

import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preinit(FMLPreInitializationEvent e) {
        super.preinit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postinit(FMLPostInitializationEvent e) {
        super.postinit(e);
    }

    @Override
    public List<String> probeForFiles(BlockPos pos) {
//        LogHelper.info("Probing for files!");
        return DeathInventoryHandler.getSavedInventories();
    }
}
