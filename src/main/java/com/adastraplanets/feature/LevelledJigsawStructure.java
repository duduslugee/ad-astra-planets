package com.adastraplanets.feature;

import com.adastraplanets.registry.ModFeatures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

/**
 * A jigsaw structure laid on the highest ground of its footprint instead of the ground at its centre, so a ruin never
 * ends up buried in a slope; where the ground within the footprint rises and falls more than max_slope, it is not
 * built at all. The structure's own plinth fills in under it.
 *
 * <ul>
 *     <li>start_pool, size, max_distance_from_center: as a jigsaw structure;</li>
 *     <li>offset: added to the ground height (negative sinks the structure, as start_height on a jigsaw);</li>
 *     <li>footprint_radius: how far round the centre the ground is measured;</li>
 *     <li>max_slope: the most the ground may vary over the footprint;</li>
 *     <li>anchor: where the footprint is, as an [x, z] offset from the template's centre (before rotation), for
 *     templates whose surface part is not over their middle (a rig on top of a deep temple).</li>
 * </ul>
 */
public class LevelledJigsawStructure extends Structure {
    public static final MapCodec<LevelledJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            Codec.intRange(0, 20).fieldOf("size").forGetter(s -> s.size),
            Codec.INT.optionalFieldOf("offset", 0).forGetter(s -> s.offset),
            Codec.intRange(0, 48).fieldOf("footprint_radius").forGetter(s -> s.footprintRadius),
            Codec.intRange(0, 64).fieldOf("max_slope").forGetter(s -> s.maxSlope),
            Codec.intRange(1, 128).optionalFieldOf("max_distance_from_center", 80).forGetter(s -> s.maxDistanceFromCenter),
            Codec.INT.listOf().optionalFieldOf("anchor", java.util.List.of(0, 0)).forGetter(s -> java.util.List.of(s.anchorX, s.anchorZ))
    ).apply(instance, LevelledJigsawStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final int size;
    private final int offset;
    private final int footprintRadius;
    private final int maxSlope;
    private final int maxDistanceFromCenter;
    private final int anchorX;
    private final int anchorZ;

    public LevelledJigsawStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int size, int offset,
                                   int footprintRadius, int maxSlope, int maxDistanceFromCenter, java.util.List<Integer> anchor) {
        super(settings);
        this.startPool = startPool;
        this.size = size;
        this.offset = offset;
        this.footprintRadius = footprintRadius;
        this.maxSlope = maxSlope;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.anchorX = anchor.isEmpty() ? 0 : anchor.get(0);
        this.anchorZ = anchor.size() < 2 ? 0 : anchor.get(1);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        // a first placement, only to learn where the start piece's centre falls (its rotation is random)
        BlockPos corner = new BlockPos(context.chunkPos().getMinBlockX(), 0, context.chunkPos().getMinBlockZ());
        Optional<GenerationStub> probe = this.place(fresh(context), corner, Optional.of(Heightmap.Types.WORLD_SURFACE_WG));
        if (probe.isEmpty()) {
            return Optional.empty();
        }
        // the footprint: the anchor, turned as the template will be (the placement's first draw is its rotation)
        net.minecraft.world.level.block.Rotation rotation = net.minecraft.world.level.block.Rotation.getRandom(fresh(context).random());
        BlockPos anchor = new BlockPos(this.anchorX, 0, this.anchorZ).rotate(rotation);
        BlockPos centre = probe.get().position().offset(anchor.getX(), 0, anchor.getZ());
        int lowest = Integer.MAX_VALUE;
        int highest = Integer.MIN_VALUE;
        int r = this.footprintRadius;
        for (int dx = -r; dx <= r; dx += Math.max(1, r / 3)) {
            for (int dz = -r; dz <= r; dz += Math.max(1, r / 3)) {
                int height = context.chunkGenerator().getFirstFreeHeight(centre.getX() + dx, centre.getZ() + dz,
                        Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
                lowest = Math.min(lowest, height);
                highest = Math.max(highest, height);
            }
        }
        if (highest - lowest > this.maxSlope) {
            return Optional.empty();
        }
        // the same placement again (same random rotation and piece), at the highest ground
        return this.place(fresh(context), corner.atY(highest + this.offset), Optional.empty());
    }

    private Optional<GenerationStub> place(GenerationContext context, BlockPos pos, Optional<Heightmap.Types> heightmap) {
        return JigsawPlacement.addPieces(context, this.startPool, Optional.empty(), this.size, pos, false, heightmap,
                // our templates say where water goes: blocks that can hold water must not fill with the sea they replace
                this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.IGNORE_WATERLOGGING);
    }

    /** A context with its random reset to the chunk's seed, so each placement draws the same rotation and piece. */
    private static GenerationContext fresh(GenerationContext context) {
        return new GenerationContext(context.registryAccess(), context.chunkGenerator(), context.biomeSource(), context.randomState(),
                context.structureTemplateManager(), context.seed(), context.chunkPos(), context.heightAccessor(), context.validBiome());
    }

    @Override
    public StructureType<?> type() {
        return ModFeatures.LEVELLED_JIGSAW.get();
    }
}
