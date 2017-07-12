package com.m4thg33k.tombmanygraves.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand {

    public String COMMAND_NAME;

    protected List<String> aliases;
    protected int requiredPermissionLevel;
    protected boolean requireRealPlayer = false;

    public CommandBase(@Nonnull String name, int requiredPermissionLevel, boolean requireRealPlayer)
    {
        COMMAND_NAME = name;
        this.requiredPermissionLevel = requiredPermissionLevel;
        this.requireRealPlayer = requireRealPlayer;
        aliases = new ArrayList<>();
    }

    @Nonnull
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(this.requiredPermissionLevel, this.getName()) &&
                (!requireRealPlayer || sender instanceof EntityPlayer);
    }

    @Override
    public int compareTo(@Nonnull ICommand o) {
        return this.getName().compareTo(o.getName());
    }

    public static boolean doesStringStartWith(String original, String region)
    {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    public static List<String> getListOfStringMatchingLastWord(String[] args, String... possibilities)
    {
        return getListOfStringMatchingLastWord(args, Arrays.asList(possibilities));
    }

    public static List<String> getListOfStringMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions)
    {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.<String>newArrayList();

        if (!possibleCompletions.isEmpty())
        {
            for (String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction()))
            {
                if (doesStringStartWith(s, s1))
                {
                    list.add(s1);
                }
            }
        }

        if (list.isEmpty())
        {
            for (Object object : possibleCompletions)
            {
                if (object instanceof ResourceLocation &&
                        doesStringStartWith(s, ((ResourceLocation) object).getResourcePath()))
                {
                    list.add(String.valueOf(object));
                }
            }
        }

        return list;
    }
}
