package com.m4thg33k.tombmanygraves.api.state;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class TMGStateProps {
	public static final PropertyObject<IBlockState> HELD_STATE = new PropertyObject<>("held_state", IBlockState.class);

	public static final PropertyObject<IBlockReader> HELD_WORLD = new PropertyObject<>("held_world", IBlockReader.class);

	public static final PropertyObject<BlockPos> HELD_POS = new PropertyObject<>("held_pos", BlockPos.class);
}
