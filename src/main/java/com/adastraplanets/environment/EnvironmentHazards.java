package com.adastraplanets.environment;

import com.adastraplanets.config.AdAstraPlanetsConfig;
import com.adastraplanets.registry.ModDamageTypes;
import com.adastraplanets.registry.ModDataMaps;
import earth.terrarium.adastra.api.systems.OxygenApi;
import earth.terrarium.adastra.api.systems.TemperatureApi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Optional;

/**
 * Hurts players whose environmental liner is too weak for where they are. Inside a sealed, oxygenated room on an
 * airless world (Ad Astra's oxygen distributor) nothing is demanded.
 */
public final class EnvironmentHazards {
    public static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.isCreative() || player.isSpectator()
                || !AdAstraPlanetsConfig.ENVIRONMENT.get()) {
            return;
        }
        EnvironmentSettings settings = Environments.settings();
        if (settings == null || player.tickCount % settings.checkInterval() != 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        EnvironmentRequirement requirement = Environments.requirement(level.dimension());
        if (requirement.demandsNothing()) {
            return;
        }
        if (!OxygenApi.API.hasOxygen(level) && OxygenApi.API.hasOxygen(level, player.blockPosition().above())) {
            return;
        }
        if (com.adastraplanets.registry.ModSuit.suited(player)) {
            return;
        }
        Optional<List<ItemStack>> worn = LinerSlots.worn(player);
        if (worn.isEmpty()) {
            return;
        }
        LinerProtection protection = protection(worn.get());

        int thermalMissing = requirement.thermal() - protection.thermal();
        int pressureMissing = requirement.pressure() - protection.pressure();
        if (thermalMissing > 0) {
            if (TemperatureApi.API.getTemperature(level, player.blockPosition()) > 0) {
                player.igniteForSeconds(settings.checkInterval() / 20F + 1);
                player.hurt(player.damageSources().onFire(), settings.thermalDamage() * thermalMissing);
            } else {
                player.setTicksFrozen(Math.min(player.getTicksRequiredToFreeze() + 20, player.getTicksFrozen() + settings.checkInterval() * 3));
                player.hurt(player.damageSources().freeze(), settings.thermalDamage() * thermalMissing);
            }
        }
        if (pressureMissing > 0) {
            if (requirement.pressureKind() == EnvironmentRequirement.PressureKind.LOW) {
                int duration = settings.checkInterval() + 20;
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, pressureMissing - 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, pressureMissing - 1, false, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0, false, false, true));
                player.hurt(ModDamageTypes.source(level, ModDamageTypes.DECOMPRESSION), settings.pressureDamage() * pressureMissing);
            } else {
                player.hurt(ModDamageTypes.source(level, ModDamageTypes.CRUSHING), settings.pressureDamage() * pressureMissing);
            }
        }
        if (thermalMissing > 0 || pressureMissing > 0) {
            player.displayClientMessage(Component.translatable("message.adastraplanets.liner_needed",
                    Math.max(requirement.thermal(), requirement.pressure())).withStyle(ChatFormatting.RED), true);
        }
    }

    /** The worn set's protection: each kind at its weakest piece, an empty slot counting as nothing. */
    public static LinerProtection protection(List<ItemStack> worn) {
        return LinerProtection.weakest(worn.stream().map(stack -> {
            LinerProtection piece = stack.isEmpty() ? null : stack.getItemHolder().getData(ModDataMaps.LINER_PROTECTION);
            return piece == null ? LinerProtection.NONE : piece;
        }).toList());
    }

    private EnvironmentHazards() {
    }
}
