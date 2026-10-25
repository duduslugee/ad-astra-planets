package com.adastraplanets.datagen;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModTags;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModFluidTagsProvider extends FluidTagsProvider {
    public ModFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper files) {
        super(output, lookup, AdAstraPlanets.MOD_ID, files);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Fluids.TIER_5_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        this.tag(ModTags.Fluids.TIER_6_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        this.tag(ModTags.Fluids.TIER_7_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        this.tag(ModTags.Fluids.TIER_8_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        this.tag(ModTags.Fluids.TIER_9_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        this.tag(ModTags.Fluids.TIER_10_ROCKET_FUEL).addOptionalTag(ModFluidTags.FUEL.location());
        // each molten metal under the common tag the smelteries read (c:molten_<metal>)
        for (var molten : com.adastraplanets.registry.ModMoltenMetals.ALL) {
            this.tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.FLUID,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "molten_" + molten.metal)))
                    .add(molten.source.get(), molten.flowing.get());
        }
        this.tag(ModTags.Fluids.METHANE).add(com.adastraplanets.registry.ModFluids.LIQUID_METHANE.get(),
                com.adastraplanets.registry.ModFluids.FLOWING_LIQUID_METHANE.get());
    }
}
