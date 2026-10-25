package com.adastraplanets.content;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Content a modpack adds through {@code config/adastraplanets/content.json}, read once at startup because Minecraft
 * only lets blocks, items and entities be registered while the game loads. Everything else about them stays in
 * datapacks and resource packs (rocket definitions, recipes, worldgen, models, textures). See the README written next
 * to the file for the format.
 */
public record StartupContent(List<RocketEntry> rockets, List<OreEntry> ores, List<GlobeEntry> globes) {
    /** An extra rocket: id (under this mod's namespace), hitbox size in blocks and an optional English name. */
    public record RocketEntry(String id, float width, float height, Optional<String> name) {
        public static final Codec<RocketEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(RocketEntry::id),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("width").forGetter(RocketEntry::width),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("height").forGetter(RocketEntry::height),
                Codec.STRING.optionalFieldOf("name").forGetter(RocketEntry::name)
        ).apply(instance, RocketEntry::new));
    }

    /**
     * An extra ore block: drawn as {@code overlay} on top of {@code base}, behaving like the vanilla block named in
     * {@code properties_from} (hardness, sound), dropping {@code drop} (fortune applies; silk touch gives the ore).
     */
    public record OreEntry(String id, ResourceLocation base, ResourceLocation overlay, ResourceLocation drop,
                           ResourceLocation propertiesFrom, String toolTier, List<ResourceLocation> tags,
                           Optional<String> name) {
        public static final Codec<OreEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(OreEntry::id),
                ResourceLocation.CODEC.fieldOf("base").forGetter(OreEntry::base),
                ResourceLocation.CODEC.fieldOf("overlay").forGetter(OreEntry::overlay),
                ResourceLocation.CODEC.fieldOf("drop").forGetter(OreEntry::drop),
                ResourceLocation.CODEC.optionalFieldOf("properties_from", ResourceLocation.withDefaultNamespace("iron_ore"))
                        .forGetter(OreEntry::propertiesFrom),
                Codec.STRING.optionalFieldOf("tool_tier", "stone").forGetter(OreEntry::toolTier),
                ResourceLocation.CODEC.listOf().optionalFieldOf("tags", List.of()).forGetter(OreEntry::tags),
                Codec.STRING.optionalFieldOf("name").forGetter(OreEntry::name)
        ).apply(instance, OreEntry::new));
    }

    /** An extra desk globe of a modpack's own world: id (e.g. "frost_nova_globe") and an optional English name. */
    public record GlobeEntry(String id, Optional<String> name) {
        public static final Codec<GlobeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(GlobeEntry::id),
                Codec.STRING.optionalFieldOf("name").forGetter(GlobeEntry::name)
        ).apply(instance, GlobeEntry::new));
    }

    public static final Codec<StartupContent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketEntry.CODEC.listOf().optionalFieldOf("rockets", List.of()).forGetter(StartupContent::rockets),
            OreEntry.CODEC.listOf().optionalFieldOf("ores", List.of()).forGetter(StartupContent::ores),
            GlobeEntry.CODEC.listOf().optionalFieldOf("globes", List.of()).forGetter(StartupContent::globes)
    ).apply(instance, StartupContent::new));

    private static final StartupContent EMPTY = new StartupContent(List.of(), List.of(), List.of());
    private static StartupContent loaded = EMPTY;

    public static StartupContent get() {
        return loaded;
    }

    /** Reads (or, on first run, creates) the file. A broken file stops the game with a clear message. */
    public static StartupContent load() {
        Path dir = FMLPaths.CONFIGDIR.get().resolve(AdAstraPlanets.MOD_ID);
        Path file = dir.resolve("content.json");
        try {
            Files.createDirectories(dir);
            if (Files.notExists(file)) {
                Files.writeString(file, new GsonBuilder().setPrettyPrinting().create()
                        .toJson(CODEC.encodeStart(JsonOps.INSTANCE, EMPTY).getOrThrow()));
            }
            Path readme = dir.resolve("README.md");
            if (Files.notExists(readme)) {
                try (var in = StartupContent.class.getResourceAsStream("/assets/adastraplanets/content_readme.md")) {
                    if (in != null) {
                        Files.copy(in, readme);
                    }
                }
            }
            loaded = CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file)))
                    .getOrThrow(error -> new IllegalStateException("config/adastraplanets/content.json: " + error));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config/adastraplanets/content.json", e);
        }
        AdAstraPlanets.LOGGER.info("Startup content: {} extra rockets, {} extra ores", loaded.rockets.size(), loaded.ores.size());
        return loaded;
    }
}
