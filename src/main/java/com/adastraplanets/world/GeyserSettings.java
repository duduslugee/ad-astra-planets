package com.adastraplanets.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

/**
 * How a geyser block behaves, from the {@code adastraplanets:geysers} block data map
 * ({@code data/<namespace>/data_maps/block/geysers.json}):
 *
 * <pre>
 * { "values": { "adastraplanets:geyser": { "interval": 240, "duration": 60, "height": 14, "strength": 0.9 } } }
 * </pre>
 *
 * @param interval ticks from one eruption's start to the next
 * @param duration ticks each eruption lasts
 * @param warning  ticks before an eruption during which the vent puffs vapour
 * @param height   how many blocks above the vent the plume reaches (and pushes)
 * @param strength upward speed given to entities in the plume, in blocks per tick
 * @param particle the plume's main particle (default {@code minecraft:cloud})
 * @param spray    particles thrown around the plume (default {@code minecraft:snowflake})
 */
public record GeyserSettings(int interval, int duration, int warning, int height, float strength, ParticleOptions particle,
                             ParticleOptions spray) {
    public static final Codec<GeyserSettings> CODEC = RecordCodecBuilder.<GeyserSettings>create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("interval").forGetter(GeyserSettings::interval),
            ExtraCodecs.POSITIVE_INT.fieldOf("duration").forGetter(GeyserSettings::duration),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("warning", 40).forGetter(GeyserSettings::warning),
            ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(GeyserSettings::height),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("strength").forGetter(GeyserSettings::strength),
            ParticleTypes.CODEC.optionalFieldOf("particle", ParticleTypes.CLOUD).forGetter(GeyserSettings::particle),
            ParticleTypes.CODEC.optionalFieldOf("spray", ParticleTypes.SNOWFLAKE).forGetter(GeyserSettings::spray)
    ).apply(instance, GeyserSettings::new)).validate(settings -> settings.duration() < settings.interval()
            ? com.mojang.serialization.DataResult.success(settings)
            : com.mojang.serialization.DataResult.error(() -> "Geyser duration must be shorter than its interval"));

    /**
     * Ticks since this geyser's current cycle began. Each position gets its own offset so neighbouring geysers do not
     * erupt together; it depends only on the game time, so client and server agree without syncing.
     */
    public long cycleTick(long gameTime, BlockPos pos) {
        return Math.floorMod(gameTime + Mth.getSeed(pos), (long) this.interval);
    }

    public boolean erupting(long gameTime, BlockPos pos) {
        return this.cycleTick(gameTime, pos) < this.duration;
    }

    public boolean warning(long gameTime, BlockPos pos) {
        return this.cycleTick(gameTime, pos) >= this.interval - this.warning;
    }
}
