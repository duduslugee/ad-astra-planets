package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Grooved terrain as a height offset in blocks: sets of parallel ridges and troughs, each set running its own way
 * across a square lane of the grid (like Ganymede's sulci), fading out towards the lane's edges so neighbouring sets
 * meet on smooth ground. Directions and spacing depend on the world seed through the given noise. Use it inside a 2D
 * (flat_cache) surface function, scaled to density units, often multiplied by a mask so only some regions are grooved:
 *
 * <pre>
 * {
 *   "type": "adastraplanets:grooves", "noise": "minecraft:surface",
 *   "lane_size": 96, "min_spacing": 6, "max_spacing": 11, "depth": 5, "edge": 12
 * }
 * </pre>
 *
 * {@code depth} is the ridges' height in blocks; {@code edge} is how far (in blocks) from a lane's border the grooves
 * fade out.
 */
public record GroovesFunction(NoiseHolder noise, int laneSize, float minSpacing, float maxSpacing, float depth, float edge,
                              long salt) implements DensityFunction {
    public static final MapCodec<GroovesFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NoiseHolder.CODEC.fieldOf("noise").forGetter(GroovesFunction::noise),
            Codec.intRange(8, 4096).fieldOf("lane_size").forGetter(GroovesFunction::laneSize),
            Codec.floatRange(2, 256).fieldOf("min_spacing").forGetter(GroovesFunction::minSpacing),
            Codec.floatRange(2, 256).fieldOf("max_spacing").forGetter(GroovesFunction::maxSpacing),
            Codec.floatRange(0, 128).fieldOf("depth").forGetter(GroovesFunction::depth),
            Codec.floatRange(0, 2048).fieldOf("edge").forGetter(GroovesFunction::edge)
    ).apply(instance, (noise, lane, min, max, depth, edge) -> new GroovesFunction(noise, lane, min, max, depth, edge, 0L)));
    public static final KeyDispatchDataCodec<GroovesFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public double compute(FunctionContext context) {
        int x = context.blockX();
        int z = context.blockZ();
        int laneX = Math.floorDiv(x, this.laneSize);
        int laneZ = Math.floorDiv(z, this.laneSize);
        long hash = Scatter.mix(this.salt ^ (laneX * 341873128712L + laneZ * 132897987541L));
        double angle = Scatter.unit(hash) * Math.PI;
        hash = Scatter.mix(hash);
        double spacing = this.minSpacing + (this.maxSpacing - this.minSpacing) * Scatter.unit(hash);
        double across = x * Math.cos(angle) + z * Math.sin(angle);
        // sharp-crested ridges with rounded troughs
        double ridge = Math.pow(1 - Math.abs(Math.sin(Math.PI * across / spacing)), 1.6);
        double inX = Math.min(x - laneX * this.laneSize, (laneX + 1) * this.laneSize - 1 - x);
        double inZ = Math.min(z - laneZ * this.laneSize, (laneZ + 1) * this.laneSize - 1 - z);
        double fade = this.edge <= 0 ? 1 : Math.min(1, Math.min(inX, inZ) / this.edge);
        return (ridge - 0.4) * this.depth * fade;
    }

    @Override
    public void fillArray(double[] values, ContextProvider provider) {
        provider.fillAllDirectly(values, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        NoiseHolder seeded = visitor.visitNoise(this.noise);
        return visitor.apply(new GroovesFunction(seeded, this.laneSize, this.minSpacing, this.maxSpacing, this.depth, this.edge,
                Scatter.salt(seeded)));
    }

    @Override
    public double minValue() {
        return -0.4 * this.depth;
    }

    @Override
    public double maxValue() {
        return 0.6 * this.depth;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
