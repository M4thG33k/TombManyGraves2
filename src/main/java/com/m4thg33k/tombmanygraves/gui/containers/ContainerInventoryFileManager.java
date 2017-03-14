package com.m4thg33k.tombmanygraves.gui.containers;

import com.m4thg33k.tombmanygraves.inventoryManagement.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class ContainerInventoryFileManager extends BaseContainer {

    protected EntityPlayer player;

    public ContainerInventoryFileManager(EntityPlayer player){
        this.player = player;
    }

    public List<String> getFileNames()
    {
//        LogHelper.info("Getting file names inside container");
        return DeathInventoryHandler.getSavedInventories();
    }
}
