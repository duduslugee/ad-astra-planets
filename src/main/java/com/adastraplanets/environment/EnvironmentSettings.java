package com.adastraplanets.environment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

/**
 * How hard unprotected players are hit, from {@code data/<namespace>/adastraplanets/environment_settings.json}
 * (the last one loaded wins):
 *
 * <pre>
 * { "check_interval": 20, "thermal_damage": 2.0, "pressure_damage": 2.0 }
 * </pre>
 *
 * @param checkInterval  ticks between checks; each check deals damage once
 * @param thermalDamage  damage per missing thermal level when too hot or too cold
 * @param pressureDamage damage per missing pressure level; low pressure also slows and weakens
 */
public record EnvironmentSettings(int checkInterval, float thermalDamage, float pressureDamage) {
    public static final Codec<EnvironmentSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("check_interval").forGetter(EnvironmentSettings::checkInterval),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("thermal_damage").forGetter(EnvironmentSettings::thermalDamage),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("pressure_damage").forGetter(EnvironmentSettings::pressureDamage)
    ).apply(instance, EnvironmentSettings::new));
}
