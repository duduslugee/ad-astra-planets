package com.adastraplanets.datagen;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AdAstraPlanets.MOD_ID)
public final class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        ExistingFileHelper files = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ModBlockStateProvider(output, files));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(output, files));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.English(output));
        generator.addProvider(event.includeClient(), new ModLanguageProvider.Portuguese(output));

        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK)), lookup));
        ModBlockTagsProvider blockTags = generator.addProvider(event.includeServer(), new ModBlockTagsProvider(output, lookup, files));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(output, lookup, blockTags.contentsGetter(), files));
        generator.addProvider(event.includeServer(), new ModFluidTagsProvider(output, lookup, files));
        generator.addProvider(event.includeServer(), new ModRecipeProvider(output, lookup));
    }

    private DataGenerators() {
    }
}
