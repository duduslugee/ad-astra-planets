package com.adastraplanets.compat.mysticalagriculture;

import com.adastraplanets.AdAstraPlanets;
import com.blakebr0.mysticalagriculture.api.IMysticalAgriculturePlugin;
import com.blakebr0.mysticalagriculture.api.MysticalAgriculturePlugin;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.api.crop.CropTier;
import com.blakebr0.mysticalagriculture.api.crop.CropType;
import com.blakebr0.mysticalagriculture.api.lib.LazyIngredient;
import com.blakebr0.mysticalagriculture.api.registry.ICropRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * A Mystical Agriculture crop per metal (found by Mystical Agriculture's own plugin scan, so it loads only with it).
 * Each crop's tier and colour come from data/adastraplanets/compat/mysticalagriculture_crops.json (the tier follows
 * the tier of the world the metal is mined on); its seeds are infused from the metal's
 * ingots and its essence crafts back into raw metal (data/adastraplanets/recipe/compat/mysticalagriculture/).
 */
@MysticalAgriculturePlugin
public final class MysticalAgricultureCrops implements IMysticalAgriculturePlugin {
    private static final String CROPS = "/data/" + AdAstraPlanets.MOD_ID + "/compat/mysticalagriculture_crops.json";
    private static final List<CropTier> TIERS = List.of(CropTier.ONE, CropTier.TWO, CropTier.THREE, CropTier.FOUR, CropTier.FIVE);

    @Override
    public void onRegisterCrops(ICropRegistry registry) {
        for (Map.Entry<String, JsonElement> entry : read().entrySet()) {
            JsonObject crop = entry.getValue().getAsJsonObject();
            String metal = entry.getKey();
            int tier = Math.max(1, Math.min(TIERS.size(), crop.get("tier").getAsInt()));
            int colour = Integer.parseInt(crop.get("colour").getAsString().substring(1), 16);
            registry.register(new Crop(AdAstraPlanets.id(metal), TIERS.get(tier - 1), CropType.RESOURCE, colour,
                    LazyIngredient.tag("c:ingots/" + metal)));
        }
    }

    private static JsonObject read() {
        try (InputStream stream = MysticalAgricultureCrops.class.getResourceAsStream(CROPS)) {
            if (stream == null) {
                AdAstraPlanets.LOGGER.warn("Missing {}: no Mystical Agriculture crops", CROPS);
                return new JsonObject();
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (java.io.IOException exception) {
            AdAstraPlanets.LOGGER.error("Could not read {}", CROPS, exception);
            return new JsonObject();
        }
    }
}
