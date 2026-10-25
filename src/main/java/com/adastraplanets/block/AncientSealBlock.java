package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * A sealed gate that wants every crystal socket set into it filled before it opens: a gate for more than one key.
 * Its blocks open together, as a gate's do, once no socket touching any of them is still empty.
 */
public class AncientSealBlock extends AncientGateBlock {
    /** The most blocks one seal can have. */
    private static final int MAX_BLOCKS = 128;

    public AncientSealBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        if (!level.isClientSide && !state.getValue(OPEN) && level.hasNeighborSignal(pos) && this.allSocketsFilled(level, pos)) {
            this.openFrom(level, pos);
        }
    }

    private boolean allSocketsFilled(Level level, BlockPos start) {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        seen.add(start);
        int sockets = 0;
        while (!queue.isEmpty() && seen.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                BlockState other = level.getBlockState(next);
                if (other.getBlock() instanceof CrystalSocketBlock) {
                    sockets++;
                    if (!other.getValue(CrystalSocketBlock.FILLED)) {
                        return false;
                    }
                } else if (other.is(this) && seen.add(next)) {
                    queue.add(next);
                }
            }
        }
        return sockets > 0;
    }
}
