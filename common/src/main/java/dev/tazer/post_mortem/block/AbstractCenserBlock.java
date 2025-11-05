package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.blockentity.LinkState;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCenserBlock extends BaseEntityBlock {
    public static final EnumProperty<LinkState> LINK_STATE = EnumProperty.create("link", LinkState.class);

    public AbstractCenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public abstract MapCodec<? extends AbstractCenserBlock> codec();

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        SpiritAnchor anchor = player.getAnchor();

        if (level.getBlockEntity(pos) instanceof AbstractCenserBlockEntity censer) {
            if (censer.spirit == null) {
                if (anchor != null) {
                    GlobalPos globalPos = anchor.getPos(level);
                    if (globalPos != null && globalPos.dimension() == level.dimension() && globalPos.pos() == pos) return InteractionResult.FAIL;
                }

                player.setAnchor(new SpiritAnchor(null, GlobalPos.of(level.dimension(), pos), AnchorType.CENSER));
                censer.spirit = player.getUUID();
                level.setBlockAndUpdate(pos, state.setValue(LINK_STATE, LinkState.STRONG));
            } else if (censer.spirit == player.getUUID()) {
                if (player instanceof ServerPlayer serverPlayer) serverPlayer.removeAnchor();
                censer.spirit = null;
                level.setBlockAndUpdate(pos, state.setValue(LINK_STATE, LinkState.ABSENT));
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof AbstractCenserBlockEntity censer && censer.spirit != null) {
            if (level.getPlayerByUUID(censer.spirit) instanceof ServerPlayer player) {
                player.removeAnchor();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public abstract @Nullable AbstractCenserBlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
