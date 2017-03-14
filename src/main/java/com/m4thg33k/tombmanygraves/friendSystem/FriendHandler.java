package com.m4thg33k.tombmanygraves.friendSystem;

import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.*;

public class FriendHandler {
    private static String PREFIX = "./TombManyGraves";
    private static String FILE = "./TombManyGraves/friends.fdat";
    private static HashMap<UUID, HashSet<UUID>> friendList;

    public static void importFriendsList()
    {
        friendList = null;
        try
        {
            checkFilePath();
            FileInputStream fileIn = new FileInputStream(FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            friendList = (HashMap<UUID, HashSet<UUID>>) in.readObject();
            in.close();
            fileIn.close();
            LogHelper.info("Successfully loaded the friend list.");
        } catch (Exception i)
        {
//            i.printStackTrace();
            LogHelper.warn("Could not find friend list file. Creating a new one.");
            friendList = new HashMap<>();
        }

        writeFriends();
    }

    public static boolean checkFilePath()
    {
        File file = new File(PREFIX);
        return file.exists() || file.mkdirs();
    }

    public static boolean hasAsFriend(UUID player1, UUID player2)
    {
        return friendList != null && friendList.containsKey(player1) && friendList.get(player1).contains(player2);
    }

    public static void writeFriends()
    {
        try
        {
            checkFilePath();
            FileOutputStream fileOut = new FileOutputStream(FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(friendList);
            out.close();
            fileOut.close();
        } catch (IOException i)
        {
            i.printStackTrace();
        }
    }

    public static boolean hasFriendList(UUID playerID)
    {
        return friendList != null && friendList.containsKey(playerID);
    }

    public static void createFriendListFor(UUID playerID)
    {
        if (friendList.containsKey(playerID))
        {
            return;
        }
        friendList.put(playerID, new HashSet<>());
        writeFriends();
    }

    public static HashSet<UUID> getFriendSetFor(UUID playerID)
    {
        if (friendList.containsKey(playerID))
        {
            return friendList.get(playerID);
        }
        return null;
    }

    public static List<String> getFriendStringListFor(MinecraftServer server, UUID playerID)
    {
        ArrayList<String> names = new ArrayList<>();

        HashSet<UUID> friends = getFriendSetFor(playerID);
        if (friends != null)
        {
            for (UUID id : friends)
            {
                try {
                    names.add(server.getPlayerProfileCache().getProfileByUUID(id).getName());
                } catch (NullPointerException e)
                {
                    LogHelper.error("Error getting name for id: " + id.toString());
                }
            }
        }
        return names;
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            FriendHandler.createFriendListFor(event.getEntity().getUniqueID());
        }
    }

    public static boolean addFriendToList(UUID owner, UUID friend)
    {
        if (friendList != null)
        {
            if (friendList.containsKey(owner))
            {
                boolean answer = friendList.get(owner).add(friend);
                writeFriends();
                return answer;
            }
            else
            {
                FriendHandler.createFriendListFor(owner);
                boolean answer = friendList.get(owner).add(friend);
                writeFriends();
                return answer;
            }
        }
        return false;
    }

    public static void printFriendList(MinecraftServer server, EntityPlayer player)
    {
        UUID playerID = player.getUniqueID();

        List<String> friends = getFriendStringListFor(server, playerID);

        if (friends.size() == 0)
        {
            player.sendMessage(new TextComponentString("You have not added any friends yet."));
            return;
        }

        for (String name : friends)
        {
            player.sendMessage(new TextComponentString(name));
        }
    }

    public static boolean removeFriend(UUID owner, UUID friend)
    {
        if (friendList != null && friendList.containsKey(owner))
        {
            boolean answer = friendList.get(owner).remove(friend);
            writeFriends();
            return answer;
        }
        return false;
    }

    public static void clearFriends(UUID owner)
    {
        if (friendList != null && friendList.containsKey(owner))
        {
            friendList.get(owner).clear();
            writeFriends();
        }
    }

    public static boolean isFriendOf(UUID owner, UUID friend)
    {
        return friendList != null && friendList.containsKey(owner) && friendList.get(owner).contains(friend);
    }
}
