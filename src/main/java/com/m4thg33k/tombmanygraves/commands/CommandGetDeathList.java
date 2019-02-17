package com.m4thg33k.tombmanygraves.commands;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.commands.argtypes.TimestampArgument;
import com.m4thg33k.tombmanygraves.commands.argtypes.UsernameArgument;
import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandGetDeathList {

	public static final SimpleCommandExceptionType INVALID_TIMESTAMP = new SimpleCommandExceptionType(new TextComponentTranslation("argument.tmg.timestamp"));
	public static final SimpleCommandExceptionType DISABLED_COMMAND = new SimpleCommandExceptionType(new TextComponentTranslation("argument.tmg.disabled"));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {

		dispatcher.register(Commands.literal("tmg_deathlist").requires((source) -> source.hasPermissionLevel(0)).then(Commands.argument("player", UsernameArgument.get()).executes((context) -> execute(context.getSource().asPlayer(), context.getArgument("player", String.class), "latest")).then(Commands.argument("timestamp", TimestampArgument.get()).executes((context) -> execute(context.getSource().asPlayer(), context.getArgument("player", String.class), context.getArgument("timestamp", String.class))))));
	}

	public static int execute(EntityPlayerMP sender, String player, String file) throws CommandSyntaxException {
		if (!ModConfigs.ALLOW_INVENTORY_SAVES) {
			throw DISABLED_COMMAND.create();
		}

		boolean worked = DeathInventoryHandler.getDeathList(sender, sender, player, file, false);
		if (!worked) {
			throw INVALID_TIMESTAMP.create();
		}
		return 0;
	}
}
