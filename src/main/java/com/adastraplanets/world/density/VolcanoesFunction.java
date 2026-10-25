package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Volcanoes as a height offset in blocks: a cone rising to a sharp rim around a steep-walled caldera whose floor sinks
 * below the surrounding plain (with a lava sea level set above it, the caldera fills with lava). Scattered like
 * craters (see Scatter). Use it inside a 2D (flat_cache) surface function, scaled to density units:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:volcanoes", "noise": "minecraft:surface",
 *   "cell_size": 220, "chance": 0.45, "min_radius": 24, "max_radius": 60,
 *   "height": 0.55, "caldera": 0.25, "caldera_depth": 12
 * }
 * </pre>
 *
 * {@code height} (the rim's height) and {@code caldera} (the pit's radius) are fractions of each volcano's radius;
 * {@code caldera_depth} is in blocks below the plain.
 */
public record VolcanoesFunction(NoiseHolder noise, int cellSize, float chance, float minRadius, float maxRadius, float height,
                                float caldera, float calderaDepth, long salt) implements DensityFunction {
    public static final MapCodec<VolcanoesFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NoiseHolder.CODEC.fieldOf("noise").forGetter(VolcanoesFunction::noise),
            Codec.intRange(8, 4096).fieldOf("cell_size").forGetter(VolcanoesFunction::cellSize),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(VolcanoesFunction::chance),
            Codec.floatRange(1, 2048).fieldOf("min_radius").forGetter(VolcanoesFunction::minRadius),
            Codec.floatRange(1, 2048).fieldOf("max_radius").forGetter(VolcanoesFunction::maxRadius),
            Codec.floatRange(0, 4).fieldOf("height").forGetter(VolcanoesFunction::height),
            Codec.floatRange(0, 0.9F).fieldOf("caldera").forGetter(VolcanoesFunction::caldera),
            Codec.floatRange(0, 256).fieldOf("caldera_depth").forGetter(VolcanoesFunction::calderaDepth)
    ).apply(instance, (noise, cell, chance, min, max, height, caldera, depth) ->
            new VolcanoesFunction(noise, cell, chance, min, max, height, caldera, depth, 0L)));
    public static final KeyDispatchDataCodec<VolcanoesFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public double compute(FunctionContext context) {
        return Scatter.sum(context.blockX(), context.blockZ(), this.salt, this.cellSize, this.chance, this.minRadius, this.maxRadius,
                this::profile);
    }

    /**
     * Height in blocks at this distance (in radii) from the centre: a flat caldera floor a fixed depth below the
     * plain (so every caldera reaches the lava level), then concave flanks falling from the rim into the plain.
     */
    private double profile(double distance, double radius) {
        if (distance >= 1) {
            return 0;
        }
        if (distance < this.caldera) {
            return -this.calderaDepth;
        }
        double t = (distance - this.caldera) / (1 - this.caldera);
        return this.height * radius * Math.pow(1 - t, 2.2);
    }

    @Override
    public void fillArray(double[] values, ContextProvider provider) {
        provider.fillAllDirectly(values, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        NoiseHolder seeded = visitor.visitNoise(this.noise);
        return visitor.apply(new VolcanoesFunction(seeded, this.cellSize, this.chance, this.minRadius, this.maxRadius, this.height,
                this.caldera, this.calderaDepth, Scatter.salt(seeded)));
    }

    @Override
    public double minValue() {
        return -this.calderaDepth;
    }

    @Override
    public double maxValue() {
        return this.height * this.maxRadius * 2;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
