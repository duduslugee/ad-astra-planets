package com.adastraplanets.datagen;

import com.adastraplanets.registry.ModMaterials;

import java.util.Map;
import net.minecraft.tags.ItemTags;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.config.ConfigConditions;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.registry.ModItems;
import com.adastraplanets.registry.ModTags;
import earth.terrarium.adastra.common.recipes.machines.CompressingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        this.helioniteMaterials(output);
        this.abyssiumMaterials(output);
        this.materialSets(output);
        this.marsMoonParts(output);
        this.jupiterSystemParts(output);
        this.helioniteParts(output);
        this.tier5Rocket(output);
        this.tier6Rocket(output);
        this.tier7Rocket(output);
        this.tier8Rocket(output);
        this.tier9Rocket(output);
        this.tier10Rocket(output);
        this.largeLaunchPad(output);
        this.liners(output);
        this.workbenches(output);
        this.stones(output);
        this.frostSlabs(output);
    }

    /** Slabs of Jupiter's frost, cut like any vanilla slab. */
    private void frostSlabs(RecipeOutput output) {
        slab(output, RecipeCategory.BUILDING_BLOCKS, ModItems.ZONE_FROST_SLAB.get(), ModItems.ZONE_FROST.get());
        slab(output, RecipeCategory.BUILDING_BLOCKS, ModItems.BELT_FROST_SLAB.get(), ModItems.BELT_FROST.get());
    }

    /** Cobblestone smelts back into its stone, as in vanilla. */
    private void stones(RecipeOutput output) {
        Map<ItemLike, ItemLike> stones = Map.ofEntries(
                Map.entry(ModItems.CARBON_COBBLESTONE.get(), ModItems.CARBON_ROCK.get()),
                Map.entry(ModItems.PALE_COBBLESTONE.get(), ModItems.PALE_ROCK.get()),
                Map.entry(ModItems.ABYSSAL_COBBLESTONE.get(), ModItems.ABYSSAL_ROCK.get()),
                Map.entry(ModItems.SULFUROUS_COBBLESTONE.get(), ModItems.SULFUROUS_ROCK.get()),
                Map.entry(ModItems.SULCUS_COBBLESTONE.get(), ModItems.SULCUS_ROCK.get()),
                Map.entry(ModItems.AGED_COBBLESTONE.get(), ModItems.AGED_ROCK.get()),
                Map.entry(ModItems.CLOUD_COBBLESTONE.get(), ModItems.CLOUD_ROCK.get()),
                Map.entry(ModItems.GLACIAL_COBBLESTONE.get(), ModItems.GLACIAL_ROCK.get()),
                Map.entry(ModItems.ICEBOUND_COBBLESTONE.get(), ModItems.ICEBOUND_ROCK.get()),
                Map.entry(ModItems.VEILED_COBBLESTONE.get(), ModItems.VEILED_ROCK.get()),
                Map.entry(ModItems.HAZED_COBBLESTONE.get(), ModItems.HAZED_ROCK.get()),
                Map.entry(ModItems.COLUMN_COBBLESTONE.get(), ModItems.COLUMN_ROCK.get()),
                Map.entry(ModItems.PATCHWORK_COBBLESTONE.get(), ModItems.PATCHWORK_ROCK.get()),
                Map.entry(ModItems.RIFT_COBBLESTONE.get(), ModItems.RIFT_ROCK.get()),
                Map.entry(ModItems.MURK_COBBLESTONE.get(), ModItems.MURK_ROCK.get()),
                Map.entry(ModItems.MANTLE_COBBLESTONE.get(), ModItems.MANTLE_ROCK.get()),
                Map.entry(ModItems.CRYO_COBBLESTONE.get(), ModItems.CRYO_ROCK.get()),
                Map.entry(ModItems.GLOOM_COBBLESTONE.get(), ModItems.GLOOM_ROCK.get()),
                Map.entry(ModItems.SLATE_COBBLESTONE.get(), ModItems.SLATE_ROCK.get()),
                Map.entry(ModItems.GLACIER_COBBLESTONE.get(), ModItems.GLACIER_ROCK.get()),
                Map.entry(ModItems.FRACTURE_COBBLESTONE.get(), ModItems.FRACTURE_ROCK.get()),
                Map.entry(ModItems.MIRROR_COBBLESTONE.get(), ModItems.MIRROR_ROCK.get()));
        stones.forEach((cobble, stone) -> SimpleCookingRecipeBuilder.smelting(Ingredient.of(cobble), RecipeCategory.BUILDING_BLOCKS, stone, 0.1F, 200)
                .unlockedBy("has_" + name(cobble), has(cobble))
                .save(output, AdAstraPlanets.id("smelting/" + name(stone) + "_from_" + name(cobble))));
    }

    /**
     * The rocket workbenches, each built round the one before it, from what the rockets of the tier before can reach:
     * the Advanced one (Tiers 5-7) from Calorite and Mercury's Helionite round the Ember Light Core, the Stellar one (Tiers 8-10) from the Uranus
     * system's Azurane and Obscurex around a Pressure Diamond and the Halo Light Core.
     */
    private void workbenches(RecipeOutput output) {
        TagKey<Item> calorite = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ad_astra", "calorite_plates"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADVANCED_NASA_WORKBENCH.get())
                .define('C', calorite)
                .define('H', ModTags.Items.PLATES_HELIONITE)
                .define('W', earth.terrarium.adastra.common.registry.ModItems.NASA_WORKBENCH.get())
                // the Ember Light Core, kept in the Expedition Outpost on the Mars moons: explore it before the Tier 5
                .define('E', com.adastraplanets.registry.ModCrystals.EMBER.core.get())
                .pattern("CHC").pattern("HWH").pattern("CEC")
                .unlockedBy("has_helionite_plate", has(ModTags.Items.PLATES_HELIONITE))
                .save(output);
        TagKey<Item> azurane = ModMaterials.AZURANE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STELLAR_NASA_WORKBENCH.get())
                .define('A', azurane)
                .define('O', ModMaterials.OBSCUREX.platesTag())
                .define('D', ModItems.PRESSURE_DIAMOND.get())
                .define('W', ModItems.ADVANCED_NASA_WORKBENCH.get())
                // the Halo Light Core, kept in the Library of the Rings on Titan: explore it before the Tier 8
                .define('H', com.adastraplanets.registry.ModCrystals.HALO.core.get())
                .pattern("ADA").pattern("OWO").pattern("AHA")
                .unlockedBy("has_azurane_plate", has(azurane))
                .save(output);
    }

    /**
     * Level I is sewn from wool around steel plates; each later level wraps the previous piece in the plates of the
     * hull material of the rocket that first reaches its worlds (II Ostrum, III Helionite of the Tier 5, IV Cassinium
     * of the Tier 7, V Tempestine of the Tier 9).
     */
    private void liners(RecipeOutput output) {
        TagKey<Item> steelPlates = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ad_astra", "steel_plates"));
        Map<String, String[]> firstLevel = Map.of(
                "hood", new String[]{"WSW", "W W"},
                "vest", new String[]{"W W", "WSW", "WWW"},
                "leggings", new String[]{"WSW", "W W", "W W"},
                "boots", new String[]{"W W", "S S"});
        Map<Integer, TagKey<Item>> upgradePlates = Map.of(
                2, TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ad_astra", "ostrum_plates")),
                3, ModTags.Items.PLATES_HELIONITE,
                4, ModMaterials.CASSINIUM.platesTag(),
                5, ModMaterials.TEMPESTINE.platesTag());
        for (String piece : ModItems.LINER_PIECES) {
            ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.liner(piece, 1))
                    .define('W', ItemTags.WOOL).define('S', steelPlates);
            for (String row : firstLevel.get(piece)) {
                builder.pattern(row);
            }
            builder.unlockedBy("has_steel_plate", has(steelPlates)).save(output);
            upgradePlates.forEach((level, plates) -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.liner(piece, level))
                    .define('P', plates).define('L', ModItems.liner(piece, level - 1))
                    .pattern(" P ").pattern("PLP").pattern(" P ")
                    .unlockedBy("has_previous_liner", has(ModItems.liner(piece, level - 1)))
                    .save(output));
        }
    }

    /** Helionite and steel around one of Ad Astra's pads: available before tier 6, which needs it. */
    private void largeLaunchPad(RecipeOutput output) {
        TagKey<Item> steelPlates = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ad_astra", "steel_plates"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LARGE_LAUNCH_PAD.get())
                .define('#', ModTags.Items.PLATES_HELIONITE)
                .define('S', steelPlates)
                .define('L', earth.terrarium.adastra.common.registry.ModItems.LAUNCH_PAD.get())
                .pattern("#S#").pattern("SLS").pattern("#S#")
                .unlockedBy("has_launch_pad", has(earth.terrarium.adastra.common.registry.ModItems.LAUNCH_PAD.get()))
                .save(output.withConditions(new ConfigConditions.Option("launch_pads.large_launch_pad")));
        // the 7x7: Cassinium and steel around a 5x5, once the Saturn system is reached
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HUGE_LAUNCH_PAD.get())
                .define('#', ModMaterials.CASSINIUM.platesTag())
                .define('S', steelPlates)
                .define('L', ModItems.LARGE_LAUNCH_PAD.get())
                .pattern("#S#").pattern("SLS").pattern("#S#")
                .unlockedBy("has_large_launch_pad", has(ModItems.LARGE_LAUNCH_PAD.get()))
                .save(output.withConditions(new ConfigConditions.Option("launch_pads.huge_launch_pad")));
    }

    private void helioniteMaterials(RecipeOutput output) {
        // by tag, so any ore variant tagged c:ores/helionite (e.g. one added in content.json) smelts too
        for (TagKey<Item> source : List.of(ModTags.Items.ORES_HELIONITE, ModTags.Items.RAW_MATERIALS_HELIONITE)) {
            String sourceName = source.location().getPath().replace('/', '_');
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(source), RecipeCategory.MISC, ModItems.HELIONITE_INGOT.get(), 1.0F, 200)
                    .unlockedBy("has_" + sourceName, has(source))
                    .save(output, AdAstraPlanets.id("smelting/helionite_ingot_from_" + sourceName));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(source), RecipeCategory.MISC, ModItems.HELIONITE_INGOT.get(), 1.0F, 100)
                    .unlockedBy("has_" + sourceName, has(source))
                    .save(output, AdAstraPlanets.id("blasting/helionite_ingot_from_" + sourceName));
        }

        this.storage(output, ModTags.Items.NUGGETS_HELIONITE, ModItems.HELIONITE_NUGGET.get(), ModTags.Items.INGOTS_HELIONITE, ModItems.HELIONITE_INGOT.get());
        this.storage(output, ModTags.Items.INGOTS_HELIONITE, ModItems.HELIONITE_INGOT.get(), ModTags.Items.STORAGE_BLOCKS_HELIONITE, ModItems.HELIONITE_BLOCK.get());
        this.storage(output, ModTags.Items.RAW_MATERIALS_HELIONITE, ModItems.RAW_HELIONITE.get(), ModTags.Items.STORAGE_BLOCKS_RAW_HELIONITE, ModItems.RAW_HELIONITE_BLOCK.get());

        output.accept(AdAstraPlanets.id("compressing/helionite_plate_from_compressing_helionite_ingots"),
                new CompressingRecipe(100, 20, Ingredient.of(ModTags.Items.INGOTS_HELIONITE), new ItemStack(ModItems.HELIONITE_PLATE.get())), null);
        output.accept(AdAstraPlanets.id("compressing/helionite_plate_from_compressing_helionite_blocks"),
                new CompressingRecipe(800, 20, Ingredient.of(ModTags.Items.STORAGE_BLOCKS_HELIONITE), new ItemStack(ModItems.HELIONITE_PLATE.get(), 9)), null);
    }

    private void abyssiumMaterials(RecipeOutput output) {
        for (TagKey<Item> source : List.of(ModTags.Items.ORES_ABYSSIUM, ModTags.Items.RAW_MATERIALS_ABYSSIUM)) {
            String sourceName = source.location().getPath().replace('/', '_');
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(source), RecipeCategory.MISC, ModItems.ABYSSIUM_INGOT.get(), 1.0F, 200)
                    .unlockedBy("has_" + sourceName, has(source))
                    .save(output, AdAstraPlanets.id("smelting/abyssium_ingot_from_" + sourceName));
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(source), RecipeCategory.MISC, ModItems.ABYSSIUM_INGOT.get(), 1.0F, 100)
                    .unlockedBy("has_" + sourceName, has(source))
                    .save(output, AdAstraPlanets.id("blasting/abyssium_ingot_from_" + sourceName));
        }

        this.storage(output, ModTags.Items.NUGGETS_ABYSSIUM, ModItems.ABYSSIUM_NUGGET.get(), ModTags.Items.INGOTS_ABYSSIUM, ModItems.ABYSSIUM_INGOT.get());
        this.storage(output, ModTags.Items.INGOTS_ABYSSIUM, ModItems.ABYSSIUM_INGOT.get(), ModTags.Items.STORAGE_BLOCKS_ABYSSIUM, ModItems.ABYSSIUM_BLOCK.get());
        this.storage(output, ModTags.Items.RAW_MATERIALS_ABYSSIUM, ModItems.RAW_ABYSSIUM.get(), ModTags.Items.STORAGE_BLOCKS_RAW_ABYSSIUM, ModItems.RAW_ABYSSIUM_BLOCK.get());

        output.accept(AdAstraPlanets.id("compressing/abyssium_plate_from_compressing_abyssium_ingots"),
                new CompressingRecipe(100, 20, Ingredient.of(ModTags.Items.INGOTS_ABYSSIUM), new ItemStack(ModItems.ABYSSIUM_PLATE.get())), null);
        output.accept(AdAstraPlanets.id("compressing/abyssium_plate_from_compressing_abyssium_blocks"),
                new CompressingRecipe(800, 20, Ingredient.of(ModTags.Items.STORAGE_BLOCKS_ABYSSIUM), new ItemStack(ModItems.ABYSSIUM_PLATE.get(), 9)), null);
    }

    /** Nine small items to one large, and back, with ids that don't collide between chains. */
    private void storage(RecipeOutput output, TagKey<Item> smallTag, ItemLike small, TagKey<Item> largeTag, ItemLike large) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, large)
                .define('#', smallTag)
                .pattern("###").pattern("###").pattern("###")
                .unlockedBy("has_" + name(small), has(smallTag))
                .save(output, AdAstraPlanets.id(name(large) + "_from_" + name(small)));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, small, 9)
                .requires(largeTag)
                .unlockedBy("has_" + name(large), has(largeTag))
                .save(output, AdAstraPlanets.id(name(small) + "_from_" + name(large)));
    }

    private void helioniteParts(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HELIONITE_ENGINE.get())
                .define('#', ModTags.Items.PLATES_HELIONITE)
                .define('E', earth.terrarium.adastra.common.registry.ModItems.CALORITE_ENGINE.get())
                .define('F', earth.terrarium.adastra.common.registry.ModItems.FAN.get())
                .pattern("###").pattern("#E#").pattern(" F ")
                .unlockedBy("has_helionite_plate", has(ModTags.Items.PLATES_HELIONITE))
                .save(output);
    }

    /** Tier 5 parts from the Mars moons: Umbrite (Phobos) fins and tanks, a Pallorite (Deimos) nose cone. */
    private void marsMoonParts(RecipeOutput output) {
        TagKey<Item> umbrite = ModMaterials.UMBRITE.platesTag();
        TagKey<Item> pallorite = ModMaterials.PALLORITE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UMBRITE_TANK.get())
                .define('#', umbrite)
                .define('T', earth.terrarium.adastra.common.registry.ModItems.CALORITE_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_umbrite_plate", has(umbrite))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UMBRITE_FIN.get())
                .define('#', umbrite)
                .define('F', earth.terrarium.adastra.common.registry.ModItems.ROCKET_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_umbrite_plate", has(umbrite))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PALLORITE_NOSE_CONE.get())
                .define('#', pallorite)
                .define('N', earth.terrarium.adastra.common.registry.ModItems.ROCKET_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_pallorite_plate", has(pallorite))
                .save(output);
    }

    /** Tier 6 parts from the Jupiter system, each built on its Tier 5 counterpart: an Igneum (Io) fin, an Eonyx
     * (Callisto) nose cone, a Stormium (Jupiter) engine and an Abyssium (Europa) tank. */
    private void jupiterSystemParts(RecipeOutput output) {
        TagKey<Item> igneum = ModMaterials.IGNEUM.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IGNEUM_FIN.get())
                .define('#', igneum)
                .define('F', ModItems.UMBRITE_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_igneum_plate", has(igneum))
                .save(output);
        TagKey<Item> eonyx = ModMaterials.EONYX.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EONYX_NOSE_CONE.get())
                .define('#', eonyx)
                .define('N', ModItems.PALLORITE_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_eonyx_plate", has(eonyx))
                .save(output);
        TagKey<Item> stormium = ModMaterials.STORMIUM.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STORMIUM_ENGINE.get())
                .define('#', stormium)
                .define('E', ModItems.HELIONITE_ENGINE.get())
                .pattern("###").pattern("#E#").pattern("# #")
                .unlockedBy("has_stormium_plate", has(stormium))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ABYSSIUM_TANK.get())
                .define('#', ModTags.Items.PLATES_ABYSSIUM)
                .define('T', ModItems.UMBRITE_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_abyssium_plate", has(ModTags.Items.PLATES_ABYSSIUM))
                .save(output);
        this.saturnSystemParts(output);
    }

    /** Tier 7 parts from the Saturn system, each built on the Tier 6 part it replaces. */
    private void saturnSystemParts(RecipeOutput output) {
        TagKey<Item> nivalum = ModMaterials.NIVALUM.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NIVALUM_TANK.get())
                .define('#', nivalum)
                .define('T', ModItems.ABYSSIUM_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_nivalum_plate", has(nivalum))
                .save(output);
        TagKey<Item> ferrolith = ModMaterials.FERROLITH.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FERROLITH_NOSE_CONE.get())
                .define('#', ferrolith)
                .define('N', ModItems.EONYX_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_ferrolith_plate", has(ferrolith))
                .save(output);
        TagKey<Item> vesperine = ModMaterials.VESPERINE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.VESPERINE_FIN.get())
                .define('#', vesperine)
                .define('F', ModItems.IGNEUM_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_vesperine_plate", has(vesperine))
                .save(output);
        TagKey<Item> tholinar = ModMaterials.THOLINAR.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.THOLINAR_ENGINE.get())
                .define('#', tholinar)
                .define('E', ModItems.STORMIUM_ENGINE.get())
                .pattern("###").pattern("#E#").pattern("# #")
                .unlockedBy("has_tholinar_plate", has(tholinar))
                .save(output);
        this.uranusSystemParts(output);
        this.neptuneSystemParts(output);
        this.plutoSystemParts(output);
    }

    /** Tier 10 parts from the Pluto system, each built on the Tier 9 part it replaces. */
    private void plutoSystemParts(RecipeOutput output) {
        TagKey<Item> mordrine = ModMaterials.MORDRINE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MORDRINE_NOSE_CONE.get())
                .define('#', mordrine)
                .define('N', ModItems.NITRALIS_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_mordrine_plate", has(mordrine))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MORDRINE_FIN.get())
                .define('#', mordrine)
                .define('F', ModItems.NITRALIS_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_mordrine_plate", has(mordrine))
                .save(output);
        TagKey<Item> cordium = ModMaterials.CORDIUM.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CORDIUM_TANK.get())
                .define('#', cordium)
                .define('T', ModItems.TEMPESTINE_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_cordium_plate", has(cordium))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CORDIUM_ENGINE.get())
                .define('#', cordium)
                .define('E', ModItems.TEMPESTINE_ENGINE.get())
                .pattern("###").pattern("#E#").pattern("# #")
                .unlockedBy("has_cordium_plate", has(cordium))
                .save(output);
    }

    /** Tier 9 parts from the Neptune system, each built on the Tier 8 part it replaces. */
    private void neptuneSystemParts(RecipeOutput output) {
        TagKey<Item> nitralis = ModMaterials.NITRALIS.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NITRALIS_NOSE_CONE.get())
                .define('#', nitralis)
                .define('N', ModItems.MOSAICON_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_nitralis_plate", has(nitralis))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NITRALIS_FIN.get())
                .define('#', nitralis)
                .define('F', ModItems.OBSCUREX_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_nitralis_plate", has(nitralis))
                .save(output);
        TagKey<Item> tempestine = ModMaterials.TEMPESTINE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TEMPESTINE_TANK.get())
                .define('#', tempestine)
                .define('T', ModItems.CHASMAL_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_tempestine_plate", has(tempestine))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TEMPESTINE_ENGINE.get())
                .define('#', tempestine)
                .define('E', ModItems.AZURANE_ENGINE.get())
                .pattern("###").pattern("#E#").pattern("# #")
                .unlockedBy("has_tempestine_plate", has(tempestine))
                .save(output);
    }

    /** Tier 8 parts from the Uranus system, each built on the Tier 7 part it replaces. */
    private void uranusSystemParts(RecipeOutput output) {
        TagKey<Item> mosaicon = ModMaterials.MOSAICON.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOSAICON_NOSE_CONE.get())
                .define('#', mosaicon)
                .define('N', ModItems.FERROLITH_NOSE_CONE.get())
                .pattern(" # ").pattern("#N#").pattern("# #")
                .unlockedBy("has_mosaicon_plate", has(mosaicon))
                .save(output);
        TagKey<Item> chasmal = ModMaterials.CHASMAL.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHASMAL_TANK.get())
                .define('#', chasmal)
                .define('T', ModItems.NIVALUM_TANK.get())
                .define('|', earth.terrarium.adastra.common.registry.ModItems.STEEL_ROD.get())
                .pattern("## ").pattern("#T|").pattern("## ")
                .unlockedBy("has_chasmal_plate", has(chasmal))
                .save(output);
        TagKey<Item> obscurex = ModMaterials.OBSCUREX.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.OBSCUREX_FIN.get())
                .define('#', obscurex)
                .define('F', ModItems.VESPERINE_FIN.get())
                .pattern(" # ").pattern("#F#").pattern(" # ")
                .unlockedBy("has_obscurex_plate", has(obscurex))
                .save(output);
        TagKey<Item> azurane = ModMaterials.AZURANE.platesTag();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AZURANE_ENGINE.get())
                .define('#', azurane)
                .define('E', ModItems.THOLINAR_ENGINE.get())
                .define('D', ModItems.PRESSURE_DIAMOND.get())
                .pattern("###").pattern("#E#").pattern("#D#")
                .unlockedBy("has_azurane_plate", has(azurane))
                .save(output);
    }

    /** Smelting, storage and compressing for every material in ModMaterials. */
    private void materialSets(RecipeOutput output) {
        // the ruins' stone: found only in ruins, cut into its other forms like stone bricks
        var stone = com.adastraplanets.registry.ModAncient.ANCIENT_STONE.get();
        var bricks = com.adastraplanets.registry.ModAncient.ANCIENT_BRICKS.get();
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, bricks, 4).define('#', stone).pattern("##").pattern("##")
                .unlockedBy("has_ancient_stone", has(stone)).save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR.get(), 2).define('#', stone).pattern("#").pattern("#")
                .unlockedBy("has_ancient_stone", has(stone)).save(output);
        stairBuilder(com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get(), Ingredient.of(bricks)).unlockedBy("has_ancient_bricks", has(bricks)).save(output);
        slab(output, RecipeCategory.BUILDING_BLOCKS, com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_SLAB.get(), bricks);
        wall(output, RecipeCategory.DECORATIONS, com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_WALL.get(), bricks);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(bricks), RecipeCategory.BUILDING_BLOCKS, com.adastraplanets.registry.ModAncient.CRACKED_ANCIENT_BRICKS.get(), 0.1F, 200)
                .unlockedBy("has_ancient_bricks", has(bricks)).save(output);
        for (var result : java.util.List.of(bricks, com.adastraplanets.registry.ModAncient.ANCIENT_BRICK_STAIRS.get(), com.adastraplanets.registry.ModAncient.ANCIENT_PILLAR.get())) {
            net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), RecipeCategory.BUILDING_BLOCKS, result)
                    .unlockedBy("has_ancient_stone", has(stone))
                    .save(output, AdAstraPlanets.id("stonecutting/" + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(result).getPath()));
        }
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, crystal.block.get())
                    .define('#', crystal.gemsTag()).pattern("##").pattern("##")
                    .unlockedBy("has_" + crystal.name() + "_crystal_shard", has(crystal.gemsTag()))
                    .save(output);
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, crystal.shard.get(), 4)
                    .requires(crystal.block.get())
                    .unlockedBy("has_" + crystal.name() + "_crystal_block", has(crystal.block.get()))
                    .save(output, AdAstraPlanets.id(crystal.name() + "_crystal_shard_from_" + crystal.name() + "_crystal_block"));
        }
        for (ModMaterials.MaterialSet material : ModMaterials.ALL) {
            String name = material.name();
            for (TagKey<Item> source : List.of(material.oresTag(), material.rawTag())) {
                String sourceName = source.location().getPath().replace('/', '_');
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(source), RecipeCategory.MISC, material.ingot.get(), 1.0F, 200)
                        .unlockedBy("has_" + sourceName, has(source))
                        .save(output, AdAstraPlanets.id("smelting/" + name + "_ingot_from_" + sourceName));
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(source), RecipeCategory.MISC, material.ingot.get(), 1.0F, 100)
                        .unlockedBy("has_" + sourceName, has(source))
                        .save(output, AdAstraPlanets.id("blasting/" + name + "_ingot_from_" + sourceName));
            }
            this.storage(output, material.nuggetsTag(), material.nugget.get(), material.ingotsTag(), material.ingot.get());
            this.storage(output, material.ingotsTag(), material.ingot.get(), material.storageTag(), material.blockItem.get());
            this.storage(output, material.rawTag(), material.raw.get(), material.rawStorageTag(), material.rawBlockItem.get());
            output.accept(AdAstraPlanets.id("compressing/" + name + "_plate_from_compressing_" + name + "_ingots"),
                    new CompressingRecipe(100, 20, Ingredient.of(material.ingotsTag()), new ItemStack(material.plate.get())), null);
            output.accept(AdAstraPlanets.id("compressing/" + name + "_plate_from_compressing_" + name + "_blocks"),
                    new CompressingRecipe(800, 20, Ingredient.of(material.storageTag()), new ItemStack(material.plate.get(), 9)), null);
        }
    }

    /** Slot order follows Ad Astra's NASA Workbench: nose cone, six body blocks, then fins, tanks and engine. */
    private void tier5Rocket(RecipeOutput output) {
        // parts from three bodies: Mercury (hull, engine), Phobos (fins, tanks), Deimos (nose cone)
        Ingredient noseCone = Ingredient.of(ModItems.PALLORITE_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModTags.Items.STORAGE_BLOCKS_HELIONITE);
        Ingredient fin = Ingredient.of(ModItems.UMBRITE_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.UMBRITE_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.HELIONITE_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_5_ROCKET.getId()))
                .accept(AdAstraPlanets.id("advanced_nasa_workbench/tier_5_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.ADVANCED, ingredients,
                        new ItemStack(ModItems.TIER_5_ROCKET.get())), null);
    }

    private void tier6Rocket(RecipeOutput output) {
        // parts from the Jupiter system: Callisto (nose cone), Ganymede (hull), Io (fins), Europa (tanks), Jupiter (engine)
        Ingredient noseCone = Ingredient.of(ModItems.EONYX_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModMaterials.AURORIUM.storageTag());
        Ingredient fin = Ingredient.of(ModItems.IGNEUM_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.ABYSSIUM_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.STORMIUM_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_6_ROCKET.getId()))
                .accept(AdAstraPlanets.id("advanced_nasa_workbench/tier_6_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.ADVANCED, ingredients,
                        new ItemStack(ModItems.TIER_6_ROCKET.get())), null);
    }

    private void tier7Rocket(RecipeOutput output) {
        // parts from the Saturn system: Rhea (nose cone), Saturn (hull), Iapetus (fins), Enceladus (tanks), Titan (engine)
        Ingredient noseCone = Ingredient.of(ModItems.FERROLITH_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModMaterials.CASSINIUM.storageTag());
        Ingredient fin = Ingredient.of(ModItems.VESPERINE_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.NIVALUM_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.THOLINAR_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_7_ROCKET.getId()))
                .accept(AdAstraPlanets.id("advanced_nasa_workbench/tier_7_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.ADVANCED, ingredients,
                        new ItemStack(ModItems.TIER_7_ROCKET.get())), null);
    }

    private void tier8Rocket(RecipeOutput output) {
        // parts from the Uranus system: Miranda (nose cone), Uranus (hull, engine), Oberon (fins), Titania (tanks)
        Ingredient noseCone = Ingredient.of(ModItems.MOSAICON_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModMaterials.AZURANE.storageTag());
        Ingredient fin = Ingredient.of(ModItems.OBSCUREX_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.CHASMAL_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.AZURANE_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_8_ROCKET.getId()))
                .accept(AdAstraPlanets.id("stellar_nasa_workbench/tier_8_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.STELLAR, ingredients,
                        new ItemStack(ModItems.TIER_8_ROCKET.get())), null);
    }

    private void tier9Rocket(RecipeOutput output) {
        // parts from the Neptune system: Triton (nose cone, fins), Neptune (hull, tanks, engine)
        Ingredient noseCone = Ingredient.of(ModItems.NITRALIS_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModMaterials.TEMPESTINE.storageTag());
        Ingredient fin = Ingredient.of(ModItems.NITRALIS_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.TEMPESTINE_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.TEMPESTINE_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_9_ROCKET.getId()))
                .accept(AdAstraPlanets.id("stellar_nasa_workbench/tier_9_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.STELLAR, ingredients,
                        new ItemStack(ModItems.TIER_9_ROCKET.get())), null);
    }

    private void tier10Rocket(RecipeOutput output) {
        // parts from the Pluto system: Charon (nose cone, fins), Pluto (hull, tanks, engine)
        Ingredient noseCone = Ingredient.of(ModItems.MORDRINE_NOSE_CONE.get());
        Ingredient body = Ingredient.of(ModMaterials.CORDIUM.storageTag());
        Ingredient fin = Ingredient.of(ModItems.MORDRINE_FIN.get());
        Ingredient tank = Ingredient.of(ModItems.CORDIUM_TANK.get());
        Ingredient engine = Ingredient.of(ModItems.CORDIUM_ENGINE.get());
        List<Ingredient> ingredients = List.of(
                noseCone,
                body, body, body, body, body, body,
                fin, tank, tank, fin,
                fin, engine, fin);
        output.withConditions(new ConfigConditions.RocketEnabled(ModEntityTypes.TIER_10_ROCKET.getId()))
                .accept(AdAstraPlanets.id("stellar_nasa_workbench/tier_10_rocket"),
                new com.adastraplanets.workbench.WorkbenchRecipe(com.adastraplanets.workbench.Workbench.STELLAR, ingredients,
                        new ItemStack(ModItems.TIER_10_ROCKET.get())), null);
    }

    private static String name(ItemLike item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }
}
