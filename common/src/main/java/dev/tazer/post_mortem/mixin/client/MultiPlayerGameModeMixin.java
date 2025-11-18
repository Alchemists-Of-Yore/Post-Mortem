package dev.tazer.post_mortem.mixin.client;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "canHurtPlayer", at = @At("RETURN"), cancellable = true)
    private void cannotHurtWhileSpirit(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && minecraft.player.getSoulState() == SoulState.SPIRIT) cir.setReturnValue(false);
    }
}
