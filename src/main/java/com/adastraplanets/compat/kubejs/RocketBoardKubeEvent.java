package com.adastraplanets.compat.kubejs;

import com.adastraplanets.api.event.RocketBoardEvent;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** {@code AdAstraPlanetsEvents.rocketBoard}: a player is getting into a rocket; {@code cancel()} keeps them out. */
public class RocketBoardKubeEvent implements KubePlayerEvent {
    private final RocketBoardEvent event;

    public RocketBoardKubeEvent(RocketBoardEvent event) {
        this.event = event;
    }

    @Override
    public Player getEntity() {
        return this.event.getPlayer();
    }

    public Rocket getRocket() {
        return this.event.getRocket();
    }

    public int getTier() {
        return this.event.getTier();
    }

    public ResourceLocation getRocketType() {
        return this.event.getRocketType();
    }
}
