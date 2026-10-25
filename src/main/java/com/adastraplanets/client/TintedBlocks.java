package com.adastraplanets.client;

import com.adastraplanets.registry.ModBlocks;
import com.adastraplanets.registry.ModItems;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Tints for blocks drawn with Minecraft's own textures and for rocket parts drawn on a shared grey base (see ModBlockStateProvider), multiplied into faces with
 * {@code tintindex 0}; other faces (a geyser's mouth, an ore's overlay) keep their colours. Resource packs that change
 * the vanilla textures change these blocks too.
 */
public final class TintedBlocks {
    private static int rgb(int red, int green, int blue) {
        return FastColor.ARGB32.color(255, red, green, blue);
    }

    /** Europa: calcite turned pale blue for the crust; packed ice (blue) turned rust by cutting its blue. */
    private static final int CRUST = rgb(205, 222, 240);
    private static final int RUST = rgb(255, 116, 50);
    /** Phobos: gravel darkened to sooty, slightly red carbon regolith. */
    private static final int CARBON_REGOLITH = rgb(120, 105, 100);
    /** Deimos: sand washed out to pale, warm dust. */
    private static final int PALE_DUST = rgb(210, 205, 215);
    /** Io: calcite turned sulfur yellow; blackstone warmed to red-brown volcanic cinder. */
    private static final int SULFUR = rgb(255, 222, 92);
    private static final int CINDER = rgb(200, 120, 100);
    /** Ganymede: tuff darkened to old brown-grey regolith; packed ice washed pale for the bright grooved terrain. */
    private static final int DARK_REGOLITH = rgb(150, 135, 125);
    private static final int GROOVE_ICE = rgb(225, 235, 245);
    /** Callisto: coarse dirt darkened to old, sooty dust. */
    private static final int ANCIENT_DUST = rgb(120, 112, 110);
    /** Enceladus: snow with a faint blue cast; packed ice deepened to the blue of the tiger-stripe fissures. */
    private static final int PLUME_SNOW = rgb(235, 245, 255);
    private static final int FISSURE_ICE = rgb(150, 205, 235);
    /** Rhea: gravel washed to pale grey, dusty frost. */
    private static final int FROST_REGOLITH = rgb(200, 205, 212);
    /** Iapetus: coarse dirt darkened to the reddish-brown dust of its dark face. */
    private static final int UMBER_DUST = rgb(130, 85, 65);
    /** Titan: sand darkened to the brown of its hydrocarbon dunes; gravel washed pale for the rounded ice pebbles. */
    private static final int THOLIN_SAND = rgb(150, 100, 60);
    private static final int ICE_PEBBLES = rgb(215, 200, 180);
    /** Saturn: snow tinted to the pale butter of ammonia frost. */
    private static final int AMMONIA_FROST = rgb(245, 232, 190);
    /** Miranda: packed ice, its blue cancelled by a warm tint, greyed to the pale bands of its coronae. */
    private static final int BANDED_ICE = rgb(235, 220, 185);
    /** Uranus: packed ice deepened to cyan; a diamond block washed to pale aqua for the pressure crystal. */
    private static final int AZURE_ICE = rgb(120, 235, 225);
    private static final int PRESSURE_CRYSTAL = rgb(210, 255, 250);
    /** Triton: snow tinted to the soft pink of nitrogen frost; packed ice greyed for the nitrogen vents. */
    private static final int NITROGEN_FROST = rgb(250, 215, 222);
    private static final int NITROGEN_VENT = rgb(170, 160, 170);
    /** Neptune: packed ice deepened to dark storm blue; an amethyst block turned deep blue for the gloom crystal. */
    private static final int DEEP_ICE = rgb(80, 105, 200);
    private static final int GLOOM_CRYSTAL = rgb(90, 130, 255);
    /** Charon: snow tinted to the dark red of the tholins on its polar cap. */
    private static final int CRIMSON_FROST = rgb(165, 80, 75);
    /** Pluto: packed ice washed to the cream of its heart; packed ice dusked to the brownish violet of methane blades. */
    private static final int CELL_ICE = rgb(255, 245, 225);
    private static final int METHANE_ICE = rgb(190, 150, 165);
    /** Eris (and the fractures of Dysnomia): packed ice washed almost white, the brightest ice in the Solar System. */
    private static final int BRIGHT_ICE = rgb(240, 250, 255);
    /** Eris: snow faintly creamed by the frozen atmosphere that fell on it; an amethyst block washed to icy lilac. */
    private static final int DRIFT_SNOW = rgb(250, 246, 238);
    private static final int ICE_SHARD = rgb(225, 225, 255);
    /** Jupiter: snow tinted to its light cream zones and rusty belts. */
    private static final int ZONE_FROST = rgb(245, 228, 195);
    private static final int BELT_FROST = rgb(205, 130, 85);

    private static Map<Supplier<? extends Block>, Integer> tints() {
        return Map.of(
                ModBlocks.CRUST_ICE, CRUST, ModBlocks.GEYSER, CRUST, ModBlocks.RUSTED_ICE, RUST,
                ModBlocks.CARBON_REGOLITH, CARBON_REGOLITH, ModBlocks.PALE_DUST, PALE_DUST,
                ModBlocks.SULFUR_CRUST, SULFUR, ModBlocks.SULFUR_VENT, SULFUR, ModBlocks.CINDER, CINDER);
    }

    /** Later worlds (Map.of stops at ten pairs). */
    private static Map<Supplier<? extends Block>, Integer> moreTints() {
        return Map.ofEntries(
                Map.entry(ModBlocks.DARK_REGOLITH, DARK_REGOLITH),
                Map.entry(ModBlocks.GROOVE_ICE, GROOVE_ICE),
                Map.entry(ModBlocks.ANCIENT_DUST, ANCIENT_DUST),
                Map.entry(ModBlocks.ZONE_FROST, ZONE_FROST),
                Map.entry(ModBlocks.BELT_FROST, BELT_FROST),
                Map.entry(ModBlocks.ZONE_FROST_SLAB, ZONE_FROST),
                Map.entry(ModBlocks.BELT_FROST_SLAB, BELT_FROST),
                Map.entry(ModBlocks.PLUME_SNOW, PLUME_SNOW),
                Map.entry(ModBlocks.FISSURE_ICE, FISSURE_ICE),
                Map.entry(ModBlocks.FROST_REGOLITH, FROST_REGOLITH),
                Map.entry(ModBlocks.UMBER_DUST, UMBER_DUST),
                Map.entry(ModBlocks.THOLIN_SAND, THOLIN_SAND),
                Map.entry(ModBlocks.ICE_PEBBLES, ICE_PEBBLES),
                Map.entry(ModBlocks.AMMONIA_FROST, AMMONIA_FROST),
                Map.entry(ModBlocks.BANDED_ICE, BANDED_ICE),
                Map.entry(ModBlocks.AZURE_ICE, AZURE_ICE),
                Map.entry(ModBlocks.PRESSURE_CRYSTAL, PRESSURE_CRYSTAL),
                Map.entry(ModBlocks.NITROGEN_FROST, NITROGEN_FROST),
                Map.entry(ModBlocks.NITROGEN_VENT, NITROGEN_VENT),
                Map.entry(ModBlocks.DEEP_ICE, DEEP_ICE),
                Map.entry(ModBlocks.GLOOM_CRYSTAL, GLOOM_CRYSTAL),
                Map.entry(ModBlocks.CRIMSON_FROST, CRIMSON_FROST),
                Map.entry(ModBlocks.CELL_ICE, CELL_ICE),
                Map.entry(ModBlocks.METHANE_ICE, METHANE_ICE),
                Map.entry(ModBlocks.BRIGHT_ICE, BRIGHT_ICE),
                Map.entry(ModBlocks.DRIFT_SNOW, DRIFT_SNOW),
                Map.entry(ModBlocks.ICE_SHARD, ICE_SHARD));
    }

    public static void blocks(RegisterColorHandlersEvent.Block event) {
        // the ancient forges' vents glow with the heat under them
        event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? rgb(255, 140, 60) : -1, com.adastraplanets.registry.ModAncient.FORGE_VENT.get());
        // the ancient ruins' runes glow in the colour of their system's crystal
        com.adastraplanets.registry.ModAncient.glowing().forEach(block -> event.register((state, level, pos, tintIndex) ->
                tintIndex == 0 ? 0xFF000000 | state.getValue(com.adastraplanets.block.AncientGlow.PROPERTY).crystal().colour : -1, block.get()));
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            int colour = 0xFF000000 | crystal.colour;
            crystal.blocks().forEach(block -> event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
        }
        tints().forEach((block, colour) -> event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
        moreTints().forEach((block, colour) -> event.register((state, level, pos, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
    }

    /** Rocket parts: the grey part base (layer 0) takes the colour of the material it is made of. */
    private static final int HELIONITE_PART = rgb(226, 191, 104);
    private static final int UMBRITE_PART = rgb(143, 122, 117);
    private static final int PALLORITE_PART = rgb(223, 212, 187);
    private static final int IGNEUM_PART = rgb(188, 99, 36);
    private static final int EONYX_PART = rgb(205, 165, 105);
    private static final int STORMIUM_PART = rgb(215, 105, 85);
    private static final int ABYSSIUM_PART = rgb(101, 214, 189);
    private static final int NIVALUM_PART = rgb(175, 205, 240);
    private static final int FERROLITH_PART = rgb(165, 165, 168);
    private static final int VESPERINE_PART = rgb(150, 105, 90);
    private static final int THOLINAR_PART = rgb(210, 135, 60);
    private static final int MOSAICON_PART = rgb(170, 190, 200);
    private static final int CHASMAL_PART = rgb(200, 150, 150);
    private static final int OBSCUREX_PART = rgb(150, 95, 130);
    private static final int AZURANE_PART = rgb(110, 200, 210);
    private static final int NITRALIS_PART = rgb(235, 190, 205);
    private static final int TEMPESTINE_PART = rgb(95, 120, 220);
    private static final int MORDRINE_PART = rgb(185, 105, 110);
    private static final int CORDIUM_PART = rgb(225, 200, 160);

    public static void items(RegisterColorHandlersEvent.Item event) {
        // molten metal buckets: the fluid inside takes its metal's colour
        com.adastraplanets.registry.ModMoltenMetals.ALL.forEach(molten -> event.register(
                new net.neoforged.neoforge.client.model.DynamicFluidContainerModel.Colors(), molten.bucket.get()));
        com.adastraplanets.registry.ModAncient.glowing().forEach(block -> event.register((stack, tintIndex) ->
                tintIndex == 0 ? 0xFF000000 | com.adastraplanets.block.AncientGlow.AMBER.crystal().colour : -1, block.get()));
        for (var crystal : com.adastraplanets.registry.ModCrystals.ALL) {
            int colour = 0xFF000000 | crystal.colour;
            crystal.blocks().forEach(block -> event.register((stack, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
            event.register((stack, tintIndex) -> tintIndex == 0 ? colour : -1, crystal.shard.get(), crystal.core.get());
        }
        tints().forEach((block, colour) -> event.register((stack, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
        moreTints().forEach((block, colour) -> event.register((stack, tintIndex) -> tintIndex == 0 ? colour : -1, block.get()));
        Map.ofEntries(
                Map.entry(ModItems.HELIONITE_ENGINE, HELIONITE_PART),
                Map.entry(ModItems.UMBRITE_FIN, UMBRITE_PART),
                Map.entry(ModItems.UMBRITE_TANK, UMBRITE_PART),
                Map.entry(ModItems.PALLORITE_NOSE_CONE, PALLORITE_PART),
                Map.entry(ModItems.IGNEUM_FIN, IGNEUM_PART),
                Map.entry(ModItems.EONYX_NOSE_CONE, EONYX_PART),
                Map.entry(ModItems.STORMIUM_ENGINE, STORMIUM_PART),
                Map.entry(ModItems.ABYSSIUM_TANK, ABYSSIUM_PART),
                Map.entry(ModItems.NIVALUM_TANK, NIVALUM_PART),
                Map.entry(ModItems.FERROLITH_NOSE_CONE, FERROLITH_PART),
                Map.entry(ModItems.VESPERINE_FIN, VESPERINE_PART),
                Map.entry(ModItems.THOLINAR_ENGINE, THOLINAR_PART),
                Map.entry(ModItems.MOSAICON_NOSE_CONE, MOSAICON_PART),
                Map.entry(ModItems.CHASMAL_TANK, CHASMAL_PART),
                Map.entry(ModItems.OBSCUREX_FIN, OBSCUREX_PART),
                Map.entry(ModItems.AZURANE_ENGINE, AZURANE_PART),
                Map.entry(ModItems.NITRALIS_NOSE_CONE, NITRALIS_PART),
                Map.entry(ModItems.NITRALIS_FIN, NITRALIS_PART),
                Map.entry(ModItems.TEMPESTINE_TANK, TEMPESTINE_PART),
                Map.entry(ModItems.TEMPESTINE_ENGINE, TEMPESTINE_PART),
                Map.entry(ModItems.MORDRINE_NOSE_CONE, MORDRINE_PART),
                Map.entry(ModItems.MORDRINE_FIN, MORDRINE_PART),
                Map.entry(ModItems.CORDIUM_TANK, CORDIUM_PART),
                Map.entry(ModItems.CORDIUM_ENGINE, CORDIUM_PART))
                .forEach((item, colour) -> event.register((stack, tintIndex) -> tintIndex == 0 ? colour : -1, item.get()));
    }

    private TintedBlocks() {
    }
}
