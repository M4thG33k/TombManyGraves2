package com.m4thg33k.tombmanygraves.api.inventory.specialInventoryImplementations;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BaublesInventory extends AbstractSpecialInventory {

    public static final String SLOT = "Slot";
    public static final String INVENTORY = "Inventory";

    @Override
    public String getUniqueIdentifier() {
        return "BaubleInventory";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true;
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {

        BaublesContainer container = (BaublesContainer) BaublesApi.getBaublesHandler(player);
        NBTTagCompound compound = new NBTTagCompound();

        boolean grabbedItems = false;
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < container.getSlots(); i++) {
            ItemStack stack = container.getStackInSlot(i);
            if (SpecialInventoryHelper.isItemValidForGrave(stack)) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte(SLOT, (byte) i);
                stack.writeToNBT(tag);

                list.appendTag(tag);

                container.setStackInSlot(i, ItemStack.EMPTY);
                grabbedItems = true;
            }
        }

        if (grabbedItems) {
            compound.setTag(INVENTORY, list);
            return compound;
        } else {
            return null;
        }
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagCompound && ((NBTTagCompound) compound).hasKey(INVENTORY)){
            BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player);

            NBTTagList list = ((NBTTagCompound) compound).getTagList(INVENTORY, 10);

            for (int i=0; i<list.tagCount(); i++){
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                int slot = (int)tag.getByte(SLOT);

                if (container.getStackInSlot(slot).isEmpty()){
                    container.setStackInSlot(slot, stack);
                } else if(shouldForce){
                    // Force the grave's item into the slot after dropping the original
                    SpecialInventoryHelper.dropItem(player, container.getStackInSlot(slot).copy());
                    container.setStackInSlot(slot, stack);
                } else {
                    // Leave the original in place, drop the grave item
                    SpecialInventoryHelper.dropItem(player, stack);
                }
            }
        }

    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(NBTBase compound) {
        List<ItemStack> drops = new ArrayList<>();
        if (compound instanceof NBTTagCompound && ((NBTTagCompound) compound).hasKey(INVENTORY)){
            NBTTagList list = ((NBTTagCompound) compound).getTagList(INVENTORY, 10);

            for (int i=0; i<list.tagCount(); i++){
                drops.add(new ItemStack(list.getCompoundTagAt(i)));
            }
        }
        return drops;
    }

    @Override
    public String getInventoryDisplayNameForGui() {
        return
                "Baubles";
    }

    @Override
    public int getInventoryDisplayNameColorForGui() {
        return 0x5E8FFF;
    }

    @Override
    public boolean isOverwritable() {
        return false;
    }
}
