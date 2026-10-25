package com.adastraplanets.registry;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.GeyserBlock;
import com.adastraplanets.block.HydrothermalVentBlock;
import com.adastraplanets.block.LargeLaunchPadBlock;
import com.adastraplanets.content.StartupContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AdAstraPlanets.MOD_ID);

    public static final DeferredBlock<DropExperienceBlock> HELIONITE_ORE = BLOCKS.register("helionite_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).mapColor(MapColor.COLOR_ORANGE)));
    public static final DeferredBlock<Block> HELIONITE_BLOCK = BLOCKS.registerSimpleBlock("helionite_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).mapColor(MapColor.COLOR_ORANGE));
    public static final DeferredBlock<Block> RAW_HELIONITE_BLOCK = BLOCKS.registerSimpleBlock("raw_helionite_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_ORANGE));

    // Europa: an ice shell over an ocean with hydrothermal vents (ids stay neutral: no world's name in them)
    public static final DeferredBlock<Block> CRUST_ICE = BLOCKS.registerSimpleBlock("crust_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> RUSTED_ICE = BLOCKS.registerSimpleBlock("rusted_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> RIME = BLOCKS.registerSimpleBlock("rime",
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.4F).sound(SoundType.SNOW));
    public static final DeferredBlock<Block> ABYSSAL_ROCK = BLOCKS.registerSimpleBlock("abyssal_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> ABYSSAL_COBBLESTONE = BLOCKS.registerSimpleBlock("abyssal_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> CHIMNEY_ROCK = BLOCKS.registerSimpleBlock("chimney_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).mapColor(MapColor.TERRACOTTA_BLACK));
    public static final DeferredBlock<HydrothermalVentBlock> HYDROTHERMAL_VENT = BLOCKS.register("hydrothermal_vent",
            () -> new HydrothermalVentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MAGMA_BLOCK).mapColor(MapColor.TERRACOTTA_BLACK)
                    .lightLevel(state -> 12).emissiveRendering((state, level, pos) -> true)));
    public static final DeferredBlock<GeyserBlock> GEYSER = BLOCKS.register("geyser",
            () -> new GeyserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
    public static final DeferredBlock<DropExperienceBlock> ABYSSIUM_ORE = BLOCKS.register("abyssium_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
                    .mapColor(MapColor.COLOR_CYAN).lightLevel(state -> 3)));
    public static final DeferredBlock<Block> ABYSSIUM_BLOCK = BLOCKS.registerSimpleBlock("abyssium_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).mapColor(MapColor.COLOR_CYAN));
    public static final DeferredBlock<Block> RAW_ABYSSIUM_BLOCK = BLOCKS.registerSimpleBlock("raw_abyssium_block",
            BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_IRON_BLOCK).mapColor(MapColor.COLOR_CYAN));

    // Phobos (dark, carbon-rich) and Deimos (pale dust): each has its own stone (drawn) and cobblestone; the loose
    // regolith and dust use Minecraft's textures, tinted (see TintedBlocks)
    public static final DeferredBlock<Block> CARBON_REGOLITH = BLOCKS.registerSimpleBlock("carbon_regolith",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> CARBON_ROCK = BLOCKS.registerSimpleBlock("carbon_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> CARBON_COBBLESTONE = BLOCKS.registerSimpleBlock("carbon_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> PALE_DUST = BLOCKS.registerSimpleBlock("pale_dust",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> PALE_ROCK = BLOCKS.registerSimpleBlock("pale_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> PALE_COBBLESTONE = BLOCKS.registerSimpleBlock("pale_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.SAND));

    // Io: its own sulfurous stone, sulfur crust and volcanic cinder (tinted Minecraft textures), sulfur plume vents
    public static final DeferredBlock<Block> SULFUROUS_ROCK = BLOCKS.registerSimpleBlock("sulfurous_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_YELLOW));
    public static final DeferredBlock<Block> SULFUROUS_COBBLESTONE = BLOCKS.registerSimpleBlock("sulfurous_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_YELLOW));
    public static final DeferredBlock<Block> SULFUR_CRUST = BLOCKS.registerSimpleBlock("sulfur_crust",
            BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(MapColor.COLOR_YELLOW));
    public static final DeferredBlock<Block> CINDER = BLOCKS.registerSimpleBlock("cinder",
            BlockBehaviour.Properties.ofFullCopy(Blocks.BLACKSTONE).mapColor(MapColor.TERRACOTTA_RED));
    public static final DeferredBlock<GeyserBlock> SULFUR_VENT = BLOCKS.register("sulfur_vent",
            () -> new GeyserBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(MapColor.COLOR_YELLOW)));

    // Ganymede: its own icy stone; dark old regolith and bright grooved ice use tinted Minecraft textures
    public static final DeferredBlock<Block> SULCUS_ROCK = BLOCKS.registerSimpleBlock("sulcus_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> SULCUS_COBBLESTONE = BLOCKS.registerSimpleBlock("sulcus_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> DARK_REGOLITH = BLOCKS.registerSimpleBlock("dark_regolith",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).mapColor(MapColor.TERRACOTTA_GRAY));
    public static final DeferredBlock<Block> GROOVE_ICE = BLOCKS.registerSimpleBlock("groove_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());

    // Enceladus: its own bright ice rock; fresh plume snow and the blue ice of the fissures use tinted Minecraft textures
    public static final DeferredBlock<Block> GLACIAL_ROCK = BLOCKS.registerSimpleBlock("glacial_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.ICE));
    public static final DeferredBlock<Block> GLACIAL_COBBLESTONE = BLOCKS.registerSimpleBlock("glacial_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.ICE));
    public static final DeferredBlock<Block> PLUME_SNOW = BLOCKS.registerSimpleBlock("plume_snow",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.SNOW));
    public static final DeferredBlock<Block> FISSURE_ICE = BLOCKS.registerSimpleBlock("fissure_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());

    // Rhea: its own dusty ice rock; grey frost regolith uses a tinted Minecraft texture, the bright wisps reuse rime
    public static final DeferredBlock<Block> ICEBOUND_ROCK = BLOCKS.registerSimpleBlock("icebound_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.QUARTZ));
    public static final DeferredBlock<Block> ICEBOUND_COBBLESTONE = BLOCKS.registerSimpleBlock("icebound_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.QUARTZ));
    public static final DeferredBlock<Block> FROST_REGOLITH = BLOCKS.registerSimpleBlock("frost_regolith",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.COLOR_LIGHT_GRAY));

    // Iapetus: its own dust-veiled stone; the dark dust of its leading face uses a tinted Minecraft texture, the bright
    // face reuses rime
    public static final DeferredBlock<Block> VEILED_ROCK = BLOCKS.registerSimpleBlock("veiled_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> VEILED_COBBLESTONE = BLOCKS.registerSimpleBlock("veiled_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> UMBER_DUST = BLOCKS.registerSimpleBlock("umber_dust",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).mapColor(MapColor.TERRACOTTA_BROWN));

    // Titan: its own haze-stained ice rock; dark tholin sand (dunes) and the rounded ice pebbles of its shores use
    // tinted Minecraft textures
    public static final DeferredBlock<Block> HAZED_ROCK = BLOCKS.registerSimpleBlock("hazed_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_ORANGE));
    public static final DeferredBlock<Block> HAZED_COBBLESTONE = BLOCKS.registerSimpleBlock("hazed_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_ORANGE));
    public static final DeferredBlock<Block> THOLIN_SAND = BLOCKS.registerSimpleBlock("tholin_sand",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.TERRACOTTA_BROWN));
    public static final DeferredBlock<Block> ICE_PEBBLES = BLOCKS.registerSimpleBlock("ice_pebbles",
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).mapColor(MapColor.TERRACOTTA_WHITE));

    // Saturn: its own frozen-ammonia stone, standing in hexagonal columns; ammonia frost uses a tinted Minecraft texture
    public static final DeferredBlock<Block> COLUMN_ROCK = BLOCKS.registerSimpleBlock("column_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> COLUMN_COBBLESTONE = BLOCKS.registerSimpleBlock("column_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> AMMONIA_FROST = BLOCKS.registerSimpleBlock("ammonia_frost",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.SAND));

    // Eris: its own pale frozen crust; drifted snow and ice shards use tinted Minecraft textures (its mirror plains
    // are the bright ice shared with Dysnomia)
    public static final DeferredBlock<Block> MIRROR_ROCK = BLOCKS.registerSimpleBlock("mirror_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.SNOW));
    public static final DeferredBlock<Block> MIRROR_COBBLESTONE = BLOCKS.registerSimpleBlock("mirror_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.SNOW));
    public static final DeferredBlock<Block> DRIFT_SNOW = BLOCKS.registerSimpleBlock("drift_snow",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.SNOW));
    public static final DeferredBlock<Block> ICE_SHARD = BLOCKS.registerSimpleBlock("ice_shard",
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.5F).sound(SoundType.AMETHYST)
                    .lightLevel(state -> 4).requiresCorrectToolForDrops());

    // Dysnomia: its own dark fractured rock; the bright ice of Eris in its fractures uses a tinted Minecraft texture
    public static final DeferredBlock<Block> FRACTURE_ROCK = BLOCKS.registerSimpleBlock("fracture_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> FRACTURE_COBBLESTONE = BLOCKS.registerSimpleBlock("fracture_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<Block> BRIGHT_ICE = BLOCKS.registerSimpleBlock("bright_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());

    // Pluto: its own water-ice bedrock; the heart's nitrogen ice and the methane ice blades use tinted Minecraft textures
    public static final DeferredBlock<Block> GLACIER_ROCK = BLOCKS.registerSimpleBlock("glacier_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> GLACIER_COBBLESTONE = BLOCKS.registerSimpleBlock("glacier_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> CELL_ICE = BLOCKS.registerSimpleBlock("cell_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> METHANE_ICE = BLOCKS.registerSimpleBlock("methane_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(1.4F).sound(SoundType.GLASS).requiresCorrectToolForDrops());

    // Charon: its own grey frosted slate; the red frost of its polar cap uses a tinted Minecraft texture
    public static final DeferredBlock<Block> SLATE_ROCK = BLOCKS.registerSimpleBlock("slate_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> SLATE_COBBLESTONE = BLOCKS.registerSimpleBlock("slate_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> CRIMSON_FROST = BLOCKS.registerSimpleBlock("crimson_frost",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.TERRACOTTA_RED));

    // Neptune: its own dark abyss rock; deep blue ice and glowing dark crystals use tinted Minecraft textures
    public static final DeferredBlock<Block> GLOOM_ROCK = BLOCKS.registerSimpleBlock("gloom_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> GLOOM_COBBLESTONE = BLOCKS.registerSimpleBlock("gloom_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> DEEP_ICE = BLOCKS.registerSimpleBlock("deep_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> GLOOM_CRYSTAL = BLOCKS.registerSimpleBlock("gloom_crystal",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1.5F).sound(SoundType.AMETHYST)
                    .lightLevel(state -> 7).requiresCorrectToolForDrops());

    // Triton: its own cantaloupe crust; pink nitrogen frost uses a tinted Minecraft texture; nitrogen geysers
    public static final DeferredBlock<Block> CRYO_ROCK = BLOCKS.registerSimpleBlock("cryo_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN));
    public static final DeferredBlock<Block> CRYO_COBBLESTONE = BLOCKS.registerSimpleBlock("cryo_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN));
    public static final DeferredBlock<Block> NITROGEN_FROST = BLOCKS.registerSimpleBlock("nitrogen_frost",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.COLOR_PINK));
    public static final DeferredBlock<GeyserBlock> NITROGEN_VENT = BLOCKS.register("nitrogen_vent",
            () -> new GeyserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F)
                    .sound(SoundType.GLASS).requiresCorrectToolForDrops()));

    // Uranus: its own icy mantle rock; cyan ice plains and crystal pinnacles use tinted Minecraft textures
    public static final DeferredBlock<Block> MANTLE_ROCK = BLOCKS.registerSimpleBlock("mantle_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_BLUE));
    public static final DeferredBlock<Block> MANTLE_COBBLESTONE = BLOCKS.registerSimpleBlock("mantle_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_LIGHT_BLUE));
    public static final DeferredBlock<Block> AZURE_ICE = BLOCKS.registerSimpleBlock("azure_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> PRESSURE_CRYSTAL = BLOCKS.registerSimpleBlock("pressure_crystal",
            BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(3.0F).sound(SoundType.AMETHYST)
                    .lightLevel(state -> 6).requiresCorrectToolForDrops());

    // Oberon: its own dark old crust; its dark crater floors reuse the sooty carbon regolith
    public static final DeferredBlock<Block> MURK_ROCK = BLOCKS.registerSimpleBlock("murk_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_GRAY));
    public static final DeferredBlock<Block> MURK_COBBLESTONE = BLOCKS.registerSimpleBlock("murk_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_GRAY));

    // Titania: its own old grey crust
    public static final DeferredBlock<Block> RIFT_ROCK = BLOCKS.registerSimpleBlock("rift_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));
    public static final DeferredBlock<Block> RIFT_COBBLESTONE = BLOCKS.registerSimpleBlock("rift_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));

    // Miranda: its own patchwork ice rock; the banded ice of its coronae uses a tinted Minecraft texture
    public static final DeferredBlock<Block> PATCHWORK_ROCK = BLOCKS.registerSimpleBlock("patchwork_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> PATCHWORK_COBBLESTONE = BLOCKS.registerSimpleBlock("patchwork_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_LIGHT_GRAY));
    public static final DeferredBlock<Block> BANDED_ICE = BLOCKS.registerSimpleBlock("banded_ice",
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(1.2F).sound(SoundType.GLASS).requiresCorrectToolForDrops());

    // Callisto: its own dark ancient stone; dark dust uses a tinted Minecraft texture, bright frost reuses rime
    public static final DeferredBlock<Block> AGED_ROCK = BLOCKS.registerSimpleBlock("aged_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> AGED_COBBLESTONE = BLOCKS.registerSimpleBlock("aged_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> ANCIENT_DUST = BLOCKS.registerSimpleBlock("ancient_dust",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).mapColor(MapColor.TERRACOTTA_BLACK));

    // Jupiter: floating islands of compacted frozen cloud (its own stone) over the void, frost tinted per band
    public static final DeferredBlock<Block> CLOUD_ROCK = BLOCKS.registerSimpleBlock("cloud_rock",
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> CLOUD_COBBLESTONE = BLOCKS.registerSimpleBlock("cloud_cobblestone",
            BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> ZONE_FROST = BLOCKS.registerSimpleBlock("zone_frost",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.SAND));
    public static final DeferredBlock<Block> BELT_FROST = BLOCKS.registerSimpleBlock("belt_frost",
            BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE));
    // slabs laid on one-block steps so players can walk up Jupiter's terrain in its heavy gravity
    public static final DeferredBlock<SlabBlock> ZONE_FROST_SLAB = BLOCKS.register("zone_frost_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.SAND)));
    public static final DeferredBlock<SlabBlock> BELT_FROST_SLAB = BLOCKS.register("belt_frost_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).mapColor(MapColor.TERRACOTTA_ORANGE)));

    public static final DeferredBlock<LargeLaunchPadBlock> LARGE_LAUNCH_PAD = BLOCKS.register("large_launch_pad",
            () -> new LargeLaunchPadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
    public static final DeferredBlock<com.adastraplanets.block.HugeLaunchPadBlock> HUGE_LAUNCH_PAD = BLOCKS.register("huge_launch_pad",
            () -> new com.adastraplanets.block.HugeLaunchPadBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));

    // the rocket workbenches past Ad Astra's: Ad Astra's NASA Workbench model and behaviour, our own art and recipes
    public static final DeferredBlock<com.adastraplanets.workbench.WorkbenchBlock> ADVANCED_NASA_WORKBENCH = BLOCKS.register(
            "advanced_nasa_workbench", () -> new com.adastraplanets.workbench.WorkbenchBlock(com.adastraplanets.workbench.Workbench.ADVANCED,
                    workbenchProperties()));
    public static final DeferredBlock<com.adastraplanets.workbench.WorkbenchBlock> STELLAR_NASA_WORKBENCH = BLOCKS.register(
            "stellar_nasa_workbench", () -> new com.adastraplanets.workbench.WorkbenchBlock(com.adastraplanets.workbench.Workbench.STELLAR,
                    workbenchProperties()));

    private static BlockBehaviour.Properties workbenchProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL)
                .requiresCorrectToolForDrops().noOcclusion();
    }

    /** An ore variant from config/adastraplanets/content.json, behaving like the named vanilla block. */
    public static DeferredBlock<DropExperienceBlock> extraOre(StartupContent.OreEntry ore) {
        return BLOCKS.register(ore.id(), () -> new DropExperienceBlock(ConstantInt.of(0),
                BlockBehaviour.Properties.ofFullCopy(BuiltInRegistries.BLOCK.get(ore.propertiesFrom()))));
    }

    private ModBlocks() {
    }
}
