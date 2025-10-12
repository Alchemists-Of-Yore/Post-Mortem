package dev.tazer.post_mortem.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {

    @Shadow
    protected float lastHurt;

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void pm$shouldStayAlive(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) cir.setReturnValue(shouldStayAlive(damageSource));
    }

    @ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    protected ItemStack pm$findTotem(ItemStack original) {
        if (original != null) return original;

        ItemStack totem = findTotem();
        return totem.isEmpty() ? null : totem;
    }

    @Inject(method = "canBeSeenByAnyone", at = @At("RETURN"), cancellable = true)
    protected void pm$canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {}

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    protected void pm$onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {}

    @Unique
    protected ItemStack findTotem() {
        return ItemStack.EMPTY;
    }

    @Unique
    protected boolean shouldStayAlive(DamageSource damageSource) {
        return false;
    }

}
