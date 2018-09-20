package com.m4thg33k.tombmanygraves.inventory.specialinventories;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import techguns.capabilities.TGExtendedPlayer;

@GraveRegistry(id = "techguns", name = "Techguns", overridable = true, reqMod = "techguns")
public class TechgunsInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true;
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		return SpecialInventoryHelper.storeInventory(TGExtendedPlayer.get(player).getTGInventory());
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		IInventory currentItems = TGExtendedPlayer.get(player).getTGInventory();

		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);
			if (!graveItem.isEmpty()) {
				ItemStack playerItem = currentItems.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					currentItems.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					currentItems.setInventorySlotContents(i, graveItem);
					SpecialInventoryHelper.dropItem(player, playerItem);
				} else {
					SpecialInventoryHelper.dropItem(player, graveItem);
				}
			}
		}
	}
}