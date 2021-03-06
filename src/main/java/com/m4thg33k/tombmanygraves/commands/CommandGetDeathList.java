package com.m4thg33k.tombmanygraves.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandGetDeathList extends CommandBase {

    public CommandGetDeathList()
    {
        super("tmg_getDeathList", 0, false);

        aliases.add("tmg_deathlist");
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME + " [player] <timestamp or latest>";
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
        {
            return getListOfStringMatchingLastWord(args, server.getOnlinePlayerNames());
        }

        if (args.length == 2)
        {
            return getListOfStringMatchingLastWord(args, DeathInventoryHandler.getFilenames(args[0]));
        }

        return new ArrayList<>();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender.getEntityWorld().isRemote)
        {
            return;
        }

        if (! ModConfigs.ALLOW_INVENTORY_SAVES)
        {
            sender.sendMessage(new TextComponentString("This command has been disabled."));
            return;
        }

        if (args.length < 2)
        {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        boolean worked = DeathInventoryHandler.getDeathList((EntityPlayer)sender, (EntityPlayer)sender, args[0], args[1], false);
        if (!worked)
        {
            sender.sendMessage(new TextComponentString("Failed to retrieve list."));
            sender.sendMessage(new TextComponentString("Check spelling and timestamp."));
        }
    }
}
