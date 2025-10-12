package dev.tazer.post_mortem.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Final
    public Gui gui;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Unique
    private int lastUsed;

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getInventory()Lnet/minecraft/world/entity/player/Inventory;"))
    private Inventory selectSpiritSlot(LocalPlayer instance, Operation<Inventory> original, @Local int i) {
        if (instance.getSoulState() == SoulState.SPIRIT) {
            gui.getSpiritGui().onHotbarSelected(i);
        }

        return original.call(instance);
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 4))
    private boolean selectSpiritSlot(KeyMapping instance, Operation<Boolean> original) {
        boolean toReturn = original.call(instance);

        if (player.getSoulState() == SoulState.ALIVE) {
            return toReturn;
        }

        return false;
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void cancelAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player.getSoulState() == SoulState.SPIRIT || player.getSoulState() == SoulState.DOWNED) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @WrapOperation(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult cancelMaybe(MultiPlayerGameMode instance, LocalPlayer localPlayer, InteractionHand hand, BlockHitResult blockHitResult, Operation<InteractionResult> original) {
        if (player.getSoulState() == SoulState.SPIRIT && player.tickCount - lastUsed <= 40) {
            return InteractionResult.FAIL;
        }

        lastUsed = player.tickCount;
        return original.call(instance, localPlayer, hand, blockHitResult);
    }
}
