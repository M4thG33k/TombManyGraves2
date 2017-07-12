package com.m4thg33k.tombmanygraves.client.render.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.m4thg33k.tombmanygraves.api.state.TMGStateProps;
import com.m4thg33k.tombmanygraves.blocks.BlockGrave;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class AdaptiveGraveModel implements IBakedModel {

    private final IBakedModel standard;
    private final IModel retexturableModel;
    private static final IBlockState dirtState = Blocks.DIRT.getDefaultState();

    private final Map<String, IBakedModel> model_cache = Maps.newHashMap();

    public AdaptiveGraveModel(IBakedModel standard, IModel retexturableModel)
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

        if (ModConfigs.FORCE_DIRT_RENDER ||
                heldState.getBlock() == Blocks.AIR ||
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

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE; // is this really what I want?
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Names.BLOCK_PATH+Names.GRAVE_BLOCK);
    }

    @Nonnull
    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    private static class FakeBlockAccess implements IBlockAccess
    {
        private final IBlockAccess compose;

        private FakeBlockAccess(IBlockAccess compose)
        {
            this.compose = compose;
        }

        @ParametersAreNonnullByDefault
        @Nullable
        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return compose.getTileEntity(pos);
        }

        @ParametersAreNonnullByDefault
        @Override
        public int getCombinedLight(BlockPos pos, int lightValue) {
            return 15 << 20 | 15 << 4;
        }

        @ParametersAreNonnullByDefault
        @Nonnull
        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = compose.getBlockState(pos);
            if (state.getBlock() instanceof BlockGrave)
            {
                TileEntity tile = compose.getTileEntity(pos);
                if (tile != null && tile instanceof TileGrave)
                {
                    state = ((TileGrave) tile).getCamoState();
                }
                else
                {
                    state = null;
                }
            }

            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @ParametersAreNonnullByDefault
        @Override
        public boolean isAirBlock(BlockPos pos) {
            return compose.isAirBlock(pos);
        }

        @Nonnull
        @ParametersAreNonnullByDefault
        @Override
        public Biome getBiome(BlockPos pos) {
            return compose.getBiome(pos);
        }

        @ParametersAreNonnullByDefault
        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction) {
            return compose.getStrongPower(pos, direction);
        }

        @Nonnull
        @Override
        public WorldType getWorldType() {
            return compose.getWorldType();
        }

        @ParametersAreNonnullByDefault
        @Override
        public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
            return compose.isSideSolid(pos, side, _default);
        }
    }
}
