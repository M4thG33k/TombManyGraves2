package com.m4thg33k.tombmanygraves.tiles;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.friendSystem.FriendHandler;
import com.m4thg33k.tombmanygraves.inventoryManagement.InventoryHolder;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.util.ChatHelper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class TileGrave extends TileEntity {

    // static final Strings for use as NBT tags (keep it consistent yo!)
    private static final String TAG_CAMO = "camo";
    private static final String TAG_CAMO_META = "camoMeta";
    private static final String SKULL_TAG = "SkullOwner";
    public static final String PLAYER_NAME = "PlayerName";
    public static final String ANGLE_OF_DEATH = "AngleOfDeath";
    public static final String LOCKED = "IsLocked";
    public static final String PLAYER_UUID = "PlayerID";
    public static final String GRAVE_PRIORITY = "GravePriority";

    private boolean GIVE_ITEMS_IN_GRAVE_PRIORITY = ModConfigs.GIVE_ITEMS_IN_GRAVE_PRIORITY;

    private boolean locked = ModConfigs.DEFAULT_TO_LOCKED;

    // default to M4thGeek's ID to keep away complaints from dirty pirates
    private String playerName = "M4thG33k";
    private UUID playerID = UUID.fromString("905379e2-068f-44c9-965b-6b9fbe1a6140");

    private int angle = 0;
    private boolean shouldRenderGround = false;
    private ItemStack skull = null;

    private IBlockState camoState;
    private String timestamp = "";

    private InventoryHolder savedInventory = new InventoryHolder();

    public TileGrave()
    {

    }

    private void setPlayerName(@Nonnull String name)
    {
        playerName = name;
        this.setSkull();
    }

    public String getPlayerName()
    {
        return playerName;
    }

    private void setPlayerID(UUID id)
    {
        this.playerID = id;
    }

    public void setPlayer(EntityPlayer player)
    {
        this.setPlayerName(player.getName());
        this.setPlayerID(player.getUniqueID());
        this.markDirty();
        world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void getPlayerData(@Nonnull EntityPlayer player)
    {
        angle = (int) player.rotationYawHead;

        if (world.isRemote)
        {
            return;
        }

        setPlayerName(player.getName());
        setPlayerID(player.getUniqueID());

        this.markDirty();
        world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);

    }

    public InventoryHolder getSavedInventory()
    {
        return savedInventory;
    }

    public void setSavedInventory(InventoryHolder holder)
    {
        this.savedInventory = holder;
        this.timestamp = holder.getTimestamp();
    }

    private void setSkull()
    {
        skull = new ItemStack(Items.SKULL, 1, 3);
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag(SKULL_TAG, new NBTTagString(playerName));
        skull.setTagCompound(compound);
    }

    public void onRightClick(@Nonnull EntityPlayer player)
    {
        if (this.hasAccess(player))
        {
            this.toggleGravePriority();
            ChatHelper.sayMessage(player,
                    GIVE_ITEMS_IN_GRAVE_PRIORITY ?
                            "Grave items will be forced into their original slots" :
                            "Items in your inventory will not move.");
        }
        else
        {
            ChatHelper.sayMessage(player, "You do not have permission to interact with this grave.");
        }
    }

    public boolean isSamePlayer(EntityPlayer player)
    {
        return ModConfigs.ALLOW_GRAVE_ROBBING || player.getUniqueID().equals(playerID);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        playerName = compound.getString(PLAYER_NAME);
        this.setSkull();
        angle = compound.getInteger(ANGLE_OF_DEATH);
        locked = compound.getBoolean(LOCKED);
        playerID = compound.getUniqueId(PLAYER_UUID);
        GIVE_ITEMS_IN_GRAVE_PRIORITY = compound.getBoolean(GRAVE_PRIORITY);
        Block b = Block.getBlockFromName(compound.getString(TAG_CAMO));
        if (b != null)
        {
            camoState = b.getStateFromMeta(compound.getInteger(TAG_CAMO_META));
        }

        savedInventory = new InventoryHolder();
        savedInventory.readFromNBT(compound);

        timestamp = compound.getString(InventoryHolder.TIMESTAMP);

        setShouldGroundRender();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setString(PLAYER_NAME, playerName);
        compound.setInteger(ANGLE_OF_DEATH, angle);
        compound.setBoolean(LOCKED, locked);
        if (playerID != null)
        {
            compound.setUniqueId(PLAYER_UUID, playerID);
        }

        compound.setBoolean(GRAVE_PRIORITY, GIVE_ITEMS_IN_GRAVE_PRIORITY);

        if (camoState != null)
        {
            compound.setString(TAG_CAMO, Block.REGISTRY.getNameForObject(camoState.getBlock()).toString());
            compound.setInteger(TAG_CAMO_META, camoState.getBlock().getMetaFromState(camoState));
        }

        compound = savedInventory.writeToNBT(compound);
        compound.setString(InventoryHolder.TIMESTAMP, timestamp);

        return compound;
    }

    private void toggleGravePriority()
    {
        GIVE_ITEMS_IN_GRAVE_PRIORITY = !GIVE_ITEMS_IN_GRAVE_PRIORITY;
        markDirty();
        world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public void onCollision(EntityPlayer player)
    {
        if (world.isRemote || locked || !(hasAccess(player)))
        {
            return;
        }

        removeCorrespondingDeathList(player);

        if (GIVE_ITEMS_IN_GRAVE_PRIORITY)
        {
            savedInventory.forceInventory(player);
        }
        else
        {
            savedInventory.insertInventory(player);
        }
        world.setBlockToAir(pos);
    }

    public void dropGraveContentsAt(World worldIn, BlockPos posIn)
    {
        if (worldIn.isRemote)
        {
            return;
        }

        savedInventory.dropInventory(worldIn, posIn);
        world.setBlockToAir(pos);
    }

    public void dropGraveContentsHere()
    {
        dropGraveContentsAt(world, pos);
    }

    private void removeCorrespondingDeathList(EntityPlayer player)
    {
        for (int i=0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.itemDeathList)
            {
                NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
                if (tagCompound != null && tagCompound.hasKey(InventoryHolder.TAG_NAME))
                {
                    NBTTagCompound tag = tagCompound.getCompoundTag(InventoryHolder.TAG_NAME);
                    if (!tag.hasKey(InventoryHolder.TIMESTAMP))
                    {
                        continue;
                    }
                    String time = tag.getString(InventoryHolder.TIMESTAMP);
                    if (time.equals(this.timestamp))
                    {
                        player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        playerName = pkt.getNbtCompound().getString(PLAYER_NAME);
        this.setSkull();
        angle = pkt.getNbtCompound().getInteger(ANGLE_OF_DEATH);
        locked = pkt.getNbtCompound().getBoolean(LOCKED);
        playerID = pkt.getNbtCompound().getUniqueId(PLAYER_UUID);
        GIVE_ITEMS_IN_GRAVE_PRIORITY = pkt.getNbtCompound().getBoolean(GRAVE_PRIORITY);

        Block b = Block.getBlockFromName(pkt.getNbtCompound().getString(TAG_CAMO));
        if (b != null)
        {
            camoState = b.getStateFromMeta(pkt.getNbtCompound().getInteger(TAG_CAMO_META));
        }
        setShouldGroundRender();
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = this.getUpdateTag();
        return new SPacketUpdateTileEntity(pos, 0, compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        compound.removeTag(InventoryHolder.TAG_NAME);
        return compound;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    public int getAngle()
    {
        return angle;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void toggleLock(EntityPlayer player)
    {
        if (world.isRemote)
        {
            return;
        }

        if (hasAccess(player)) {
            locked = ! locked;
            if (ModConfigs.ALLOW_LOCKING_MESSAGES)
            {
                ChatHelper.sayMessage(player, "The grave is now " + (locked ? "" : "un") + "locked!");
            }

            markDirty();
            world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
        }
        else
        {
            ChatHelper.sayMessage(player, "You do not have permission to modify this grave.");
        }
    }

    public boolean isFriend(EntityPlayer player)
    {
        return FriendHandler.hasAsFriend(playerID, player.getUniqueID());
    }

    public boolean hasAccess(EntityPlayer player)
    {
        return ModConfigs.ALLOW_GRAVE_ROBBING || isSamePlayer(player) || isFriend(player);
    }

    public boolean getShouldRenderGround()
    {
        return shouldRenderGround;
    }

    public ItemStack getSkull()
    {
        return skull;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }

    public void setCamoState(IBlockState state)
    {
        camoState = state;
        setShouldGroundRender();
    }

    public IBlockState getCamoState()
    {
        return camoState;
    }

    public void setShouldGroundRender()
    {
        shouldRenderGround = !(camoState == null || camoState == ModBlocks.blockGrave.getDefaultState());
    }

    public boolean areGraveItemsForced()
    {
        return GIVE_ITEMS_IN_GRAVE_PRIORITY;
    }

    public void setTimestamp(String stamp)
    {
        this.timestamp = stamp;
        markDirty();
    }

    public String getTimestamp()
    {
        return timestamp;
    }
}
