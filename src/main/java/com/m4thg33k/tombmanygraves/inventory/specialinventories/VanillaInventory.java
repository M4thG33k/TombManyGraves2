package com.m4thg33k.tombmanygraves.inventory.specialinventories;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = VanillaInventory.UNIQUE_IDENTIFIER, name = "Inventory")
public class VanillaInventory implements IGraveInventory {

	public static final String UNIQUE_IDENTIFIER = "vanilla";

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		// Vanilla Minecraft always allows graves to form
		return true;
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		return SpecialInventoryHelper.storeInventory((player.inventory));
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		IInventory currentInventory = player.inventory;

		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);
			if (!graveItem.isEmpty()) {
				ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					// No problem, just put the grave item in!
					currentInventory.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					// Slot is blocked, but we're forcing the grave item into
					// place.
					currentInventory.setInventorySlotContents(i, graveItem);
					SpecialInventoryHelper.dropItem(player, playerItem);
				} else {
					// Slot is blocked, but we're not forcing items in - drop
					// the grave item
					SpecialInventoryHelper.dropItem(player, graveItem);
				}
			}
		}

	}
}
