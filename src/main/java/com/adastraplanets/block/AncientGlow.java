package com.adastraplanets.block;

import com.adastraplanets.AdAstraPlanets;
import com.adastraplanets.registry.ModCrystals;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Which system's crystal an ancient block glows with (and, for a socket, which crystal opens it): one per crystal of
 * {@link ModCrystals}, in the same order, so the runes of a ruin shine in its system's colour.
 */
public enum AncientGlow implements StringRepresentable {
    EMBER, AMBER, HALO, TIDE, COBALT, ROSE, FROST;

    public static final EnumProperty<AncientGlow> PROPERTY = EnumProperty.create("glow", AncientGlow.class);

    public ModCrystals.CrystalSet crystal() {
        return ModCrystals.ALL.get(this.ordinal());
    }

    /** The items a crystal socket of this glow accepts: by default its crystal's shard. */
    public TagKey<Item> socketKeys() {
        return TagKey.create(Registries.ITEM, AdAstraPlanets.id("socket_keys/" + this.getSerializedName()));
    }

    /** The items a core socket of this glow accepts: by default its system's Light Core. */
    public TagKey<Item> coreKeys() {
        return TagKey.create(Registries.ITEM, AdAstraPlanets.id("core_keys/" + this.getSerializedName()));
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(java.util.Locale.ROOT);
    }
}
