package com.m4thg33k.tombmanygraves.inventoryManagement;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TransitionInventory extends InventoryBasic {

    public TransitionInventory(int slotCount)
    {
        super("Temp", false, slotCount);
    }

    public TransitionInventory(NBTTagList tagList)
    {
        this(TransitionInventory.getMaxSlotInTagList(tagList));
    }

    public NBTTagList writeToTagList(NBTTagList list)
    {
        for (int i=0; i<this.getSizeInventory(); i++)
        {
            if (!this.getStackInSlot(i).isEmpty())
            {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setByte("Slot", (byte)i);
                this.getStackInSlot(i).writeToNBT(compound);
                list.appendTag(compound);
            }
        }

        return list;
    }

    public void readFromTagList(NBTTagList list)
    {
        for (int i=0; i<list.tagCount(); i++)
        {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(compound);
            this.setInventorySlotContents(compound.getByte("Slot") & 255, stack);
        }
    }

    private static int getMaxSlotInTagList(NBTTagList tagList)
    {
        int max = -1;
        for (int i=0; i<tagList.tagCount();i++)
        {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            int val = compound.getByte("Slot") & 255;
            if (val > max)
            {
                max = val;
            }
        }
        return max + 1;
    }

    public ArrayList<String> getListOfItemsInInventory()
    {
        ArrayList<String> stringList = new ArrayList<>();
        int itemNumber = 1;

        for (int i=0; i < this.getSizeInventory(); i++)
        {
            ItemStack inSlot = this.getStackInSlot(i);
            if (!inSlot.isEmpty())
            {
                String name = inSlot.getDisplayName();
                if (name.length() > 28)
                {
                    name = name.substring(0, 25) + "...";
                }

                stringList.add(itemNumber + ") " + name + (inSlot.getCount()>1 ? " x" + inSlot.getCount() : ""));
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(inSlot);
                stringList.addAll(
                        enchants.keySet()
                                .stream()
                                .map(e -> "  -> " + e.getTranslatedName(enchants.get(e)))
                                .collect(Collectors.toList())
                );
//                for (Enchantment key : enchants.keySet())
//                {
//                    stringList.add("  -> " + key.getTranslatedName(enchants.get(key)));
//                }

                // TODO: 2/27/2017 handle backpack cases

                itemNumber += 1;
            }
        }

        return stringList;
    }
}
