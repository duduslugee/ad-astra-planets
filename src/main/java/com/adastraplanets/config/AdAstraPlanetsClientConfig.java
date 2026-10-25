package com.adastraplanets.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

/**
 * Client config for the "where to find it" line on ores (config/adastraplanets/adastraplanets-ore_tooltips.toml); it
 * only changes what this player sees. Modpacks can rewrite or extend it, including for other mods' items.
 */
public final class AdAstraPlanetsClientConfig {
    public static final String TOPIC = "ore_tooltips";
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ORE_TOOLTIPS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ORE_LOCATIONS;

    /**
     * The built-in ores and where they generate (translation keys, so the line follows the game's language). They
     * always apply, so ores added in later versions get their line in existing config files too; the config's own
     * entries are applied over them.
     */
    public static final List<String> BUILT_IN_LOCATIONS = List.of(
            "adastraplanets:helionite_ore=planet.ad_astra.mercury",
            "adastraplanets:umbrite_ore=planet.adastraplanets.phobos",
            "adastraplanets:pallorite_ore=planet.adastraplanets.deimos",
            "adastraplanets:igneum_ore=planet.adastraplanets.io",
            "adastraplanets:abyssium_ore=tooltip.adastraplanets.where.europa_vents",
            "adastraplanets:aurorium_ore=planet.adastraplanets.ganymede",
            "adastraplanets:eonyx_ore=planet.adastraplanets.callisto",
            "adastraplanets:stormium_ore=planet.adastraplanets.jupiter",
            "adastraplanets:nivalum_ore=planet.adastraplanets.enceladus",
            "adastraplanets:ferrolith_ore=planet.adastraplanets.rhea",
            "adastraplanets:vesperine_ore=planet.adastraplanets.iapetus",
            "adastraplanets:tholinar_ore=planet.adastraplanets.titan",
            "adastraplanets:cassinium_ore=planet.adastraplanets.saturn",
            "adastraplanets:mosaicon_ore=planet.adastraplanets.miranda",
            "adastraplanets:chasmal_ore=planet.adastraplanets.titania",
            "adastraplanets:obscurex_ore=planet.adastraplanets.oberon",
            "adastraplanets:azurane_ore=planet.adastraplanets.uranus",
            "adastraplanets:nitralis_ore=planet.adastraplanets.triton",
            "adastraplanets:tempestine_ore=planet.adastraplanets.neptune",
            "adastraplanets:mordrine_ore=planet.adastraplanets.charon",
            "adastraplanets:cordium_ore=planet.adastraplanets.pluto",
            "adastraplanets:shardex_ore=planet.adastraplanets.dysnomia",
            "adastraplanets:albedon_ore=planet.adastraplanets.eris");

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ORE_TOOLTIPS = builder.comment("Show the \"Found on: ...\" line").define("enabled", true);
        ORE_LOCATIONS = builder
                .comment("Extra or replaced lines, one entry per item: \"<item id>=<where>\". <where> is a translation key",
                        "(e.g. \"planet.adastraplanets.io\", from any resource pack) or plain text (e.g. \"Io, near volcanoes\").",
                        "This mod's ores already have their line built in; an entry here replaces it, and \"<item id>=\" with",
                        "nothing after the = hides it. Any item works, not only this mod's ores.",
                        "The line itself is the translation key tooltip.adastraplanets.found_on (\"Found on: %s\").")
                .defineListAllowEmpty("locations", List.of(), () -> "minecraft:iron_ore=Overworld",
                        AdAstraPlanetsClientConfig::validLocation);

        SPEC = builder.build();
    }

    private static boolean validLocation(Object entry) {
        if (!(entry instanceof String text)) {
            return false;
        }
        int split = text.indexOf('=');
        return split > 0 && ResourceLocation.tryParse(text.substring(0, split).trim()) != null;
    }

    private AdAstraPlanetsClientConfig() {
    }
}
