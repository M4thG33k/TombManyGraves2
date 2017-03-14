package com.m4thg33k.tombmanygraves.commands;

import com.m4thg33k.tombmanygraves.friendSystem.FriendHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandFriends extends CommandBase {

    public static List<String> parameters = new ArrayList<>();

    public CommandFriends()
    {
        super("tmg_friends", 0, true);
        parameters.add("add");
        parameters.add("remove");
        parameters.add("list");
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME + " [add/remove/list] [player]";
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer) || ((EntityPlayer) sender).world.isRemote)
        {
            return;
        }
        else
        {
            if (args.length == 0)
            {
                sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
            }
            else if (args.length == 1)
            {
                if (args[0].equals("list"))
                {
                    FriendHandler.printFriendList(server, (EntityPlayer)sender);
                }
                else
                {
                    sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
                }
            }
            else if (args.length == 2)
            {
                if (args[0].equals("list"))
                {
                    sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
                }
                else if (args[0].equals("add"))
                {
                    if (sender.getName().toLowerCase().equals(args[1].toLowerCase()))
                    {
                        sender.sendMessage(new TextComponentString("You cannot add yourself to your friends."));
                    }
                    else
                    {
                        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(args[1]);
                        if (profile == null)
                        {
                            sender.sendMessage(new TextComponentString("That player doesn't seem to exist."));
                            return;
                        }
                        UUID friend = profile.getId();

                        if (FriendHandler.addFriendToList(((EntityPlayer) sender).getUniqueID(), friend))
                        {
                            sender.sendMessage(new TextComponentString("Added " + args[1] + " to your friends."));
                        }
                        else
                        {
                            sender.sendMessage(new TextComponentString("Unable to add " + args[1] + "."));
                            sender.sendMessage(new TextComponentString("Maybe they were already there?"));
                        }
                    }
                }
                else if (args[0].equals("remove"))
                {
                    GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(args[1]);
                    if (profile == null)
                    {
                        sender.sendMessage(new TextComponentString("That player doesn't seem to exist."));
                        return;
                    }
                    UUID friend = profile.getId();

                    if (FriendHandler.removeFriend(((EntityPlayer) sender).getUniqueID(), friend))
                    {
                        sender.sendMessage(new TextComponentString("Removed " + args[1] + "."));
                    }
                    else
                    {
                        sender.sendMessage(new TextComponentString("Unable to remove " + args[1] + "."));
                        sender.sendMessage(new TextComponentString("Maybe they never existed?"));
                    }
                }
                else
                {
                    sender.sendMessage(new TextComponentString("Invalid usage: " + getUsage(sender)));
                }
            }
        }

    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
        {
            return getListOfStringMatchingLastWord(args, parameters);
        }
        else if (args.length == 2)
        {
            if  (args[0].equals("add"))
            {
                return getListOfStringMatchingLastWord(args, server.getOnlinePlayerNames());
            }
            else if (args[0].equals("remove") && sender instanceof EntityPlayer)
            {
                return getListOfStringMatchingLastWord(args,
                        FriendHandler.getFriendStringListFor(server, ((EntityPlayer) sender).getUniqueID()));
            }
            else
            {
                return new ArrayList<>();
            }
        }
        else
        {
            return new ArrayList<>();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }
}
