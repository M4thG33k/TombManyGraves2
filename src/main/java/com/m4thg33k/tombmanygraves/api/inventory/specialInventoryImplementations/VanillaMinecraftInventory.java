package com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VanillaMinecraftInventory extends AbstractSpecialInventory {

    public static final String UNIQUE_IDENTIFIER = "PlayerInventory";

    @Override
    public String getUniqueIdentifier() {
        return UNIQUE_IDENTIFIER;
    }

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        // Vanilla Minecraft always allows graves to form
        return true;
    }

    @Override
    public int getPriority() {
        // Vanilla Minecraft has priority 0
        return 0;
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {
        // really returning an NBTTagList
        return SpecialInventoryHelper.getTagListFromIInventory(player.inventory);
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagList){
            TransitionInventory graveItems = new TransitionInventory((NBTTagList)compound);
            IInventory currentInventory = player.inventory;

            for (int i=0; i<graveItems.getSizeInventory(); i++){
                ItemStack graveItem = graveItems.getStackInSlot(i);
                if (!graveItem.isEmpty()){
                    ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                    if (playerItem.isEmpty()){
                        // No problem, just put the grave item in!
                        currentInventory.setInventorySlotContents(i, graveItem);
                    } else if (shouldForce){
                        // Slot is blocked, but we're forcing the grave item into place.
                        currentInventory.setInventorySlotContents(i, graveItem);
                        SpecialInventoryHelper.dropItem(player, playerItem);
                    } else {
                        // Slot is blocked, but we're not forcing items in - drop the grave item
                        SpecialInventoryHelper.dropItem(player, graveItem);
                    }
                }
            }
        }

    }

    @Override
    @Nonnull
    public List<ItemStack> getDrops(NBTBase compound) {
        if (compound instanceof NBTTagList){
            return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String getInventoryDisplayNameForGui() {
        return "Main Inventory";
    }

    @Override
    public int getInventoryDisplayNameColorForGui() {
        return 0;
    }

    @Override
    public boolean isOverwritable() {
        return false;
    }
}
