package com.adastraplanets.environment;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Loads the environment requirements and settings (see EnvironmentRequirement, EnvironmentSettings); reloadable. */
public final class Environments extends SimpleJsonResourceReloadListener {
    private static final ResourceLocation SETTINGS = AdAstraPlanets.id("adastraplanets/environment_settings.json");

    private static volatile Map<ResourceLocation, EnvironmentRequirement> byDimension = Map.of();
    private static volatile EnvironmentRequirement fallback = EnvironmentRequirement.NONE;
    private static volatile EnvironmentSettings settings = null;

    public Environments() {
        super(new Gson(), AdAstraPlanets.MOD_ID + "/environments");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, EnvironmentRequirement> loaded = new HashMap<>();
        EnvironmentRequirement[] defaults = {EnvironmentRequirement.NONE};
        entries.forEach((id, json) -> EnvironmentRequirement.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> AdAstraPlanets.LOGGER.error("Invalid environment {}: {}", id, error))
                .ifPresent(requirement -> {
                    requirement.dimensions().forEach(dimension -> loaded.put(dimension, requirement));
                    if (requirement.isDefault()) {
                        defaults[0] = requirement;
                    }
                }));
        byDimension = Map.copyOf(loaded);
        fallback = defaults[0];
        settings = this.loadSettings(manager);
        AdAstraPlanets.LOGGER.info("Loaded environment requirements for {} dimensions", loaded.size());
    }

    private EnvironmentSettings loadSettings(ResourceManager manager) {
        List<Resource> stack = manager.getResourceStack(SETTINGS);
        if (stack.isEmpty()) {
            AdAstraPlanets.LOGGER.error("Missing {}: the environmental liner system is off", SETTINGS);
            return null;
        }
        try (Reader reader = stack.getLast().openAsReader()) {
            return EnvironmentSettings.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(reader))
                    .resultOrPartial(error -> AdAstraPlanets.LOGGER.error("Invalid {}: {}", SETTINGS, error)).orElse(null);
        } catch (Exception e) {
            AdAstraPlanets.LOGGER.error("Could not read {}", SETTINGS, e);
            return null;
        }
    }

    /** What this dimension demands: its own entry, else the default for Ad Astra planets, else nothing. */
    public static EnvironmentRequirement requirement(ResourceKey<Level> dimension) {
        EnvironmentRequirement own = byDimension.get(dimension.location());
        if (own != null) {
            return own;
        }
        return AdAstraData.isPlanet(dimension) ? fallback : EnvironmentRequirement.NONE;
    }

    /** Null when no valid settings file is loaded (the system then does nothing). */
    public static EnvironmentSettings settings() {
        return settings;
    }
}
