package dev.tazer.post_mortem.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected float lastHurt;

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void shouldStayAlive(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            cir.setReturnValue(th$shouldStayAlive(damageSource));
        }
    }

    @ModifyVariable(method = "checkTotemDeathProtection", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    protected ItemStack findTotem(ItemStack original) {
        if (original != null) return original;

        ItemStack totem = th$findTotem();
        return totem.isEmpty() ? null : totem;
    }

    @Inject(method = "canBeSeenByAnyone", at = @At("RETURN"), cancellable = true)
    protected void th$canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {}

    @Unique
    protected ItemStack th$findTotem() {
        return ItemStack.EMPTY;
    }

    @Unique
    protected boolean th$shouldStayAlive(DamageSource damageSource) {
        return false;
    }
}
