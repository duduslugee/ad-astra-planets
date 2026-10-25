package com.adastraplanets.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The Ore Scanner's highlights: a glowing copy of each ore (a block display, outlined through walls in the ore's own
 * map colour) that disappears after a while. A new scan replaces the scanning player's previous highlights, and any
 * highlight saved with the world (the game closed before it expired) is dropped when it loads again.
 */
public final class OreHighlights {
    private static final String TAG = "adastraplanets.ore_highlight";

    private record Shown(ServerLevel level, List<Entity> entities, long expiresAt) {
    }

    private static final Map<UUID, Shown> SHOWN = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(OreHighlights::tick);
        NeoForge.EVENT_BUS.addListener(OreHighlights::dropSaved);
    }

    /** Highlights these ores for the given player, replacing that player's previous highlights. */
    public static void show(ServerLevel level, UUID player, List<BlockPos> positions, int seconds) {
        clear(player);
        List<Entity> entities = new ArrayList<>();
        for (BlockPos pos : positions) {
            BlockState state = level.getBlockState(pos);
            CompoundTag tag = new CompoundTag();
            tag.putString("id", "minecraft:block_display");
            tag.put("block_state", NbtUtils.writeBlockState(state));
            tag.putBoolean("Glowing", true);
            tag.putInt("glow_color_override", state.getMapColor(level, pos).col);
            // a hair larger than the block, so the copy does not flicker against the real one
            CompoundTag transformation = new CompoundTag();
            transformation.put("left_rotation", floats(0, 0, 0, 1));
            transformation.put("right_rotation", floats(0, 0, 0, 1));
            transformation.put("translation", floats(-0.005F, -0.005F, -0.005F));
            transformation.put("scale", floats(1.01F, 1.01F, 1.01F));
            tag.put("transformation", transformation);
            ListTag tags = new ListTag();
            tags.add(StringTag.valueOf(TAG));
            tag.put("Tags", tags);
            EntityType.create(tag, level).ifPresent(entity -> {
                entity.moveTo(pos.getX(), pos.getY(), pos.getZ());
                level.addFreshEntity(entity);
                entities.add(entity);
            });
        }
        SHOWN.put(player, new Shown(level, entities, level.getGameTime() + seconds * 20L));
    }

    private static ListTag floats(float... values) {
        ListTag list = new ListTag();
        for (float value : values) {
            list.add(FloatTag.valueOf(value));
        }
        return list;
    }

    private static void clear(UUID player) {
        Shown shown = SHOWN.remove(player);
        if (shown != null) {
            shown.entities().forEach(Entity::discard);
        }
    }

    private static void tick(ServerTickEvent.Post event) {
        if (SHOWN.isEmpty()) {
            return;
        }
        SHOWN.values().removeIf(shown -> {
            boolean expired = shown.level().getGameTime() >= shown.expiresAt();
            if (expired) {
                shown.entities().forEach(Entity::discard);
            }
            return expired;
        });
    }

    private static void dropSaved(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() && event.getEntity().getTags().contains(TAG)) {
            event.setCanceled(true);
        }
    }

    private OreHighlights() {
    }
}
