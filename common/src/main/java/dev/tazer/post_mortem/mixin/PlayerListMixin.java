package dev.tazer.post_mortem.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.post_mortem.block.AbstractCenserBlock;
import dev.tazer.post_mortem.block.CenserLinkState;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    @Nullable
    public abstract ServerPlayer getPlayer(UUID playerUUID);

    @ModifyVariable(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V", at = @At("HEAD"), argsOnly = true)
    private PlayerChatMessage colorSpiritMessages(PlayerChatMessage message, @Local(argsOnly = true) ServerPlayer sender) {
        if (sender != null && sender.getSoulState() != SoulState.ALIVE && sender.getSoulState() != SoulState.DOWNED) {
            message = message.withUnsignedContent(Component.literal(message.signedContent()).withColor(0xdde2f0));
        }

        return message;
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerRemoved(ServerPlayer player, CallbackInfo ci) {
        SpiritAnchor anchor = player.getAnchor();

        if (anchor != null && anchor.uuid().isEmpty()) {
            GlobalPos pos = anchor.getPos(player.level());

            if (pos != null) {
                ServerLevel level = player.server.getLevel(pos.dimension());
                if (level != null && level.getBlockEntity(pos.pos()) instanceof AbstractCenserBlockEntity censer && censer.spirit.orElse(null) == player.getUUID()) {
                    player.level().setBlockAndUpdate(pos.pos(), censer.getBlockState().setValue(AbstractCenserBlock.LINK_STATE, CenserLinkState.WEAK));
                }
            }
        }
    }

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void onPlayerPlaced(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        SpiritAnchor anchor = player.getAnchor();

        if (anchor != null) {
            boolean linked = false;
            UUID uuid = anchor.uuid().orElse(null);

            if (uuid == null) {
                GlobalPos pos = anchor.globalPos().orElseThrow();
                ServerLevel level = player.server.getLevel(pos.dimension());
                if (anchor.type() == AnchorType.CENSER && level != null && level.getBlockEntity(pos.pos()) instanceof AbstractCenserBlockEntity censer) {
                    if (censer.spirit.orElse(null) == player.getUUID()) {
                        player.level().setBlockAndUpdate(pos.pos(), censer.getBlockState().setValue(AbstractCenserBlock.LINK_STATE, CenserLinkState.STRONG));
                    }
                }
            } else if (anchor.type() == AnchorType.PLAYER) {
                Player hauntedPlayer = getPlayer(uuid);
                if (hauntedPlayer != null) {
                    SoulState soulState = hauntedPlayer.getSoulState();
                    if (soulState == SoulState.ALIVE || soulState == SoulState.DOWNED) {
                        linked = true;
                    }
                }
            }

            player.validateAnchor();
            if (!linked) player.setAnchor(null);
        }
    }
}
