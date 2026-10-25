package com.adastraplanets.content;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builds the files that {@link StartupContent} entries need (models, blockstates, names, loot, tags) and serves them
 * as built-in packs at the bottom of the pack list, so a modpack's own resource pack or datapack still overrides them.
 * Modpacks only supply textures, GeckoLib models, rocket definitions and worldgen.
 */
public final class GeneratedResources {
    public static void addPacks(AddPackFindersEvent event) {
        StartupContent content = StartupContent.get();
        if (content.rockets().isEmpty() && content.ores().isEmpty() && content.globes().isEmpty()) {
            return;
        }
        PackType type = event.getPackType();
        Map<ResourceLocation, byte[]> files = type == PackType.CLIENT_RESOURCES ? assets(content) : data(content);
        PackLocationInfo info = new PackLocationInfo(AdAstraPlanets.MOD_ID + "_generated",
                Component.literal("Ad Astra: Planets (content.json)"), PackSource.BUILT_IN, Optional.empty());
        int format = SharedConstants.getCurrentVersion().getPackVersion(type);
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return new GeneratedPack(location, type, files, format);
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                return this.openPrimary(location);
            }
        };
        Pack pack = Pack.readMetaAndCreate(info, supplier, type, new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
        if (pack != null) {
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }

    static Map<ResourceLocation, byte[]> assets(StartupContent content) {
        Map<ResourceLocation, byte[]> files = new HashMap<>();
        JsonObject lang = new JsonObject();
        for (StartupContent.RocketEntry rocket : content.rockets()) {
            // inherits the Tier 5 rocket's display transforms; a resource pack can supply its own model instead
            files.put(file("models/item/" + rocket.id() + ".json"), json("{\"parent\": \"adastraplanets:item/tier_5_rocket\"}"));
            rocket.name().ifPresent(name -> {
                lang.addProperty("item.adastraplanets." + rocket.id(), name);
                lang.addProperty("entity.adastraplanets." + rocket.id(), name);
            });
        }
        for (StartupContent.OreEntry ore : content.ores()) {
            files.put(file("blockstates/" + ore.id() + ".json"),
                    json("{\"variants\": {\"\": {\"model\": \"adastraplanets:block/" + ore.id() + "\"}}}"));
            JsonObject model = new JsonObject();
            model.addProperty("parent", "adastraplanets:block/ore_overlay");
            JsonObject textures = new JsonObject();
            textures.addProperty("base", ore.base().toString());
            textures.addProperty("overlay", ore.overlay().toString());
            textures.addProperty("particle", ore.base().toString());
            model.add("textures", textures);
            files.put(file("models/block/" + ore.id() + ".json"), bytes(model));
            files.put(file("models/item/" + ore.id() + ".json"), json("{\"parent\": \"adastraplanets:block/" + ore.id() + "\"}"));
            ore.name().ifPresent(name -> lang.addProperty("block.adastraplanets." + ore.id(), name));
        }
        for (StartupContent.GlobeEntry globe : content.globes()) {
            // the stand and the spinning cube both read textures/block/globe/<id>.png
            String id = globe.id();
            files.put(file("blockstates/" + id + ".json"),
                    json("{\"variants\": {\"\": {\"model\": \"adastraplanets:block/" + id + "\"}}}"));
            String texture = "adastraplanets:block/globe/" + id;
            files.put(file("models/block/" + id + ".json"), json("{\"parent\": \"adastraplanets:block/globe\", \"render_type\": \"cutout\", \"textures\": {\"0\": \""
                    + texture + "\", \"particle\": \"" + texture + "\"}}"));
            files.put(file("models/block/" + id + "_cube.json"),
                    json("{\"parent\": \"adastraplanets:block/globe_cube\", \"textures\": {\"0\": \"" + texture + "\"}}"));
            files.put(file("models/item/" + id + ".json"), json("{\"parent\": \"adastraplanets:item/globe\"}"));
            globe.name().ifPresent(name -> lang.addProperty("block.adastraplanets." + id, name));
        }
        if (!lang.isEmpty()) {
            files.put(file("lang/en_us.json"), bytes(lang));
        }
        return files;
    }

    static Map<ResourceLocation, byte[]> data(StartupContent content) {
        Map<ResourceLocation, byte[]> files = new HashMap<>();
        Map<String, List<String>> blockTags = new LinkedHashMap<>();
        Map<String, List<String>> itemTags = new LinkedHashMap<>();
        for (StartupContent.RocketEntry rocket : content.rockets()) {
            add(itemTags, "ad_astra:held_over_head", AdAstraPlanets.id(rocket.id()).toString());
        }
        for (StartupContent.OreEntry ore : content.ores()) {
            String id = AdAstraPlanets.id(ore.id()).toString();
            files.put(file("loot_table/blocks/" + ore.id() + ".json"), bytes(oreLoot(id, ore.drop().toString())));
            add(blockTags, "minecraft:mineable/pickaxe", id);
            if (!ore.toolTier().equals("wood")) {
                add(blockTags, "minecraft:needs_" + ore.toolTier() + "_tool", id);
            }
            add(blockTags, "c:ores", id);
            add(itemTags, "c:ores", id);
            for (ResourceLocation tag : ore.tags()) {
                add(blockTags, tag.toString(), id);
                add(itemTags, tag.toString(), id);
            }
        }
        for (StartupContent.GlobeEntry globe : content.globes()) {
            String id = AdAstraPlanets.id(globe.id()).toString();
            files.put(file("loot_table/blocks/" + globe.id() + ".json"), bytes(JsonParser.parseString("""
                    {"type": "minecraft:block", "pools": [{"rolls": 1, "bonus_rolls": 0, "entries": [{"type": "minecraft:item",
                      "name": "%s"}], "conditions": [{"condition": "minecraft:survives_explosion"}]}]}""".formatted(id)).getAsJsonObject()));
            add(blockTags, "minecraft:mineable/pickaxe", id);
            add(blockTags, "ad_astra:globes", id);
            add(itemTags, "ad_astra:globes", id);
        }
        blockTags.forEach((tag, values) -> files.put(tagFile("block", tag), bytes(tagJson(values))));
        itemTags.forEach((tag, values) -> files.put(tagFile("item", tag), bytes(tagJson(values))));
        return files;
    }

    /** Same shape as vanilla ore loot: silk touch keeps the block, otherwise the drop with fortune and explosion decay. */
    private static JsonObject oreLoot(String block, String drop) {
        return JsonParser.parseString("""
                {"type": "minecraft:block", "pools": [{"rolls": 1, "bonus_rolls": 0, "entries": [{"type": "minecraft:alternatives",
                  "children": [
                    {"type": "minecraft:item", "name": "%s", "conditions": [{"condition": "minecraft:match_tool", "predicate":
                      {"predicates": {"minecraft:enchantments": [{"enchantments": "minecraft:silk_touch", "levels": {"min": 1}}]}}}]},
                    {"type": "minecraft:item", "name": "%s", "functions": [
                      {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                      {"function": "minecraft:explosion_decay"}]}]}]}]}
                """.formatted(block, drop)).getAsJsonObject();
    }

    private static void add(Map<String, List<String>> tags, String tag, String value) {
        tags.computeIfAbsent(tag, key -> new java.util.ArrayList<>()).add(value);
    }

    private static JsonObject tagJson(List<String> values) {
        JsonObject tag = new JsonObject();
        JsonArray array = new JsonArray();
        values.forEach(array::add);
        tag.add("values", array);
        return tag;
    }

    private static ResourceLocation tagFile(String registry, String tag) {
        ResourceLocation id = ResourceLocation.parse(tag);
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "tags/" + registry + "/" + id.getPath() + ".json");
    }

    private static ResourceLocation file(String path) {
        return AdAstraPlanets.id(path);
    }

    private static byte[] json(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] bytes(JsonObject json) {
        return json.toString().getBytes(StandardCharsets.UTF_8);
    }

    private GeneratedResources() {
    }
}
