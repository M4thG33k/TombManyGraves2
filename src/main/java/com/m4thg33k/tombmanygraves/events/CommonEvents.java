package com.m4thg33k.tombmanygraves.events;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import com.m4thg33k.tombmanygraves.util.ChatHelper;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import com.sun.jmx.mbeanserver.MBeanInstantiator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonEvents {

    public CommonEvents()
    {

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.isCanceled() ||
                event.getEntityLiving().getEntityWorld().isRemote ||
                !(event.getEntityLiving() instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        if (ModConfigs.PRINT_DEATH_LOG)
        {
            handleDeathLog(player);
        }

        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory"))
        {
            handleGraveLogic(player);
        }
    }

    private void handleDeathLog(EntityPlayer player)
    {
        BlockPos pos = player.getPosition();
        LogHelper.info(player.getName() + " died in dimension " + player.dimension + " at " +
                pos.toString() + "." + (ModConfigs.ENABLE_GRAVES ? " Their grave may be near!" : ""));
    }

    private void handleGraveLogic(EntityPlayer player)
    {
        ChatHelper.sayMessage(player, "Place of death: " + player.getPosition().toString());
        if (ModConfigs.ENABLE_GRAVES)
        {
            // generate the grave
            IBlockState state = ModBlocks.blockGrave.getDefaultState();
            BlockPos posToPlace = new BlockPos(0, -1, 0);
            if (ModConfigs.ASCEND_LIQUID)
            {
                posToPlace = findValidGraveLocation(player.worldObj,
                        ascendFromFluid(player.worldObj, player.getPosition()));
            }
            if (posToPlace.getY() == -1)
            {
                posToPlace = findValidGraveLocation(player.worldObj, player.getPosition());
            }

            if (posToPlace.getY() == -1)
            {
                ChatHelper.sayMessage(player, "But a suitable location for the grave wasn't found.");
            }
            else
            {
                player.worldObj.setBlockState(posToPlace, state);
                TileEntity tile = player.worldObj.getTileEntity(posToPlace);
                if (tile != null && tile instanceof TileGrave)
                {
                    TileGrave grave = (TileGrave) tile;
                    grave.getPlayerData(player);

                    IBlockState camoBlock = getBlockStateBelow(player.worldObj, posToPlace);

                    if (camoBlock.getMaterial() == Material.AIR)
                    {
                        camoBlock = ModBlocks.blockGrave.getDefaultState();
                    }
                    else if (camoBlock.getMaterial() == Material.GRASS)
                    {
                        camoBlock = Blocks.DIRT.getDefaultState();
                    }

                    grave.setCamoState(camoBlock);


                }
                else
                {
                    LogHelper.info("Error! Grave could not be found after placement! " + posToPlace.toString());
                    ChatHelper.sayMessage(player, "ERROR! Grave formed incorrectly! Report to M4thG33k!");
                }
            }
        }
        else
        {
            // only create the death inventory list
        }
    }

    private BlockPos findValidGraveLocation(World world, BlockPos pos)
    {
        BlockPos toReturn = new BlockPos(-1, -1, -1);
        BlockPos toCheck = pos.add(0, 0, 0);

        if (toCheck.getY() <= 0)
        {
            toCheck = toCheck.add(0,
                    MathHelper.abs_int(toCheck.getY()) +
                            (ModConfigs.START_VOID_SEARCH_AT_ONE ? 1 : ModConfigs.MAX_GRAVE_SEARCH_RADIUS),
                    0);
        }

        for (int r = 0; r <= ModConfigs.MAX_GRAVE_SEARCH_RADIUS; r++)
        {
            toReturn = checkLevel(world, toCheck, r, false);
            if (toReturn.getY() != -1)
            {
                return toReturn;
            }
        }
        return toReturn;
    }

    private BlockPos ascendFromFluid(World world, BlockPos pos)
    {
        BlockPos toCheck = pos.add(0, 0, 0);
        int height = 0;
        while (pos.getY()+height < world.getActualHeight() - ModConfigs.MAX_GRAVE_SEARCH_RADIUS &&
                !isValidLocation(world, toCheck, true))
        {
            int temp = pos.getY() + height;
            int temp2 = world.getActualHeight();
            toCheck = checkLevel(world, pos.add(0, height, 0), 1, true);
            height += 1;
        }

        return toCheck;
    }

    private BlockPos checkLevel(World world, BlockPos pos, int radius, boolean ignoreFluidConfigs)
    {
        if (radius == 0 && isValidLocation(world, pos, ignoreFluidConfigs))
        {
            return pos;
        }

        for (int i = -radius; i <= radius; i++)
        {
            for (int j = -radius; j <= radius; j++)
            {
                for (int k = radius; k >= -radius; k--)
                {
                    if (MathHelper.abs_int(i) == radius ||
                            MathHelper.abs_int(j) == radius ||
                            MathHelper.abs_int(k) == radius)
                    {
                        if (isValidLocation(world, pos.add(i,j,k), ignoreFluidConfigs))
                        {
                            return pos.add(i,j,k);
                        }
                    }
                }
            }
        }

        return new BlockPos(-1, -1, -1);
    }

    private boolean isValidLocation(World world, BlockPos pos, boolean ignoreFluidConfigs)
    {
        if (pos.getY() < 0 || pos.getY() >= world.getActualHeight())
        {
            return false;
        }
        if (world.isAirBlock(pos))
        {
            return true;
        }

        Block theBlock = world.getBlockState(pos).getBlock();
        if (ModConfigs.REPLACE_PLANTS && theBlock instanceof IPlantable)
        {
            return true;
        }

        if (!ignoreFluidConfigs)
        {
            if ((ModConfigs.REPLACE_STILL_LAVA && theBlock == Blocks.LAVA) ||
                    (ModConfigs.REPLACE_FLOWING_LAVA && theBlock == Blocks.FLOWING_LAVA) ||
                    (ModConfigs.REPLACE_STILL_WATER && theBlock == Blocks.WATER) ||
                    (ModConfigs.REPLACE_FLOWING_WATER && theBlock == Blocks.FLOWING_WATER))
            {
                return true;
            }
        }

        return false;
    }

    private IBlockState getBlockStateBelow(World world, BlockPos pos)
    {
        return world.getBlockState(pos.add(0, -1, 0));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void itemToss(ItemTossEvent event)
    {
        Item item = event.getEntityItem().getEntityItem().getItem();
        if (item == Item.getItemFromBlock(ModBlocks.blockGrave)) // TODO: 1/15/2017 add death inventory to list
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void itemDrop(PlayerDropsEvent event)
    {
        for (EntityItem entityItem : event.getDrops())
        {
            if (entityItem.getEntityItem().getItem() == Item.getItemFromBlock(ModBlocks.blockGrave)) /// TODO: 1/15/2017 add death inventory check
            {
                entityItem.setDead();
            }
        }
    }

    // TODO: 1/15/2017 add death inventory list spawning
}
