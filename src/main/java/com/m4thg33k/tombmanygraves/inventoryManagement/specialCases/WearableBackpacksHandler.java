package com.m4thg33k.tombmanygraves.inventoryManagement.specialCases;

import com.m4thg33k.tombmanygraves.client.gui.GuiDeathItems;
import com.m4thg33k.tombmanygraves.inventoryManagement.InventoryHolder;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.backpacks.misc.BackpackSize;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WearableBackpacksHandler{

    public static final String BASE = "Base";
    public static final String STACK = "Stack";

    public WearableBackpacksHandler()
    {

    }

    public static List<NBTTagCompound> getBackpackData(EntityPlayer player)
    {
        List<NBTTagCompound> ret = new ArrayList<>();

        IBackpack backpack = player.getCapability(IBackpack.CAPABILITY, (EnumFacing)null);

        if (backpack != null && !backpack.getStack().isEmpty())
        {
            NBTBase base = backpack.getData().serializeNBT();
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag(BASE, base);

            ItemStack stack = backpack.getStack();
            NBTTagCompound stackTag = new NBTTagCompound();
            stack.writeToNBT(stackTag);
            compound.setTag(STACK, stackTag);

            BackpackHelper.setEquippedBackpack(player, ItemStack.EMPTY, (IBackpackData)null);

            if (InventoryHolder.isItemValidForGrave(stack)) {
                ret.add(compound);
            }
        }

        return ret;
    }

    // Used to insert (but not replace) items into a player's backpack slot
    public static void insertInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(STACK))
        {
            ItemStack stack = new ItemStack(compound.getCompoundTag(STACK));
            NBTTagCompound base = (NBTTagCompound)compound.getCompoundTag(BASE);
            BackpackSize size = BackpackSize.parse(base.getTag("size"));

            IBackpackData data = new BackpackDataItems(size);
            data.deserializeNBT(base);

            if (BackpackHelper.canEquipBackpack(player))
            {
                BackpackHelper.setEquippedBackpack(player, stack, data);
            }
            else
            {
                dropBackpackAndInventoryOnGround(player, stack, data);
            }
        }
    }

    // Used to force (replace) the currently equipped backpack with the saved one
    public static void forceInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(STACK) && compound.hasKey(BASE))
        {
            ItemStack stack = new ItemStack(compound.getCompoundTag(STACK));
            NBTTagCompound base = (NBTTagCompound)compound.getCompoundTag(BASE);
            BackpackSize size = BackpackSize.parse(base.getTag("size"));

            IBackpackData data = new BackpackDataItems(size);
            data.deserializeNBT(base);

            if (! BackpackHelper.canEquipBackpack(player))
            {
                // This means there is already a backpack equipped (or the armor slot is already full).
                // We must first remove the issue and then we may place the saved pack
                if (BackpackHelper.getBackpack(player) != null) {
                    IBackpack currentPack = BackpackHelper.getBackpack(player);

                    dropBackpackAndInventoryOnGround(player, currentPack.getStack(), currentPack.getData());
                }
                else
                {
                    InventoryHolder.dropItem(player, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
                }
            }

            BackpackHelper.setEquippedBackpack(player, stack, data);
        }
    }

    public static void dropInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(STACK))
        {
            ItemStack stack = new ItemStack(compound.getCompoundTag(STACK));
            NBTTagCompound base = (NBTTagCompound)compound.getCompoundTag(BASE);
            BackpackSize size = BackpackSize.parse(base.getTag("size"));

            IBackpackData data = new BackpackDataItems(size);
            data.deserializeNBT(base);

            dropBackpackAndInventoryOnGround(player, stack, data);
        }
    }

    public static void dropInventory(World world, BlockPos pos, NBTTagCompound compound)
    {
        if (compound.hasKey(STACK))
        {
            ItemStack stack = new ItemStack(compound.getCompoundTag(STACK));
            NBTTagCompound base = (NBTTagCompound)compound.getCompoundTag(BASE);
            BackpackSize size = BackpackSize.parse(base.getTag("size"));

            IBackpackData data = new BackpackDataItems(size);
            data.deserializeNBT(base);

            dropBackpackAndInventoryOnGround(world, pos, stack, data);
        }
    }

    public static void dropBackpackAndInventoryOnGround(EntityPlayer player, ItemStack packStack, IBackpackData data)
    {
        if (packStack.isEmpty() || data == null)
        {
            return;
        }

        ItemStackHandler items = ((BackpackDataItems)data).items;

        for (int i=0; i < items.getSlots(); i++)
        {
            ItemStack stack = items.getStackInSlot(i);
            if (stack.isEmpty())
            {
                continue;
            }

            InventoryHolder.dropItem(player, stack);
        }

        InventoryHolder.dropItem(player, packStack);
    }

    public static void dropBackpackAndInventoryOnGround(World world, BlockPos pos, ItemStack packStack, IBackpackData data)
    {
        if (packStack.isEmpty() || data == null)
        {
            return;
        }

        ItemStackHandler items = ((BackpackDataItems)data).items;

        for (int i=0; i < items.getSlots(); i++)
        {
            ItemStack stack = items.getStackInSlot(i);
            if (stack.isEmpty())
            {
                continue;
            }

            InventoryHolder.dropItem(world, pos, stack);
        }

        InventoryHolder.dropItem(world, pos, packStack);
    }

    public static ArrayList<String> getListOfItemsInInventory(NBTTagCompound compound)
    {
        ArrayList<String> ret = new ArrayList<>();

        if (compound.hasKey(STACK))
        {
            ret.add(GuiDeathItems.BREAK);
            ret.add("Wearable Backpack");
            ret.add(GuiDeathItems.BREAK);

            int itemNumber = 1;

            ItemStack stack = new ItemStack(compound.getCompoundTag(STACK));
            NBTTagCompound base = (NBTTagCompound)compound.getCompoundTag(BASE);
//            BackpackSize size = BackpackSize.parse(base.getTag("size"));
            BackpackSize size = new BackpackSize(9, 4);

            NBTTagCompound no_size = base.getCompoundTag("items");

            IBackpackData data = new BackpackDataItems(size);
            data.deserializeNBT(no_size);

            String packName = stack.getDisplayName();
            if (packName.length() > 28)
            {
                packName = packName.substring(0, 25) + "...";
            }
            ret.add("Backpack name: ");
            ret.add(packName);
            ret.add(" ");

            ItemStackHandler items = ((BackpackDataItems) data).items;

            for (int i=0; i < items.getSlots(); i++)
            {
                ItemStack itemStack = items.getStackInSlot(i);
                if (!itemStack.isEmpty())
                {
                    String name = itemStack.getDisplayName();

                    if (name.length() > 28)
                    {
                        name = name.substring(0, 25) + "...";
                    }

                    ret.add(itemNumber + ") " + name + (itemStack.getCount() > 1 ? " x" + itemStack.getCount() : ""));
                    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(itemStack);
                    ret.addAll(enchants.keySet().stream()
                        .map(e -> " -> " + e.getTranslatedName(enchants.get(e)))
                        .collect(Collectors.toList()));

                    itemNumber += 1;
                }
            }
        }

        return ret;
    }
}
