package com.m4thg33k.tombmanygraves.inventory;

import com.legacy.aether.containers.inventory.InventoryAccessories;
import com.legacy.aether.player.PlayerAether;
import com.m4thg33k.tombmanygraves.api.GraveInventoryHelper;
import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
 * Does not work AetherLegacy (tested on v3.2) since they dont want to use PlayerDropsEvent to add the accessory drops.
 * Instead, they drop the items themselves and there is pretty much no way for me to catch that.
 */
@GraveRegistry(id = "aether_legacy", name = "Aether", reqMod = "aether_legacy", overridable = true)
public class AetherLegacyInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true;
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		InventoryAccessories ac = PlayerAether.get(player).accessories;
		for(int i = 0; i < ac.getSizeInventory(); i++){
			System.out.println(ac.getStackInSlot(i));
		}
		return GraveInventoryHelper.storeInventory(PlayerAether.get(player).accessories);
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		InventoryAccessories inventory = PlayerAether.get(player).accessories;
		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);

			if (!graveItem.isEmpty()) {
				ItemStack playerItem = inventory.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					// No problem, just put the grave item in!
					inventory.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					// Slot is blocked, but we're forcing it
					inventory.setInventorySlotContents(i, graveItem);
					GraveInventoryHelper.dropItem(player, playerItem);
				} else {
					// Slot is blocked, but not forcing
					GraveInventoryHelper.dropItem(player, graveItem);
				}
			}
		}
	}
}