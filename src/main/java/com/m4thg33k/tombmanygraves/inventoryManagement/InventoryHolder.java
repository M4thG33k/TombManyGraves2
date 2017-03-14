package com.m4thg33k.tombmanygraves.inventoryManagement;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.client.gui.GuiDeathItems;
import com.m4thg33k.tombmanygraves.items.ItemDeathList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryHolder {
    public static final String TAG_NAME = "InventoryHolder";
    public static final String PLAYER_INVENTORY = "PlayerInventory";
    public static final String EMPTY = "IsEmpty";
    public static final String TIMESTAMP = "Timestamp";
    public static final String PLAYER_NAME = "PlayerName";
    public static final String BAUBLE_INVENTORY = "BaubleInventory";
    public static final String X = "Xcoord";
    public static final String Y = "Ycoord";
    public static final String Z = "Zcoord";

    private NBTTagCompound compound = new NBTTagCompound();
    private boolean isEmpty = true;
    private String timestamp = "";
    private int xcoord = -1;
    private int ycoord = -1;
    private int zcoord = -1;
    private String playerName = "unknown";

    public InventoryHolder()
    {

    }

    public boolean isInventoryEmpty()
    {
        return isEmpty;
    }

    public void grabPlayerData(EntityPlayer player)
    {
        compound = new NBTTagCompound();
        compound.setTag(PLAYER_INVENTORY, getTagFromInventory(player.inventory));

        if (TombManyGraves.BAUBLES)
        {
            List<NBTTagCompound> baubles = BaubleInventoryHandler.getBaubleData(player);
            if (baubles.size() == 1)
            {
                compound.setTag(BAUBLE_INVENTORY, baubles.get(0));
                isEmpty = false;
            }
            else
            {
                compound.setTag(BAUBLE_INVENTORY, new NBTTagCompound());
            }
        }
        else
        {
            compound.setTag(BAUBLE_INVENTORY, new NBTTagCompound());
        }


        playerName = player.getName();
        compound.setString(PLAYER_NAME, playerName);

        setTimestamp(new SimpleDateFormat("MM_dd_YYYY_HH_mm_ss").format(new Date()));
    }

    public void grabPlayerData(EntityPlayer player, BlockPos pos)
    {
        grabPlayerData(player);

        setPosition(pos);
    }

    public void setPosition(BlockPos pos)
    {
        xcoord = pos.getX();
        ycoord = pos.getY();
        zcoord = pos.getZ();

        compound.setInteger(X, xcoord);
        compound.setInteger(Y, ycoord);
        compound.setInteger(Z, zcoord);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public BlockPos getPosition()
    {
        return new BlockPos(xcoord, ycoord, zcoord);
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
        compound.setString(TIMESTAMP, timestamp);
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    private NBTTagList getTagFromInventory(IInventory inventory)
    {
        TransitionInventory copy = new TransitionInventory(inventory.getSizeInventory());
        for (int i=0; i< inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (isItemValidForGrave(stack))
            {
                copy.setInventorySlotContents(i, stack.copy());
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                isEmpty = false;
            }
        }

        return copy.writeToTagList(new NBTTagList());
    }

    public static boolean isItemValidForGrave(ItemStack stack)
    {
        if (stack.isEmpty()
                || stack.getItem() instanceof ItemDeathList
                || stack.getItem() == Item.getItemFromBlock(ModBlocks.blockGrave))
        {
            return false;
        }
        return true;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound inCompound)
    {
        inCompound.setTag(TAG_NAME, this.compound);
        inCompound.setBoolean(EMPTY, this.isEmpty);
        return inCompound;
    }

    public void readFromNBT(NBTTagCompound inCompound)
    {
        if (inCompound.hasKey(TAG_NAME))
        {
            this.compound = inCompound.getCompoundTag(TAG_NAME);
            this.isEmpty = inCompound.getBoolean(EMPTY);

            this.xcoord = compound.getInteger(X);
            this.ycoord = compound.getInteger(Y);
            this.zcoord = compound.getInteger(Z);

            this.timestamp = compound.getString(TIMESTAMP);
            this.playerName = compound.getString(PLAYER_NAME);
        }
        else
        {
            this.compound = new NBTTagCompound();
            this.isEmpty = true;

            this.xcoord = -1;
            this.ycoord = -1;
            this.zcoord = -1;

            this.timestamp = "";
            this.playerName = "unknown";
        }
    }

    // Used to insert (put not replace) inventory items to a player
    public void insertInventory(EntityPlayer player)
    {
        if (isEmpty)
        {
            return;
        }

        TransitionInventory saved = getSavedPlayerInventory(player);
        for (int i=0; i<saved.getSizeInventory(); i++)
        {
            ItemStack inSaved = saved.getStackInSlot(i);
            if (!inSaved.isEmpty())
            {
                if (player.inventory.getStackInSlot(i).isEmpty())
                {
                    player.inventory.setInventorySlotContents(i, inSaved);
                }
                else
                {
                    dropItem(player, inSaved);
                }
            }
        }

        if (TombManyGraves.BAUBLES)
        {
            BaubleInventoryHandler.insertInventory(player, compound.getCompoundTag(BAUBLE_INVENTORY));
        }
    }

    // Used to force (replace) inventory items on the player
    public void forceInventory(EntityPlayer player)
    {
        if (isEmpty)
        {
            return;
        }

        TransitionInventory saved = getSavedPlayerInventory(player);
        for (int i=0; i<saved.getSizeInventory(); i++)
        {
            ItemStack inSaved = saved.getStackInSlot(i);
            if (!inSaved.isEmpty())
            {
                ItemStack onPlayer = player.inventory.getStackInSlot(i).copy();
                player.inventory.setInventorySlotContents(i, inSaved);
                if (!onPlayer.isEmpty())
                {
                    dropItem(player, onPlayer);
                }
            }
        }

        if (TombManyGraves.BAUBLES)
        {
            BaubleInventoryHandler.forceInventory(player, compound.getCompoundTag(BAUBLE_INVENTORY));
        }
    }

    // Used to drop all items at a specific player's location
    public void dropInventory(EntityPlayer player)
    {
        TransitionInventory saved = getSavedPlayerInventory(player);
        for (int i=0; i <saved.getSizeInventory(); i++)
        {
            ItemStack stack = saved.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                dropItem(player, stack);
            }
        }

        if (TombManyGraves.BAUBLES)
        {
            BaubleInventoryHandler.dropInventory(player, compound.getCompoundTag(BAUBLE_INVENTORY));
        }
    }

    public static void dropItem(EntityPlayer player, ItemStack stack)
    {
        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.posX, player.posY, player.posZ, stack);
    }

    private TransitionInventory getSavedPlayerInventory(EntityPlayer player)
    {
        TransitionInventory saved = new TransitionInventory(player.inventory.getSizeInventory());
        saved.readFromTagList(compound.getTagList(PLAYER_INVENTORY, 10));
        return saved;
    }

    private TransitionInventory getSavedPlayerInventory(int size)
    {
        TransitionInventory saved = new TransitionInventory(size);
        saved.readFromTagList(compound.getTagList(PLAYER_INVENTORY, 10));
        return saved;
    }

    private TransitionInventory getSavedPlayerInventory()
    {
        NBTTagList list = compound.getTagList(PLAYER_INVENTORY, 10);
        TransitionInventory saved = new TransitionInventory(list);
        saved.readFromTagList(compound.getTagList(PLAYER_INVENTORY, 10));
        return saved;
    }

    public ArrayList<String> createListOfItemsInMainInventory()
    {
        TransitionInventory saved = getSavedPlayerInventory();
        ArrayList<String> ret = saved.getListOfItemsInInventory();
        ret.add(0, "Main Inventory");
        ret.add(1, GuiDeathItems.BREAK);
        ret.add(0, GuiDeathItems.BREAK);
        return ret;
//        return saved.getListOfItemsInInventory().add(0, "Main Inventory");
    }

    public ArrayList<String> getListOfBaubles()
    {
        if (TombManyGraves.BAUBLES)
        {
            return BaubleInventoryHandler.getListOfItemsInInventory(compound.getCompoundTag(BAUBLE_INVENTORY));
        }

        return new ArrayList<>();
    }
}
