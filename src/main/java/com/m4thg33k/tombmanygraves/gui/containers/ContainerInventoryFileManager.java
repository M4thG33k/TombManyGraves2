package com.m4thg33k.tombmanygraves.gui.containers;

import java.util.List;

import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;

import net.minecraft.entity.player.EntityPlayer;

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
