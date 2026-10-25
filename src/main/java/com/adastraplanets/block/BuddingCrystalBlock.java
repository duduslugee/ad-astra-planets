package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.function.Supplier;

/** Budding amethyst for one of our crystals: grows its own buds, small to medium to large to cluster. */
public class BuddingCrystalBlock extends BuddingAmethystBlock {
    private final List<Supplier<? extends Block>> stages;

    /** stages: small bud, medium bud, large bud, cluster. */
    public BuddingCrystalBlock(Properties properties, List<Supplier<? extends Block>> stages) {
        super(properties);
        this.stages = stages;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROWTH_CHANCE) != 0) {
            return;
        }
        Direction direction = Direction.getRandom(random);
        BlockPos target = pos.relative(direction);
        BlockState current = level.getBlockState(target);
        Block next = null;
        if (canClusterGrowAtState(current)) {
            next = this.stages.get(0).get();
        } else {
            for (int i = 0; i < this.stages.size() - 1; i++) {
                if (current.is(this.stages.get(i).get()) && current.getValue(AmethystClusterBlock.FACING) == direction) {
                    next = this.stages.get(i + 1).get();
                    break;
                }
            }
        }
        if (next != null) {
            level.setBlockAndUpdate(target, next.defaultBlockState()
                    .setValue(AmethystClusterBlock.FACING, direction)
                    .setValue(AmethystClusterBlock.WATERLOGGED, current.getFluidState().getType() == Fluids.WATER));
        }
    }
}
