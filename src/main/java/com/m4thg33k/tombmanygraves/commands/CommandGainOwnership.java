package com.m4thg33k.tombmanygraves.commands;

import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandGainOwnership {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("tmg_steal").requires((source) -> {
			return source.hasPermissionLevel(2);
		});
		builder.then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((context) -> {
			TileEntity tile = context.getSource().asPlayer().world.getTileEntity(new BlockPos(context.getArgument("pos", ILocationArgument.class).getBlockPos(context.getSource())));
			if (tile == null || !(tile instanceof TileGrave)) {
				context.getSource().asPlayer().sendMessage(new TextComponentString("No grave exists at that location."));
			} else {
				((TileGrave) tile).setPlayer(context.getSource().asPlayer());
			}
			return 0;
		}));
		dispatcher.register(builder);
	}
}
