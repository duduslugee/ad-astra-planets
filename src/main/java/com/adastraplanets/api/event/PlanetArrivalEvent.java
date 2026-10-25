package com.adastraplanets.api.event;

import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * Fired on the server after a player arrives in a dimension that Ad Astra knows as a planet or an orbit (including
 * ones added by datapacks). Informational: use it for quests, advancements or messages.
 */
public class PlanetArrivalEvent extends Event {
    private final ServerPlayer player;
    private final Planet planet;
    private final ResourceKey<Level> from;
    private final ResourceKey<Level> to;

    public PlanetArrivalEvent(ServerPlayer player, Planet planet, ResourceKey<Level> from, ResourceKey<Level> to) {
        this.player = player;
        this.planet = planet;
        this.from = from;
        this.to = to;
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }

    /** Ad Astra's planet entry (tier, gravity, oxygen, temperature, solar system, orbit). */
    public Planet getPlanet() {
        return this.planet;
    }

    public ResourceKey<Level> getFrom() {
        return this.from;
    }

    public ResourceKey<Level> getDimension() {
        return this.to;
    }

    /** True when the destination is an orbit (space) rather than a planet's surface; Ad Astra lists orbits as planets
     * without an orbit of their own. */
    public boolean isOrbit() {
        return this.planet.isSpace();
    }
}
