package com.adastraplanets.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;

/**
 * Puts a slab in front of every one-block step of the surface, so players can walk up the terrain without jumping
 * (a slab is within the walking step height). Meant for high-gravity worlds where jumping a full block is hard.
 * Runs over the whole chunk once; place it with a plain "count: 1" and no spreading:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:step_slabs",
 *   "config": { "rules": [ { "on": "adastraplanets:zone_frost", "slab": { "Name": "adastraplanets:zone_frost_slab" } } ] }
 * }
 * </pre>
 *
 * Each rule says which slab to lay on which surface block; columns whose top matches no rule are left alone.
 */
public class StepSlabsFeature extends Feature<StepSlabsFeature.Configuration> {
    public record Rule(HolderSet<Block> on, BlockState slab) {
        public static final Codec<Rule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("on").forGetter(Rule::on),
                BlockState.CODEC.fieldOf("slab").forGetter(Rule::slab)
        ).apply(instance, Rule::new));
    }

    public record Configuration(List<Rule> rules) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Rule.CODEC.listOf().fieldOf("rules").forGetter(Configuration::rules)
        ).apply(instance, Configuration::new));
    }

    public StepSlabsFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        WorldGenLevel level = context.level();
        BlockPos corner = new BlockPos(context.origin().getX() & ~15, 0, context.origin().getZ() & ~15);
        boolean placed = false;
        for (int dx = 0; dx < 16; dx++) {
            for (int dz = 0; dz < 16; dz++) {
                int x = corner.getX() + dx;
                int z = corner.getZ() + dz;
                int height = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                BlockPos top = new BlockPos(x, height - 1, z);
                BlockPos above = top.above();
                if (!level.isEmptyBlock(above)) {
                    continue;
                }
                BlockState ground = level.getBlockState(top);
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    // a neighbour exactly one block higher makes a step worth smoothing
                    if (level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x + direction.getStepX(), z + direction.getStepZ()) == height + 1) {
                        for (Rule rule : context.config().rules()) {
                            if (ground.is(rule.on())) {
                                level.setBlock(above, rule.slab(), 2);
                                placed = true;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return placed;
    }
}
