package com.m4thg33k.tombmanygraves.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.tiles.TileGrave;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class CommandGainOwnership extends CommandBase {

    public CommandGainOwnership()
    {
        super("tmg_steal", 2, true);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME + " [x] [y] [z]";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (world.isRemote || !(sender instanceof EntityPlayer))
        {
            return;
        }

        if (args.length != 3)
        {
            sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
        }

        int[] coords = new int[3];
        for (int i=0; i<3; i++)
        {
            try{
                coords[i] = Integer.parseInt(args[i]);
            } catch (Exception e)
            {
                sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
            }
        }

        TileEntity tile = world.getTileEntity(new BlockPos(coords[0], coords[1], coords [2]));
        if (tile == null || !(tile instanceof TileGrave))
        {
            sender.sendMessage(new TextComponentString("No grave exists at that location."));
        }
        else
        {
            ((TileGrave) tile).setPlayer((EntityPlayer)sender);
        }

    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
