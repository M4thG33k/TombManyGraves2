package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import techguns.capabilities.TGExtendedPlayer;

@GraveRegistry(id = "techguns", name = "Techguns", overridable = true, reqMod = "techguns")
public class TechgunsInventory implements IGraveInventory{

        @Override
        public boolean pregrabLogic(EntityPlayer player) {
            return true;
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player) {
            return SpecialInventoryHelper.getTagListFromIInventory(TGExtendedPlayer.get(player).getTGInventory());
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
            if (compound instanceof NBTTagList){
                TempInventory graveItems = new TempInventory((NBTTagList)compound);
                IInventory currentItems = TGExtendedPlayer.get(player).getTGInventory();

                for (int i=0; i<graveItems.getSizeInventory(); i++){
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty()){
                        ItemStack playerItem = currentItems.getStackInSlot(i).copy();

                        if (playerItem.isEmpty()){
                            currentItems.setInventorySlotContents(i, graveItem);
                        } else if (shouldForce){
                            currentItems.setInventorySlotContents(i, graveItem);
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
                return (new TempInventory((NBTTagList)compound)).getListOfNonEmptyItemStacks();
            } else {
                return new ArrayList<ItemStack>();
            }
        }
    }