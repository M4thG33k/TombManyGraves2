package com.m4thg33k.tombmanygraves.proxy;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.client.fx.PathFX;
import com.m4thg33k.tombmanygraves.client.render.ItemRenderRegister;
import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.events.ClientEvents;
import com.m4thg33k.tombmanygraves.events.RenderEvents;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class ClientProxy extends CommonProxy{

    private Color NEAR;
    private Color FAR;

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

        NEAR = ModConfigs.NEAR_PARTICLE;
        FAR = ModConfigs.FAR_PARTICLE;
    }

    @Override
    public void postinit(FMLPostInitializationEvent e) {
        super.postinit(e);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new RenderEvents());
    }

    @Override
    public void particleStream(Vector3f start, Vector3f end) {
        Vector3f diff = Vector3f.sub(start, end, null);
        float length = diff.length();
        float scale = diff.length() < 5 ? 10 : diff.length();

        Vector3f motion = new Vector3f(diff.x/scale, diff.y/scale, diff.z/scale);

        float[] color = getParticleColor(length);
        TombManyGraves.proxy.pathFX(start.x, start.y, start.z, color[0]/255.0f, color[1]/255.0f, color[2]/255.0f,
                0.4f, motion.x, motion.y, motion.z, 1f);
    }

    private float[] getParticleColor(float length)
    {
        float[] ret = new float[3];
        if (length < 10)
        {
            ret[0] = NEAR.getRed();
            ret[1] = NEAR.getGreen();
            ret[2] = NEAR.getBlue();
        }
        else if (length > 100)
        {
            ret[0] = FAR.getRed();
            ret[1] = FAR.getGreen();
            ret[2] = FAR.getBlue();
        }
        else
        {
            ret[0] = intermediateValue(length, FAR.getRed(), NEAR.getRed());
            ret[1] = intermediateValue(length, FAR.getGreen(), NEAR.getGreen());
            ret[2] = intermediateValue(length, FAR.getBlue(), NEAR.getBlue());
        }

        return ret;
    }

    private int intermediateValue(float length, int far, int near)
    {
        return (int)((far-near)*length/90.0 + (10*near - far)/9.0);
    }

    @Override
    public void pathFX(double x, double y, double z, float r, float g, float b, float size,
                       float motionX, float motionY, float motionZ, float maxAge) {
        if (!ModConfigs.ALLOW_PARTICLE_PATH)
        {
            return;
        }

        PathFX path = new PathFX(Minecraft.getMinecraft().world, x, y, z, size, r, g, b, true, false, maxAge);
        path.setSpeed(motionX, motionY, motionZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(path);
    }
}
