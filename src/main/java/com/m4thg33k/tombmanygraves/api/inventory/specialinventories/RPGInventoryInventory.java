package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves2api.api.ISpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.TransitionInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.rpginventory.capability.playerinventory.RpgInventoryData;

@SpecialInventory(id = "rpginventory", name = "RPG Inventory", overridable = true, reqMod = "rpginventory")
public class RPGInventoryInventory implements ISpecialInventory{

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true; // don't stop grave logic
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            RpgInventoryData inventoryData = RpgInventoryData.get(player);

            if (inventoryData != null){
                ItemStackHandler data = inventoryData.getInventory();

                TransitionInventory transitionInventory = new TransitionInventory(data.getSlots());
                for (int i=0; i <data.getSlots(); i++){
                    ItemStack stack = data.getStackInSlot(i);
                    if (SpecialInventoryHelper.isItemValidForGrave(stack)){
                        transitionInventory.setInventorySlotContents(i, stack);
                        data.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                return transitionInventory.writeToTagList(new NBTTagList());
            }
            return null;
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            RpgInventoryData inventoryData = RpgInventoryData.get(player);
            if (compound instanceof NBTTagList && inventoryData != null){
                TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);

                ItemStackHandler currentInventory =  inventoryData.getInventory();

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            currentInventory.setStackInSlot(i, graveItem);
                        } else if (shouldForce){
                            currentInventory.setStackInSlot(i, graveItem);
                            SpecialInventoryHelper.dropItem(player, playerItem);
                        } else {
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
            }
            return new ArrayList<ItemStack>();
        }
    }