package com.m4thg33k.tombmanygraves.util;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class Utility {

    public static final String RESOURCE = Names.MODID.toLowerCase();

    public static String resource(String res)
    {
        return String.format("%s:%s", RESOURCE, res);
    }

    public static ResourceLocation getResource(String res)
    {
        return new ResourceLocation(RESOURCE, res);
    }

    public static ModelResourceLocation getModelResource(String res, String variant)
    {
        return new ModelResourceLocation(resource(res), variant);
    }
}
