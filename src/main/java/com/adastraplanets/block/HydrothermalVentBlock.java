package com.adastraplanets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

/**
 * The glowing mouth of a hydrothermal chimney on the sea floor: streams bubbles (or smoke out of water) and burns
 * whatever stands on it, like a magma block.
 */
public class HydrothermalVentBlock extends Block {
    public HydrothermalVentBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isSteppingCarefully() && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().hotFloor(), 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        boolean underwater = level.getFluidState(above).is(Fluids.WATER);
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        for (int i = 0; i < 3; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.4;
            double dz = (random.nextDouble() - 0.5) * 0.4;
            if (underwater) {
                level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + dx, y, z + dz, 0, 0.04, 0);
            } else {
                level.addParticle(ParticleTypes.LARGE_SMOKE, x + dx, y, z + dz, 0, 0.05, 0);
            }
        }
        if (underwater && random.nextInt(6) == 0) {
            level.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.4F, 0.7F + random.nextFloat() * 0.2F, false);
        }
    }
}
