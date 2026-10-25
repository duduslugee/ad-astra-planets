package com.adastraplanets.client;

import net.neoforged.neoforge.client.event.ModelEvent;

import net.neoforged.neoforge.common.NeoForge;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.config.AdAstraPlanetsClientConfig;
import com.adastraplanets.config.AdAstraPlanetsConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import com.adastraplanets.client.rocket.TieredRocketItemRenderer;
import com.adastraplanets.client.solarsystem.SolarSystemMaps;
import com.adastraplanets.client.rocket.TieredRocketRenderer;
import com.adastraplanets.registry.ModEntityTypes;
import com.adastraplanets.registry.ModItems;
import earth.terrarium.adastra.client.neoforge.AdAstraClientNeoForge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod(value = AdAstraPlanets.MOD_ID, dist = Dist.CLIENT)
public final class AdAstraPlanetsClient {
    public AdAstraPlanetsClient(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, AdAstraPlanetsClientConfig.SPEC,
                AdAstraPlanetsConfig.fileName(AdAstraPlanetsClientConfig.TOPIC));
        modBus.addListener(AdAstraPlanetsClient::registerRenderers);
        modBus.addListener(AdAstraPlanetsClient::clientSetup);
        modBus.addListener(SuitModels::registerLayers);
        modBus.addListener(SuitModels::registerExtensions);
        modBus.addListener((net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers event) -> SuitModels.clear());
        modBus.addListener(TintedBlocks::blocks);
        modBus.addListener(TintedBlocks::items);
        modBus.addListener((ModelEvent.RegisterGeometryLoaders event) ->
                event.register(AdAstraPlanets.id("connected"), com.adastraplanets.client.model.ConnectedModel.LOADER));
        NeoForge.EVENT_BUS.addListener(LinerTooltips::add);
        NeoForge.EVENT_BUS.addListener(OreTooltips::add);
        NeoForge.EVENT_BUS.addListener(ObtainTooltips::add);
        modBus.addListener((RegisterClientReloadListenersEvent event) -> event.registerReloadListener(new ObtainTooltips()));
        modBus.addListener((RegisterClientReloadListenersEvent event) -> event.registerReloadListener(new SolarSystemMaps()));
        modBus.addListener(FluidLooks::register);
        modBus.addListener(GlobeRenderer::registerRenderer);
        modBus.addListener(GlobeRenderer::registerModels);
        modBus.addListener(GlobeRenderer::registerItems);
        modBus.addListener(WorkbenchScreen::register);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // every rocket (built in or from content.json) uses geo/entity/<id>.geo.json and textures/entity/<id>.png
        ModEntityTypes.rockets().forEach((id, type) ->
                event.registerEntityRenderer(type.get(), context -> new TieredRocketRenderer(context, id)));
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        // Ad Astra already attaches a client extension to every rocket item and looks its renderer up in this map.
        event.enqueueWork(() -> ModItems.rockets().forEach(item ->
                AdAstraClientNeoForge.ITEM_RENDERERS.put(item.get(), new TieredRocketItemRenderer(item.getId()))));
        event.enqueueWork(FluidLooks::renderLayers);
        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            event.enqueueWork(com.adastraplanets.compat.curios.CuriosSuitRenderer::register);
        }
    }
}
