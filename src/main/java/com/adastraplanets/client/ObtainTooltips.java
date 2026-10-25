package com.adastraplanets.client;

import com.adastraplanets.AdAstraPlanets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * "How to get it" under Shift, in Ad Astra's style, for every item listed in assets/adastraplanets/obtain.json (written
 * from the mod's worldgen, loot tables and recipes; a resource pack can replace it). Each
 * line is a translation key whose arguments are translation keys, numbers or lists of keys joined with commas.
 */
public final class ObtainTooltips implements ResourceManagerReloadListener {
    private static final ResourceLocation FILE = AdAstraPlanets.id("obtain.json");
    private static Map<ResourceLocation, List<Component>> hints = Map.of();

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<ResourceLocation, List<Component>> loaded = new HashMap<>();
        manager.getResource(FILE).ifPresent(resource -> {
            try (Reader reader = resource.openAsReader()) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                root.entrySet().forEach(entry -> {
                    List<Component> lines = new ArrayList<>();
                    for (JsonElement line : entry.getValue().getAsJsonArray()) {
                        lines.add(line(line.getAsJsonObject()));
                    }
                    loaded.put(ResourceLocation.parse(entry.getKey()), lines);
                });
            } catch (Exception e) {
                AdAstraPlanets.LOGGER.warn("Could not read {}", FILE, e);
            }
        });
        hints = loaded;
    }

    private static Component line(JsonObject line) {
        List<Object> args = new ArrayList<>();
        for (JsonElement arg : line.getAsJsonArray("args")) {
            args.add(arg.isJsonArray() ? joined(arg.getAsJsonArray()) : argument(arg.getAsString()));
        }
        return Component.translatable(line.get("key").getAsString(), args.toArray());
    }

    /** A number stays as written; anything else is a translation key. */
    private static Component argument(String value) {
        return value.matches("-?\\d+") ? Component.literal(value) : Component.translatable(value);
    }

    private static Component joined(JsonArray keys) {
        MutableComponent text = Component.empty();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) {
                text.append(", ");
            }
            text.append(argument(keys.get(i).getAsString()));
        }
        return text;
    }

    public static void add(ItemTooltipEvent event) {
        List<Component> lines = hints.get(BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()));
        if (lines == null || lines.isEmpty()) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        if (!net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            // items with a description of their own already show the "hold Shift" line: one is enough
            if (!tooltip.contains(ConstantComponents.SHIFT_DESCRIPTION)) {
                tooltip.add(ConstantComponents.SHIFT_DESCRIPTION);
            }
            return;
        }
        lines.forEach(line -> TooltipUtils.addDescriptionComponent(tooltip, line.copy().withStyle(ChatFormatting.GRAY)));
    }
}
