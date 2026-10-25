package com.adastraplanets;

import com.adastraplanets.api.event.EventHooks;
import com.adastraplanets.config.AdAstraPlanetsConfig;
import com.adastraplanets.content.GeneratedResources;
import com.adastraplanets.content.StartupContent;
import com.adastraplanets.environment.EnvironmentHazards;
import com.adastraplanets.environment.Environments;
import com.adastraplanets.environment.LinerSlots;
import com.adastraplanets.config.ConfigConditions;
import com.adastraplanets.registry.ModBlockEntityTypes;
import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModFluids;
import com.adastraplanets.registry.ModDataMaps;
import com.adastraplanets.registry.ModFeatures;
import com.adastraplanets.registry.ModMaterials;
import com.adastraplanets.world.InsulatedOceans;
import com.adastraplanets.registry.ModCreativeTabs;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.registry.ModItems;
import com.adastraplanets.rocket.RocketDefinitions;
import com.adastraplanets.rocket.SyncRocketDefinitionsPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(AdAstraPlanets.MOD_ID)
public final class AdAstraPlanets {
    public static final String MOD_ID = "adastraplanets";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdAstraPlanets(IEventBus modBus, ModContainer container) {
        // development check of the landmarks as the world builds them (the verifyStructures run)
        if (com.adastraplanets.debug.StructureVerifier.enabled()) {
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(com.adastraplanets.debug.StructureVerifier.class);
            if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
                com.adastraplanets.debug.VerifyClient.init();
                net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(com.adastraplanets.debug.VerifyClient.class);
            }
        }
        // content.json must be read before the registers are attached: extra blocks, items and rockets join them here
        StartupContent content = StartupContent.load();
        content.rockets().forEach(rocket -> {
            ModEntityTypes.rocket(rocket.id(), rocket.width(), rocket.height());
            ModItems.rocket(rocket.id());
        });
        content.ores().forEach(ore -> ModItems.extraOre(ModBlocks.extraOre(ore)));
        // the built-in globes register as ModGlobes loads; content.json's join them
        content.globes().forEach(globe -> com.adastraplanets.registry.ModGlobes.register(globe.id()));
        com.adastraplanets.registry.ModGlobes.blocks();
        modBus.addListener(GeneratedResources::addPacks);

        ModMaterials.init();
        com.adastraplanets.registry.ModCrystals.init();
        com.adastraplanets.registry.ModAncient.init();
        com.adastraplanets.registry.ModExpedition.init();
        com.adastraplanets.registry.ModSuit.init();
        com.adastraplanets.registry.ModMoltenMetals.init();
        com.adastraplanets.registry.ModSuit.MATERIALS.register(modBus);
        ModFluids.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntityTypes.ENTITY_TYPES.register(modBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modBus);
        com.adastraplanets.registry.ModRecipes.TYPES.register(modBus);
        com.adastraplanets.registry.ModRecipes.SERIALIZERS.register(modBus);
        com.adastraplanets.registry.ModMenus.MENUS.register(modBus);
        ModFeatures.FEATURES.register(modBus);
        ModFeatures.DENSITY_FUNCTION_TYPES.register(modBus);
        ModFeatures.STRUCTURE_TYPES.register(modBus);
        modBus.addListener(ModDataMaps::register);
        ModCreativeTabs.TABS.register(modBus);
        ConfigConditions.CONDITIONS.register(modBus);
        AdAstraPlanetsConfig.FILES.forEach((topic, spec) ->
                container.registerConfig(ModConfig.Type.COMMON, spec, AdAstraPlanetsConfig.fileName(topic)));

        modBus.addListener(AdAstraPlanets::registerPayloads);
        NeoForge.EVENT_BUS.addListener(AdAstraPlanets::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(AdAstraPlanets::syncDatapackContents);
        EventHooks.register();
        InsulatedOceans.register();
        com.adastraplanets.debug.OreHighlights.register();
        com.adastraplanets.world.CorePressure.register();
        NeoForge.EVENT_BUS.addListener(EnvironmentHazards::tick);
        NeoForge.EVENT_BUS.addListener(com.adastraplanets.item.ArmorStats::apply);
        com.adastraplanets.registry.ModSuit.hookAdAstra();
        if (LinerSlots.available()) {
            // kept behind the check so Curios classes are never touched without Curios
            modBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) ->
                    event.enqueueWork(com.adastraplanets.compat.curios.CuriosSuit::register));
        }
        if (!LinerSlots.available()) {
            LOGGER.warn("Curios is not installed: the environmental liner system is off");
        }
        if (ModList.get().isLoaded("kubejs")) {
            // kept behind the check so KubeJS classes are never touched without KubeJS
            com.adastraplanets.compat.kubejs.AdAstraPlanetsKubeEvents.bridge();
        }
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SyncRocketDefinitionsPayload.TYPE, SyncRocketDefinitionsPayload.STREAM_CODEC,
                SyncRocketDefinitionsPayload::handle);
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RocketDefinitions(event.getConditionContext(), event.getRegistryAccess()));
        event.addListener(new Environments());
    }

    private static void syncDatapackContents(OnDatapackSyncEvent event) {
        SyncRocketDefinitionsPayload payload = new SyncRocketDefinitionsPayload(RocketDefinitions.all());
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
