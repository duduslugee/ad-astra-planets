package com.adastraplanets.datagen;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.block.LargeLaunchPadBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    // stone the ore is set in (Ad Astra's Mercury stone); a resource pack can swap the texture
    private static final ResourceLocation ORE_HOST_STONE = ResourceLocation.fromNamespaceAndPath("ad_astra", "block/mercury_stone");

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper files) {
        super(output, AdAstraPlanets.MOD_ID, files);
    }

    @Override
    protected void registerStatesAndModels() {
        // the workbenches: Ad Astra's NASA Workbench model, our texture; turned with the block's facing like Ad Astra's
        for (var bench : java.util.List.of(ModBlocks.ADVANCED_NASA_WORKBENCH, ModBlocks.STELLAR_NASA_WORKBENCH)) {
            String id = bench.getId().getPath();
            var model = this.models().getBuilder(id)
                    .parent(new net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile(
                            ResourceLocation.fromNamespaceAndPath("ad_astra", "block/nasa_workbench")))
                    .texture("0", this.modLoc("block/" + id)).texture("particle", this.modLoc("block/" + id));
            this.horizontalBlock(bench.get(), model, 180);   // Ad Astra's workbench model faces north unturned
            this.simpleBlockItem(bench.get(), model);
        }
        // globes: the stand is the block model, the spinning globe a model of its own (drawn by GlobeRenderer)
        ModGlobes.blocks().forEach((id, block) -> {
            var texture = this.modLoc("block/globe/" + id);
            // cutout on each model: the bracket is drawn on a plate whose empty pixels must stay see-through
            var stand = this.models().withExistingParent(id, this.modLoc("block/globe")).texture("0", texture).texture("particle", texture)
                    .renderType("cutout");
            this.models().withExistingParent(id + "_cube", this.modLoc("block/globe_cube")).texture("0", texture).renderType("cutout");
            this.getVariantBuilder(block.get()).forAllStates(state ->
                    net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder().modelFile(stand).build());
        });
        // storage blocks join into panels (ConnectedModel); their models are hand-written in resources
        this.simpleBlockWithItem(ModBlocks.HELIONITE_BLOCK.get(), this.models().getExistingFile(this.modLoc("block/helionite_block")));
        this.simpleBlockWithItem(ModBlocks.RAW_HELIONITE_BLOCK.get(), this.cubeAll(ModBlocks.RAW_HELIONITE_BLOCK.get()));

        // Ore = the host stone texture (referenced, not copied) with our overlay on top.
        this.simpleBlockWithItem(ModBlocks.HELIONITE_ORE.get(), this.models()
                .withExistingParent("helionite_ore", this.modLoc("block/ore_overlay"))
                .texture("base", ORE_HOST_STONE)
                .texture("overlay", this.modLoc("block/helionite_ore_overlay"))
                .texture("particle", ORE_HOST_STONE));

        this.largeLaunchPad();
        this.europa();
        this.marsMoons();
        this.io();
        this.ganymede();
        this.callisto();
        this.enceladus();
        this.rhea();
        this.iapetus();
        this.titan();
        this.saturn();
        this.miranda();
        this.simpleBlockWithItem(ModBlocks.RIFT_ROCK.get(), this.cubeAll(ModBlocks.RIFT_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.RIFT_COBBLESTONE.get(), this.cubeAll(ModBlocks.RIFT_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.MURK_ROCK.get(), this.cubeAll(ModBlocks.MURK_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.MURK_COBBLESTONE.get(), this.cubeAll(ModBlocks.MURK_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.MIRROR_ROCK.get(), this.cubeAll(ModBlocks.MIRROR_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.MIRROR_COBBLESTONE.get(), this.cubeAll(ModBlocks.MIRROR_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.DRIFT_SNOW.get(), this.models()
                .withExistingParent("drift_snow", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        this.simpleBlockWithItem(ModBlocks.ICE_SHARD.get(), this.models()
                .withExistingParent("ice_shard", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/amethyst_block")));
        this.simpleBlockWithItem(ModBlocks.FRACTURE_ROCK.get(), this.cubeAll(ModBlocks.FRACTURE_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.FRACTURE_COBBLESTONE.get(), this.cubeAll(ModBlocks.FRACTURE_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.BRIGHT_ICE.get(), this.models()
                .withExistingParent("bright_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.GLACIER_ROCK.get(), this.cubeAll(ModBlocks.GLACIER_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.GLACIER_COBBLESTONE.get(), this.cubeAll(ModBlocks.GLACIER_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.CELL_ICE.get(), this.models()
                .withExistingParent("cell_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.METHANE_ICE.get(), this.models()
                .withExistingParent("methane_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.SLATE_ROCK.get(), this.cubeAll(ModBlocks.SLATE_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.SLATE_COBBLESTONE.get(), this.cubeAll(ModBlocks.SLATE_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.CRIMSON_FROST.get(), this.models()
                .withExistingParent("crimson_frost", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        this.simpleBlockWithItem(ModBlocks.GLOOM_ROCK.get(), this.cubeAll(ModBlocks.GLOOM_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.GLOOM_COBBLESTONE.get(), this.cubeAll(ModBlocks.GLOOM_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.DEEP_ICE.get(), this.models()
                .withExistingParent("deep_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.GLOOM_CRYSTAL.get(), this.models()
                .withExistingParent("gloom_crystal", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/amethyst_block")));
        this.simpleBlockWithItem(ModBlocks.CRYO_ROCK.get(), this.cubeAll(ModBlocks.CRYO_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.CRYO_COBBLESTONE.get(), this.cubeAll(ModBlocks.CRYO_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.NITROGEN_FROST.get(), this.models()
                .withExistingParent("nitrogen_frost", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        this.simpleBlockWithItem(ModBlocks.NITROGEN_VENT.get(), this.models()
                .withExistingParent("nitrogen_vent", this.modLoc("block/tinted_top_overlay"))
                .texture("base", this.mcLoc("block/packed_ice")).texture("top_overlay", this.modLoc("block/sulfur_vent_top_overlay")));
        this.simpleBlockWithItem(ModBlocks.MANTLE_ROCK.get(), this.cubeAll(ModBlocks.MANTLE_ROCK.get()));
        this.simpleBlockWithItem(ModBlocks.MANTLE_COBBLESTONE.get(), this.cubeAll(ModBlocks.MANTLE_COBBLESTONE.get()));
        this.simpleBlockWithItem(ModBlocks.AZURE_ICE.get(), this.models()
                .withExistingParent("azure_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.PRESSURE_CRYSTAL.get(), this.models()
                .withExistingParent("pressure_crystal", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/diamond_block")));
        this.jupiter();
    }

    /** Jupiter: its own cloud stone and cobblestone; frost uses tinted Minecraft textures. */
    private void jupiter() {
        for (var block : java.util.List.of(ModBlocks.CLOUD_ROCK, ModBlocks.CLOUD_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.ZONE_FROST.get(), this.models()
                .withExistingParent("zone_frost", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        this.simpleBlockWithItem(ModBlocks.BELT_FROST.get(), this.models()
                .withExistingParent("belt_frost", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        for (var pair : java.util.List.of(java.util.Map.entry(ModBlocks.ZONE_FROST_SLAB, "zone_frost"),
                java.util.Map.entry(ModBlocks.BELT_FROST_SLAB, "belt_frost"))) {
            String name = pair.getKey().getId().getPath();
            ModelFile bottom = this.models().withExistingParent(name, this.modLoc("block/tinted_slab")).texture("all", this.mcLoc("block/snow"));
            ModelFile top = this.models().withExistingParent(name + "_top", this.modLoc("block/tinted_slab_top")).texture("all", this.mcLoc("block/snow"));
            this.slabBlock(pair.getKey().get(), bottom, top, this.models().getExistingFile(this.modLoc("block/" + pair.getValue())));
            this.simpleBlockItem(pair.getKey().get(), bottom);
        }
    }

    /** Miranda: its own stone and cobblestone; banded ice uses a tinted Minecraft texture. */
    private void miranda() {
        for (var block : java.util.List.of(ModBlocks.PATCHWORK_ROCK, ModBlocks.PATCHWORK_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.BANDED_ICE.get(), this.models()
                .withExistingParent("banded_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
    }

    /** Saturn: its own stone and cobblestone; ammonia frost uses a tinted Minecraft texture. */
    private void saturn() {
        for (var block : java.util.List.of(ModBlocks.COLUMN_ROCK, ModBlocks.COLUMN_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.AMMONIA_FROST.get(), this.models()
                .withExistingParent("ammonia_frost", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
    }

    /** Titan: its own stone and cobblestone; tholin sand and ice pebbles use tinted Minecraft textures; liquid methane. */
    private void titan() {
        for (var block : java.util.List.of(ModBlocks.HAZED_ROCK, ModBlocks.HAZED_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.THOLIN_SAND.get(), this.models()
                .withExistingParent("tholin_sand", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/sand")));
        this.simpleBlockWithItem(ModBlocks.ICE_PEBBLES.get(), this.models()
                .withExistingParent("ice_pebbles", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/gravel")));
        // fluids are drawn by the fluid renderer; the block model only gives the breaking particles
        for (var molten : com.adastraplanets.registry.ModMoltenMetals.ALL) {
            this.simpleBlock(molten.block.get(), this.models().getBuilder("molten_" + molten.metal).texture("particle", this.modLoc("block/molten_metal_still")));
        }
        this.simpleBlock(com.adastraplanets.registry.ModFluids.LIQUID_METHANE_BLOCK.get(),
                this.models().getBuilder("liquid_methane").texture("particle", this.mcLoc("block/water_still")));
    }

    /** Iapetus: its own stone and cobblestone; umber dust uses a tinted Minecraft texture. */
    private void iapetus() {
        for (var block : java.util.List.of(ModBlocks.VEILED_ROCK, ModBlocks.VEILED_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.UMBER_DUST.get(), this.models()
                .withExistingParent("umber_dust", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/coarse_dirt")));
    }

    /** Rhea: its own stone and cobblestone; frost regolith uses a tinted Minecraft texture. */
    private void rhea() {
        for (var block : java.util.List.of(ModBlocks.ICEBOUND_ROCK, ModBlocks.ICEBOUND_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.FROST_REGOLITH.get(), this.models()
                .withExistingParent("frost_regolith", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/gravel")));
    }

    /** Enceladus: its own stone and cobblestone; plume snow and fissure ice use tinted Minecraft textures. */
    private void enceladus() {
        for (var block : java.util.List.of(ModBlocks.GLACIAL_ROCK, ModBlocks.GLACIAL_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.PLUME_SNOW.get(), this.models()
                .withExistingParent("plume_snow", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/snow")));
        this.simpleBlockWithItem(ModBlocks.FISSURE_ICE.get(), this.models()
                .withExistingParent("fissure_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
    }

    /** Callisto: its own stone and cobblestone; dark dust uses a tinted Minecraft texture. */
    private void callisto() {
        for (var block : java.util.List.of(ModBlocks.AGED_ROCK, ModBlocks.AGED_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.ANCIENT_DUST.get(), this.models()
                .withExistingParent("ancient_dust", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/coarse_dirt")));
    }

    /** Ganymede: its own stone and cobblestone; dark regolith and grooved ice use tinted Minecraft textures. */
    private void ganymede() {
        for (var block : java.util.List.of(ModBlocks.SULCUS_ROCK, ModBlocks.SULCUS_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.DARK_REGOLITH.get(), this.models()
                .withExistingParent("dark_regolith", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/tuff")));
        this.simpleBlockWithItem(ModBlocks.GROOVE_ICE.get(), this.models()
                .withExistingParent("groove_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
    }

    /** Io: its own stone and cobblestone; sulfur crust, cinder and the plume vent use tinted Minecraft textures. */
    private void io() {
        for (var block : java.util.List.of(ModBlocks.SULFUROUS_ROCK, ModBlocks.SULFUROUS_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.SULFUR_CRUST.get(), this.models()
                .withExistingParent("sulfur_crust", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/calcite")));
        this.simpleBlockWithItem(ModBlocks.CINDER.get(), this.models()
                .withExistingParent("cinder", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/blackstone")));
        this.simpleBlockWithItem(ModBlocks.SULFUR_VENT.get(), this.models()
                .withExistingParent("sulfur_vent", this.modLoc("block/tinted_top_overlay"))
                .texture("base", this.mcLoc("block/calcite")).texture("top_overlay", this.modLoc("block/sulfur_vent_top_overlay")));
    }

    /** Phobos and Deimos: their own stones and cobblestones; regolith and dust are Minecraft's textures, tinted. */
    private void marsMoons() {
        java.util.Map<net.neoforged.neoforge.registries.DeferredBlock<?>, String> tinted = java.util.Map.of(
                ModBlocks.CARBON_REGOLITH, "block/gravel", ModBlocks.PALE_DUST, "block/sand");
        tinted.forEach((block, texture) -> this.simpleBlockWithItem(block.get(), this.models()
                .withExistingParent(block.getId().getPath(), this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc(texture))));
        for (var block : java.util.List.of(ModBlocks.CARBON_ROCK, ModBlocks.CARBON_COBBLESTONE, ModBlocks.PALE_ROCK,
                ModBlocks.PALE_COBBLESTONE)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        // each ore sits in its world's own stone
        java.util.Map<com.adastraplanets.registry.ModMaterials.MaterialSet, String> hosts = java.util.Map.ofEntries(
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.UMBRITE, "block/carbon_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.PALLORITE, "block/pale_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.IGNEUM, "block/sulfurous_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.AURORIUM, "block/sulcus_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.EONYX, "block/aged_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.STORMIUM, "block/cloud_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.NIVALUM, "block/glacial_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.FERROLITH, "block/icebound_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.VESPERINE, "block/veiled_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.THOLINAR, "block/hazed_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.CASSINIUM, "block/column_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.MOSAICON, "block/patchwork_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.CHASMAL, "block/rift_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.OBSCUREX, "block/murk_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.AZURANE, "block/mantle_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.NITRALIS, "block/cryo_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.TEMPESTINE, "block/gloom_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.MORDRINE, "block/slate_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.CORDIUM, "block/glacier_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.SHARDEX, "block/fracture_rock"),
                java.util.Map.entry(com.adastraplanets.registry.ModMaterials.ALBEDON, "block/mirror_rock"));
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            this.crystal(crystal);
        }
        this.ancient();
        this.accessReader();
        for (var material : com.adastraplanets.registry.ModMaterials.ALL) {
            this.simpleBlockWithItem(material.block.get(), this.models().getExistingFile(this.modLoc("block/" + material.name() + "_block")));
            this.simpleBlockWithItem(material.rawBlock.get(), this.cubeAll(material.rawBlock.get()));
            this.simpleBlockWithItem(material.ore.get(), this.models()
                    .withExistingParent(material.name() + "_ore", this.modLoc("block/ore_overlay"))
                    .texture("base", this.modLoc(hosts.get(material)))
                    .texture("particle", this.modLoc(hosts.get(material)))
                    .texture("overlay", this.modLoc("block/" + material.name() + "_ore_overlay")));
        }
    }

    /** The ruins' plain blocks; the glowing ones (runes, sockets, gates) have hand-written models with light data. */
    private void ancient() {
        for (var block : java.util.List.of(com.adastraplanets.registry.ModAncient.ANCIENT_STONE, com.adastraplanets.registry.ModAncient.ANCIENT_BRICKS, com.adastraplanets.registry.ModAncient.CRACKED_ANCIENT_BRICKS)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        var bricks = this.modLoc("block/ancient_bricks");
        this.stairsBlock(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get(), bricks);
        this.simpleBlockItem(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get(), this.models().getExistingFile(this.modLoc("block/ancient_brick_stairs")));
        this.slabBlock(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get(), bricks, bricks);
        this.simpleBlockItem(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get(), this.models().getExistingFile(this.modLoc("block/ancient_brick_slab")));
        this.wallBlock(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL.get(), bricks);
        this.itemModels().wallInventory("ancient_brick_wall", bricks);
        this.axisBlock(com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR.get(), this.modLoc("block/ancient_pillar"), this.modLoc("block/ancient_pillar_top"));
        this.simpleBlockItem(com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR.get(), this.models().getExistingFile(this.modLoc("block/ancient_pillar")));
    }

    /** The expedition's access reader: its front red until a card is swiped, then green. */
    private void accessReader() {
        var side = this.modLoc("block/access_reader_side");
        var locked = this.models().orientable("access_reader", side, this.modLoc("block/access_reader_front"), side);
        var granted = this.models().orientable("access_reader_granted", side, this.modLoc("block/access_reader_front_granted"), side);
        this.horizontalBlock(com.adastraplanets.registry.ModExpedition.ACCESS_READER.get(),
                state -> state.getValue(com.adastraplanets.block.AccessReaderBlock.GRANTED) ? granted : locked);
        this.simpleBlockItem(com.adastraplanets.registry.ModExpedition.ACCESS_READER.get(), locked);
    }

    private void crystal(com.adastraplanets.registry.ModCrystals.CrystalSet crystal) {
        String name = crystal.name();
        this.simpleBlockWithItem(crystal.block.get(), this.models().withExistingParent(name + "_crystal_block",
                this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/amethyst_block")));
        this.simpleBlockWithItem(crystal.budding.get(), this.models().withExistingParent("budding_" + name + "_crystal",
                this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/budding_amethyst")));
        java.util.List<String> textures = java.util.List.of("small_amethyst_bud", "medium_amethyst_bud", "large_amethyst_bud", "amethyst_cluster");
        for (int i = 0; i < 4; i++) {
            var block = crystal.growths().get(i).get();
            String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
            var model = this.models().withExistingParent(id, this.modLoc("block/tinted_cross"))
                    .texture("cross", this.mcLoc("block/" + textures.get(i))).renderType("cutout");
            this.directionalBlock(block, model);
            this.itemModels().withExistingParent(id, this.mcLoc("item/generated")).texture("layer0", this.mcLoc("block/" + textures.get(i)));
        }
    }

    private void europa() {
        for (var block : java.util.List.of(ModBlocks.ABYSSAL_ROCK, ModBlocks.ABYSSAL_COBBLESTONE, ModBlocks.CHIMNEY_ROCK,
                ModBlocks.RAW_ABYSSIUM_BLOCK)) {
            this.simpleBlockWithItem(block.get(), this.cubeAll(block.get()));
        }
        this.simpleBlockWithItem(ModBlocks.ABYSSIUM_BLOCK.get(), this.models().getExistingFile(this.modLoc("block/abyssium_block")));
        // The ice blocks point at Minecraft's own textures (never copied into the mod) and are tinted in game (see
        // TintedBlocks), so they also follow whatever resource pack the player uses.
        this.simpleBlockWithItem(ModBlocks.CRUST_ICE.get(), this.models()
                .withExistingParent("crust_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/calcite")));
        this.simpleBlockWithItem(ModBlocks.RUSTED_ICE.get(), this.models()
                .withExistingParent("rusted_ice", this.modLoc("block/tinted_cube_all")).texture("all", this.mcLoc("block/packed_ice")));
        this.simpleBlockWithItem(ModBlocks.RIME.get(), this.models()
                .withExistingParent("rime", this.mcLoc("block/cube_all")).texture("all", this.mcLoc("block/snow")));
        // the ore sits in our own abyssal rock, so the base texture is ours
        this.simpleBlockWithItem(ModBlocks.ABYSSIUM_ORE.get(), this.models()
                .withExistingParent("abyssium_ore", this.modLoc("block/ore_overlay"))
                .texture("base", this.modLoc("block/abyssal_rock"))
                .texture("overlay", this.modLoc("block/abyssium_ore_overlay"))
                .texture("particle", this.modLoc("block/abyssal_rock")));
        this.simpleBlockWithItem(ModBlocks.HYDROTHERMAL_VENT.get(), this.models().cubeBottomTop("hydrothermal_vent",
                this.modLoc("block/hydrothermal_vent_side"), this.modLoc("block/chimney_rock"), this.modLoc("block/hydrothermal_vent_top")));
        // same tinted ice as the crust, with the vent's mouth drawn over the top
        this.simpleBlockWithItem(ModBlocks.GEYSER.get(), this.models()
                .withExistingParent("geyser", this.modLoc("block/tinted_top_overlay"))
                .texture("base", this.mcLoc("block/calcite")).texture("top_overlay", this.modLoc("block/geyser_top_overlay")));
    }

    private void largeLaunchPad() {
        this.launchPad(ModBlocks.LARGE_LAUNCH_PAD.get(), "large_launch_pad");
        this.launchPad(ModBlocks.HUGE_LAUNCH_PAD.get(), "huge_launch_pad");
    }

    /** Each part is a 2px slab showing its own 16x16 slice of the whole pad's texture (80x80 for 5x5, 112x112 for 7x7). */
    private void launchPad(com.adastraplanets.block.MultiblockLaunchPadBlock block, String name) {
        int size = block.size();
        float slice = 16f / size;
        ResourceLocation top = this.modLoc("block/" + name);
        ResourceLocation side = this.modLoc("block/large_launch_pad_side");
        MultiPartBlockStateBuilder multipart = this.getMultipartBuilder(block);
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                float u = x * slice;
                float v = z * slice;
                ModelFile model = this.models().withExistingParent(name + "_" + x + "_" + z, this.mcLoc("block/block"))
                        .texture("top", top).texture("side", side).texture("particle", top)
                        .element().from(0, 0, 0).to(16, 2, 16)
                        .face(Direction.UP).uvs(u, v, u + slice, v + slice).texture("#top").end()
                        .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.DOWN).end()
                        .face(Direction.NORTH).uvs(0, 0, 16, 2).texture("#side").end()
                        .face(Direction.SOUTH).uvs(0, 0, 16, 2).texture("#side").end()
                        .face(Direction.EAST).uvs(0, 0, 16, 2).texture("#side").end()
                        .face(Direction.WEST).uvs(0, 0, 16, 2).texture("#side").end()
                        .end();
                multipart.part().modelFile(model).addModel()
                        .condition(block.xProperty(), x).condition(block.zProperty(), z).end();
            }
        }
        // item: the whole pad on one slab, held over the head like Ad Astra's launch pad (ad_astra:held_over_head).
        // A model may not reach past three blocks (-16..32), so the slab is three blocks wide and the hand transforms,
        // copied from Ad Astra's pad, scale it up to its real size.
        float real = size / 3f;
        this.itemModels().withExistingParent(name, this.mcLoc("block/block"))
                .texture("top", top).texture("side", side).texture("particle", top)
                .guiLight(net.minecraft.client.renderer.block.model.BlockModel.GuiLight.FRONT)
                .element().from(-16, 0, -16).to(32, 2, 32)
                .allFaces((direction, face) -> face.texture(direction.getAxis().isVertical() ? "#top" : "#side")
                        .uvs(0, 0, 16, direction.getAxis().isVertical() ? 16 : 2))
                .end()
                .transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(85, 0, 0).translation(-5.5F, 0, 5.75F).scale(real).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(85, 0, 0).translation(-5.5F, 0, 5.75F).scale(real).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(-17, 0, 0).translation(-9, 24, 9.5F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(-17, 0, 0).translation(-9, 24, 9.5F).end()
                .transform(ItemDisplayContext.GROUND).translation(0, 2.75F, 0).scale(0.5F).end()
                .transform(ItemDisplayContext.GUI).rotation(30, 225, 0).translation(0, 2, 0).scale(0.23F).end()
                .transform(ItemDisplayContext.FIXED).rotation(90, 0, 0).translation(0, 0, 10.5F).scale(0.67F, 1.55F, 0.67F).end()
                .end();
    }
}
