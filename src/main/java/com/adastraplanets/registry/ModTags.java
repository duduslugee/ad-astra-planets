package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;

public final class ModTags {
    public static final class Blocks {
        public static final TagKey<Block> ORES_HELIONITE = common("ores/helionite");
        /** Launch pads big enough for the largest rockets (tier 6 and up only accept these). */
        public static final TagKey<Block> LARGE_LAUNCH_PADS = TagKey.create(Registries.BLOCK, AdAstraPlanets.id("large_launch_pads"));
        /** 7x7 pads; they also count as large pads, since a big pad takes any smaller rocket. */
        public static final TagKey<Block> HUGE_LAUNCH_PADS = TagKey.create(Registries.BLOCK, AdAstraPlanets.id("huge_launch_pads"));
        public static final TagKey<Block> STORAGE_BLOCKS_HELIONITE = common("storage_blocks/helionite");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_HELIONITE = common("storage_blocks/raw_helionite");
        public static final TagKey<Block> ORES_ABYSSIUM = common("ores/abyssium");
        public static final TagKey<Block> STORAGE_BLOCKS_ABYSSIUM = common("storage_blocks/abyssium");
        public static final TagKey<Block> STORAGE_BLOCKS_RAW_ABYSSIUM = common("storage_blocks/raw_abyssium");
        /** What a hydrothermal vent may build over and seed with ore (see HydrothermalVentFeature). */
        public static final TagKey<Block> HYDROTHERMAL_VENT_REPLACEABLE = TagKey.create(Registries.BLOCK, AdAstraPlanets.id("hydrothermal_vent_replaceable"));

        private static TagKey<Block> common(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }

    public static final class Items {
        /** What opens the lost expedition's access readers. */
        public static final TagKey<Item> ACCESS_CARDS = TagKey.create(Registries.ITEM, AdAstraPlanets.id("access_cards"));
        /** Armor that, worn as a full set, needs no environmental liner anywhere. */
        public static final TagKey<Item> ENVIRONMENT_PROOF_ARMOR = TagKey.create(Registries.ITEM, AdAstraPlanets.id("environment_proof_armor"));
        public static final TagKey<Item> ORES_HELIONITE = common("ores/helionite");
        public static final TagKey<Item> STORAGE_BLOCKS_HELIONITE = common("storage_blocks/helionite");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_HELIONITE = common("storage_blocks/raw_helionite");
        public static final TagKey<Item> RAW_MATERIALS_HELIONITE = common("raw_materials/helionite");
        public static final TagKey<Item> INGOTS_HELIONITE = common("ingots/helionite");
        public static final TagKey<Item> NUGGETS_HELIONITE = common("nuggets/helionite");
        public static final TagKey<Item> PLATES = common("plates");
        public static final TagKey<Item> PLATES_HELIONITE = common("plates/helionite");
        public static final TagKey<Item> ORES_ABYSSIUM = common("ores/abyssium");
        public static final TagKey<Item> STORAGE_BLOCKS_ABYSSIUM = common("storage_blocks/abyssium");
        public static final TagKey<Item> STORAGE_BLOCKS_RAW_ABYSSIUM = common("storage_blocks/raw_abyssium");
        public static final TagKey<Item> RAW_MATERIALS_ABYSSIUM = common("raw_materials/abyssium");
        public static final TagKey<Item> INGOTS_ABYSSIUM = common("ingots/abyssium");
        public static final TagKey<Item> NUGGETS_ABYSSIUM = common("nuggets/abyssium");
        public static final TagKey<Item> PLATES_ABYSSIUM = common("plates/abyssium");

        private static TagKey<Item> common(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }

    public static final class Fluids {
        public static final TagKey<Fluid> TIER_5_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_5_rocket_fuel"));
        public static final TagKey<Fluid> TIER_6_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_6_rocket_fuel"));
        public static final TagKey<Fluid> TIER_7_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_7_rocket_fuel"));
        public static final TagKey<Fluid> TIER_8_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_8_rocket_fuel"));
        public static final TagKey<Fluid> TIER_9_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_9_rocket_fuel"));
        public static final TagKey<Fluid> TIER_10_ROCKET_FUEL = TagKey.create(Registries.FLUID, AdAstraPlanets.id("tier_10_rocket_fuel"));
        public static final TagKey<Fluid> METHANE = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", "methane"));
    }

    public static final class DimensionTypes {
        /** Water out of sight of the sky does not freeze here, however cold the planet (see InsulatedOceans). */
        /** Worlds with no ground under their islands: landings are moved to the nearest one (see SafeLanding). */
        public static final TagKey<DimensionType> VOID_WORLDS = TagKey.create(Registries.DIMENSION_TYPE, AdAstraPlanets.id("void_worlds"));
        public static final TagKey<DimensionType> INSULATED_OCEANS = TagKey.create(Registries.DIMENSION_TYPE, AdAstraPlanets.id("insulated_oceans"));
    }

    private ModTags() {
    }
}
