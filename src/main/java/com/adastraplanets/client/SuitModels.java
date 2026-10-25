package com.adastraplanets.client;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModSuit;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

/**
 * The Ancients' Suit's own model, bulkier than vanilla armor (in the manner of Ad Astra's suits): one mesh per armor
 * slot, built from {@code assets/adastraplanets/models/armor/ancient_suit.json} (made with its texture), worn in the
 * armor slots or drawn by the Curios renderer.
 */
public final class SuitModels {
    public static final ResourceLocation TEXTURE = AdAstraPlanets.id("textures/models/armor/ancient_suit.png");
    private static final String GEOMETRY = "/assets/" + AdAstraPlanets.MOD_ID + "/models/armor/ancient_suit.json";
    private static final Map<EquipmentSlot, String> SLOTS = Map.of(EquipmentSlot.HEAD, "head", EquipmentSlot.CHEST, "chest",
            EquipmentSlot.LEGS, "legs", EquipmentSlot.FEET, "feet");
    private static final Map<EquipmentSlot, ModelLayerLocation> LAYERS = new EnumMap<>(EquipmentSlot.class);
    private static final Map<EquipmentSlot, HumanoidModel<LivingEntity>> MODELS = new EnumMap<>(EquipmentSlot.class);

    static {
        SLOTS.forEach((slot, name) -> LAYERS.put(slot, new ModelLayerLocation(AdAstraPlanets.id("ancient_suit_" + name), "main")));
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        JsonObject geometry = read();
        JsonArray size = geometry.getAsJsonArray("texture_size");
        JsonObject slots = geometry.getAsJsonObject("slots");
        LAYERS.forEach((slot, layer) -> event.registerLayerDefinition(layer, () ->
                create(slots.getAsJsonObject(SLOTS.get(slot)), size.get(0).getAsInt(), size.get(1).getAsInt())));
    }

    public static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HumanoidModel<LivingEntity> model = model(slot);
                return model != null ? model : original;
            }
        }, ModSuit.PIECES.stream().map(piece -> piece.get()).toArray(net.minecraft.world.item.Item[]::new));
    }

    /** The baked model of a slot's pieces (baked once, from the current model set). */
    public static HumanoidModel<LivingEntity> model(EquipmentSlot slot) {
        ModelLayerLocation layer = LAYERS.get(slot);
        if (layer == null) {
            return null;
        }
        return MODELS.computeIfAbsent(slot, s -> new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(layer)));
    }

    /** Drops the baked models so they are baked again from reloaded layers. */
    public static void clear() {
        MODELS.clear();
    }

    private static LayerDefinition create(JsonObject parts, int width, int height) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        add(root, parts, "head", PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        add(root, parts, "body", PartPose.ZERO);
        add(root, parts, "right_arm", PartPose.offset(-5.0F, 2.0F, 0.0F));
        add(root, parts, "left_arm", PartPose.offset(5.0F, 2.0F, 0.0F));
        add(root, parts, "right_leg", PartPose.offset(-1.9F, 12.0F, 0.0F));
        add(root, parts, "left_leg", PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, width, height);
    }

    private static void add(PartDefinition root, JsonObject parts, String name, PartPose pose) {
        CubeListBuilder cubes = CubeListBuilder.create();
        if (parts.has(name)) {
            for (JsonElement element : parts.getAsJsonArray(name)) {
                JsonObject box = element.getAsJsonObject();
                JsonArray origin = box.getAsJsonArray("origin");
                JsonArray size = box.getAsJsonArray("size");
                JsonArray uv = box.getAsJsonArray("uv");
                cubes.texOffs(uv.get(0).getAsInt(), uv.get(1).getAsInt())
                        .mirror(box.has("mirror") && box.get("mirror").getAsBoolean())
                        .addBox(origin.get(0).getAsFloat(), origin.get(1).getAsFloat(), origin.get(2).getAsFloat(),
                                size.get(0).getAsFloat(), size.get(1).getAsFloat(), size.get(2).getAsFloat(),
                                new CubeDeformation(box.has("inflate") ? box.get("inflate").getAsFloat() : 0.0F));
            }
        }
        root.addOrReplaceChild(name, cubes, pose);
    }

    private static JsonObject read() {
        try (InputStream stream = SuitModels.class.getResourceAsStream(GEOMETRY)) {
            if (stream == null) {
                throw new IllegalStateException("Missing " + GEOMETRY);
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not read " + GEOMETRY, exception);
        }
    }

    private SuitModels() {
    }
}
