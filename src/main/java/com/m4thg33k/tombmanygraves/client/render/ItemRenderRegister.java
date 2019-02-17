package com.m4thg33k.tombmanygraves.client.render;

import com.m4thg33k.tombmanygraves.items.ModItems;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ItemRenderRegister {

    @OnlyIn(Dist.CLIENT)
    public static void initClient(ItemModelMesher mesher)
    {
        registerSingleModel(ModItems.itemDeathList, mesher);
    }

    public static void registerSingleModel(Item item, ItemModelMesher mesher)
    {
        ModelResourceLocation model = new ModelResourceLocation(item.getRegistryName(), "inventory");
        //ModelLoader.registerItemVariants(item, model); //TODO: find replacement
        mesher.register(item,  model);
    }
}
