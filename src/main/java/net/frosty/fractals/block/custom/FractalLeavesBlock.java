package net.frosty.fractals.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FractalLeavesBlock extends LeavesBlock {
    public FractalLeavesBlock(float leafParticleChance, Settings settings) {
        super(leafParticleChance, settings.nonOpaque());
    }

    @Override
    protected int getOpacity(BlockState state) {
        return 0;
    }

    @Override
    public MapCodec<? extends LeavesBlock> getCodec() {
        return null;
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    protected void spawnLeafParticle(World world, BlockPos pos, Random random) {

    }
}
