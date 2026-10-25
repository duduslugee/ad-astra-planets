package com.adastraplanets.world.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Bands along one axis, from -1 to 1 and back every {@code period} blocks, warped a little so their edges are not
 * ruler-straight: a gas giant's light zones and dark belts, used as a climate value to pick biomes.
 *
 * <pre>
 * { "type": "adastraplanets:bands", "axis": "z", "period": 256, "warp": 12, "warp_scale": 96 }
 * </pre>
 *
 * {@code offset} (blocks, default 0) shifts the bands along the axis: a quarter period puts a band's peak at 0.
 */
public record BandsFunction(Axis axis, float period, float warp, float warpScale, float offset) implements DensityFunction.SimpleFunction {
    public static final MapCodec<BandsFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Axis.CODEC.optionalFieldOf("axis", Axis.Z).forGetter(BandsFunction::axis),
            Codec.floatRange(2, 65536).fieldOf("period").forGetter(BandsFunction::period),
            Codec.floatRange(0, 4096).optionalFieldOf("warp", 0F).forGetter(BandsFunction::warp),
            Codec.floatRange(1, 65536).optionalFieldOf("warp_scale", 64F).forGetter(BandsFunction::warpScale),
            Codec.FLOAT.optionalFieldOf("offset", 0F).forGetter(BandsFunction::offset)
    ).apply(instance, BandsFunction::new));
    public static final KeyDispatchDataCodec<BandsFunction> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Override
    public double compute(FunctionContext context) {
        double along = this.axis == Axis.X ? context.blockX() : context.blockZ();
        double across = this.axis == Axis.X ? context.blockZ() : context.blockX();
        double warped = along + this.offset + this.warp * Math.sin(across * Math.PI * 2 / this.warpScale);
        return Math.sin(warped * Math.PI * 2 / this.period);
    }

    @Override
    public double minValue() {
        return -1;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }

    public enum Axis implements StringRepresentable {
        X("x"), Z("z");

        public static final Codec<Axis> CODEC = StringRepresentable.fromEnum(Axis::values);
        private final String name;

        Axis(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
