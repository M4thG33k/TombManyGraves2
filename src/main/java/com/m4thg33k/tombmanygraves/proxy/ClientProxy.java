package com.m4thg33k.tombmanygraves.proxy;

import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.events.ClientEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy{

    @Override
    public void preinit(FMLPreInitializationEvent e) {
        super.preinit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);

        ModRenders.init();
    }

    @Override
    public void postinit(FMLPostInitializationEvent e) {
        super.postinit(e);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
