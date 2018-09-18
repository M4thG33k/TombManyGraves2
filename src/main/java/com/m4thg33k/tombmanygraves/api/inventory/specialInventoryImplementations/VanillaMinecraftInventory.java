package com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves2api.api.ISpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.TransitionInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

@SpecialInventory(id = VanillaMinecraftInventory.UNIQUE_IDENTIFIER, name = "Main Inventory")
public class VanillaMinecraftInventory implements ISpecialInventory {

	public static final String UNIQUE_IDENTIFIER = "PlayerInventory";
	
    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        // Vanilla Minecraft always allows graves to form
        return true;
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
}
