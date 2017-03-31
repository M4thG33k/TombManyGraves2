package com.m4thg33k.tombmanygraves.commands;

import com.m4thg33k.tombmanygraves.network.TMGNetwork;
import com.m4thg33k.tombmanygraves.network.packets.GravePosTogglePacket;
import com.m4thg33k.tombmanygraves.network.packets.GraveRenderTogglePacket;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class CommandToggleGravePos extends CommandBase {

    public CommandToggleGravePos()
    {
        super("tmg_toggle_grave_pos", 0, true);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP))
        {
            return;
        }
        TMGNetwork.sendTo(new GravePosTogglePacket(), (EntityPlayerMP)sender);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
