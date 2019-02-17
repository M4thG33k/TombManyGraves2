package com.m4thg33k.tombmanygraves.invman;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.m4thg33k.tombmanygraves.api.TempInventory;
import com.m4thg33k.tombmanygraves.items.ItemDeathList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHolder {
    public static final String TAG_NAME = "InventoryHolder";
    public static final String EMPTY = "IsEmpty";
    public static final String TIMESTAMP = "Timestamp";
    public static final String PLAYER_NAME = "PlayerName";
    public static final String X = "Xcoord";
    public static final String Y = "Ycoord";
    public static final String Z = "Zcoord";

    private NBTTagCompound compound = new NBTTagCompound();
    private boolean isEmpty = true;
    private String timestamp = "";
    private int xcoord = - 1;
    private int ycoord = - 1;
    private int zcoord = - 1;
    private String playerName = "unknown";

    public InventoryHolder() {

    }

    public boolean isInventoryEmpty() {
        return isEmpty;
    }


    public void grabPlayerData(EntityPlayer player) {
    	HashMap<String, TempInventory> map = GraveInventoryManager.getInstance().grabItemsFromPlayer(player);
        isEmpty = map.isEmpty();
        compound = new NBTTagCompound();
        for(Entry<String, TempInventory> entry : map.entrySet()){
        	NBTTagList list = new NBTTagList();
        	entry.getValue().writeToTagList(list);
        	compound.put(entry.getKey(), list);
        }

        playerName = player.getName().getFormattedText();
        compound.putString(PLAYER_NAME, playerName);

        setTimestamp(new SimpleDateFormat("MM_dd_YYYY_HH_mm_ss").format(new Date()));
    }
    
    public List<ItemStack> getAllItems(){
    	List<ItemStack> list = new ArrayList<>();
    	for(String key : compound.keySet()){
    		TempInventory inv = new TempInventory(compound.getList(key, 10));
    		list.addAll(inv.getListOfNonEmptyItemStacks());
    	}
    	return list;
    }
    
    public void ensure(Collection<EntityItem> collection){
    	for(String key : compound.keySet()){
    		TempInventory inv = new TempInventory(compound.getList(key, 10));
    		for(int i = 0; i < inv.getSizeInventory(); i++){
    			boolean contains = false;
    			for(EntityItem itm : collection){
    				if(ItemStack.areItemStacksEqual(inv.getStackInSlot(i), itm.getItem())){
    					contains = true;
    					collection.remove(itm);
    					break;
    				}
    			}
    			if(!contains){
    				inv.setInventorySlotContents(i, ItemStack.EMPTY);
    			}
    		}
    		NBTTagList list = new NBTTagList();
    		inv.writeToTagList(list);
    		compound.put(key, list);
    	}
    	isEmpty = getAllItems().isEmpty();
    }

    public void grabPlayerData(EntityPlayer player, BlockPos pos) {
        grabPlayerData(player);
        setPosition(pos);
    }

    public void setPosition(BlockPos pos) {
        xcoord = pos.getX();
        ycoord = pos.getY();
        zcoord = pos.getZ();

        compound.putInt(X, xcoord);
        compound.putInt(Y, ycoord);
        compound.putInt(Z, zcoord);
    }

    public String getPlayerName() {
        return playerName;
    }

    public BlockPos getPosition() {
        return new BlockPos(xcoord, ycoord, zcoord);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        compound.putString(TIMESTAMP, timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static boolean isItemValidForGrave(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() instanceof ItemDeathList) {
            return false;
        }
        return true;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound inCompound) {
        inCompound.put(TAG_NAME, this.compound);
        inCompound.putBoolean(EMPTY, this.isEmpty);
        return inCompound;
    }

    public void readFromNBT(NBTTagCompound inCompound) {
        if (inCompound.contains(TAG_NAME)) {
            this.compound = inCompound.getCompound(TAG_NAME);
            this.isEmpty = inCompound.getBoolean(EMPTY);

            this.xcoord = compound.getInt(X);
            this.ycoord = compound.getInt(Y);
            this.zcoord = compound.getInt(Z);

            this.timestamp = compound.getString(TIMESTAMP);
            this.playerName = compound.getString(PLAYER_NAME);
        } else {
            this.compound = new NBTTagCompound();
            this.isEmpty = true;

            this.xcoord = - 1;
            this.ycoord = - 1;
            this.zcoord = - 1;

            this.timestamp = "";
            this.playerName = "unknown";
        }
    }

    // Used to insert (put not replace) inventory items to a player
    public void insertInventory(EntityPlayer player) {
        if (isEmpty) {
            return;
        }

        GraveInventoryManager.getInstance().insertInventory(player, compound, false);
    }

    // Used to force (replace) inventory items on the player
    public void forceInventory(EntityPlayer player) {
        if (isEmpty) {
            return;
        }

        GraveInventoryManager.getInstance().insertInventory(player, compound, true);
    }

    // Used to drop all items at a specific player's location
    public void dropInventory(EntityPlayer player) {
        dropInventory(player.getEntityWorld(), player.getPosition());
    }

    // Used to drop all items at a specific position in a world
    public void dropInventory(World world, BlockPos pos) {
        GraveInventoryManager
                .getInstance()
                .generateDrops(compound)
                .forEach(
                        itemStack -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack)
                );
    }

    public static void dropItem(EntityPlayer player, ItemStack stack) {
        InventoryHelper.spawnItemStack(player.getEntityWorld(), player.posX, player.posY, player.posZ, stack);
    }

    public static void dropItem(World world, BlockPos pos, ItemStack stack) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }

    public Map<String, Tuple<String, List<String>>> getItemStackStringsForGui() {
        return GraveInventoryManager.getInstance().createItemListForGui(compound);
    }

}
