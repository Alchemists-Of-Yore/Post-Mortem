package dev.tazer.post_mortem.mixin;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulFireBlock.class)
public class SoulFireBlockMixin extends BaseFireBlockMixin {
    @Override
    protected void pm$entityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        super.pm$entityInside(state, level, pos, entity, ci);

        if (entity instanceof Player player && player.getSoulState() == SoulState.MANIFESTATION) {
            // TODO: particle effects?
            if (player.tickCount % 40 == 0) player.heal(1);
            ci.cancel();
        }
    }
}
