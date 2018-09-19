package com.m4thg33k.tombmanygraves2api.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpecialInventoryHelper {

	public static TempInventory storeInventory(IInventory inventory) {
		TempInventory copy = new TempInventory(inventory.getSizeInventory());

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			copy.setInventorySlotContents(i, stack.copy());
		}
		return copy;
	}

	public static void dropItem(EntityPlayer player, ItemStack stack) {
		dropItem(player.getEntityWorld(), player.getPosition(), stack);
	}

	public static void dropItem(World world, BlockPos pos, ItemStack stack) {
		InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	}
}
