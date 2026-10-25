package com.adastraplanets.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

/**
 * A hydrothermal chimney on the sea floor: a mound, a column with a glowing vent on top, and ore seeded in the rock
 * around its base. Everything is set in the configured feature's JSON:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:hydrothermal_vent",
 *   "config": {
 *     "chimney": { "type": "minecraft:simple_state_provider", "state": { "Name": "adastraplanets:chimney_rock" } },
 *     "vent": { "type": "minecraft:simple_state_provider", "state": { "Name": "adastraplanets:hydrothermal_vent" } },
 *     "ore": { "type": "minecraft:simple_state_provider", "state": { "Name": "adastraplanets:abyssium_ore" } },
 *     "height": { "type": "minecraft:uniform", "min_inclusive": 3, "max_inclusive": 8 },
 *     "mound_radius": 2, "ore_radius": 4,
 *     "ore_count": { "type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4 },
 *     "can_replace": "#adastraplanets:hydrothermal_vent_replaceable"
 *   }
 * }
 * </pre>
 *
 * It looks down from where it is placed for the sea floor, and only builds under water.
 */
public class HydrothermalVentFeature extends Feature<HydrothermalVentFeature.Configuration> {
    public record Configuration(BlockStateProvider chimney, BlockStateProvider vent, BlockStateProvider ore, IntProvider height,
                                IntProvider moundRadius, IntProvider oreRadius, IntProvider oreCount, HolderSet<Block> canReplace)
            implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("chimney").forGetter(Configuration::chimney),
                BlockStateProvider.CODEC.fieldOf("vent").forGetter(Configuration::vent),
                BlockStateProvider.CODEC.fieldOf("ore").forGetter(Configuration::ore),
                IntProvider.codec(1, 32).fieldOf("height").forGetter(Configuration::height),
                IntProvider.codec(0, 8).fieldOf("mound_radius").forGetter(Configuration::moundRadius),
                IntProvider.codec(0, 8).fieldOf("ore_radius").forGetter(Configuration::oreRadius),
                IntProvider.codec(0, 64).fieldOf("ore_count").forGetter(Configuration::oreCount),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_replace").forGetter(Configuration::canReplace)
        ).apply(instance, Configuration::new));
    }

    public HydrothermalVentFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        Configuration config = context.config();

        // find the floor: the first replaceable solid block under water
        BlockPos.MutableBlockPos cursor = context.origin().mutable();
        while (cursor.getY() > level.getMinBuildHeight() + 1 && level.getBlockState(cursor).getFluidState().is(Fluids.WATER)) {
            cursor.move(0, -1, 0);
        }
        BlockPos floor = cursor.immutable();
        if (!this.replaceable(level.getBlockState(floor), config) || !level.getFluidState(floor.above()).is(Fluids.WATER)) {
            return false;
        }

        int mound = config.moundRadius().sample(random);
        for (int dx = -mound; dx <= mound; dx++) {
            for (int dz = -mound; dz <= mound; dz++) {
                int rise = mound - Math.max(Math.abs(dx), Math.abs(dz)) + (random.nextInt(3) == 0 ? 1 : 0);
                for (int dy = 0; dy <= rise; dy++) {
                    BlockPos pos = floor.offset(dx, dy, dz);
                    if (dy == 0 || level.getFluidState(pos).is(Fluids.WATER)) {
                        level.setBlock(pos, config.chimney().getState(random, pos), 2);
                    }
                }
            }
        }

        // the column rises from the mound's top until it reaches its height or leaves the water; the vent caps it
        int height = config.height().sample(random);
        BlockPos base = floor.above(mound + 1);
        int built = 0;
        while (built < height && level.getFluidState(base.above(built + 1)).is(Fluids.WATER)) {
            BlockPos pos = base.above(built);
            level.setBlock(pos, config.chimney().getState(random, pos), 2);
            built++;
        }
        BlockPos mouth = base.above(built);
        level.setBlock(mouth, config.vent().getState(random, mouth), 2);

        // exactly ore_count blocks of ore, scattered through the rock within ore_radius of the base
        int oreRadius = config.oreRadius().sample(random);
        List<BlockPos> candidates = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(floor.offset(-oreRadius, -oreRadius, -oreRadius), floor.offset(oreRadius, mound, oreRadius))) {
            if (pos.distSqr(floor) <= oreRadius * oreRadius && this.replaceable(level.getBlockState(pos), config)) {
                candidates.add(pos.immutable());
            }
        }
        Util.shuffle(candidates, random);
        for (BlockPos pos : candidates.subList(0, Math.min(candidates.size(), config.oreCount().sample(random)))) {
            level.setBlock(pos, config.ore().getState(random, pos), 2);
        }
        return true;
    }

    private boolean replaceable(BlockState state, Configuration config) {
        return state.is(config.canReplace());
    }
}
