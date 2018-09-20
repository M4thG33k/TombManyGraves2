package com.m4thg33k.tombmanygraves.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.invman.InventoryHolder;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandDropInventory extends CommandBase {

    public CommandDropInventory()
    {
        super("tmg_drop", 2, false);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME + " <player> <timestamp or latest> [receiving player]";
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

        EntityPlayer receiver = args.length > 2 ?
                sender.getEntityWorld().getPlayerEntityByName(args[2]) :
                sender.getEntityWorld().getPlayerEntityByName(args[0]);

        if (receiver == null)
        {
            sender.sendMessage(new TextComponentString("Either the owning or receiving player is offline."));
            return;
        }

        NBTTagCompound savedData = DeathInventoryHandler.getSavedInventoryAsNBT(args[0], args[1]);
        if (savedData == null)
        {
            sender.sendMessage(new TextComponentString("Either the owning player is misspelled or the timestamp is invalid"));
            return;
        }

        InventoryHolder holder = new InventoryHolder();
        holder.readFromNBT(savedData);

        holder.dropInventory(receiver);
        sender.sendMessage(new TextComponentString("Inventory restored."));
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1 || args.length == 3)
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
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0 || index == 2;
    }
}
