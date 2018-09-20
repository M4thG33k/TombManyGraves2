package com.m4thg33k.tombmanygraves.client.fx;

import java.util.ArrayDeque;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import com.m4thg33k.tombmanygraves.Names;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public class PathFX extends Particle {

    public static final ResourceLocation particles = new ResourceLocation(Names.MODID + ":textures/misc/path_particle.png");

    private static final Queue<PathFX> queuedRenders = new ArrayDeque<>();
    private static final Queue<PathFX> queuedDepthIgnoringRenders = new ArrayDeque<>();

    private float f;
    private float f1;
    private float f2;
    private float f3;
    private float f4;
    private float f5;

    private boolean depthTest = true;
    public boolean distanceLimit = true;
    private final float moteParticleScale;
    private final int moteHalfLife;

    public PathFX(World world, double x, double y, double z, float size, float red, float green, float blue,
                  boolean distanceLimit, boolean depthTest, float maxAge)
    {
        super(world, x, y, z, 0, 0, 0);
        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        particleAlpha = 0.5f;
        particleGravity = 0;
        motionX = motionY = motionZ = 0;
        particleScale *= size;
        moteParticleScale = particleScale;
        particleMaxAge = (int)(28 / (Math.random() * 0.3 + 0.7) * maxAge);
        this.depthTest = depthTest;

        moteHalfLife = particleMaxAge / 2;
        setSize(0.1f, 0.1f);
        Entity renderEntity = FMLClientHandler.instance().getClient().getRenderViewEntity();

        if (distanceLimit)
        {
            int visibleDistance = 50;
            if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics)
            {
                visibleDistance = 25;
            }

            if (renderEntity == null || renderEntity.getDistance(posX, posY, posZ) > visibleDistance)
            {
                particleMaxAge = 0;
            }
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

    public static void dispatchQueuedRenders(Tessellator tessellator)
    {
        ParticleRenderDispatcher.pathFXCount = 0;
        ParticleRenderDispatcher.depthIgnoringPathFXCount = 0;

        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.75f);
        Minecraft.getMinecraft().renderEngine.bindTexture(particles);

        if (!queuedRenders.isEmpty())
        {
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            for (PathFX path : queuedRenders)
            {
                path.renderQueued(tessellator, true);
            }
            tessellator.draw();
        }

        if (!queuedDepthIgnoringRenders.isEmpty())
        {
            GlStateManager.disableDepth();
            tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            for (PathFX path : queuedDepthIgnoringRenders)
            {
                path.renderQueued(tessellator, false);
            }
            tessellator.draw();
            GlStateManager.enableBlend();
        }

        queuedRenders.clear();
        queuedDepthIgnoringRenders.clear();
    }

    private void renderQueued(Tessellator tessellator, boolean depthEnabled)
    {
        if (depthEnabled)
        {
            ParticleRenderDispatcher.pathFXCount++;
        }
        else
        {
            ParticleRenderDispatcher.depthIgnoringPathFXCount++;
        }

        float ageScale = (float)particleAge / (float) moteHalfLife;

        if (ageScale > 1)
        {
            ageScale = 2- ageScale;
        }

        particleScale = moteParticleScale * ageScale;

        float f10 = 0.5f * particleScale;
        float f11 = (float)(prevPosX + (posX-prevPosX) * f - interpPosX);
        float f12 = (float)(prevPosY + (posY-prevPosY) * f - interpPosY);
        float f13 = (float)(prevPosZ + (posZ-prevPosZ) * f - interpPosZ);
        int combined = 15 << 20 | 15 << 4;
        int k3 = combined >> 16 & 0xFFFF;
        int l3 = combined & 0xFFFF;
        tessellator.getBuffer().pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5).tex(0,1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5f).endVertex();
        tessellator.getBuffer().pos(f11 - f1 * f10 + f4 * f10, f12 + f2 * f10, f13 - f3 * f10 + f5 * f10).tex(1, 1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
        tessellator.getBuffer().pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(1, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
        tessellator.getBuffer().pos(f11 + f1 * f10 - f4 * f10, f12 - f2 * f10, f13 + f3 * f10 - f5 * f10).tex(0, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.f = partialTicks;
        this.f1 = rotationX;
        this.f2 = rotationZ;
        this.f3 = rotationYZ;
        this.f4 = rotationXY;
        this.f5 = rotationXZ;

        if (depthTest)
        {
            queuedRenders.add(this);
        }
        else
        {
            queuedDepthIgnoringRenders.add(this);
        }
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge)
        {
            setExpired();
        }

        motionY -= -0.4d * particleGravity;
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        double speed = Math.sqrt(motionX*motionX + motionY*motionY + motionZ*motionZ);
        double factor = speed < 0.5 ? 0.75 : 0.95;
        motionX *= factor;
        motionY *= factor;
        motionZ *= factor;
    }

    public void setGravity(float value)
    {
        particleGravity = value;
    }

    public void setSpeed(float mx, float my, float mz)
    {
        motionX = mx;
        motionY = my;
        motionZ = mz;
    }
}
