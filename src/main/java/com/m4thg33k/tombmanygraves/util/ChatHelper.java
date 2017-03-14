package com.m4thg33k.tombmanygraves.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ChatHelper {
    public static void sayMessage(World world, EntityPlayer player, String text)
    {
        if(!world.isRemote)
        {
            player.sendMessage(new TextComponentString(text));
//            player.addChatMessage(new TextComponentString(text));
        }
    }

    public static void sayMessage(EntityPlayer player, String text)
    {
        sayMessage(player.world, player, text);
    }
}
