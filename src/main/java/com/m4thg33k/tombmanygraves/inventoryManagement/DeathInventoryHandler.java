package com.m4thg33k.tombmanygraves.inventoryManagement;

import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.util.ChatHelper;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DeathInventoryHandler {

    private static String FILE_PREFIX = "." + "/TombManyGraves/savedInventories";

    public static boolean checkFilePath()
    {
        File file = new File(FILE_PREFIX);
        boolean hasPath = true;
        if (!file.exists())
        {
            hasPath = file.mkdirs();
        }

        return hasPath;
    }

    public static boolean writeInventoryFile(InventoryHolder inventoryHolder)
    {
        boolean hasPath = checkFilePath();

        if (! ModConfigs.ALLOW_INVENTORY_SAVES || !hasPath)
        {
            return false;
        }

        boolean didWork = false;

        String filename = "/" + inventoryHolder.getPlayerName();
        String timestamp = inventoryHolder.getTimestamp();
        String filePostfix = timestamp + ".json";

        String fullFilename = FILE_PREFIX + filename + "#" + filePostfix;

        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound = inventoryHolder.writeToNBT(tagCompound);
        didWork = writePortion(fullFilename, tagCompound.toString());

        if (didWork)
        {
            try {
                Path from = Paths.get(fullFilename);
                Path to = Paths.get(FILE_PREFIX + filename + "#latest.json");
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return didWork;
    }

    private static boolean writePortion(String filename, String toWrite)
    {
        boolean didWork = true;

        try (FileWriter file = new FileWriter(filename))
        {
            file.write(toWrite);
            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            didWork = false;
        }

        return didWork;
    }

    public static void clearLatest(EntityPlayer player)
    {
        String filename = "/" + player.getName();
        String timestamp = "latest";
        String filePostfix = timestamp + ".json";

        String fullFilename = FILE_PREFIX + filename + "#" + filePostfix;
        writePortion(fullFilename, "{}");
    }

    public static List<String> getSavedInventories()
    {
        List<String> saved = new ArrayList<>();
        boolean doesPathExist = checkFilePath();
        if (!doesPathExist)
        {
            return saved;
        }
        String[] files = new File(FILE_PREFIX).list();
        for (String file : files)
        {
            if (file.endsWith(".json"))
            {
                saved.add(file);
            }
        }

        return saved;
    }

    public static List<String> getFilenames(String playerName)
    {
        List<String> saved = new ArrayList<>();
        boolean doesPathExist = checkFilePath();
        if (!doesPathExist)
        {
            return saved;
        }
        String[] files = new File(FILE_PREFIX).list();
        for (String file : files)
        {
            if (file.startsWith(playerName) && file.endsWith(".json"))
            {
                saved.add(file.substring(playerName.length()+1, file.length()-5));
            }
        }

        return saved;
    }

    public static boolean getDeathList(EntityPlayer player, String playerName, String timestamp)
    {
        boolean didWork = true;

        String filename = FILE_PREFIX + "/" + playerName + "#" + timestamp + ".json";

        BufferedReader reader;

        try
        {
            reader = new BufferedReader(new FileReader(filename));
            String fileData = reader.readLine();
            NBTTagCompound allNBT = JsonToNBT.getTagFromJson(fileData);
            if (allNBT.getKeySet().size() > 0)
            {
                ItemStack theList = new ItemStack(ModItems.itemDeathList, 1);
                theList.setTagCompound(allNBT);
                EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY, player.posZ, theList);
                player.world.spawnEntity(entityItem);
            }
            else
            {
                ChatHelper.sayMessage(player.world, player, playerName + " had no items upon death!");
            }
            reader.close();
        }
        catch (Exception e)
        {
            didWork = false;
        }
        return didWork;
    }
}
