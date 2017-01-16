package com.m4thg33k.tombmanygraves.events;

import com.m4thg33k.tombmanygraves.client.render.models.AdaptiveGraveModel;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.util.Utility;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEvents {

    private static final ResourceLocation graveModel = Utility.getResource("block/grave");
    private static final String graveLocation = Utility.resource(Names.GRAVE_BLOCK);
    private static final ModelResourceLocation graveModelLocation =
            Utility.getModelResource(Names.GRAVE_BLOCK, "normal");

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        try
        {
            IModel model = ModelLoaderRegistry.getModel(graveModel);
            if (model instanceof IRetexturableModel)
            {
                IRetexturableModel gModel = (IRetexturableModel) model;
                IBakedModel standard = event.getModelRegistry().getObject(graveModelLocation);
                IBakedModel finalModel = new AdaptiveGraveModel(standard, gModel);
                event.getModelRegistry().putObject(graveModelLocation, finalModel);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre pre)
    {
        pre.getMap().registerSprite(new ResourceLocation(Names.MODID, "blocks/red"));
    }
}
