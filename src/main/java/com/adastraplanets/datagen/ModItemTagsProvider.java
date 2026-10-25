package com.adastraplanets.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.registry.ModItems;
import com.adastraplanets.registry.ModTags;
import earth.terrarium.adastra.common.tags.ModItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.minecraft.data.tags.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                               CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper files) {
        super(output, lookup, blockTags, AdAstraPlanets.MOD_ID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(earth.terrarium.adastra.common.tags.ModBlockTags.GLOBES, earth.terrarium.adastra.common.tags.ModItemTags.GLOBES);
        this.copy(ModTags.Blocks.ORES_HELIONITE, ModTags.Items.ORES_HELIONITE);
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        // the pressure diamond works as a diamond wherever another mod asks for the tag (our own recipes name the item)
        this.tag(Tags.Items.GEMS_DIAMOND).add(ModItems.PRESSURE_DIAMOND.get());
        this.tag(net.minecraft.tags.ItemTags.BEACON_PAYMENT_ITEMS).add(ModItems.PRESSURE_DIAMOND.get());
        this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
        this.copy(ModTags.Blocks.STORAGE_BLOCKS_HELIONITE, ModTags.Items.STORAGE_BLOCKS_HELIONITE);
        this.copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_HELIONITE, ModTags.Items.STORAGE_BLOCKS_RAW_HELIONITE);
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        this.tag(ModTags.Items.RAW_MATERIALS_HELIONITE).add(ModItems.RAW_HELIONITE.get());
        this.tag(Tags.Items.RAW_MATERIALS).addTag(ModTags.Items.RAW_MATERIALS_HELIONITE);
        this.tag(ModTags.Items.INGOTS_HELIONITE).add(ModItems.HELIONITE_INGOT.get());
        this.tag(Tags.Items.INGOTS).addTag(ModTags.Items.INGOTS_HELIONITE);
        this.tag(ModTags.Items.NUGGETS_HELIONITE).add(ModItems.HELIONITE_NUGGET.get());
        this.tag(Tags.Items.NUGGETS).addTag(ModTags.Items.NUGGETS_HELIONITE);
        this.tag(ModTags.Items.PLATES_HELIONITE).add(ModItems.HELIONITE_PLATE.get());
        this.tag(ModTags.Items.PLATES).addTag(ModTags.Items.PLATES_HELIONITE);

        this.copy(ModTags.Blocks.ORES_ABYSSIUM, ModTags.Items.ORES_ABYSSIUM);
        this.copy(ModTags.Blocks.STORAGE_BLOCKS_ABYSSIUM, ModTags.Items.STORAGE_BLOCKS_ABYSSIUM);
        this.copy(ModTags.Blocks.STORAGE_BLOCKS_RAW_ABYSSIUM, ModTags.Items.STORAGE_BLOCKS_RAW_ABYSSIUM);
        this.tag(ModTags.Items.RAW_MATERIALS_ABYSSIUM).add(ModItems.RAW_ABYSSIUM.get());
        this.tag(Tags.Items.RAW_MATERIALS).addTag(ModTags.Items.RAW_MATERIALS_ABYSSIUM);
        this.tag(ModTags.Items.INGOTS_ABYSSIUM).add(ModItems.ABYSSIUM_INGOT.get());
        this.tag(Tags.Items.INGOTS).addTag(ModTags.Items.INGOTS_ABYSSIUM);
        this.tag(ModTags.Items.NUGGETS_ABYSSIUM).add(ModItems.ABYSSIUM_NUGGET.get());
        this.tag(Tags.Items.NUGGETS).addTag(ModTags.Items.NUGGETS_ABYSSIUM);
        this.tag(ModTags.Items.PLATES_ABYSSIUM).add(ModItems.ABYSSIUM_PLATE.get());
        this.tag(ModTags.Items.PLATES).addTag(ModTags.Items.PLATES_ABYSSIUM);

        this.tag(com.adastraplanets.registry.ModTags.Items.ACCESS_CARDS).add(com.adastraplanets.registry.ModExpedition.ACCESS_CARD.get());
        // a crystal socket opens with its system's crystal shard; modpacks can add other keys
        for (var glow : com.adastraplanets.block.AncientGlow.values()) {
            this.tag(glow.socketKeys()).addTag(glow.crystal().gemsTag());
            // a core socket opens with its system's Light Core
            this.tag(glow.coreKeys()).add(glow.crystal().core.get());
        }
        // the Ancients' Suit: no liner, no oxygen, no heat or cold (Ad Astra's own armor tags)
        for (var piece : com.adastraplanets.registry.ModSuit.PIECES) {
            this.tag(com.adastraplanets.registry.ModTags.Items.ENVIRONMENT_PROOF_ARMOR).add(piece.get());
            for (String tag : new String[]{"space_resistant_armor", "oxygen_supplying_armor", "heat_resistant_armor", "freeze_resistant_armor"}) {
                this.tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ad_astra", tag))).add(piece.get());
            }
        }
        // each suit piece also fits its liner slot of Curios, leaving the armor slots for other armor
        for (int i = 0; i < 4; i++) {
            this.tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", com.adastraplanets.registry.ModSuit.SLOTS.get(i))))
                    .add(com.adastraplanets.registry.ModSuit.PIECES.get(i).get());
        }
        this.tag(net.minecraft.tags.ItemTags.HEAD_ARMOR).add(com.adastraplanets.registry.ModSuit.HELMET.get());
        this.tag(net.minecraft.tags.ItemTags.CHEST_ARMOR).add(com.adastraplanets.registry.ModSuit.CHESTPLATE.get());
        this.tag(net.minecraft.tags.ItemTags.LEG_ARMOR).add(com.adastraplanets.registry.ModSuit.LEGGINGS.get());
        this.tag(net.minecraft.tags.ItemTags.FOOT_ARMOR).add(com.adastraplanets.registry.ModSuit.BOOTS.get());
        this.tag(net.minecraft.tags.ItemTags.STAIRS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get().asItem());
        this.tag(net.minecraft.tags.ItemTags.SLABS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get().asItem());
        this.tag(net.minecraft.tags.ItemTags.WALLS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL.get().asItem());
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            this.tag(crystal.gemsTag()).add(crystal.shard.get());
            this.tag(Tags.Items.GEMS).addTag(crystal.gemsTag());
        }
        for (var material : com.adastraplanets.registry.ModMaterials.ALL) {
            this.copy(material.oresBlockTag(), material.oresTag());
            this.copy(material.storageBlockTag(), material.storageTag());
            this.copy(material.rawStorageBlockTag(), material.rawStorageTag());
            this.tag(material.rawTag()).add(material.raw.get());
            this.tag(Tags.Items.RAW_MATERIALS).addTag(material.rawTag());
            this.tag(material.ingotsTag()).add(material.ingot.get());
            this.tag(Tags.Items.INGOTS).addTag(material.ingotsTag());
            this.tag(material.nuggetsTag()).add(material.nugget.get());
            this.tag(Tags.Items.NUGGETS).addTag(material.nuggetsTag());
            this.tag(material.platesTag()).add(material.plate.get());
            this.tag(ModTags.Items.PLATES).addTag(material.platesTag());
        }

        this.copy(Tags.Blocks.STONES, Tags.Items.STONES);
        this.copy(net.minecraft.tags.BlockTags.SLABS, net.minecraft.tags.ItemTags.SLABS);
        this.copy(Tags.Blocks.COBBLESTONES, Tags.Items.COBBLESTONES);
        this.copy(Tags.Blocks.GRAVELS, Tags.Items.GRAVELS);
        // our cobblestones work in furnaces, stone tools and other stone recipes, like vanilla cobblestone
        for (var cobble : java.util.List.of(ModItems.CARBON_COBBLESTONE, ModItems.PALE_COBBLESTONE, ModItems.ABYSSAL_COBBLESTONE,
                ModItems.SULFUROUS_COBBLESTONE, ModItems.SULCUS_COBBLESTONE, ModItems.AGED_COBBLESTONE,
                ModItems.CLOUD_COBBLESTONE, ModItems.GLACIAL_COBBLESTONE,
                ModItems.ICEBOUND_COBBLESTONE, ModItems.VEILED_COBBLESTONE,
                ModItems.HAZED_COBBLESTONE, ModItems.COLUMN_COBBLESTONE,
                ModItems.PATCHWORK_COBBLESTONE, ModItems.RIFT_COBBLESTONE, ModItems.MURK_COBBLESTONE,
                ModItems.MANTLE_COBBLESTONE, ModItems.CRYO_COBBLESTONE,
                ModItems.GLOOM_COBBLESTONE, ModItems.SLATE_COBBLESTONE, ModItems.GLACIER_COBBLESTONE,
                ModItems.FRACTURE_COBBLESTONE, ModItems.MIRROR_COBBLESTONE)) {
            this.tag(net.minecraft.tags.ItemTags.STONE_CRAFTING_MATERIALS).add(cobble.get());
            this.tag(net.minecraft.tags.ItemTags.STONE_TOOL_MATERIALS).add(cobble.get());
        }

        // each liner piece fits its own Curios slot (slots defined in data/adastraplanets/curios/)
        for (int i = 0; i < ModItems.LINER_PIECES.size(); i++) {
            String slot = com.adastraplanets.environment.LinerSlots.SLOTS.get(i);
            var tag = this.tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", slot)));
            for (int level = 1; level <= ModItems.LINER_LEVELS; level++) {
                tag.add(ModItems.liner(ModItems.LINER_PIECES.get(i), level));
            }
        }

        // Ad Astra raises both arms and carries the item overhead for anything in this tag.
        this.tag(ModItemTags.HELD_OVER_HEAD).add(ModItems.TIER_5_ROCKET.get(), ModItems.TIER_6_ROCKET.get(),
                ModItems.TIER_7_ROCKET.get(), ModItems.TIER_8_ROCKET.get(), ModItems.TIER_9_ROCKET.get(), ModItems.TIER_10_ROCKET.get(), ModItems.ANCIENT_SHIP.get(),
                ModItems.LARGE_LAUNCH_PAD.get(),
                ModItems.HUGE_LAUNCH_PAD.get());
    }
}
