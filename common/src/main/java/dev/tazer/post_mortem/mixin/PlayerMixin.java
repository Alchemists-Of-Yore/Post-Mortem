package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.entity.PlayerUtil;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import dev.tazer.post_mortem.mixininterface.PlayerExtension;
import dev.tazer.post_mortem.mixininterface.Spirit;
import dev.tazer.post_mortem.registry.PMDamageTypes;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin implements PlayerExtension, Spirit {

    @Unique
    private final Player pm$player = (Player) (Object) this;

    @Unique
    private static final EntityDataAccessor<Integer> SOUL_STATE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Optional<UUID>> ENTITY_ANCHOR = SynchedEntityData.defineId(Player.class, EntityDataSerializers.OPTIONAL_UUID);
    @Unique
    private static final EntityDataAccessor<Optional<GlobalPos>> BLOCK_ANCHOR = SynchedEntityData.defineId(Player.class, EntityDataSerializers.OPTIONAL_GLOBAL_POS);
    @Unique
    private static final EntityDataAccessor<Optional<GlobalPos>> GRAVE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.OPTIONAL_GLOBAL_POS);

    @Unique
    private int downedTime = 0;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void pm$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(SOUL_STATE, 0);
        builder.define(ENTITY_ANCHOR, Optional.empty());
        builder.define(BLOCK_ANCHOR, Optional.empty());
        builder.define(GRAVE, Optional.empty());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addSoulStateData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("SoulState", getSoulState().id());

        GlobalPos grave = getGrave();
        if (grave != null) {
            GlobalPos.CODEC
                    .encodeStart(NbtOps.INSTANCE, grave)
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .ifPresent(tag -> compound.put("GraveLocation", tag));
        }

        SpiritAnchor spiritAnchor = getAnchor();
        if (spiritAnchor != null) {
            SpiritAnchor.CODEC
                    .encodeStart(NbtOps.INSTANCE, spiritAnchor)
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .ifPresent(tag -> compound.put("SpiritAnchor", tag));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readSoulStateData(CompoundTag compound, CallbackInfo ci) {
        setSoulState(SoulState.byId(compound.getInt("SoulState")));

        if (compound.contains("GraveLocation")) {
            setGrave(GlobalPos.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("GraveLocation"))
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .orElse(null)
            );
        }

        if (compound.contains("SpiritAnchor")) {
            setAnchor(SpiritAnchor.CODEC
                    .parse(NbtOps.INSTANCE, compound.get("SpiritAnchor"))
                    .resultOrPartial(PostMortem.LOGGER::error)
                    .orElse(null)
            );
        }
    }

    @Override
    protected void pm$onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        super.pm$onSyncedDataUpdated(key, ci);

        if (key.equals(SOUL_STATE)) onSoulStateUpdated(getSoulState());
    }

    @Unique
    protected void onSoulStateUpdated(SoulState soulState) {
        PlayerUtil.resetAttributes(pm$player);
        AttributeMap attributeMap = pm$player.getAttributes();
        Map<Holder<Attribute>, Double> newAttributeMap = null;

        switch (soulState) {
            case ALIVE -> setAnchor(null);
            case DOWNED -> {
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
                        Attributes.ENTITY_INTERACTION_RANGE, -3D
                );

                GlobalPos pos = getAnchor() == null ? GlobalPos.of(pm$player.level().dimension(), pm$player.blockPosition()) : getAnchor().pos();
                setAnchor(new SpiritAnchor(null, pos));
                pm$player.getInventory().dropAll();
            }
            case MANIFESTATION -> {

                newAttributeMap = Map.of(
                        Attributes.MAX_HEALTH, -10D
                );

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
    public SoulState getSoulState() {
        return SoulState.byId(pm$player.getEntityData().get(SOUL_STATE));
    }

    @Override
    public void setSoulState(SoulState soulState) {
        pm$player.getEntityData().set(SOUL_STATE, soulState.id());
    }

    @Override
    public @Nullable SpiritAnchor getAnchor() {
        SynchedEntityData data = pm$player.getEntityData();
        Optional<GlobalPos> pos = data.get(BLOCK_ANCHOR);
        Optional<UUID> uuid = data.get(ENTITY_ANCHOR);
        return pos.isPresent() || uuid.isPresent() ? new SpiritAnchor(uuid, pos) : null;
    }

    @Override
    public void setAnchor(@Nullable SpiritAnchor spiritAnchor) {
        SynchedEntityData data = pm$player.getEntityData();
        if (spiritAnchor != null) {
            data.set(ENTITY_ANCHOR, Optional.ofNullable(spiritAnchor.uuid()));
            data.set(BLOCK_ANCHOR, Optional.ofNullable(spiritAnchor.pos()));
        } else {
            data.set(ENTITY_ANCHOR, Optional.empty());
            data.set(BLOCK_ANCHOR, Optional.empty());
        }
    }

    @Override
    public @Nullable GlobalPos getGrave() {
        return pm$player.getEntityData().get(GRAVE).orElse(null);
    }

    @Override
    public void setGrave(@Nullable GlobalPos grave) {
        pm$player.getEntityData().set(GRAVE, Optional.ofNullable(grave));
    }

    @Override
    protected ItemStack findTotem() {
        Inventory inventory = pm$player.getInventory();
        for (int i = 0; i < inventory.items.size(); ++i) {
            ItemStack stack = inventory.items.get(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                stack.shrink(1);
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected boolean shouldStayAlive(DamageSource damageSource) {
        SoulState soulState = getSoulState();

        setSoulState(SoulState.SPIRIT);

        return switch (soulState) {
            case ALIVE -> {
                if (lastHurt <= pm$player.getMaxHealth() + 20) {
                    setSoulState(SoulState.DOWNED);
                    yield true;
                } else yield false;
            }
            case MANIFESTATION -> true;
            default -> false;
        };
    }

    @Override
    protected void pm$canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            SoulState state = getSoulState();
            if (state == SoulState.DOWNED || state == SoulState.SPIRIT) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void swimWhenDowned(CallbackInfo ci) {
        if (getSoulState() == SoulState.DOWNED) {
            pm$player.setSwimming(true);
            ci.cancel();
        }
    }

    @Override
    protected void pm$onInsideBlock(BlockState state, CallbackInfo ci) {
        super.pm$onInsideBlock(state, ci);

        if (state.getBlock() instanceof BaseFireBlock && getSoulState() == SoulState.SPIRIT) {
            Vec3 vec3 = pm$player.getDeltaMovement();
            float factor = 0.75F;
            pm$player.setDeltaMovement(vec3.multiply(factor, 1, factor));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void cancelHurtWhenSpirit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (getSoulState() == SoulState.SPIRIT) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Override
    protected boolean fireImmune() {
        return getSoulState() == SoulState.SPIRIT;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (pm$player.getSoulState() == SoulState.SPIRIT || pm$player.getSoulState() == SoulState.MANIFESTATION) {
            SpiritAnchor spiritAnchor = pm$player.getAnchor();
            if (spiritAnchor != null) {
                Vec3 centre = spiritAnchor.getCentre(pm$player.level());
                if (centre != null) {
                    double distance = pm$player.position().distanceTo(centre);

                    Vec3 motion = pm$player.getDeltaMovement();

                    if (pm$player.position().add(motion).distanceTo(centre) > distance) {
                        double scale;
                        if (distance <= 15.0) {
                            scale = 1.0;
                        } else if (distance >= 25.0) {
                            scale = 0.0;
                        } else {
                            double t = (distance - 15.0) / 10.0;

                            double sharpness = 4.0;
                            scale = Math.pow(1.0 - t, sharpness);

                            scale = Mth.clamp(scale, 0.0, 1.0);
                        }

                        if (scale > 0) pm$player.setSprinting(false);

                        pm$player.setDeltaMovement(motion.scale(scale));
                    }
                }
            }
        }

        if (!pm$player.isDeadOrDying()) {
            SoulState soulState = getSoulState();

            if (soulState == SoulState.MANIFESTATION) {
                if (pm$player.tickCount % 120 == 0) pm$player.heal(1);
            }

            if (soulState == SoulState.DOWNED) {
                downedTime++;

                if (downedTime % (20 + pm$player.getFoodData().getFoodLevel() * 4) == 0) {
                    pm$player.hurt(PMDamageTypes.getSimpleDamageSource(pm$player.level(), PMDamageTypes.BLEED), 1);
                }
            }
        }
    }
}