package com.adastraplanets.rocket;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.registry.ModItems;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the rocket definitions currently in effect. The server fills it on datapack (re)load and
 * {@link SyncRocketDefinitionsPayload} mirrors it to clients, so both sides read the same values.
 */
public final class RocketDefinitions extends SimpleJsonResourceReloadListener {
    public static final String DIRECTORY = AdAstraPlanets.MOD_ID + "/rocket";
    private static final Gson GSON = new GsonBuilder().create();

    private static volatile Map<ResourceLocation, RocketDefinition> definitions = Map.of();

    private final ICondition.IContext conditions;
    private final RegistryAccess registries;

    /** Definitions honour {@code neoforge:conditions}, e.g. to drop a rocket disabled in the config. */
    public RocketDefinitions(ICondition.IContext conditions, RegistryAccess registries) {
        super(GSON, DIRECTORY);
        this.conditions = conditions;
        this.registries = registries;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, RocketDefinition> loaded = new HashMap<>();
        var ops = new ConditionalOps<>(RegistryOps.create(JsonOps.INSTANCE, this.registries), this.conditions);
        var codec = ConditionalOps.createConditionalCodec(RocketDefinition.CODEC);
        entries.forEach((id, json) -> codec.parse(ops, json)
                .resultOrPartial(error -> AdAstraPlanets.LOGGER.error("Invalid rocket definition {}: {}", id, error))
                .ifPresent(definition -> definition.ifPresentOrElse(
                        value -> loaded.put(id, value),
                        () -> AdAstraPlanets.LOGGER.info("Rocket definition {} skipped by its conditions", id))));
        definitions = Map.copyOf(loaded);
        publishProperties();
        AdAstraPlanets.LOGGER.info("Loaded {} rocket definitions", loaded.size());
    }

    public static Map<ResourceLocation, RocketDefinition> all() {
        return definitions;
    }

    static void replaceAll(Map<ResourceLocation, RocketDefinition> synced) {
        definitions = Map.copyOf(synced);
        publishProperties();
    }

    /**
     * Mirrors each defined rocket into Ad Astra's {@link Rocket#ROCKET_TO_PROPERTIES} (tier, item, fuel tag), where
     * other mods look rockets up: a fuel loader, for one, only fills rockets listed there and only takes their fuels.
     */
    private static void publishProperties() {
        ModEntityTypes.rockets().forEach((id, holder) -> {
            RocketDefinition definition = definitions.get(id);
            if (definition == null) {
                Rocket.ROCKET_TO_PROPERTIES.remove(holder.get());
            } else {
                Rocket.ROCKET_TO_PROPERTIES.put(holder.get(), new Rocket.RocketProperties(definition.tier(),
                        ModItems.rocketItem(id.getPath()), definition.ridingOffset(), definition.fuel()));
            }
        });
    }

    @Nullable
    public static RocketDefinition get(EntityType<?> type) {
        return definitions.get(BuiltInRegistries.ENTITY_TYPE.getKey(type));
    }
}
