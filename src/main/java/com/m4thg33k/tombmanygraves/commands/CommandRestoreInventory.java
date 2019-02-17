package com.m4thg33k.tombmanygraves.commands;

import com.m4thg33k.tombmanygraves.commands.argtypes.TimestampArgument;
import com.m4thg33k.tombmanygraves.commands.argtypes.UsernameArgument;
import com.m4thg33k.tombmanygraves.invman.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.invman.InventoryHolder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandRestoreInventory {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("tmg_restore").requires((source) -> {
			return source.hasPermissionLevel(2);
		});
		builder.then(Commands.argument("player", UsernameArgument.get()).then((Commands.argument("timestamp", TimestampArgument.get())
			.executes((context) -> {
			return execute(context.getSource(), context.getArgument("player", String.class), context.getArgument("timestamp", String.class), context.getArgument("player", String.class));
		}).then(Commands.argument("recieving", UsernameArgument.get())
			.executes((context) ->{
			return execute(context.getSource(), context.getArgument("player", String.class), context.getArgument("timestamp", String.class), context.getArgument("recieving", String.class));
		})))));
		dispatcher.register(builder);
	}

	public static int execute(CommandSource source, String player, String timestamp, String recieving) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		EntityPlayer receiver = server.getPlayerList().getPlayerByUsername(recieving);

		if (receiver == null) {
			source.sendFeedback(new TextComponentString("The receiving player is offline."), true);
			return 0;
		}

		NBTTagCompound savedData = DeathInventoryHandler.getSavedInventoryAsNBT(player, timestamp);
		if (savedData == null) {
			source.sendFeedback(new TextComponentString("Either the owning player is misspelled or the timestamp is invalid"), true);
			return 0;
		}

		InventoryHolder holder = new InventoryHolder();
		holder.readFromNBT(savedData);

		holder.forceInventory(receiver);
		source.sendFeedback(new TextComponentString("Inventory restored."), true);
		return 0;
	}
}
