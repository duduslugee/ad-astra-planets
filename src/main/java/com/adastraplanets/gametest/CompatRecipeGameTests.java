package com.adastraplanets.gametest;

import com.adastraplanets.AdAstraPlanets;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Map;

/**
 * The recipes for other mods' machines (data/adastraplanets/recipe/compat/) load when their mod is installed: every
 * one of them, so none was dropped for a malformed field. Run with the mods via ./gradlew runGameTestServer
 * -PcompatTest; without a mod its recipes are simply absent and the check for it is skipped.
 */
@GameTestHolder(AdAstraPlanets.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CompatRecipeGameTests {
    /** Recipes per mod (data/adastraplanets/recipe/compat/<mod>/). */
    private static final Map<String, Integer> EXPECTED = Map.ofEntries(Map.entry("create", 46), Map.entry("enderio", 23), Map.entry("hephaestus", 230), Map.entry("immersiveengineering", 46), Map.entry("industrialforegoing", 23), Map.entry("mekanism", 23), Map.entry("modern_industrialization", 46), Map.entry("mysticalagriculture", 23), Map.entry("pneumaticcraft", 23), Map.entry("productivebees", 69), Map.entry("productivemetalworks", 253), Map.entry("replication", 115));

    /** Mods whose recipes give no plain item result to check (matter values, fluids, bee products, drills). */
    private static final java.util.Set<String> NO_RESULT = java.util.Set.of("replication", "hephaestus",
            "productivemetalworks", "productivebees", "industrialforegoing", "enderio", "modern_industrialization",
            "pneumaticcraft");

    @GameTest(template = "empty")
    public static void compatRecipesLoad(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager().getRecipes();
        StringBuilder problems = new StringBuilder();
        EXPECTED.forEach((mod, expected) -> {
            String prefix = "compat/" + mod + "/";
            long loaded = recipes.stream().map(RecipeHolder::id)
                    .filter(id -> id.getNamespace().equals(AdAstraPlanets.MOD_ID) && id.getPath().startsWith(prefix)).count();
            boolean present = ModList.get().isLoaded(mod);
            if (present && loaded != expected) {
                problems.append(mod).append(": ").append(loaded).append(" of ").append(expected).append(" recipes loaded; ");
            }
            if (!present && loaded != 0) {
                problems.append(mod).append(" is absent but ").append(loaded).append(" of its recipes loaded; ");
            }
            // each one makes something (a tag output that matched nothing, or a wrong item id, comes out empty)
            recipes.stream().filter(holder -> holder.id().getNamespace().equals(AdAstraPlanets.MOD_ID) && holder.id().getPath().startsWith(prefix))
                    .filter(holder -> !NO_RESULT.contains(mod) && holder.value().getResultItem(helper.getLevel().registryAccess()).isEmpty())
                    .forEach(holder -> problems.append(holder.id()).append(" makes nothing; "));
            AdAstraPlanets.LOGGER.info("[compat] {}: {}, {} recipes", mod, present ? "installed" : "absent", loaded);
        });
        helper.assertTrue(problems.isEmpty(), problems.toString());
        helper.succeed();
    }
}
