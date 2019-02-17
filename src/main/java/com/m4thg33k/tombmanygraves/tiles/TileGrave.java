package com.m4thg33k.tombmanygraves.tiles;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.m4thg33k.tombmanygraves.ModConfigs;
import com.m4thg33k.tombmanygraves.blocks.BlockGrave;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.friends.FriendHandler;
import com.m4thg33k.tombmanygraves.invman.InventoryHolder;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.util.ChatHelper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileGrave extends TileEntity {

	public TileGrave() {
		super(ModTiles.GRAVE_TYPE);
	}

	// static final Strings for use as NBT tags (keep it consistent yo!)
	private static final String TAG_CAMO = "camo";
	private static final String SKULL_TAG = "SkullOwner";
	public static final String PLAYER_NAME = "PlayerName";
	public static final String ANGLE_OF_DEATH = "AngleOfDeath";
	public static final String LOCKED = "IsLocked";
	public static final String PLAYER_UUID = "PlayerID";
	public static final String GRAVE_PRIORITY = "GravePriority";

	private boolean GIVE_ITEMS_IN_GRAVE_PRIORITY = ModConfigs.GIVE_ITEMS_IN_GRAVE_PRIORITY;

	private boolean locked = ModConfigs.DEFAULT_TO_LOCKED;

	// default to Tiffit's ID to keep away complaints from dirty pirates
	private String playerName = "Tiffit";
	private UUID playerID = UUID.fromString("01977d39-7106-449d-bf9e-e3497ca7c557");

	private int angle = 0;
	private boolean shouldRenderGround = false;
	private ItemStack skull = null;

	private IBlockState camoState;
	private String timestamp = "";

	private InventoryHolder savedInventory = new InventoryHolder();

	private void setPlayerName(@Nonnull String name) {
		playerName = name;
		this.setSkull();
	}

	public String getPlayerName() {
		return playerName;
	}

	private void setPlayerID(UUID id) {
		this.playerID = id;
	}

	public void setPlayer(EntityPlayer player) {
		this.setPlayerName(player.getName().getFormattedText());
		this.setPlayerID(player.getUniqueID());
		this.markDirty();
		world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
	}

	public void getPlayerData(@Nonnull EntityPlayer player) {
		angle = (int) player.rotationYawHead;

		if (world.isRemote) {
			return;
		}

		setPlayerName(player.getName().getFormattedText());
		setPlayerID(player.getUniqueID());

		this.markDirty();
		world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);

	}

	public InventoryHolder getSavedInventory() {
		return savedInventory;
	}

	public void setSavedInventory(InventoryHolder holder) {
		this.savedInventory = holder;
		this.timestamp = holder.getTimestamp();
	}

	private void setSkull() {
		skull = new ItemStack(Items.PLAYER_HEAD);
		NBTTagCompound compound = new NBTTagCompound();
		compound.put(SKULL_TAG, new NBTTagString(playerName));
		skull.setTag(compound);
	}

	public void onRightClick(@Nonnull EntityPlayer player) {
		if (this.hasAccess(player)) {
			this.toggleGravePriority();
			ChatHelper.sayMessage(player, GIVE_ITEMS_IN_GRAVE_PRIORITY ? "Grave items will be forced into their original slots" : "Items in your inventory will not move.");
		} else {
			ChatHelper.sayMessage(player, "You do not have permission to interact with this grave.");
		}
	}

	public boolean isSamePlayer(EntityPlayer player) {
		return ModConfigs.ALLOW_GRAVE_ROBBING || player.getUniqueID().equals(playerID);
	}

	@Override
	public void read(NBTTagCompound compound) {
		super.read(compound);

		playerName = compound.getString(PLAYER_NAME);
		this.setSkull();
		angle = compound.getInt(ANGLE_OF_DEATH);
		locked = compound.getBoolean(LOCKED);
		playerID = compound.getUniqueId(PLAYER_UUID);
		GIVE_ITEMS_IN_GRAVE_PRIORITY = compound.getBoolean(GRAVE_PRIORITY);
		camoState = Block.getStateById(compound.getInt(TAG_CAMO));

		savedInventory = new InventoryHolder();
		savedInventory.readFromNBT(compound);

		timestamp = compound.getString(InventoryHolder.TIMESTAMP);

		setShouldGroundRender();
	}

	@Nonnull
	@Override
	public NBTTagCompound write(NBTTagCompound compound) {
		super.write(compound);

		compound.putString(PLAYER_NAME, playerName);
		compound.putInt(ANGLE_OF_DEATH, angle);
		compound.putBoolean(LOCKED, locked);
		if (playerID != null) {
			compound.putUniqueId(PLAYER_UUID, playerID);
		}

		compound.putBoolean(GRAVE_PRIORITY, GIVE_ITEMS_IN_GRAVE_PRIORITY);

		if (camoState != null) {
			compound.putInt(TAG_CAMO, Block.getStateId(camoState));
		}

		compound = savedInventory.writeToNBT(compound);
		compound.putString(InventoryHolder.TIMESTAMP, timestamp);

		return compound;
	}

	private void toggleGravePriority() {
		GIVE_ITEMS_IN_GRAVE_PRIORITY = !GIVE_ITEMS_IN_GRAVE_PRIORITY;
		markDirty();
		world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
	}

	public void onCollision(EntityPlayer player) {
		if (world.isRemote || locked || !(hasAccess(player))) {
			return;
		}

		removeCorrespondingDeathList(player);

		if (GIVE_ITEMS_IN_GRAVE_PRIORITY) {
			savedInventory.forceInventory(player);
		} else {
			savedInventory.insertInventory(player);
		}
		IBlockState state = world.getBlockState(pos);
		if(state.get(BlockGrave.WATERLOGGED)){
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		}else{
			world.removeBlock(pos);
		}
	}

	public void dropGraveContentsAt(World worldIn, BlockPos posIn) {
		if (worldIn.isRemote) {
			return;
		}

		savedInventory.dropInventory(worldIn, posIn);
		IBlockState state = world.getBlockState(pos);
		if(state.get(BlockGrave.WATERLOGGED)){
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		}else{
			world.removeBlock(pos);
		}
	}

	public void dropGraveContentsHere() {
		dropGraveContentsAt(world, pos);
	}

	private void removeCorrespondingDeathList(EntityPlayer player) {
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == ModItems.itemDeathList) {
				NBTTagCompound tagCompound = stack.getOrCreateTag();
				if (tagCompound != null && tagCompound.contains(InventoryHolder.TAG_NAME)) {
					NBTTagCompound tag = tagCompound.getCompound(InventoryHolder.TAG_NAME);
					if (!tag.contains(InventoryHolder.TIMESTAMP)) {
						continue;
					}
					String time = tag.getString(InventoryHolder.TIMESTAMP);
					if (time.equals(this.timestamp)) {
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
		angle = pkt.getNbtCompound().getInt(ANGLE_OF_DEATH);
		locked = pkt.getNbtCompound().getBoolean(LOCKED);
		playerID = pkt.getNbtCompound().getUniqueId(PLAYER_UUID);
		GIVE_ITEMS_IN_GRAVE_PRIORITY = pkt.getNbtCompound().getBoolean(GRAVE_PRIORITY);
		camoState = Block.getStateById(pkt.getNbtCompound().getInt(TAG_CAMO));
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
		this.write(compound);
		compound.remove(InventoryHolder.TAG_NAME);
		return compound;
	}

	@ParametersAreNonnullByDefault
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.read(tag);
	}

	public int getAngle() {
		return angle;
	}

	public boolean isLocked() {
		return locked;
	}

	public void toggleLock(EntityPlayer player) {
		if (world.isRemote) {
			return;
		}

		if (hasAccess(player)) {
			locked = !locked;
			if (ModConfigs.ALLOW_LOCKING_MESSAGES) {
				ChatHelper.sayMessage(player, "The grave is now " + (locked ? "" : "un") + "locked!");
			}

			markDirty();
			world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 2);
		} else {
			ChatHelper.sayMessage(player, "You do not have permission to modify this grave.");
		}
	}

	public boolean isFriend(EntityPlayer player) {
		return FriendHandler.hasAsFriend(playerID, player.getUniqueID());
	}

	public boolean hasAccess(EntityPlayer player) {
		return ModConfigs.ALLOW_GRAVE_ROBBING || isSamePlayer(player) || isFriend(player);
	}

	public boolean getShouldRenderGround() {
		return shouldRenderGround;
	}

	public ItemStack getSkull() {
		return skull;
	}

	public void setCamoState(IBlockState state) {
		camoState = state;
		setShouldGroundRender();
	}

	public IBlockState getCamoState() {
		return camoState;
	}

	public void setShouldGroundRender() {
		shouldRenderGround = !(camoState == null || camoState == ModBlocks.blockGrave.getDefaultState());
	}

	public boolean areGraveItemsForced() {
		return GIVE_ITEMS_IN_GRAVE_PRIORITY;
	}

	public void setTimestamp(String stamp) {
		this.timestamp = stamp;
		markDirty();
	}

	public String getTimestamp() {
		return timestamp;
	}
}
