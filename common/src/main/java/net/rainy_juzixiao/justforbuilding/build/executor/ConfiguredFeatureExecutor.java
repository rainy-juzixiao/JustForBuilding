/*
 * Copyright (c) 2026 rainy-juzixiao
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package net.rainy_juzixiao.justforbuilding.build.executor;

import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildExecutor;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfiguredFeatureExecutor implements BuildExecutor {
    private static final int RANGE_XZ = 16;
    private static final int BELOW = 16;
    private static final int ABOVE = 64;

    private final ConfiguredFeature<?, ?> feature;

    private final long seed;

    public ConfiguredFeatureExecutor(ConfiguredFeature<?, ?> feature) {
        this.feature = feature;
        this.seed = new Random().nextLong();
    }

    @Override
    public List<BuildOperation> execute(ServerLevel level, BlockPos pos, BlockState seed, ServerPlayer player) {
        List<BuildOperation> operations = new ArrayList<>();
        if (!level.isLoaded(pos)) {
            return operations;
        }

        if (!level.getBlockState(pos).isAir()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        }

        int minX = pos.getX() - RANGE_XZ;
        int maxX = pos.getX() + RANGE_XZ;
        int minZ = pos.getZ() - RANGE_XZ;
        int maxZ = pos.getZ() + RANGE_XZ;
        int minY = Math.max(0, pos.getY() - BELOW);
        int maxY = Math.min(level.getHeight() - 1, pos.getY() + ABOVE);

        Map<Long, BlockState> before = snapshot(level, minX, maxX, minY, maxY, minZ, maxZ);

        if (!tryPlace(level, pos)) {
            return operations;
        }

        for (Map.Entry<Long, BlockState> entry : before.entrySet()) {
            long key = entry.getKey();
            BlockState after = level.getBlockState(BlockPos.of(key));
            if (after != entry.getValue()) {
                operations.add(new BuildOperation(
                        OperationType.PLACE_BLOCK,
                        level.dimension().location().toString(),
                        key,
                        BlockOperations.idOf(entry.getValue()),
                        BlockOperations.idOf(after)
                ));
            }
        }
        return operations;
    }

    /**
     * 放置树木；失败时把树根处的方块换成泥土再试一次。
     * TreeFeature 要求树根下方必须是草方块/泥土/耕地，否则直接放弃放置。
     */
    private boolean tryPlace(ServerLevel level, BlockPos pos) {
        Random random = new Random(seed);
        if (feature.place(level, level.getChunkSource().getGenerator(), random, pos)) {
            return true;
        }
        if (level.getBlockState(pos.below()).getBlock() != Blocks.DIRT) {
            level.setBlock(pos.below(), Blocks.DIRT.defaultBlockState(), 2);
        }
        return feature.place(level, level.getChunkSource().getGenerator(), random, pos);
    }

    private Map<Long, BlockState> snapshot(ServerLevel level, int minX, int maxX,
                                           int minY, int maxY, int minZ, int maxZ) {
        Map<Long, BlockState> before = new HashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                ChunkAccess chunk = level.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
                if (chunk == null) {
                    continue;
                }
                for (int y = minY; y <= maxY; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    before.put(p.asLong(), chunk.getBlockState(p));
                }
            }
        }
        return before;
    }
}
