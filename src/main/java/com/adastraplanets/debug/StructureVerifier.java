package com.adastraplanets.debug;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModAncient;
import com.adastraplanets.registry.ModCrystals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Development check of the landmarks as the world really builds them, run when the game is started with
 * -Dadastraplanets.verifyStructures=true (the verifyStructures run): each landmark is generated far out on its own
 * world and placed chunk by chunk as world generation would; its crystal sockets and Light Core are counted; the
 * world then runs for a while and the flowing water and air inside it are counted again, so a leak or a collapse
 * shows. Every result is logged as a line starting with "[verify]", then the game closes.
 */
public final class StructureVerifier {
    private static final Logger LOG = LoggerFactory.getLogger("adastraplanets-verify");
    private static final int SETTLE_TICKS = 400;

    private record Landmark(String world, String structure, Supplier<Item> core, int sockets) {
    }

    private static final List<Landmark> LANDMARKS = List.of(
            new Landmark("phobos", "phobos/expedition_outpost", () -> ModCrystals.EMBER.core.get(), 4),
            new Landmark("europa", "europa/drowned_sanctuary", () -> ModCrystals.AMBER.core.get(), 4),
            new Landmark("titan", "titan/library_of_the_rings", () -> ModCrystals.HALO.core.get(), 2),
            new Landmark("miranda", "miranda/tilted_observatory", () -> ModCrystals.TIDE.core.get(), 10),
            new Landmark("triton", "triton/abyssal_forge", () -> ModCrystals.COBALT.core.get(), 2),
            new Landmark("pluto", "pluto/sealed_heart", () -> ModCrystals.ROSE.core.get(), 2),
            new Landmark("eris", "eris/last_door", () -> ModCrystals.FROST.core.get(), 7));

    private record Placed(Landmark landmark, ServerLevel level, BoundingBox box, List<ChunkPos> chunks, int[] before,
                          BlockPos origin, net.minecraft.world.level.block.Rotation rotation, java.util.Set<BlockPos> air) {
        /** A world position back in the template's own coordinates, as the structure scripts write them. */
        BlockPos local(BlockPos pos) {
            BlockPos offset = pos.subtract(this.origin);
            net.minecraft.world.level.block.Rotation back = switch (this.rotation) {
                case CLOCKWISE_90 -> net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
                default -> this.rotation;
            };
            return offset.rotate(back);
        }
    }

    private static final List<Placed> PLACED = new ArrayList<>();
    private static int ticks = -1;
    private static int failures;
    /** Each structure checked gets its own stretch of its world, so none is built over another. */
    private static int slot;
    /** Run when the check is done (the client closes the game). */
    public static Runnable onFinished = () -> { };

    public static boolean enabled() {
        return Boolean.getBoolean("adastraplanets.verifyStructures");
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        List<Landmark> all = new ArrayList<>(LANDMARKS);
        // and every other structure of the mod (the points of interest), checked only for leaks and collapse
        server.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet().stream()
                .filter(id -> id.getNamespace().equals(AdAstraPlanets.MOD_ID) && id.getPath().contains("/"))
                .filter(id -> LANDMARKS.stream().noneMatch(l -> l.structure().equals(id.getPath())))
                .sorted()
                .forEach(id -> all.add(new Landmark(id.getPath().substring(0, id.getPath().indexOf('/')), id.getPath(), () -> null, -1)));
        for (Landmark landmark : all) {
            try {
                place(server, landmark);
            } catch (Exception e) {
                fail(landmark, "crashed while placing: " + e);
                LOG.error("[verify] stack trace", e);
            }
        }
        ticks = 0;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ticks < 0 || ++ticks < SETTLE_TICKS) {
            return;
        }
        ticks = -1;
        for (Placed placed : PLACED) {
            int[] after = survey(placed.level(), placed.box(), placed.landmark().core().get());
            int newFlowing = after[1] - placed.before()[1];
            int lostAir = placed.before()[3] - after[3];
            if (newFlowing > 40 || lostAir > 60) {
                List<String> leaks = new ArrayList<>();
                for (BlockPos pos : placed.air()) {
                    BlockState now = placed.level().getBlockState(pos);
                    if (!now.isAir() && leaks.size() < 40) {
                        BlockPos local = placed.local(pos);
                        leaks.add(local.getX() + "," + local.getY() + "," + local.getZ() + "="
                                + net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(now.getBlock()).getPath());
                    }
                }
                LOG.error("[verify] {} air turned to something else at (template x,y,z=block): {}", placed.landmark().structure(), leaks);
            }
            if (newFlowing > 40) {
                fail(placed.landmark(), "water flowed in: " + placed.before()[1] + " -> " + after[1] + " flowing water blocks");
            }
            if (lostAir > 60) {
                fail(placed.landmark(), "lost " + lostAir + " air blocks while settling (flooding or collapse)");
            }
            if (newFlowing <= 40 && lostAir <= 60) {
                LOG.info("[verify] PASS {} settled (flowing water {} -> {}, air {} -> {})", placed.landmark().structure(),
                        placed.before()[1], after[1], placed.before()[3], after[3]);
            }
            for (ChunkPos chunk : placed.chunks()) {
                placed.level().setChunkForced(chunk.x, chunk.z, false);
            }
        }
        LOG.info("[verify] DONE {} structures, {} failures", PLACED.size(), failures);
        onFinished.run();
    }

    private static void place(MinecraftServer server, Landmark landmark) {
        ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, AdAstraPlanets.id(landmark.world())));
        if (level == null) {
            fail(landmark, "no world " + landmark.world());
            return;
        }
        Holder<Structure> structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .getHolderOrThrow(ResourceKey.create(Registries.STRUCTURE, AdAstraPlanets.id(landmark.structure())));
        StructureStart start = null;
        int tries = 0;
        slot++;
        for (; tries < 60 && (start == null || !start.isValid()); tries++) {
            ChunkPos chunk = new ChunkPos(400 + slot * 24 + tries * 11, 400 + tries * 7);
            start = structure.value().generate(level.registryAccess(), level.getChunkSource().getGenerator(),
                    level.getChunkSource().getGenerator().getBiomeSource(), level.getChunkSource().randomState(),
                    level.getStructureManager(), level.getSeed(), chunk, 0, level, biome -> true);
        }
        if (start == null || !start.isValid()) {
            fail(landmark, "found no place to generate in 60 tries (ground too steep everywhere?)");
            return;
        }
        BoundingBox box = start.getBoundingBox();
        List<ChunkPos> chunks = ChunkPos.rangeClosed(new ChunkPos(box.minX() >> 4, box.minZ() >> 4),
                new ChunkPos(box.maxX() >> 4, box.maxZ() >> 4)).toList();
        for (ChunkPos chunk : chunks) {
            level.setChunkForced(chunk.x, chunk.z, true);
            level.getChunk(chunk.x, chunk.z);
        }
        for (ChunkPos chunk : chunks) {
            start.placeInChunk(level, level.structureManager(), level.getChunkSource().getGenerator(), level.getRandom(),
                    new BoundingBox(chunk.getMinBlockX(), level.getMinBuildHeight(), chunk.getMinBlockZ(), chunk.getMaxBlockX(),
                            level.getMaxBuildHeight(), chunk.getMaxBlockZ()), chunk);
        }
        int[] before = survey(level, box, landmark.core().get());
        LOG.info("[verify] {} placed after {} tries at {} (box {})", landmark.structure(), tries, box.getCenter(), box);
        boolean ok = true;
        if (landmark.sockets() < 0) {
            PLACED.add(placedRecord(landmark, level, box, chunks, before, start));
            return;
        }
        if (before[0] != landmark.sockets()) {
            ok = fail(landmark, before[0] + " crystal sockets, expected " + landmark.sockets());
        }
        if (before[2] != 1) {
            ok = fail(landmark, before[2] + " Light Cores in its containers, expected 1");
        }
        if (box.minY() <= level.getMinBuildHeight() || box.maxY() >= level.getMaxBuildHeight() - 1) {
            ok = fail(landmark, "its box " + box + " reaches past the world's height limits");
        }
        if (ok) {
            LOG.info("[verify] PASS {} built whole: {} sockets, its Light Core, {} flowing water blocks to start with",
                    landmark.structure(), before[0], before[1]);
        }
        PLACED.add(placedRecord(landmark, level, box, chunks, before, start));
    }

    private static Placed placedRecord(Landmark landmark, ServerLevel level, BoundingBox box, List<ChunkPos> chunks, int[] before,
                                       StructureStart start) {
        var piece = start.getPieces().get(0);
        BlockPos origin = piece instanceof net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece pool
                ? pool.getPosition() : new BlockPos(box.minX(), box.minY(), box.minZ());
        java.util.Set<BlockPos> air = new java.util.HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            if (level.getBlockState(pos).isAir()) {
                air.add(pos.immutable());
            }
        }
        return new Placed(landmark, level, box, chunks, before, origin, piece.getRotation(), air);
    }

    private static boolean fail(Landmark landmark, String why) {
        failures++;
        LOG.error("[verify] FAIL {}: {}", landmark.structure(), why);
        return false;
    }

    /** {crystal sockets, flowing liquid blocks, Light Cores in containers, air blocks} inside the box. */
    private static int[] survey(ServerLevel level, BoundingBox box, Item core) {
        int sockets = 0;
        int flowing = 0;
        int cores = 0;
        int air = 0;
        for (BlockPos pos : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
                air++;
                continue;
            }
            if (state.getBlock() instanceof com.adastraplanets.block.CrystalSocketBlock) {   // core sockets too
                sockets++;
            }
            FluidState fluid = state.getFluidState();
            if (!fluid.isEmpty() && !fluid.isSource()) {    // flowing water, methane, anything
                flowing++;
            }
            if (level.getBlockEntity(pos) instanceof Container container) {
                for (int i = 0; i < container.getContainerSize(); i++) {
                    if (core != null && container.getItem(i).is(core)) {
                        cores += container.getItem(i).getCount();
                    }
                }
            }
        }
        return new int[] {sockets, flowing, cores, air};
    }

    private StructureVerifier() {
    }
}
