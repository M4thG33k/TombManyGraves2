package com.m4thg33k.tombmanygraves.inventory;

import com.m4thg33k.tombmanygraves.api.GraveInventoryHelper;
import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import de.eydamos.backpack.data.PlayerSave;
import de.eydamos.backpack.helper.BackpackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = "eydamosbackpacks", name = "Backpacks", overridable = true, reqMod = "backpack")
public class BackpackInventory implements IGraveInventory {

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true; // no logic to stop graves
    }

    @Override
    public TempInventory getItems(EntityPlayer player) {
        ItemStack backpack = BackpackHelper.getBackpackFromPlayer(player, false); // get backpack in slot
        if (! backpack.isEmpty()) {
            return GraveInventoryHelper.storeInventory(PlayerSave.loadPlayer(player.getEntityWorld(), player));
        } else {
            return null;
        }
    }

    @Override
    public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
            IInventory currentInventory = PlayerSave.loadPlayer(player.getEntityWorld(), player);

            for (int i = 0; i < graveItems.getSizeInventory(); i++) {
                ItemStack graveItem = graveItems.getStackInSlot(i);
                if (! graveItem.isEmpty()) {
                    ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                    if (playerItem.isEmpty()) {
                        // No problem, just put the grave item in!
                        currentInventory.setInventorySlotContents(i, graveItem);
                    } else if (shouldForce) {
                        // Slot is blocked, but we're forcing the grave item into place.
                        currentInventory.setInventorySlotContents(i, graveItem);
                        GraveInventoryHelper.dropItem(player, playerItem);
                    } else {
                        // Slot is blocked, but we're not forcing items in - drop the grave item
                        GraveInventoryHelper.dropItem(player, graveItem);
                    }
                }
            }
    }
}