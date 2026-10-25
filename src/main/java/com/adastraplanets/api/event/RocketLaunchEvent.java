package com.adastraplanets.api.event;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired on the server when any rocket (Ad Astra's or this addon's) is about to start its launch countdown, from the
 * pilot's key or a powered launch pad. Cancelling keeps the rocket on the pad; while it stays blocked the event is not
 * fired again for a second, so handlers can show a message without spamming it.
 */
public class RocketLaunchEvent extends Event implements ICancellableEvent {
    private final Rocket rocket;

    public RocketLaunchEvent(Rocket rocket) {
        this.rocket = rocket;
    }

    public Rocket getRocket() {
        return this.rocket;
    }

    /** The player piloting the rocket, or null for an empty rocket launched by redstone. */
    @Nullable
    public Player getPilot() {
        return this.rocket.getControllingPassenger() instanceof Player player ? player : null;
    }

    public int getTier() {
        return this.rocket.tier();
    }

    public ResourceLocation getRocketType() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(this.rocket.getType());
    }
}
