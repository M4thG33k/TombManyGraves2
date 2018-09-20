package com.m4thg33k.tombmanygraves.inventory.specialinventories;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import micdoodle8.mods.galacticraft.api.inventory.AccessInventoryGC;
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@GraveRegistry(id = "galacticraft", name = "Galacticraft", reqMod = "galacticraftcore", overridable = true)
public class GalacticraftInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true;
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		return SpecialInventoryHelper.storeInventory(AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP) player));
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		IInventoryGC inventoryGC = AccessInventoryGC.getGCInventoryForPlayer((EntityPlayerMP) player);
		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);

			if (!graveItem.isEmpty()) {
				ItemStack playerItem = inventoryGC.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					// No problem, just put the grave item in!
					inventoryGC.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					// Slot is blocked, but we're forcing it
					inventoryGC.setInventorySlotContents(i, graveItem);
					SpecialInventoryHelper.dropItem(player, playerItem);
				} else {
					// Slot is blocked, but not forcing
					SpecialInventoryHelper.dropItem(player, graveItem);
				}
			}
		}
	}
}