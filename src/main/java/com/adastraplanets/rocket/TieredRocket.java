package com.adastraplanets.rocket;

import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import earth.terrarium.adastra.common.registry.ModDataManagers;
import earth.terrarium.common_storage_lib.fluid.impl.SimpleFluidStorage;
import earth.terrarium.common_storage_lib.fluid.util.FluidStorageData;
import earth.terrarium.common_storage_lib.resources.fluid.FluidResource;
import earth.terrarium.common_storage_lib.resources.fluid.util.FluidAmounts;
import earth.terrarium.common_storage_lib.storage.base.CommonStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Supplier;

/**
 * An Ad Astra rocket whose balancing comes from a {@link RocketDefinition} instead of Ad Astra's built-in tables.
 * Extending {@link Rocket} keeps the launch pad, planet selection screen, fuel menu and landing working unchanged.
 */
public class TieredRocket extends Rocket implements GeoEntity {
    private static final String SPEED_TAG = "adastraplanets:speed";
    private static final Component UNDEFINED_MESSAGE = Component.translatable("message.adastraplanets.rocket_undefined").withStyle(ChatFormatting.RED);

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final SimpleFluidStorage tank;
    private float flightSpeed;

    public TieredRocket(EntityType<?> type, Level level, Supplier<Item> item) {
        super(type, level, initialProperties(type, item.get()));
        // Rocket's own tank has a fixed size; this one shares the same stored contents but uses the definition's capacity.
        RocketDefinition definition = RocketDefinitions.get(type);
        long capacity = definition == null ? 0 : FluidAmounts.toPlatformAmount(definition.fuelCapacity());
        this.tank = new SimpleFluidStorage(this, ModDataManagers.FLUID_CONTENTS, 1, capacity)
                .filter(0, fluid -> isAcceptedFuel(fluid));
    }

    private static RocketProperties initialProperties(EntityType<?> type, Item item) {
        RocketDefinition definition = RocketDefinitions.get(type);
        if (definition == null) {
            return new RocketProperties(0, item, 0, TagKey.create(Registries.FLUID, BuiltInRegistries.ENTITY_TYPE.getKey(type)));
        }
        return new RocketProperties(definition.tier(), item, definition.ridingOffset(), definition.fuel());
    }

    @Nullable
    public RocketDefinition definition() {
        return RocketDefinitions.get(this.getType());
    }

    private boolean isAcceptedFuel(FluidResource fluid) {
        RocketDefinition definition = this.definition();
        return definition != null && fluid.is(definition.fuel());
    }

    @Override
    public int tier() {
        RocketDefinition definition = this.definition();
        return definition == null ? 0 : definition.tier();
    }

    @Override
    public CommonStorage<FluidResource> getFluids(Direction direction) {
        return this.tank;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float scale) {
        RocketDefinition definition = this.definition();
        return definition == null ? super.getPassengerAttachmentPoint(entity, dimensions, scale) : new Vec3(0, definition.ridingOffset(), 0);
    }

    @Override
    public boolean canLaunch() {
        if (this.definition() == null) {
            if (this.getControllingPassenger() instanceof ServerPlayer player) {
                player.displayClientMessage(UNDEFINED_MESSAGE, true);
            }
            return false;
        }
        return super.canLaunch();
    }

    @Override
    public boolean consumeFuel(boolean simulate) {
        RocketDefinition definition = this.definition();
        if (this.level().isClientSide() || definition == null) {
            return false;
        }
        FluidResource fuel = this.tank.getResource(0);
        int cost = fuel.is(definition.efficientFuel()) ? definition.efficientLaunchCost() : definition.launchCost();
        long amount = FluidAmounts.toPlatformAmount(cost);
        return this.tank.extract(fuel, amount, simulate) >= amount;
    }

    @Override
    public void tick() {
        super.tick();
        // Rocket accelerates with fixed values; replace its vertical speed with the definition's curve.
        RocketDefinition definition = this.definition();
        if (definition != null && this.hasLaunched() && !this.isLaunching()) {
            this.flightSpeed = Math.min(definition.maxSpeed(), this.flightSpeed + definition.acceleration());
            Vec3 delta = this.getDeltaMovement();
            this.setDeltaMovement(delta.x(), this.flightSpeed, delta.z());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.flightSpeed = compound.getFloat(SPEED_TAG);
        this.tank.readSnapshot((FluidStorageData) ModDataManagers.FLUID_CONTENTS.get(this));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(SPEED_TAG, this.flightSpeed);
    }

    /**
     * A rocket's animation file may hold two loops, animation.<rocket>.idle (standing) and animation.<rocket>.launch
     * (counting down and flying); a rocket without them stays still.
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "motion", 10, state -> {
            String name = "animation." + BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getPath()
                    + (this.isLaunching() || this.hasLaunched() ? ".launch" : ".idle");
            // the predicate only runs where the rocket is drawn, so the client class is never touched on a server
            if (!com.adastraplanets.client.rocket.RocketAnimations.has(this.getType(), name)) {
                return PlayState.STOP;
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop(name));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }
}
