package com.m4thg33k.tombmanygraves.items;

import com.m4thg33k.tombmanygraves.lib.Names;

import net.minecraft.item.Item;

public class ItemFileControl extends Item {

    public ItemFileControl()
    {
        super();

        this.setUnlocalizedName(Names.ITEM_FILE_CONTROL);

        this.setMaxStackSize(1);
        this.setRegistryName(Names.MODID, Names.ITEM_FILE_CONTROL);
    }


//    @ParametersAreNonnullByDefault
//    @Nonnull
//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
//        if (!worldIn.isRemote)
//        {
//            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
//        }
//        playerIn.openGui(TombManyGraves.INSTANCE, ModGuiHandler.DEATH_INVENTORIES,
//                worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
//        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
//    }


}
