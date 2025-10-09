package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.common.entity.IPlayerExtension;
import dev.tazer.post_mortem.common.entity.PlayerUtil;
import dev.tazer.post_mortem.common.entity.SoulState;
import dev.tazer.post_mortem.common.entity.Spectre;
import dev.tazer.post_mortem.registry.PMDataSerializers;
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
    private final Player th$player = (Player) (Object) this;

    @Unique
    private static final EntityDataAccessor<SoulState> SOUL_STATE = SynchedEntityData.defineId(Player.class, PMDataSerializers.SOUL_STATE);

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
    public SoulState getSoulState() {
        return th$player.getEntityData().get(SOUL_STATE);
    }

    @Override
    public void setSoulState(SoulState soulState) {
        th$player.getEntityData().set(SOUL_STATE, soulState);
    }

    @Override
    protected ItemStack th$findTotem() {
        if (getSoulState() == SoulState.DOWNED) {
            Inventory inventory = th$player.getInventory();
            for (int i = 0; i < inventory.items.size(); ++i) {
                ItemStack stack = inventory.items.get(i);
                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    stack.shrink(1);
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    protected boolean th$shouldStayAlive(DamageSource damageSource) {
        if (getSoulState() == SoulState.ALIVE && lastHurt <= th$player.getMaxHealth() + 8) {
            transitionTo(SoulState.DOWNED);
            return true;
        }

        transitionTo(SoulState.SPECTRE);
        return false;
    }

    @Override
    public void transitionTo(SoulState soulState) {
        setSoulState(soulState);

        switch (soulState) {
            case DOWNED -> {
                AttributeMap attributeMap = th$player.getAttributes();

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

                th$player.setHealth(th$player.getMaxHealth());
                setSoulState(SoulState.DOWNED);
            }
            case POSSESSING -> {
                // code to make player GONE
            }
            default -> PlayerUtil.resetAttributes(th$player);
        }
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
            th$player.setSwimming(true);
            ci.cancel();
        }
    }
}