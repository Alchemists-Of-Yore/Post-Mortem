package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class HauntedCenserBlock extends LanternBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public HauntedCenserBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public MapCodec<LanternBlock> codec() {
        return simpleCodec(HauntedCenserBlock::new);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.getSoulState() == SoulState.SPIRIT) {
            player.setSoulState(SoulState.MANIFESTATION);
            player.setAnchor(new SpiritAnchor(null, GlobalPos.of(level.dimension(), pos)));
        } else if (player.getAnchor().pos() != null && player.getSoulState() == SoulState.MANIFESTATION && player.getAnchor().pos().pos().equals(pos)) {
            player.setSoulState(SoulState.SPIRIT);
            // TODO: go to last *default* anchor
            player.setAnchor(null);
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
