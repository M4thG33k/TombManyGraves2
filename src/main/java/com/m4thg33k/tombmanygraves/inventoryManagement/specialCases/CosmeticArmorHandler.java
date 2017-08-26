/*package com.m4thg33k.tombmanygraves.inventoryManagement.specialCases;

import com.m4thg33k.tombmanygraves.client.gui.GuiDeathItems;
import com.m4thg33k.tombmanygraves.inventoryManagement.InventoryHolder;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CosmeticArmorHandler {

    public static final String INVENTORY = "Inventory";
    public static final String SLOT = "Slot";

    public CosmeticArmorHandler()
    {

    }

    public static List<NBTTagCompound> getCosmeticData(EntityPlayer player)
    {
        List<NBTTagCompound> ret = new ArrayList<>();

        InventoryCosArmor cosmeticInventory = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
        IInventory cosmetic = (IInventory)cosmeticInventory;

        if (cosmetic == null)
        {
            return ret;
        }
        else
        {
            NBTTagCompound compound = new NBTTagCompound();
            boolean grabbedItems = false;
            NBTTagList list = new NBTTagList();

            for (int i=0; i < cosmetic.getSizeInventory(); i++)
            {
                ItemStack stack = cosmetic.getStackInSlot(i);
                if (InventoryHolder.isItemValidForGrave(stack))
                {
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setByte(SLOT, (byte)i);
                    stack.writeToNBT(tag);

                    list.appendTag(tag);

                    cosmetic.setInventorySlotContents(i, ItemStack.EMPTY);
                    grabbedItems = true;
                }
            }

            if (grabbedItems)
            {
                compound.setTag(INVENTORY, list);
                ret.add(compound);

                cosmetic.markDirty();
            }

            return ret;
        }
    }

    public static void insertInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            InventoryCosArmor currentCosInv = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
            IInventory currentCos = (IInventory)currentCosInv;
            if (currentCos == null)
            {
                return;
            }

            NBTTagList list = compound.getTagList(INVENTORY, 10);
            boolean changedInventory = false;

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                int slot = (int)tag.getByte(SLOT);

                if (! currentCos.getStackInSlot(slot).isEmpty())
                {
                    InventoryHolder.dropItem(player, stack);
                }
                else {
                    currentCos.setInventorySlotContents(slot, stack);
                    changedInventory = true;
                }
            }

            if (changedInventory)
            {
                currentCos.markDirty();
            }
        }
    }

    public static void forceInventory(EntityPlayer player, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            InventoryCosArmor currentCosInv = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
            IInventory currentCos = (IInventory)currentCosInv;
            if (currentCos == null)
            {
                return;
            }

            NBTTagList list = compound.getTagList(INVENTORY, 10);
            boolean changedInventory = false;

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                int slot = (int)tag.getByte(SLOT);

                if (! currentCos.getStackInSlot(slot).isEmpty())
                {
                    InventoryHolder.dropItem(player, currentCos.getStackInSlot(slot));
                }

                currentCos.setInventorySlotContents(slot, stack);
                changedInventory = true;
            }

            if (changedInventory)
            {
                currentCos.markDirty();
            }
        }
    }

    public static void dropInventory(EntityPlayer player, NBTTagCompound compound)
    {
        dropInventory(player.world, player.getPosition(), compound);
    }

    public static void dropInventory(World world, BlockPos pos, NBTTagCompound compound)
    {
        if (compound.hasKey(INVENTORY))
        {
            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);

                if (!stack.isEmpty())
                {
                    InventoryHolder.dropItem(world, pos, stack);
                }
            }
        }
    }

    public static ArrayList<String> getListOfItemsInInventory(NBTTagCompound compound)
    {
        return InventoryHolder.getListOfItemsInInventory(compound, "Cosmetic Armor");
    }
}*/
