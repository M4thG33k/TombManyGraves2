package com.m4thg33k.tombmanygraves.inventory;

import com.m4thg33k.tombmanygraves.api.GraveInventoryHelper;
import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = "bauble", reqMod = "baubles", color = 0x5E8FFF, name = "Baubles")
public class BaublesInventory implements IGraveInventory {

    public static final String SLOT = "Slot";
    public static final String INVENTORY = "Inventory";

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true;
    }

    @Override
    public TempInventory getItems(EntityPlayer player) {
    	BaublesContainer container = (BaublesContainer) BaublesApi.getBaublesHandler(player);
    	TempInventory inv = new TempInventory(container.getSlots());
    	for(int i = 0; i < container.getSlots(); i++){
    		inv.setInventorySlotContents(i, container.getStackInSlot(i));
    	}
    	return inv;
    }
    
    @Override
    public void insertInventory(EntityPlayer player, TempInventory inventory, boolean shouldForce) {
            BaublesContainer container = (BaublesContainer)BaublesApi.getBaublesHandler(player);
            for (int i=0; i < inventory.getSizeInventory(); i++){
                ItemStack stack = inventory.getStackInSlot(i);

                if (container.getStackInSlot(i).isEmpty()){
                    container.setStackInSlot(i, stack);
                } else if(shouldForce){
                    // Force the grave's item into the slot after dropping the original
                    GraveInventoryHelper.dropItem(player, container.getStackInSlot(i).copy());
                    container.setStackInSlot(i, stack);
                } else {
                    // Leave the original in place, drop the grave item
                    GraveInventoryHelper.dropItem(player, stack);
                }
            }

    }
}
