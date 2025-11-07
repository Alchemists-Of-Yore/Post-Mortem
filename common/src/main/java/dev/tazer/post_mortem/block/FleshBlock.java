package dev.tazer.post_mortem.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class FleshBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<FleshQuality> QUALITY = EnumProperty.create("quality", FleshQuality.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public FleshBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(QUALITY, FleshQuality.NORMAL).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(QUALITY, WATERLOGGED);
    }
}
