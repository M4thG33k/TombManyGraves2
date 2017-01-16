package com.m4thg33k.tombmanygraves.tiles;

import com.m4thg33k.tombmanygraves.blocks.BlockGrave;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class TileGrave extends TileEntity {
    private static boolean DROP_ITEMS = ModConfigs.DROP_ITEMS_ON_GROUND;

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

    public void getPlayerData(@Nonnull EntityPlayer player)
    {
        angle = (int) player.rotationYawHead;

        if (worldObj.isRemote)
        {
            return;
        }

        setPlayerName(player.getName());
        setPlayerID(player.getUniqueID());

        this.markDirty();
        worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 1);

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
                            "Your current inventory will not be altered.");
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

        return compound;
    }

    private void toggleGravePriority()
    {
        GIVE_ITEMS_IN_GRAVE_PRIORITY = !GIVE_ITEMS_IN_GRAVE_PRIORITY;
        markDirty();
        worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
    }

    public void onCollision(EntityPlayer player)
    {
        if (worldObj.isRemote || locked || !(hasAccess(player)))
        {
            return;
        }

        // TODO: 1/15/2017 Add logic to return items to the player
        worldObj.setBlockToAir(pos);
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
        return this.writeToNBT(new NBTTagCompound());
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
        if (worldObj.isRemote)
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
            worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
        }
        else
        {
            ChatHelper.sayMessage(player, "You do not have permission to modify this grave.");
        }
    }

    public boolean isFriend(EntityPlayer player)
    {
        // TODO: 1/15/2017 Implement friend interactions
        return true;
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
        shouldRenderGround = !(camoState == null || camoState.getBlock() instanceof BlockGrave);
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
