package com.adastraplanets.registry;

import com.adastraplanets.block.AncientGateBlock;
import com.adastraplanets.block.CrystalSocketBlock;
import com.adastraplanets.block.GlowingAncientBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

/**
 * The blocks of the ancient civilization's ruins: dark cut stone with runes glowing in the system's crystal colour,
 * and the crystal sockets and sealed gates of their vaults (unbreakable, opened only with the right crystal).
 */
public final class ModAncient {
    private static BlockBehaviour.Properties stone() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS).mapColor(MapColor.TERRACOTTA_BLUE);
    }

    public static final DeferredBlock<Block> ANCIENT_STONE = ModBlocks.BLOCKS.registerSimpleBlock("ancient_stone", stone());
    public static final DeferredBlock<Block> ANCIENT_BRICKS = ModBlocks.BLOCKS.registerSimpleBlock("ancient_bricks", stone());
    public static final DeferredBlock<Block> CRACKED_ANCIENT_BRICKS = ModBlocks.BLOCKS.registerSimpleBlock("cracked_ancient_bricks", stone());
    public static final DeferredBlock<StairBlock> ANCIENT_BRICK_STAIRS = ModBlocks.BLOCKS.register("ancient_brick_stairs",
            () -> new StairBlock(ANCIENT_BRICKS.get().defaultBlockState(), stone()));
    public static final DeferredBlock<SlabBlock> ANCIENT_BRICK_SLAB = ModBlocks.BLOCKS.register("ancient_brick_slab",
            () -> new SlabBlock(stone()));
    public static final DeferredBlock<WallBlock> ANCIENT_BRICK_WALL = ModBlocks.BLOCKS.register("ancient_brick_wall",
            () -> new WallBlock(stone().forceSolidOn()));
    public static final DeferredBlock<RotatedPillarBlock> ANCIENT_PILLAR = ModBlocks.BLOCKS.register("ancient_pillar",
            () -> new RotatedPillarBlock(stone()));
    public static final DeferredBlock<GlowingAncientBlock> ANCIENT_RUNE_STONE = ModBlocks.BLOCKS.register("ancient_rune_stone",
            () -> new GlowingAncientBlock(stone().lightLevel(state -> 6)));
    public static final DeferredBlock<CrystalSocketBlock> CRYSTAL_SOCKET = ModBlocks.BLOCKS.register("crystal_socket",
            // not a redstone conductor (like a block of redstone), so its neighbours read its own signal
            () -> new CrystalSocketBlock(stone().strength(-1.0F, 3600000.0F).noLootTable().isRedstoneConductor((state, level, pos) -> false)
                    .lightLevel(state -> state.getValue(CrystalSocketBlock.FILLED) ? 12 : 3)));
    public static final DeferredBlock<AncientGateBlock> ANCIENT_GATE = ModBlocks.BLOCKS.register("ancient_gate",
            () -> new AncientGateBlock(stone().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()
                    .lightLevel(state -> state.getValue(AncientGateBlock.OPEN) ? 0 : 4)));

    /** A gate for more than one key: it opens once every crystal socket set into it is filled. */
    public static final DeferredBlock<com.adastraplanets.block.AncientSealBlock> ANCIENT_SEAL = ModBlocks.BLOCKS.register("ancient_seal",
            () -> new com.adastraplanets.block.AncientSealBlock(stone().strength(-1.0F, 3600000.0F).noLootTable().noOcclusion()
                    .lightLevel(state -> state.getValue(AncientGateBlock.OPEN) ? 0 : 4)));

    /** A rune of a sequence lock: touched in the right order its lock's runes light and open the gates near them. */
    public static final DeferredBlock<com.adastraplanets.block.GlyphRuneBlock> GLYPH_RUNE = ModBlocks.BLOCKS.register("glyph_rune",
            () -> new com.adastraplanets.block.GlyphRuneBlock(stone().strength(-1.0F, 3600000.0F).noLootTable()
                    .lightLevel(state -> state.getValue(com.adastraplanets.block.GlyphRuneBlock.LIT) ? 9 : 2)));

    /** A vent of the ancients' forges: a geyser with a short, quick jet (see the geysers data map). */
    public static final DeferredBlock<com.adastraplanets.block.GeyserBlock> FORGE_VENT = ModBlocks.BLOCKS.register("forge_vent",
            () -> new com.adastraplanets.block.GeyserBlock(stone().strength(-1.0F, 3600000.0F).noLootTable().lightLevel(state -> 7)));

    /** The Last Door's great socket: like a crystal socket, but it takes its system's Light Core. */
    public static final DeferredBlock<com.adastraplanets.block.CoreSocketBlock> CORE_SOCKET = ModBlocks.BLOCKS.register("core_socket",
            () -> new com.adastraplanets.block.CoreSocketBlock(stone().strength(-1.0F, 3600000.0F).noLootTable().isRedstoneConductor((state, level, pos) -> false)
                    .lightLevel(state -> state.getValue(CrystalSocketBlock.FILLED) ? 15 : 4)));

    public static final List<DeferredItem<BlockItem>> ITEMS = List.of(
            ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_STONE), ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_BRICKS),
            ModItems.ITEMS.registerSimpleBlockItem(CRACKED_ANCIENT_BRICKS), ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_BRICK_STAIRS),
            ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_BRICK_SLAB), ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_BRICK_WALL),
            ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_PILLAR), ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_RUNE_STONE),
            ModItems.ITEMS.registerSimpleBlockItem(CRYSTAL_SOCKET), ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_GATE),
            ModItems.ITEMS.registerSimpleBlockItem(ANCIENT_SEAL), ModItems.ITEMS.registerSimpleBlockItem(GLYPH_RUNE),
            ModItems.ITEMS.registerSimpleBlockItem(FORGE_VENT), ModItems.ITEMS.registerSimpleBlockItem(CORE_SOCKET));

    /** The blocks tinted with their glow's crystal colour. */
    public static List<DeferredBlock<? extends Block>> glowing() {
        return List.of(ANCIENT_RUNE_STONE, CRYSTAL_SOCKET, ANCIENT_GATE, ANCIENT_SEAL, GLYPH_RUNE, CORE_SOCKET);
    }

    /** Called from the mod constructor so every block is registered before the registers are attached. */
    public static void init() {
    }

    private ModAncient() {
    }
}
