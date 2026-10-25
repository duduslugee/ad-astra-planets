package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModAncient;
import com.adastraplanets.registry.ModCrystals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The mod's structures as the game reads them: every block of every template must load as written (no unknown block
 * turned to air, no property dropped back to its default). Generating them on their worlds is checked by the
 * verifyStructures run (StructureVerifier), since the test server has no worlds but the overworld.
 */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class StructureGameTests {
    @GameTest(template = "empty")
    public static void templatesLoadAsWritten(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HolderGetter<Block> blocks = level.holderLookup(Registries.BLOCK);
        List<String> problems = new ArrayList<>();
        var found = level.getServer().getResourceManager().listResources("structure",
                location -> location.getNamespace().equals(AdAstraPlanets.MOD_ID) && location.getPath().endsWith(".nbt"));
        for (Map.Entry<ResourceLocation, net.minecraft.server.packs.resources.Resource> file : found.entrySet()) {
            CompoundTag root;
            try (InputStream in = file.getValue().open()) {
                root = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
            } catch (Exception e) {
                problems.add(file.getKey() + ": unreadable (" + e.getMessage() + ")");
                continue;
            }
            ListTag palette = root.getList("palette", Tag.TAG_COMPOUND);
            for (int i = 0; i < palette.size(); i++) {
                CompoundTag entry = palette.getCompound(i);
                String name = entry.getString("Name");
                BlockState state = NbtUtils.readBlockState(blocks, entry);
                if (!BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals(name)) {
                    problems.add(file.getKey().getPath() + ": unknown block " + name);
                    continue;
                }
                CompoundTag properties = entry.getCompound("Properties");
                for (String key : properties.getAllKeys()) {
                    Property<?> property = state.getBlock().getStateDefinition().getProperty(key);
                    if (property == null) {
                        problems.add(file.getKey().getPath() + ": " + name + " has no property " + key);
                    } else if (!valueName(state, property).equals(properties.getString(key))) {
                        problems.add(file.getKey().getPath() + ": " + name + " " + key + "=" + properties.getString(key) + " not valid");
                    }
                }
            }
        }
        helper.assertTrue(problems.isEmpty(), problems.size() + " problems, e.g. " + problems.stream().distinct().limit(8).toList());
        helper.succeed();
    }

    private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private StructureGameTests() {
    }
}
