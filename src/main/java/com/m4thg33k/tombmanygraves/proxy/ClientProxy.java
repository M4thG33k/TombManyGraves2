package com.m4thg33k.tombmanygraves.proxy;

import com.m4thg33k.tombmanygraves.client.render.ItemRenderRegister;
import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.events.ClientEvents;
import com.m4thg33k.tombmanygraves.events.RenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
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
//        ItemRenderRegister.registerItemRenderers();
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        ItemRenderRegister.initClient(mesher);
    }

    @Override
    public void postinit(FMLPostInitializationEvent e) {
        super.postinit(e);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
    }
}
