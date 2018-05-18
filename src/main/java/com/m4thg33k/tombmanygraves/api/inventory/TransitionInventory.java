package com.m4thg33k.tombmanygraves.api.inventory;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TransitionInventory extends InventoryBasic {

    public TransitionInventory(int slotCount) {
        super("Temp", false, slotCount);
    }

    public TransitionInventory(NBTTagList tagList) {
        this(TransitionInventory.getMaxSlotInTagList(tagList));

        this.readFromTagList(tagList);
    }

    private static int getMaxSlotInTagList(NBTTagList tagList) {
        int max = - 1;
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            int val = compound.getByte("Slot") & 255;
            if (val > max) {
                max = val;
            }
        }

        return max + 1;
    }

    public NBTTagList writeToTagList(NBTTagList list) {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            if (! this.getStackInSlot(i).isEmpty()) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setByte("Slot", (byte) i);
                this.getStackInSlot(i).writeToNBT(compound);
                list.appendTag(compound);
            }
        }

        return list;
    }

    public void readFromTagList(NBTTagList list) {
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(compound);
            this.setInventorySlotContents(compound.getByte("Slot") & 255, stack);
        }
    }

    public List<ItemStack> getListOfNonEmptyItemStacks() {
        List<ItemStack> ret = new ArrayList<>();
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack inSlot = this.getStackInSlot(i);
            if (! inSlot.isEmpty()) {
                ret.add(inSlot);
            }
        }

        return ret;
    }

    public static List<String> getGuiStringsForItemStack(int number, ItemStack stack){
        List<String> ret = new ArrayList<>();

        if (!stack.isEmpty()){
            String name = stack.getDisplayName();
            if (name.length() > 28){
                name = name.substring(0, 25) + "...";
            }

            ret.add(number + ") " + name + (stack.getCount() > 1 ? " x" + stack.getCount() : ""));
            ret.addAll(
                    EnchantmentHelper.getEnchantments(stack)
                    .entrySet()
                    .stream()
                    .map(entry -> "    " + entry.getKey().getTranslatedName(entry.getValue()))
                    .collect(Collectors.toList())
            );

            // Todo possibly handle backpack items to show internal contents. Maybe not...
        }

        return ret;
    }

    public static List<String> getGuiStringsForItemStackList(List<ItemStack> items){
        List<String> ret = new ArrayList<>();
        AtomicInteger itemNumber = new AtomicInteger(1);

        items.stream().forEach(
                stack -> ret.addAll(getGuiStringsForItemStack(itemNumber.getAndIncrement(), stack))
        );

        return ret;
    }

    public ArrayList<String> getListOfItemsInInventoryAsStrings() {
        ArrayList<String> ret = new ArrayList<>();
        final AtomicInteger itemNumber = new AtomicInteger(1);

        this.getListOfNonEmptyItemStacks()
                .stream()
                .forEach(
                        inSlot -> ret.addAll(getGuiStringsForItemStack(itemNumber.getAndIncrement(), inSlot))
                );

        return ret;
    }
}
