package com.adastraplanets.client.solarsystem;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.JsonOps;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.menus.PlanetsMenu;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Loads {@link SolarSystemMap}s from resource packs and draws them on Ad Astra's planet selection screen: the
 * overview of a system (replacing Ad Astra's fixed drawing when a file exists) and the close-up of one planet with its
 * moons and an information panel. Clickable icons are reported through a {@link Hit} consumer so the screen can turn
 * clicks into selections.
 */
public final class SolarSystemMaps extends SimpleJsonResourceReloadListener {
    /** Width of the information panel on the right of the close-up. */
    public static final int PANEL_WIDTH = 150;
    /** Right edge of Ad Astra's left-hand selection menu when a planet is selected. */
    public static final int LEFT_MENU_EDGE = 216;

    /** Default look of systems and bodies that no map file describes. */
    private static final ResourceLocation DEFAULT_SUN = ResourceLocation.fromNamespaceAndPath("ad_astra", "textures/environment/sun.png");
    private static final ResourceLocation UNKNOWN_ICON = ResourceLocation.fromNamespaceAndPath(AdAstraPlanets.MOD_ID, "textures/gui/planets/unknown.png");
    private static final int DEFAULT_ORBIT_GAP = 24;

    /** Map files as loaded. */
    private static volatile Map<ResourceLocation, SolarSystemMap> files = Map.of();
    /** Map files completed with the planets they miss, and generated maps for systems without a file. */
    private static volatile Map<ResourceLocation, SolarSystemMap> maps = Map.of();
    /** A copy of the planets {@link #maps} was built from; Ad Astra refills its planet map whenever the server syncs. */
    @Nullable
    private static Map<ResourceKey<Level>, Planet> builtFrom;

    /** A clickable icon drawn this frame: the destination it stands for, the planet it belongs to, its square. */
    public record Hit(ResourceLocation planet, SolarSystemMap.Body body, int x, int y, int size) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= this.x - 2 && mouseX <= this.x + this.size + 2 && mouseY >= this.y - 2 && mouseY <= this.y + this.size + 2;
        }
    }

    public SolarSystemMaps() {
        super(new Gson(), AdAstraPlanets.MOD_ID + "/solar_systems");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, SolarSystemMap> loaded = new HashMap<>();
        entries.forEach((id, json) -> SolarSystemMap.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> AdAstraPlanets.LOGGER.error("Invalid solar system map {}: {}", id, error))
                .ifPresent(map -> loaded.put(id, map)));
        files = Map.copyOf(loaded);
        builtFrom = null;
        AdAstraPlanets.LOGGER.info("Loaded {} solar system maps", loaded.size());
    }

    public static boolean has(@Nullable ResourceLocation solarSystem) {
        return solarSystem != null && maps().containsKey(solarSystem);
    }

    @Nullable
    public static SolarSystemMap get(@Nullable ResourceLocation solarSystem) {
        return solarSystem == null ? null : maps().get(solarSystem);
    }

    /**
     * Every solar system's map: its file, with the system's planets that the file does not show added on outer orbits
     * (unless the file turns that off), or for systems with no file a generated map of all their planets, so systems
     * and planets added by datapacks or KubeJS can be clicked without any client resources.
     */
    private static Map<ResourceLocation, SolarSystemMap> maps() {
        Map<ResourceKey<Level>, Planet> planets = AdAstraData.planets();
        if (!planets.equals(builtFrom)) {
            Map<ResourceLocation, SolarSystemMap> built = new HashMap<>(files);
            Map<ResourceLocation, List<Planet>> bySystem = new HashMap<>();
            planets.values().stream().filter(planet -> !planet.isSpace())
                    .sorted(Comparator.comparingInt(Planet::tier).thenComparing(planet -> planet.dimension().location().getPath()))
                    .forEach(planet -> bySystem.computeIfAbsent(planet.solarSystem(), id -> new ArrayList<>()).add(planet));
            bySystem.forEach((system, members) -> {
                SolarSystemMap file = files.get(system);
                if (file == null) {
                    built.put(system, complete(new SolarSystemMap(DEFAULT_SUN, 16, 0xFF24327B, DEFAULT_ORBIT_GAP, 6, List.of(), true), members));
                } else if (file.addMissingPlanets()) {
                    built.put(system, complete(file, members));
                }
            });
            maps = Map.copyOf(built);
            builtFrom = Map.copyOf(planets);
        }
        return maps;
    }

    /** Adds the planets no body or moon of the map links to, one per orbit beyond the outermost. */
    private static SolarSystemMap complete(SolarSystemMap map, List<Planet> members) {
        Set<ResourceLocation> shown = new HashSet<>();
        for (SolarSystemMap.Body body : map.planets()) {
            body.planet().ifPresent(shown::add);
            body.moons().forEach(moon -> moon.planet().ifPresent(shown::add));
        }
        List<SolarSystemMap.Body> bodies = new ArrayList<>(map.planets());
        int orbit = map.planets().stream().mapToInt(SolarSystemMap.Body::orbit).max().orElse(0);
        for (Planet planet : members) {
            ResourceLocation id = planet.dimension().location();
            if (shown.contains(id)) {
                continue;
            }
            orbit++;
            // farther planets circle more slowly, as on Ad Astra's own map
            float speed = 2.4F / (float) Math.sqrt(orbit);
            float phase = Math.floorMod(id.hashCode(), 360);
            bodies.add(new SolarSystemMap.Body(icon(id), orbit, 12, speed, phase, List.of(), Optional.of(id), List.of()));
        }
        if (bodies.size() == map.planets().size()) {
            return map;
        }
        return new SolarSystemMap(map.sun(), map.sunSize(), map.orbitColor(), map.orbitGap(), map.moonGap(), List.copyOf(bodies),
                map.addMissingPlanets());
    }

    /**
     * A planet's icon by convention: {@code <namespace>:textures/gui/planets/<path>.png}, else Ad Astra's environment
     * texture of the same name, else a generic icon.
     */
    private static ResourceLocation icon(ResourceLocation planet) {
        ResourceManager resources = Minecraft.getInstance().getResourceManager();
        for (ResourceLocation candidate : List.of(
                planet.withPath("textures/gui/planets/" + planet.getPath() + ".png"),
                ResourceLocation.fromNamespaceAndPath("ad_astra", "textures/environment/" + planet.getPath() + ".png"))) {
            if (resources.getResource(candidate).isPresent()) {
                return candidate;
            }
        }
        return UNKNOWN_ICON;
    }

    /** The planet-level body of a system that stands for this destination or has it as a moon. */
    public static Optional<SolarSystemMap.Body> bodyFor(@Nullable ResourceLocation solarSystem, ResourceLocation planet) {
        SolarSystemMap map = get(solarSystem);
        if (map == null) {
            return Optional.empty();
        }
        return map.planets().stream().filter(body -> body.planet().map(planet::equals).orElse(false)
                || body.moons().stream().anyMatch(moon -> moon.planet().map(planet::equals).orElse(false))).findFirst();
    }

    /** Whether this destination is drawn as a moon on some map (moons are reached from their planet's close-up). */
    public static boolean isMoon(ResourceLocation planet) {
        return maps().values().stream().flatMap(map -> map.planets().stream()).flatMap(body -> body.moons().stream())
                .anyMatch(moon -> moon.planet().map(planet::equals).orElse(false));
    }

    /** How far the map reaches from its sun, in GUI pixels: the outermost orbit plus its body and moons. */
    public static float radius(SolarSystemMap map) {
        float radius = map.sunSize() / 2F;
        for (SolarSystemMap.Body body : map.planets()) {
            float reach = body.size() / 2F;
            for (SolarSystemMap.Moon moon : body.moons()) {
                reach = Math.max(reach, body.size() / 2F + moon.orbit() * map.moonGap() + moon.size() / 2F);
            }
            radius = Math.max(radius, body.orbit() * map.orbitGap() + reach);
        }
        return radius + 2;
    }

    // ------------------------------------------------------------------ overview

    public static void render(GuiGraphics graphics, ResourceLocation solarSystem, int width, int height, Consumer<Hit> hits) {
        SolarSystemMap map = get(solarSystem);
        if (map == null) {
            return;
        }
        float cx = width / 2F;
        float cy = height / 2F;
        rings(map.planets().stream().mapToInt(SolarSystemMap.Body::orbit).distinct().mapToDouble(orbit -> orbit * map.orbitGap()),
                cx, cy, map.orbitColor());
        int sun = map.sunSize();
        graphics.blit(map.sun(), Math.round(cx - sun / 2F), Math.round(cy - sun / 2F), 0, 0, sun, sun, sun, sun);

        float time = Util.getMillis() / 100F;
        for (SolarSystemMap.Body body : map.planets()) {
            double angle = Math.toRadians(body.phase() + time * body.speed());
            float bx = cx + (float) Math.cos(angle) * body.orbit() * map.orbitGap();
            float by = cy + (float) Math.sin(angle) * body.orbit() * map.orbitGap();
            int x = Math.round(bx - body.size() / 2F);
            int y = Math.round(by - body.size() / 2F);
            graphics.blit(body.texture(), x, y, 0, 0, body.size(), body.size(), body.size(), body.size());
            body.planet().ifPresent(planet -> hits.accept(new Hit(planet, body, x, y, body.size())));
            for (SolarSystemMap.Moon moon : body.moons()) {
                double moonAngle = Math.toRadians(moon.phase() + time * moon.speed());
                float distance = body.size() / 2F + moon.orbit() * map.moonGap();
                int mx = Math.round(bx + (float) Math.cos(moonAngle) * distance - moon.size() / 2F);
                int my = Math.round(by + (float) Math.sin(moonAngle) * distance - moon.size() / 2F);
                graphics.blit(moon.texture(), mx, my, 0, 0, moon.size(), moon.size(), moon.size(), moon.size());
            }
        }
    }

    // ------------------------------------------------------------------ close-up

    /**
     * One planet large in the middle of the free space, its moons circling it on their own rings (clickable), the
     * selected body outlined, and an information panel on the right.
     */
    public static void renderFocus(GuiGraphics graphics, SolarSystemMap map, SolarSystemMap.Body body, @Nullable ResourceLocation selected,
                                   PlanetsMenu menu, int width, int height, Consumer<Hit> hits) {
        float cx = (LEFT_MENU_EDGE + (width - PANEL_WIDTH - 10)) / 2F;
        float cy = height / 2F;
        int planetSize = 40;
        int moonSize = 14;
        float firstRing = planetSize / 2F + 18;
        float ringGap = 18;

        rings(body.moons().stream().mapToDouble(moon -> firstRing + (moon.orbit() - 1) * ringGap), cx, cy, map.orbitColor());
        int px = Math.round(cx - planetSize / 2F);
        int py = Math.round(cy - planetSize / 2F);
        graphics.blit(body.texture(), px, py, 0, 0, planetSize, planetSize, planetSize, planetSize);
        body.planet().ifPresent(planet -> {
            hits.accept(new Hit(planet, body, px, py, planetSize));
            if (planet.equals(selected)) {
                outline(graphics, px, py, planetSize);
            }
        });

        float time = Util.getMillis() / 100F;
        for (SolarSystemMap.Moon moon : body.moons()) {
            double angle = Math.toRadians(moon.phase() + time * moon.speed() / 4F);
            float distance = firstRing + (moon.orbit() - 1) * ringGap;
            int mx = Math.round(cx + (float) Math.cos(angle) * distance - moonSize / 2F);
            int my = Math.round(cy + (float) Math.sin(angle) * distance - moonSize / 2F);
            graphics.blit(moon.texture(), mx, my, 0, 0, moonSize, moonSize, moonSize, moonSize);
            moon.planet().ifPresent(planet -> {
                hits.accept(new Hit(planet, body, mx, my, moonSize));
                if (planet.equals(selected)) {
                    outline(graphics, mx, my, moonSize);
                }
            });
        }

        ResourceLocation shown = selected != null ? selected : body.planet().orElse(null);
        if (shown != null) {
            infoPanel(graphics, shown, body, menu, width, height);
        }
    }

    private static void infoPanel(GuiGraphics graphics, ResourceLocation planetId, SolarSystemMap.Body body, PlanetsMenu menu,
                                  int width, int height) {
        Font font = Minecraft.getInstance().font;
        int x0 = width - PANEL_WIDTH - 10;
        int y0 = height / 2 - 88;
        int x1 = width - 10;
        int y1 = height / 2 + 89;
        graphics.fill(x0, y0, x1, y1, 0xD0080B1E);
        graphics.renderOutline(x0, y0, x1 - x0, y1 - y0, 0xFFFFFFFF);

        int y = y0 + 8;
        graphics.drawCenteredString(font, name(planetId), (x0 + x1) / 2, y, 0xFFFFFF);
        y += 14;
        if (!planetId.equals(body.planet().orElse(null))) {
            graphics.drawCenteredString(font, Component.translatable("gui.adastraplanets.moon_of", name(body.planet().orElse(planetId)))
                    .withStyle(ChatFormatting.GRAY), (x0 + x1) / 2, y, 0xFFFFFF);
            y += 14;
        }
        y += 4;

        Planet planet = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, planetId));
        if (planet == null) {
            line(graphics, font, x0, y, Component.translatable("gui.adastraplanets.status.unexplored").withStyle(ChatFormatting.GRAY));
            return;
        }
        line(graphics, font, x0, y, label("tier", String.valueOf(planet.tier())));
        y += 12;
        line(graphics, font, x0, y, label("gravity", String.format(Locale.ROOT, "%.2f m/s²", planet.gravity())));
        y += 12;
        line(graphics, font, x0, y, label("temperature", planet.temperature() + " °C"));
        y += 12;
        line(graphics, font, x0, y, Component.translatable("gui.adastraplanets.oxygen").append(": ")
                .append(Component.translatable(planet.oxygen() ? "gui.adastraplanets.yes" : "gui.adastraplanets.no")));
        y += 12;
        line(graphics, font, x0, y, label("solar_power", String.valueOf(planet.solarPower())));
        y += 18;

        Component status;
        if (menu.disabledPlanets().contains(planetId)) {
            status = Component.translatable("gui.adastraplanets.status.unavailable").withStyle(ChatFormatting.GRAY);
        } else if (menu.tier() < planet.tier()) {
            status = Component.translatable("gui.adastraplanets.status.requires_tier", planet.tier()).withStyle(ChatFormatting.RED);
        } else {
            status = Component.translatable("gui.adastraplanets.status.ready").withStyle(ChatFormatting.GREEN);
        }
        graphics.drawWordWrap(font, status, x0 + 8, y, PANEL_WIDTH - 16, 0xFFFFFF);
        y += font.wordWrapHeight(status, PANEL_WIDTH - 16) + 8;
        resources(graphics, font, resourcesOf(planetId, body), x0, y);
    }

    /** What the map lists as worth gathering on this destination: the body's own list, or the matching moon's. */
    private static List<ResourceLocation> resourcesOf(ResourceLocation planetId, SolarSystemMap.Body body) {
        if (planetId.equals(body.planet().orElse(null))) {
            return body.resources();
        }
        return body.moons().stream().filter(moon -> planetId.equals(moon.planet().orElse(null)))
                .findFirst().map(SolarSystemMap.Moon::resources).orElse(List.of());
    }

    /** A "Resources" heading, then each item's icon and name on its own line. */
    private static void resources(GuiGraphics graphics, Font font, List<ResourceLocation> items, int x0, int y) {
        if (items.isEmpty()) {
            return;
        }
        line(graphics, font, x0, y, Component.translatable("gui.adastraplanets.resources").withStyle(ChatFormatting.GOLD));
        y += 12;
        for (ResourceLocation id : items) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item == Items.AIR) {
                continue;
            }
            ItemStack stack = new ItemStack(item);
            graphics.renderItem(stack, x0 + 8, y - 4);
            graphics.drawString(font, stack.getHoverName(), x0 + 28, y, 0xFFFFFF);
            y += 18;
        }
    }

    public static Component name(ResourceLocation planet) {
        return Component.translatableWithFallback("planet.%s.%s".formatted(planet.getNamespace(), planet.getPath()),
                title(planet.getPath()));
    }

    private static String title(String path) {
        StringBuilder out = new StringBuilder();
        for (String word : path.split("_")) {
            if (!word.isEmpty()) {
                out.append(out.isEmpty() ? "" : " ").append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return out.toString();
    }

    private static Component label(String key, String value) {
        return Component.translatable("gui.adastraplanets." + key).append(": ").append(Component.literal(value).withStyle(ChatFormatting.AQUA));
    }

    private static void line(GuiGraphics graphics, Font font, int x0, int y, Component text) {
        graphics.drawString(font, text, x0 + 8, y, 0xFFFFFF);
    }

    private static void outline(GuiGraphics graphics, int x, int y, int size) {
        graphics.renderOutline(x - 2, y - 2, size + 4, size + 4, 0xFFFFFFFF);
    }

    private static void rings(java.util.stream.DoubleStream radii, float cx, float cy, int colour) {
        double[] values = radii.toArray();
        if (values.length == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder lines = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (double radius : values) {
            ring(lines, cx, cy, (float) radius, colour);
        }
        BufferUploader.drawWithShader(lines.buildOrThrow());
    }

    /** A one-pixel ring made of short line segments. */
    private static void ring(BufferBuilder lines, float cx, float cy, float radius, int colour) {
        int sides = 75;
        for (float r = radius - 0.5F; r <= radius + 0.5F; r += 0.1F) {
            for (int i = 0; i < sides; i++) {
                double a = i * 2 * Math.PI / sides;
                double b = (i + 1) * 2 * Math.PI / sides;
                lines.addVertex((float) (cx + r * Math.cos(a)), (float) (cy + r * Math.sin(a)), 0).setColor(colour);
                lines.addVertex((float) (cx + r * Math.cos(b)), (float) (cy + r * Math.sin(b)), 0).setColor(colour);
            }
        }
    }
}
