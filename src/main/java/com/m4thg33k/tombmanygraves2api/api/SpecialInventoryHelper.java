package com.m4thg33k.tombmanygraves2api.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpecialInventoryHelper {

    public static boolean isItemValidForGrave(ItemStack stack){
        if (stack.isEmpty()
                || stack.getItem() instanceof IInvalidGraveItem
                ){
            return false;
        }

        return true;
    }

    public static NBTTagList getTagListFromIInventory(IInventory inventory){
        TransitionInventory copy = new TransitionInventory(inventory.getSizeInventory());
        boolean isEmpty = true;

        for (int i=0; i < inventory.getSizeInventory(); i++){
            ItemStack stack = inventory.getStackInSlot(i);

            if (isItemValidForGrave(stack)){
                copy.setInventorySlotContents(i, stack.copy());
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                isEmpty = false;
            }
        }

        return isEmpty ? null : copy.writeToTagList(new NBTTagList());
    }

    public static void dropItem(EntityPlayer player, ItemStack stack){
        dropItem(player.getEntityWorld(), player.getPosition(), stack);
    }

    public static void dropItem(World world, BlockPos pos, ItemStack stack){
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
