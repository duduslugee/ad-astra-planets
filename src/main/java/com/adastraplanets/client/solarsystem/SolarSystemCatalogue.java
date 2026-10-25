package com.adastraplanets.client.solarsystem;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.adastra.api.client.events.AdAstraClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The first page of the planet screen: every solar system drawn as a small live map with its name above it, laid out
 * in a grid that fills the space right of Ad Astra's list. The mouse wheel zooms, dragging pans, and clicking a map
 * opens that system. One instance lives with each open screen.
 */
public final class SolarSystemCatalogue {
    /** Right edge of Ad Astra's solar system list on this page; the grid lives to its right. */
    public static final int LIST_EDGE = 116;
    private static final int BASE_CELL = 130;
    private static final int NAME_HEIGHT = 12;
    /** Fallback radius (unscaled GUI pixels) for systems drawn by Ad Astra or other mods, which have no map file. */
    private static final float DEFAULT_RADIUS = 130;
    private static final float MIN_ZOOM = 0.35F;
    private static final float MAX_ZOOM = 3F;

    private record Cell(ResourceLocation system, int x, int y, int size) {
        boolean contains(double mouseX, double mouseY) {
            return mouseX >= this.x && mouseX < this.x + this.size && mouseY >= this.y - NAME_HEIGHT && mouseY < this.y + this.size;
        }
    }

    private final List<Cell> cells = new ArrayList<>();
    private float zoom = 1F;
    private float panX;
    private float panY;
    private double pressX;
    private double pressY;
    private boolean pressed;
    private boolean dragged;

    public static boolean inArea(double mouseX) {
        return mouseX > LIST_EDGE;
    }

    public void render(GuiGraphics graphics, List<ResourceLocation> systems, int width, int height, double mouseX, double mouseY) {
        this.cells.clear();
        int left = LIST_EDGE + 6;
        int areaWidth = width - left - 6;
        int areaHeight = height - 12;
        if (systems.isEmpty() || areaWidth <= 0) {
            return;
        }
        int cell = Math.round(BASE_CELL * this.zoom);
        int pitch = cell + 10;
        int rowPitch = cell + NAME_HEIGHT + 10;
        int columns = Mth.clamp(areaWidth / pitch, 1, systems.size());
        int rows = (systems.size() + columns - 1) / columns;
        int gridWidth = columns * pitch - 10;
        int gridHeight = rows * rowPitch - 10;
        this.clampPan(gridWidth, gridHeight, areaWidth, areaHeight);
        int x0 = Math.round(left + (areaWidth - gridWidth) / 2F + this.panX);
        int y0 = Math.round(6 + (areaHeight - gridHeight) / 2F + this.panY);

        Font font = Minecraft.getInstance().font;
        graphics.enableScissor(left, 0, width, height);
        for (int i = 0; i < systems.size(); i++) {
            ResourceLocation system = systems.get(i);
            int x = x0 + (i % columns) * pitch;
            int y = y0 + (i / columns) * rowPitch + NAME_HEIGHT;
            Cell box = new Cell(system, x, y, cell);
            this.cells.add(box);
            boolean hovered = !this.dragged && box.contains(mouseX, mouseY);

            graphics.fill(x, y, x + cell, y + cell, hovered ? 0x60202A60 : 0x40101530);
            graphics.renderOutline(x, y, cell, cell, hovered ? 0xFFFFFFFF : 0xFF3A4A8A);
            Component name = Component.translatableWithFallback(
                    "solar_system.%s.%s".formatted(system.getNamespace(), system.getPath()), title(system.getPath()));
            graphics.drawCenteredString(font, name, x + cell / 2, y - NAME_HEIGHT + 2, hovered ? 0xFFFF55 : 0xFFFFFF);
            graphics.flush();

            graphics.enableScissor(Math.max(x + 1, left), y + 1, x + cell - 1, y + cell - 1);
            drawMiniature(graphics, system, x + cell / 2F, y + cell / 2F, cell / 2F - 4, width, height);
            graphics.disableScissor();
        }
        graphics.disableScissor();
    }

    /**
     * Draws a system's full map shrunk to fit a circle of this radius. The model-view matrix is scaled rather than the
     * GUI pose so drawings that ignore the pose (Ad Astra's orbit rings) shrink too.
     */
    private static void drawMiniature(GuiGraphics graphics, ResourceLocation system, float cx, float cy, float radius, int width, int height) {
        SolarSystemMap map = SolarSystemMaps.get(system);
        float scale = radius / (map != null ? SolarSystemMaps.radius(map) : DEFAULT_RADIUS);
        Matrix4fStack view = RenderSystem.getModelViewStack();
        view.pushMatrix();
        view.translate(cx, cy, 0);
        view.scale(scale, scale, 1);
        view.translate(-width / 2F, -height / 2F, 0);
        RenderSystem.applyModelViewMatrix();
        if (map != null) {
            SolarSystemMaps.render(graphics, system, width, height, hit -> {});
        } else {
            AdAstraClientEvents.RenderSolarSystemEvent.fire(graphics, system, width, height);
        }
        graphics.flush();
        view.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    private void clampPan(int gridWidth, int gridHeight, int areaWidth, int areaHeight) {
        // the grid may move until its far edge reaches the middle of the area, never further
        float limitX = Math.max(0, (gridWidth - areaWidth) / 2F) + areaWidth / 3F;
        float limitY = Math.max(0, (gridHeight - areaHeight) / 2F) + areaHeight / 3F;
        this.panX = Mth.clamp(this.panX, -limitX, limitX);
        this.panY = Mth.clamp(this.panY, -limitY, limitY);
    }

    public void zoom(double scroll) {
        this.zoom = Mth.clamp(this.zoom * (float) Math.pow(1.15, scroll), MIN_ZOOM, MAX_ZOOM);
    }

    public void press(double mouseX, double mouseY) {
        this.pressed = true;
        this.dragged = false;
        this.pressX = mouseX;
        this.pressY = mouseY;
    }

    public boolean drag(double mouseX, double mouseY, double dragX, double dragY) {
        if (!this.pressed) {
            return false;
        }
        if (Math.abs(mouseX - this.pressX) + Math.abs(mouseY - this.pressY) > 3) {
            this.dragged = true;
        }
        if (this.dragged) {
            this.panX += (float) dragX;
            this.panY += (float) dragY;
        }
        return true;
    }

    /** Ends a press; returns the system clicked, unless the press was a drag. */
    @Nullable
    public ResourceLocation release(double mouseX, double mouseY) {
        boolean click = this.pressed && !this.dragged;
        this.pressed = false;
        this.dragged = false;
        if (click) {
            for (Cell cell : this.cells) {
                if (cell.contains(mouseX, mouseY)) {
                    return cell.system();
                }
            }
        }
        return null;
    }

    private static String title(String path) {
        StringBuilder out = new StringBuilder();
        for (String word : path.split("_")) {
            if (!word.isEmpty()) {
                out.append(out.isEmpty() ? "" : " ").append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return out.toString();
    }
}
