package com.adastraplanets.api.event;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** Fired on the server when a player gets into any rocket. Cancelling keeps them out. */
public class RocketBoardEvent extends Event implements ICancellableEvent {
    private final Rocket rocket;
    private final Player player;

    public RocketBoardEvent(Rocket rocket, Player player) {
        this.rocket = rocket;
        this.player = player;
    }

    public Rocket getRocket() {
        return this.rocket;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getTier() {
        return this.rocket.tier();
    }

    public ResourceLocation getRocketType() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(this.rocket.getType());
    }
}
