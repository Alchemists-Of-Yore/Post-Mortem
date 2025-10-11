package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.common.entity.IPlayerExtension;
import dev.tazer.post_mortem.common.entity.PlayerUtil;
import dev.tazer.post_mortem.common.entity.SoulState;
import dev.tazer.post_mortem.common.entity.Spectre;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.platform.Services;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin implements IPlayerExtension, Spectre {

    @Unique
    private final Player pm$player = (Player) (Object) this;

    @Unique @Nullable
    protected GlobalPos grave = null;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSoulStateData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(SOUL_STATE, SoulState.ALIVE);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void th$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("SoulState", getSoulState().id());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void th$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        setSoulState(SoulState.byId(compound.getInt("SoulState")));
    }

    @Override
    protected void onSyncedDataUpdated(EntityDataAccessor<?> key, CallbackInfo ci) {
        super.onSyncedDataUpdated(key, ci);

        if (key.equals(SOUL_STATE)) {
            SoulState soulState = getSoulState();

            switch (soulState) {
                case DOWNED -> {
                    AttributeMap attributeMap = pm$player.getAttributes();

                    Map<Holder<Attribute>, Double> newAttributeMap = Map.of(
                            Attributes.MAX_HEALTH, -14D,
                            Attributes.MOVEMENT_SPEED, -0.06,
                            Attributes.ENTITY_INTERACTION_RANGE, -3D,
                            Attributes.BLOCK_INTERACTION_RANGE, -4.5D
                    );

                    for(Map.Entry<Holder<Attribute>, Double> entry : newAttributeMap.entrySet()) {
                        AttributeInstance attributeinstance = attributeMap.getInstance(entry.getKey());
                        if (attributeinstance != null) {
                            attributeinstance.removeModifiers();
                            attributeinstance.addPermanentModifier(new AttributeModifier(ResourceLocation.parse(entry.getKey().getRegisteredName()), entry.getValue(), AttributeModifier.Operation.ADD_VALUE));
                        }
                    }

                    pm$player.setHealth(pm$player.getMaxHealth());
                    setSoulState(SoulState.DOWNED);
                }
                case ETHEREAL -> {
                    AttributeMap attributeMap = pm$player.getAttributes();

                    Map<Holder<Attribute>, Double> newAttributeMap = Map.of(
                            Attributes.MAX_HEALTH, -10D
                    );

                    for(Map.Entry<Holder<Attribute>, Double> entry : newAttributeMap.entrySet()) {
                        AttributeInstance attributeinstance = attributeMap.getInstance(entry.getKey());
                        if (attributeinstance != null) {
                            attributeinstance.removeModifiers();
                            attributeinstance.addPermanentModifier(new AttributeModifier(ResourceLocation.parse(entry.getKey().getRegisteredName()), entry.getValue(), AttributeModifier.Operation.ADD_VALUE));
                        }
                    }

                    pm$player.setHealth(pm$player.getMaxHealth());
                    setSoulState(SoulState.DOWNED);
                }
                default -> PlayerUtil.resetAttributes(pm$player);
            }
        }
    }

    @Override
    public SoulState getSoulState() {
        return pm$player.getEntityData().get(SOUL_STATE);
    }

    @Override
    public void setSoulState(SoulState soulState) {
        pm$player.getEntityData().set(SOUL_STATE, soulState);
    }

    @Override
    public @Nullable GlobalPos getGrave() {
        if (grave == null && pm$player.level().isClientSide()) Services.PLATFORM.sendToServer(new GraveRequestPayload());
        return grave;
    }

    @Override
    protected ItemStack th$findTotem() {
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
    protected boolean th$shouldStayAlive(DamageSource damageSource) {
        SoulState soulState = getSoulState();

        setSoulState(SoulState.SPECTRE);

        return switch (soulState) {
            case ALIVE -> {
                if (lastHurt <= pm$player.getMaxHealth() + 20) {
                    setSoulState(SoulState.DOWNED);
                    yield true;
                } else yield false;
            }
            case ETHEREAL -> {
                pm$player.getInventory().dropAll();
                yield true;
            }
            default -> false;
        };
    }

    @Override
    protected void th$canBeSeenByAnyone(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            SoulState state = getSoulState();
            if (state == SoulState.DOWNED || state == SoulState.SPECTRE) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void swimIfDowned(CallbackInfo ci) {
        if (getSoulState() == SoulState.DOWNED) {
            pm$player.setSwimming(true);
            ci.cancel();
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void notHurtIfSpectre(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (getSoulState() == SoulState.SPECTRE) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}