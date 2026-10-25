package com.adastraplanets.compat.kubejs;

import com.adastraplanets.api.event.RocketLaunchEvent;
import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/** {@code AdAstraPlanetsEvents.rocketLaunch}: a rocket is about to start its countdown; {@code cancel()} stops it. */
public class RocketLaunchKubeEvent implements KubeEntityEvent {
    private final RocketLaunchEvent event;

    public RocketLaunchKubeEvent(RocketLaunchEvent event) {
        this.event = event;
    }

    @Override
    public Entity getEntity() {
        return this.event.getRocket();
    }

    public Rocket getRocket() {
        return this.event.getRocket();
    }

    @Nullable
    public Player getPilot() {
        return this.event.getPilot();
    }

    @Override
    public Player getPlayer() {
        return this.event.getPilot();
    }

    public int getTier() {
        return this.event.getTier();
    }

    public ResourceLocation getRocketType() {
        return this.event.getRocketType();
    }
}
