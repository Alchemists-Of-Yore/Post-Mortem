package dev.tazer.post_mortem.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    private void pm$fireImmune(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(isImmuneToFire());
    }

    @Inject(method = "onInsideBlock", at = @At("TAIL"))
    protected void pm$onInsideBlock(BlockState state, CallbackInfo ci) {}

    @Unique
    protected boolean isImmuneToFire() {
        return false;
    }
}
