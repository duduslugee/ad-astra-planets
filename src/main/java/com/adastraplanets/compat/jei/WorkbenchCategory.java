package com.adastraplanets.compat.jei;

import com.adastraplanets.workbench.Workbench;
import com.adastraplanets.workbench.WorkbenchRecipe;
import earth.terrarium.adastra.client.screens.machines.NasaWorkbenchScreen;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * A workbench's rockets, laid out in the 14 slots of Ad Astra's NASA Workbench screen (the screen is Ad Astra's,
 * drawn the way its own JEI category draws it, since our workbenches open that same screen).
 */
public class WorkbenchCategory implements IRecipeCategory<WorkbenchRecipe> {
    private static final Map<Workbench, RecipeType<WorkbenchRecipe>> TYPES = new EnumMap<>(Workbench.class);
    /** Positions of the 14 input slots, in the recipe's order. */
    private static final int[][] SLOTS = {{57, 16}, {48, 34}, {66, 34}, {48, 52}, {66, 52}, {48, 70}, {66, 70}, {30, 88},
            {48, 88}, {66, 88}, {84, 88}, {30, 106}, {57, 106}, {84, 106}};

    private final Workbench workbench;
    private final IDrawable icon;

    public WorkbenchCategory(IGuiHelper guiHelper, Workbench workbench) {
        this.workbench = workbench;
        this.icon = guiHelper.createDrawableItemStack(BuiltInRegistries.ITEM.get(workbench.location()).getDefaultInstance());
    }

    public static RecipeType<WorkbenchRecipe> type(Workbench workbench) {
        return TYPES.computeIfAbsent(workbench, w -> new RecipeType<>(w.location(), WorkbenchRecipe.class));
    }

    @Override
    public RecipeType<WorkbenchRecipe> getRecipeType() {
        return type(this.workbench);
    }

    @Override
    public Component getTitle() {
        return BuiltInRegistries.BLOCK.get(this.workbench.location()).getName();
    }

    @Override
    public int getWidth() {
        return 180;
    }

    @Override
    public int getHeight() {
        return 145;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WorkbenchRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < SLOTS.length; i++) {
            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, SLOTS[i][0], SLOTS[i][1]);
            if (i < recipe.ingredients().size()) {
                slot.addIngredients(recipe.ingredients().get(i));
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 52).addItemStack(recipe.result());
    }

    @Override
    public void draw(WorkbenchRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(NasaWorkbenchScreen.TEXTURE, 1, -4, 0, 0, 177, 140, 177, 224);
        graphics.blit(NasaWorkbenchScreen.TEXTURE, 1, 136, 0, 217, 177, 7, 177, 224);
    }
}
