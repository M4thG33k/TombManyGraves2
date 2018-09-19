package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves.api.GraveRegistry;
import com.m4thg33k.tombmanygraves.api.IGraveInventory;
import com.m4thg33k.tombmanygraves.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves.api.TempInventory;

import lain.mods.cos.CosmeticArmorReworked;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

@GraveRegistry(id = "cosmeticarmor", name = "Cosmetic Armor", overridable = true, reqMod = "cosmeticarmorreworked")
public class CosmeticArmorReworkedInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true; // No logic to stop graves
	}

	@Override
	public NBTBase getNbtData(EntityPlayer player) {
		return SpecialInventoryHelper.getTagListFromIInventory(CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()));
	}

	@Override
	public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
		if (compound instanceof NBTTagList) {
			TempInventory graveItems = new TempInventory((NBTTagList) compound);
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
						SpecialInventoryHelper.dropItem(player, playerItem);
					} else {
						// Slot is blocked, but we're not forcing items in -
						// drop the grave item
						SpecialInventoryHelper.dropItem(player, graveItem);
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ItemStack> getDrops(NBTBase compound) {
		if (compound instanceof NBTTagList) {
			return (new TempInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
		} else {
			return new ArrayList<ItemStack>();
		}
	}
}