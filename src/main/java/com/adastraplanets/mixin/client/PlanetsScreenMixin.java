package com.adastraplanets.mixin.client;

import com.adastraplanets.client.solarsystem.SolarSystemCatalogue;
import com.adastraplanets.client.solarsystem.SolarSystemMap;
import com.adastraplanets.client.solarsystem.SolarSystemMaps;
import earth.terrarium.adastra.api.client.events.AdAstraClientEvents;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.client.components.LabeledImageButton;
import earth.terrarium.adastra.client.screens.PlanetsScreen;
import earth.terrarium.adastra.common.compat.cadmus.CadmusIntegration;
import earth.terrarium.adastra.common.menus.PlanetsMenu;
import earth.terrarium.adastra.common.planets.AdAstraData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Turns Ad Astra's planet screen into a map you can click, as in ExtraPlanets:
 * <ul>
 *     <li>solar systems with a map file are drawn from it, replacing Ad Astra's fixed drawing;</li>
 *     <li>clicking a planet on the map opens its close-up (planet, moons, information panel); clicking a moon or the
 *     planet there selects it. Reachable bodies are selected through Ad Astra's own selection, so its Land button,
 *     space stations and back button keep working; bodies that are not destinations yet are shown without them;</li>
 *     <li>the left-hand list shows every planet drawn on the map, in orbit order, reachable or not (those out of
 *     reach in grey, opening their close-up); moons are picked in their planet's close-up;</li>
 *     <li>solar systems without any selectable planet get no button;</li>
 *     <li>the first page shows every solar system as a small live map to click, besides Ad Astra's list
 *     (see {@link SolarSystemCatalogue}).</li>
 * </ul>
 */
@Mixin(PlanetsScreen.class)
public abstract class PlanetsScreenMixin extends AbstractContainerScreen<PlanetsMenu> {
    @Shadow
    private int pageIndex;
    @Shadow
    private Planet selectedPlanet;
    @Shadow
    private ResourceLocation selectedSolarSystem;
    @Shadow
    private Button backButton;
    @Shadow
    private double scrollAmount;
    @Shadow
    private double spaceStationScrollAmount;
    @Shadow
    @Final
    private List<Button> spaceStationButtons;
    @Shadow
    private boolean hasMultipleSolarSystems;
    @Shadow
    @Final
    private List<Button> buttons;

    /** Close-up opened from the map for a body that is not a destination (or not selectable); null otherwise. */
    @Unique
    @Nullable
    private SolarSystemMap.Body adastraplanets$focus;
    /** What the close-up's panel describes when Ad Astra has no selection for it (unexplored bodies). */
    @Unique
    @Nullable
    private ResourceLocation adastraplanets$focusSelection;
    @Unique
    private final List<SolarSystemMaps.Hit> adastraplanets$hits = new ArrayList<>();
    /** The moons listed under their planet: where they start in the list, how many, and when they were opened. */
    @Unique
    private int adastraplanets$moonStart = -1;
    @Unique
    private int adastraplanets$moonCount;
    @Unique
    private long adastraplanets$expandedAt;
    @Unique
    @Nullable
    private ResourceLocation adastraplanets$expanded;
    @Unique
    private final SolarSystemCatalogue adastraplanets$catalogue = new SolarSystemCatalogue();

    private PlanetsScreenMixin(PlanetsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    /** The body whose close-up is showing: the selected planet's (or its moon's) body, else the explicit focus. */
    @Unique
    @Nullable
    private SolarSystemMap.Body adastraplanets$focusedBody() {
        if (this.pageIndex == 2 && this.selectedPlanet != null) {
            return SolarSystemMaps.bodyFor(this.selectedSolarSystem, this.selectedPlanet.dimension().location()).orElse(null);
        }
        return this.pageIndex == 1 ? this.adastraplanets$focus : null;
    }

    @Redirect(method = "renderBackground", at = @At(value = "INVOKE",
            target = "Learth/terrarium/adastra/api/client/events/AdAstraClientEvents$RenderSolarSystemEvent;fire(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;II)V"))
    private void adastraplanets$drawSolarSystem(GuiGraphics graphics, ResourceLocation solarSystem, int width, int height) {
        this.adastraplanets$hits.clear();
        if (this.pageIndex == 0) {
            Minecraft minecraft = Minecraft.getInstance();
            double mouseX = minecraft.mouseHandler.xpos() * width / minecraft.getWindow().getScreenWidth();
            double mouseY = minecraft.mouseHandler.ypos() * height / minecraft.getWindow().getScreenHeight();
            this.adastraplanets$catalogue.render(graphics, this.adastraplanets$visibleSolarSystems().stream()
                    .sorted(Comparator.comparing(ResourceLocation::getPath)).toList(), width, height, mouseX, mouseY);
            return;
        }
        SolarSystemMap map = SolarSystemMaps.get(solarSystem);
        if (map == null) {
            AdAstraClientEvents.RenderSolarSystemEvent.fire(graphics, solarSystem, width, height);
            return;
        }
        if (this.pageIndex == 1 && this.backButton != null) {
            // with a single solar system Ad Astra hides it on page 1, but a close-up still needs a way back
            this.backButton.visible = this.hasMultipleSolarSystems || this.adastraplanets$focus != null;
        }
        SolarSystemMap.Body focus = this.adastraplanets$focusedBody();
        if (focus != null) {
            ResourceLocation selected = this.pageIndex == 2 && this.selectedPlanet != null
                    ? this.selectedPlanet.dimension().location() : this.adastraplanets$focusSelection;
            SolarSystemMaps.renderFocus(graphics, map, focus, selected, this.menu, width, height, this.adastraplanets$hits::add);
        } else {
            SolarSystemMaps.render(graphics, solarSystem, width, height, this.adastraplanets$hits::add);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Ad Astra's back button steps one page back, which from a close-up of a body that is not a destination
        // (shown on page 1) would leave the solar system; close the close-up instead
        if (button == 0 && this.adastraplanets$focus != null && this.backButton != null && this.backButton.visible
                && this.backButton.isMouseOver(mouseX, mouseY)) {
            this.backButton.playDownSound(Minecraft.getInstance().getSoundManager());
            this.adastraplanets$focus = null;
            this.adastraplanets$focusSelection = null;
            this.rebuildWidgets(); // the list goes back from the planet's moons to every planet
            return true;
        }
        // widgets (buttons) first; then map icons; the container screen's own handling treats any click outside its
        // slots as handled, so it must come last
        for (var child : this.children()) {
            if (child.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(child);
                return true;
            }
        }
        if (button == 0 && this.pageIndex == 0 && SolarSystemCatalogue.inArea(mouseX)) {
            this.adastraplanets$catalogue.press(mouseX, mouseY);
            return true;
        }
        if (button == 0 && this.pageIndex != 0) {
            for (SolarSystemMaps.Hit hit : this.adastraplanets$hits) {
                if (hit.contains(mouseX, mouseY)) {
                    this.adastraplanets$select(hit);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.pageIndex == 0 && this.adastraplanets$catalogue.drag(mouseX, mouseY, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.pageIndex == 0) {
            ResourceLocation system = this.adastraplanets$catalogue.release(mouseX, mouseY);
            if (system != null) {
                // same as Ad Astra's own solar system button
                this.pageIndex = 1;
                this.selectedSolarSystem = system;
                this.rebuildWidgets();
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /** On the first page the wheel zooms the solar system grid; over the list it keeps scrolling the list. */
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$zoomCatalogue(double mouseX, double mouseY, double scrollX, double scrollY,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (this.pageIndex == 0 && SolarSystemCatalogue.inArea(mouseX)) {
            this.adastraplanets$catalogue.zoom(scrollY);
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void adastraplanets$select(SolarSystemMaps.Hit hit) {
        Planet planet = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, hit.planet()));
        if (this.adastraplanets$selectable(hit.planet(), planet)) {
            this.adastraplanets$focus = null;
            this.adastraplanets$focusSelection = null;
            this.selectedPlanet = planet;
            this.pageIndex = 2;
        } else {
            // not a destination (yet): show its close-up and information without Ad Astra's selection
            this.adastraplanets$focus = hit.body();
            this.adastraplanets$focusSelection = hit.planet();
            this.selectedPlanet = null;
            this.pageIndex = 1;
        }
        this.rebuildWidgets();
    }

    /** Whether Ad Astra can select this as a destination (and so offer landing). */
    @Unique
    private boolean adastraplanets$selectable(ResourceLocation id, @Nullable Planet planet) {
        return planet != null && !planet.isSpace() && !this.menu.disabledPlanets().contains(id) && this.menu.tier() >= planet.tier();
    }

    /** Escape (and Ad Astra's back) leaves a close-up opened for a body that is not a destination. */
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$closeFocus(CallbackInfo ci) {
        if (this.pageIndex == 1 && this.adastraplanets$focus != null) {
            this.adastraplanets$focus = null;
            this.adastraplanets$focusSelection = null;
            this.rebuildWidgets();
            ci.cancel();
        }
    }

    /**
     * The left-hand list is one list of every planet the map draws, in orbit order, instead of only the destinations
     * the rocket can reach: reachable ones select as in Ad Astra, the rest open their close-up and are greyed. The
     * planet whose close-up is open has its moons listed under it, indented; the selected entry is highlighted.
     */
    @Inject(method = "createPlanetButtons", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$listMapPlanets(CallbackInfo ci) {
        SolarSystemMap map = SolarSystemMaps.get(this.selectedSolarSystem);
        if (map == null) {
            return;
        }
        ci.cancel();
        if (CadmusIntegration.cadmusLoaded()) {
            this.menu.getSortedPlanets().forEach(planet -> CadmusIntegration.addClientListeners(planet.dimension()));
        }
        SolarSystemMap.Body focus = this.adastraplanets$focusedBody();
        ResourceLocation selected = this.pageIndex == 2 && this.selectedPlanet != null
                ? this.selectedPlanet.dimension().location() : this.pageIndex == 1 ? this.adastraplanets$focusSelection : null;
        this.adastraplanets$moonStart = -1;
        this.adastraplanets$moonCount = 0;
        for (SolarSystemMap.Body body : map.planets().stream().filter(body -> body.planet().isPresent())
                .sorted(Comparator.comparingInt(SolarSystemMap.Body::orbit)).toList()) {
            ResourceLocation id = body.planet().get();
            this.adastraplanets$listButton(id, body, false, id.equals(selected));
            if (focus != null && focus.planet().equals(body.planet())) {
                this.adastraplanets$moonStart = this.buttons.size();
                for (SolarSystemMap.Moon moon : focus.moons().stream().filter(moon -> moon.planet().isPresent())
                        .sorted(Comparator.comparingInt(SolarSystemMap.Moon::orbit)).toList()) {
                    ResourceLocation moonId = moon.planet().get();
                    this.adastraplanets$listButton(moonId, focus, true, moonId.equals(selected));
                }
                this.adastraplanets$moonCount = this.buttons.size() - this.adastraplanets$moonStart;
            }
        }
        // a planet opened anew unfolds its moons; picking one of them keeps the list as it is
        ResourceLocation expanded = this.adastraplanets$moonCount > 0 ? focus.planet().get() : null;
        if (expanded != null && !expanded.equals(this.adastraplanets$expanded)) {
            this.adastraplanets$expandedAt = net.minecraft.Util.getMillis();
        }
        this.adastraplanets$expanded = expanded;
        // the list stays where it was scrolled to, within its new length
        this.scrollAmount = Math.min(this.scrollAmount, Math.max(0, this.buttons.size() * 24 - 131 - this.adastraplanets$extra()));
        // Ad Astra places its buttons only after drawing them, so new ones would spend a frame at the top (the list
        // would blink); place them now, as Ad Astra will
        for (int i = 0; i < this.buttons.size(); i++) {
            this.buttons.get(i).setY(i * 24 - (int) this.scrollAmount + (this.height / 2 - 41));
        }
        this.adastraplanets$unfold();
    }

    /**
     * One entry of the list: selects the destination (or opens its close-up). Moons are indented and tinted, out of
     * reach entries grey, the selected entry yellow and marked.
     */
    @Unique
    private void adastraplanets$listButton(ResourceLocation id, SolarSystemMap.Body body, boolean moon, boolean selected) {
        Planet planet = AdAstraData.getPlanet(ResourceKey.create(Registries.DIMENSION, id));
        Component label = planet != null ? this.menu.getPlanetName(planet.dimension()) : SolarSystemMaps.name(id);
        ChatFormatting colour = selected ? ChatFormatting.YELLOW
                : !this.adastraplanets$selectable(id, planet) ? ChatFormatting.GRAY : moon ? ChatFormatting.AQUA : ChatFormatting.WHITE;
        Component name = Component.literal(selected ? "» " : "").append(moon ? Component.literal("• ").append(label) : label)
                .withStyle(colour);
        this.buttons.add(this.addWidget(new LabeledImageButton(moon ? 18 : 10, 0, moon ? 91 : 99, 20, PlanetsScreen.BUTTON_SPRITES,
                button -> this.adastraplanets$select(new SolarSystemMaps.Hit(id, body, 0, 0, 0)), name)));
    }

    // ---- the panels fit their lists: never shorter than Ad Astra's (177 px), never past 8 px above the screen's bottom

    /**
     * How many pixels taller than Ad Astra's the panels are: enough for every entry of the planet list (moons counted
     * as far as they have unfolded) and of the space station list, as far as the screen allows.
     */
    @Unique
    private int adastraplanets$extra() {
        float entries = this.buttons.size() - this.adastraplanets$moonCount * (1.0F - this.adastraplanets$unfolded());
        // the planet list starts 41 px above the middle, the stations' at it; Ad Astra's panel ends 89 px below it
        int list = Math.round(entries * 24) + 2 - 130;
        int stations = this.pageIndex == 2 ? this.spaceStationButtons.size() * 24 + 2 - 89 : 0;
        int room = (this.height - 8) - (this.height / 2 + 89);
        return Math.clamp(Math.max(list, stations), 0, Math.max(0, room));
    }

    /** How far the moons have unfolded, 0 to 1, eased out. */
    @Unique
    private float adastraplanets$unfolded() {
        if (this.adastraplanets$moonCount == 0) {
            return 1.0F;
        }
        float t = net.minecraft.util.Mth.clamp((net.minecraft.Util.getMillis() - this.adastraplanets$expandedAt) / adastraplanets$UNFOLD, 0.0F, 1.0F);
        return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
    }

    /** Draws a panel sprite lengthened: its top, then a row from its lower part repeated, then its last rows. */
    @Redirect(method = "renderSelectionMenu", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void adastraplanets$tallPanel(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        int extra = this.adastraplanets$extra();
        int split = height - 12;                         // above the panels' rounded bottom corners
        graphics.blitSprite(sprite, width, height, 0, 0, x, y, width, split);
        for (int i = 0; i < extra; i++) {
            graphics.blitSprite(sprite, width, height, 0, split - 1, x, y + split + i, width, 1);
        }
        graphics.blitSprite(sprite, width, height, 0, split, x, y + split + extra, width, height - split);
    }

    /** The planet list's window (the first scissor box) and the space stations' (the second) reach the new bottom. */
    @org.spongepowered.asm.mixin.injection.ModifyArg(method = "renderButtons", index = 5, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lcom/teamresourceful/resourcefullib/client/utils/RenderUtils;createScissorBox(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;IIII)Lcom/teamresourceful/resourcefullib/client/scissor/ClosingScissorBox;"))
    private int adastraplanets$tallList(int height) {
        return height + this.adastraplanets$extra();
    }

    @org.spongepowered.asm.mixin.injection.ModifyArg(method = "renderButtons", index = 5, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lcom/teamresourceful/resourcefullib/client/utils/RenderUtils;createScissorBox(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;IIII)Lcom/teamresourceful/resourcefullib/client/scissor/ClosingScissorBox;"))
    private int adastraplanets$tallStations(int height) {
        return height + this.adastraplanets$extra();
    }

    /** Buttons in the lengthened part of the list can be clicked too. */
    @Inject(method = "render", at = @At("TAIL"))
    private void adastraplanets$tallListActive(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        int bottom = this.height / 2 + 88 + this.adastraplanets$extra();
        this.buttons.forEach(button -> button.active = button.getY() > this.height / 2 - 63 && button.getY() < bottom);
    }

    @Inject(method = "setScrollAmount", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$tallListScroll(double amount, CallbackInfo ci) {
        this.scrollAmount = net.minecraft.util.Mth.clamp(amount, 0.0, Math.max(0, this.buttons.size() * 24 - 131 - this.adastraplanets$extra()));
        ci.cancel();
    }

    @Inject(method = "setSpaceStationScrollAmount", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$tallStationScroll(double amount, CallbackInfo ci) {
        this.spaceStationScrollAmount = net.minecraft.util.Mth.clamp(amount, 0.0,
                Math.max(0, this.spaceStationButtons.size() * 24 - 90 - this.adastraplanets$extra()));
        ci.cancel();
    }

    @Shadow
    protected abstract void setScrollAmount(double amount);

    @Shadow
    protected abstract void setSpaceStationScrollAmount(double amount);

    /** The wheel scrolls the lists over their lengthened parts as well. */
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$tallListWheel(double mouseX, double mouseY, double scrollX, double scrollY,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (this.pageIndex == 0) {
            return;
        }
        double bottom = this.height / 2.0 + 88 + this.adastraplanets$extra();
        if (mouseX > 6 && mouseX < 112 && mouseY > this.height / 2.0 - 43 && mouseY < bottom) {
            this.setScrollAmount(this.scrollAmount - scrollY * 8.0);
            cir.setReturnValue(true);
        } else if (mouseX > 112 && mouseX < 224 && mouseY > this.height / 2.0 - 2 && mouseY < bottom) {
            this.setSpaceStationScrollAmount(this.spaceStationScrollAmount - scrollY * 8.0);
            cir.setReturnValue(true);
        }
    }

    /** How long the moons take to unfold, in milliseconds. */
    @Unique
    private static final float adastraplanets$UNFOLD = 250.0F;

    /**
     * Unfolds the moons: they slide out from under their planet one after another, pushing the planets below down,
     * easing out. Ad Astra lays the list out after drawing it, so the positions set here are the next frame's.
     */
    @Inject(method = "renderButtons", at = @At("RETURN"))
    private void adastraplanets$unfoldMoons(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.adastraplanets$unfold();
    }

    /** Moves the listed moons, and the planets below them, from their laid-out places to where the unfolding has them. */
    @Unique
    private void adastraplanets$unfold() {
        int start = this.adastraplanets$moonStart;
        int count = this.adastraplanets$moonCount;
        if (start < 1 || count == 0) {
            return;
        }
        float shown = this.adastraplanets$unfolded();
        for (int i = start; i < this.buttons.size(); i++) {
            Button button = this.buttons.get(i);
            if (i < start + count) {
                int rest = (i - start + 1) * 24;
                int offset = Math.round(rest * (1.0F - shown));
                button.setY(button.getY() - offset);
                // hidden while still tucked under the planet (drawn after it, it would cover it)
                button.visible = rest - offset >= 20;
            } else {
                button.setY(button.getY() - Math.round(count * 24 * (1.0F - shown)));
            }
        }
    }

    /** The left-hand list shows planets; moons are reached from their planet's close-up. */
    @Redirect(method = "createPlanetButtons",
            at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/menus/PlanetsMenu;getSortedPlanets()Ljava/util/List;"))
    private List<Planet> adastraplanets$planetsOnly(PlanetsMenu menu) {
        return menu.getSortedPlanets().stream()
                .filter(planet -> !SolarSystemMaps.isMoon(planet.dimension().location()))
                .toList();
    }

    @Redirect(method = "createSolarSystemButtons",
            at = @At(value = "INVOKE", target = "Learth/terrarium/adastra/common/planets/AdAstraData;solarSystems()Ljava/util/Set;"))
    private Set<ResourceLocation> adastraplanets$solarSystemButtons() {
        return this.adastraplanets$visibleSolarSystems();
    }

    @Unique
    private Set<ResourceLocation> adastraplanets$visibleSolarSystems() {
        return AdAstraData.planets().values().stream()
                .filter(planet -> !this.menu.disabledPlanets().contains(planet.dimension().location()))
                .map(Planet::solarSystem)
                .collect(Collectors.toUnmodifiableSet());
    }
}
