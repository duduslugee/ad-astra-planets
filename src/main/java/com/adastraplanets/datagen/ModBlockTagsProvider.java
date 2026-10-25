package com.adastraplanets.datagen;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.registry.ModTags;
import earth.terrarium.adastra.common.tags.ModBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
        super(output, lookup, AdAstraPlanets.MOD_ID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ADVANCED_NASA_WORKBENCH.get(), ModBlocks.STELLAR_NASA_WORKBENCH.get());
        // our globes join Ad Astra's own globe tag
        ModGlobes.blocks().values().forEach(block -> {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
            this.tag(earth.terrarium.adastra.common.tags.ModBlockTags.GLOBES).add(block.get());
        });
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModBlocks.HELIONITE_ORE.get(), ModBlocks.HELIONITE_BLOCK.get(), ModBlocks.RAW_HELIONITE_BLOCK.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                ModBlocks.HELIONITE_ORE.get(), ModBlocks.HELIONITE_BLOCK.get(), ModBlocks.RAW_HELIONITE_BLOCK.get());

        this.tag(ModTags.Blocks.ORES_HELIONITE).add(ModBlocks.HELIONITE_ORE.get());
        this.tag(Tags.Blocks.ORES).addTag(ModTags.Blocks.ORES_HELIONITE);
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(ModBlocks.HELIONITE_ORE.get());

        this.tag(ModTags.Blocks.STORAGE_BLOCKS_HELIONITE).add(ModBlocks.HELIONITE_BLOCK.get());
        this.tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_HELIONITE).add(ModBlocks.RAW_HELIONITE_BLOCK.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_HELIONITE)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_RAW_HELIONITE);
        this.tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.HELIONITE_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CRUST_ICE.get(), ModBlocks.RUSTED_ICE.get(), ModBlocks.ABYSSAL_ROCK.get(),
                ModBlocks.CHIMNEY_ROCK.get(), ModBlocks.HYDROTHERMAL_VENT.get(), ModBlocks.GEYSER.get(),
                ModBlocks.ABYSSIUM_ORE.get(), ModBlocks.ABYSSIUM_BLOCK.get(), ModBlocks.RAW_ABYSSIUM_BLOCK.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.RIME.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                ModBlocks.ABYSSIUM_ORE.get(), ModBlocks.ABYSSIUM_BLOCK.get(), ModBlocks.RAW_ABYSSIUM_BLOCK.get());
        this.tag(ModTags.Blocks.ORES_ABYSSIUM).add(ModBlocks.ABYSSIUM_ORE.get());
        this.tag(Tags.Blocks.ORES).addTag(ModTags.Blocks.ORES_ABYSSIUM);
        this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(ModBlocks.ABYSSIUM_ORE.get());
        this.tag(ModTags.Blocks.STORAGE_BLOCKS_ABYSSIUM).add(ModBlocks.ABYSSIUM_BLOCK.get());
        this.tag(ModTags.Blocks.STORAGE_BLOCKS_RAW_ABYSSIUM).add(ModBlocks.RAW_ABYSSIUM_BLOCK.get());
        this.tag(Tags.Blocks.STORAGE_BLOCKS)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_ABYSSIUM)
                .addTag(ModTags.Blocks.STORAGE_BLOCKS_RAW_ABYSSIUM);
        this.tag(BlockTags.BEACON_BASE_BLOCKS).add(ModBlocks.ABYSSIUM_BLOCK.get());
        this.tag(ModTags.Blocks.HYDROTHERMAL_VENT_REPLACEABLE).add(ModBlocks.ABYSSAL_ROCK.get(), ModBlocks.CHIMNEY_ROCK.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CARBON_ROCK.get(), ModBlocks.PALE_ROCK.get(),
                ModBlocks.CARBON_COBBLESTONE.get(), ModBlocks.PALE_COBBLESTONE.get(), ModBlocks.ABYSSAL_COBBLESTONE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.CARBON_ROCK.get(), ModBlocks.PALE_ROCK.get(), ModBlocks.ABYSSAL_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.CARBON_COBBLESTONE.get(), ModBlocks.PALE_COBBLESTONE.get(),
                ModBlocks.ABYSSAL_COBBLESTONE.get());
        this.tag(Tags.Blocks.GRAVELS).add(ModBlocks.CARBON_REGOLITH.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SULFUROUS_ROCK.get(), ModBlocks.SULFUROUS_COBBLESTONE.get(),
                ModBlocks.SULFUR_CRUST.get(), ModBlocks.CINDER.get(), ModBlocks.SULFUR_VENT.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.SULFUROUS_ROCK.get(), ModBlocks.SULCUS_ROCK.get(), ModBlocks.AGED_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.SULFUROUS_COBBLESTONE.get(), ModBlocks.SULCUS_COBBLESTONE.get(),
                ModBlocks.AGED_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.AGED_ROCK.get(), ModBlocks.AGED_COBBLESTONE.get(),
                ModBlocks.CLOUD_ROCK.get(), ModBlocks.CLOUD_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.ZONE_FROST.get(), ModBlocks.BELT_FROST.get(),
                ModBlocks.ZONE_FROST_SLAB.get(), ModBlocks.BELT_FROST_SLAB.get());
        this.tag(BlockTags.SLABS).add(ModBlocks.ZONE_FROST_SLAB.get(), ModBlocks.BELT_FROST_SLAB.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.CLOUD_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.CLOUD_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.ANCIENT_DUST.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.GLACIAL_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.GLACIAL_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GLACIAL_ROCK.get(), ModBlocks.GLACIAL_COBBLESTONE.get(),
                ModBlocks.FISSURE_ICE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.PLUME_SNOW.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.PLUME_SNOW.get());
        this.tag(BlockTags.ICE).add(ModBlocks.FISSURE_ICE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.ICEBOUND_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.ICEBOUND_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ICEBOUND_ROCK.get(), ModBlocks.ICEBOUND_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.FROST_REGOLITH.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.VEILED_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.VEILED_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.VEILED_ROCK.get(), ModBlocks.VEILED_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.UMBER_DUST.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.HAZED_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.HAZED_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.HAZED_ROCK.get(), ModBlocks.HAZED_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.THOLIN_SAND.get(), ModBlocks.ICE_PEBBLES.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.COLUMN_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.COLUMN_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.COLUMN_ROCK.get(), ModBlocks.COLUMN_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.AMMONIA_FROST.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.AMMONIA_FROST.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.MIRROR_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.MIRROR_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MIRROR_ROCK.get(), ModBlocks.MIRROR_COBBLESTONE.get(),
                ModBlocks.ICE_SHARD.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.DRIFT_SNOW.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.DRIFT_SNOW.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.FRACTURE_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.FRACTURE_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.FRACTURE_ROCK.get(), ModBlocks.FRACTURE_COBBLESTONE.get(),
                ModBlocks.BRIGHT_ICE.get());
        this.tag(BlockTags.ICE).add(ModBlocks.BRIGHT_ICE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.GLACIER_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.GLACIER_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GLACIER_ROCK.get(), ModBlocks.GLACIER_COBBLESTONE.get(),
                ModBlocks.CELL_ICE.get(), ModBlocks.METHANE_ICE.get());
        this.tag(BlockTags.ICE).add(ModBlocks.CELL_ICE.get(), ModBlocks.METHANE_ICE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.SLATE_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.SLATE_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SLATE_ROCK.get(), ModBlocks.SLATE_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.CRIMSON_FROST.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.CRIMSON_FROST.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.GLOOM_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.GLOOM_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GLOOM_ROCK.get(), ModBlocks.GLOOM_COBBLESTONE.get(),
                ModBlocks.DEEP_ICE.get(), ModBlocks.GLOOM_CRYSTAL.get());
        this.tag(BlockTags.ICE).add(ModBlocks.DEEP_ICE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.CRYO_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.CRYO_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CRYO_ROCK.get(), ModBlocks.CRYO_COBBLESTONE.get(),
                ModBlocks.NITROGEN_VENT.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.NITROGEN_FROST.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.NITROGEN_FROST.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.MANTLE_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.MANTLE_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MANTLE_ROCK.get(), ModBlocks.MANTLE_COBBLESTONE.get(),
                ModBlocks.AZURE_ICE.get(), ModBlocks.PRESSURE_CRYSTAL.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.PRESSURE_CRYSTAL.get());
        this.tag(BlockTags.ICE).add(ModBlocks.AZURE_ICE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.MURK_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.MURK_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MURK_ROCK.get(), ModBlocks.MURK_COBBLESTONE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.RIFT_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.RIFT_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.RIFT_ROCK.get(), ModBlocks.RIFT_COBBLESTONE.get());
        this.tag(Tags.Blocks.STONES).add(ModBlocks.PATCHWORK_ROCK.get());
        this.tag(Tags.Blocks.COBBLESTONES).add(ModBlocks.PATCHWORK_COBBLESTONE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PATCHWORK_ROCK.get(), ModBlocks.PATCHWORK_COBBLESTONE.get(),
                ModBlocks.BANDED_ICE.get());
        this.tag(BlockTags.ICE).add(ModBlocks.BANDED_ICE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SULCUS_ROCK.get(), ModBlocks.SULCUS_COBBLESTONE.get(), ModBlocks.GROOVE_ICE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.DARK_REGOLITH.get());
        this.tag(BlockTags.ICE).add(ModBlocks.GROOVE_ICE.get());
        this.tag(BlockTags.SNOW).add(ModBlocks.RIME.get());
        this.tag(BlockTags.ICE).add(ModBlocks.CRUST_ICE.get(), ModBlocks.RUSTED_ICE.get());
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.CARBON_REGOLITH.get(), ModBlocks.PALE_DUST.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(com.adastraplanets.registry.ModAncient.ANCIENT_STONE.get(), com.adastraplanets.registry.ModAncient.ANCIENT_BRICKS.get(),
                com.adastraplanets.registry.ModAncient.CRACKED_ANCIENT_BRICKS.get(), com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get(), com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get(),
                com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL.get(), com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR.get(), com.adastraplanets.registry.ModAncient.ANCIENT_RUNE_STONE.get());
        this.tag(BlockTags.STAIRS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get());
        this.tag(BlockTags.SLABS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get());
        this.tag(BlockTags.WALLS).add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL.get());
        // the ruins' and the expedition's locks: nothing may move or break them
        this.tag(BlockTags.WITHER_IMMUNE).add(com.adastraplanets.registry.ModExpedition.ACCESS_READER.get());
        this.tag(BlockTags.DRAGON_IMMUNE).add(com.adastraplanets.registry.ModExpedition.ACCESS_READER.get());
        this.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(com.adastraplanets.registry.ModExpedition.ACCESS_READER.get());
        this.tag(BlockTags.WITHER_IMMUNE).add(com.adastraplanets.registry.ModAncient.CRYSTAL_SOCKET.get(), com.adastraplanets.registry.ModAncient.ANCIENT_GATE.get(), com.adastraplanets.registry.ModAncient.ANCIENT_SEAL.get(), com.adastraplanets.registry.ModAncient.GLYPH_RUNE.get(), com.adastraplanets.registry.ModAncient.FORGE_VENT.get(), com.adastraplanets.registry.ModAncient.CORE_SOCKET.get());
        this.tag(BlockTags.DRAGON_IMMUNE).add(com.adastraplanets.registry.ModAncient.CRYSTAL_SOCKET.get(), com.adastraplanets.registry.ModAncient.ANCIENT_GATE.get(), com.adastraplanets.registry.ModAncient.ANCIENT_SEAL.get(), com.adastraplanets.registry.ModAncient.GLYPH_RUNE.get(), com.adastraplanets.registry.ModAncient.FORGE_VENT.get(), com.adastraplanets.registry.ModAncient.CORE_SOCKET.get());
        this.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(com.adastraplanets.registry.ModAncient.CRYSTAL_SOCKET.get(), com.adastraplanets.registry.ModAncient.ANCIENT_GATE.get(), com.adastraplanets.registry.ModAncient.ANCIENT_SEAL.get(), com.adastraplanets.registry.ModAncient.GLYPH_RUNE.get(), com.adastraplanets.registry.ModAncient.FORGE_VENT.get(), com.adastraplanets.registry.ModAncient.CORE_SOCKET.get());
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            crystal.blocks().forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get()));
            this.tag(BlockTags.CRYSTAL_SOUND_BLOCKS).add(crystal.block.get(), crystal.budding.get());
            this.tag(Tags.Blocks.BUDDING_BLOCKS).add(crystal.budding.get());
            crystal.growths().forEach(block -> this.tag(Tags.Blocks.BUDS).add(block.get()));
            this.tag(Tags.Blocks.CLUSTERS).add(crystal.cluster.get());
        }
        for (var material : com.adastraplanets.registry.ModMaterials.ALL) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(material.ore.get(), material.block.get(), material.rawBlock.get());
            this.tag(BlockTags.NEEDS_IRON_TOOL).add(material.ore.get(), material.block.get(), material.rawBlock.get());
            this.tag(material.oresBlockTag()).add(material.ore.get());
            this.tag(Tags.Blocks.ORES).addTag(material.oresBlockTag());
            this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(material.ore.get());
            this.tag(material.storageBlockTag()).add(material.block.get());
            this.tag(material.rawStorageBlockTag()).add(material.rawBlock.get());
            this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(material.storageBlockTag()).addTag(material.rawStorageBlockTag());
            this.tag(BlockTags.BEACON_BASE_BLOCKS).add(material.block.get());
        }

        // the large pad works for every rocket, Ad Astra's included; big rockets accept only large pads
        this.tag(ModTags.Blocks.HUGE_LAUNCH_PADS).add(ModBlocks.HUGE_LAUNCH_PAD.get());
        this.tag(ModTags.Blocks.LARGE_LAUNCH_PADS).add(ModBlocks.LARGE_LAUNCH_PAD.get()).addTag(ModTags.Blocks.HUGE_LAUNCH_PADS);
        this.tag(ModBlockTags.LAUNCH_PADS).addTag(ModTags.Blocks.LARGE_LAUNCH_PADS);
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.LARGE_LAUNCH_PAD.get(), ModBlocks.HUGE_LAUNCH_PAD.get());
    }
}
