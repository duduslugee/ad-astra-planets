package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A pedestal of the ancient ruins: set the right crystal shard in it (its glow's socket keys, an item tag) and it
 * lights up for good and powers the blocks around it, opening the sealed gates it is wired to.
 */
public class CrystalSocketBlock extends Block {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public CrystalSocketBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AncientGlow.PROPERTY, AncientGlow.AMBER).setValue(FILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AncientGlow.PROPERTY, FILLED);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(FILLED) || !stack.is(this.keys(state))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            stack.consume(1, player);
            level.setBlockAndUpdate(pos, state.setValue(FILLED, true));
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.6F, 1.4F);
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 16, 0.2, 0.3, 0.2, 0.02);
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /** The items this socket accepts. */
    protected net.minecraft.tags.TagKey<net.minecraft.world.item.Item> keys(BlockState state) {
        return state.getValue(AncientGlow.PROPERTY).socketKeys();
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(FILLED) ? 15 : 0;
    }
}
