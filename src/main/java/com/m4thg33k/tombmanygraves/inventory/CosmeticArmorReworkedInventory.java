package com.m4thg33k.tombmanygraves.inventory;

import com.m4thg33k.tombmanygraves.api.GraveInventoryHelper;
import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = "cosmeticarmor", name = "Cosmetic Armor", overridable = true, reqMod = "cosmeticarmorreworked")
public class CosmeticArmorReworkedInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true; // No logic to stop graves
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		return GraveInventoryHelper.storeInventory(CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()));
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		IInventory currentInventory = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());

		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);
			if (!graveItem.isEmpty()) {
				ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					// No problem, just put the grave item in!
					currentInventory.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					// Slot is blocked, but we're forcing the grave item
					// into place.
					currentInventory.setInventorySlotContents(i, graveItem);
					GraveInventoryHelper.dropItem(player, playerItem);
				} else {
					// Slot is blocked, but we're not forcing items in -
					// drop the grave item
					GraveInventoryHelper.dropItem(player, graveItem);
				}
			}
		}
	}
}