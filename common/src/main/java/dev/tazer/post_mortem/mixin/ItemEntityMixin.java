package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void cancelMaybe(Player player, CallbackInfo ci) {
        if (player.getSoulState() == SoulState.SPIRIT) ci.cancel();
    }
}
