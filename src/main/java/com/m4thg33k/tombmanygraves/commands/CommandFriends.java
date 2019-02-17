package com.m4thg33k.tombmanygraves.commands;

import java.util.UUID;

import com.m4thg33k.tombmanygraves.commands.argtypes.UsernameArgument;
import com.m4thg33k.tombmanygraves.friends.FriendHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class CommandFriends {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal("tmg_friends").requires((source) -> {
			return source.hasPermissionLevel(0);
		});
		{ // add
			builder.then(Commands.literal("add").then(Commands.argument("username", UsernameArgument.get()).executes((context) -> {
				EntityPlayerMP sender = context.getSource().asPlayer();
				String username = context.getArgument("username", String.class);
				if (sender.getName().getFormattedText().toLowerCase().equals(username.toLowerCase())) {
					sender.sendMessage(new TextComponentString("You cannot add yourself to your friends."));
				} else {
					GameProfile profile = context.getSource().getServer().getPlayerProfileCache().getGameProfileForUsername(username);
					if (profile == null) {
						sender.sendMessage(new TextComponentString("That player doesn't seem to exist."));
						return 0;
					}
					UUID friend = profile.getId();

					if (FriendHandler.addFriendToList(((EntityPlayer) sender).getUniqueID(), friend)) {
						sender.sendMessage(new TextComponentString("Added " + username + " to your friends."));
					} else {
						sender.sendMessage(new TextComponentString("Unable to add " + username + "."));
						sender.sendMessage(new TextComponentString("Maybe they were already there?"));
					}
				}
				return 0;
			})));
		}
		{ // remove
			builder.then(Commands.literal("remove").then(Commands.argument("username", UsernameArgument.get()).executes((context) -> {
				EntityPlayerMP sender = context.getSource().asPlayer();
				String username = context.getArgument("username", String.class);
				GameProfile profile = context.getSource().getServer().getPlayerProfileCache().getGameProfileForUsername(username);
				if (profile == null) {
					sender.sendMessage(new TextComponentString("That player doesn't seem to exist."));
					return 0;
				}
				UUID friend = profile.getId();

				if (FriendHandler.removeFriend(((EntityPlayer) sender).getUniqueID(), friend)) {
					sender.sendMessage(new TextComponentString("Removed " + username + "."));
				} else {
					sender.sendMessage(new TextComponentString("Unable to remove " + username + "."));
					sender.sendMessage(new TextComponentString("Maybe they never existed?"));
				}
				return 0;
			})));
		}
		{ // list
			builder.then(Commands.literal("list").executes((context) -> {
				FriendHandler.printFriendList(context.getSource().getServer(), context.getSource().asPlayer());
				return 0;
			}));
		}
		dispatcher.register(builder);
	}
}
