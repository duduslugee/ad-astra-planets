package com.adastraplanets.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * A geyser set into the surface with a ragged ring of frost around it, where its plume falls back as snow. Placed at
 * the surface block it replaces; everything is set in the configured feature's JSON:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:geyser",
 *   "config": {
 *     "geyser": { "type": "minecraft:simple_state_provider", "state": { "Name": "adastraplanets:geyser" } },
 *     "frost": { "type": "minecraft:simple_state_provider", "state": { "Name": "adastraplanets:rime" } },
 *     "frost_radius": { "type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4 },
 *     "can_replace": ["adastraplanets:crust_ice", "adastraplanets:rusted_ice"]
 *   }
 * }
 * </pre>
 */
public class GeyserFeature extends Feature<GeyserFeature.Configuration> {
    public record Configuration(BlockStateProvider geyser, BlockStateProvider frost, IntProvider frostRadius, HolderSet<Block> canReplace)
            implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("geyser").forGetter(Configuration::geyser),
                BlockStateProvider.CODEC.fieldOf("frost").forGetter(Configuration::frost),
                IntProvider.codec(0, 8).fieldOf("frost_radius").forGetter(Configuration::frostRadius),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_replace").forGetter(Configuration::canReplace)
        ).apply(instance, Configuration::new));
    }

    /** How far up or down from the geyser the frost looks for the surface of uneven ground. */
    private static final int SURFACE_SEARCH = 3;

    public GeyserFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        Configuration config = context.config();
        BlockPos origin = context.origin();
        if (!level.getBlockState(origin).is(config.canReplace())) {
            return false;
        }

        int radius = config.frostRadius().sample(random);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                // dense near the vent, thinning out to a ragged edge
                if (distance > radius + 0.5 || random.nextFloat() > 1.15 - distance / (radius + 1)) {
                    continue;
                }
                BlockPos surface = this.surface(level, origin.offset(dx, 0, dz), config);
                if (surface != null) {
                    level.setBlock(surface, config.frost().getState(random, surface), 2);
                }
            }
        }
        level.setBlock(origin, config.geyser().getState(random, origin), 2);
        return true;
    }

    /** The replaceable block with open air above it in this column, near the geyser's height. */
    private BlockPos surface(WorldGenLevel level, BlockPos start, Configuration config) {
        for (int dy = SURFACE_SEARCH; dy >= -SURFACE_SEARCH; dy--) {
            BlockPos pos = start.above(dy);
            if (level.getBlockState(pos).is(config.canReplace()) && level.isEmptyBlock(pos.above())) {
                return pos;
            }
        }
        return null;
    }
}
