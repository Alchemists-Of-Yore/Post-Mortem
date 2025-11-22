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
    private ItemStack pm$findTotem(ItemStack original) {
        if (original != null) return original;

        ItemStack totem = findTotem();
        return totem.isEmpty() ? null : totem;
    }

    @Inject(method = "canBeSeenByAnyone", at = @At("RETURN"), cancellable = true)
    protected void postmortem$canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {}

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    protected void onSoulDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {}

    @Inject(method = "canUsePortal", at = @At("RETURN"), cancellable = true)
    private void canUsePortal(boolean allowPassengers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) cir.setReturnValue(canChangeLevel());
    }

    @Unique
    protected ItemStack findTotem() {
        return ItemStack.EMPTY;
    }

    /**
     * @param damageSource The damage source that caused the damage
     * @return {@code true} if the entity should survive fatal damage
     */
    @Unique
    protected boolean shouldStayAlive(DamageSource damageSource) {
        return false;
    }

    @Unique
    protected boolean canChangeLevel() {
        return true;
    }

    // TODO: take bound spirit with you when traveling levels
}
