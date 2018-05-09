package com.m4thg33k.tombmanygraves.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface ISpecialInventory {

    /**
     * Returns the unique identifier for the inventory in question;
     * Unique identifiers should be alphanumeric (including underscore "_").
     * Failure to follow this format will cause an error.
     * If two special inventories with the same id are attempted to be "installed", an error will be thrown
     **/
    default String getUniqueIdentifier() {
        return "Create a unique id for " + this.getClass().toString() + "!";
    }

    // As of this moment, priority actually does not affect the order in which inventories are handled.
    // In the future, this may be necessary, but I wanted this method in the API to begin with.
    //
    // Return an integer between -10 and 10 (both inclusive). Larger numbers have a higher priority.
    // Meaning that 3 > 2 => an inventory with a priority of 3 will have its methods handled before an inventory with a
    // priority of 2.
    // The default (vanilla) Minecraft player inventory has a priority of 0 (zero).
    // 99.99% of the time, you should leave the priority as 0.
    // If two inventories have the same priority, they will be handled in order of their unique identifier
    // (using the standard lexigraphical order).
    default int getPriority() {
        return 0;
    }

    // Any implementation returning true from this method can be replaced by another implementation with the same
    // unique identifier. This is mainly used if a mod author wants to absorb a plugin that I (M4thG33k) created
    // into their own mod. *MY* implementations (apart from Baubles for now) will return true, allowing a break
    // from my implementation to theirs, if/when desired.
    default boolean isOverwritable(){
        return false;
    }


    // Pre-grab logic happens before the grave starts iterating through inventories to save into a grave.
    // Use this time to do any checks and see if anything within the special inventory should cause the grave not
    // to form. Inventories should not be modified at this time!!
    // This method should return false if and only if grave logic should cease (meaning a grave will not form and
    // all items in inventories will be handled elsewhere)
    default boolean pregrabLogic() {
        return true;
    }

    // Returns the NBTBase used to encode the items in the inventory.
    // This methods should return null if and only if the inventory has no items - this is to allow the grave creation
    // algorithm to determine whether or not any items are being saved in the grave (if no items would be saved,
    // the grave is not formed!)
    default NBTBase getNbtData(EntityPlayer player) {
        return null;
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
    default void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
    }

    // Return a list of items contained in the NBTBase compound that would have gone into the inventory.
    // This method is used to drop those items in-world in certain circumstances.
    // The subclass of NBTBase passed in here can be assumed to be the same type returned via the implementation
    // of the getNbtData(...) method above.
    //
    // This method is also used by Tomb Many Graves to determine the Death Inventory List
    @Nonnull
    default List<ItemStack> getDrops(NBTBase compound) {
        return new ArrayList<>();
    }

    // Returns the string used inside GUIs (for example, the death inventory list) for this specific inventory.
    // For example, vanilla Minecraft uses "Main Inventory" and Baubles uses "Baubles".
    // The vanilla Minecraft "Main Inventory" item will be displayed first, followed by all other inventories
    // in order of the value returned by this method in lexigraphical order.
    default String getInventoryDisplayNameForGui() {
        return "Unnamed inventory for " + this.getClass().toString();
    }

    // Return the color you wish the inventory display name to be within the death inventory list.
    default int getInventoryDisplayNameColorForGui() {
        return 0;
    }
}
