package dev.tazer.post_mortem.worldgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.function.Predicate;

// Mostly copied from GeodeFeature
public class FleshGeodeFeature extends Feature<FleshGeodeConfiguration> {
    
    public FleshGeodeFeature(Codec<FleshGeodeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FleshGeodeConfiguration> context) {
        FleshGeodeConfiguration config = context.config();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        int minGenOffset = config.minGenOffset();
        int maxGenOffset = config.maxGenOffset();
        List<Pair<BlockPos, Integer>> list = Lists.newLinkedList();
        int sampledDistributionPoints = config.distributionPoints().sample(random);
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(level.getSeed()));
        NormalNoise noise = NormalNoise.create(worldgenRandom, -4, 1);
        List<BlockPos> list1 = Lists.newLinkedList();
        double d0 = (double) sampledDistributionPoints / config.outerWallDistance().getMaxValue();
        GeodeLayerSettings geodelayersettings = config.geodeLayerSettings();
        GeodeBlockSettings geodeblocksettings = config.geodeBlockSettings();
        GeodeCrackSettings geodecracksettings = config.geodeCrackSettings();
        double d1 = 1 / Math.sqrt(geodelayersettings.filling);
        double d2 = 1 / Math.sqrt(geodelayersettings.innerLayer + d0);
        double d3 = 1 / Math.sqrt(geodelayersettings.middleLayer + d0);
        double d4 = 1 / Math.sqrt(geodelayersettings.outerLayer + d0);
        double d5 = 1 / Math.sqrt(geodecracksettings.baseCrackSize + random.nextDouble() / 2 + (sampledDistributionPoints > 3 ? d0 : 0.0));
        boolean flag = random.nextFloat() < geodecracksettings.generateCrackChance;
        int invalidBlocks = 0;

        for (int i1 = 0; i1 < sampledDistributionPoints; i1++) {
            int j1 = config.outerWallDistance().sample(random);
            int k1 = config.outerWallDistance().sample(random);
            int l1 = config.outerWallDistance().sample(random);
            BlockPos offset = origin.offset(j1, k1, l1);
            BlockState state = level.getBlockState(offset);
            if (state.isAir() || state.is(BlockTags.GEODE_INVALID_BLOCKS)) {
                if (invalidBlocks++ > config.invalidBlocksThreshold()) return false;
            }

            list.add(Pair.of(offset, config.pointOffset().sample(random)));
        }

        if (flag) {
            int i2 = random.nextInt(4);
            int j2 = sampledDistributionPoints * 2 + 1;
            if (i2 == 0) {
                list1.add(origin.offset(j2, 7, 0));
                list1.add(origin.offset(j2, 5, 0));
                list1.add(origin.offset(j2, 1, 0));
            } else if (i2 == 1) {
                list1.add(origin.offset(0, 7, j2));
                list1.add(origin.offset(0, 5, j2));
                list1.add(origin.offset(0, 1, j2));
            } else if (i2 == 2) {
                list1.add(origin.offset(j2, 7, j2));
                list1.add(origin.offset(j2, 5, j2));
                list1.add(origin.offset(j2, 1, j2));
            } else {
                list1.add(origin.offset(0, 7, 0));
                list1.add(origin.offset(0, 5, 0));
                list1.add(origin.offset(0, 1, 0));
            }
        }

        List<BlockPos> list2 = Lists.newArrayList();
        Predicate<BlockState> isReplaceable = isReplaceable(config.geodeBlockSettings().cannotReplace);

        for (BlockPos blockpos3 : BlockPos.betweenClosed(origin.offset(minGenOffset, minGenOffset, minGenOffset), origin.offset(maxGenOffset, maxGenOffset, maxGenOffset))) {
            double d8 = noise.getValue(blockpos3.getX(), blockpos3.getY(), blockpos3.getZ()) * config.noiseMultiplier();
            double d6 = 0;
            double d7 = 0;

            for (Pair<BlockPos, Integer> pair : list) {
                d6 += Mth.invSqrt(blockpos3.distSqr(pair.getFirst()) + pair.getSecond()) + d8;
            }

            for (BlockPos blockpos6 : list1) {
                d7 += Mth.invSqrt(blockpos3.distSqr(blockpos6) + geodecracksettings.crackPointOffset) + d8;
            }

            if (!(d6 < d4)) {
                if (flag && d7 >= d5 && d6 < d1) {
                    this.safeSetBlock(level, blockpos3, Blocks.AIR.defaultBlockState(), isReplaceable);

                    for (Direction direction1 : Direction.values()) {
                        BlockPos blockpos2 = blockpos3.relative(direction1);
                        FluidState fluidstate = level.getFluidState(blockpos2);
                        if (!fluidstate.isEmpty()) level.scheduleTick(blockpos2, fluidstate.getType(), 0);
                    }
                } else if (d6 >= d1) {
                    safeSetBlock(level, blockpos3, geodeblocksettings.fillingProvider.getState(random, blockpos3), isReplaceable);
                } else if (d6 >= d2) {
                    boolean flag1 = (double) random.nextFloat() < 0.08;
                    if (flag1) {
                        safeSetBlock(level, blockpos3, geodeblocksettings.alternateInnerLayerProvider.getState(random, blockpos3), isReplaceable);
                        list2.add(blockpos3.immutable());
                    } else safeSetBlock(level, blockpos3, geodeblocksettings.innerLayerProvider.getState(random, blockpos3), isReplaceable);
                } else if (d6 >= d3) {
                    safeSetBlock(level, blockpos3, geodeblocksettings.middleLayerProvider.getState(random, blockpos3), isReplaceable);
                } else if (d6 >= d4) safeSetBlock(level, blockpos3, geodeblocksettings.outerLayerProvider.getState(random, blockpos3), isReplaceable);
            }
        }

        List<BlockState> list3 = geodeblocksettings.innerPlacements;

        for (BlockPos blockpos4 : list2) {
            BlockState blockstate1 = Util.getRandom(list3, random);

            for (Direction direction : Direction.values()) {
                if (blockstate1.hasProperty(BlockStateProperties.FACING)) blockstate1 = blockstate1.setValue(BlockStateProperties.FACING, direction);

                BlockPos blockpos5 = blockpos4.relative(direction);
                BlockState blockstate2 = level.getBlockState(blockpos5);
                if (blockstate1.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    blockstate1 = blockstate1.setValue(BlockStateProperties.WATERLOGGED, blockstate2.getFluidState().isSource());
                }

                if (BuddingAmethystBlock.canClusterGrowAtState(blockstate2)) {
                    safeSetBlock(level, blockpos5, blockstate1, isReplaceable);
                    break;
                }
            }
        }

        return true;
    }
}
