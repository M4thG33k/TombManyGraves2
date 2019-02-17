package com.m4thg33k.tombmanygraves.events;

import com.m4thg33k.tombmanygraves.Names;
import com.m4thg33k.tombmanygraves.blocks.BlockGrave;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.items.ItemDeathList;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.tiles.ModTiles;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Names.MODID, bus = Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void itemRegistry(RegistryEvent.Register<Item> e) {
    	e.getRegistry().register(ModItems.itemDeathList = new ItemDeathList());
    }
    
    @SubscribeEvent
    public static void blockRegistry(RegistryEvent.Register<Block> e) {
    	e.getRegistry().register(ModBlocks.blockGrave = new BlockGrave());
    }
    
    @SubscribeEvent
    public static void tileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> e) {
    	ModTiles.GRAVE_TYPE = TileEntityType.register(Names.MODID + ":" + Names.GRAVE_BLOCK, TileEntityType.Builder.create(TileGrave::new));
    }
	
}
