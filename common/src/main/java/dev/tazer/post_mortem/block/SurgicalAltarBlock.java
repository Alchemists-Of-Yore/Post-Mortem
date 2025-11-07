package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.blockentity.SurgicalAltarBlockEntity;
import dev.tazer.post_mortem.registry.PMBlockEntities;
import dev.tazer.post_mortem.registry.PMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SurgicalAltarBlock extends AbstractCenserBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SurgicalAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LINK_STATE, CenserLinkState.ABSENT));
    }

    @Override
    public MapCodec<? extends AbstractCenserBlock> codec() {
        return simpleCodec(SurgicalAltarBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINK_STATE, FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction direction = state.getValue(FACING);
            BlockPos headPos = pos.relative(direction.getCounterClockWise());
            BlockPos footPos = pos.relative(direction.getClockWise());
            level.setBlock(headPos, PMBlocks.SURGICAL_ALTAR_PART.withPropertiesOf(state).setValue(SurgicalAltarPartBlock.PART, BedPart.HEAD), 3);
            level.setBlock(footPos, PMBlocks.SURGICAL_ALTAR_PART.withPropertiesOf(state).setValue(SurgicalAltarPartBlock.PART, BedPart.FOOT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!neighborState.is(PMBlocks.SURGICAL_ALTAR_PART)) {
            Direction facing = state.getValue(FACING);
            if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        BlockPos clockwisePos = pos.relative(direction.getClockWise());
        BlockPos counterClockwisePos = pos.relative(direction.getClockWise());
        Level level = context.getLevel();
        return isValid(level, context, clockwisePos) && isValid(level, context, counterClockwisePos)
                ? defaultBlockState().setValue(FACING, direction) : null;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public static boolean isValid(Level level, BlockPlaceContext context, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(pos);
    }

    @Override
    public @Nullable AbstractCenserBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SurgicalAltarBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, PMBlockEntities.SURGICAL_ALTAR, SurgicalAltarBlockEntity::tick);
    }
}
