package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves2api.api.ISpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.TransitionInventory;

import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC;
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

@SpecialInventory(id = "galacticraft", name = "Galacticraft", reqMod = "galacticraftcore", overridable = true)
public class GalacticraftInventory implements ISpecialInventory {
	
    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true;
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {
        return SpecialInventoryHelper.getTagListFromIInventory(AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP)player));
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagList && player instanceof EntityPlayerMP){
            TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
            IInventoryGC inventoryGC = AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP) player);

            for (int i=0; i<graveItems.getSizeInventory(); i++){
                ItemStack graveItem = graveItems.getStackInSlot(i);

                if (!graveItem.isEmpty()){
                    ItemStack playerItem = inventoryGC.getStackInSlot(i).copy();

                    if (playerItem.isEmpty()){
                        // No problem, just put the grave item in!
                        inventoryGC.setInventorySlotContents(i, graveItem);
                    } else if (shouldForce){
                        // Slot is blocked, but we're forcing it
                        inventoryGC.setInventorySlotContents(i, graveItem);
                        SpecialInventoryHelper.dropItem(player, playerItem);
                    } else {
                        // Slot is blocked, but not forcing
                        SpecialInventoryHelper.dropItem(player, graveItem);
                    }
                }
            }
        }

    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(NBTBase compound) {
        if (compound instanceof NBTTagList){
            return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
        } else {
            return new ArrayList<ItemStack>();
        }
    }
}