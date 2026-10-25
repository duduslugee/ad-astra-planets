package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A rune of the ancients' sequence locks: touched, it lights if it is the next one of its lock's sequence (its order,
 * 1 to 6; 0 for a rune that belongs to no sequence); a wrong touch puts every rune of the lock out and shocks the one
 * who touched it. When the last rune of the sequence lights, every sealed gate near the lock opens.
 *
 * <p>A lock is every rune within {@link #REACH} blocks; the order is part of the block state, so a structure (or a
 * datapack's) decides it, and its clues are written wherever the structure likes.
 */
public class GlyphRuneBlock extends Block {
    public enum Glyph implements StringRepresentable {
        SUN, RING, MOON, STAR, WAVE, EYE;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static final EnumProperty<Glyph> GLYPH = EnumProperty.create("glyph", Glyph.class);
    public static final IntegerProperty ORDER = IntegerProperty.create("order", 0, 6);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    /** How far a lock's runes, and the gates it opens, may be from the rune touched. */
    public static final int REACH = 20;
    /** How far from the lock the gates it opens may be. */
    private static final int GATE_REACH = 24;

    public GlyphRuneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(GLYPH, Glyph.SUN).setValue(ORDER, 0).setValue(LIT, false)
                .setValue(AncientGlow.PROPERTY, AncientGlow.HALO));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GLYPH, ORDER, LIT, AncientGlow.PROPERTY);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(LIT)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        List<BlockPos> runes = new ArrayList<>();
        for (BlockPos other : BlockPos.betweenClosed(pos.offset(-REACH, -REACH, -REACH), pos.offset(REACH, REACH, REACH))) {
            if (level.getBlockState(other).is(this)) {
                runes.add(other.immutable());
            }
        }
        int length = 0;
        int lit = 0;
        for (BlockPos rune : runes) {
            BlockState other = level.getBlockState(rune);
            if (other.getValue(ORDER) > 0) {
                length++;
                if (other.getValue(LIT)) {
                    lit++;
                }
            }
        }
        int order = state.getValue(ORDER);
        if (order > 0 && order == lit + 1) {
            level.setBlockAndUpdate(pos, state.setValue(LIT, true));
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5F, 0.6F + 0.15F * order);
            if (order == length) {
                this.complete(level, pos);
            }
        } else {
            for (BlockPos rune : runes) {
                BlockState other = level.getBlockState(rune);
                if (other.getValue(LIT)) {
                    level.setBlockAndUpdate(rune, other.setValue(LIT, false));
                }
            }
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 0.5F);
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.4, 0.4, 0.4, 0.2);
            }
            player.hurt(level.damageSources().magic(), 3.0F);
        }
        return InteractionResult.CONSUME;
    }

    /** Opens every sealed gate (not seals: they keep wanting their sockets) near the lock. */
    private void complete(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 2.0F, 1.2F);
        for (BlockPos other : BlockPos.betweenClosed(pos.offset(-GATE_REACH, -GATE_REACH, -GATE_REACH), pos.offset(GATE_REACH, GATE_REACH, GATE_REACH))) {
            BlockState state = level.getBlockState(other);
            if (state.getBlock() instanceof AncientGateBlock gate && !(gate instanceof AncientSealBlock) && !state.getValue(AncientGateBlock.OPEN)) {
                gate.openFrom(level, other.immutable());
            }
        }
    }
}
