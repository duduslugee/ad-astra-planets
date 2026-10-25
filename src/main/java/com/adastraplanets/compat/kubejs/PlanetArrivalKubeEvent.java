package com.adastraplanets.compat.kubejs;

import com.adastraplanets.api.event.PlanetArrivalEvent;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** {@code AdAstraPlanetsEvents.planetArrival}: a player arrived on a planet or in an orbit. */
public class PlanetArrivalKubeEvent implements KubePlayerEvent {
    private final PlanetArrivalEvent event;

    public PlanetArrivalKubeEvent(PlanetArrivalEvent event) {
        this.event = event;
    }

    @Override
    public Player getEntity() {
        return this.event.getPlayer();
    }

    /** The dimension arrived in, e.g. {@code ad_astra:mars} or a datapack planet. */
    public ResourceLocation getPlanet() {
        return this.event.getDimension().location();
    }

    public ResourceLocation getFrom() {
        return this.event.getFrom().location();
    }

    public int getTier() {
        return this.event.getPlanet().tier();
    }

    public ResourceLocation getSolarSystem() {
        return this.event.getPlanet().solarSystem();
    }

    public boolean isOrbit() {
        return this.event.isOrbit();
    }

    public boolean hasOxygen() {
        return this.event.getPlanet().oxygen();
    }

    public float getGravity() {
        return this.event.getPlanet().gravity();
    }
}
