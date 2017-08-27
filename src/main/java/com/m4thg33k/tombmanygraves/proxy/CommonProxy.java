package com.m4thg33k.tombmanygraves.proxy;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.blocks.itemblocks.ModItemBlocks;
import com.m4thg33k.tombmanygraves.events.CommonEvents;
import com.m4thg33k.tombmanygraves.friendSystem.FriendHandler;
import com.m4thg33k.tombmanygraves.gui.ModGuiHandler;
import com.m4thg33k.tombmanygraves.inventoryManagement.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import com.m4thg33k.tombmanygraves.tiles.ModTiles;
import com.m4thg33k.tombmanygraves.util.LogHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent e)
    {
        FriendHandler.importFriendsList();
        ModConfigs.preInit(e);
        TMGNetwork.setup();
        ModItems.createItems();
        ModBlocks.preInit();
        ModItemBlocks.createItemblocks();
    }

    public void init(FMLInitializationEvent e)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(TombManyGraves.INSTANCE, new ModGuiHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(new FriendHandler());

        ModTiles.init();
    }

    public void postinit(FMLPostInitializationEvent e)
    {

    }

    public List<String> probeForFiles(BlockPos pos)
    {
//        LogHelper.info("Probing for files in Common!");
        return DeathInventoryHandler.getSavedInventories();
    }

    public void particleStream(Vector3f start, Vector3f end)
    {

    }

    public void pathFX(double x, double y, double z, float r, float g, float b, float size,
                       float motionX, float motionY, float motionZ, float maxAge)
    {

    }

    public void toggleGraveRendering()
    {
    }

    public void toggleGravePositionRendering()
    {

    }
}
