package com.m4thg33k.tombmanygraves.items;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModItems {

//    public static ItemFileControl itemFileControl = new ItemFileControl();
    public static ItemDeathList itemDeathList = new ItemDeathList();

    public static void createItems()
    {
//        GameRegistry.register(itemFileControl);
       ForgeRegistries.ITEMS.register(itemDeathList);
    }
}
