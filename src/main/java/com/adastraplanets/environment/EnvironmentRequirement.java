package com.adastraplanets.environment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

import java.util.List;

/**
 * How much environmental liner a place demands, from {@code data/<namespace>/adastraplanets/environments/*.json}:
 *
 * <pre>
 * { "dimensions": ["adastraplanets:europa", "adastraplanets:europa_orbit"], "thermal": 3, "pressure": 3, "pressure_kind": "low" }
 * { "default": true, "thermal": 1, "pressure": 1, "pressure_kind": "low" }
 * </pre>
 *
 * A {@code default} entry applies to every Ad Astra planet (and orbit) that no entry names; dimensions that are not
 * planets (the Nether, the End) demand nothing unless named. Whether the thermal danger is heat or cold comes from
 * Ad Astra's temperature at the player's position.
 *
 * @param thermal      liner level needed against the temperature (0 = none)
 * @param pressure     liner level needed against the pressure (0 = none)
 * @param pressureKind {@code low} (vacuum, thin air: decompression) or {@code high} (dense atmospheres: crushing)
 */
public record EnvironmentRequirement(List<ResourceLocation> dimensions, boolean isDefault, int thermal, int pressure,
                                     PressureKind pressureKind) {
    public static final EnvironmentRequirement NONE = new EnvironmentRequirement(List.of(), false, 0, 0, PressureKind.LOW);

    public static final Codec<EnvironmentRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().optionalFieldOf("dimensions", List.of()).forGetter(EnvironmentRequirement::dimensions),
            Codec.BOOL.optionalFieldOf("default", false).forGetter(EnvironmentRequirement::isDefault),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("thermal").forGetter(EnvironmentRequirement::thermal),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("pressure").forGetter(EnvironmentRequirement::pressure),
            PressureKind.CODEC.fieldOf("pressure_kind").forGetter(EnvironmentRequirement::pressureKind)
    ).apply(instance, EnvironmentRequirement::new));

    public boolean demandsNothing() {
        return this.thermal <= 0 && this.pressure <= 0;
    }

    public enum PressureKind implements StringRepresentable {
        LOW("low"), HIGH("high");

        public static final Codec<PressureKind> CODEC = StringRepresentable.fromEnum(PressureKind::values);
        private final String name;

        PressureKind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
