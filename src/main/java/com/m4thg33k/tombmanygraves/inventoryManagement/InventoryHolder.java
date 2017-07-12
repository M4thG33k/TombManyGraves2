package com.m4thg33k.tombmanygraves.inventoryManagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.client.gui.GuiDeathItems;
//import com.m4thg33k.tombmanygraves.inventoryManagement.specialCases.CosmeticArmorHandler;
//import com.m4thg33k.tombmanygraves.inventoryManagement.specialCases.CyberwareHandler;
//import com.m4thg33k.tombmanygraves.inventoryManagement.specialCases.InventoryPetsHandler;
import com.m4thg33k.tombmanygraves.inventoryManagement.specialCases.WearableBackpacksHandler;
import com.m4thg33k.tombmanygraves.items.ItemDeathList;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    public static final String INVENTORY = "Inventory";

    public static final String WEARABLE_BACKPACKS_INVENTORY = "WBInventory";
    public static final String COSMETIC_ARMOR_INVENTORY = "CosmeticArmorInventory";

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
        boolean gravePetCollecting = false;

        /*/ if cyberware is installed, check if the defrib is installed & able to be used
        if (TombManyGraves.CYBERWARE)
        {
            if (CyberwareHandler.willCyberHandleDeath(player))
            {
                return;
            }
        }

        if (TombManyGraves.INVENTORY_PETS) {
            if (InventoryPetsHandler.isGravePetActive(player)) {
                gravePetCollecting = true;
            } else {
                InventoryPetsHandler.resetGravePet(player);
            }
        }*/

        // Handle Wearable Backpacks
        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            List<NBTTagCompound> wback = WearableBackpacksHandler.getBackpackData(player);
            if (wback.size() == 1)
            {
                compound.setTag(WEARABLE_BACKPACKS_INVENTORY, wback.get(0));
                isEmpty = false;
            }
            else
            {
                compound.setTag(WEARABLE_BACKPACKS_INVENTORY, new NBTTagCompound());
            }
        }

        // Get Vanilla Player Data
        if (!gravePetCollecting)
        {
            compound.setTag(PLAYER_INVENTORY, getTagFromInventory(player.inventory));
        }

        // Handle Baubles
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

        /*/ Handle Cosmetic Armor
        if (TombManyGraves.COSMETIC_ARMOR)
        {
            List<NBTTagCompound> cosmetic = CosmeticArmorHandler.getCosmeticData(player);
            if (cosmetic.size() == 1)
            {
                compound.setTag(COSMETIC_ARMOR_INVENTORY, cosmetic.get(0));
                isEmpty = false;
            }
            else
            {
                compound.setTag(COSMETIC_ARMOR_INVENTORY, new NBTTagCompound());
            }
        }*/


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

//        if (TombManyGraves.INVENTORY_PETS && stack.getItem() == InventoryPets.petGrave)
//        {
//            return false;
//        }
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

        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            WearableBackpacksHandler.insertInventory(player, compound.getCompoundTag(WEARABLE_BACKPACKS_INVENTORY));
        }

        if (TombManyGraves.COSMETIC_ARMOR)
        {
        //   CosmeticArmorHandler.insertInventory(player, compound.getCompoundTag(COSMETIC_ARMOR_INVENTORY));
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

        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            WearableBackpacksHandler.forceInventory(player, compound.getCompoundTag(WEARABLE_BACKPACKS_INVENTORY));
        }

        if (TombManyGraves.COSMETIC_ARMOR)
        {
         //   CosmeticArmorHandler.forceInventory(player, compound.getCompoundTag(COSMETIC_ARMOR_INVENTORY));
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

        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            WearableBackpacksHandler.dropInventory(player, compound.getCompoundTag(WEARABLE_BACKPACKS_INVENTORY));
        }

        if (TombManyGraves.COSMETIC_ARMOR)
        {
         //   CosmeticArmorHandler.dropInventory(player, compound.getCompoundTag(COSMETIC_ARMOR_INVENTORY));
        }
    }

    // Used to drop all items at a specific position in a world
    public void dropInventory(World world, BlockPos pos)
    {
        TransitionInventory saved = getSavedPlayerInventory();
        for (int i=0; i < saved.getSizeInventory(); i++)
        {
            ItemStack stack = saved.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                dropItem(world, pos, stack);
            }
        }

        if (TombManyGraves.BAUBLES)
        {
            BaubleInventoryHandler.dropInventory(world, pos, compound.getCompoundTag(BAUBLE_INVENTORY));
        }

        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            WearableBackpacksHandler.dropInventory(world, pos, compound.getCompoundTag(WEARABLE_BACKPACKS_INVENTORY));
        }

        if (TombManyGraves.COSMETIC_ARMOR)
        {
         //   CosmeticArmorHandler.dropInventory(world, pos, compound.getCompoundTag(COSMETIC_ARMOR_INVENTORY));
        }

    }

    public static void dropItem(EntityPlayer player, ItemStack stack)
    {
        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.posX, player.posY, player.posZ, stack);
    }

    public static void dropItem(World world, BlockPos pos, ItemStack stack)
    {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
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

    public ArrayList<String> getListOfItemsInWearableBackpack()
    {
        if (TombManyGraves.WEARABLE_BACKPACKS)
        {
            return WearableBackpacksHandler.getListOfItemsInInventory(compound.getCompoundTag(WEARABLE_BACKPACKS_INVENTORY));
        }

        return new ArrayList<>();
    }

    public ArrayList<String> getListOfItemsInCosmeticArmor()
    {
        if (TombManyGraves.COSMETIC_ARMOR)
        {
        //    return CosmeticArmorHandler.getListOfItemsInInventory(compound.getCompoundTag(COSMETIC_ARMOR_INVENTORY));
        }
        return new ArrayList<>();
    }

    public static ArrayList<String> getListOfItemsInInventory(NBTTagCompound compound, String invName)
    {
        ArrayList<String> ret = new ArrayList<>();

        if (compound.hasKey(INVENTORY)) {
            ret.add(GuiDeathItems.BREAK);
            ret.add(invName);
            ret.add(GuiDeathItems.BREAK);

            int itemNumber = 1;

            NBTTagList list = compound.getTagList(INVENTORY, 10);

            for (int i=0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(tag);
                String name = stack.getDisplayName();

                if (name.length() > 28)
                {
                    name = name.substring(0, 25) + "...";
                }

                ret.add(itemNumber + ") " + name + (stack.getCount() > 1 ? " x" + stack.getCount() : ""));
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                ret.addAll(enchants.keySet().stream()
                        .map(e -> "  -> " + e.getTranslatedName(enchants.get(e)))
                        .collect(Collectors.toList()));

                itemNumber += 1;
            }
        }

        return ret;
    }
}
