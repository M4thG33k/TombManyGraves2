package com.m4thg33k.tombmanygraves.blocks;

import com.m4thg33k.tombmanygraves.api.state.TMGStateProps;
import com.m4thg33k.tombmanygraves.lib.ModConfigs;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.tiles.TileGrave;
import com.m4thg33k.tombmanygraves.util.LogHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockGrave extends BaseBlock {

    public BlockGrave()
    {
        super(Names.GRAVE_BLOCK, Material.WOOD, 100.0f, 100.0f);

        this.setBlockUnbreakable();

        this.setRegistryName(Names.MODID, Names.GRAVE_BLOCK);

        this.setDefaultState(((IExtendedBlockState) blockState.getBaseState())
                .withProperty(TMGStateProps.HELD_STATE, null)
                .withProperty(TMGStateProps.HELD_WORLD, null)
                .withProperty(TMGStateProps.HELD_POS, null));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if (!worldIn.isRemote)
        {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile != null && tile instanceof TileGrave)
            {
                if (playerIn.isSneaking())
                {
                    ((TileGrave) tile).toggleLock(playerIn);
                }
                else
                {
                    ((TileGrave) tile).onRightClick(playerIn);
                }
            }
        }
        return true;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {},
                new IUnlistedProperty[] {TMGStateProps.HELD_STATE, TMGStateProps.HELD_WORLD, TMGStateProps.HELD_POS});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = ((IExtendedBlockState) state).withProperty(TMGStateProps.HELD_WORLD, world)
                .withProperty(TMGStateProps.HELD_POS, pos);

        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileGrave)
        {
            state = ((IExtendedBlockState)state).withProperty(TMGStateProps.HELD_STATE,
                    ((TileGrave) tile).getCamoState());
        }

        return state;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGrave();
    }


    @ParametersAreNonnullByDefault
    @Override
    public void addCollisionBoxToList(IBlockState state,World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes,@Nullable Entity entityIn, boolean bool) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileGrave)
        {
            if (entityIn instanceof EntityPlayer &&
                    !(((TileGrave) tile).isLocked()) &&
                    ((TileGrave) tile).hasAccess((EntityPlayer)entityIn))
            {
                super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, bool);
            }
        }
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileGrave)
        {
            if (entityIn instanceof EntityPlayer && entityIn.isEntityAlive())
            {
                if (ModConfigs.REQUIRE_SNEAKING)
                {
                    if (entityIn.isSneaking())
                    {
                        ((TileGrave) tile).onCollision((EntityPlayer) entityIn);
                    }
                }
                else
                {
                    ((TileGrave) tile).onCollision((EntityPlayer) entityIn);
                }
            }
        }
    }

    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        // TODO: 1/15/2017 set logic once TE implemented
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // TODO: 1/15/2017 implement once model is coded
        return super.getRenderType(state);
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @ParametersAreNonnullByDefault
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {

    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return 0;
    }

    @ParametersAreNonnullByDefault
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }
}
