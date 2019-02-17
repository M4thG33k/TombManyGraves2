package com.m4thg33k.tombmanygraves.events;

import com.m4thg33k.tombmanygraves.client.fx.ParticleRenderDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        Profiler profiler = Minecraft.getInstance().profiler;

        profiler.startSection("tmg_particles");
        ParticleRenderDispatcher.dispatch();
        profiler.endSection();
    }
}
