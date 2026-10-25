package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Impact craters as a height offset in blocks: a rounded bowl with a raised rim, scattered over a grid of cells (see
 * Scatter). Positions depend on the world seed through the given noise. Use it inside a 2D (flat_cache) surface
 * function, scaled to density units:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:craters", "noise": "minecraft:surface",
 *   "cell_size": 160, "chance": 0.5, "min_radius": 12, "max_radius": 45, "depth": 0.3, "rim": 0.06
 * }
 * </pre>
 *
 * {@code depth} and {@code rim} are fractions of each crater's radius.
 */
public record CratersFunction(NoiseHolder noise, int cellSize, float chance, float minRadius, float maxRadius, float depth,
                              float rim, long salt) implements DensityFunction {
    public static final MapCodec<CratersFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NoiseHolder.CODEC.fieldOf("noise").forGetter(CratersFunction::noise),
            Codec.intRange(8, 4096).fieldOf("cell_size").forGetter(CratersFunction::cellSize),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(CratersFunction::chance),
            Codec.floatRange(1, 2048).fieldOf("min_radius").forGetter(CratersFunction::minRadius),
            Codec.floatRange(1, 2048).fieldOf("max_radius").forGetter(CratersFunction::maxRadius),
            Codec.floatRange(0, 4).fieldOf("depth").forGetter(CratersFunction::depth),
            Codec.floatRange(0, 4).fieldOf("rim").forGetter(CratersFunction::rim)
    ).apply(instance, (noise, cell, chance, min, max, depth, rim) -> new CratersFunction(noise, cell, chance, min, max, depth, rim, 0L)));
    public static final KeyDispatchDataCodec<CratersFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    /** The rim fades out this far beyond the radius (as a fraction of it). */
    private static final double RIM_WIDTH = 0.5;

    @Override
    public double compute(FunctionContext context) {
        return Scatter.sum(context.blockX(), context.blockZ(), this.salt, this.cellSize, this.chance, this.minRadius, this.maxRadius,
                this::profile);
    }

    /** Height in blocks at this distance (in radii) from a crater's centre; depth and rim scale with the radius. */
    private double profile(double distance, double radius) {
        if (distance < 1) {
            // bowl floor rising to the rim's crest at the edge
            return (-this.depth * (1 - distance * distance) + this.rim * Math.pow(distance, 6)) * radius;
        }
        if (distance < 1 + RIM_WIDTH) {
            double t = (distance - 1) / RIM_WIDTH;
            return this.rim * (1 - t) * (1 - t) * radius;
        }
        return 0;
    }

    @Override
    public void fillArray(double[] values, ContextProvider provider) {
        provider.fillAllDirectly(values, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        NoiseHolder seeded = visitor.visitNoise(this.noise);
        return visitor.apply(new CratersFunction(seeded, this.cellSize, this.chance, this.minRadius, this.maxRadius, this.depth, this.rim,
                Scatter.salt(seeded)));
    }

    @Override
    public double minValue() {
        return -this.depth * Math.max(this.minRadius, this.maxRadius) * 2;
    }

    @Override
    public double maxValue() {
        return this.rim * Math.max(this.minRadius, this.maxRadius) * 2;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
