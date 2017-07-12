package com.m4thg33k.tombmanygraves.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandOpenFileManager extends CommandBase{

    public CommandOpenFileManager()
    {
        super("tmg_files", 0, true);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public String getUsage(ICommandSender sender) {
        return "/tmg_files will bring up a gui to access death backup files (if enabled)";
    }

    @ParametersAreNonnullByDefault
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (sender instanceof EntityPlayer)
//        {
//            LogHelper.info("Opening gui!");
//            ((EntityPlayer) sender).openGui(TombManyGraves.INSTANCE, ModGuiHandler.DEATH_INVENTORIES,
//                    sender.getEntityWorld(), sender.getPosition().getX(), sender.getPosition().getY(),
//                    sender.getPosition().getZ());
//        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return new ArrayList<>();
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
