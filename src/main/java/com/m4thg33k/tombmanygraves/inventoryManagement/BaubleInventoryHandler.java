package com.m4thg33k.tombmanygraves.inventoryManagement;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BaubleInventoryHandler {

    public static final String INVENTORY = "Inventory";
    public static final String SLOT = "Slot";

    public BaubleInventoryHandler()
    {

    }

    public static List<NBTTagCompound> getBaubleData(EntityPlayer player)
    {
        List<NBTTagCompound> ret = new ArrayList<>();

        BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player);

        NBTTagCompound compound = new NBTTagCompound();
        boolean grabbedItems = false;
        NBTTagList list = new NBTTagList();

        for (int i=0; i < container.getSlots(); i++)
        {
            ItemStack stack = container.getStackInSlot(i);
            if (InventoryHolder.isItemValidForGrave(stack))
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte(SLOT, (byte)i);
                stack.writeToNBT(tag);

                list.appendTag(tag);

                container.setStackInSlot(i, ItemStack.EMPTY);
                grabbedItems = true;
            }
        }

        if (grabbedItems)
        {
            compound.setTag(INVENTORY, list);
            ret.add(compound);
        }

        return ret;
    }

    // Used to insert (but not replace) items into a player's Bauble Inventory
    public static void insertInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player);

            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                int slot = (int)tag.getByte(SLOT);

                if (! container.getStackInSlot(slot).isEmpty())
                {
                    InventoryHolder.dropItem(player, stack);
                }
                else
                {
                    container.setStackInSlot(slot, stack);
                }
            }
        }
    }

    // Used to force (replace) items into a player's Bauble Inventory
    public static void forceInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player);

            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                int slot = (int)tag.getByte(SLOT);

                if (! container.getStackInSlot(slot).isEmpty())
                {
                    InventoryHolder.dropItem(player, container.getStackInSlot(slot));
                }

                container.setStackInSlot(slot, stack);
            }
        }
    }

    // Used to drop all items at a specific player's location
    public static void dropInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);

                InventoryHolder.dropItem(player, stack);
            }
        }
    }

    public static void dropInventory(World world, BlockPos pos, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);

                if (!stack.isEmpty())
                {
                    InventoryHolder.dropItem(world, pos, stack);
                }
            }
        }
    }

    public static ArrayList<String> getListOfItemsInInventory(NBTTagCompound compound)
    {
        return InventoryHolder.getListOfItemsInInventory(compound, "Baubles");
//        ArrayList<String> ret = new ArrayList<>();
//
//        if (compound.hasKey(INVENTORY)) {
//            ret.add(GuiDeathItems.BREAK);
//            ret.add("Baubles");
//            ret.add(GuiDeathItems.BREAK);
//
//            int itemNumber = 1;
//
//            NBTTagList list = compound.getTagList(INVENTORY, 10);
//
//            for (int i=0; i < list.tagCount(); i++)
//            {
//                NBTTagCompound tag = list.getCompoundTagAt(i);
//                ItemStack stack = new ItemStack(tag);
//                String name = stack.getDisplayName();
//
//                if (name.length() > 28)
//                {
//                    name = name.substring(0, 25) + "...";
//                }
//
//                ret.add(itemNumber + ") " + name + (stack.getCount() > 1 ? " x" + stack.getCount() : ""));
//                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
//                ret.addAll(enchants.keySet().stream()
//                        .map(e -> "  -> " + e.getTranslatedName(enchants.get(e)))
//                        .collect(Collectors.toList()));
//
//                itemNumber += 1;
//            }
//        }
//
//        return ret;
    }
}
