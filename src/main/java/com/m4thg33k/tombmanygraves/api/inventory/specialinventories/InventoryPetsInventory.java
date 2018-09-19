package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.inventorypets.InventoryPets;
import com.inventorypets.capabilities.CapabilityRefs;
import com.inventorypets.capabilities.ICapabilityPlayer;
import com.m4thg33k.tombmanygraves2api.api.IGraveInventory;
import com.m4thg33k.tombmanygraves2api.api.GraveRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;

@GraveRegistry(id = "inventorypets", name = "Inventory Pets", overridable = true, reqMod = "inventorypets")
public class InventoryPetsInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		// If the grave pet is active; stop grave logic

		boolean gravePetKeepsInventory = false;
		for (int i = 0; i < 10; i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == InventoryPets.petGrave && stack.getItemDamage() == 0) {
				gravePetKeepsInventory = true;
				break;
			}
		}

		if (!gravePetKeepsInventory) {
			ICapabilityPlayer props = CapabilityRefs.getPlayerCaps(player);
			props.setRestoreItems(false);
		}

		return !gravePetKeepsInventory;
	}

	@Override
	public NBTBase getNbtData(EntityPlayer player) {
		return null;
	}

	@Override
	public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {

	}

	@Nonnull
	@Override
	public List<ItemStack> getDrops(NBTBase compound) {
		return new ArrayList<ItemStack>();
	}
}