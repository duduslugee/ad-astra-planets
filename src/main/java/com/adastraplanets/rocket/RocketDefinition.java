package com.adastraplanets.rocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

/**
 * Balancing attributes of a rocket, loaded from {@code data/<namespace>/adastraplanets/rocket/<entity path>.json}.
 * The file id must match the rocket's entity type id. All fluid amounts are in millibuckets.
 *
 * @param tier                destinations with a planet tier up to this value can be reached
 * @param fuel                fluids accepted in the rocket's tank
 * @param fuelCapacity        tank size
 * @param launchCost          fuel consumed per launch
 * @param efficientFuel       fluids that use {@code efficientLaunchCost} instead of {@code launchCost}
 * @param efficientLaunchCost fuel consumed per launch when the tank holds an efficient fuel
 * @param acceleration        vertical speed gained per tick after lift-off
 * @param maxSpeed            vertical speed cap, in blocks per tick
 * @param ridingOffset        passenger seat height above the rocket's origin, in blocks
 * @param launchPads          launch pad blocks the rocket can be placed on (e.g. only the large pad for big rockets)
 * @param exhausts            where flame and smoke leave the rocket (its engine and each booster); one under the
 *                            centre if absent
 */
public record RocketDefinition(
        int tier,
        TagKey<Fluid> fuel,
        int fuelCapacity,
        int launchCost,
        TagKey<Fluid> efficientFuel,
        int efficientLaunchCost,
        float acceleration,
        float maxSpeed,
        float ridingOffset,
        TagKey<Block> launchPads,
        List<Exhaust> exhausts
) {
    /** The single exhaust under the centre, as on Ad Astra's rockets. */
    public static final List<Exhaust> CENTRE = List.of(new Exhaust(0, -0.75F, 0, 1));

    public static final Codec<RocketDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("tier").forGetter(RocketDefinition::tier),
            TagKey.codec(Registries.FLUID).fieldOf("fuel").forGetter(RocketDefinition::fuel),
            ExtraCodecs.POSITIVE_INT.fieldOf("fuel_capacity").forGetter(RocketDefinition::fuelCapacity),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("launch_cost").forGetter(RocketDefinition::launchCost),
            TagKey.codec(Registries.FLUID).fieldOf("efficient_fuel").forGetter(RocketDefinition::efficientFuel),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("efficient_launch_cost").forGetter(RocketDefinition::efficientLaunchCost),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("acceleration").forGetter(RocketDefinition::acceleration),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("max_speed").forGetter(RocketDefinition::maxSpeed),
            Codec.FLOAT.fieldOf("riding_offset").forGetter(RocketDefinition::ridingOffset),
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("launch_pads").forGetter(RocketDefinition::launchPads),
            Exhaust.CODEC.listOf().optionalFieldOf("exhausts", CENTRE).forGetter(RocketDefinition::exhausts)
    ).apply(instance, RocketDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RocketDefinition> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    /**
     * An engine or booster nozzle, in blocks from the rocket's origin as seen from its front (turns with the rocket).
     *
     * @param size share of Ad Astra's flame and smoke this nozzle gives off (1 = a rocket's whole engine)
     */
    public record Exhaust(float x, float y, float z, float size) {
        public static final Codec<Exhaust> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("x", 0F).forGetter(Exhaust::x),
                Codec.FLOAT.optionalFieldOf("y", -0.75F).forGetter(Exhaust::y),
                Codec.FLOAT.optionalFieldOf("z", 0F).forGetter(Exhaust::z),
                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("size", 1F).forGetter(Exhaust::size)
        ).apply(instance, Exhaust::new));
    }
}
