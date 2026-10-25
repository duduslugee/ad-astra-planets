package com.adastraplanets.block.entity;

import com.adastraplanets.registry.ModBlockEntityTypes;
import com.adastraplanets.registry.ModDataMaps;
import com.adastraplanets.world.GeyserSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Drives a geyser: while it erupts, entities in the plume are pushed up (server) and a column of vapour rises
 * (client). Timing comes from the block's {@link GeyserSettings} and the game time, so nothing is stored or synced.
 */
public class GeyserBlockEntity extends BlockEntity {
    public GeyserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.GEYSER.get(), pos, state);
    }

    @Nullable
    private static GeyserSettings settings(BlockState state) {
        return state.getBlockHolder().getData(ModDataMaps.GEYSERS);
    }

    /** How far the plume rises before a block stops it. */
    private static int plumeHeight(Level level, BlockPos pos, GeyserSettings settings) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int i = 1; i <= settings.height(); i++) {
            cursor.move(0, 1, 0);
            if (level.getBlockState(cursor).isCollisionShapeFullBlock(level, cursor)) {
                return i - 1;
            }
        }
        return settings.height();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeyserBlockEntity geyser) {
        GeyserSettings settings = settings(state);
        if (settings == null || !settings.erupting(level.getGameTime(), pos)) {
            return;
        }
        if (settings.cycleTick(level.getGameTime(), pos) == 0) {
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.2F, 0.5F);
        }
        int height = plumeHeight(level, pos, settings);
        if (height <= 0) {
            return;
        }
        AABB plume = new AABB(pos.getX() + 0.1, pos.getY() + 1, pos.getZ() + 0.1, pos.getX() + 0.9, pos.getY() + 1 + height, pos.getZ() + 0.9);
        for (Entity entity : level.getEntities((Entity) null, plume, entity -> !entity.isSpectator() && !entity.isPassenger())) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < settings.strength()) {
                entity.setDeltaMovement(motion.x, settings.strength(), motion.z);
            }
            entity.resetFallDistance();
            if (entity instanceof ServerPlayer) {
                // players move themselves on the client; this sends them the new velocity
                entity.hurtMarked = true;
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, GeyserBlockEntity geyser) {
        GeyserSettings settings = settings(state);
        if (settings == null) {
            return;
        }
        RandomSource random = level.getRandom();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        if (settings.erupting(level.getGameTime(), pos)) {
            // particle speed is matched to the plume height: clouds slow down by about 4% a tick
            double speed = 0.1 + plumeHeight(level, pos, settings) * 0.045;
            for (int i = 0; i < 4; i++) {
                level.addAlwaysVisibleParticle(settings.particle(), true, x + (random.nextDouble() - 0.5) * 0.5, y,
                        z + (random.nextDouble() - 0.5) * 0.5, (random.nextDouble() - 0.5) * 0.05, speed * (0.8 + random.nextDouble() * 0.4),
                        (random.nextDouble() - 0.5) * 0.05);
            }
            if (random.nextInt(2) == 0) {
                level.addParticle(settings.spray(), x + (random.nextDouble() - 0.5) * 0.8, y + random.nextDouble() * 2,
                        z + (random.nextDouble() - 0.5) * 0.8, (random.nextDouble() - 0.5) * 0.2, speed * 0.6, (random.nextDouble() - 0.5) * 0.2);
            }
        } else if (settings.warning(level.getGameTime(), pos) && random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.POOF, x + (random.nextDouble() - 0.5) * 0.4, y, z + (random.nextDouble() - 0.5) * 0.4,
                    0, 0.05 + random.nextDouble() * 0.05, 0);
        }
    }
}
