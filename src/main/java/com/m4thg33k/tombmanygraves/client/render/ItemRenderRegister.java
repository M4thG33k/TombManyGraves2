package com.m4thg33k.tombmanygraves.client.render;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.items.ModItems;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemRenderRegister {

    @SideOnly(Side.CLIENT)
    public static void initClient(ItemModelMesher mesher)
    {
        registerSingleModel(ModItems.itemDeathList, mesher);

        registerSingleModel(Item.getItemFromBlock(ModBlocks.blockGrave), mesher);
    }

    public static void registerSingleModel(Item item, ItemModelMesher mesher)
    {
        ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");

        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, 0, model);
    }
}
