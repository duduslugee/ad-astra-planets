package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.block.AncientGateBlock;
import com.adastraplanets.block.AncientGlow;
import com.adastraplanets.block.CrystalSocketBlock;
import com.adastraplanets.registry.ModAncient;
import com.adastraplanets.registry.ModCrystals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** A ruin's crystal socket takes only its system's shard, and once set it opens the whole gate beside it. */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class AncientRuinGameTests {
    @GameTest(template = "empty")
    public static void socketOpensTheWholeGate(GameTestHelper helper) {
        BlockPos socket = new BlockPos(0, 1, 1);
        helper.setBlock(socket, ModAncient.CRYSTAL_SOCKET.get().defaultBlockState().setValue(AncientGlow.PROPERTY, AncientGlow.ROSE));
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                helper.setBlock(new BlockPos(x, y, 1), ModAncient.ANCIENT_GATE.get().defaultBlockState().setValue(AncientGlow.PROPERTY, AncientGlow.ROSE));
            }
        }
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        // the wrong crystal does nothing
        use(helper, player, socket, new ItemStack(ModCrystals.AMBER.shard.get()));
        helper.assertBlockProperty(socket, CrystalSocketBlock.FILLED, false);
        use(helper, player, socket, new ItemStack(ModCrystals.ROSE.shard.get()));
        helper.assertBlockProperty(socket, CrystalSocketBlock.FILLED, true);
        helper.runAfterDelay(2, () -> {
            for (int x = 1; x <= 3; x++) {
                for (int y = 1; y <= 3; y++) {
                    helper.assertBlockProperty(new BlockPos(x, y, 1), AncientGateBlock.OPEN, true);
                }
            }
            helper.succeed();
        });
    }

    /** A seal between two sockets stays shut with one of them filled and opens once both are. */
    @GameTest(template = "empty")
    public static void sealWantsEverySocket(GameTestHelper helper) {
        BlockPos west = new BlockPos(0, 1, 1);
        BlockPos east = new BlockPos(2, 1, 1);
        BlockPos seal = new BlockPos(1, 1, 1);
        for (BlockPos pos : java.util.List.of(west, east)) {
            helper.setBlock(pos, ModAncient.CRYSTAL_SOCKET.get().defaultBlockState().setValue(AncientGlow.PROPERTY, AncientGlow.AMBER));
        }
        helper.setBlock(seal, ModAncient.ANCIENT_SEAL.get().defaultBlockState().setValue(AncientGlow.PROPERTY, AncientGlow.AMBER));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        use(helper, player, west, new ItemStack(ModCrystals.AMBER.shard.get()));
        helper.runAfterDelay(2, () -> {
            helper.assertBlockProperty(seal, AncientGateBlock.OPEN, false);
            use(helper, player, east, new ItemStack(ModCrystals.AMBER.shard.get()));
            helper.runAfterDelay(2, () -> {
                helper.assertBlockProperty(seal, AncientGateBlock.OPEN, true);
                helper.succeed();
            });
        });
    }

    /** The Last Door's threshold in small: a row of core sockets along a seal opens it only once every core is set. */
    @GameTest(template = "empty")
    public static void coreSocketsOpenTheDoor(GameTestHelper helper) {
        AncientGlow[] glows = {AncientGlow.EMBER, AncientGlow.HALO, AncientGlow.FROST};
        for (int i = 0; i < glows.length; i++) {
            helper.setBlock(new BlockPos(i, 1, 1), ModAncient.ANCIENT_SEAL.get().defaultBlockState().setValue(AncientGlow.PROPERTY, AncientGlow.FROST));
            helper.setBlock(new BlockPos(i, 1, 2), ModAncient.CORE_SOCKET.get().defaultBlockState().setValue(AncientGlow.PROPERTY, glows[i]));
        }
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        // a shard is not a core, and each socket wants its own system's core
        use(helper, player, new BlockPos(0, 1, 2), new ItemStack(ModCrystals.EMBER.shard.get()));
        helper.assertBlockProperty(new BlockPos(0, 1, 2), CrystalSocketBlock.FILLED, false);
        use(helper, player, new BlockPos(0, 1, 2), new ItemStack(ModCrystals.HALO.core.get()));
        helper.assertBlockProperty(new BlockPos(0, 1, 2), CrystalSocketBlock.FILLED, false);
        use(helper, player, new BlockPos(0, 1, 2), new ItemStack(ModCrystals.EMBER.core.get()));
        use(helper, player, new BlockPos(1, 1, 2), new ItemStack(ModCrystals.HALO.core.get()));
        helper.runAfterDelay(2, () -> {
            helper.assertBlockProperty(new BlockPos(1, 1, 1), AncientGateBlock.OPEN, false);
            use(helper, player, new BlockPos(2, 1, 2), new ItemStack(ModCrystals.FROST.core.get()));
            helper.runAfterDelay(2, () -> {
                for (int i = 0; i < 3; i++) {
                    helper.assertBlockProperty(new BlockPos(i, 1, 1), AncientGateBlock.OPEN, true);
                }
                helper.succeed();
            });
        });
    }

    /** A Light Core used in a recipe comes back out of the grid. */
    @GameTest(template = "empty")
    public static void lightCoreIsACatalyst(GameTestHelper helper) {
        ItemStack core = new ItemStack(ModCrystals.EMBER.core.get());
        helper.assertTrue(core.hasCraftingRemainingItem(), "the core leaves nothing behind in the grid");
        helper.assertTrue(core.getCraftingRemainingItem().is(ModCrystals.EMBER.core.get()), "the core does not come back");
        helper.succeed();
    }

    /** The Ancients' Suit is armor for Ad Astra's space checks and this mod's liner check. */
    @GameTest(template = "empty")
    public static void ancientSuitIsEnvironmentProof(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        var tag = com.adastraplanets.registry.ModTags.Items.ENVIRONMENT_PROOF_ARMOR;
        helper.assertFalse(com.adastraplanets.registry.ModSuit.wearsFullSet(player, tag), "a bare player counts as suited");
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(com.adastraplanets.registry.ModSuit.HELMET.get()));
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(com.adastraplanets.registry.ModSuit.CHESTPLATE.get()));
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, new ItemStack(com.adastraplanets.registry.ModSuit.LEGGINGS.get()));
        helper.assertFalse(com.adastraplanets.registry.ModSuit.wearsFullSet(player, tag), "three pieces count as the set");
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, new ItemStack(com.adastraplanets.registry.ModSuit.BOOTS.get()));
        helper.assertTrue(com.adastraplanets.registry.ModSuit.wearsFullSet(player, tag), "the full set is not environment-proof");
        helper.assertTrue(earth.terrarium.adastra.common.items.armor.SpaceSuitItem.hasFullSet(player,
                earth.terrarium.adastra.common.tags.ModItemTags.SPACE_RESISTANT_ARMOR), "Ad Astra does not see the suit");
        helper.assertTrue(new ItemStack(com.adastraplanets.registry.ModSuit.CHESTPLATE.get()).getAttributeModifiers().modifiers().stream()
                .anyMatch(entry -> entry.attribute().is(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR) && entry.modifier().amount() > 0),
                "the suit gives no armor (armor_stats data map)");
        helper.succeed();
    }

    /** The suit worn in the liner's Curios slots counts as worn, with other armor in the armor slots. */
    @GameTest(template = "empty")
    public static void ancientSuitWorksAsCurios(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        var inventory = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player);
        helper.assertTrue(inventory.isPresent(), "the player has no Curios inventory");
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE));
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(com.adastraplanets.registry.ModSuit.HELMET.get()));
        inventory.get().setEquippedCurio("liner_chest", 0, new ItemStack(com.adastraplanets.registry.ModSuit.CHESTPLATE.get()));
        inventory.get().setEquippedCurio("liner_legs", 0, new ItemStack(com.adastraplanets.registry.ModSuit.LEGGINGS.get()));
        helper.assertFalse(com.adastraplanets.registry.ModSuit.suited(player), "three pieces count as the suit");
        inventory.get().setEquippedCurio("liner_feet", 0, new ItemStack(com.adastraplanets.registry.ModSuit.BOOTS.get()));
        helper.assertTrue(com.adastraplanets.registry.ModSuit.suited(player), "the suit in the liner slots does not count");
        helper.assertTrue(earth.terrarium.adastra.api.systems.OxygenApi.API.hasOxygen(player), "Ad Astra still finds no oxygen for the suited player");
        helper.succeed();
    }

    /** A sequence lock: a wrong rune puts the lit ones out; the right order opens the gate near it. */
    @GameTest(template = "empty")
    public static void glyphSequenceOpensTheGate(GameTestHelper helper) {
        var G = com.adastraplanets.block.GlyphRuneBlock.class;
        BlockPos first = new BlockPos(0, 1, 0);
        BlockPos second = new BlockPos(1, 1, 0);
        BlockPos decoy = new BlockPos(2, 1, 0);
        BlockPos gate = new BlockPos(1, 1, 2);
        var rune = ModAncient.GLYPH_RUNE.get().defaultBlockState();
        helper.setBlock(first, rune.setValue(com.adastraplanets.block.GlyphRuneBlock.ORDER, 1));
        helper.setBlock(second, rune.setValue(com.adastraplanets.block.GlyphRuneBlock.ORDER, 2)
                .setValue(com.adastraplanets.block.GlyphRuneBlock.GLYPH, com.adastraplanets.block.GlyphRuneBlock.Glyph.RING));
        helper.setBlock(decoy, rune.setValue(com.adastraplanets.block.GlyphRuneBlock.GLYPH, com.adastraplanets.block.GlyphRuneBlock.Glyph.MOON));
        helper.setBlock(gate, ModAncient.ANCIENT_GATE.get().defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        touch(helper, player, first);
        helper.assertBlockProperty(first, com.adastraplanets.block.GlyphRuneBlock.LIT, true);
        touch(helper, player, decoy);
        helper.assertBlockProperty(first, com.adastraplanets.block.GlyphRuneBlock.LIT, false);
        touch(helper, player, first);
        touch(helper, player, second);
        helper.assertBlockProperty(second, com.adastraplanets.block.GlyphRuneBlock.LIT, true);
        helper.assertBlockProperty(gate, AncientGateBlock.OPEN, true);
        helper.succeed();
    }

    private static void touch(GameTestHelper helper, Player player, BlockPos local) {
        BlockPos pos = helper.absolutePos(local);
        helper.getLevel().getBlockState(pos).useWithoutItem(helper.getLevel(), player,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    /** The expedition's reader opens the steel door beside it with an access card, and refuses anything else. */
    @GameTest(template = "empty")
    public static void accessCardOpensTheDoor(GameTestHelper helper) {
        BlockPos reader = new BlockPos(1, 1, 1);
        BlockPos door = new BlockPos(0, 1, 1);
        helper.setBlock(reader, com.adastraplanets.registry.ModExpedition.ACCESS_READER.get());
        var steelDoor = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse("ad_astra:steel_door"));
        helper.setBlock(door, steelDoor.defaultBlockState());
        helper.setBlock(door.above(), steelDoor.defaultBlockState().setValue(net.minecraft.world.level.block.DoorBlock.HALF,
                net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        use(helper, player, reader, new ItemStack(ModCrystals.ROSE.shard.get()));
        helper.assertBlockProperty(reader, com.adastraplanets.block.AccessReaderBlock.GRANTED, false);
        use(helper, player, reader, new ItemStack(com.adastraplanets.registry.ModExpedition.ACCESS_CARD.get()));
        helper.assertBlockProperty(reader, com.adastraplanets.block.AccessReaderBlock.GRANTED, true);
        helper.runAfterDelay(2, () -> {
            helper.assertBlockProperty(door, net.minecraft.world.level.block.DoorBlock.OPEN, true);
            helper.succeed();
        });
    }

    private static void use(GameTestHelper helper, Player player, BlockPos local, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos pos = helper.absolutePos(local);
        var state = helper.getLevel().getBlockState(pos);
        state.useItemOn(stack, helper.getLevel(), player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    private AncientRuinGameTests() {
    }
}
