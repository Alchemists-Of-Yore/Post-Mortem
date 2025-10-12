package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin {

    @Unique
    private final ServerPlayer pm$player = (ServerPlayer) (Object) this;

    @Override
    protected void onSoulStateUpdated(SoulState soulState) {
        super.onSoulStateUpdated(soulState);

        if (soulState == SoulState.DOWNED) {
            // Copied from ServerPlayer::die
            // Sends death message to players
            if (pm$player.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                Component component = pm$player.getCombatTracker().getDeathMessage();

                Team team = pm$player.getTeam();
                if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                    pm$player.server.getPlayerList().broadcastSystemMessage(component, false);
                } else {
                    switch (team.getDeathMessageVisibility()) {
                        case HIDE_FOR_OTHER_TEAMS ->
                                pm$player.server.getPlayerList().broadcastSystemToTeam(pm$player, component);
                        case HIDE_FOR_OWN_TEAM ->
                                pm$player.server.getPlayerList().broadcastSystemToAllExceptTeam(pm$player, component);
                    }
                }
            }
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreFrom(ServerPlayer dead, boolean keepEverything, CallbackInfo ci) {
        setSoulState(dead.getSoulState());
        setGrave(dead.getGrave());
    }

    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void findRespawnAndUseSpawnBlock(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        ServerLevel level = pm$player.getLastDeathLocation().map(GlobalPos::dimension).map(pm$player.server::getLevel).orElse(pm$player.serverLevel());
        BlockPos pos = pm$player.getLastDeathLocation().map(GlobalPos::pos).orElse(pm$player.blockPosition());
        cir.setReturnValue(new DimensionTransition(level, Vec3.atCenterOf(pos), Vec3.ZERO, pm$player.getYRot(), 0, postDimensionTransition));
        cir.cancel();
    }

    @Inject(method = "setGameMode", at = @At("RETURN"))
    private void reviveIfNotSurvival(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (!gameMode.isSurvival() && getSoulState() == SoulState.DOWNED) setSoulState(SoulState.ALIVE);
    }
}
