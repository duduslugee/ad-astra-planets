package com.adastraplanets.registry;

import com.adastraplanets.block.GlobeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Desk globes, one per world, like Ad Astra's own (their ids name the world they show, as Ad Astra's do). Each uses
 * models/block/&lt;id&gt;.json (the stand) and models/block/&lt;id&gt;_cube.json (the spinning globe) with
 * textures/block/globe/&lt;id&gt;.png. Modpacks add globes of their own worlds through content.json.
 */
public final class ModGlobes {
    /** The worlds this mod adds, each paired with the material whose plates its globe is made from. */
    public static final Map<String, String> BUILT_IN = Map.ofEntries(
            Map.entry("phobos", "umbrite"), Map.entry("deimos", "pallorite"),
            Map.entry("io", "igneum"), Map.entry("europa", "abyssium"), Map.entry("ganymede", "aurorium"),
            Map.entry("callisto", "eonyx"), Map.entry("jupiter", "stormium"),
            Map.entry("enceladus", "nivalum"), Map.entry("rhea", "ferrolith"), Map.entry("iapetus", "vesperine"),
            Map.entry("titan", "tholinar"), Map.entry("saturn", "cassinium"),
            Map.entry("miranda", "mosaicon"), Map.entry("titania", "chasmal"), Map.entry("oberon", "obscurex"),
            Map.entry("uranus", "azurane"),
            Map.entry("triton", "nitralis"), Map.entry("neptune", "tempestine"),
            Map.entry("charon", "mordrine"), Map.entry("pluto", "cordium"),
            Map.entry("dysnomia", "shardex"), Map.entry("eris", "albedon"));

    private static final Map<String, DeferredBlock<GlobeBlock>> BLOCKS = new LinkedHashMap<>();
    private static final Map<String, DeferredItem<BlockItem>> ITEMS = new LinkedHashMap<>();

    static {
        List.of("phobos", "deimos", "io", "europa", "ganymede", "callisto", "jupiter", "enceladus", "rhea", "iapetus",
                "titan", "saturn", "miranda", "titania", "oberon", "uranus", "triton", "neptune", "charon", "pluto",
                "dysnomia", "eris").forEach(world -> register(world + "_globe"));
    }

    /** Registers a globe block and its item; also called for the globes in content.json. */
    public static void register(String id) {
        DeferredBlock<GlobeBlock> block = ModBlocks.BLOCKS.register(id, () -> new GlobeBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL).strength(1.0F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.DESTROY)));
        BLOCKS.put(id, block);
        ITEMS.put(id, ModItems.ITEMS.registerSimpleBlockItem(block, new Item.Properties().stacksTo(1)));
    }

    public static Map<String, DeferredBlock<GlobeBlock>> blocks() {
        return Collections.unmodifiableMap(BLOCKS);
    }

    public static Map<String, DeferredItem<BlockItem>> items() {
        return Collections.unmodifiableMap(ITEMS);
    }

    public static Block[] allBlocks() {
        return BLOCKS.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    private ModGlobes() {
    }
}
