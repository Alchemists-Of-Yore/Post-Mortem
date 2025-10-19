package dev.tazer.post_mortem.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SurgicalAltarBlock extends HorizontalDirectionalBlock {

    public static final EnumProperty<AltarPart> PART = EnumProperty.create("part", AltarPart.class);

    public SurgicalAltarBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, AltarPart.MIDDLE));
    }


    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(GravestoneBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction direction = state.getValue(FACING);
            BlockPos headPos = pos.relative(direction.getCounterClockWise());
            BlockPos footPos = pos.relative(direction.getClockWise());
            level.setBlock(headPos, state.setValue(PART, AltarPart.HEAD), 3);
            level.setBlock(footPos, state.setValue(PART, AltarPart.FOOT), 3);
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
        if (!neighborState.is(this)) {
            Direction facing = state.getValue(FACING);
            switch (state.getValue(PART)) {
                case MIDDLE -> {
                    if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
                        return Blocks.AIR.defaultBlockState();
                    }
                }
                case HEAD -> {
                    if (direction == facing.getClockWise()) {
                        return Blocks.AIR.defaultBlockState();
                    }
                }
                case FOOT -> {
                    if (direction == facing.getCounterClockWise()) {
                        return Blocks.AIR.defaultBlockState();
                    }
                }
            }
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
//        if (!level.isClientSide && player.isCreative()) {
//            AltarPart part = state.getValue(PART);
//            if (part == AltarPart.MIDDLE) {
//                BlockPos blockpos = pos.relative(getNeighbourDirection(part, state.getValue(FACING)));
//                BlockState blockstate = level.getBlockState(blockpos);
//                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
//                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
//                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
//                }
//            }
//        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        BlockPos clockwisePos = pos.relative(direction.getClockWise());
        BlockPos counterClockwisePos = pos.relative(direction.getClockWise());
        Level level = context.getLevel();
        return isValid(level, context, clockwisePos) && isValid(level, context, counterClockwisePos)
                ? defaultBlockState().setValue(FACING, direction)
                : null;
    }

    public static boolean isValid(Level level, BlockPlaceContext context, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(pos);
    }
}
