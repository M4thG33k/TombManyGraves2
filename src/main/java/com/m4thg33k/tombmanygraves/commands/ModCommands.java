package com.m4thg33k.tombmanygraves.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ModCommands {

    public static void initCommands(FMLServerStartingEvent event)
    {
//        event.registerServerCommand(new CommandOpenFileManager());
        event.registerServerCommand(new CommandGetDeathList());
        event.registerServerCommand(new CommandFriends());
    }
}
