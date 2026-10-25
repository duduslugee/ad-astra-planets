package com.adastraplanets.mixin;

import com.adastraplanets.config.AdAstraPlanetsConfig;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.utils.ModUtils;
import com.adastraplanets.world.SafeLanding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Server-side check matching the hidden planet list: hidden planets cannot be landed on (creative/operators can).
 * Landings on worlds without ground are moved to the nearest island (see SafeLanding).
 */
@Mixin(ModUtils.class)
public abstract class ModUtilsMixin {
    @ModifyVariable(method = "land", at = @At("HEAD"), argsOnly = true)
    private static Vec3 adastraplanets$landOnGround(Vec3 pos, ServerPlayer player, ServerLevel targetLevel) {
        return SafeLanding.adjust(targetLevel, pos);
    }

    @Inject(method = "canTeleportToPlanet", at = @At("HEAD"), cancellable = true)
    private static void adastraplanets$refuseHiddenSolarSystems(Player player, Planet targetPlanet, CallbackInfoReturnable<Boolean> cir) {
        if (AdAstraPlanetsConfig.solarSystemHidden(targetPlanet.solarSystem())
                && !player.isCreative() && !player.isSpectator() && !player.hasPermissions(2)) {
            cir.setReturnValue(false);
        }
    }
}
