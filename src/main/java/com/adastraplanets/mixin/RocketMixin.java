package com.adastraplanets.mixin;

import com.adastraplanets.api.event.RocketLaunchEvent;
import earth.terrarium.adastra.common.entities.vehicles.Rocket;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets {@link RocketLaunchEvent} veto the launch countdown of every rocket, Ad Astra's included. */
@Mixin(Rocket.class)
public abstract class RocketMixin {
    @Unique
    private boolean adastraplanets$denied;
    @Unique
    private long adastraplanets$deniedAt;

    @Inject(method = "initiateLaunchSequence", at = @At("HEAD"), cancellable = true)
    private void adastraplanets$fireLaunchEvent(CallbackInfo ci) {
        Rocket rocket = (Rocket) (Object) this;
        if (rocket.level().isClientSide()) {
            return;
        }
        long now = rocket.level().getGameTime();
        // Ad Astra retries the launch every tick while the key is held or the pad is powered: decide once per second
        if (this.adastraplanets$denied && now - this.adastraplanets$deniedAt < 20) {
            ci.cancel();
            return;
        }
        this.adastraplanets$denied = NeoForge.EVENT_BUS.post(new RocketLaunchEvent(rocket)).isCanceled();
        if (this.adastraplanets$denied) {
            this.adastraplanets$deniedAt = now;
            ci.cancel();
        }
    }
}
