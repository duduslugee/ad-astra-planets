package com.adastraplanets.api.event;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Turns vanilla/NeoForge events into this addon's rocket and planet events. */
public final class EventHooks {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventHooks::onMount);
        NeoForge.EVENT_BUS.addListener(EventHooks::onChangedDimension);
    }

    private static void onMount(EntityMountEvent event) {
        if (event.isMounting() && !event.getLevel().isClientSide()
                && event.getEntityBeingMounted() instanceof Rocket rocket && event.getEntityMounting() instanceof Player player
                && NeoForge.EVENT_BUS.post(new RocketBoardEvent(rocket, player)).isCanceled()) {
            event.setCanceled(true);
        }
    }

    private static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && AdAstraData.isPlanet(event.getTo())) {
            Planet planet = AdAstraData.getPlanet(event.getTo());
            if (planet != null) {
                NeoForge.EVENT_BUS.post(new PlanetArrivalEvent(player, planet, event.getFrom(), event.getTo()));
            }
        }
    }

    private EventHooks() {
    }
}
