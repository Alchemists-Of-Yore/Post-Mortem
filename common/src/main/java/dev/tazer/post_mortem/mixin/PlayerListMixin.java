package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At("HEAD"))
    private void colorSpiritMessages(PlayerChatMessage message, Predicate<ServerPlayer> shouldFilterMessageTo, ServerPlayer sender, ChatType.Bound boundChatType, CallbackInfo ci) {
        if (sender.getSoulState() != SoulState.ALIVE && sender.getSoulState() != SoulState.DOWNED) {
            message = message.withUnsignedContent(Component.literal(message.signedContent()).withColor(0xe7eaf2));
        }
    }
}
