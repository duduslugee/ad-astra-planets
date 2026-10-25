package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.rocket.TieredRocketItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AdAstraPlanets.MOD_ID);

    public static final DeferredItem<BlockItem> HELIONITE_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.HELIONITE_ORE);
    public static final DeferredItem<BlockItem> HELIONITE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.HELIONITE_BLOCK);
    public static final DeferredItem<BlockItem> RAW_HELIONITE_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.RAW_HELIONITE_BLOCK);

    public static final DeferredItem<BlockItem> LARGE_LAUNCH_PAD = ITEMS.registerSimpleBlockItem(ModBlocks.LARGE_LAUNCH_PAD);
    public static final DeferredItem<BlockItem> HUGE_LAUNCH_PAD = ITEMS.registerSimpleBlockItem(ModBlocks.HUGE_LAUNCH_PAD);

    public static final DeferredItem<BlockItem> CARBON_REGOLITH = ITEMS.registerSimpleBlockItem(ModBlocks.CARBON_REGOLITH);
    public static final DeferredItem<BlockItem> CARBON_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.CARBON_ROCK);
    public static final DeferredItem<BlockItem> PALE_DUST = ITEMS.registerSimpleBlockItem(ModBlocks.PALE_DUST);
    public static final DeferredItem<BlockItem> PALE_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.PALE_ROCK);
    public static final DeferredItem<BlockItem> SULFUROUS_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.SULFUROUS_ROCK);
    public static final DeferredItem<BlockItem> SULFUROUS_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.SULFUROUS_COBBLESTONE);
    public static final DeferredItem<BlockItem> SULFUR_CRUST = ITEMS.registerSimpleBlockItem(ModBlocks.SULFUR_CRUST);
    public static final DeferredItem<BlockItem> CINDER = ITEMS.registerSimpleBlockItem(ModBlocks.CINDER);
    public static final DeferredItem<BlockItem> SULFUR_VENT = ITEMS.registerSimpleBlockItem(ModBlocks.SULFUR_VENT);
    public static final DeferredItem<BlockItem> SULCUS_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.SULCUS_ROCK);
    public static final DeferredItem<BlockItem> SULCUS_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.SULCUS_COBBLESTONE);
    public static final DeferredItem<BlockItem> DARK_REGOLITH = ITEMS.registerSimpleBlockItem(ModBlocks.DARK_REGOLITH);
    public static final DeferredItem<BlockItem> GROOVE_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.GROOVE_ICE);
    public static final DeferredItem<BlockItem> GLACIAL_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.GLACIAL_ROCK);
    public static final DeferredItem<BlockItem> GLACIAL_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.GLACIAL_COBBLESTONE);
    public static final DeferredItem<BlockItem> PLUME_SNOW = ITEMS.registerSimpleBlockItem(ModBlocks.PLUME_SNOW);
    public static final DeferredItem<BlockItem> FISSURE_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.FISSURE_ICE);
    public static final DeferredItem<BlockItem> ICEBOUND_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.ICEBOUND_ROCK);
    public static final DeferredItem<BlockItem> ICEBOUND_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.ICEBOUND_COBBLESTONE);
    public static final DeferredItem<BlockItem> FROST_REGOLITH = ITEMS.registerSimpleBlockItem(ModBlocks.FROST_REGOLITH);
    public static final DeferredItem<BlockItem> VEILED_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.VEILED_ROCK);
    public static final DeferredItem<BlockItem> VEILED_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.VEILED_COBBLESTONE);
    public static final DeferredItem<BlockItem> UMBER_DUST = ITEMS.registerSimpleBlockItem(ModBlocks.UMBER_DUST);
    public static final DeferredItem<BlockItem> HAZED_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.HAZED_ROCK);
    public static final DeferredItem<BlockItem> HAZED_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.HAZED_COBBLESTONE);
    public static final DeferredItem<BlockItem> THOLIN_SAND = ITEMS.registerSimpleBlockItem(ModBlocks.THOLIN_SAND);
    public static final DeferredItem<BlockItem> ICE_PEBBLES = ITEMS.registerSimpleBlockItem(ModBlocks.ICE_PEBBLES);
    public static final DeferredItem<BlockItem> COLUMN_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.COLUMN_ROCK);
    public static final DeferredItem<BlockItem> COLUMN_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.COLUMN_COBBLESTONE);
    public static final DeferredItem<BlockItem> AMMONIA_FROST = ITEMS.registerSimpleBlockItem(ModBlocks.AMMONIA_FROST);
    public static final DeferredItem<BlockItem> MIRROR_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.MIRROR_ROCK);
    public static final DeferredItem<BlockItem> MIRROR_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.MIRROR_COBBLESTONE);
    public static final DeferredItem<BlockItem> DRIFT_SNOW = ITEMS.registerSimpleBlockItem(ModBlocks.DRIFT_SNOW);
    public static final DeferredItem<BlockItem> ICE_SHARD = ITEMS.registerSimpleBlockItem(ModBlocks.ICE_SHARD);
    public static final DeferredItem<BlockItem> FRACTURE_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.FRACTURE_ROCK);
    public static final DeferredItem<BlockItem> FRACTURE_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.FRACTURE_COBBLESTONE);
    public static final DeferredItem<BlockItem> BRIGHT_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.BRIGHT_ICE);
    public static final DeferredItem<BlockItem> GLACIER_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.GLACIER_ROCK);
    public static final DeferredItem<BlockItem> GLACIER_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.GLACIER_COBBLESTONE);
    public static final DeferredItem<BlockItem> CELL_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.CELL_ICE);
    public static final DeferredItem<BlockItem> METHANE_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.METHANE_ICE);
    public static final DeferredItem<BlockItem> SLATE_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.SLATE_ROCK);
    public static final DeferredItem<BlockItem> SLATE_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.SLATE_COBBLESTONE);
    public static final DeferredItem<BlockItem> CRIMSON_FROST = ITEMS.registerSimpleBlockItem(ModBlocks.CRIMSON_FROST);
    public static final DeferredItem<BlockItem> GLOOM_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.GLOOM_ROCK);
    public static final DeferredItem<BlockItem> GLOOM_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.GLOOM_COBBLESTONE);
    public static final DeferredItem<BlockItem> DEEP_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.DEEP_ICE);
    public static final DeferredItem<BlockItem> GLOOM_CRYSTAL = ITEMS.registerSimpleBlockItem(ModBlocks.GLOOM_CRYSTAL);
    public static final DeferredItem<BlockItem> CRYO_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.CRYO_ROCK);
    public static final DeferredItem<BlockItem> CRYO_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.CRYO_COBBLESTONE);
    public static final DeferredItem<BlockItem> NITROGEN_FROST = ITEMS.registerSimpleBlockItem(ModBlocks.NITROGEN_FROST);
    public static final DeferredItem<BlockItem> NITROGEN_VENT = ITEMS.registerSimpleBlockItem(ModBlocks.NITROGEN_VENT);
    public static final DeferredItem<BlockItem> MANTLE_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.MANTLE_ROCK);
    public static final DeferredItem<BlockItem> MANTLE_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.MANTLE_COBBLESTONE);
    public static final DeferredItem<BlockItem> AZURE_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.AZURE_ICE);
    public static final DeferredItem<BlockItem> PRESSURE_CRYSTAL = ITEMS.registerSimpleBlockItem(ModBlocks.PRESSURE_CRYSTAL);
    public static final DeferredItem<BlockItem> MURK_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.MURK_ROCK);
    public static final DeferredItem<BlockItem> MURK_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.MURK_COBBLESTONE);
    public static final DeferredItem<BlockItem> RIFT_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.RIFT_ROCK);
    public static final DeferredItem<BlockItem> RIFT_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.RIFT_COBBLESTONE);
    public static final DeferredItem<BlockItem> PATCHWORK_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.PATCHWORK_ROCK);
    public static final DeferredItem<BlockItem> PATCHWORK_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.PATCHWORK_COBBLESTONE);
    public static final DeferredItem<BlockItem> BANDED_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.BANDED_ICE);
    public static final DeferredItem<BlockItem> AGED_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.AGED_ROCK);
    public static final DeferredItem<BlockItem> AGED_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.AGED_COBBLESTONE);
    public static final DeferredItem<BlockItem> ANCIENT_DUST = ITEMS.registerSimpleBlockItem(ModBlocks.ANCIENT_DUST);
    public static final DeferredItem<BlockItem> CLOUD_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.CLOUD_ROCK);
    public static final DeferredItem<BlockItem> CLOUD_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.CLOUD_COBBLESTONE);
    public static final DeferredItem<BlockItem> ZONE_FROST = ITEMS.registerSimpleBlockItem(ModBlocks.ZONE_FROST);
    public static final DeferredItem<BlockItem> BELT_FROST = ITEMS.registerSimpleBlockItem(ModBlocks.BELT_FROST);
    public static final DeferredItem<BlockItem> ZONE_FROST_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.ZONE_FROST_SLAB);
    public static final DeferredItem<BlockItem> BELT_FROST_SLAB = ITEMS.registerSimpleBlockItem(ModBlocks.BELT_FROST_SLAB);
    public static final DeferredItem<BlockItem> CARBON_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.CARBON_COBBLESTONE);
    public static final DeferredItem<BlockItem> PALE_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.PALE_COBBLESTONE);
    public static final DeferredItem<BlockItem> ABYSSAL_COBBLESTONE = ITEMS.registerSimpleBlockItem(ModBlocks.ABYSSAL_COBBLESTONE);

    public static final DeferredItem<BlockItem> CRUST_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.CRUST_ICE);
    public static final DeferredItem<BlockItem> RUSTED_ICE = ITEMS.registerSimpleBlockItem(ModBlocks.RUSTED_ICE);
    public static final DeferredItem<BlockItem> RIME = ITEMS.registerSimpleBlockItem(ModBlocks.RIME);
    public static final DeferredItem<BlockItem> ABYSSAL_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.ABYSSAL_ROCK);
    public static final DeferredItem<BlockItem> CHIMNEY_ROCK = ITEMS.registerSimpleBlockItem(ModBlocks.CHIMNEY_ROCK);
    public static final DeferredItem<BlockItem> HYDROTHERMAL_VENT = ITEMS.registerSimpleBlockItem(ModBlocks.HYDROTHERMAL_VENT);
    public static final DeferredItem<BlockItem> GEYSER = ITEMS.registerSimpleBlockItem(ModBlocks.GEYSER);
    public static final DeferredItem<BlockItem> ABYSSIUM_ORE = ITEMS.registerSimpleBlockItem(ModBlocks.ABYSSIUM_ORE);
    public static final DeferredItem<BlockItem> ABYSSIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.ABYSSIUM_BLOCK);
    public static final DeferredItem<BlockItem> RAW_ABYSSIUM_BLOCK = ITEMS.registerSimpleBlockItem(ModBlocks.RAW_ABYSSIUM_BLOCK);

    public static final DeferredItem<Item> RAW_HELIONITE = ITEMS.registerSimpleItem("raw_helionite");
    public static final DeferredItem<Item> HELIONITE_INGOT = ITEMS.registerSimpleItem("helionite_ingot");
    public static final DeferredItem<Item> HELIONITE_NUGGET = ITEMS.registerSimpleItem("helionite_nugget");
    public static final DeferredItem<Item> HELIONITE_PLATE = ITEMS.registerSimpleItem("helionite_plate");
    public static final DeferredItem<Item> HELIONITE_ENGINE = ITEMS.registerSimpleItem("helionite_engine");
    // Tier 5 rocket parts made from the Mars moons' materials
    public static final DeferredItem<Item> UMBRITE_FIN = ITEMS.registerSimpleItem("umbrite_fin");
    public static final DeferredItem<Item> UMBRITE_TANK = ITEMS.registerSimpleItem("umbrite_tank");
    public static final DeferredItem<Item> PALLORITE_NOSE_CONE = ITEMS.registerSimpleItem("pallorite_nose_cone");
    // Tier 6 rocket parts made from the Jupiter system's materials
    public static final DeferredItem<Item> IGNEUM_FIN = ITEMS.registerSimpleItem("igneum_fin");
    public static final DeferredItem<Item> EONYX_NOSE_CONE = ITEMS.registerSimpleItem("eonyx_nose_cone");
    public static final DeferredItem<Item> STORMIUM_ENGINE = ITEMS.registerSimpleItem("stormium_engine");
    public static final DeferredItem<Item> ABYSSIUM_TANK = ITEMS.registerSimpleItem("abyssium_tank");
    // Tier 7 rocket parts made from the Saturn system's materials
    public static final DeferredItem<Item> NIVALUM_TANK = ITEMS.registerSimpleItem("nivalum_tank");
    public static final DeferredItem<Item> FERROLITH_NOSE_CONE = ITEMS.registerSimpleItem("ferrolith_nose_cone");
    public static final DeferredItem<Item> VESPERINE_FIN = ITEMS.registerSimpleItem("vesperine_fin");
    public static final DeferredItem<Item> THOLINAR_ENGINE = ITEMS.registerSimpleItem("tholinar_engine");
    // Tier 8 rocket parts made from the Uranus system's materials
    public static final DeferredItem<Item> MOSAICON_NOSE_CONE = ITEMS.registerSimpleItem("mosaicon_nose_cone");
    public static final DeferredItem<Item> CHASMAL_TANK = ITEMS.registerSimpleItem("chasmal_tank");
    public static final DeferredItem<Item> OBSCUREX_FIN = ITEMS.registerSimpleItem("obscurex_fin");
    public static final DeferredItem<Item> AZURANE_ENGINE = ITEMS.registerSimpleItem("azurane_engine");
    /** Uranus: diamond squeezed out of its icy mantle, mined from the pressure crystal pinnacles. */
    public static final DeferredItem<Item> PRESSURE_DIAMOND = ITEMS.registerSimpleItem("pressure_diamond");
    // Tier 9 rocket parts made from the Neptune system's materials
    public static final DeferredItem<Item> NITRALIS_NOSE_CONE = ITEMS.registerSimpleItem("nitralis_nose_cone");
    public static final DeferredItem<Item> NITRALIS_FIN = ITEMS.registerSimpleItem("nitralis_fin");
    public static final DeferredItem<Item> TEMPESTINE_TANK = ITEMS.registerSimpleItem("tempestine_tank");
    public static final DeferredItem<Item> TEMPESTINE_ENGINE = ITEMS.registerSimpleItem("tempestine_engine");
    // Tier 10 rocket parts made from the Pluto system's materials
    public static final DeferredItem<Item> MORDRINE_NOSE_CONE = ITEMS.registerSimpleItem("mordrine_nose_cone");
    public static final DeferredItem<Item> MORDRINE_FIN = ITEMS.registerSimpleItem("mordrine_fin");
    public static final DeferredItem<Item> CORDIUM_TANK = ITEMS.registerSimpleItem("cordium_tank");
    public static final DeferredItem<Item> CORDIUM_ENGINE = ITEMS.registerSimpleItem("cordium_engine");

    /** Debug: counts the ores around the player (creative or operators only). */
    public static final DeferredItem<com.adastraplanets.debug.OreScannerItem> ORE_SCANNER = ITEMS.register("ore_scanner",
            () -> new com.adastraplanets.debug.OreScannerItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> RAW_ABYSSIUM = ITEMS.registerSimpleItem("raw_abyssium");
    public static final DeferredItem<Item> ABYSSIUM_INGOT = ITEMS.registerSimpleItem("abyssium_ingot");
    public static final DeferredItem<Item> ABYSSIUM_NUGGET = ITEMS.registerSimpleItem("abyssium_nugget");
    public static final DeferredItem<Item> ABYSSIUM_PLATE = ITEMS.registerSimpleItem("abyssium_plate");

    /** Environmental liner pieces, worn in the Curios liner slots; their protection comes from a data map. */
    public static final List<String> LINER_PIECES = List.of("hood", "vest", "leggings", "boots");
    public static final int LINER_LEVELS = 5;
    private static final Map<String, DeferredItem<Item>> LINERS = new LinkedHashMap<>();

    static {
        for (int level = 1; level <= LINER_LEVELS; level++) {
            for (String piece : LINER_PIECES) {
                String id = linerId(piece, level);
                LINERS.put(id, ITEMS.registerSimpleItem(id, new Item.Properties().stacksTo(1)));
            }
        }
    }

    public static String linerId(String piece, int level) {
        return "liner_" + piece + "_" + level;
    }

    public static Item liner(String piece, int level) {
        return LINERS.get(linerId(piece, level)).get();
    }

    private static final Map<String, DeferredItem<TieredRocketItem>> ROCKET_ITEMS = new LinkedHashMap<>();
    private static final List<DeferredItem<BlockItem>> EXTRA_ORES = new ArrayList<>();

    public static final DeferredItem<TieredRocketItem> TIER_5_ROCKET = rocket("tier_5_rocket");
    public static final DeferredItem<TieredRocketItem> TIER_6_ROCKET = rocket("tier_6_rocket");
    public static final DeferredItem<TieredRocketItem> TIER_7_ROCKET = rocket("tier_7_rocket");
    public static final DeferredItem<TieredRocketItem> TIER_8_ROCKET = rocket("tier_8_rocket");
    public static final DeferredItem<TieredRocketItem> TIER_9_ROCKET = rocket("tier_9_rocket");
    public static final DeferredItem<TieredRocketItem> TIER_10_ROCKET = rocket("tier_10_rocket");
    public static final DeferredItem<TieredRocketItem> ANCIENT_SHIP = rocket("ancient_ship");

    /** Registers the item that places the rocket entity with the same id (see {@link ModEntityTypes#rocket}). */
    public static DeferredItem<TieredRocketItem> rocket(String id) {
        DeferredItem<TieredRocketItem> item = ITEMS.register(id, () -> new TieredRocketItem(
                () -> ModEntityTypes.rockets().get(AdAstraPlanets.id(id)).get(), new Item.Properties().stacksTo(1).fireResistant()));
        ROCKET_ITEMS.put(id, item);
        return item;
    }

    public static TieredRocketItem rocketItem(String id) {
        return ROCKET_ITEMS.get(id).get();
    }

    public static Collection<DeferredItem<TieredRocketItem>> rockets() {
        return Collections.unmodifiableCollection(ROCKET_ITEMS.values());
    }

    public static final DeferredItem<BlockItem> ADVANCED_NASA_WORKBENCH = ITEMS.registerSimpleBlockItem(ModBlocks.ADVANCED_NASA_WORKBENCH);
    public static final DeferredItem<BlockItem> STELLAR_NASA_WORKBENCH = ITEMS.registerSimpleBlockItem(ModBlocks.STELLAR_NASA_WORKBENCH);

    public static void extraOre(DeferredBlock<? extends Block> block) {
        EXTRA_ORES.add(ITEMS.registerSimpleBlockItem(block));
    }

    private ModItems() {
    }
}
