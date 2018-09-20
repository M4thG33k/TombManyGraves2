package com.m4thg33k.tombmanygraves.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IGraveInventory {

    // Pre-grab logic happens before the grave starts iterating through inventories to save into a grave.
    // Use this time to do any checks and see if anything within the special inventory should cause the grave not
    // to form. Inventories should not be modified at this time!!
    // This method should return false if and only if grave logic should cease (meaning a grave will not form and
    // all items in inventories will be handled elsewhere)
    default boolean pregrabLogic(EntityPlayer player) {
        return true;
    }

    // Returns the NBTBase used to encode the items in the inventory.
    // This methods should return null if and only if the inventory has no items - this is to allow the grave creation
    // algorithm to determine whether or not any items are being saved in the grave (if no items would be saved,
    // the grave is not formed!)
    default TempInventory getItems(EntityPlayer player) {
        return new TempInventory(0);
    }

    // Use the NBTBase compound to insert items into the special inventory.
    // The subclass of NBTBase passed in here can be assumed to be the same type returned via the implementation
    // of the getNbtData(...) method above.
    //
    // This method should allow items to go back into their original slot if possible. If its original slot
    // is not empty, then:
    // A) If shouldForce is true, the item currently in the slot should be dropped and the
    // item from compound should enter the slot. Otherwise...
    // B) If shouldForce is false, the item currently in the slot stays there and the item from compound
    // should be dropped on the ground instead.
    default void insertInventory(EntityPlayer player, TempInventory inventory, boolean shouldForce) {
    }
}
