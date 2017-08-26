package com.m4thg33k.tombmanygraves.blocks.itemblocks;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.m4thg33k.tombmanygraves.lib.Names;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GraveItemBlock extends ItemBlock{

    public GraveItemBlock(Block block)
    {
        super(block);
        this.setMaxDamage(0);

        this.setRegistryName(Names.MODID, Names.GRAVE_BLOCK);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer stack, World playerIn,@Nullable BlockPos worldIn,
                                      @Nullable EnumHand pos,@Nullable EnumFacing hand,
                                      float facing, float hitX, float hitY) {
        return EnumActionResult.FAIL;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (tooltip == null){
            return;
        }
        tooltip.add(TextFormatting.RED + "You should not have this block. Unless you cheated it in.");
        tooltip.add(TextFormatting.RED + "Please report this to M4thG33k otherwise.");
        tooltip.add(TextFormatting.ITALIC + "Cannot be placed in the world!");
        tooltip.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + "Throw from your inventory to delete.");
    }
}
