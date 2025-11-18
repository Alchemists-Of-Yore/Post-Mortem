package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SoulState;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCenserBlock extends BaseEntityBlock {
    public static final EnumProperty<CenserLinkState> LINK_STATE = EnumProperty.create("link", CenserLinkState.class);
    // TODO: take bound player and put them in censer

    public AbstractCenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public abstract MapCodec<? extends AbstractCenserBlock> codec();

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        SpiritAnchor anchor = player.getAnchor();

        if ((player.getSoulState() == SoulState.SPIRIT || player.getSoulState() == SoulState.MANIFESTATION) && level.getBlockEntity(pos) instanceof AbstractCenserBlockEntity censer) {
            if (censer.spirit.isEmpty()) {
                if (anchor != null) {
                    GlobalPos globalPos = anchor.getPos(level);
                    if (globalPos != null && globalPos.dimension() == level.dimension() && globalPos.pos() == pos) return InteractionResult.FAIL;
                }

                player.setAnchor(new SpiritAnchor(null, GlobalPos.of(level.dimension(), pos), AnchorType.CENSER));
            } else if (censer.spirit.get() == player.getUUID()) {
                player.setAnchor(null);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!newState.is(this) && level.getBlockEntity(pos) instanceof AbstractCenserBlockEntity censer && censer.spirit.isPresent()) {
            if (level.getPlayerByUUID(censer.spirit.get()) instanceof ServerPlayer player) {
                player.setAnchor(null);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public abstract @Nullable AbstractCenserBlockEntity newBlockEntity(BlockPos pos, BlockState state);
}
