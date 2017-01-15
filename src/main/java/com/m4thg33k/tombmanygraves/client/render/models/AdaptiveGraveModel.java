package com.m4thg33k.tombmanygraves.client.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.m4thg33k.tombmanygraves.api.state.TMGStateProps;
import com.m4thg33k.tombmanygraves.blocks.BlockGrave;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class AdaptiveGraveModel implements IBakedModel {

    private final IBakedModel standard;
    private final IRetexturableModel retexturableModel;
    private static final IBlockState dirtState = Blocks.DIRT.getDefaultState();

    private final Map<String, IBakedModel> model_cache = Maps.newHashMap();

    public AdaptiveGraveModel(IBakedModel standard, IRetexturableModel retexturableModel)
    {
        this.standard = standard;
        this.retexturableModel = retexturableModel;
    }

    protected IBakedModel getActualModel(String texture)
    {
        IBakedModel bakedModel = standard;

        if (texture != null)
        {
            if (model_cache.containsKey(texture))
            {
                bakedModel = model_cache.get(texture);
            }
            else if (retexturableModel != null)
            {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                builder.put("bottom", texture);
                builder.put("top", texture);
                builder.put("side", texture);
                IModel retexturedModel = retexturableModel.retexture(builder.build());
                bakedModel = retexturedModel.bake(TRSRTransformation.identity(),
                        Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
                model_cache.put(texture, bakedModel);
            }
        }

        return bakedModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state == null)
        {
            return standard.getQuads(null, side, rand);
        }

        if (state.getBlock() != ModBlocks.blockGrave)
        {
            return standard.getQuads(state, side, rand);
        }

        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

        IBlockState heldState = ((IExtendedBlockState) state).getValue(TMGStateProps.HELD_STATE);
        IBlockAccess heldWorld = ((IExtendedBlockState) state).getValue(TMGStateProps.HELD_WORLD);
        BlockPos heldPos = ((IExtendedBlockState) state).getValue(TMGStateProps.HELD_POS);

        if (heldWorld == null || heldPos == null)
        {
            return ImmutableList.of();
        }

        if (heldState == null || heldState.getBlock() instanceof BlockGrave)
        {
            return ImmutableList.of();
        }

        if (heldState.getBlock() == Blocks.AIR || //// TODO: 1/15/2017 add forced dirt check to front
                !heldState.getBlock().canRenderInLayer(heldState, layer) ||
                heldState.getBlock().hasTileEntity(heldState))
        {
            return getActualModel(ModelHelper.getTextureFromBlockstate(dirtState).getIconName())
                    .getQuads(dirtState, side, rand);
        }

        IBlockState actualState = heldState.getBlock()
                .getActualState(heldState, new FakeBlockAccess(heldWorld), heldPos);

        return getActualModel(ModelHelper.getTextureFromBlockstate(actualState).getIconName())
                .getQuads(actualState, side, rand);

    }

    // TODO: 1/15/2017 add the rest of the methods from the old code 
}
