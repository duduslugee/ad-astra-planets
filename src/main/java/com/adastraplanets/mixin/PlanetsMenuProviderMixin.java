package com.adastraplanets.mixin;

import com.adastraplanets.config.AdAstraPlanetsConfig;
import earth.terrarium.adastra.common.menus.base.PlanetsMenuProvider;
import earth.terrarium.adastra.common.menus.content.PlanetsContent;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Hides the planets of solar systems listed in {@code solar_systems.hidden} by adding them to the disabled planets Ad
 * Astra sends with its planet screen; the menu, the screen and the client all already honour that list.
 */
@Mixin(PlanetsMenuProvider.class)
public abstract class PlanetsMenuProviderMixin {
    @Inject(method = "createContent", at = @At("RETURN"), cancellable = true)
    private void adastraplanets$hideSolarSystems(ServerPlayer player, CallbackInfoReturnable<PlanetsContent> cir) {
        PlanetsContent content = cir.getReturnValue();
        Set<ResourceLocation> disabled = new HashSet<>(content.disabledPlanets());
        AdAstraData.planets().values().stream()
                .filter(planet -> AdAstraPlanetsConfig.solarSystemHidden(planet.solarSystem()))
                .forEach(planet -> disabled.add(planet.dimension().location()));
        if (disabled.size() != content.disabledPlanets().size()) {
            cir.setReturnValue(new PlanetsContent(Set.copyOf(disabled), content.spaceStations(), content.spawnLocations()));
        }
    }
}
