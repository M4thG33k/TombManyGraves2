package com.m4thg33k.tombmanygraves.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;

public class TempInventory extends InventoryBasic {

	public TempInventory(int slotCount) {
		super(new TextComponentString("Temp"), slotCount);
	}

	public TempInventory(NBTTagList tagList) {
		this(getMaxSlotInTagList(tagList));

		this.readFromTagList(tagList);
	}

	private static int getMaxSlotInTagList(NBTTagList tagList) {
		int max = -1;
		for (int i = 0; i < tagList.size(); i++) {
			NBTTagCompound compound = tagList.getCompound(i);
			int val = compound.getByte("Slot") & 255;
			if (val > max) {
				max = val;
			}
		}

		return max + 1;
	}

	public NBTTagList writeToTagList(NBTTagList list) {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (!this.getStackInSlot(i).isEmpty()) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.putByte("Slot", (byte) i);
				this.getStackInSlot(i).write(compound);
				list.add(compound);
			}
		}

		return list;
	}

	public void readFromTagList(NBTTagList list) {
		for (int i = 0; i < list.size(); i++) {
			NBTTagCompound compound = list.getCompound(i);
			ItemStack stack = ItemStack.read(compound);
			this.setInventorySlotContents(compound.getByte("Slot") & 255, stack);
		}
	}

	public List<ItemStack> getListOfNonEmptyItemStacks() {
		List<ItemStack> ret = new ArrayList<>();
		for (int i = 0; i < this.getSizeInventory(); i++) {
			ItemStack inSlot = this.getStackInSlot(i);
			if (!inSlot.isEmpty()) {
				ret.add(inSlot);
			}
		}

		return ret;
	}

	public static List<String> getGuiStringsForItemStack(int number, ItemStack stack) {
		List<String> ret = new ArrayList<>();

		if (!stack.isEmpty()) {
			String name = stack.getDisplayName().getFormattedText();
			if (name.length() > 28) {
				name = name.substring(0, 25) + "...";
			}

			ret.add(number + ") " + name + (stack.getCount() > 1 ? " x" + stack.getCount() : ""));
			ret.addAll(EnchantmentHelper.getEnchantments(stack).entrySet().stream().map(entry -> "    " + entry.getKey().func_200305_d(entry.getValue())).collect(Collectors.toList()));

			// Todo possibly handle backpack items to show internal contents.
			// Maybe not...
		}

		return ret;
	}

	public static List<String> getGuiStringsForItemStackList(List<ItemStack> items) {
		List<String> ret = new ArrayList<>();
		AtomicInteger itemNumber = new AtomicInteger(1);

		items.stream().forEach(stack -> ret.addAll(getGuiStringsForItemStack(itemNumber.getAndIncrement(), stack)));

		return ret;
	}

	public ArrayList<String> getListOfItemsInInventoryAsStrings() {
		ArrayList<String> ret = new ArrayList<>();
		final AtomicInteger itemNumber = new AtomicInteger(1);

		this.getListOfNonEmptyItemStacks().stream().forEach(inSlot -> ret.addAll(getGuiStringsForItemStack(itemNumber.getAndIncrement(), inSlot)));

		return ret;
	}
}
