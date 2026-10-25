package com.adastraplanets.datagen;

import com.adastraplanets.block.LargeLaunchPadBlock;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class ModBlockLootProvider extends BlockLootSubProvider {
    /** A cluster: itself with silk touch; four shards (with Fortune) to a pickaxe, two to anything else. */
    private LootTable.Builder crystalCluster(Block cluster, net.minecraft.world.item.Item shard) {
        var enchantments = this.registries.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(cluster, LootItem.lootTableItem(shard)
                .apply(net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount(ConstantValue.exactly(4)))
                .apply(net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.addOreBonusCount(
                        enchantments.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE)))
                .when(net.minecraft.world.level.storage.loot.predicates.MatchTool.toolMatches(
                        net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(net.minecraft.tags.ItemTags.CLUSTER_MAX_HARVESTABLES)))
                .otherwise(this.applyExplosionDecay(cluster, LootItem.lootTableItem(shard)
                        .apply(net.minecraft.world.level.storage.loot.functions.SetItemCountFunction.setCount(ConstantValue.exactly(2))))));
    }

    public ModBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.ADVANCED_NASA_WORKBENCH.get());
        this.dropSelf(ModBlocks.STELLAR_NASA_WORKBENCH.get());
        ModGlobes.blocks().values().forEach(block -> this.dropSelf(block.get()));
        this.dropSelf(ModBlocks.HELIONITE_BLOCK.get());
        this.dropSelf(ModBlocks.RAW_HELIONITE_BLOCK.get());
        this.add(ModBlocks.HELIONITE_ORE.get(), block -> this.createOreDrop(block, ModItems.RAW_HELIONITE.get()));

        for (var block : java.util.List.of(ModBlocks.CRUST_ICE, ModBlocks.RUSTED_ICE, ModBlocks.RIME,
                ModBlocks.CHIMNEY_ROCK, ModBlocks.GEYSER, ModBlocks.ABYSSIUM_BLOCK, ModBlocks.RAW_ABYSSIUM_BLOCK)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.ABYSSIUM_ORE.get(), block -> this.createOreDrop(block, ModItems.RAW_ABYSSIUM.get()));
        for (var block : java.util.List.of(ModBlocks.CARBON_REGOLITH, ModBlocks.PALE_DUST, ModBlocks.CARBON_COBBLESTONE,
                ModBlocks.PALE_COBBLESTONE, ModBlocks.ABYSSAL_COBBLESTONE)) {
            this.dropSelf(block.get());
        }
        // stones break into their cobblestone unless mined with silk touch, as in vanilla
        this.add(ModBlocks.CARBON_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.CARBON_COBBLESTONE.get()));
        this.add(ModBlocks.PALE_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.PALE_COBBLESTONE.get()));
        this.add(ModBlocks.ABYSSAL_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.ABYSSAL_COBBLESTONE.get()));
        this.add(ModBlocks.SULFUROUS_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.SULFUROUS_COBBLESTONE.get()));
        this.add(ModBlocks.SULCUS_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.SULCUS_COBBLESTONE.get()));
        this.add(ModBlocks.AGED_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.AGED_COBBLESTONE.get()));
        this.add(ModBlocks.CLOUD_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.CLOUD_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.CLOUD_COBBLESTONE, ModBlocks.ZONE_FROST, ModBlocks.BELT_FROST)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.ZONE_FROST_SLAB.get(), this::createSlabItemTable);
        this.add(ModBlocks.BELT_FROST_SLAB.get(), this::createSlabItemTable);
        this.add(ModBlocks.GLACIAL_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.GLACIAL_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.GLACIAL_COBBLESTONE, ModBlocks.PLUME_SNOW, ModBlocks.FISSURE_ICE)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.ICEBOUND_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.ICEBOUND_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.ICEBOUND_COBBLESTONE, ModBlocks.FROST_REGOLITH)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.VEILED_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.VEILED_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.VEILED_COBBLESTONE, ModBlocks.UMBER_DUST)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.HAZED_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.HAZED_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.HAZED_COBBLESTONE, ModBlocks.THOLIN_SAND, ModBlocks.ICE_PEBBLES)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.COLUMN_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.COLUMN_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.COLUMN_COBBLESTONE, ModBlocks.AMMONIA_FROST)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.MIRROR_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.MIRROR_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.MIRROR_COBBLESTONE, ModBlocks.DRIFT_SNOW, ModBlocks.ICE_SHARD)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.FRACTURE_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.FRACTURE_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.FRACTURE_COBBLESTONE, ModBlocks.BRIGHT_ICE)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.GLACIER_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.GLACIER_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.GLACIER_COBBLESTONE, ModBlocks.CELL_ICE, ModBlocks.METHANE_ICE)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.SLATE_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.SLATE_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.SLATE_COBBLESTONE, ModBlocks.CRIMSON_FROST)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.GLOOM_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.GLOOM_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.GLOOM_COBBLESTONE, ModBlocks.DEEP_ICE, ModBlocks.GLOOM_CRYSTAL)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.CRYO_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.CRYO_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.CRYO_COBBLESTONE, ModBlocks.NITROGEN_FROST, ModBlocks.NITROGEN_VENT)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.MANTLE_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.MANTLE_COBBLESTONE.get()));
        // pressure crystal: itself with silk touch, otherwise sometimes a pressure diamond (more often with fortune)
        var fortune = this.registries.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);
        this.add(ModBlocks.PRESSURE_CRYSTAL.get(), block -> this.createSilkTouchDispatchTable(block, this.applyExplosionCondition(block,
                net.minecraft.world.level.storage.loot.entries.LootItem.lootTableItem(ModItems.PRESSURE_DIAMOND.get())
                        .when(net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition
                                .bonusLevelFlatChance(fortune, 0.3F, 0.4F, 0.5F, 0.65F)))));
        for (var block : java.util.List.of(ModBlocks.MANTLE_COBBLESTONE, ModBlocks.AZURE_ICE)) {
            this.dropSelf(block.get());
        }
        this.add(ModBlocks.MURK_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.MURK_COBBLESTONE.get()));
        this.dropSelf(ModBlocks.MURK_COBBLESTONE.get());
        this.add(ModBlocks.RIFT_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.RIFT_COBBLESTONE.get()));
        this.dropSelf(ModBlocks.RIFT_COBBLESTONE.get());
        this.add(ModBlocks.PATCHWORK_ROCK.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.PATCHWORK_COBBLESTONE.get()));
        for (var block : java.util.List.of(ModBlocks.PATCHWORK_COBBLESTONE, ModBlocks.BANDED_ICE)) {
            this.dropSelf(block.get());
        }
        for (var block : java.util.List.of(ModBlocks.AGED_COBBLESTONE, ModBlocks.ANCIENT_DUST)) {
            this.dropSelf(block.get());
        }
        for (var block : java.util.List.of(ModBlocks.SULCUS_COBBLESTONE, ModBlocks.DARK_REGOLITH, ModBlocks.GROOVE_ICE)) {
            this.dropSelf(block.get());
        }
        for (var block : java.util.List.of(ModBlocks.SULFUROUS_COBBLESTONE, ModBlocks.SULFUR_CRUST, ModBlocks.CINDER, ModBlocks.SULFUR_VENT)) {
            this.dropSelf(block.get());
        }
        for (var block : java.util.List.of(com.adastraplanets.registry.ModAncient.ANCIENT_STONE, com.adastraplanets.registry.ModAncient.ANCIENT_BRICKS, com.adastraplanets.registry.ModAncient.CRACKED_ANCIENT_BRICKS,
                com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS, com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL, com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR, com.adastraplanets.registry.ModAncient.ANCIENT_RUNE_STONE)) {
            this.dropSelf(block.get());
        }
        this.add(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get(), this::createSlabItemTable);
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            this.dropSelf(crystal.block.get());
            this.add(crystal.budding.get(), noDrop());
            for (var bud : java.util.List.of(crystal.smallBud, crystal.mediumBud, crystal.largeBud)) {
                this.dropWhenSilkTouch(bud.get());
            }
            this.add(crystal.cluster.get(), this.crystalCluster(crystal.cluster.get(), crystal.shard.get()));
        }
        for (var material : com.adastraplanets.registry.ModMaterials.ALL) {
            this.dropSelf(material.block.get());
            this.dropSelf(material.rawBlock.get());
            this.add(material.ore.get(), block -> this.createOreDrop(block, material.raw.get()));
        }
        // the glowing mouth only survives silk touch; otherwise it breaks into chimney rock
        this.add(ModBlocks.HYDROTHERMAL_VENT.get(), block -> this.createSingleItemTableWithSilkTouch(block, ModItems.CHIMNEY_ROCK.get()));
        // breaking any part breaks the whole pad; only the centre drops it
        for (var pad : java.util.List.<com.adastraplanets.block.MultiblockLaunchPadBlock>of(ModBlocks.LARGE_LAUNCH_PAD.get(),
                ModBlocks.HUGE_LAUNCH_PAD.get())) {
            this.add(pad, block -> LootTable.lootTable().withPool(this.applyExplosionCondition(block,
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block)
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(
                                    StatePropertiesPredicate.Builder.properties()
                                            .hasProperty(pad.xProperty(), pad.middle())
                                            .hasProperty(pad.zProperty(), pad.middle())))))));
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
    }
}
