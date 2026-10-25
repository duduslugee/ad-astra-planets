package com.adastraplanets.debug;

import com.adastraplanets.config.AdAstraPlanetsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Debug tool for checking ore generation: right-click counts every block in the configured tag (c:ores by default)
 * in the loaded chunks around the player and lists, per ore, how many there are, per chunk on average, the height
 * range they were found at and the nearest one (click it to teleport); the nearest ores also glow for a while
 * ({@link OreHighlights}). Creative mode or operators only; radius, tag and highlights are in
 * config/adastraplanets/adastraplanets-debug.toml.
 */
public class OreScannerItem extends Item {
    public OreScannerItem(Properties properties) {
        super(properties);
    }

    private static final class Found {
        int count;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        BlockPos nearest;
        double nearestDistance = Double.MAX_VALUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }
        if (!player.isCreative() && !player.hasPermissions(2)) {
            player.displayClientMessage(Component.translatable("message.adastraplanets.ore_scanner.denied").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, 20);
        int radius = AdAstraPlanetsConfig.SCANNER_RADIUS.get();
        TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(AdAstraPlanetsConfig.SCANNER_TAG.get()));
        BlockPos origin = player.blockPosition();
        int centreX = origin.getX() >> 4;
        int centreZ = origin.getZ() >> 4;

        Map<Block, Found> found = new LinkedHashMap<>();
        List<BlockPos> all = new ArrayList<>();
        int scanned = 0;
        int unloaded = 0;
        for (int cx = centreX - radius; cx <= centreX + radius; cx++) {
            for (int cz = centreZ - radius; cz <= centreZ + radius; cz++) {
                LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(cx, cz);
                if (chunk == null) {
                    unloaded++;
                    continue;
                }
                scanned++;
                LevelChunkSection[] sections = chunk.getSections();
                for (int index = 0; index < sections.length; index++) {
                    LevelChunkSection section = sections[index];
                    if (section.hasOnlyAir() || !section.maybeHas(state -> state.is(tag))) {
                        continue;
                    }
                    int baseY = serverLevel.getSectionYFromSectionIndex(index) << 4;
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                                BlockState state = section.getBlockState(x, y, z);
                                if (!state.is(tag)) {
                                    continue;
                                }
                                BlockPos pos = new BlockPos((cx << 4) + x, baseY + y, (cz << 4) + z);
                                all.add(pos);
                                Found entry = found.computeIfAbsent(state.getBlock(), block -> new Found());
                                entry.count++;
                                entry.minY = Math.min(entry.minY, pos.getY());
                                entry.maxY = Math.max(entry.maxY, pos.getY());
                                double distance = pos.distSqr(origin);
                                if (distance < entry.nearestDistance) {
                                    entry.nearestDistance = distance;
                                    entry.nearest = pos;
                                }
                            }
                        }
                    }
                }
            }
        }

        int side = radius * 2 + 1;
        player.sendSystemMessage(Component.translatable("message.adastraplanets.ore_scanner.header", side + "x" + side,
                serverLevel.dimension().location().toString(), unloaded, tag.location().toString()).withStyle(ChatFormatting.GOLD));
        if (found.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.adastraplanets.ore_scanner.none").withStyle(ChatFormatting.GRAY));
            return InteractionResultHolder.success(stack);
        }
        int chunks = Math.max(1, scanned);
        List<Map.Entry<Block, Found>> entries = found.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<Block, Found> e) -> e.getValue().count).reversed()).toList();
        for (Map.Entry<Block, Found> e : entries) {
            Found f = e.getValue();
            BlockPos p = f.nearest;
            MutableComponent nearest = Component.literal("[" + p.getX() + " " + p.getY() + " " + p.getZ() + "]")
                    .withStyle(style -> style.withColor(ChatFormatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/tp @s " + p.getX() + " " + (p.getY() + 1) + " " + p.getZ()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    Component.translatable("message.adastraplanets.ore_scanner.teleport"))));
            player.sendSystemMessage(Component.translatable("message.adastraplanets.ore_scanner.entry",
                    e.getKey().getName(), f.count, String.format("%.1f", f.count / (double) chunks), f.minY, f.maxY, nearest));
        }
        // the nearest ones glow (outlined through walls) for a while
        all.sort(Comparator.comparingDouble(pos -> pos.distSqr(origin)));
        int highlighted = Math.min(all.size(), AdAstraPlanetsConfig.SCANNER_HIGHLIGHTS.get());
        if (highlighted > 0) {
            int seconds = AdAstraPlanetsConfig.SCANNER_HIGHLIGHT_SECONDS.get();
            OreHighlights.show(serverLevel, player.getUUID(), all.subList(0, highlighted), seconds);
            player.sendSystemMessage(Component.translatable("message.adastraplanets.ore_scanner.highlighted", highlighted, seconds)
                    .withStyle(ChatFormatting.GRAY));
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.adastraplanets.ore_scanner").withStyle(ChatFormatting.GRAY));
    }
}
