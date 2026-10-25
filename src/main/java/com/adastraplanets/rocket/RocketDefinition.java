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
        TagKey<Block> launchPads
) {
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
            TagKey.hashedCodec(Registries.BLOCK).fieldOf("launch_pads").forGetter(RocketDefinition::launchPads)
    ).apply(instance, RocketDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RocketDefinition> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);
}
