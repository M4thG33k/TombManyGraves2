package com.m4thg33k.tombmanygraves.blocks;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BaseBlock extends Block {

	
    public BaseBlock(String name, Material material, float hardness, float resistance)
    {
        super(Block.Properties.create(material).hardnessAndResistance(hardness, resistance));
        setRegistryName(Names.MODID, name);
        
    }

    public BaseBlock(String name, float hardness, float resistance)
    {
        this(name, Material.ROCK, hardness, resistance);
    }

    public BaseBlock(String name)
    {
        this(name, 2.0f, 10.0f);
    }
}
