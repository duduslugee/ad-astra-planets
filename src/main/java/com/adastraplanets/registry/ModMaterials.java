package com.adastraplanets.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

/**
 * Ore materials that each come from one planet or moon and feed the next rocket's parts. Every material gets the
 * same set (ore, raw, ingot, nugget, plate, storage blocks) and the same {@code c:} tags, so datagen handles them in
 * one loop. Ids carry the material's name, never the body's.
 */
public final class ModMaterials {
    /** Phobos: dark, carbon-rich; Tier 5 fins and tanks. */
    public static final MaterialSet UMBRITE = new MaterialSet("umbrite", MapColor.COLOR_BLACK);
    /** Deimos: pale, dusty; the Tier 5 nose cone. */
    public static final MaterialSet PALLORITE = new MaterialSet("pallorite", MapColor.SAND);

    /** Io: volcanic, molten; Tier 6 fins. */
    public static final MaterialSet IGNEUM = new MaterialSet("igneum", MapColor.COLOR_ORANGE);

    /** Ganymede: crystalline, aurora-coloured; the Tier 6 hull (its storage blocks). */
    public static final MaterialSet AURORIUM = new MaterialSet("aurorium", MapColor.COLOR_LIGHT_GREEN);

    /** Callisto: ancient slate veined with amber; the Tier 6 nose cone. */
    public static final MaterialSet EONYX = new MaterialSet("eonyx", MapColor.TERRACOTTA_ORANGE);

    /** Jupiter: storm-red and cream, swirled; the Tier 6 engine. */
    public static final MaterialSet STORMIUM = new MaterialSet("stormium", MapColor.COLOR_RED);

    /** Enceladus: frozen spray from its geysers; the Tier 7 tank. */
    public static final MaterialSet NIVALUM = new MaterialSet("nivalum", MapColor.COLOR_LIGHT_BLUE);
    /** Rhea: meteoric iron from its old impacts; the Tier 7 nose cone. */
    public static final MaterialSet FERROLITH = new MaterialSet("ferrolith", MapColor.METAL);
    /** Iapetus: two-faced like its moon, dark umber and bright ice; the Tier 7 fins. */
    public static final MaterialSet VESPERINE = new MaterialSet("vesperine", MapColor.TERRACOTTA_BROWN);
    /** Titan: tholins, the organic haze settled out of its sky; the Tier 7 engine. */
    public static final MaterialSet THOLINAR = new MaterialSet("tholinar", MapColor.COLOR_ORANGE);
    /** Saturn: its rings; Tier 7 rocket hulls are built from its storage blocks. */
    public static final MaterialSet CASSINIUM = new MaterialSet("cassinium", MapColor.SAND);
    /** Miranda: its patchwork surface; the Tier 8 nose cone. */
    public static final MaterialSet MOSAICON = new MaterialSet("mosaicon", MapColor.COLOR_LIGHT_GRAY);
    /** Titania: laid bare in the walls of its great rift canyons; the Tier 8 tanks. */
    public static final MaterialSet CHASMAL = new MaterialSet("chasmal", MapColor.TERRACOTTA_PINK);
    /** Oberon: the dark stuff of its crater floors; the Tier 8 fins. */
    public static final MaterialSet OBSCUREX = new MaterialSet("obscurex", MapColor.TERRACOTTA_PURPLE);
    /** Uranus: crystal from its deep icy mantle; the Tier 8 hull and engine. */
    public static final MaterialSet AZURANE = new MaterialSet("azurane", MapColor.COLOR_CYAN);
    /** Triton: frozen nitrogen from its pink polar cap; the Tier 9 nose cone and fins. */
    public static final MaterialSet NITRALIS = new MaterialSet("nitralis", MapColor.COLOR_PINK);
    /** Neptune: storm-blue metal from the walls of its abyss; the Tier 9 hull, engine and tanks. */
    public static final MaterialSet TEMPESTINE = new MaterialSet("tempestine", MapColor.COLOR_BLUE);
    /** Charon: from the dark red stain of its polar cap; the Tier 10 nose cone and fins. */
    public static final MaterialSet MORDRINE = new MaterialSet("mordrine", MapColor.TERRACOTTA_RED);
    /** Pluto: from the bedrock under its pale heart; the Tier 10 hull, engine and tanks. */
    public static final MaterialSet CORDIUM = new MaterialSet("cordium", MapColor.SAND);
    /** Dysnomia: dark shards of its broken crust; an endgame material left to modpacks' recipes. */
    public static final MaterialSet SHARDEX = new MaterialSet("shardex", MapColor.COLOR_GRAY);
    /** Eris: mirror-bright metal from under its ice; an endgame material left to modpacks' recipes. */
    public static final MaterialSet ALBEDON = new MaterialSet("albedon", MapColor.SNOW);

    public static final List<MaterialSet> ALL = List.of(UMBRITE, PALLORITE, IGNEUM, AURORIUM, EONYX, STORMIUM, NIVALUM, FERROLITH, VESPERINE, THOLINAR, CASSINIUM, MOSAICON, CHASMAL, OBSCUREX, AZURANE, NITRALIS, TEMPESTINE, MORDRINE, CORDIUM, SHARDEX, ALBEDON);

    public static final class MaterialSet {
        private final String name;
        public final DeferredBlock<DropExperienceBlock> ore;
        public final DeferredBlock<Block> block;
        public final DeferredBlock<Block> rawBlock;
        public final DeferredItem<BlockItem> oreItem;
        public final DeferredItem<BlockItem> blockItem;
        public final DeferredItem<BlockItem> rawBlockItem;
        public final DeferredItem<Item> raw;
        public final DeferredItem<Item> ingot;
        public final DeferredItem<Item> nugget;
        public final DeferredItem<Item> plate;

        MaterialSet(String name, MapColor colour) {
            this.name = name;
            this.ore = ModBlocks.BLOCKS.register(name + "_ore", () -> new DropExperienceBlock(ConstantInt.of(0),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).mapColor(colour)));
            this.block = ModBlocks.BLOCKS.registerSimpleBlock(name + "_block", BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(colour));
            this.rawBlock = ModBlocks.BLOCKS.registerSimpleBlock("raw_" + name + "_block",
                    BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK).mapColor(colour));
            this.oreItem = ModItems.ITEMS.registerSimpleBlockItem(this.ore);
            this.blockItem = ModItems.ITEMS.registerSimpleBlockItem(this.block);
            this.rawBlockItem = ModItems.ITEMS.registerSimpleBlockItem(this.rawBlock);
            this.raw = ModItems.ITEMS.registerSimpleItem("raw_" + name);
            this.ingot = ModItems.ITEMS.registerSimpleItem(name + "_ingot");
            this.nugget = ModItems.ITEMS.registerSimpleItem(name + "_nugget");
            this.plate = ModItems.ITEMS.registerSimpleItem(name + "_plate");
        }

        public String name() {
            return this.name;
        }

        public TagKey<Block> oresBlockTag() {
            return blockTag("ores/" + this.name);
        }

        public TagKey<Block> storageBlockTag() {
            return blockTag("storage_blocks/" + this.name);
        }

        public TagKey<Block> rawStorageBlockTag() {
            return blockTag("storage_blocks/raw_" + this.name);
        }

        public TagKey<Item> oresTag() {
            return itemTag("ores/" + this.name);
        }

        public TagKey<Item> storageTag() {
            return itemTag("storage_blocks/" + this.name);
        }

        public TagKey<Item> rawStorageTag() {
            return itemTag("storage_blocks/raw_" + this.name);
        }

        public TagKey<Item> rawTag() {
            return itemTag("raw_materials/" + this.name);
        }

        public TagKey<Item> ingotsTag() {
            return itemTag("ingots/" + this.name);
        }

        public TagKey<Item> nuggetsTag() {
            return itemTag("nuggets/" + this.name);
        }

        public TagKey<Item> platesTag() {
            return itemTag("plates/" + this.name);
        }

        private static TagKey<Block> blockTag(String path) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", path));
        }

        private static TagKey<Item> itemTag(String path) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
        }
    }

    /** Called from the mod constructor so every material is registered before the registers are attached. */
    public static void init() {
    }

    private ModMaterials() {
    }
}
