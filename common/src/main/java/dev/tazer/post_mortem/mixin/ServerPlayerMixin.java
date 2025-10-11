package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.common.entity.SoulState;
import dev.tazer.post_mortem.common.entity.SpectreAnchor;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.platform.Services;
import dev.tazer.post_mortem.registry.PMDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
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

    @Unique @Nullable
    private SpectreAnchor spectreAnchor = null;

    @Unique
    private int downedTime = 0;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void th$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        GlobalPos grave = getGrave();
        if (grave != null) {
            GlobalPos.CODEC
                    .encodeStart(NbtOps.INSTANCE, grave)
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .ifPresent(tag -> compound.put("GraveLocation", tag));
        }

        SpectreAnchor spectreAnchor = getAnchor();
        if (spectreAnchor != null) {
            SpectreAnchor.CODEC
                    .encodeStart(NbtOps.INSTANCE, spectreAnchor)
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .ifPresent(tag -> compound.put("SpectreAnchor", tag));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void th$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("GraveLocation")) {
            setGrave(GlobalPos.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("GraveLocation"))
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .orElse(null)
            );
        }

        if (compound.contains("SpectreAnchor")) {
            setAnchor(SpectreAnchor.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("SpectreAnchor"))
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .orElse(null)
            );
        }
    }

    @Override
    protected void onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        super.onSyncedDataUpdated(key, ci);

        if (key.equals(SOUL_STATE)) {
            SoulState soulState = getSoulState();

            switch (soulState) {
                case ALIVE -> setAnchor(null);
                case DOWNED -> {
                    // Copied from ServerPlayer::die
                    // Sends death message to players
                    if (pm$player.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                        Component component = pm$player.getCombatTracker().getDeathMessage();

                        Team team = pm$player.getTeam();
                        if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                            pm$player.server.getPlayerList().broadcastSystemMessage(component, false);
                        } else {
                            switch (team.getDeathMessageVisibility()) {
                                case HIDE_FOR_OTHER_TEAMS -> pm$player.server.getPlayerList().broadcastSystemToTeam(pm$player, component);
                                case HIDE_FOR_OWN_TEAM -> pm$player.server.getPlayerList().broadcastSystemToAllExceptTeam(pm$player, component);
                            }
                        }
                    }
                }
                case SPECTRE -> {
                    GlobalPos pos = getAnchor() == null ? GlobalPos.of(pm$player.level().dimension(), pm$player.blockPosition()) : getAnchor().pos();
                    setAnchor(new SpectreAnchor(null, pos));
                }
            }
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreFrom(ServerPlayer dead, boolean keepEverything, CallbackInfo ci) {
        setSoulState(dead.getSoulState());
        setGrave(dead.getGrave());
    }

    @Override
    public @Nullable SpectreAnchor getAnchor() {
        return spectreAnchor;
    }

    @Override
    public void setAnchor(@Nullable SpectreAnchor spectreAnchor) {
        this.spectreAnchor = spectreAnchor;
    }

    @Override
    public void setGrave(@Nullable GlobalPos grave) {
        Services.PLATFORM.sendToClient(pm$player, new GraveRequestPayload());
        this.grave = grave;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (getSoulState() == SoulState.DOWNED && !pm$player.isDeadOrDying()) {
            downedTime++;

            if (downedTime % (20 + pm$player.getFoodData().getFoodLevel() * 4) == 0) {
                pm$player.hurt(PMDamageTypes.getSimpleDamageSource(pm$player.level(), PMDamageTypes.BLEED), 1);
            }
        }
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
