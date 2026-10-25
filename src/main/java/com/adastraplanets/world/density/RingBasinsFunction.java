package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Multi-ring impact basins as a height offset in blocks: a smooth, slightly sunken central plain surrounded by
 * concentric ridges that fade towards the edge (like Callisto's Valhalla). Rare and huge, scattered like craters (see
 * Scatter). Use it inside a 2D (flat_cache) surface function, scaled to density units:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:ring_basins", "noise": "minecraft:surface",
 *   "cell_size": 1200, "chance": 0.5, "min_radius": 160, "max_radius": 320,
 *   "plain": 0.25, "plain_depth": 3, "rings": 6, "ring_height": 7
 * }
 * </pre>
 *
 * {@code plain} is the central plain's radius as a fraction of the basin's; depths and heights are in blocks.
 */
public record RingBasinsFunction(NoiseHolder noise, int cellSize, float chance, float minRadius, float maxRadius, float plain,
                                 float plainDepth, int rings, float ringHeight, long salt) implements DensityFunction {
    public static final MapCodec<RingBasinsFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NoiseHolder.CODEC.fieldOf("noise").forGetter(RingBasinsFunction::noise),
            Codec.intRange(8, 16384).fieldOf("cell_size").forGetter(RingBasinsFunction::cellSize),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(RingBasinsFunction::chance),
            Codec.floatRange(1, 8192).fieldOf("min_radius").forGetter(RingBasinsFunction::minRadius),
            Codec.floatRange(1, 8192).fieldOf("max_radius").forGetter(RingBasinsFunction::maxRadius),
            Codec.floatRange(0, 0.9F).fieldOf("plain").forGetter(RingBasinsFunction::plain),
            Codec.floatRange(0, 256).fieldOf("plain_depth").forGetter(RingBasinsFunction::plainDepth),
            Codec.intRange(1, 64).fieldOf("rings").forGetter(RingBasinsFunction::rings),
            Codec.floatRange(0, 256).fieldOf("ring_height").forGetter(RingBasinsFunction::ringHeight)
    ).apply(instance, (noise, cell, chance, min, max, plain, depth, rings, height) ->
            new RingBasinsFunction(noise, cell, chance, min, max, plain, depth, rings, height, 0L)));
    public static final KeyDispatchDataCodec<RingBasinsFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public double compute(FunctionContext context) {
        return Scatter.sum(context.blockX(), context.blockZ(), this.salt, this.cellSize, this.chance, this.minRadius, this.maxRadius,
                this::profile);
    }

    /** Height in blocks at this distance (in radii) from a basin's centre. */
    private double profile(double distance, double radius) {
        if (distance >= 1) {
            return 0;
        }
        if (distance < this.plain) {
            // the plain dips gently towards its centre
            return -this.plainDepth * (1 - (distance / this.plain) * (distance / this.plain) * 0.5);
        }
        double t = (distance - this.plain) / (1 - this.plain);
        double ring = Math.pow(Math.max(0, Math.cos(Math.PI * t * this.rings)), 3);
        // ridges strongest near the plain, fading out at the edge
        return ring * this.ringHeight * Math.sqrt(1 - t) - this.plainDepth * (1 - t) * 0.5;
    }

    @Override
    public void fillArray(double[] values, ContextProvider provider) {
        provider.fillAllDirectly(values, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        NoiseHolder seeded = visitor.visitNoise(this.noise);
        return visitor.apply(new RingBasinsFunction(seeded, this.cellSize, this.chance, this.minRadius, this.maxRadius, this.plain,
                this.plainDepth, this.rings, this.ringHeight, Scatter.salt(seeded)));
    }

    @Override
    public double minValue() {
        return -this.plainDepth * 2;
    }

    @Override
    public double maxValue() {
        return this.ringHeight * 2;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
