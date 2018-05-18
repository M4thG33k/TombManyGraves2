package com.m4thg33k.tombmanygraves;

import com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations.BaublesInventory;
import com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations.VanillaMinecraftInventory;
import com.m4thg33k.tombmanygraves.commands.ModCommands;
import com.m4thg33k.tombmanygraves.inventoryManagement.SpecialInventoryManager;
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

@Mod(modid = Names.MODID, name = Names.MODNAME, version = Names.VERSION, dependencies = TombManyGraves.DEPENDENCIES)
public class TombManyGraves {

    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2655,);required-after:tombmanygraves2api@[1.12.2-1.0.0,)";

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

        BAUBLES = Loader.isModLoaded("baubles");

        printModLoaded(BAUBLES, "Baubles");


        // create special inventories
        new VanillaMinecraftInventory();
        if (BAUBLES) {
            new BaublesInventory();
        }
    }

    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent e)
    {
        proxy.postinit(e);


        // Make sure to finalize the listeners so the mod actually works...
        SpecialInventoryManager.getInstance().finalizeListeners();
    }

    private void printModLoaded(boolean bool, String name)
    {
        LogHelper.info(name + " is " + (bool ? "" : "NOT ") + "installed.");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        ModCommands.initCommands(event);
    }
}
