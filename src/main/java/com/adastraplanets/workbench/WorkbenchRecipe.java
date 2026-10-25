package com.adastraplanets.workbench;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A rocket built in one of our workbenches: the same shape as Ad Astra's NASA Workbench recipe (up to 14 ingredients,
 * one per slot in the workbench's order, and the result), under the workbench's own recipe type:
 *
 * <pre>
 * { "type": "adastraplanets:advanced_nasa_workbench", "ingredients": [ ... ], "result": { "id": "...", "count": 1 } }
 * </pre>
 */
public record WorkbenchRecipe(Workbench workbench, List<Ingredient> ingredients, ItemStack result) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level level) {
        if (input.size() < this.ingredients.size()) {
            return false;
        }
        for (int i = 0; i < this.ingredients.size(); i++) {
            if (!this.ingredients.get(i).test(input.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredients.toArray(Ingredient[]::new));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.workbench.serializer();
    }

    @Override
    public RecipeType<?> getType() {
        return this.workbench.recipeType();
    }

    /** One serializer per workbench; the workbench is not in the JSON, it is the recipe's type. */
    public record Serializer(Workbench workbench, MapCodec<WorkbenchRecipe> codec,
                             StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> streamCodec) implements RecipeSerializer<WorkbenchRecipe> {
        public static Serializer of(Workbench workbench) {
            MapCodec<WorkbenchRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(WorkbenchRecipe::ingredients),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(WorkbenchRecipe::result)
            ).apply(instance, (ingredients, result) -> new WorkbenchRecipe(workbench, ingredients, result)));
            StreamCodec<RegistryFriendlyByteBuf, WorkbenchRecipe> stream = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), WorkbenchRecipe::ingredients,
                    ItemStack.STREAM_CODEC, WorkbenchRecipe::result,
                    (ingredients, result) -> new WorkbenchRecipe(workbench, ingredients, result));
            return new Serializer(workbench, codec, stream);
        }
    }
}
