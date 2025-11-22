package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.block.GravestoneBlock;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import dev.tazer.post_mortem.mixininterface.ServerPlayerExtension;
import dev.tazer.post_mortem.registry.keys.PMTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends PlayerMixin implements ServerPlayerExtension {

    @Unique
    private final ServerPlayer pm$player = (ServerPlayer) (Object) this;

    @Unique
    private AnchorType lastAnchor;

    @Override
    protected void onSoulStateUpdated(SoulState soulState) {
        super.onSoulStateUpdated(soulState);

        // Reset all relevant attributes before applying new modifiers
        AttributeMap attributeMap = pm$player.getAttributes();
        List<Holder<Attribute>> attributes = List.of(
                Attributes.MAX_HEALTH,
                Attributes.MOVEMENT_SPEED,
                Attributes.ENTITY_INTERACTION_RANGE,
                Attributes.BLOCK_INTERACTION_RANGE
        );

        for (Holder<Attribute> attribute : attributes) {
            AttributeInstance attributeinstance = attributeMap.getInstance(attribute);
            if (attributeinstance != null) {
                attributeinstance.removeModifiers();
            }
        }

        Map<Holder<Attribute>, Double> newAttributeMap = null;

        switch (soulState) {
            case ALIVE -> setAnchor(null);
            case DOWNED -> {
                // Copied from ServerPlayer#die
                // Sends death message to players
                if (pm$player.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
                    Component component = pm$player.getCombatTracker().getDeathMessage();

                    Team team = pm$player.getTeam();
                    PlayerList playerList = pm$player.server.getPlayerList();
                    if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                        playerList.broadcastSystemMessage(component, false);
                    } else {
                        switch (team.getDeathMessageVisibility()) {
                            case HIDE_FOR_OTHER_TEAMS -> playerList.broadcastSystemToTeam(pm$player, component);
                            case HIDE_FOR_OWN_TEAM -> playerList.broadcastSystemToAllExceptTeam(pm$player, component);
                        }
                    }
                }

                newAttributeMap = Map.of(
                        Attributes.MAX_HEALTH, -14D,
                        Attributes.MOVEMENT_SPEED, -0.06,
                        Attributes.ENTITY_INTERACTION_RANGE, -3D,
                        Attributes.BLOCK_INTERACTION_RANGE, -4.5D
                );

                pm$player.setHealth(pm$player.getMaxHealth());
            }
            case SPIRIT -> {
                newAttributeMap = Map.of(
                        Attributes.ENTITY_INTERACTION_RANGE, -3D,
                        Attributes.BLOCK_BREAK_SPEED, -1D
                );

                if (getAnchor() == null) setAnchor(findValidAnchor(AnchorType.DEATH));
                pm$player.getInventory().dropAll();
            }
            case MANIFESTATION -> {
                newAttributeMap = Map.of(Attributes.MAX_HEALTH, -10D);

                pm$player.setHealth(pm$player.getMaxHealth());
            }
        }

        if (newAttributeMap != null) {
            for (Map.Entry<Holder<Attribute>, Double> entry : newAttributeMap.entrySet()) {
                AttributeInstance attributeinstance = attributeMap.getInstance(entry.getKey());
                if (attributeinstance != null) {
                    attributeinstance.removeModifiers();
                    attributeinstance.addPermanentModifier(new AttributeModifier(ResourceLocation.parse(entry.getKey().getRegisteredName()), entry.getValue(), AttributeModifier.Operation.ADD_VALUE));
                }
            }
        }
    }

    @Override
    protected void onAnchorUpdated(SpiritAnchor anchor) {
        super.onAnchorUpdated(anchor);

        // Return manifestation to spirit if the anchor is removed
        if (anchor == null) {
            if (getSoulState() == SoulState.MANIFESTATION) {
                setSoulState(SoulState.SPIRIT);
            }
        } else {
            if (anchor.uuid().isPresent()) {
                Player player = pm$player.level().getPlayerByUUID(anchor.uuid().get());
                if (player != null) {
                    player.setSpirit(pm$player.getUUID());
                }
            } else {
                // Link to the new anchor if it's a censer
                GlobalPos centre = anchor.getPos(pm$player.level());

                if (centre != null) {
                    Level level = pm$player.server.getLevel(centre.dimension());
                    if (level != null) {
                        if (level.getBlockEntity(centre.pos()) instanceof AbstractCenserBlockEntity censer) {
                            censer.addLink(pm$player);
                        }
                    }
                }
            }
        }

        validateAnchor();
    }

    /**
     * Persists soul state and grave location when copying player data
     */
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreSoulData(ServerPlayer dead, boolean keepEverything, CallbackInfo ci) {
        setSoulState(dead.getSoulState());
        setGrave(dead.getGrave());
    }

    /**
     * Overrides the respawn position logic to force the player to respawn at their
     * death location.
     */
    @Inject(method = "findRespawnPositionAndUseSpawnBlock", at = @At("HEAD"), cancellable = true)
    private void respawnAtDeathLocation(boolean keepInventory, DimensionTransition.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<DimensionTransition> cir) {
        ServerLevel level = pm$player.getLastDeathLocation().map(GlobalPos::dimension).map(pm$player.server::getLevel).orElse(pm$player.serverLevel());
        BlockPos pos = pm$player.getLastDeathLocation().map(GlobalPos::pos).orElse(pm$player.blockPosition());
        cir.setReturnValue(new DimensionTransition(level, Vec3.atCenterOf(pos), Vec3.ZERO, pm$player.getYRot(), 0, postDimensionTransition));
        cir.cancel();
    }

    @Inject(method = "setGameMode", at = @At("RETURN"))
    private void maybeRevive(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (!gameMode.isSurvival() && getSoulState() == SoulState.DOWNED) setSoulState(SoulState.ALIVE);
    }

    @Override
    public void setAnchor(@Nullable SpiritAnchor spiritAnchor) {
        SpiritAnchor previous = getAnchor();

        if (previous != null) {
            if (previous.type() != AnchorType.CENSER && previous.type() != AnchorType.PLAYER) lastAnchor = previous.type();
            if (previous.uuid().isPresent()) {
                Player player = pm$player.level().getPlayerByUUID(previous.uuid().get());
                if (player != null) {
                    player.setSpirit(null);
                }
            } else {
                GlobalPos centre = previous.getPos(pm$player.level());

                if (centre != null) {
                    Level level = pm$player.server.getLevel(centre.dimension());
                    if (level != null) {
                        if (level.getBlockEntity(centre.pos()) instanceof AbstractCenserBlockEntity censer) {
                            censer.removeLink();
                        }
                    }
                }
            }
        }

        if (spiritAnchor == null) {
            if (getSoulState() == SoulState.SPIRIT) {
                spiritAnchor = findValidAnchor(AnchorType.PLAYER);
                if (spiritAnchor == null && lastAnchor != null) spiritAnchor = findValidAnchor(lastAnchor);
                if (spiritAnchor == null) spiritAnchor = findValidAnchor(AnchorType.DEATH);
                if (spiritAnchor == null) spiritAnchor = new SpiritAnchor(null, GlobalPos.of(Level.OVERWORLD, pm$player.server.getWorldData().overworldData().getSpawnPos()), AnchorType.SPAWN);
            }
        }

        super.setAnchor(spiritAnchor);
    }

    @Override
    public @Nullable SpiritAnchor findValidAnchor(AnchorType type) {
        UUID uuid = null;
        GlobalPos pos = null;

        switch (type) {
            case DEATH -> pos = pm$player.getLastDeathLocation().orElse(null);
            case GRAVESTONE -> {
                GlobalPos grave = pm$player.getGrave();
                if (grave != null) {
                    ServerLevel level = pm$player.server.getLevel(grave.dimension());
                    if (level != null) {
                        BlockPos blockPos = grave.pos();
                        BlockState state = level.getBlockState(blockPos);
                        if (state.getBlock() instanceof GravestoneBlock) pos = grave;
                        else setGrave(null);
                    }
                }
            }
            case BED -> {
                BlockPos respawnPos = pm$player.getRespawnPosition();

                if (respawnPos != null) {
                    ServerLevel level = pm$player.server.getLevel(pm$player.getRespawnDimension());
                    if (level != null) {
                        BlockState state = level.getBlockState(respawnPos);
                        if (state.getBlock() instanceof BedBlock) pos = GlobalPos.of(level.dimension(), respawnPos);
                        else pm$player.setRespawnPosition(Level.OVERWORLD, null, 0, false, false);
                    }
                }
            }
            case PLAYER -> {
                Player validPlayer = pm$player.level().getNearestPlayer(pm$player.getX(), pm$player.getY(), pm$player.getZ(), 20, entity -> {
                    if (entity instanceof Player player) {
                        SoulState soulState = player.getSoulState();
                        return soulState == SoulState.ALIVE || soulState == SoulState.DOWNED;
                    }

                    return false;
                });

                if (validPlayer != null) uuid = validPlayer.getUUID();
            }
            default -> {
                return null;
            }
        }

        return uuid != null || pos != null ? new SpiritAnchor(uuid, pos, type) : null;
    }

    /**
     * Ensures the player is within range of their anchor.
     * If they are too far or in the wrong dimension, they are teleported back.
     */
    @Override
    public void validateAnchor() {
        if (!pm$player.level().getBiome(pm$player.blockPosition()).is(PMTags.SPIRITS_ROAM_IN)) {
            SpiritAnchor anchor = pm$player.getAnchor();
            if (anchor != null) {
                GlobalPos centre = anchor.getPos(pm$player.level());

                if (centre != null) {
                    Vec3 position = Vec3.atCenterOf(centre.pos());
                    if (pm$player.level().dimension() != centre.dimension() || pm$player.distanceToSqr(position) > 20 * 20) {
                        ServerLevel level = pm$player.server.getLevel(centre.dimension());
                        if (level != null) {
                            pm$player.teleportTo(level, position.x, position.y, position.z, pm$player.getYRot(), pm$player.getXRot());
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void maybeCancelAttack(Entity targetEntity, CallbackInfo ci) {
        if (!getSoulState().canAttack()) ci.cancel();
    }
}
