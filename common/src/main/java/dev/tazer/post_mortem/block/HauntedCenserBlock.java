package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.blockentity.HauntedCenserBlockEntity;
import dev.tazer.post_mortem.registry.PMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class HauntedCenserBlock extends AbstractCenserBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public HauntedCenserBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LINK_STATE, CenserLinkState.ABSENT).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINK_STATE, WATERLOGGED);
    }

    @Override
    public MapCodec<? extends AbstractCenserBlock> codec() {
        return simpleCodec(HauntedCenserBlock::new);
    }

    @Override
    public @Nullable AbstractCenserBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HauntedCenserBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, PMBlockEntities.HAUNTED_CENSER, HauntedCenserBlockEntity::tick);
    }
}
