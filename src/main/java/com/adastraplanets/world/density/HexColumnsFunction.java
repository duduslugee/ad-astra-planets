package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Hexagonal columns, like basalt columns or Saturn's polar hexagon: the plane is split into hexagonal cells and each
 * cell gets its own height step, from 0 to 1 in {@code steps} even steps (a hash of the cell and the world seed).
 * Within {@code crack} blocks of a cell's edge the value drops by {@code crack_depth} steps, so neighbouring columns
 * are split by a narrow crevice. Use it inside a 2D (flat_cache) surface function, scaled to a height in blocks:
 *
 * <pre>
 * { "type": "adastraplanets:hex_columns", "noise": "minecraft:surface", "cell_size": 5, "steps": 4, "crack": 0.6, "crack_depth": 1 }
 * </pre>
 *
 * {@code cell_size} is the distance from a cell's centre to its corners, in blocks.
 */
public record HexColumnsFunction(NoiseHolder noise, float cellSize, int steps, float crack, float crackDepth, long salt)
        implements DensityFunction {
    public static final MapCodec<HexColumnsFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NoiseHolder.CODEC.fieldOf("noise").forGetter(HexColumnsFunction::noise),
            Codec.floatRange(1, 1024).fieldOf("cell_size").forGetter(HexColumnsFunction::cellSize),
            Codec.intRange(1, 64).optionalFieldOf("steps", 4).forGetter(HexColumnsFunction::steps),
            Codec.floatRange(0, 64).optionalFieldOf("crack", 0F).forGetter(HexColumnsFunction::crack),
            Codec.floatRange(0, 64).optionalFieldOf("crack_depth", 1F).forGetter(HexColumnsFunction::crackDepth)
    ).apply(instance, (noise, size, steps, crack, depth) -> new HexColumnsFunction(noise, size, steps, crack, depth, 0L)));
    public static final KeyDispatchDataCodec<HexColumnsFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    private static final double SQRT3 = Math.sqrt(3);

    @Override
    public double compute(FunctionContext context) {
        double x = context.blockX() + 0.5;
        double z = context.blockZ() + 0.5;
        // pointy-top axial coordinates, rounded to the nearest cell, then the nearest and second-nearest centres
        double q = (SQRT3 / 3 * x - z / 3) / this.cellSize;
        double r = (2.0 / 3 * z) / this.cellSize;
        long baseQ = Math.round(q);
        long baseR = Math.round(r);
        double nearest = Double.MAX_VALUE;
        double second = Double.MAX_VALUE;
        long cellQ = baseQ;
        long cellR = baseR;
        for (long dq = -1; dq <= 1; dq++) {
            for (long dr = -1; dr <= 1; dr++) {
                long cq = baseQ + dq;
                long cr = baseR + dr;
                double cx = this.cellSize * SQRT3 * (cq + cr / 2.0);
                double cz = this.cellSize * 1.5 * cr;
                double distance = Math.hypot(x - cx, z - cz);
                if (distance < nearest) {
                    second = nearest;
                    nearest = distance;
                    cellQ = cq;
                    cellR = cr;
                } else if (distance < second) {
                    second = distance;
                }
            }
        }
        long hash = Scatter.mix(this.salt ^ (cellQ * 341873128712L + cellR * 132897987541L));
        double step = Math.floor(Scatter.unit(hash) * this.steps) / Math.max(1, this.steps - 1);
        // half the gap between the two nearest centres' distances is the distance to the shared edge
        if (this.crack > 0 && (second - nearest) / 2 < this.crack) {
            step -= this.crackDepth / Math.max(1, this.steps - 1);
        }
        return step;
    }

    @Override
    public void fillArray(double[] values, ContextProvider provider) {
        provider.fillAllDirectly(values, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        NoiseHolder seeded = visitor.visitNoise(this.noise);
        return visitor.apply(new HexColumnsFunction(seeded, this.cellSize, this.steps, this.crack, this.crackDepth,
                Scatter.salt(seeded)));
    }

    @Override
    public double minValue() {
        return -this.crackDepth;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
