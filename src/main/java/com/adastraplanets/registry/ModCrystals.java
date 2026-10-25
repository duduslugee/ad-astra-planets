package com.adastraplanets.registry;

import com.adastraplanets.block.BuddingCrystalBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

/**
 * Crystals grown in the geodes of each planetary system, amethyst-like: a crystal block, a budding block that grows
 * buds into clusters, and the shard the clusters drop. Their look is Minecraft's amethyst, tinted (see TintedBlocks).
 * Ids carry the crystal's name, never a body's.
 */
public final class ModCrystals {
    /** Phobos and Deimos. */
    public static final CrystalSet EMBER = new CrystalSet("ember", MapColor.COLOR_RED, 0xFF785A, "Ember", "de Brasa");
    /** Jupiter's moons. */
    public static final CrystalSet AMBER = new CrystalSet("amber", MapColor.COLOR_ORANGE, 0xFFB446, "Amber", "Âmbar");
    /** Saturn's moons. */
    public static final CrystalSet HALO = new CrystalSet("halo", MapColor.SAND, 0xFAE6A0, "Halo", "de Halo");
    /** Uranus and its moons. */
    public static final CrystalSet TIDE = new CrystalSet("tide", MapColor.COLOR_CYAN, 0x78F0E1, "Tide", "de Maré");
    /** Neptune and Triton. */
    public static final CrystalSet COBALT = new CrystalSet("cobalt", MapColor.COLOR_BLUE, 0x5A80FF, "Cobalt", "de Cobalto");
    /** Pluto and Charon. */
    public static final CrystalSet ROSE = new CrystalSet("rose", MapColor.COLOR_PINK, 0xFFA0C3, "Rose", "Rosa");
    /** Eris and Dysnomia. */
    public static final CrystalSet FROST = new CrystalSet("frost", MapColor.ICE, 0xDCE1FF, "Frost", "de Geada");

    public static final List<CrystalSet> ALL = List.of(EMBER, AMBER, HALO, TIDE, COBALT, ROSE, FROST);

    public static final class CrystalSet {
        private final String name;
        /** The tint laid over Minecraft's amethyst textures. */
        public final int colour;
        /** Display names: "Ember" (English, before "Crystal") and "de Brasa" (Portuguese, after "Cristal"). */
        public final String english;
        public final String portuguese;
        public final DeferredBlock<AmethystBlock> block;
        public final DeferredBlock<BuddingCrystalBlock> budding;
        public final DeferredBlock<AmethystClusterBlock> smallBud;
        public final DeferredBlock<AmethystClusterBlock> mediumBud;
        public final DeferredBlock<AmethystClusterBlock> largeBud;
        public final DeferredBlock<AmethystClusterBlock> cluster;
        public final DeferredItem<Item> shard;
        /** The system's Light Core, kept at the end of its landmark; the seven open the Last Door. */
        public final DeferredItem<Item> core;
        public final List<DeferredItem<BlockItem>> blockItems;

        CrystalSet(String name, MapColor mapColour, int colour, String english, String portuguese) {
            this.name = name;
            this.colour = colour;
            this.english = english;
            this.portuguese = portuguese;
            this.block = ModBlocks.BLOCKS.register(name + "_crystal_block",
                    () -> new AmethystBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).mapColor(mapColour)));
            this.cluster = ModBlocks.BLOCKS.register(name + "_crystal_cluster",
                    () -> new AmethystClusterBlock(7.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_CLUSTER).mapColor(mapColour)));
            this.largeBud = ModBlocks.BLOCKS.register("large_" + name + "_crystal_bud",
                    () -> new AmethystClusterBlock(5.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.LARGE_AMETHYST_BUD).mapColor(mapColour)));
            this.mediumBud = ModBlocks.BLOCKS.register("medium_" + name + "_crystal_bud",
                    () -> new AmethystClusterBlock(4.0F, 3.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.MEDIUM_AMETHYST_BUD).mapColor(mapColour)));
            this.smallBud = ModBlocks.BLOCKS.register("small_" + name + "_crystal_bud",
                    () -> new AmethystClusterBlock(3.0F, 4.0F, BlockBehaviour.Properties.ofFullCopy(Blocks.SMALL_AMETHYST_BUD).mapColor(mapColour)));
            this.budding = ModBlocks.BLOCKS.register("budding_" + name + "_crystal",
                    () -> new BuddingCrystalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BUDDING_AMETHYST).mapColor(mapColour),
                            List.of(this.smallBud, this.mediumBud, this.largeBud, this.cluster)));
            this.blockItems = List.of(ModItems.ITEMS.registerSimpleBlockItem(this.block), ModItems.ITEMS.registerSimpleBlockItem(this.budding),
                    ModItems.ITEMS.registerSimpleBlockItem(this.smallBud), ModItems.ITEMS.registerSimpleBlockItem(this.mediumBud),
                    ModItems.ITEMS.registerSimpleBlockItem(this.largeBud), ModItems.ITEMS.registerSimpleBlockItem(this.cluster));
            this.shard = ModItems.ITEMS.registerSimpleItem(name + "_crystal_shard");
            this.core = ModItems.ITEMS.registerItem(name + "_light_core", properties -> new com.adastraplanets.item.LightCoreItem(properties.stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.EPIC).fireResistant()
                    .component(net.minecraft.core.component.DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
        }

        public String name() {
            return this.name;
        }

        /** The buds and the cluster, smallest first. */
        public List<DeferredBlock<AmethystClusterBlock>> growths() {
            return List.of(this.smallBud, this.mediumBud, this.largeBud, this.cluster);
        }

        public List<DeferredBlock<? extends Block>> blocks() {
            return List.of(this.block, this.budding, this.smallBud, this.mediumBud, this.largeBud, this.cluster);
        }

        public TagKey<Item> gemsTag() {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gems/" + this.name + "_crystal"));
        }
    }

    /** Called from the mod constructor so every crystal is registered before the registers are attached. */
    public static void init() {
    }

    private ModCrystals() {
    }
}
