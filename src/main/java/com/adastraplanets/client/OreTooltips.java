package com.adastraplanets.client;

import com.adastraplanets.config.AdAstraPlanetsClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adds "Found on: ..." to the items listed in the client config (see {@link AdAstraPlanetsClientConfig}). */
public final class OreTooltips {
    private static List<? extends String> parsedFrom;
    private static Map<ResourceLocation, String> locations = Map.of();

    public static void add(ItemTooltipEvent event) {
        if (!AdAstraPlanetsClientConfig.SPEC.isLoaded() || !AdAstraPlanetsClientConfig.ORE_TOOLTIPS.get()) {
            return;
        }
        String where = locations().get(BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem()));
        if (where != null && !where.isEmpty()) {
            // a translation key or plain text: an unknown key is shown as written
            event.getToolTip().add(Component.translatable("tooltip.adastraplanets.found_on", Component.translatable(where))
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * The built-in entries with the config's applied over them (an empty place hides a line), parsed again only when the
     * config's list changes (it can be edited live).
     */
    private static Map<ResourceLocation, String> locations() {
        List<? extends String> entries = AdAstraPlanetsClientConfig.ORE_LOCATIONS.get();
        if (entries != parsedFrom) {
            Map<ResourceLocation, String> map = new HashMap<>();
            for (String entry : AdAstraPlanetsClientConfig.BUILT_IN_LOCATIONS) {
                put(map, entry);
            }
            for (String entry : entries) {
                put(map, entry);
            }
            locations = map;
            parsedFrom = entries;
        }
        return locations;
    }

    private static void put(Map<ResourceLocation, String> map, String entry) {
        int split = entry.indexOf('=');
        ResourceLocation item = ResourceLocation.tryParse(entry.substring(0, split).trim());
        if (item != null) {
            map.put(item, entry.substring(split + 1).trim());
        }
    }

    private OreTooltips() {
    }
}
