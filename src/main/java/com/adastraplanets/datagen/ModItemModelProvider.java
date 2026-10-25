package com.adastraplanets.datagen;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModGlobes;
import com.adastraplanets.registry.ModItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper files) {
        super(output, AdAstraPlanets.MOD_ID, files);
    }

    @Override
    protected void registerModels() {
        ModGlobes.items().keySet().forEach(id -> this.withExistingParent(id, this.modLoc("item/globe")));
        this.basicItem(ModItems.RAW_HELIONITE.get());
        this.basicItem(ModItems.HELIONITE_INGOT.get());
        this.basicItem(ModItems.HELIONITE_NUGGET.get());
        this.basicItem(ModItems.HELIONITE_PLATE.get());
        // rocket parts share one grey base per part (layer 0, tinted with the material's colour in TintedBlocks) and
        // untinted steel fittings (layer 1); a new material only needs a colour
        java.util.Map<net.neoforged.neoforge.registries.DeferredItem<?>, String> parts = java.util.Map.ofEntries(
                java.util.Map.entry(ModItems.UMBRITE_FIN, "fin"),
                java.util.Map.entry(ModItems.IGNEUM_FIN, "fin"),
                java.util.Map.entry(ModItems.UMBRITE_TANK, "tank"),
                java.util.Map.entry(ModItems.PALLORITE_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.HELIONITE_ENGINE, "engine"),
                java.util.Map.entry(ModItems.EONYX_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.STORMIUM_ENGINE, "engine"),
                java.util.Map.entry(ModItems.ABYSSIUM_TANK, "tank"),
                java.util.Map.entry(ModItems.NIVALUM_TANK, "tank"),
                java.util.Map.entry(ModItems.FERROLITH_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.VESPERINE_FIN, "fin"),
                java.util.Map.entry(ModItems.THOLINAR_ENGINE, "engine"),
                java.util.Map.entry(ModItems.MOSAICON_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.CHASMAL_TANK, "tank"),
                java.util.Map.entry(ModItems.OBSCUREX_FIN, "fin"),
                java.util.Map.entry(ModItems.AZURANE_ENGINE, "engine"),
                java.util.Map.entry(ModItems.NITRALIS_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.NITRALIS_FIN, "fin"),
                java.util.Map.entry(ModItems.TEMPESTINE_TANK, "tank"),
                java.util.Map.entry(ModItems.TEMPESTINE_ENGINE, "engine"),
                java.util.Map.entry(ModItems.MORDRINE_NOSE_CONE, "nose_cone"),
                java.util.Map.entry(ModItems.MORDRINE_FIN, "fin"),
                java.util.Map.entry(ModItems.CORDIUM_TANK, "tank"),
                java.util.Map.entry(ModItems.CORDIUM_ENGINE, "engine"));
        parts.forEach((item, part) -> this.withExistingParent(item.getId().getPath(), this.mcLoc("item/generated"))
                .texture("layer0", this.modLoc("item/rocket_part/" + part))
                .texture("layer1", this.modLoc("item/rocket_part/" + part + "_fittings")));
        this.basicItem(com.adastraplanets.registry.ModExpedition.ACCESS_CARD.get());
        com.adastraplanets.registry.ModSuit.PIECES.forEach(piece -> this.basicItem(piece.get()));
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            this.withExistingParent(crystal.core.getId().getPath(), this.mcLoc("item/generated"))
                    .texture("layer0", this.modLoc("item/light_core")).texture("layer1", this.modLoc("item/light_core_frame"));
            this.withExistingParent(crystal.shard.getId().getPath(), this.mcLoc("item/generated"))
                    .texture("layer0", this.mcLoc("item/amethyst_shard"));
        }
        for (var material : com.adastraplanets.registry.ModMaterials.ALL) {
            this.basicItem(material.raw.get());
            this.basicItem(material.ingot.get());
            this.basicItem(material.nugget.get());
            this.basicItem(material.plate.get());
        }
        this.basicItem(ModItems.RAW_ABYSSIUM.get());
        this.basicItem(ModItems.ABYSSIUM_INGOT.get());
        this.basicItem(ModItems.ABYSSIUM_NUGGET.get());
        this.basicItem(ModItems.ABYSSIUM_PLATE.get());
        for (int level = 1; level <= ModItems.LINER_LEVELS; level++) {
            for (String piece : ModItems.LINER_PIECES) {
                this.basicItem(ModItems.liner(piece, level));
            }
        }

        this.basicItem(ModItems.ORE_SCANNER.get());
        this.basicItem(ModItems.PRESSURE_DIAMOND.get());
        // Minecraft's empty bucket (referenced) under our liquid
        // a bucket drawn with its fluid in it (NeoForge's fluid container model)
        for (var molten : com.adastraplanets.registry.ModMoltenMetals.ALL) {
            this.getBuilder(molten.bucket.getId().getPath())
                    .parent(new net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile("neoforge:item/bucket"))
                    .customLoader(net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder::begin)
                    .fluid(molten.source.get()).applyTint(true).end();
        }
        this.withExistingParent("liquid_methane_bucket", this.mcLoc("item/generated"))
                .texture("layer0", this.mcLoc("item/bucket")).texture("layer1", this.modLoc("item/liquid_methane_bucket_liquid"));
        this.rocketItem(ModItems.TIER_5_ROCKET.getId().getPath(), 1.0F, 0.1302F, 2.69F, -6.42F, 0.931F);
        // the Tier 6 is about 12.7 blocks tall against the Tier 5's 9
        this.rocketItem(ModItems.TIER_6_ROCKET.getId().getPath(), 0.71F, 0.0873F, 2.60F, -5.44F, 0.665F);
        // the Tier 7 is about 14.7 blocks tall
        this.rocketItem(ModItems.TIER_7_ROCKET.getId().getPath(), 0.61F, 0.0827F, 2.54F, -6.33F, 0.590F);
        this.rocketItem(ModItems.TIER_8_ROCKET.getId().getPath(), 0.5F, 0.0784F, 2.25F, -5.99F, 0.574F);
        this.rocketItem(ModItems.TIER_9_ROCKET.getId().getPath(), 0.48F, 0.0682F, 3.12F, -6.36F, 0.486F);
        this.rocketItem(ModItems.TIER_10_ROCKET.getId().getPath(), 0.45F, 0.0632F, 2.42F, -5.40F, 0.484F);
        this.rocketItem(ModItems.ANCIENT_SHIP.getId().getPath(), 0.65F, 0.0940F, 2.99F, -5.96F, 0.689F);
    }

    /**
     * Rendered by GeckoLib in the same space as Ad Astra's rockets (see TieredRocketItemRenderer), so these transforms
     * mirror theirs; GUI/ground/fixed are scaled down because our rockets are taller (fit scales a taller one further).
     */
    /**
     * A rocket's item model. fit scales it on the ground and in item frames; the inventory icon gets its own scale and
     * translation so every rocket sits centred in its slot whatever its shape; in the hands it is placed as Ad Astra
     * places its Tier 4 rocket, shrunk by held so it looks no bigger than that one (values fitted
     * to each model's bounds).
     */
    private void rocketItem(String id, float fit, float guiScale, float guiX, float guiY, float held) {
        this.getBuilder(id)
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .guiLight(BlockModel.GuiLight.FRONT)
                .transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(180, 0, -90).translation(-35, 4.5F, -14).scale(0.8F * held).end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(180, 0, -90).translation(-35, -7, -14).scale(0.8F * held).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(80, 180, 90).translation(-25, 20, 6).scale(0.5F * held).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(80, 180, 90).translation(-25, 18, 0).scale(0.5F * held).end()
                .transform(ItemDisplayContext.GROUND).translation(2, 2, 2).scale(0.18F * fit).end()
                .transform(ItemDisplayContext.GUI).rotation(-41, -170, -26).translation(guiX, guiY, 0).scale(guiScale).end()
                .transform(ItemDisplayContext.HEAD).translation(2.5F, 9, 2.25F).scale(0.27F).end()
                .transform(ItemDisplayContext.FIXED).translation(1, -2.5F * fit, 0).scale(0.18F * fit).end()
                .end();
    }
}
