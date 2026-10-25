package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * A sealed gate of the ancient ruins, unbreakable: when any of its blocks is powered (by a crystal socket) the whole
 * gate, every gate block touching it, dissolves open, and stays open.
 */
public class AncientGateBlock extends Block {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    /** The most blocks one gate can have, so a huge wall of them cannot stall the server. */
    private static final int MAX_BLOCKS = 128;

    public AncientGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AncientGlow.PROPERTY, AncientGlow.AMBER).setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AncientGlow.PROPERTY, OPEN);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean moving) {
        if (!level.isClientSide && !state.getValue(OPEN) && level.hasNeighborSignal(pos)) {
            this.openFrom(level, pos);
        }
    }

    /** Opens the whole gate, every block of it touching this one. */
    public void openFrom(Level level, BlockPos start) {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        seen.add(start);
        while (!queue.isEmpty() && seen.size() <= MAX_BLOCKS) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, state.setValue(OPEN, true), Block.UPDATE_CLIENTS);
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.01);
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                BlockState other = level.getBlockState(next);
                if (other.is(this) && !other.getValue(OPEN) && seen.add(next)) {
                    queue.add(next);
                }
            }
        }
        level.playSound(null, start, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 0.6F);
        level.playSound(null, start, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 0.7F);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? Shapes.empty() : Shapes.block();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(OPEN) ? 1.0F : super.getShadeBrightness(state, level, pos);
    }
}
