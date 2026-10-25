package com.adastraplanets.client.solarsystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

/**
 * How a solar system looks on Ad Astra's planet selection screen, from
 * {@code assets/<namespace>/adastraplanets/solar_systems/<name>.json}; the file id is the solar system id that
 * Ad Astra's planet JSONs use ({@code "solar_system": "<namespace>:<name>"}).
 *
 * <pre>
 * {
 *   "sun": "ad_astra:textures/environment/red_sun.png",
 *   "orbit_color": "#1F7F80",
 *   "planets": [
 *     { "texture": "mypack:textures/gui/planets/ember.png", "orbit": 1, "speed": 2.0 },
 *     { "texture": "mypack:textures/gui/planets/cinder.png", "orbit": 3, "speed": 0.5, "phase": 120,
 *       "moons": [{ "texture": "mypack:textures/gui/planets/ash.png", "orbit": 1, "speed": 6.0 }] }
 *   ]
 * }
 * </pre>
 *
 * @param sun        sun texture, drawn in the middle
 * @param sunSize    sun size in GUI pixels
 * @param orbitColor colour of the orbit rings, "#RRGGBB"
 * @param orbitGap   distance between neighbouring orbit rings, in GUI pixels
 * @param moonGap    distance between neighbouring moon orbits around a planet, in GUI pixels
 * @param planets    the bodies circling the sun
 * @param addMissingPlanets whether planets of this system that no body links to are added on extra outer orbits, so
 *                   every destination can be clicked (default true); systems without a map file are drawn this way
 */
public record SolarSystemMap(ResourceLocation sun, int sunSize, int orbitColor, int orbitGap, int moonGap, List<Body> planets,
                             boolean addMissingPlanets) {
    /**
     * A moon circling a planet on the map.
     *
     * @param orbit which moon orbit around its planet (1 = closest)
     * @param size  icon size in GUI pixels
     * @param speed degrees per tenth of a second
     * @param phase starting angle, in degrees
     * @param planet the destination it stands for: an Ad Astra planet's dimension id (also its name's lang key,
     *               {@code planet.<namespace>.<path>}); without one the icon is decoration only
     * @param resources items worth gathering there to progress (shown in the information panel)
     */
    public record Moon(ResourceLocation texture, int orbit, int size, float speed, float phase, Optional<ResourceLocation> planet,
                       List<ResourceLocation> resources) {
        public static final Codec<Moon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Moon::texture),
                ExtraCodecs.POSITIVE_INT.fieldOf("orbit").forGetter(Moon::orbit),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("size", 6).forGetter(Moon::size),
                Codec.FLOAT.optionalFieldOf("speed", 4F).forGetter(Moon::speed),
                Codec.FLOAT.optionalFieldOf("phase", 0F).forGetter(Moon::phase),
                ResourceLocation.CODEC.optionalFieldOf("planet").forGetter(Moon::planet),
                ResourceLocation.CODEC.listOf().optionalFieldOf("resources", List.of()).forGetter(Moon::resources)
        ).apply(instance, Moon::new));
    }

    /**
     * @param texture icon of the body
     * @param orbit   which ring it travels on (1 = closest to the sun)
     * @param size    icon size in GUI pixels
     * @param speed   degrees per tenth of a second (Ad Astra's own planets use 0.5 to 2)
     * @param phase   starting angle, in degrees
     * @param moons   moons circling this body
     * @param planet  the destination it stands for (see {@link Moon#planet()}); clicking it opens its close-up
     * @param resources items worth gathering there to progress (shown in the information panel)
     */
    public record Body(ResourceLocation texture, int orbit, int size, float speed, float phase, List<Moon> moons,
                       Optional<ResourceLocation> planet, List<ResourceLocation> resources) {
        public static final Codec<Body> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Body::texture),
                ExtraCodecs.POSITIVE_INT.fieldOf("orbit").forGetter(Body::orbit),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("size", 12).forGetter(Body::size),
                Codec.FLOAT.optionalFieldOf("speed", 1F).forGetter(Body::speed),
                Codec.FLOAT.optionalFieldOf("phase", 0F).forGetter(Body::phase),
                Moon.CODEC.listOf().optionalFieldOf("moons", List.of()).forGetter(Body::moons),
                ResourceLocation.CODEC.optionalFieldOf("planet").forGetter(Body::planet),
                ResourceLocation.CODEC.listOf().optionalFieldOf("resources", List.of()).forGetter(Body::resources)
        ).apply(instance, Body::new));
    }

    private static final Codec<Integer> HEX_COLOUR = Codec.STRING.comapFlatMap(text -> {
        try {
            return com.mojang.serialization.DataResult.success(0xFF000000 | Integer.parseInt(text.replace("#", ""), 16));
        } catch (NumberFormatException e) {
            return com.mojang.serialization.DataResult.error(() -> "Not a colour: " + text);
        }
    }, colour -> String.format("#%06X", colour & 0xFFFFFF));

    public static final Codec<SolarSystemMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("sun").forGetter(SolarSystemMap::sun),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("sun_size", 16).forGetter(SolarSystemMap::sunSize),
            HEX_COLOUR.optionalFieldOf("orbit_color", 0xFF24327B).forGetter(SolarSystemMap::orbitColor),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("orbit_gap", 30).forGetter(SolarSystemMap::orbitGap),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("moon_gap", 6).forGetter(SolarSystemMap::moonGap),
            Body.CODEC.listOf().optionalFieldOf("planets", List.of()).forGetter(SolarSystemMap::planets),
            Codec.BOOL.optionalFieldOf("add_missing_planets", true).forGetter(SolarSystemMap::addMissingPlanets)
    ).apply(instance, SolarSystemMap::new));
}
