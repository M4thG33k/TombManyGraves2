package com.m4thg33k.tombmanygraves.commands;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class ModCommands {

    public static void initCommands(FMLServerStartingEvent e)
    {
    	CommandDispatcher<CommandSource> d = e.getCommandDispatcher();
    	CommandGetDeathList.register(d);
    	CommandFriends.register(d);
    	CommandGainOwnership.register(d);
    	CommandPopGrave.register(d);
    	CommandRestoreInventory.register(d);
//        event.registerServerCommand(new CommandDropInventory());
    }
}
