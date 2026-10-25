package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.environment.LinerProtection;
import com.adastraplanets.world.GeyserSettings;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public final class ModDataMaps {
    /** Eruption timing and strength of geyser blocks; a block without an entry never erupts. Synced to clients. */
    public static final DataMapType<Block, GeyserSettings> GEYSERS = DataMapType.builder(
            AdAstraPlanets.id("geysers"), Registries.BLOCK, GeyserSettings.CODEC).synced(GeyserSettings.CODEC, true).build();

    /** What each environmental liner piece protects against. Synced so tooltips can show it. */
    public static final DataMapType<Item, LinerProtection> LINER_PROTECTION = DataMapType.builder(
            AdAstraPlanets.id("liner_protection"), Registries.ITEM, LinerProtection.CODEC).synced(LinerProtection.CODEC, true).build();

    /** The protection of this mod's armor pieces (armor, toughness, knockback resistance). Synced for tooltips. */
    public static final DataMapType<Item, com.adastraplanets.item.ArmorStats> ARMOR_STATS = DataMapType.builder(
            AdAstraPlanets.id("armor_stats"), Registries.ITEM, com.adastraplanets.item.ArmorStats.CODEC)
            .synced(com.adastraplanets.item.ArmorStats.CODEC, true).build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ARMOR_STATS);
        event.register(GEYSERS);
        event.register(LINER_PROTECTION);
    }

    private ModDataMaps() {
    }
}
