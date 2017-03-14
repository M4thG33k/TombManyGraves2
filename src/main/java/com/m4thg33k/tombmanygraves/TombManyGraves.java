package com.m4thg33k.tombmanygraves;

import com.m4thg33k.tombmanygraves.commands.ModCommands;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.proxy.CommonProxy;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;

@Mod(modid = Names.MODID, name = Names.MODNAME, version = Names.VERSION)
public class TombManyGraves {

    public static boolean BAUBLES;

    @Mod.Instance
    public static TombManyGraves INSTANCE = new TombManyGraves();

    @SidedProxy(clientSide = "com.m4thg33k.tombmanygraves.proxy.ClientProxy", serverSide = "com.m4thg33k.tombmanygraves.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        proxy.preinit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent e)
    {
        proxy.postinit(e);

        BAUBLES = Loader.isModLoaded("baubles");

        LogHelper.info("Baubles is " + (BAUBLES ? "" : "NOT ") + "installed.");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        ModCommands.initCommands(event);
    }
}
