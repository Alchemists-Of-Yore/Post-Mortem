package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.common.entity.SoulState;
import dev.tazer.post_mortem.common.entity.SpectreAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
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
    private final ServerPlayer th$player = (ServerPlayer) (Object) this;

    @Unique @Nullable
    private GlobalPos grave = null;

    @Unique @Nullable
    private SpectreAnchor spectreAnchor = null;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void th$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("SoulState", getSoulState().id());

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
        setSoulState(SoulState.byId(compound.getInt("SoulState")));

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
    public @Nullable GlobalPos getGrave() {
        return grave;
    }

    @Override
    public void setGrave(@Nullable GlobalPos grave) {
        this.grave = grave;
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
    public void transitionTo(SoulState soulState) {
        super.transitionTo(soulState);

        if (soulState == SoulState.SPECTRE) {
            setAnchor(new SpectreAnchor(null, GlobalPos.of(th$player.level().dimension(), th$player.blockPosition())));
        }
    }

    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void findRespawnAndUseSpawnBlock(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        ServerLevel level = th$player.getLastDeathLocation().map(GlobalPos::dimension).map(th$player.server::getLevel).orElse(th$player.serverLevel());
        BlockPos pos = th$player.getLastDeathLocation().map(GlobalPos::pos).orElse(th$player.blockPosition());
        cir.setReturnValue(new DimensionTransition(level, Vec3.atCenterOf(pos), Vec3.ZERO, th$player.getYRot(), 0, postDimensionTransition));
        cir.cancel();
    }

    @Inject(method = "setGameMode", at = @At("RETURN"))
    private void reviveIfNotSurvival(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (!gameMode.isSurvival() && getSoulState() == SoulState.DOWNED) transitionTo(SoulState.ALIVE);
    }
}
