package com.adastraplanets.compat.kubejs;

import com.adastraplanets.api.event.PlanetArrivalEvent;
import com.adastraplanets.api.event.RocketBoardEvent;
import com.adastraplanets.api.event.RocketLaunchEvent;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.neoforged.neoforge.common.NeoForge;

/**
 * KubeJS side of the addon's events (server scripts):
 * <pre>
 * AdAstraPlanetsEvents.rocketLaunch(event => {
 *   if (event.tier >= 7 && !event.pilot?.stages.has('outer_planets')) event.cancel()
 * })
 * AdAstraPlanetsEvents.rocketBoard(event => { ... event.cancel() to keep the player out })
 * AdAstraPlanetsEvents.planetArrival(event => { event.player.tell(`Welcome to ${event.planet}`) })
 * </pre>
 * Only loaded when KubeJS is installed.
 */
public final class AdAstraPlanetsKubeEvents {
    public static final EventGroup GROUP = EventGroup.of("AdAstraPlanetsEvents");
    public static final EventHandler ROCKET_LAUNCH = GROUP.server("rocketLaunch", () -> RocketLaunchKubeEvent.class).hasResult();
    public static final EventHandler ROCKET_BOARD = GROUP.server("rocketBoard", () -> RocketBoardKubeEvent.class).hasResult();
    public static final EventHandler PLANET_ARRIVAL = GROUP.server("planetArrival", () -> PlanetArrivalKubeEvent.class);

    /** Forwards the addon's NeoForge events to scripts; a script's cancel() cancels the NeoForge event. */
    public static void bridge() {
        NeoForge.EVENT_BUS.addListener((RocketLaunchEvent event) -> {
            if (ROCKET_LAUNCH.hasListeners()) {
                ROCKET_LAUNCH.post(new RocketLaunchKubeEvent(event)).applyCancel(event);
            }
        });
        NeoForge.EVENT_BUS.addListener((RocketBoardEvent event) -> {
            if (ROCKET_BOARD.hasListeners()) {
                ROCKET_BOARD.post(new RocketBoardKubeEvent(event)).applyCancel(event);
            }
        });
        NeoForge.EVENT_BUS.addListener((PlanetArrivalEvent event) -> {
            if (PLANET_ARRIVAL.hasListeners()) {
                PLANET_ARRIVAL.post(new PlanetArrivalKubeEvent(event));
            }
        });
    }

    private AdAstraPlanetsKubeEvents() {
    }
}
