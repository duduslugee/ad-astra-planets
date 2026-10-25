package com.adastraplanets.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Common config, one file per topic in config/adastraplanets/ (adastraplanets-rockets.toml, -worldgen, -launch_pads,
 * -solar_systems, -environment, -debug; see {@link #FILES}). It only switches content on and off; every number and recipe
 * stays in datapacks. Datapack files react to it through the {@code adastraplanets:option} and
 * {@code adastraplanets:rocket_enabled} conditions, so disabling something removes its recipes, worldgen and rocket
 * definition without any special code.
 */
public final class AdAstraPlanetsConfig {
    /** Each topic's spec, by topic name; registered as config/adastraplanets/adastraplanets-&lt;topic&gt;.toml. */
    public static final Map<String, ModConfigSpec> FILES = new LinkedHashMap<>();
    public static final ModConfigSpec.ConfigValue<List<? extends String>> DISABLED_ROCKETS;
    public static final ModConfigSpec.BooleanValue HELIONITE_ORE;
    public static final ModConfigSpec.BooleanValue LARGE_LAUNCH_PAD;
    public static final ModConfigSpec.BooleanValue HUGE_LAUNCH_PAD;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> HIDDEN_SOLAR_SYSTEMS;
    public static final ModConfigSpec.BooleanValue ENVIRONMENT;
    public static final ModConfigSpec.IntValue SCANNER_RADIUS;
    public static final ModConfigSpec.ConfigValue<String> SCANNER_TAG;
    public static final ModConfigSpec.IntValue SCANNER_HIGHLIGHTS;
    public static final ModConfigSpec.IntValue SCANNER_HIGHLIGHT_SECONDS;

    /** Boolean options addressable from datapacks by name (the {@code adastraplanets:option} condition). */
    private static final Map<String, BooleanSupplier> OPTIONS;

    static {
        ModConfigSpec.Builder rockets = new ModConfigSpec.Builder();
        DISABLED_ROCKETS = rockets
                .comment("Rocket ids to disable, e.g. [\"adastraplanets:tier_5_rocket\"]. A disabled rocket loses its",
                        "recipe and definition (it cannot launch) and is hidden from the creative tab.")
                .defineListAllowEmpty("disabled", List.of(), () -> "adastraplanets:tier_5_rocket",
                        entry -> entry instanceof String id && ResourceLocation.tryParse(id) != null);
        FILES.put("rockets", rockets.build());

        ModConfigSpec.Builder worldgen = new ModConfigSpec.Builder();
        HELIONITE_ORE = worldgen.comment("Generate Helionite ore (where: the adastraplanets:has_helionite_ore biome tag)")
                .define("helionite_ore", true);
        FILES.put("worldgen", worldgen.build());

        ModConfigSpec.Builder launchPads = new ModConfigSpec.Builder();
        LARGE_LAUNCH_PAD = launchPads.comment("Enable the 5x5 Large Launch Pad recipe (rockets from tier 6 need it)")
                .define("large_launch_pad", true);
        HUGE_LAUNCH_PAD = launchPads.comment("Enable the 7x7 Huge Launch Pad recipe (rockets from tier 8 need it)")
                .define("huge_launch_pad", true);
        FILES.put("launch_pads", launchPads.build());

        ModConfigSpec.Builder solarSystems = new ModConfigSpec.Builder();
        HIDDEN_SOLAR_SYSTEMS = solarSystems
                .comment("Solar systems to hide from the planet selection screen, e.g. Ad Astra's own",
                        "[\"ad_astra:solar_system\", \"ad_astra:proxima_centauri\"]. Their planets keep working (gravity,",
                        "oxygen, launching from them) but cannot be chosen as destinations, except in creative or by operators.",
                        "If the Overworld's system is hidden, move the Overworld to another system first (override",
                        "data/ad_astra/planets/earth.json in a datapack), or players cannot fly back home.",
                        "The server's value applies to everyone on it.")
                .defineListAllowEmpty("hidden", List.of(), () -> "ad_astra:proxima_centauri",
                        entry -> entry instanceof String id && ResourceLocation.tryParse(id) != null);
        FILES.put("solar_systems", solarSystems.build());

        ModConfigSpec.Builder environment = new ModConfigSpec.Builder();
        ENVIRONMENT = environment.comment("Planets demand an environmental liner (four Curios slots) against their temperature and",
                        "pressure; how much each one demands is in datapacks (adastraplanets/environments). Needs Curios.")
                .define("enabled", true);
        FILES.put("environment", environment.build());

        ModConfigSpec.Builder debug = new ModConfigSpec.Builder();
        debug.comment("Ore Scanner (debug item, creative or operators only): right-click to count ores around you").push("ore_scanner");
        SCANNER_RADIUS = debug.comment("How many chunks around the player to scan (4 = a 9x9 chunk square); only loaded chunks count")
                .defineInRange("radius", 4, 0, 16);
        SCANNER_TAG = debug.comment("Block tag of what to count, e.g. \"c:ores\" or \"c:ores/nivalum\"")
                .define("tag", "c:ores", value -> value instanceof String id && ResourceLocation.tryParse(id) != null);
        SCANNER_HIGHLIGHTS = debug.comment("How many of the nearest ores glow after a scan (outlined through walls, in each ore's colour); 0 = none")
                .defineInRange("highlights", 128, 0, 2048);
        SCANNER_HIGHLIGHT_SECONDS = debug.comment("How long the highlights stay, in seconds (a new scan replaces them)")
                .defineInRange("highlight_seconds", 30, 1, 600);
        debug.pop();
        FILES.put("debug", debug.build());

        // names kept as "<topic>.<key>", so datapacks written for the single-file config keep working
        OPTIONS = Map.of(
                "worldgen.helionite_ore", HELIONITE_ORE::getAsBoolean,
                "launch_pads.large_launch_pad", LARGE_LAUNCH_PAD::getAsBoolean,
                "launch_pads.huge_launch_pad", HUGE_LAUNCH_PAD::getAsBoolean);
    }

    /** The file a topic's config is saved to, relative to the config folder. */
    public static String fileName(String topic) {
        return "adastraplanets/adastraplanets-" + topic + ".toml";
    }

    public static boolean option(String name) {
        BooleanSupplier value = OPTIONS.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Unknown Ad Astra: Planets config option '" + name + "', known: " + OPTIONS.keySet());
        }
        return value.getAsBoolean();
    }

    public static boolean solarSystemHidden(ResourceLocation solarSystem) {
        return HIDDEN_SOLAR_SYSTEMS.get().contains(solarSystem.toString());
    }

    public static boolean rocketEnabled(ResourceLocation rocket) {
        return !DISABLED_ROCKETS.get().contains(rocket.toString());
    }

    private AdAstraPlanetsConfig() {
    }
}
